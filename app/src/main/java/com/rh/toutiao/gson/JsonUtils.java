package com.rh.toutiao.gson;

import android.util.Log;

import com.google.gson.Gson;
import com.rh.neihan.gson.Joke;

/**
 * @author RH
 * @date 2018/2/6
 */
public class JsonUtils {
    public static NewsArticleBean parseToutiaoData(String string){
        Gson gson = new Gson();
        NewsArticleBean newsArticleBean = gson.fromJson(string,NewsArticleBean.class);
        return newsArticleBean;
    }

    public static NewsArticleBeanContent parseToutiaoDataContent(String string){
        Gson gson = new Gson();
        NewsArticleBeanContent newsArticleBeanContent = gson.fromJson(string, NewsArticleBeanContent.class);
        return newsArticleBeanContent;
    }



}
