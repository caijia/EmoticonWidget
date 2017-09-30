package com.caijiatest.emoticonwidget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by cai.jia on 2017/9/14 0014.
 */

public class TestRecyclerFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_recyclerview, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MyAdapter());
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyVH>{

        @Override
        public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_lyrics, parent, false);
            return new MyVH(view);
        }

        @Override
        public void onBindViewHolder(MyVH holder, int position) {
            holder.tvTest.setText("item = " + position);
        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }

    public static class MyVH extends RecyclerView.ViewHolder{
        TextView tvTest;
        public MyVH(View itemView) {
            super(itemView);
            tvTest = (TextView) itemView.findViewById(R.id.tv_lyrics);
        }
    }
}
