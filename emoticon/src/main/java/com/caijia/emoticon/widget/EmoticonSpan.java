package com.caijia.emoticon.widget;

import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

public class EmoticonSpan extends DynamicDrawableSpan {

    private int mSize;

    private Drawable mDrawable;

    public EmoticonSpan(Drawable drawable, int size) {
        super();
        mDrawable = drawable;
        mSize = size;
    }

    public Drawable getDrawable() {
        if (mDrawable != null) {
            mDrawable.setBounds(0, 0, mSize, mSize);
        }
        return mDrawable;
    }
}