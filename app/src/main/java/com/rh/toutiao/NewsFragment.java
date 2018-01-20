package com.rh.toutiao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rh.materialdemo.MyApplication;
import com.rh.materialdemo.R;
import com.rh.materialdemo.adapter.BaseFragmentPagerAdapter;
import com.rh.toutiao.fragment.EachNewsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RH
 * @date 2018/1/17
 */
public class NewsFragment extends Fragment {
    private static NewsFragment newsFragment;
    private List<Fragment> list = new ArrayList<>();
    private ViewPager view_pager;

    public static NewsFragment getInstance() {
        if (newsFragment == null) {
            newsFragment = new NewsFragment();
        }
        return newsFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        intView(view);
        intData();
        return view;
    }

    private void intView(View view) {
        TabLayout tabLayout = view.findViewById(R.id.news_fragment_tab_layout);
        view_pager = view.findViewById(R.id.news_fragment_viewpager);
        //tabLayout.setBackgroundColor(-14776091);
        tabLayout.setupWithViewPager(view_pager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        view_pager.setOffscreenPageLimit(10);
    }

    private void intData() {
        String categoryId[] = MyApplication.getContext().getResources().getStringArray(R.array.id);
        String categoryName[] = MyApplication.getContext().getResources().getStringArray(R.array.name);
        for (int i = 0; i < categoryId.length; i++) {
            Fragment fragment = EachNewsFragment.newInstance(categoryId[i]);
            list.add(fragment);
        }
        BaseFragmentPagerAdapter adapter = new BaseFragmentPagerAdapter(getFragmentManager(), list, categoryName);
        view_pager.setAdapter(adapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        newsFragment = null;
    }
}
