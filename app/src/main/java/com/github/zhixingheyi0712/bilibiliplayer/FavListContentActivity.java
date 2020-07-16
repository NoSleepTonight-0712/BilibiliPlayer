package com.github.zhixingheyi0712.bilibiliplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.zhixingheyi0712.bilibiliplayer.ui.FavListContentAdapter;
import com.github.zhixingheyi0712.bilibiliplayer.util.FavListObject;
import com.github.zhixingheyi0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongList;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongObject;
import com.github.zhixingheyi0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.info.LocalInfoManager;
import com.github.zhixingheyi0712.bilibiliplayer.util.json.favlistContent.FavlistContentJsonBean;
import com.github.zhixingheyi0712.bilibiliplayer.util.json.favlistContent.Medias;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayListManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FavListContentActivity extends AppCompatActivity {
    private SongList songObjectList = new SongList();
    private ActionBar actionBar;
    private String fid;
    private int total;
    private Handler uiHandler;
    private FavListContentAdapter adapter;
    private SwipeRefreshLayout swipe;
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 5, 10,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_list_content);

        // 从PlayerAdapter发出的intent
        Intent intent = getIntent();
        fid = intent.getStringExtra("fid");
        total = intent.getIntExtra("total", -1);

        // 用于发Toast
        uiHandler = new Handler();

        // 滚动菜单相关
        RecyclerView recyclerView = findViewById(R.id.playlist_content_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new FavListContentAdapter(songObjectList.getSongObjects(), this);
        recyclerView.setAdapter(adapter);

        // 顶部栏
        Toolbar toolbar = findViewById(R.id.songlist_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(intent.getStringExtra("name"));
        }

        // 下拉刷新
        swipe = findViewById(R.id.playlist_content_refresh);
        swipe.setColorSchemeResources(R.color.TianYiBlue);
        swipe.setOnRefreshListener(this::refreshFavlistContent);
    }

    /**
     * 滚动菜单
     * @param menu 自动填充
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_toolbar, menu);
        return true;
    }

    /**
     * 退出歌单界面
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
        PlayListManager.setSongList(songObjectList.clone());
    }

    /**
     * 全部下载
     * @param item 下载按钮
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.playlist_download_all_toolbar_button:
                Toast.makeText(this, R.string.t_downloading, Toast.LENGTH_SHORT).show();
                for (SongObject s : songObjectList.getSongObjects()) {
                    Runnable runnable = () -> {
                        LocalInfoManager.getMediaFile(s, UpdateMode.LOCAL);
                    };
                    threadPool.execute(runnable);
                }
                Runnable endTask = () -> {
                    while (threadPool.getCompletedTaskCount() == songObjectList.getSize()) {
                        try {
                            this.wait(1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    uiHandler.post(() -> Toast.makeText(this, R.string.t_download_finish, Toast.LENGTH_SHORT).show());
                };
                threadPool.execute(endTask);
        }
        return true;
    }

    /**
     * 在线刷新
     */
    private void refreshFavlistContent() {
        Log.i(GlobalVariables.TAG, "Force Refresh Content UI");
        updateUI(UpdateMode.ONLINE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(GlobalVariables.TAG, "Refresh Content UI");
        updateUI(UpdateMode.LOCAL);
    }

    /**
     * 更新收藏夹UI（滚动列表）
     * @param mode 更新模式，除了下拉刷新都是LOCAL {@link com.github.zhixingheyi0712.bilibiliplayer.util.PlayMode}
     */
    private void updateUI(UpdateMode mode) {
        if (mode == UpdateMode.ONLINE) {
            new updateFavlistContent(swipe, uiHandler, adapter, songObjectList, fid, total, UpdateMode.ONLINE).start();
        } else if (mode == UpdateMode.LOCAL) {
            if (!LocalInfoManager.isFileExists(GlobalVariables.FavListIndexFileName(fid))) return;
            new updateFavlistContent(swipe, uiHandler, adapter, songObjectList, fid, total, UpdateMode.LOCAL).start();
        }
    }

    /**
     * 开新线程更新UI，可能有网络传输所以要多线程不然会ANR
     */
    static class updateFavlistContent extends Thread {
        private SwipeRefreshLayout swipe;
        private Handler ui;
        private UpdateMode mode;
        private FavListContentAdapter adapter;
        private SongList songlist;
        private String fid;
        private int total;

        public updateFavlistContent(SwipeRefreshLayout swipe, Handler ui,
                                    FavListContentAdapter adapter, SongList songlist,
                                    String fid, int total, UpdateMode mode) {
            this.swipe = swipe;
            this.ui = ui;
            this.adapter = adapter;
            this.songlist = songlist;
            this.mode = mode;
            this.fid = fid;
            this.total = total;
        }

        /**
         * 下拉刷新停止并更新数据
         * 不需要自己调用，这个方法会自动被 {@link #run()}调用
         */
        private void updateFavlistContentUI() {
            ui.post(() -> {
                adapter.notifyDataSetChanged();
                swipe.setRefreshing(false);
            });
        }

        /**
         * 获取收藏夹内容并加入songlist, 同时呼起更新UI
         * {@link #updateFavlistContentUI()}
         * {@link LocalInfoManager}
         */
        @Override
        public void run() {
            @Nullable FavlistContentJsonBean json = LocalInfoManager.getFavListContent(fid, total, mode);
            if (json == null) return;
            songlist.setName(json.getData().getInfo().getTitle());
            songlist.setFid(String.valueOf(json.getData().getInfo().getId()));
            songlist.getSongObjects().clear();
            for (Medias m : json.getData().getMedias()) {
                songlist.getSongObjects().add(new SongObject(m));
            }
            updateFavlistContentUI();
        }
    }
}