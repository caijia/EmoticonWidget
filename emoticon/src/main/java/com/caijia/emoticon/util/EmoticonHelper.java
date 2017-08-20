package com.caijia.emoticon.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by cai.jia on 2015/12/2.
 */
public class EmoticonHelper {

    private static final String TAG = "emoticonHelper";
    /**
     * emoticon的unicode,drawable映射表
     */
    private static final String ASSETS_EMOTION = "emoticon/emoticon.json";
    private static final String DRAWABLE = "drawable";
    private static volatile EmoticonHelper instance = null;

    /**
     * 支持的表情
     */
    private Map<String, Integer> mSupportEmoticonMap;

    /**
     * key的正则匹配pattern
     */
    private String mPattern;

    private EmoticonHelper() {
        mSupportEmoticonMap = new LinkedHashMap<>();
    }

    public static EmoticonHelper getInstance() {
        if (instance == null) {
            synchronized (EmoticonHelper.class) {
                if (instance == null) {
                    instance = new EmoticonHelper();
                }
            }
        }
        return instance;
    }

    private @DrawableRes int getDrawableId(Context context,String resName) {
        return context.getResources().getIdentifier(resName, DRAWABLE, context.getPackageName());
    }

    public void loadEmoticon(Context context) {
        loadEmoticon(context, ASSETS_EMOTION);
    }

    public void loadEmoticon(Context context,String sourceFileName) {
        long start = System.currentTimeMillis();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(sourceFileName);
            String emoticonJson = FileUtil.streamToString(inputStream);
            JSONObject jo = new JSONObject(emoticonJson);
            Iterator<String> keys = jo.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = null;
                try {
                    //key可能是unicode的字符
                    value = jo.getString(key);
                    key = String.valueOf(Character.toChars(Integer.parseInt(key, 16)));
                } catch (Exception e) {
                }

                if (value != null) {
                    mSupportEmoticonMap.put(key, getDrawableId(context,value));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPattern = getPattern();
        Log.d(TAG, "loadEmoticonTime=" + (System.currentTimeMillis() - start));
        Log.d(TAG, "EmoticonPattern=" + mPattern);
    }

    /**
     * @param key
     * @return
     */
    public @DrawableRes int getEmoticon(String key) {
        return mSupportEmoticonMap.get(key);
    }

    /**
     * 得到系统emoticon正则匹配的pattern -> [\uD83D\uDE04]
     *
     * @return
     */
    public String getPattern() {
        if (!TextUtils.isEmpty(mPattern)) {
            return mPattern;
        }
        StringBuilder sb = new StringBuilder();
        for (String key : mSupportEmoticonMap.keySet()) {
            String newKey = filter(key);
            if (sb.length() > 0) {
                sb.append("|");
            }

            if (!TextUtils.isEmpty(newKey)) {
                sb.append(newKey);
            }
        }
        mPattern = sb.toString();
        return mPattern;
    }

    /**
     * 过滤正则表达式中需要转义的符号
     *
     * @param key
     * @return
     */
    private String filter(String key) {
        if (!TextUtils.isEmpty(key)) {
            return key.replace("[", "\\[").replace("]", "\\]")
                    .replace("$", "\\$").replace("(", "\\(").replace(")", "\\)")
                    .replace("*", "\\*").replace("+", "\\+").replace(".", "\\.")
                    .replace("?", "\\?").replace("\\", "\\").replace("^", "\\^")
                    .replace("{", "\\{").replace("}", "\\}").replace("|", "\\|");
        }
        return null;
    }
}
