package com.rh.neihan.fragment;

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
import com.rh.neihan.callback.JokeDataCallbackListener;
import com.rh.neihan.gson.JokeDataDataEntity;
import com.rh.neihan.utils.LoadData;

import java.util.ArrayList;
import java.util.List;


/**
 * @author RH
 * @date 2018/1/18
 */
public class JokeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, JokeDataCallbackListener {
    private static final String CATEGORY = "CATEGORY";
    private List<JokeDataDataEntity> jokeDataDataGroupEntityList = new ArrayList<>();
    private JokeAdapter jokeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView loadFail;
    private static JokeFragment jokeFragment;

    public static JokeFragment newInstance(String categoryId) {
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORY, categoryId);
        JokeFragment jokeFragment = new JokeFragment();
        jokeFragment.setArguments(bundle);
        return jokeFragment;
    }
    public static JokeFragment getInstance() {
        if (jokeFragment == null) {
            jokeFragment = new JokeFragment();
        }
        return jokeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neihan_joke, container, false);
        initView(view);
        intData();
        return view;
    }

    private void initView(View view) {
        Log.e("JokeFragment", "initView: ");
        progressBar = view.findViewById(R.id.joke_fragment_progressbar);
        loadFail = view.findViewById(R.id.joke_fragment_load_fail);
        swipeRefreshLayout = view.findViewById(R.id.joke_fragment_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.joke_fragment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        jokeAdapter = new JokeAdapter(jokeDataDataGroupEntityList);
        recyclerView.setAdapter(jokeAdapter);
    }

    private void intData() {
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            LoadData.loadJokeData(this);
        } else {
            loadFail.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        LoadData.loadJokeData(this);
    }

    @Override
    public void onSuccess(List<JokeDataDataEntity> jokeDataDataEntityList) {
        getActivity().runOnUiThread(() -> {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            loadFail.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            jokeDataDataGroupEntityList.clear();
            jokeDataDataGroupEntityList.addAll(jokeDataDataEntityList);
            jokeAdapter.notifyDataSetChanged();

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
    public void onFail(String s) {
        getActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            MyToast.show(s);
            swipeRefreshLayout.setRefreshing(false);
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
