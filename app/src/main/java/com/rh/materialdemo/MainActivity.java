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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rh.materialdemo.activity.ClientActivity;
import com.rh.materialdemo.activity.ServerActivity;
import com.rh.materialdemo.adapter.PictureAdapter;
import com.rh.materialdemo.bean.Picture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private List<Picture> pictureList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private PictureAdapter pictureAdapter;
    private Picture[] pictures = {
            new Picture("2017-0801", R.mipmap.ic_20170801),
            new Picture("2017-0802",R.mipmap.ic_20170802),
            new Picture("2017-0803",R.mipmap.ic_20170803),
            new Picture("2017-0804",R.mipmap.ic_20170804),
            new Picture("2017-0805",R.mipmap.ic_20170808),
            new Picture("2017-0806",R.mipmap.ic_20170809),
            new Picture("2017-0807",R.mipmap.ic_20170810),
            new Picture("2017-0808",R.mipmap.ic_20170812),
            new Picture("2017-0809",R.mipmap.ic_20170813),
            new Picture("2017-0810",R.mipmap.ic_20170814),
            new Picture("2017-0811",R.mipmap.ic_20170815),
            new Picture("2017-0812",R.mipmap.ic_20170816),
            new Picture("2017-0813",R.mipmap.ic_20170817),
            new Picture("2017-0814",R.mipmap.ic_20170818),
            new Picture("2017-0815",R.mipmap.ic_20170819),
            new Picture("2017-0816",R.mipmap.ic_20170820),
            new Picture("2017-0817",R.mipmap.ic_20170821),
            new Picture("2017-0818",R.mipmap.ic_20170822),
            new Picture("2017-0819",R.mipmap.ic_20170823),
            new Picture("2017-0820",R.mipmap.ic_20170826)


    };
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

        initPicture();//初始化图片
        initRecyclerView();//初始化控件
        initSwipeRefresh();//初始化刷新控件
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
                                initPicture();
                                pictureAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void initPicture(){
        pictureList.clear();
        for (int i = 0; i<pictures.length;i++){
            Random random =new Random();
            int index = random.nextInt(pictures.length);
            pictureList.add(pictures[index]);
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
         pictureAdapter = new PictureAdapter(pictureList);
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
                        Toast.makeText(MainActivity.this,"Data restored",Toast.LENGTH_SHORT).show();
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
                switch (item.getItemId()){
                    case R.id.nav_call:
                        Intent intent_client = new Intent(MainActivity.this, ClientActivity.class);
                        startActivity(intent_client);
                        break;
                    case R.id.nav_folder:
                        Intent intent_server = new Intent(MainActivity.this, ServerActivity.class);
                        startActivity(intent_server);
                        break;
                    case R.id.nav_friend:
                        Toast.makeText(MyApplication.getContext(), "You clicked nav_friend", Toast.LENGTH_SHORT).show();
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
