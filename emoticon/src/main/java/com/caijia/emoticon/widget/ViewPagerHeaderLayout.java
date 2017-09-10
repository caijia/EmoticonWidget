package com.caijia.emoticon.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

/**
 * Created by cai.jia on 2017/9/6 0006.
 */

public class ViewPagerHeaderLayout extends FrameLayout implements NestedScrollingParent {

    private static final String TAG = "viewPager_header_layout";
    View headerView;
    View scrollingViewParent;
    private NestedScrollingParentHelper nestedScrollingParentHelper;
    private int headVisibleMinHeight = 120;
    private int scrollDistance;
    private int minFlingVelocity;
    private int maxFlingVelocity;
    private float initialMotionX;
    private float initialMotionY;
    private float lastTouchX;
    private float lastTouchY;
    private int activePointerId;
    private boolean isBeginDragged;
    private VelocityTracker velocityTracker;
    private int maxFlexibleHeight;
    private int touchSlop;
    private ScrollerCompat mScroller;
    private FlingRunnable flingRunnable;
    private View currScrollingView;

    public ViewPagerHeaderLayout(Context context) {
        this(context, null);
    }

    public ViewPagerHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewPagerHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        minFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        maxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mScroller = ScrollerCompat.create(context);
        flingRunnable = new FlingRunnable();
    }

    public void setHeadVisibleMinHeight(int minHeight) {
        this.headVisibleMinHeight = minHeight;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new RuntimeException("only two child view");
        }

        headerView = getChildAt(0);
        scrollingViewParent = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int left = getPaddingLeft();
        int right = getMeasuredWidth() - getPaddingRight();
        int headerViewHeight = headerView.getMeasuredHeight();
        headerView.layout(left, getPaddingTop(), right, getPaddingTop() + headerViewHeight);
        scrollingViewParent.layout(left, getPaddingTop() + headerViewHeight, right,
                getPaddingTop() + headerViewHeight + scrollingViewParent.getMeasuredHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxFlexibleHeight = headerView.getMeasuredHeight() - headVisibleMinHeight;
    }

    private void onMoveDown(MotionEvent ev) {
        initialMotionY = lastTouchY = ev.getY(0);
        initialMotionX = lastTouchX = ev.getX(0);
        activePointerId = ev.getPointerId(0);
        isBeginDragged = false;
    }

    private void onSecondaryPointerDown(MotionEvent ev) {
        int index = ev.getActionIndex();
        activePointerId = ev.getPointerId(index);
        lastTouchX = ev.getX(index);
        lastTouchY = ev.getY(index);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int index = ev.getActionIndex();
        if (ev.getPointerId(index) == activePointerId) {
            final int newIndex = index == 0 ? 1 : 0;
            activePointerId = ev.getPointerId(newIndex);
            lastTouchX = ev.getX(newIndex);
            lastTouchY = ev.getY(newIndex);
        }
    }

    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    private void recyclerVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private void computeVelocity() {
        if (velocityTracker != null) {
            velocityTracker.computeCurrentVelocity(1000);
        }
    }

    /**
     * 获取当前可伸缩的高度
     *
     * @return
     */
    private int getRemainingFlexibleHeight() {
        return headerView.getHeight() - headVisibleMinHeight + scrollDistance;
    }

    private
    @Nullable
    View getCurrentScrollView() {
        if (scrollingViewParent == null) {
            return null;
        }

        return findScrollingView(scrollingViewParent);
    }

    public interface CurrentViewProvider {

        View provideCurrentView(int position);
    }

    private View findScrollingView(View view) {
        if (isScrollingView(view)) {
            return view;
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            if (group instanceof ViewPager) {
                ViewPager pager = (ViewPager) group;
                PagerAdapter adapter = pager.getAdapter();
                if (!(adapter instanceof CurrentViewProvider)) {
                    throw new RuntimeException("PagerAdapter must be implements"
                            + CurrentViewProvider.class.getCanonicalName());
                }

                CurrentViewProvider provider = (CurrentViewProvider) adapter;
                View currentView = provider.provideCurrentView(pager.getCurrentItem());
                View scrollingView = findScrollingView(currentView);
                if (scrollingView != null)
                    Log.d(TAG, "scrollingView=" + scrollingView.toString());
                return scrollingView;

            }else{
                int childCount = group.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = group.getChildAt(i);
                    View scrollingView = findScrollingView(child);
                    if (scrollingView != null)
                        return scrollingView;
                }
            }
        }
        return null;
    }

    private boolean isScrollingView(View view) {
        return view != null && (view instanceof ScrollingView || view instanceof AbsListView
                || view instanceof ScrollView || view instanceof WebView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        addVelocityTracker(ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                onMoveDown(ev);
                currScrollingView = getCurrentScrollView();
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                onSecondaryPointerDown(ev);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(activePointerId);
                if (index < 0) {
                    return false;
                }

                float x = ev.getX(index);
                float y = ev.getY(index);

                startDragging(x, y);

                lastTouchX = x;
                lastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                recyclerVelocityTracker();
                isBeginDragged = false;
                activePointerId = INVALID_POINTER;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
                break;
            }
        }
        return isBeginDragged;
    }

    /**
     * 拦截条件:
     * 0.垂直滑动距离大于水平滑动距离
     * ViewPager里面滚动的View(别名vb){@link #getCurrentScrollView()}
     * 1.当(vb)到达顶点时 && 向下滑动
     * 2.向上滑动 && {@link #getRemainingFlexibleHeight()} != 0
     *
     * @return
     */
    private void startDragging(float x, float y) {
        if (currScrollingView == null) {
            Log.d(TAG, "currentScrollView is null");
            return;
        }
        float dy = y - lastTouchY;
        float xDis = Math.abs(x - initialMotionX);
        float yDis = Math.abs(y - initialMotionY);
        if (Math.hypot(xDis, yDis) > touchSlop && yDis > xDis) {
            boolean isTop = scrollingViewIsTop();
            int remainingFlexibleHeight = getRemainingFlexibleHeight();
            if ((dy < 0 && remainingFlexibleHeight != 0) || (isTop && dy > 0)) {
                isBeginDragged = true;
            }else{
                isBeginDragged = false;
            }
        }
    }

    private boolean scrollingViewIsTop() {
        if (currScrollingView == null) {
            return false;
        }
        Log.d(TAG, "scrollY = " + currScrollingView.getScrollY());
        return currScrollingView.getScrollY() == 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        addVelocityTracker(ev);

        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                onMoveDown(ev);
                currScrollingView = getCurrentScrollView();
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                onSecondaryPointerDown(ev);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(activePointerId);
                if (index < 0) {
                    return false;
                }

                float x = ev.getX(index);
                float y = ev.getY(index);

                startDragging(x, y);
                //move
                int dy = (int) (y - lastTouchY);
                Log.d(TAG, "move dy = " + dy);

                //上滑
                if (dy < 0) {
                    //上滑动,可伸缩的距离已经滑完,这时滑动dy不进行平移,而是滑动currScrollingView
                    int overflowDis = translateChild(dy);
                    if (overflowDis != 0) {
                        handleCurrentScrollingView(-overflowDis);
                    }

                }else{
                    boolean isTop = scrollingViewIsTop();
                    if (isTop) {
                        translateChild(dy);

                    }else{
                        //将currScrollingView滑动到顶部,然后再平移头部
                        handleCurrentScrollingView(-dy);
                    }
                }

                lastTouchX = x;
                lastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                int index = ev.findPointerIndex(activePointerId);
                if (index < 0) {
                    return false;
                }
                computeVelocity();
                int velocityY = (int) velocityTracker.getYVelocity(activePointerId);
                //fling
                Log.d(TAG, "touch fling velocityY = " + velocityY);

                if (velocityIsValid(velocityY)) {
                    fling(-velocityY);
                }
                activePointerId = INVALID_POINTER;
                isBeginDragged = false;
                break;
            }
        }
        return true;
    }

    private void handleCurrentScrollingView(int dy) {
        currScrollingView.scrollBy(0, dy);
    }

    private boolean velocityIsValid(int velocity) {
        int absVelocity = Math.abs(velocity);
        return absVelocity > minFlingVelocity && absVelocity < maxFlingVelocity;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {

    }

    @Override
    public void onStopNestedScroll(View target) {

    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//        boolean isTop = scrollingViewIsTop();
//        if (isTop) {
//            //可以向上滚动的距离 dy>0表示向上滚动
//            int remainingFlexibleHeight = getRemainingFlexibleHeight();
//            if (dy > 0 && remainingFlexibleHeight > 0) {
//                if (dy > remainingFlexibleHeight) {
//                    consumed[1] = remainingFlexibleHeight;
//                } else {
//                    consumed[1] = dy;
//                }
//                translateChild(-consumed[1]);
//
//            } else if (dy < 0 && remainingFlexibleHeight < maxFlexibleHeight) {
//                if (dy > maxFlexibleHeight - remainingFlexibleHeight) {
//                    consumed[1] = maxFlexibleHeight - remainingFlexibleHeight;
//
//                } else {
//                    consumed[1] = dy;
//                }
//                translateChild(-consumed[1]);
//            }
//        }
    }

    /**
     * 滚动HeaderView 和 TargetView
     * @param dy y轴的距离差
     * @return 返回溢出距离(假如 距离顶部的距离3,dy = 5,这时溢出为2)
     */
    private int translateChild(int dy) {
        scrollDistance += dy;
        int overflowDis = 0;
        if (scrollDistance > 0) {
            overflowDis = scrollDistance;
            scrollDistance = 0;
        }

        if (scrollDistance < -maxFlexibleHeight) {
            overflowDis = scrollDistance + maxFlexibleHeight;
            scrollDistance = -maxFlexibleHeight;
        }
        headerView.setTranslationY(scrollDistance);
        scrollingViewParent.setTranslationY(scrollDistance);
        if (headerViewScrollListener != null) {
            headerViewScrollListener.onHeaderViewScroll(scrollDistance, maxFlexibleHeight);
        }
        return overflowDis;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //手动滑动 currentScrollView 到top ,然后手动滑动头部。
        return false;
    }

    private void fling(int velocityY) {
        if (!mScroller.isFinished()) {
            Log.d(TAG, "fling is not finish");
            return;
        }

        mScroller.fling(
                0, 0, //init value
                0, velocityY, //velocity
                0, 0, // x
                Integer.MIN_VALUE, Integer.MAX_VALUE); //y
        if (mScroller.computeScrollOffset()) {
            flingRunnable.setDirection(velocityY > 0 ? FlingRunnable.UP : FlingRunnable.DOWN);
            ViewCompat.postOnAnimation(this, flingRunnable);
        }
    }

    private void flingTarget(int currVelocity) {
        final View targetView = currScrollingView;
        if (targetView == null) {
            Log.d(TAG, "fling target is null");
            return;
        }

        if (targetView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) targetView;
            recyclerView.fling(0, currVelocity);

        } else if (targetView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) targetView;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                absListView.fling(currVelocity);
            }

        } else if (targetView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) targetView;
            scrollView.fling((int) mScroller.getCurrVelocity());

        } else if (targetView instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) targetView;
            scrollView.fling(currVelocity);

        } else if (targetView instanceof WebView) {
            WebView webView = (WebView) targetView;
            webView.flingScroll(0, currVelocity);
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return nestedScrollingParentHelper.getNestedScrollAxes();
    }

    private class FlingRunnable implements Runnable {

        static final int DOWN = 1;
        static final int UP = -1;
        private int direction;

        private int oldCurrY;

        void setDirection(int direction) {
            this.direction = direction;
        }

        private void reset() {
            oldCurrY = 0;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                int dy = oldCurrY - mScroller.getCurrY();
                Log.d(TAG, "fling dy=" + dy);
                translateChild(dy);
                oldCurrY = mScroller.getCurrY();
                switch (direction) {
                    case UP: {
                        boolean toTop = scrollDistance <= -maxFlexibleHeight;
                        if (toTop) {
                            reset();
                            int currVelocity = Math.round(mScroller.getCurrVelocity());
                            mScroller.abortAnimation();
                            flingTarget(currVelocity);

                        } else {
                            ViewCompat.postOnAnimation(ViewPagerHeaderLayout.this, this);
                        }
                        break;
                    }

                    case DOWN: {
                        boolean toBottom = scrollDistance >= 0;
                        if (toBottom) {
                            reset();
                            mScroller.abortAnimation();

                        } else {
                            ViewCompat.postOnAnimation(ViewPagerHeaderLayout.this, this);
                        }
                        break;
                    }
                }

            } else {
                reset();
            }
        }
    }

    public interface OnHeaderViewScrollListener{

        /**
         * @param scrollDis 头部View滚动的距离
         * @param maxScrollDis 头部View最大滚动的距离
         */
        void onHeaderViewScroll(int scrollDis, int maxScrollDis);
    }

    private OnHeaderViewScrollListener headerViewScrollListener;

    public void setOnHeaderViewScrollListener(OnHeaderViewScrollListener listener) {
        this.headerViewScrollListener = listener;
    }
}
