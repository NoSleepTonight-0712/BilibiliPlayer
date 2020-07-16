package com.github.zhixingheyi0712.bilibiliplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.github.zhixingheyi0712.bilibiliplayer.util.GlobalVariables.TAG;

import com.github.zhixingheyi0712.bilibiliplayer.ui.PlayerFragment;
import com.github.zhixingheyi0712.bilibiliplayer.ui.PlaylistFragment;
import com.github.zhixingheyi0712.bilibiliplayer.ui.SettingsFragment;
import com.github.zhixingheyi0712.bilibiliplayer.ui.UserFragment;
import com.github.zhixingheyi0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixingheyi0712.bilibiliplayer.util.SongList;
import com.github.zhixingheyi0712.bilibiliplayer.util.UserSettings;
import com.github.zhixingheyi0712.bilibiliplayer.util.info.LocalInfoManager;
import com.github.zhixingheyi0712.bilibiliplayer.util.json.user.UserInfoJsonBean;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayerBinder;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayerService;
import com.google.android.material.navigation.NavigationView;

import java.io.FileNotFoundException;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private final OkHttpClient client = new OkHttpClient();
    public Handler uiHandler = new Handler();
    private ActionBar actionBar;

    @Nullable
    public SongList getSongList() {
        return songList;
    }

    @Nullable private SongList songList;

    public PlayerBinder.PlayBinderPointer getpBinder() {
        return pBinder;
    }

    private PlayerBinder.PlayBinderPointer pBinder = new PlayerBinder.PlayBinderPointer();
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pBinder.setBinder((PlayerBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.player);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        replaceFragment(new PlayerFragment());
        initDrawerMenu();
        initMediaPlayer();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 用于处理标题栏按钮
        switch (item.getItemId()) {
            case android.R.id.home:
                updateUI();
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    /**
     * 初始化抽屉目录
     */
    private void initDrawerMenu() {
        Log.i(TAG, "Start init Drawer Menu");
        NavigationView navView = findViewById(R.id.navigation_view);
        navView.setCheckedItem(R.id.menu_player);
        navView.setNavigationItemSelectedListener((MenuItem item) -> {
            switch (item.getItemId()) {
                case R.id.menu_player:
                    actionBar.setTitle(R.string.player);
                    replaceFragment(new PlayerFragment());
                    break;
                case R.id.menu_user:
                    actionBar.setTitle(R.string.user);
                    replaceFragment(new UserFragment());
                    break;
                case R.id.menu_playlist:
                    actionBar.setTitle(R.string.playlist);
                    replaceFragment(new PlaylistFragment());
                    break;
                case R.id.menu_settings:
                    actionBar.setTitle(R.string.settings);
                    replaceFragment(new SettingsFragment());
                    break;
                default:
            }
            mDrawerLayout.close();
            return true;
        });
    }

    private void updateUI() {
        // 本地更新
        // 如果本地文件有缺失就停止
        TextView menuUidTextBox = findViewById(R.id.menu_uid_textbox);
        TextView menuNameBox = findViewById(R.id.menu_name_box);
        LinearLayout menuUidLayout = findViewById(R.id.menu_uid_layout);
        ImageView menuFace = findViewById(R.id.menu_face_image);

        if (!LocalInfoManager.isFileExists(GlobalVariables.USER_FACE_FILE_NAME)) return;
        if (!LocalInfoManager.isFileExists(GlobalVariables.USER_INFO_FILE_NAME)) return;

        try {
            String uid = UserSettings.getUid();
            String name = UserSettings.getName();

            if (uid.equals("-1") || name.equals("")) {
                // usersettings中没有
                UserInfoJsonBean info = LocalInfoManager.getUserInfo();
                uid = String.valueOf(info.getData().getMid());
                name = info.getData().getName();
            }

            Bitmap faceImage = BitmapFactory.decodeStream
                    (ApplicationMain.getContext().openFileInput(GlobalVariables.USER_FACE_FILE_NAME));

            menuUidTextBox.setText(uid);
            menuFace.setImageBitmap(faceImage);
            menuUidLayout.setVisibility(View.VISIBLE);
            menuNameBox.setText(name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换页面并模拟返回栈singleTask
     *
     * @param fragment fragment实例
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_layout, fragment);
        transaction.commit();
    }

    public void setToolbarTitle(int stringId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(stringId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void initMediaPlayer() {
        Intent bindStartPlayIntent = new Intent(this, PlayerService.class);
        bindService(bindStartPlayIntent, connection, Context.BIND_AUTO_CREATE);
    }
}