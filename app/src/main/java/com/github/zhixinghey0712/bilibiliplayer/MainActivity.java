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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import static com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables.TAG;
import static com.github.zhixinghey0712.bilibiliplayer.util.FragmentUniqueTag.*;

import com.github.zhixinghey0712.bilibiliplayer.ui.PlayerFragment;
import com.github.zhixinghey0712.bilibiliplayer.ui.UserFragment;
import com.github.zhixinghey0712.bilibiliplayer.util.FragmentUniqueTag;
import com.google.android.material.navigation.NavigationView;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private final OkHttpClient client = new OkHttpClient();
    public Handler uiHandler = new Handler();
    private AppBarConfiguration mAppBarConfiguration;

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

        replaceFragment(new PlayerFragment(), PLAYER, false);
        initDrawerMenu();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
                    replaceFragment(new PlayerFragment(), PLAYER);
                    break;
                case R.id.menu_user:
                    replaceFragment(new UserFragment(), USER);
                    break;
                default:
            }
            return true;
        });
    }

    /**
     * 切换页面并模拟返回栈singleTask
     * @param fragment fragment实例
     * @param fragmentTagEnum fragment对应的tag, 在{@link FragmentUniqueTag}中
     * @param isAddToBackStack 是否添加进返回栈
     */
    private void replaceFragment(Fragment fragment, FragmentUniqueTag fragmentTagEnum,
                                 boolean isAddToBackStack) {
        FragmentManager manager = getSupportFragmentManager();
        String fragmentTag = fragmentTagEnum.name();

        if (manager.findFragmentByTag(fragmentTag) == null) {
            // 返回栈中没有这个Fragment
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content_layout, fragment, fragmentTag);
            if (isAddToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        } else {
            // 有，弹栈
            manager.popBackStack(fragmentTag, 0);
        }
    }

    /**
     * 切换页面并模拟返回栈singleTask
     * 一定添加进返回栈
     * @param fragment fragment实例
     * @param fragmentTagEnum fragment对应的tag, 在{@link FragmentUniqueTag}
     */
    private void replaceFragment(Fragment fragment, FragmentUniqueTag fragmentTagEnum) {
        replaceFragment(fragment, fragmentTagEnum, true);
    }
}