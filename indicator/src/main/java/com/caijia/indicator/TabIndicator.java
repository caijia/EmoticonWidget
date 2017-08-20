package com.caijia.indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by cai.jia on 2017/8/17.
 */

public class TabIndicator extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    private static final String COLOR_DEFAULT_DIVIDER = "#eeeeee";
    private static final int MODE_FIXED = 1;
    private static final int MODE_SCROLL = 2;
    private static final int INDICATOR_WRAP = 3;
    private static final int INDICATOR_FILL = 4;

    private ViewPager viewPager;
    private LinearLayout tabContainer;
    private int customTabId;
    private int currentPosition;
    private float positionOffset;
    private int selectedPosition;
    private int dividerWidth;
    private int dividerPadding;
    private int tabMode;
    private int indicatorMode;
    private int tabTextSize;
    private int tabBackground;
    private int tabSelectColor;
    private int tabNormalColor;
    private int tabIndicatorColor;
    private float tabIndicatorHeight;
    private int tabPaddingHorizontal;
    private int tabParentPaddingHorizontal;
    private Paint dividerPaint;
    private Paint indicatorPaint;
    private LinearLayout.LayoutParams wrapTabWidthParams;
    private LinearLayout.LayoutParams weightTabWidthParams;
    private ViewPager.OnAdapterChangeListener onAdapterChangeListener;
    private PagerAdapterObserver pagerAdapterObserver;

    public TabIndicator(Context context) {
        this(context, null);
    }

    public TabIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        // Default enabled state
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        return new ColorStateList(states, colors);
    }

    private void init(Context context, AttributeSet attrs) {
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);
        setWillNotDraw(false);
        tabContainer = new LinearLayout(context);
        tabContainer.setOrientation(LinearLayout.HORIZONTAL);
        wrapTabWidthParams = new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
        weightTabWidthParams = new LinearLayout.LayoutParams(0, MATCH_PARENT, 1f);
        addView(tabContainer, new HorizontalScrollView.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        int dividerColor;
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.TabIndicator);
            dividerWidth = a.getDimensionPixelOffset(R.styleable.TabIndicator_ti_divider_width, 1);
            dividerPadding = a.getDimensionPixelOffset(R.styleable.TabIndicator_ti_divider_padding,
                    dpToPx(8));
            dividerColor = a.getColor(R.styleable.TabIndicator_ti_divider_color,
                    Color.parseColor(COLOR_DEFAULT_DIVIDER));
            tabMode = a.getInt(R.styleable.TabIndicator_ti_tab_mode, MODE_SCROLL);
            indicatorMode = a.getInt(R.styleable.TabIndicator_ti_indicator_mode, INDICATOR_FILL);
            tabTextSize = a.getDimensionPixelSize(R.styleable.TabIndicator_android_textSize,
                    spToPx(14));
            tabBackground = a.getResourceId(R.styleable.TabIndicator_ti_tab_background, 0);
            tabSelectColor = a.getColor(R.styleable.TabIndicator_ti_select_color, Color.RED);
            tabNormalColor = a.getColor(R.styleable.TabIndicator_ti_normal_color, Color.BLACK);
            tabIndicatorColor = a.getColor(R.styleable.TabIndicator_ti_tab_indicator_color,
                    Color.RED);
            tabIndicatorHeight = a.getDimensionPixelOffset(
                    R.styleable.TabIndicator_ti_tab_indicator_height, dpToPx(2));
            tabPaddingHorizontal = a.getDimensionPixelOffset(
                    R.styleable.TabIndicator_ti_tab_padding_horizontal, 0);
            tabParentPaddingHorizontal = a.getDimensionPixelOffset(
                    R.styleable.TabIndicator_ti_tab_parent_padding_horizontal, 0);
            tabParentPaddingHorizontal = tabMode == MODE_FIXED ? 0 : tabParentPaddingHorizontal;
        } finally {
            if (a != null) {
                a.recycle();
            }
        }

        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setAntiAlias(true);
        dividerPaint.setColor(dividerColor);

        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setColor(tabIndicatorColor);
    }

    public void setupWithViewPager(ViewPager pager) {
        if (viewPager != null) {
            if (onAdapterChangeListener != null) {
                viewPager.removeOnAdapterChangeListener(onAdapterChangeListener);
                onAdapterChangeListener = null;
            }

            PagerAdapter pagerAdapter = viewPager.getAdapter();
            if (pagerAdapter != null && pagerAdapterObserver != null) {
                pagerAdapter.unregisterDataSetObserver(pagerAdapterObserver);
                pagerAdapterObserver = null;
            }
        }

        viewPager = pager;

        PagerAdapter pagerAdapter = pager.getAdapter();
        viewPager.addOnPageChangeListener(this);

        onAdapterChangeListener = new AdapterChangeListener();
        viewPager.addOnAdapterChangeListener(onAdapterChangeListener);

        pagerAdapterObserver = new PagerAdapterObserver();
        pagerAdapter.registerDataSetObserver(pagerAdapterObserver);

        populateFromPagerAdapter(pagerAdapter);
    }

    private int dpToPx(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics()));
    }

    private int spToPx(float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, getResources().getDisplayMetrics()));
    }

    public void setCustomTab(@LayoutRes int customTabId) {
        this.customTabId = customTabId;
    }

    private void populateFromPagerAdapter() {
        if (viewPager == null) {
            return;
        }
        populateFromPagerAdapter(viewPager.getAdapter());
    }

    private void populateFromPagerAdapter(PagerAdapter adapter) {
        if (adapter == null) {
            return;
        }
        tabContainer.removeAllViews();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            if (customTabId != 0) {
                View tabView = LayoutInflater.from(getContext())
                        .inflate(customTabId, tabContainer, false);
                tabContainer.addView(tabView);

            } else {
                LinearLayout tabParent = new LinearLayout(getContext());
                tabParent.setGravity(Gravity.CENTER);

                TextView tabView = new TextView(getContext());
                tabView.setGravity(Gravity.CENTER);
                tabView.setPadding(tabPaddingHorizontal, 0, tabPaddingHorizontal, 0);
                tabView.setText(adapter.getPageTitle(i));
                tabView.setMaxLines(1);
                tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tabView.setTextColor(createColorStateList(tabNormalColor, tabSelectColor));
                tabParent.addView(tabView, WRAP_CONTENT, MATCH_PARENT);

                Drawable tabBackgroundDrawable = tabBackground == 0
                        ? getSystemSelectableItemBackground()
                        : AppCompatResources.getDrawable(getContext(), tabBackground);
                ViewCompat.setBackground(tabView, tabBackgroundDrawable);
                final int tabIndex = i;
                tabParent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //click tab
                        if (viewPager != null) {
                            viewPager.setCurrentItem(tabIndex);
                        }
                    }
                });
                tabParent.setPadding(tabParentPaddingHorizontal, 0, tabParentPaddingHorizontal, 0);
                tabContainer.addView(tabParent, tabMode == MODE_FIXED
                        ? wrapTabWidthParams
                        : weightTabWidthParams);
            }
        }
    }

    private Drawable getSystemSelectableItemBackground() {
        int[] attribute = new int[]{android.R.attr.selectableItemBackground};
        TypedValue typedValue = new TypedValue();
        getContext().getTheme()
                .resolveAttribute(attribute[0], typedValue, true);
        TypedArray typedArray = getContext().getTheme()
                .obtainStyledAttributes(typedValue.resourceId, attribute);
        return typedArray.getDrawable(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.currentPosition = position;
        this.positionOffset = positionOffset;
        int scrollX = calculateScrollXForTab(position, positionOffset);
        scrollTo(scrollX, 0);
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        selectedPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        if (tabMode == MODE_SCROLL) {
            final View selectedChild = tabContainer.getChildAt(position);
            final View nextChild = position + 1 < tabContainer.getChildCount()
                    ? tabContainer.getChildAt(position + 1)
                    : null;
            final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
            final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;

            return selectedChild.getLeft()
                    + ((int) ((selectedWidth + nextWidth) * positionOffset * 0.5f))
                    + (selectedChild.getWidth() / 2)
                    - (getWidth() / 2);
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || viewPager == null || viewPager.getAdapter() == null
                || viewPager.getAdapter().getCount() == 0) {
            return;
        }
        int childCount = tabContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View tabView = tabContainer.getChildAt(i);

            //selected
            tabView.setSelected(i == selectedPosition);

            //draw divider line
            boolean isLast = i == childCount - 1;
            if (isLast) {
                continue;
            }
            int right = tabView.getRight();
            int tabHeight = tabView.getHeight();
            canvas.drawRect(right - dividerWidth, dividerPadding, right,
                    tabHeight - dividerPadding, dividerPaint);
        }

        //draw indicator
        drawIndicator(canvas);
    }

    private void drawIndicator(Canvas canvas) {
        View selectView = tabContainer.getChildAt(currentPosition);
        View nextView = tabContainer.getChildAt(currentPosition + 1);
        View selectTabView = selectView != null ? ((ViewGroup) selectView).getChildAt(0) : null;
        View nextTabView = nextView != null ? ((ViewGroup) nextView).getChildAt(0) : null;

        int selectViewLeft = selectView == null ? 0 : selectView.getLeft();
        int selectViewRight = selectView == null ? 0 : selectView.getRight();
        int selectTabLeft = selectTabView == null ? 0 : selectTabView.getLeft();

        int nextViewLeft = nextView == null ? 0 : nextView.getLeft();
        int nextViewRight = nextView == null ? 0 : nextView.getRight();
        int nextTabLeft = nextTabView == null ? 0 : nextTabView.getLeft();

        if (indicatorMode == INDICATOR_FILL) {
            selectTabLeft = 0;
            nextTabLeft = 0;
        }

        int indicatorLeft = (int) ((selectViewLeft + selectTabLeft) * (1 - positionOffset)
                + (nextViewLeft + nextTabLeft) * positionOffset);
        int indicatorRight = (int) ((selectViewRight - selectTabLeft) * (1 - positionOffset)
                + (nextViewRight - nextTabLeft) * positionOffset);

        int height = getHeight();
        canvas.drawRect(indicatorLeft, height - tabIndicatorHeight,
                indicatorRight, height, indicatorPaint);
    }

    private class PagerAdapterObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            populateFromPagerAdapter();
        }

        @Override
        public void onInvalidated() {
            populateFromPagerAdapter();
        }
    }

    private class AdapterChangeListener implements ViewPager.OnAdapterChangeListener {

        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager,
                                     @Nullable PagerAdapter oldAdapter,
                                     @Nullable PagerAdapter newAdapter) {
            if (TabIndicator.this.viewPager == viewPager) {
                populateFromPagerAdapter();
            }
        }
    }
}
