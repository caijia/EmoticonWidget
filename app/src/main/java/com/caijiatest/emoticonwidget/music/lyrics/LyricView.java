package com.caijiatest.emoticonwidget.music.lyrics;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌词View
 * Created by cai.jia on 2017/9/27 0027.
 */

public class LyricView extends View {

    private static final String TAG = "lyricView";

    public LyricView(Context context) {
        this(context,null);
    }

    public LyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private TextPaint paint;
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private FlingRunnable flingRunnable;

    private void init(Context context, @Nullable AttributeSet attrs) {
        scroller = new Scroller(context);
        flingRunnable = new FlingRunnable();
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //scroll up distanceY > 0
                scrollOffsetY += (-distanceY);
                invalidate();
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                Log.d(TAG, "velocityY =" + velocityY);
                scroller.fling(0, 0, 0, Math.abs((int) velocityY), 0, 0, 0, Integer.MAX_VALUE);
                if (scroller.computeScrollOffset()) {
                    Log.d(TAG, "onFling");
                    flingRunnable.setDirection(velocityY > 0 ? FlingRunnable.DOWN : FlingRunnable.UP);
                    ViewCompat.postOnAnimation(LyricView.this, flingRunnable);
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        paint = new TextPaint (Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(spToPx(14));

        lyricList = new ArrayList<>();
    }

    private float spToPx(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    private class FlingRunnable implements Runnable{
        public static final int UP = 1;
        public static final int DOWN = 2;
        private int oldCurrY;
        private int direction;

        @Override
        public void run() {
            //If it returns true,the animation is not yet finished.
            Log.d(TAG, "computeScrollOffset = " + scroller.computeScrollOffset());
            if (scroller != null && scroller.computeScrollOffset()) {
                int currY = scroller.getCurrY();
                int dy = oldCurrY - currY;
                Log.d(TAG, "dy = " + dy);
                scrollOffsetY += (direction == DOWN ? -dy : dy);
                postInvalidate();

                if (Math.abs(scrollOffsetY) > 1000) {
                    Log.d(TAG, "abortAnimation");
                    scroller.abortAnimation();
                }else{
                    ViewCompat.postOnAnimation(LyricView.this, this);
                }

            }else{
                oldCurrY = 0;
            }
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private int scrollOffsetY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画出歌词
        if (lyricList == null || lyricList.isEmpty()) {
            return;
        }
        Log.d(TAG, "onDraw");

        int width = getWidth();
        int height = getHeight();
        int currentHeight = scrollOffsetY; //滑动时改变
        for (RowLyricWrapper wrapper : lyricList) {
            RowLyric rowLyric = wrapper.lyric;
            StaticLayout staticLayout = wrapper.staticLayout;
            String lyric = rowLyric.getLyric();
            if (staticLayout == null) {
                staticLayout = new StaticLayout(lyric, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
                wrapper.staticLayout = staticLayout;
            }
            canvas.save();
            canvas.translate(0,currentHeight);
            staticLayout.draw(canvas);
            currentHeight += staticLayout.getHeight();
            canvas.restore();
        }
    }

    private class RowLyricWrapper{

        RowLyric lyric;
        StaticLayout staticLayout;

        public RowLyricWrapper(RowLyric lyric, StaticLayout staticLayout) {
            this.lyric = lyric;
            this.staticLayout = staticLayout;
        }
    }

    /**
     * 歌词数据
     */
    private List<RowLyricWrapper> lyricList;

    /**
     * 设置歌词数据
     * @param lyricList
     */
    public void setLyric(List<RowLyric> lyricList) {
        if (lyricList == null || lyricList.isEmpty()) {
            return;
        }

        for (RowLyric rowLyric : lyricList) {
            RowLyricWrapper wrapper = new RowLyricWrapper(rowLyric, null);
            this.lyricList.add(wrapper);
        }
    }
}
