package com.caijiatest.emoticonwidget.music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caijiatest.emoticonwidget.R;
import com.caijiatest.emoticonwidget.music.lyrics.RowLyric;

import java.util.List;

/**
 * Created by cai.jia on 2017/8/18.
 */

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.LyricsVH>{

    private List<RowLyric> lyricList;

    public LyricsAdapter(List<RowLyric> lyricList) {
        this.lyricList = lyricList;
    }

    @Override
    public LyricsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lyrics, parent, false);
        return new LyricsVH(view);
    }

    @Override
    public int getItemCount() {
        return lyricList == null ? 0 : lyricList.size();
    }

    @Override
    public void onBindViewHolder(LyricsVH holder, int position) {
        RowLyric rowLyric = lyricList.get(position);
        holder.tvLyrics.setText(rowLyric.getStrTime()+rowLyric.getLyric());
    }

    static class LyricsVH extends RecyclerView.ViewHolder{
        TextView tvLyrics;
        public LyricsVH(View itemView) {
            super(itemView);
            tvLyrics = (TextView) itemView.findViewById(R.id.tv_lyrics);
        }
    }
}
