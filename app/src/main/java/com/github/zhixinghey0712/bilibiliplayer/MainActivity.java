package com.github.zhixinghey0712.bilibiliplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import static com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables.TAG;

import com.github.zhixinghey0712.bilibiliplayer.ui.PlayerFragment;
import com.github.zhixinghey0712.bilibiliplayer.ui.UserFragment;
import com.google.android.material.navigation.NavigationView;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private final OkHttpClient client = new OkHttpClient();
    public Handler uiHandler = new Handler();
    private AppBarConfiguration mAppBarConfiguration;

    private static boolean firstFragmentAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        replaceFragment(new PlayerFragment());
        initDrawerMenu();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 用于处理标题栏按钮
        switch (item.getItemId()) {
            case android.R.id.home:
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
                    replaceFragment(new PlayerFragment());
                    mDrawerLayout.close();
                    break;
                case R.id.menu_user:
                    replaceFragment(new UserFragment());
                    mDrawerLayout.close();
                    break;
                case R.id.menu_playlist:

                default:
            }
            return true;
        });
    }

    /**
     * 切换页面并模拟返回栈singleTask
     *
     * @param fragment         fragment实例
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_layout, fragment);
        transaction.commit();
    }
}