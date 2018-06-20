package com.caijiatest.emoticonwidget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.caijia.indicator.TabIndicator;
import com.caijia.widget.ScrollingViewHeaderLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cai.jia on 2017/9/10.
 */

public class ViewPagerHeaderAct extends AppCompatActivity {

    private ScrollingViewHeaderLayout scrollingViewHeaderLayout;
    private ViewPager viewPager;
    private TabIndicator tabIndicator;
    private List<Fragment> fragments;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_view_pager_header);
        viewPager = findViewById(R.id.view_pager);
        tabIndicator = findViewById(R.id.tab_indicator);
        imageView = findViewById(R.id.image_view);
        scrollingViewHeaderLayout = findViewById(R.id.view_pager_header_layout);
        scrollingViewHeaderLayout.setDebug(true);

        tabIndicator.post(new Runnable() {
            @Override
            public void run() {
                int height = tabIndicator.getHeight();
                scrollingViewHeaderLayout.setHeadVisibleMinHeight(height);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "click image", Toast.LENGTH_SHORT).show();
            }
        });

        fragments = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (i == 1) {
                fragments.add(new TestRecyclerFragment());
            } else if (i == 2) {
                fragments.add(new TestFragment1());
            } else {
                fragments.add(new TestFragment());
            }
        }

        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabIndicator.setupWithViewPager(viewPager);
        scrollingViewHeaderLayout.setOnHeaderViewScrollListener(new ScrollingViewHeaderLayout.OffsetChangeListener() {
            @Override
            public void onOffsetChanged(int scrollDis, int maxScrollDis) {
                Log.d("view_pager", "scrollDis = " + scrollDis + "---maxScrollDis = " + maxScrollDis);
            }
        });
    }

    private class MyPagerAdapter extends CacheFagmentAdapter implements ScrollingViewHeaderLayout.CurrentViewProvider {

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
