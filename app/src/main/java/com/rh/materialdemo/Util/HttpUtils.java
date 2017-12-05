package com.rh.materialdemo.Util;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by RH on 2017/11/9.
 */

public class HttpUtils {


    //发起一条简单的GET请求，并使用com.squareup.okhttp3:okhttp:3.9.0开源jar包自带的okhttp3.Callback回调接口。
    public static void sendOkHttpRequestWithGET(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
        //OkHttp在enqueue()方法的内部已经帮我们开好了子线程，会在子线程中去执行HTTP请求。
        // 而execute（）方法没有开启线程，因此使用时一般需要开启一个线程来执行sendOkHttpRequestWithGET()中的HTTP请求
    }


    //发起一条POST请求比GET复杂点，需要先构建一个RequestBody对象来存放待提交的数据
    public static void sendOkHttpRequestWithPOST(String address, String name, String passwd, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("username", name)
                .add("password", passwd)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)//调用post方法
                .build();
        client.newCall(request).enqueue(callback);
    }

}
