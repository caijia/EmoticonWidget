package com.caijiatest.emoticonwidget.music.lyrics;

import android.animation.ValueAnimator;
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
    private TextPaint paint;
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private FlingRunnable flingRunnable;
    private int scrollOffsetY;
    /**
     * 歌词数据
     */
    private List<RowLyricWrapper> lyricList;
    public LyricView(Context context) {
        this(context, null);
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

    private void init(Context context, @Nullable AttributeSet attrs) {
        scroller = new Scroller(context);
        flingRunnable = new FlingRunnable();
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //scroll up distanceY > 0
                scrollOffsetY += (-distanceY);
                invalidate();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //velocity < 0  scroll up
                if (scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                scroller.fling(0, 0, 0, Math.abs((int) velocityY), 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (scroller.computeScrollOffset()) {
                    flingRunnable.setDirection(velocityY > 0 ? FlingRunnable.DOWN : FlingRunnable.UP);
                    ViewCompat.postOnAnimation(LyricView.this, flingRunnable);
                }
                return true;
            }
        });
        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setTextSize(spToPx(18));

        lyricList = new ArrayList<>();
    }

    private float spToPx(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isConsume = gestureDetector.onTouchEvent(event);
        int action = event.getActionMasked();
        if (!isConsume && (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)) {
            scrollToItemCenter();
        }
        return true;
    }

    /**
     * 获取距离中心线最近的item index
     * @return
     */
    private int getSnapItemIndex() {
        if (lyricList == null || lyricList.isEmpty()) {
            return -1;
        }

        int snapItemIndex = -1;
        int minOffsetY = Integer.MAX_VALUE;
        int halfViewHeight = viewHeight / 2;
        int index = 0;
        for (RowLyricWrapper wrapper : lyricList) {
            int y = wrapper.y;
            int offsetY = Math.abs(halfViewHeight - y);
            if (offsetY < minOffsetY) {
                minOffsetY = offsetY;
                snapItemIndex = index;
            }
            index++;
        }
        return snapItemIndex;
    }

    /**
     * 如果滑动结束后没有到达一个Item的中点,则手动处理
     * 那个item距离中心点最近
     */
    private void scrollToItemCenter() {
        int snapItemIndex = getSnapItemIndex();
        if (snapItemIndex != -1) {
            RowLyricWrapper wrapper = lyricList.get(snapItemIndex);
            int offset = viewHeight / 2 - wrapper.y;
            smoothScrollBy(offset);
        }
    }

    private ValueAnimator smoothScrollAnimator;

    private void smoothScrollBy(int dy) {
        if (smoothScrollAnimator != null && smoothScrollAnimator.isStarted()) {
            smoothScrollAnimator.cancel();
        }
        final int originalScrollOffsetY = scrollOffsetY;
        smoothScrollAnimator = ValueAnimator.ofInt(0, dy);
        smoothScrollAnimator.setDuration(260);
        smoothScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                scrollOffsetY = originalScrollOffsetY + value;
                postInvalidate();
            }
        });
        smoothScrollAnimator.start();
    }

    /**
     * 歌词内容高度
     */
    private int totalLyricHeight;

    /**
     * 歌词之间的间距
     */
    private int lyricSpacing = 30;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画出歌词
        if (lyricList == null || lyricList.isEmpty()) {
            return;
        }
        totalLyricHeight = 0;

        int width = getWidth();
        int currentHeight = scrollOffsetY; //滑动时改变
        int index = 0;
        int size = lyricList.size();
        int snapItemIndex = getSnapItemIndex();
        for (RowLyricWrapper wrapper : lyricList) {
            RowLyric rowLyric = wrapper.lyric;
            StaticLayout staticLayout = wrapper.staticLayout;
            String lyric = rowLyric.getLyric();
            if (staticLayout == null) {
                staticLayout = new StaticLayout(lyric, paint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, false);
                wrapper.staticLayout = staticLayout;
            }
            boolean last = index == size -1;
            totalLyricHeight += (staticLayout.getHeight() + (last ? 0 : lyricSpacing));
            paint.setColor(snapItemIndex == index ? Color.BLUE : Color.WHITE);

            canvas.save();
            canvas.translate(0, currentHeight);
            staticLayout.draw(canvas);
            wrapper.y = currentHeight + staticLayout.getHeight() / 2;
            currentHeight += (staticLayout.getHeight() + lyricSpacing);
            canvas.restore();
            index++;
        }
    }

    private int viewHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
    }

    /**
     * 设置歌词数据
     *
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

    private class FlingRunnable implements Runnable {
        private static final int UP = 1;
        private static final int DOWN = 2;
        private int oldCurrY;
        private int direction;

        @Override
        public void run() {
            //If it returns true,the animation is not yet finished.
            if (scroller != null && scroller.computeScrollOffset()) {
                int currY = scroller.getCurrY();
                int dy = oldCurrY - currY;
                oldCurrY = currY;
                scrollOffsetY += (direction == DOWN ? -dy : dy);
                boolean isFinish = computeScrollOffsetY(direction);
                postInvalidate();
                if (isFinish) {
                    scroller.abortAnimation();
                    oldCurrY = 0;
                    scrollToItemCenter();

                } else {
                    ViewCompat.postOnAnimation(LyricView.this, this);
                }

            } else {
                oldCurrY = 0;
                scrollToItemCenter();
            }
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        /**
         * 计算滑动偏移量,检查边界
         * @param direction 滑动方向 {@link #DOWN,#UP}
         * @return 是否滑动到边界点
         */
        private boolean computeScrollOffsetY(int direction) {
            boolean isFinish = false;
            //向下滑时,最大偏移量为ViewHeight / 2,向上滑时,最大偏移量为歌词内容高度 - ViewHeight / 2
            int halfViewHeight = viewHeight / 2;
            int maxScrollOffsetY = direction == DOWN ? halfViewHeight : totalLyricHeight - halfViewHeight;
            if (direction == DOWN && scrollOffsetY > maxScrollOffsetY) {
                scrollOffsetY = maxScrollOffsetY;
                isFinish = true;
            }

            if (direction == UP && scrollOffsetY < -maxScrollOffsetY) {
                scrollOffsetY = -maxScrollOffsetY;
                isFinish = true;
            }
            return isFinish;
        }
    }

    private class RowLyricWrapper {

        RowLyric lyric;
        StaticLayout staticLayout;
        int y;

        public RowLyricWrapper(RowLyric lyric, StaticLayout staticLayout) {
            this.lyric = lyric;
            this.staticLayout = staticLayout;
        }
    }
}
