package com.rh.neihan.utils;

import android.util.Log;

import com.rh.neihan.callback.JokeDataCallbackListener;
import com.rh.neihan.gson.Joke;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author RH
 * @date 2018/1/18
 */
public class LoadData {
    private static final String SUCCESS = "success";

    public static void loadJokeData(JokeDataCallbackListener jokeDataCallbackListener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://is.snssdk.com/neihan/stream/mix/v1/?mpic=1&webp=1&essence=1&content_type=-102&message_cursor=-1&am_longitude=110&am_latitude=120&am_city=%E5%8C%97%E4%BA%AC%E5%B8%82&am_loc_time=1489226058493&count=30&min_time=1489205901&screen_width=1450&do00le_col_mode=0&iid=3216590132&device_id=32613520945&ac=wifi&channel=360&aid=7&app_name=joke_essay&version_code=612&version_name=6.1.2&device_platform=android&ssmix=a&device_type=sansung&device_brand=xiaomi&os_api=28&os_version=6.10.1&uuid=326135942187625&openudid=3dg6s95rhg2a3dg5&manifest_version_code=612&resolution=1450*2800&dpi=620&update_version_code=6120")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                jokeDataCallbackListener.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Joke joke = JsonUtils.parseJokeData(response.body().string());
                    if (SUCCESS.equals(joke.getMessage())) {
                        if (joke.getData().isHas_more() && joke.getData().getData().size() != 0){
                            jokeDataCallbackListener.onSuccess(joke.getData().getData());
                        }else {
                            jokeDataCallbackListener.onFail("暂无数据更新，请稍后刷新重试！");
                        }
                    } else {
                        Log.e("LoadData", "message为retry: ");
                        jokeDataCallbackListener.onFail("数据获取失败，请刷新重试！");
                    }
                } else {
                    Log.e("LoadData", "请求未完成: ");
                    jokeDataCallbackListener.onFail("服务器数据返回失败，请刷新重试！");
                }
            }
        });
    }
}
