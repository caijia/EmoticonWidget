package com.caijiatest.emoticonwidget;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caijia.indicator.TabIndicator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabIndicator.OnTabClickListener {

    private TabIndicator tabIndicator;
    private ViewPager viewPager;
    private TestPagerAdapter pagerAdapter;
    private DefaultTabAdapter defaultTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabIndicator = (TabIndicator) findViewById(R.id.tab_indicator);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new TestPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
//        tabIndicator.setupWithViewPager(viewPager);

        defaultTabAdapter = new DefaultTabAdapter();
        tabIndicator.setupWithTabAdapter(defaultTabAdapter);
        tabIndicator.setOnTabClickListener(this);

        tabIndicator.setup(new String[]{"ITEM1","ITEM2","ITEM3","ITEM4","ITEM5"});
    }

    public void playMusic(View view) {
//        Intent i = new Intent(this, PlayMusicActivity.class);
//        startActivity(i);

        pagerAdapter.notifyDataSetChanged();

        defaultTabAdapter.addTab();
        defaultTabAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTabClick(View view, int tabIndex) {
        Log.d("Tab", "tabIndex=" + tabIndex);
    }

    private class TestPagerAdapter extends PagerAdapter implements TabIndicator.CustomTab {

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

        @Override
        public View createTab(ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_custom_tab, parent,false);
            TextView tabView = (TextView) view.findViewById(R.id.tv_text);
            tabView.setText("item=" + position);
            return view;
        }
    }


    private class DefaultTabAdapter extends TabIndicator.TabAdapter {

        List<String> list;

        public DefaultTabAdapter() {
            list = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return 5 + list.size();
        }

        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup parent, int position) {
            View view = inflater.inflate(R.layout.item_custom_tab, parent,false);
            TextView tabView = (TextView) view.findViewById(R.id.tv_text);
            tabView.setText("item=" + position);
            return view;
        }

        public void addTab(){
            list.add("11");
        }

    }
}
