package com.caijia.emoticon.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.caijia.emoticon.util.EmoticonHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 可以添加和删除emoticon的TextView
 * Created by cai.jia on 2015/11/27.
 */
public class EmoticonTextView extends AppCompatTextView {

    public EmoticonTextView(Context context) {
        this(context, null);
    }

    public EmoticonTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public EmoticonTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void init() {
        EmoticonHelper.getInstance().loadEmoticon(getContext());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Pattern p = Pattern.compile(EmoticonHelper.getInstance().getPattern(),
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);

        while (matcher.find()) {
            String unicode = matcher.group();
            int drawableInt = EmoticonHelper.getInstance().getEmoticon(unicode);
            if (drawableInt != 0) {
                Drawable newDrawable = ContextCompat.getDrawable(getContext(), drawableInt);
                EmoticonSpan span = new EmoticonSpan(newDrawable, dpToPx(getContext(), 16));
                int startPos = matcher.start();
                int endPos = matcher.end();
                builder.setSpan(span, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(builder, type);
    }

    private int dpToPx(Context context, float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics()));
    }
}
