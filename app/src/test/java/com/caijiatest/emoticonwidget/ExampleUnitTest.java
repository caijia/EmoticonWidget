package com.caijiatest.emoticonwidget;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String s = "[ar:草蜢]\n" +
                "[al:]\n" +
                "[00:00.00]草蜢-失恋战线联盟\n" +
                "[00:08.78]编辑:小婧\n" +
                "[01:43.33][00:16.27]她总是只留下电话号码\n" +
                "[01:46.97][00:19.81]从不肯让我送她回家\n" +
                "[01:50.61][00:23.43]听说你也曾经爱上过她\n" +
                "[01:54.15][00:27.07]曾经也同样无法自拔\n" +
                "[01:57.78][00:30.72]你说你学不会假装潇洒\n" +
                "[02:01.41][00:34.36]却叫我别太早放弃她\n" +
                "[02:05.05][00:37.99]把过去传说成一段神话\n" +
                "[02:08.70][00:41.59]然后笑你是一样的傻\n" +
                "[02:12.01][00:45.11]我们那么在乎她\n" +
                "[02:14.15][00:47.01]却被她全部抹杀\n" +
                "[02:15.96][00:48.87]越谈她越相信永远得不到回答\n" +
                "[02:19.57][00:52.49]到底她怎么想\n" +
                "[02:21.35][00:54.28]应该继续在这么\n" +
                "[02:23.37][00:56.36]还是说穿跑了吧\n" +
                "[02:26.89][00:59.80]找一个承认失恋的方法\n" +
                "[02:30.48][01:03.41]让心情好好地放个假\n" +
                "[02:34.14][01:07.00]当你我不小心又想起她\n" +
                "[02:45.69][02:42.20][02:37.69][01:10.60]就在记忆里画一个叉\n" +
                "[02:48.69]\n" +
                "[01:33.58]编辑:小婧";

        Pattern lyricLinePattern = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d\\d\\])+)(.+)");
        Pattern lyricTimePattern = Pattern.compile("\\[\\d\\d:\\d\\d\\.\\d\\d\\]");
        Matcher lineMatcher = lyricLinePattern.matcher(s);
        Matcher timeMatcher = lyricTimePattern.matcher("");
        while (lineMatcher.find()) {
            String lyrics = lineMatcher.group(3);
            String timeStr = lineMatcher.group(1);
            timeMatcher.reset(timeStr);
            while (timeMatcher.find()) {
                System.out.println(timeMatcher.group() + "--" + lyrics);
            }
        }
    }
}