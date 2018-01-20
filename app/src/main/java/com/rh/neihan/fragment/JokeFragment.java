package com.rh.neihan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.MyToast;
import com.rh.neihan.adapter.JokeAdapter;
import com.rh.neihan.callback.JokeDataCallbackListener;
import com.rh.neihan.gson.Joke;
import com.rh.neihan.gson.JokeDataDataEntity;
import com.rh.neihan.utils.LoadData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RH
 * @date 2018/1/18
 */
public class JokeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener , JokeDataCallbackListener{
    private static final String CATEGORY = "CATEGORY";
    private List<JokeDataDataEntity> jokeDataDataGroupEntityList = new ArrayList<>();
   private JokeAdapter jokeAdapter;
   private  SwipeRefreshLayout swipeRefreshLayout;

    public static JokeFragment newInstance(String categoryId) {
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORY, categoryId);
        JokeFragment jokeFragment = new JokeFragment();
        jokeFragment.setArguments(bundle);
        return jokeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neihan_joke,container,false);
        initView(view);
        intData();
        return view;
    }

    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.joke_fragment_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.joke_fragment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        jokeAdapter = new JokeAdapter(jokeDataDataGroupEntityList);
        recyclerView.setAdapter(jokeAdapter);
    }

    private void intData() {
        LoadData.loadJokeData(this);
    }

    @Override
    public void onRefresh() {
        LoadData.loadJokeData(this);
    }

    @Override
    public void onSuccess(Joke joke) {
        getActivity().runOnUiThread(() -> {
            swipeRefreshLayout.setRefreshing(false);
            jokeDataDataGroupEntityList.clear();
            jokeDataDataGroupEntityList.addAll(joke.getData().getData());
            jokeAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onError(Exception e) {
        swipeRefreshLayout.setRefreshing(false);
        MyToast.show("获取数据失败");
    }
}
