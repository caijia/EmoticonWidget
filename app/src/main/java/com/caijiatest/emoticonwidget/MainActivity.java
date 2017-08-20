package com.caijiatest.emoticonwidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caijia.indicator.TabIndicator;

public class MainActivity extends AppCompatActivity {

    private TabIndicator tabIndicator;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabIndicator = (TabIndicator) findViewById(R.id.tab_indicator);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new TestPagerAdapter());
        tabIndicator.setupWithViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);
    }

    public void playMusic(View view) {
        Intent i = new Intent(this, PlayMusicActivity.class);
        startActivity(i);
    }

    private class TestPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView textView = new TextView(container.getContext());
            textView.setText("item=" + position);
            container.addView(textView);
            return textView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "item=" + position;
        }
    }
}
