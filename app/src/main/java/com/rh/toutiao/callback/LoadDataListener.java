package com.rh.toutiao.callback;

import com.rh.neihan.gson.JokeDataDataEntity;
import com.rh.toutiao.gson.NewsArticleBean;
import com.rh.toutiao.gson.NewsArticleBeanContent;

import java.util.List;

/**
 * @author RH
 * @date 2018/2/6
 */
public interface LoadDataListener {
    void onSuccess( NewsArticleBeanContent newsArticleBeanContent);
    void onError(Exception e);
    void onFail(String string);
}
