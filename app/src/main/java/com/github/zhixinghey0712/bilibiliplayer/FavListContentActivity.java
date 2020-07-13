package com.github.zhixinghey0712.bilibiliplayer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.github.zhixinghey0712.bilibiliplayer.ui.FavListContentAdapter;
import com.github.zhixinghey0712.bilibiliplayer.ui.PlaylistAdapter;
import com.github.zhixinghey0712.bilibiliplayer.ui.PlaylistFragment;
import com.github.zhixinghey0712.bilibiliplayer.util.FavListObject;
import com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixinghey0712.bilibiliplayer.util.SongObject;
import com.github.zhixinghey0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixinghey0712.bilibiliplayer.util.info.LocalInfoManager;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlistContent.FavlistContentJsonBean;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlistContent.Medias;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavListContentActivity extends AppCompatActivity {
    private List<SongObject> songObjectList = new ArrayList<>();
    private ActionBar actionBar;
    private String fid;
    private int total;
    private Handler uiHandler;
    private List<FavListObject> favListObjects = new ArrayList<>();
    private FavListContentAdapter adapter;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_list_content);

        Intent intent = getIntent();
        fid = intent.getStringExtra("fid");
        total = intent.getIntExtra("total", -1);
        uiHandler = new Handler();

        RecyclerView recyclerView = findViewById(R.id.playlist_content_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new FavListContentAdapter(songObjectList);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.songlist_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(intent.getStringExtra("name"));
        }

        swipe = findViewById(R.id.playlist_content_refresh);
        swipe.setColorSchemeResources(R.color.TianYiBlue);
        swipe.setOnRefreshListener(this::refreshFavlistContent);
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

    private void updateUI(UpdateMode mode) {
        if (mode == UpdateMode.ONLINE) {
            new updateFavlistContent(swipe, uiHandler, adapter, songObjectList, fid, total, UpdateMode.ONLINE).start();
        } else if (mode == UpdateMode.LOCAL) {
            if (!LocalInfoManager.isFileExists(GlobalVariables.FavListIndexFileName(fid))) return;
            new updateFavlistContent(swipe, uiHandler, adapter, songObjectList, fid, total, UpdateMode.LOCAL).start();
        }
    }

    static class updateFavlistContent extends Thread {
        private SwipeRefreshLayout swipe;
        private Handler ui;
        private UpdateMode mode;
        private FavListContentAdapter adapter;
        private List<SongObject> songObjects;
        private String fid;
        private int total;

        public updateFavlistContent(SwipeRefreshLayout swipe, Handler ui,
                                    FavListContentAdapter adapter, List<SongObject> songObjects,
                                    String fid, int total, UpdateMode mode) {
            this.swipe = swipe;
            this.ui = ui;
            this.adapter = adapter;
            this.songObjects = songObjects;
            this.mode = mode;
            this.fid = fid;
            this.total = total;
        }

        private void updateFavlistContentUI() {
            ui.post(() -> {
                adapter.notifyDataSetChanged();
                swipe.setRefreshing(false);
            });
        }

        @Override
        public void run() {
            FavlistContentJsonBean json = LocalInfoManager.getFavListContent(fid, total, mode);
            if (json == null) return;
            songObjects.clear();
            for (Medias m : json.getData().getMedias()) {
                songObjects.add(new SongObject(m));
            }
            updateFavlistContentUI();
        }
    }
}