package com.caijiatest.emoticonwidget.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by cai.jia on 2017/9/14 0014.
 */

public class MyInstrumentation extends Instrumentation {

    private Instrumentation mBase;

    public MyInstrumentation(Instrumentation base) {
        this.mBase = base;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target,
                                            Intent intent, int requestCode, Bundle options){
        Log.d("hook", "hook before");
        try {
            Method execStartActivity = Instrumentation.class.getDeclaredMethod("execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class,
                    int.class, Bundle.class);
            execStartActivity.setAccessible(true);
            return (ActivityResult) execStartActivity.invoke(mBase, who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("hook", "hook error ");
        }
        return null;
    }

}
