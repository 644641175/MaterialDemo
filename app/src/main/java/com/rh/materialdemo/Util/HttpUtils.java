package com.rh.materialdemo.Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 *
 * @author RH
 * @date 2017/11/9
 */

public class HttpUtils {


/**
    发起一条简单的GET请求，并使用com.squareup.okhttp3:okhttp:3.9.0开源jar包自带的okhttp3.Callback回调接口。
*/
    public static void sendOkHttpRequestWithGET(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
        //OkHttp在enqueue()方法的内部已经帮我们开好了子线程，会在子线程中去执行HTTP请求。
        // 而execute（）方法没有开启线程，因此使用时一般需要开启一个线程来执行sendOkHttpRequestWithGET()中的HTTP请求
    }


/**
    发起一条POST请求比GET复杂点，需要先构建一个RequestBody对象来存放待提交的数据
*/
    public static void sendOkHttpRequestWithPOST(String address, String name, String passwd, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("username", name)
                .add("password", passwd)
                .build();
        Request request = new Request.Builder()
                .url(address)
                //调用post方法
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setDoInput(true);
                //connection.setDoOutput(true);//get请求并不需要设置，设置将导致请求以post方式提交,部分接口可能无法使用，即使设置了connection.setRequestMethod("GET")也无用（如每日一文接口）;
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null){
                    response.append(line);
                }

                if (listener != null){
                    //回调onFinish()方法
                    listener.onFinish(response.toString());
                }

            } catch (Exception e) {
                //回调onError()fangfa
                if (listener != null){
                    listener.onError(e);
                }
            }finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
        }).start();

    }


}
