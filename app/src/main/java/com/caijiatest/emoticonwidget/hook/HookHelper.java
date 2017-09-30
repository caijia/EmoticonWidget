package com.caijiatest.emoticonwidget.hook;

import android.app.Instrumentation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by cai.jia on 2017/9/14 0014.
 */

public class HookHelper {

    /**
     * mMainThread.getInstrumentation().execStartActivity(
     * getOuterContext(), mMainThread.getApplicationThread(), null,
     * (Activity) null, intent, -1, options);
     */
    public static void contextStartAct() {

        try {
            //获取ActivityThread实例
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);

            Field mInstrumentationField = clazz.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) mInstrumentationField.get(activityThread);

            MyInstrumentation myInstrumentation = new MyInstrumentation(instrumentation);
            mInstrumentationField.set(activityThread,myInstrumentation);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
