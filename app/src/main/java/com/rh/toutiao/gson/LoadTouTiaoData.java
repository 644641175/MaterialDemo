package com.rh.toutiao.gson;

import android.text.TextUtils;
import android.util.Log;

import com.rh.toutiao.callback.LoadDataListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author RH
 * @date 2018/2/6
 */
public class LoadTouTiaoData {
    private static final String TAG = "LoadTouTiaoData";
    private static final String SUCCESS = "success";
    private static List<NewsArticleBeanContent> dataList = new ArrayList<>();

    public static void loadNewsArticleData(LoadDataListener loadDataListener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://is.snssdk.com/api/news/feed/v62/?iid=5034850950&device_id=6096495334&refer=1&count=20&aid=13&category=")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadDataListener.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    NewsArticleBean newsArticleBean = JsonUtils.parseToutiaoData(response.body().string());
                    if (SUCCESS.equals(newsArticleBean.getMessage())) {
                        if (newsArticleBean.getTotal_number()!= 0){

                            for (NewsArticleBean.DataBean dataBean : newsArticleBean.getData())
                            {
                              NewsArticleBeanContent content =  JsonUtils.parseToutiaoDataContent(dataBean.getContent());
                              if (!TextUtils.isEmpty(content.getTitle()))
                              {
                                  loadDataListener.onSuccess(JsonUtils.parseToutiaoDataContent(dataBean.getContent()));
                                  Log.e(TAG, "onResponse: 返回的标题为"+JsonUtils.parseToutiaoDataContent(dataBean.getContent()).getTitle());
                              }
                            }
                            //loadDataListener.onSuccess(dataList);

                        }else {
                            loadDataListener.onFail("暂无数据更新，请稍后刷新重试！");
                        }
                    } else {
                        Log.e("LoadTouTiaoData", "message为retry: ");
                        loadDataListener.onFail("数据获取失败，请刷新重试！");
                    }
                } else {
                    Log.e("LoadTouTiaoData", "请求未完成: ");
                    loadDataListener.onFail("服务器数据返回失败，请刷新重试！");
                }
            }
        });
    }

}
