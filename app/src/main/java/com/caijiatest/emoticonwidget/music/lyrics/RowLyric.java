package com.caijiatest.emoticonwidget.music.lyrics;

import android.support.annotation.NonNull;

/**
 * 一行的歌词
 * Created by cai.jia on 2017/8/18.
 */

public class RowLyric implements Comparable<RowLyric> {

    /**
     * 歌词字符串时间
     */
    private String strTime;

    /**
     * 歌词时间(ms)
     */
    private long time;

    /**
     * 歌词
     */
    private String lyric;

    public RowLyric(String strTime, long time, String lyric) {
        this.strTime = strTime;
        this.time = time;
        this.lyric = lyric;
    }

    public RowLyric() {
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    @Override
    public int compareTo(@NonNull RowLyric o) {
        return Float.compare(this.getTime(), o.getTime());
    }
}
