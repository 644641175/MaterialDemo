package com.rh.neihan.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.Util.MyToast;
import com.rh.materialdemo.Util.ParseJsonUtils;
import com.rh.materialdemo.adapter.PictureAdapter;
import com.rh.materialdemo.gson.BingDaily;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author RH
 * @date 2018/1/23
 */
public class BingPictureFragment extends Fragment{
    private List<BingDaily> bingDailyList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private PictureAdapter pictureAdapter;
    private static BingPictureFragment bingPictureFragment;

    public static BingPictureFragment getInstance(){
        if (bingPictureFragment == null){
            bingPictureFragment = new BingPictureFragment();
        }
        return bingPictureFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bingpicture , container , false);
        //初始化控件
        initRecyclerView(view);
        //初始化图片
        initPicture(8);
        //初始化刷新控件
        initSwipeRefresh(view);
        return view;
    }

    private void initSwipeRefresh(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(() -> {
                initPicture(8);
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start());
    }

    private void initPicture(int number) {
        String bingPictureUrl = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=" + number;
        HttpUtils.sendOkHttpRequestWithGET(bingPictureUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> MyToast.systemshow("获取图片失败，请检查网络状态"));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final List<BingDaily> bingList = ParseJsonUtils.handleBingPicResponseWithGson(response.body().string());
                getActivity().runOnUiThread(() -> {
                    //刷新
                    freshPicture(bingList);
                });
            }
        });
    }

    /**
     * 刷新RecyclerView
     *
     * @param nowList 更新后的集合
     */
    private void freshPicture(List<BingDaily> nowList) {
        bingDailyList.clear();
        //list集合不能变，变了之后就用下面注释代码，重新绑定Adapter
        bingDailyList.addAll(nowList);
        pictureAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        pictureAdapter = new PictureAdapter(bingDailyList);
        recyclerView.setAdapter(pictureAdapter);
    }

}
