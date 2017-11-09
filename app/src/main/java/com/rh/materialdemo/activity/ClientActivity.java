package com.rh.materialdemo.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.Util.ParseJsonUtils;
import com.rh.materialdemo.bean.Diary;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";
    private Button upload_json;
    private Button upload_xml;
    private TextView tv_data;
    private TextView tv_weather;
    private TextView tv_location;
    private TextView tv_mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//显示导航按钮
        }
        initView();
    }

    private void initView() {
        upload_json = (Button) findViewById(R.id.upload_json);
        upload_xml = (Button) findViewById(R.id.upload_xml);
        tv_data = (TextView) findViewById(R.id.date);
        tv_weather = (TextView) findViewById(R.id.weather);
        tv_location = (TextView) findViewById(R.id.location);
        tv_mood = (TextView) findViewById(R.id.mood);

        upload_xml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Diary diary = new Diary();
                diary.setDate(tv_data.getText().toString());
                diary.setWeather(tv_weather.getText().toString());
                diary.setLocation(tv_location.getText().toString());
                diary.setMood(tv_mood.getText().toString());
            }
        });

        upload_json.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //使用OkHttp
                HttpUtils.sendOkHttpRequestWithGET("http://10.0.2.2:8080/TomcatTest/get_data.json", new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //在这里对异常情况进行处理
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ParseJsonUtils.parseJSONWithGSON(response.body().string());//解析接收到的json数据
                    }
                });

            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
