package com.github.zhixingheyi0712.bilibiliplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.github.zhixingheyi0712.bilibiliplayer.util.GlobalVariables.TAG;

import com.github.zhixingheyi0712.bilibiliplayer.ui.ExoPlayerFragment;
import com.github.zhixingheyi0712.bilibiliplayer.ui.PlaylistFragment;
import com.github.zhixingheyi0712.bilibiliplayer.ui.SettingsFragment;
import com.github.zhixingheyi0712.bilibiliplayer.ui.UserFragment;
import com.github.zhixingheyi0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixingheyi0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixingheyi0712.bilibiliplayer.util.UserSettings;
import com.github.zhixingheyi0712.bilibiliplayer.util.info.LocalInfoManager;
import com.github.zhixingheyi0712.bilibiliplayer.util.json.user.UserInfoJsonBean;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayerEvents;
import com.github.zhixingheyi0712.bilibiliplayer.util.player.PlayerService;
import com.google.android.material.navigation.NavigationView;
import com.huawei.hms.audiokit.player.manager.HwAudioConfigManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBar actionBar;

    @Override
    @Subscribe
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.player);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        replaceFragment(new ExoPlayerFragment());
        initDrawerMenu();
        initMediaPlayer();
    }

    /**
     * 当菜单被按下时触发
     * 作用是 {@link #initDrawerMenu()}
     * @param item 点击项
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 用于处理标题栏按钮
        if (item.getItemId() == android.R.id.home) {
            updateUI();
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * 初始化抽屉目录
     * 仅运行一次
     */
    private void initDrawerMenu() {
        Log.i(TAG, "Start init Drawer Menu");
        NavigationView navView = findViewById(R.id.navigation_view);
        navView.setCheckedItem(R.id.menu_exo_player);
        navView.setNavigationItemSelectedListener((MenuItem item) -> {
            switch (item.getItemId()) {
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
                case R.id.menu_exo_player:
                    actionBar.setTitle(R.string.exoplayer);
                    replaceFragment(new ExoPlayerFragment());
                default:
            }
            mDrawerLayout.close();
            return true;
        });
    }

    /**
     * 用于更新侧滑栏的用户界面
     * 首先检测user.json和face.jpg, 如果有就继续，不然停止
     */
    private void updateUI() {
        TextView menuUidTextBox = findViewById(R.id.menu_uid_textbox);
        TextView menuNameBox = findViewById(R.id.menu_name_box);
        LinearLayout menuUidLayout = findViewById(R.id.menu_uid_layout);
        ImageView menuFace = findViewById(R.id.menu_face_image);

        // 本地更新
        // 如果本地文件有缺失就停止
        if (!LocalInfoManager.isFileExists(GlobalVariables.USER_FACE_FILE_NAME)) return;
        if (!LocalInfoManager.isFileExists(GlobalVariables.USER_INFO_FILE_NAME)) return;

        try {
            String uid = UserSettings.getUid();
            String name = UserSettings.getName();

            // 再在Usersettings中查找
            if (uid.equals("-1") || name.equals("")) {
                // usersettings中没有
                UserInfoJsonBean info = LocalInfoManager.getUserInfo(UpdateMode.FORCE_LOCAL);
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
     * 切换页面
     * 并不能模拟返回栈
     * @param fragment fragment实例
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_layout, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 启动播放服务
     * {@link PlayerService}
     */
    private void initMediaPlayer() {
        Intent startPlayer = new Intent(this, PlayerService.class);
        startForegroundService(startPlayer);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void sendToastHint(@NotNull PlayerEvents.HintToast event) {
        Toast.makeText(this, event.getId(), Toast.LENGTH_SHORT).show();
    }

    private void init(Context context) {
    }
}