package com.rh.materialdemo;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rh.materialdemo.Util.MyToast;
import com.rh.materialdemo.activity.BaseActivity;
import com.rh.materialdemo.activity.ChatLoginActivity;
import com.rh.materialdemo.activity.ClientActivity;
import com.rh.materialdemo.activity.DownloadActivity;
import com.rh.materialdemo.activity.ServerActivity;
import com.rh.materialdemo.activity.WeatherLocationActivity;


/**
 * @author RH
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //退出Activity时清除fragment的缓存，防止下次再进入时重影
        if (savedInstanceState != null) {
            String tag = "android:support:fragments";
            savedInstanceState.putParcelable(tag, null);
            Log.e(TAG, "清楚fragment的缓存，防止重影");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置toolbar替代原ActionBar
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //显示导航按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            //设置导航按钮图标，默认为一个返回箭头
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
        //初始化滑动菜单布局
        initNavigationView();
    }

    private void initNavigationView() {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        //将Call菜单项设置为默认选中
        // navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(item -> {
            //不显示选中效果
            //item.setCheckable(false);
            switch (item.getItemId()) {
                case R.id.nav_call:
                    MyToast.show("功能开发中，敬请期待！");
                    /*Intent intentClient = new Intent(MainActivity.this, ClientActivity.class);
                    startActivity(intentClient);*/
                    break;
                case R.id.nav_friend:
                    Intent intentWeatherLocation = new Intent(MainActivity.this, WeatherLocationActivity.class);
                    startActivity(intentWeatherLocation);
                    break;
                case R.id.nav_mail:
                    Intent intentDownload = new Intent(MainActivity.this, DownloadActivity.class);
                    startActivity(intentDownload);
                    break;
                case R.id.nav_location:
                    //MyToast.show("功能开发中，敬请期待！");
                    Intent intentServer = new Intent(MainActivity.this, ServerActivity.class);
                    startActivity(intentServer);
                    break;
                case R.id.nav_folder:
                    Intent intentChat = new Intent(MainActivity.this, ChatLoginActivity.class);
                    startActivity(intentChat);
                    break;
                default:
                    break;
            }
            //隐藏左侧菜单栏,NavigationView
            mDrawerLayout.closeDrawers();
            return true;
        });
    }

    /**
     * 加载Toolbar菜单文件
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * Toolbar点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                MyToast.show("You clicked camera");
                break;
            case R.id.backup:
                MyToast.show("You clicked backup");
                break;
            case R.id.sunny:
                MyToast.show("You clicked sunny");
                break;
            //HomeAsUp按钮的id永远是android.R.id.home
            case android.R.id.home:
                //打开隐藏的菜单栏，NavigationView
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

}
