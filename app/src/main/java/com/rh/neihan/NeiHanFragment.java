package com.rh.neihan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rh.materialdemo.MyApplication;
import com.rh.materialdemo.R;
import com.rh.materialdemo.adapter.BaseFragmentPagerAdapter;
import com.rh.neihan.fragment.JokeFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RH
 * @date 2018/1/18
 */
public class NeiHanFragment extends Fragment {
    private static NeiHanFragment jokeFragment;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>();

    public static NeiHanFragment getInstance() {
        if (jokeFragment == null) {
            jokeFragment = new NeiHanFragment();
        }
        return jokeFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neihan, container, false);
        initView(view);
        initFragment();
        initData();
        return view;
    }

    private void initView(View view) {
        TabLayout tabLayout = view.findViewById(R.id.neihan_fragment_tab_layout);
        viewPager = view.findViewById(R.id.neihan_fragment_viewpager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.setOffscreenPageLimit(6);
    }

    private void initFragment() {
        String[] titleArray = MyApplication.getContext().getResources().getStringArray(R.array.jokeName);
        String[] idArray = MyApplication.getContext().getResources().getStringArray(R.array.jokeId);
        Log.e("NeiHanFragment", "titleArray: "+titleArray.length );
        for (int i = 0; i < titleArray.length; i++) {
            Fragment jokeFragment = JokeFragment.newInstance(idArray[i]);
            fragmentList.add(jokeFragment);
        }

         // 在Fragment中的ViewPager中添加Fragment时，获取FragmentManager不能使用getFragmentManager，
         //否则会报如下错误：
         // java.lang.IllegalStateException: FragmentManager is already executing transactions
         // 解决方法：http://blog.csdn.net/a1274624994/article/details/53575976
         //使用getChildFragmentManager()
        BaseFragmentPagerAdapter baseFragmentPagerAdapter = new BaseFragmentPagerAdapter(getChildFragmentManager(), fragmentList, titleArray);
        viewPager.setAdapter(baseFragmentPagerAdapter);
    }

    private void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        jokeFragment = null;
    }
}
