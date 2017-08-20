package com.caijiatest.emoticonwidget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.caijiatest.emoticonwidget.music.adapter.LyricsAdapter;
import com.caijiatest.emoticonwidget.music.lyrics.LyricsParser;
import com.caijiatest.emoticonwidget.music.lyrics.RowLyric;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by cai.jia on 2017/8/18.
 */

public class PlayMusicActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LyricsParser lyricsParser = new LyricsParser();
        try {
            InputStream lyricStream = getAssets().open("test.lrc");
            List<RowLyric> lyricList = lyricsParser.parser(lyricStream);
            LyricsAdapter adapter = new LyricsAdapter(lyricList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
