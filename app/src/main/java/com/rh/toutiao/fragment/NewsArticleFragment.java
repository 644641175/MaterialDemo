package com.rh.toutiao.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.MyToast;
import com.rh.materialdemo.Util.NetworkUtils;
import com.rh.neihan.adapter.JokeAdapter;
import com.rh.neihan.gson.JokeDataDataEntity;
import com.rh.toutiao.adapter.NewsArticleAdapter;
import com.rh.toutiao.callback.LoadDataListener;
import com.rh.toutiao.gson.LoadTouTiaoData;
import com.rh.toutiao.gson.NewsArticleBeanContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author RH
 * @date 2018/2/6
 */
public class NewsArticleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoadDataListener {
    private static final String TAG = "NewsArticleFragment";
    private static final String CATEGORY = "CATEGORY";
    private List<NewsArticleBeanContent> contentList = new ArrayList<>();
    private NewsArticleAdapter newsArticleAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView loadFail;
    private static NewsArticleFragment newsArticleFragment;

    public static NewsArticleFragment getInstance() {
        if (newsArticleFragment == null) {
            newsArticleFragment = new NewsArticleFragment();
        }
        return newsArticleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_article, container, false);
        initView(view);
        intData();
        return view;
    }

    private void initView(View view) {
        progressBar = view.findViewById(R.id.news_article_fragment_progressbar);
        loadFail = view.findViewById(R.id.news_article_fragment_load_fail);
        swipeRefreshLayout = view.findViewById(R.id.news_article_fragment_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.news_article_fragment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsArticleAdapter = new NewsArticleAdapter(contentList);
        recyclerView.setAdapter(newsArticleAdapter);
    }

    private void intData() {
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            LoadTouTiaoData.loadNewsArticleData(this);
        } else {
            loadFail.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        LoadTouTiaoData.loadNewsArticleData(this);
    }

    private static List<NewsArticleBeanContent> dataList = new ArrayList<>();

    @Override
    public void onSuccess(NewsArticleBeanContent newsArticleBeanContent) {
        getActivity().runOnUiThread(() -> {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            loadFail.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            if (dataList.size() > 50) {
                dataList.clear();
            } else if (dataList.size() == 0) {
                dataList.add(newsArticleBeanContent);
            } else {
                for (int i = 0; i < contentList.size(); i++) {
                    if (dataList.get(i).getTitle().equals(newsArticleBeanContent.getTitle())) {
                        Log.e(TAG, "有重复 ");
                        break;
                    } else {
                        if (i == dataList.size() - 1) {
                            dataList.add(newsArticleBeanContent);
                            Log.e(TAG, "加一 ");
                        }
                    }
                }
            }

            Collections.reverse(dataList);
            Log.e(TAG, "onSuccess: " + contentList.size());
            contentList.clear();
            contentList.addAll(dataList);
            newsArticleAdapter.notifyDataSetChanged();
            Collections.reverse(dataList);

            swipeRefreshLayout.setRefreshing(false);

        });
    }

    @Override
    public void onError(Exception e) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                MyToast.show("网络连接异常，请检查当前网络状态！");
                progressBar.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            });
        } else {
            MyToast.show("Activity 为空");
        }
    }

    @Override
    public void onFail(String string) {
        getActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            MyToast.show(string);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
