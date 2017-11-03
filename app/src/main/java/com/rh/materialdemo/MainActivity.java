package com.rh.materialdemo;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import com.rh.materialdemo.adapter.PictureAdapter;
import com.rh.materialdemo.bean.Picture;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private List<Picture> pictureList = new ArrayList<>();
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

        initPicture();
        initRecyclerView();
    }

    private void initPicture() {
        pictureList.clear();
        pictureList.add(new Picture("2017-0801",R.mipmap.ic_20170801));
        pictureList.add(new Picture("2017-0802",R.mipmap.ic_20170802));
        pictureList.add(new Picture("2017-0803",R.mipmap.ic_20170803));
        pictureList.add(new Picture("2017-0804",R.mipmap.ic_20170804));
        pictureList.add(new Picture("2017-0805",R.mipmap.ic_20170808));
        pictureList.add(new Picture("2017-0806",R.mipmap.ic_20170809));
        pictureList.add(new Picture("2017-0807",R.mipmap.ic_20170810));
        pictureList.add(new Picture("2017-0808",R.mipmap.ic_20170812));
        pictureList.add(new Picture("2017-0809",R.mipmap.ic_20170813));
        pictureList.add(new Picture("2017-0810",R.mipmap.ic_20170814));
        pictureList.add(new Picture("2017-0811",R.mipmap.ic_20170815));
        pictureList.add(new Picture("2017-0812",R.mipmap.ic_20170816));
        pictureList.add(new Picture("2017-0813",R.mipmap.ic_20170817));
        pictureList.add(new Picture("2017-0814",R.mipmap.ic_20170818));
        pictureList.add(new Picture("2017-0815",R.mipmap.ic_20170819));
        pictureList.add(new Picture("2017-0816",R.mipmap.ic_20170820));
        pictureList.add(new Picture("2017-0817",R.mipmap.ic_20170821));
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        PictureAdapter pictureAdapter = new PictureAdapter(pictureList);
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
        navView.setCheckedItem(R.id.nav_call);//将Call菜单项设置为默认选中
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

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
