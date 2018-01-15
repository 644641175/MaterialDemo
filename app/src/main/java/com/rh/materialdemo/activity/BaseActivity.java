package com.rh.materialdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.rh.materialdemo.MyApplication;
import com.rh.materialdemo.Util.ActivityCollector;
import com.rh.materialdemo.Util.MyToast;

/**
 * @author RH
 * @date 2018/1/6
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this, getClass());
        MyToast.initToast(MyApplication.getContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
