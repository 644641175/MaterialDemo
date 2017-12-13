package com.rh.materialdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import android.view.View;
import android.widget.Toast;

import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.Util.ParseJsonUtils;
import com.rh.materialdemo.activity.ClientActivity;
import com.rh.materialdemo.activity.ServerActivity;
import com.rh.materialdemo.activity.WeatherActivity;
import com.rh.materialdemo.activity.WeatherLocationActivity;
import com.rh.materialdemo.adapter.PictureAdapter;
import com.rh.materialdemo.bean.Diary;
import com.rh.materialdemo.bean.Picture;
import com.rh.materialdemo.gson.BingDaily;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private List<BingDaily> bingDailyList = new ArrayList<>();
    private List<Diary> pictureList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private PictureAdapter pictureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//设置toolbar替代原ActionBar
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//显示导航按钮
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);//设置导航按钮图标，默认为一个返回箭头
        }
        initNavigationView();//初始化滑动菜单布局
        initFloatingActionButton();//初始化悬浮按钮
        initRecyclerView();//初始化控件
        initPicture(8);//初始化图片
        initSwipeRefresh();//初始化刷新控件

        Log.e(TAG, "onCreate: ");
    }

    private void initSwipeRefresh() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initPicture(8);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void initPicture(int number) {
        pictureList.clear();
        String bingPictureUrl = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=" + number;
        HttpUtils.sendOkHttpRequestWithGET(bingPictureUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "获取图片失败，请检查网络状态", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final List<BingDaily> bingList = ParseJsonUtils.handleBingPicResponseWithGson(response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FreshPicture(bingList);//刷新
                    }
                });
            }
        });
    }

    /**
     * 刷新RecyclerView
     *
     * @param nowList
     */
    private void FreshPicture(List<BingDaily> nowList) {
        bingDailyList.clear();
        bingDailyList.addAll(nowList);//list集合不能变，变了之后就用下面注释代码，重新绑定Adapter
        /*bingDailyList = nowList;
        pictureAdapter = new PictureAdapter(bingDailyList);
        recyclerView.setAdapter(pictureAdapter);*/
        pictureAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
       /* bingDailyList.add(new BingDaily("111","https://www.bing.com/az/hprichbg/rb/Snow_ZH-CN11178898651_1920x1080.jpg"));
        bingDailyList.add(new BingDaily("222","https://www.bing.com/az/hprichbg/rb/Snow_ZH-CN11178898651_1920x1080.jpg"));
        bingDailyList.add(new BingDaily("333","https://www.bing.com/az/hprichbg/rb/Snow_ZH-CN11178898651_1920x1080.jpg"));*/
        pictureAdapter = new PictureAdapter(bingDailyList);
        recyclerView.setAdapter(pictureAdapter);
    }

    private void initFloatingActionButton() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar,可添加功能的Toast
                Snackbar.make(v, "Data deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Data restored", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
    }

    private void initNavigationView() {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        //navView.setCheckedItem(R.id.nav_call);//将Call菜单项设置为默认选中
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_call:
                        Intent intent_client = new Intent(MainActivity.this, ClientActivity.class);
                        startActivity(intent_client);
                        break;
                    case R.id.nav_folder:
                        Intent intent_server = new Intent(MainActivity.this, ServerActivity.class);
                        startActivity(intent_server);
                        break;
                    case R.id.nav_friend:
                        Intent intent_weather_location = new Intent(MainActivity.this, WeatherLocationActivity.class);
                        startActivity(intent_weather_location);
                        break;
                    case R.id.nav_location:
                        Toast.makeText(MyApplication.getContext(), "You clicked nav_location", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_mail:
                        Toast.makeText(MyApplication.getContext(), "You clicked nav_mail", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    /*加载Toolbar菜单文件*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /*Toolbar点击事件*/
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
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

}
