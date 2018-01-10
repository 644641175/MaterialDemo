package com.rh.materialdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.Util.ParseJsonUtils;
import com.rh.materialdemo.activity.ChatLoginActivity;
import com.rh.materialdemo.activity.CheckApkVersionActivity;
import com.rh.materialdemo.activity.ClientActivity;
import com.rh.materialdemo.activity.DownloadActivity;
import com.rh.materialdemo.activity.WeatherLocationActivity;
import com.rh.materialdemo.adapter.PictureAdapter;
import com.rh.materialdemo.gson.BingDaily;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author RH
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private List<BingDaily> bingDailyList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private PictureAdapter pictureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        //初始化悬浮按钮
        initFloatingActionButton();
        //初始化控件
        initRecyclerView();
        //初始化图片
        initPicture(8);
        //初始化刷新控件
        initSwipeRefresh();

        Log.e(TAG, "onCreate: ");
    }

    private void initSwipeRefresh() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                initPicture(8);
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start());
    }

    private void initPicture(int number) {
        String bingPictureUrl = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=" + number;
        HttpUtils.sendOkHttpRequestWithGET(bingPictureUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "获取图片失败，请检查网络状态", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final List<BingDaily> bingList = ParseJsonUtils.handleBingPicResponseWithGson(response.body().string());
                runOnUiThread(() -> {
                    //刷新
                    freshPicture(bingList);
                });
            }
        });
    }

    /**
     * 刷新RecyclerView
     *
     * @param nowList 更新后的集合
     */
    private void freshPicture(List<BingDaily> nowList) {
        bingDailyList.clear();
        //list集合不能变，变了之后就用下面注释代码，重新绑定Adapter
        bingDailyList.addAll(nowList);
        pictureAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        pictureAdapter = new PictureAdapter(bingDailyList);
        recyclerView.setAdapter(pictureAdapter);
    }

    private void initFloatingActionButton() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatButton);
        floatingActionButton.setOnClickListener(v -> {
            //Snackbar,可添加功能的Toast
            Snackbar.make(v, "Data deleted", Snackbar.LENGTH_SHORT)
                    .setAction("Undo", v1 -> Toast.makeText(MainActivity.this, "Data restored", Toast.LENGTH_SHORT).show()).show();
        });
    }

    private void initNavigationView() {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        //将Call菜单项设置为默认选中
        //navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_call:
                    Intent intentClient = new Intent(MainActivity.this, ClientActivity.class);
                    startActivity(intentClient);
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
                    //Toast.makeText(MyApplication.getContext(), "You clicked nav_location", Toast.LENGTH_SHORT).show();
                    Intent intentUpdateApk = new Intent(MainActivity.this, CheckApkVersionActivity.class);
                    startActivity(intentUpdateApk);
                    break;
                case R.id.nav_folder:
                    Intent intentChat = new Intent(MainActivity.this, ChatLoginActivity.class);
                    startActivity(intentChat);
                    break;
                default:
                    break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        });
    }

    /**加载Toolbar菜单文件*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**Toolbar点击事件*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                Toast.makeText(this, "You clicked camera", Toast.LENGTH_SHORT).show();
                break;
            case R.id.backup:
                Toast.makeText(this, "You clicked backup", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sunny:
                Toast.makeText(this, "You clicked sunny", Toast.LENGTH_SHORT).show();
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
