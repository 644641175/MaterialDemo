package com.rh.materialdemo.activity;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.HttpCallbackListener;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.Util.ParseJsonUtils;
import com.rh.materialdemo.bean.Picture;
import com.rh.materialdemo.gson.BingDaily;
import com.rh.materialdemo.gson.DailyArticle;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by RH on 2017/11/3.
 */

public class PictureActivity extends AppCompatActivity {
    private static final String TAG = "PictureActivity";
    public static final String NAME = "picture_name";
    public static final String IMAGE_ID = "picture_image_id";
    private TextView pictureContentText,author,title;
    private String words;
    private DailyArticle dailyArticle = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        BingDaily bingDaily = (BingDaily) getIntent().getSerializableExtra("BingDaily_data");
        String pictureName = bingDaily.getDate();
        String pictureImageId = bingDaily.getUrl();
        words = bingDaily.getCopyright();

        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_tool);
        ImageView pictureImageView = (ImageView) findViewById(R.id.picture_image_view);
        author = (TextView) findViewById(R.id.picture_author);
        title = (TextView) findViewById(R.id.picture_title);
        pictureContentText = (TextView) findViewById(R.id.picture_content_text);
        setSupportActionBar(toolbar1);//设置toolbar替代原ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(pictureName);
        Glide.with(this).load(pictureImageId).into(pictureImageView);
        title.setText(words);//首次，无网，默认加载必应Copyright
        intContent(pictureName);//加载每日一文

    }

    private void intContent(String date) {
        String url = "https://interface.meiriyiwen.com/article/day?dev=1&date=" + date;//此接口用okhttp3请求会出错,可能将 //connection.setDoOutput(true);
        HttpUtils.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String string) {
                try {
                    dailyArticle = ParseJsonUtils.handleDailyArticle(string);
                    words = dailyArticle.getContent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(dailyArticle.getTitle());
                        author.setText("—"+dailyArticle.getAuthor());
                        pictureContentText.setText(Html.fromHtml(words));//也可以使用Android富文本来加载Html格式的文本
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PictureActivity.this, "加载每日一文失败", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e(TAG, "onError: " + e);
            }
        });

    }

   /* private String generayePictureContent(String pictureName) {
        StringBuilder pictureContent = new StringBuilder();
        for (int i = 0;i<500;i++){
            pictureContent.append(pictureName);
        }
        return pictureContent.toString();
    }*/

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
