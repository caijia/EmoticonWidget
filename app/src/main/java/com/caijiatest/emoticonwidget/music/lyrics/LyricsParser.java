package com.caijiatest.emoticonwidget.music.lyrics;

import com.caijia.emoticon.util.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 歌词解析器
 * 歌词文件
 * [ar:草蜢]
 * [al:]
 * [00:00.00]草蜢-失恋战线联盟
 * [00:08.78]编辑:小婧
 * [01:43.33][00:16.27]她总是只留下电话号码
 * [01:46.97][00:19.81]从不肯让我送她回家
 * [01:50.61][00:23.43]听说你也曾经爱上过她
 * [01:54.15][00:27.07]曾经也同样无法自拔
 * [01:57.78][00:30.72]你说你学不会假装潇洒
 * [02:01.41][00:34.36]却叫我别太早放弃她
 * [02:05.05][00:37.99]把过去传说成一段神话
 * [02:08.70][00:41.59]然后笑你是一样的傻
 * [02:12.01][00:45.11]我们那么在乎她
 * [02:14.15][00:47.01]却被她全部抹杀
 * [02:15.96][00:48.87]越谈她越相信永远得不到回答
 * [02:19.57][00:52.49]到底她怎么想
 * [02:21.35][00:54.28]应该继续在这么
 * [02:23.37][00:56.36]还是说穿跑了吧
 * [02:26.89][00:59.80]找一个承认失恋的方法
 * [02:30.48][01:03.41]让心情好好地放个假
 * [02:34.14][01:07.00]当你我不小心又想起她
 * [02:45.69][02:42.20][02:37.69][01:10.60]就在记忆里画一个叉
 * [02:48.69]
 * [01:33.58]编辑:小婧
 * Created by cai.jia on 2017/8/18.
 */

public class LyricsParser {

    /**
     * 解析歌词
     *
     * @param lyrics 完整的歌词文件字符串
     * @return 歌词文件集合
     */
    public List<RowLyric> parser(String lyrics) {
        List<RowLyric> lyricList = new ArrayList<>();
        Pattern lyricRowPattern = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d\\d\\])+)(.+)");
        Pattern lyricTimePattern = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d\\d)\\]");
        Matcher lineMatcher = lyricRowPattern.matcher(lyrics);
        Matcher timeMatcher = lyricTimePattern.matcher("");
        while (lineMatcher.find()) {
            String strRowLyrics = lineMatcher.group(3); //一行里的歌词
            String strTimeArray = lineMatcher.group(1); //[02:45.69][02:42.20] 可能有多个时间
            timeMatcher.reset(strTimeArray);
            while (timeMatcher.find()) {
                String strTime = timeMatcher.group();
                int minute = Integer.parseInt(timeMatcher.group(1));
                int second = Integer.parseInt(timeMatcher.group(2));
                int milliSecond = Integer.parseInt(timeMatcher.group(3));
                long time = minute * 60 * 1000 + second * 1000 + milliSecond;
                RowLyric rowLyric = new RowLyric(strTime, time, strRowLyrics);
                lyricList.add(rowLyric);
            }
        }
        Collections.sort(lyricList);
        return lyricList;
    }

    /**
     * 解析歌词
     *
     * @param lyricFile 歌词文件
     * @return 歌词文件集合
     */
    public List<RowLyric> parser(File lyricFile) {
        String lyric = FileUtil.fileToString(lyricFile);
        return parser(lyric);
    }

    /**
     * 解析歌词
     *
     * @param lyricInStream 歌词文件Stream
     * @return 歌词文件集合
     */
    public List<RowLyric> parser(InputStream lyricInStream) {
        String lyric = FileUtil.streamToString(lyricInStream);
        return parser(lyric);
    }
}
