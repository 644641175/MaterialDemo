package com.rh.materialdemo.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.Util.ParseJsonUtils;
import com.rh.materialdemo.bean.Diary;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author RH
 */
public class ClientActivity extends BaseActivity {
    private static final String TAG = "ClientActivity";
    private TextView mData;
    private TextView mWeather;
    private TextView mLocation;
    private TextView mMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //显示导航按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
    }

    private void initView() {
        Button uploadJson = (Button) findViewById(R.id.upload_json);
        Button uploadXml = (Button) findViewById(R.id.upload_xml);
        mData = (TextView) findViewById(R.id.date);
        mWeather = (TextView) findViewById(R.id.weather);
        mLocation = (TextView) findViewById(R.id.location);
        mMood = (TextView) findViewById(R.id.mood);

        uploadXml.setOnClickListener(v -> {
            Diary diary = new Diary();
            diary.setDate(mData.getText().toString());
            diary.setWeather(mWeather.getText().toString());
            diary.setLocation(mLocation.getText().toString());
            diary.setMood(mMood.getText().toString());
        });

        uploadJson.setOnClickListener(v -> {

            //使用OkHttp
            HttpUtils.sendOkHttpRequestWithGET("http://10.203.147.113:8080/MyServlet/json/get_data.json", new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //在这里对异常情况进行处理
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //解析接收到的json数据
                    ParseJsonUtils.parseJSONWithGSON(response.body().string());
                }
            });

        });

        /*
        * Android7.0以上API默认支持分屏android:resizeableActivity="true",不想某应用支持分屏，在Application和Activity中设置false即可。
        * */
        findViewById(R.id.splitscreen).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (ClientActivity.this.isInMultiWindowMode()) {
                    //开启本应用Activity
                    Intent intentClient = new Intent(ClientActivity.this, ServerActivity.class);
                    //分屏模式下在另一窗口启动
                    intentClient.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentClient);
                    //开启其它应用程序
                    /*Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName("com.example.cardview", "com.example.cardview.MainActivity");
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);//分屏模式下在另一窗口启动应用
                    startActivity(intent);*/

                } else {
                    Toast.makeText(ClientActivity.this, "当前未处于分屏模式", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ClientActivity.this, "当前Android版本不支持分屏", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}
