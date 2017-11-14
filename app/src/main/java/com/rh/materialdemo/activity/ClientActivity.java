package com.rh.materialdemo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
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
import android.widget.Toast;

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

        findViewById(R.id.splitscreen).setOnClickListener(new View.OnClickListener() {
            /*
            * Android7.0以上API默认支持分屏android:resizeableActivity="true",不想某应用支持分屏，在Application和Activity中设置false即可。
            * */
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (ClientActivity.this.isInMultiWindowMode()){
                        //开启本应用Activity
                        Intent intent_client = new Intent(ClientActivity.this, ServerActivity.class);
                        intent_client.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);//分屏模式下在另一窗口启动
                        startActivity(intent_client);
                        //开启其它应用程序
                        /*Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn = new ComponentName("com.example.cardview", "com.example.cardview.MainActivity");
                        intent.setComponent(cn);
                        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);//分屏模式下在另一窗口启动应用
                        startActivity(intent);*/

                    }else{
                        Toast.makeText(ClientActivity.this,"当前未处于分屏模式",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(ClientActivity.this,"当前Android版本不支持分屏",Toast.LENGTH_LONG).show();
                }
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
