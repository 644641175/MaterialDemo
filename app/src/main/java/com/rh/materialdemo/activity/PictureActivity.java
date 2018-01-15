package com.rh.materialdemo.activity;

import android.annotation.SuppressLint;
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
import com.bumptech.glide.Glide;
import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.HttpCallbackListener;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.Util.MyToast;
import com.rh.materialdemo.Util.ParseJsonUtils;
import com.rh.materialdemo.gson.BingDaily;
import com.rh.materialdemo.gson.DailyArticle;
import org.json.JSONException;

/**
 * @author RH
 * @date 2017/11/3
 */

public class PictureActivity extends BaseActivity {
    private static final String TAG = "PictureActivity";
    private TextView pictureContentText, author, title;
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
        //设置toolbar替代原ActionBar
        setSupportActionBar(toolbar1);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(pictureName);
        Glide.with(this).load(pictureImageId).into(pictureImageView);
        //首次，无网，默认加载必应Copyright
        title.setText(words);
        //加载每日一文
        intContent(pictureName);

    }

    private void intContent(String date) {
        //此接口用okhttp3请求会出错,可能将 //connection.setDoOutput(true)
        String url = "https://interface.meiriyiwen.com/article/day?dev=1&date=" + date;
        HttpUtils.sendHttpRequest(url, new HttpCallbackListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish(String string) {
                try {
                    dailyArticle = ParseJsonUtils.handleDailyArticle(string);
                    words = dailyArticle.getContent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    title.setText(dailyArticle.getTitle());
                    author.setText("—" + dailyArticle.getAuthor());
                    //也可以使用Android富文本来加载Html格式的文本
                    pictureContentText.setText(Html.fromHtml(words));
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> MyToast.show("加载每日一文失败"));
                Log.e(TAG, "onError: " + e);
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
