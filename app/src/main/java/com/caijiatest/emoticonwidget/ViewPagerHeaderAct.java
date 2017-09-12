package com.caijiatest.emoticonwidget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.caijia.indicator.TabIndicator;
import com.caijia.viewpagerheaderlayout.ViewPagerHeaderLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cai.jia on 2017/9/10.
 */

public class ViewPagerHeaderAct extends AppCompatActivity {

    private ViewPagerHeaderLayout viewPagerHeaderLayout;
    private ViewPager viewPager;
    private TabIndicator tabIndicator;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_view_pager_header);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabIndicator = (TabIndicator) findViewById(R.id.tab_indicator);
        viewPagerHeaderLayout = (ViewPagerHeaderLayout) findViewById(R.id.view_pager_header_layout);

        fragments = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            fragments.add(new TestFragment());
        }

        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabIndicator.setupWithViewPager(viewPager);
        viewPagerHeaderLayout.setOnHeaderViewScrollListener(new ViewPagerHeaderLayout.OffsetChangeListener() {
            @Override
            public void onOffsetChanged(int scrollDis, int maxScrollDis) {
                Log.d("view_pager", "scrollDis = " + scrollDis + "---maxScrollDis = " + maxScrollDis);
            }
        });
    }

    private class MyPagerAdapter extends CacheFagmentAdapter implements ViewPagerHeaderLayout.CurrentViewProvider{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "item = " + position;
        }

        @Override
        public View provideCurrentView(int position) {
            return fragments.get(position).getView();
        }
    }
}
