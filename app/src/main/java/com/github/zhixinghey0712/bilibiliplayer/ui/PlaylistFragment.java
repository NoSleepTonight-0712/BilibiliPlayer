package com.github.zhixinghey0712.bilibiliplayer.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.zhixinghey0712.bilibiliplayer.ApplicationMain;
import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.FavListObject;
import com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixinghey0712.bilibiliplayer.util.SongList;
import com.github.zhixinghey0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixinghey0712.bilibiliplayer.util.UserSettings;
import com.github.zhixinghey0712.bilibiliplayer.util.info.LocalInfoManager;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlist.FavList;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlist.FavListJsonBean;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables.TAG;

public class PlaylistFragment extends Fragment {
    private View view;
    private Handler uiHandler;
    private List<FavListObject> favListObjects = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private PlaylistAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_playlist, container, false);
        uiHandler = new Handler();

        RecyclerView recyclerView = view.findViewById(R.id.playlist_view);
        LinearLayoutManager manager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(manager);

        if (!UserSettings.isLogin()) {
            Toast.makeText(ApplicationMain.getContext(), R.string.t_please_login, Toast.LENGTH_SHORT).show();
            return view;
        }

        // 已登录
        swipeRefresh = view.findViewById(R.id.playlist_refresh);
        swipeRefresh.setColorSchemeResources(R.color.TianYiBlue);
        swipeRefresh.setOnRefreshListener(this::refreshFavlists);

        adapter = new PlaylistAdapter(favListObjects);
        adapter.setPlayListActivity(requireActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    /**
     * 在线刷新
     */
    private void refreshFavlists() {
        updateUI(UpdateMode.ONLINE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(GlobalVariables.TAG, "Refresh UI");
        updateUI(UpdateMode.LOCAL);
    }

    private void updateUI(UpdateMode mode) {
        if (mode == UpdateMode.ONLINE) {
            // 在线更新
            new PlaylistFragment.updateFavList(view, uiHandler, adapter, favListObjects, UpdateMode.ONLINE).start();
        } else if (mode == UpdateMode.LOCAL) {
            // 本地更新
            // 如果本地文件有缺失就停止
            if (!LocalInfoManager.isFileExists(GlobalVariables.USER_FACE_FILE_NAME)) return;
            if (!LocalInfoManager.isFileExists(GlobalVariables.USER_INFO_FILE_NAME)) return;
            new PlaylistFragment.updateFavList(view, uiHandler, adapter, favListObjects, UpdateMode.LOCAL).start();
        }
    }

    static class updateFavList extends Thread {
        private View view;
        private Handler ui;
        private UpdateMode mode;
        private PlaylistAdapter adapter;
        List<FavListObject> favListObjects;

        /**
         * 在线更新
         *
         * @param view view
         * @param ui   Handler
         * @param mode {@link UpdateMode}
         */
        public updateFavList(View view, Handler ui, PlaylistAdapter adapter,
                             List<FavListObject> favListObjects, UpdateMode mode) {
            this.view = view;
            this.ui = ui;
            this.mode = mode;
            this.adapter = adapter;
            this.favListObjects = favListObjects;
        }

        private void updatePlayListUI() {
            ui.post(() -> {
                adapter.notifyDataSetChanged();
                SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.playlist_refresh);
                swipeRefresh.setRefreshing(false);
            });
        }

        @Override
        public void run() {
            FavListJsonBean json = LocalInfoManager.getFavList(UserSettings.getUid(), mode);
            if (json == null) return;
            favListObjects.clear();
            for (FavList favList : json.getData().getList()) {
                favListObjects.add(new FavListObject(favList));
            }
            updatePlayListUI();
        }
    }
}