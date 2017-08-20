package com.caijia.emoticon.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.widget.TextView;

import com.caijia.emoticon.util.EmoticonHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cai.jia on 2015/12/2.
 */
public class EmoticonHandler implements TextWatcher {

    private TextView mTextView;

    private List<ImageSpan> mRemoveSpanList;

    public EmoticonHandler(TextView textView) {
        mRemoveSpanList = new ArrayList<>();
        mTextView = textView;
        mTextView.addTextChangedListener(this);
    }

    /**
     * (删除) -> count
     *
     * @param s     表示之前的文本内容
     * @param start 添加时 ->  光标开始的位置,删除时为光标结束位置
     * @param count 删除时 ->  添加的字符个数,添加时为0
     * @param after 添加时 ->  添加的字符个数,删除时为0
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (count > 0) {
            //表示删除,找到删除中是否包含imageSpan
            Editable message = mTextView.getEditableText();
            ImageSpan[] spans = message.getSpans(start, start + count, ImageSpan.class);
            if (spans != null && spans.length > 0) {
                Collections.addAll(mRemoveSpanList, spans);
            }
        }
    }

    /**
     * @param s 当前文本内容
     */
    @Override
    public void afterTextChanged(Editable s) {
        Editable message = mTextView.getEditableText();
        if (!mRemoveSpanList.isEmpty()) {
            for (ImageSpan span : mRemoveSpanList) {
                message.removeSpan(span);
            }
            mRemoveSpanList.clear();
        }
    }

    /**
     * (添加) -> count
     *
     * @param s      表示当前的文本内容
     * @param start  添加时 ->  光标开始的位置,删除时为光标结束位置
     * @param before 删除时 -> 删除的字符个数,添加时为0
     * @param count  添加时 -> 添加的字符个数,删除时为0
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count > 0) {
            //添加的字符中是否包含表情编码
            CharSequence text = s.subSequence(start, start + count);
            Editable editable = mTextView.getEditableText();

            Pattern p = Pattern.compile(EmoticonHelper.getInstance().getPattern(),
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(text);

            while (matcher.find()) {
                String unicode = matcher.group();
                int drawableInt = EmoticonHelper.getInstance().getEmoticon(unicode);
                if (drawableInt != 0) {
                    Drawable newDrawable = ContextCompat.getDrawable(mTextView.getContext(),drawableInt);
                    EmoticonSpan emoticonSpan = new EmoticonSpan(newDrawable, dpToPx(mTextView.getContext(), 16));
                    int startPos = matcher.start() + start;
                    int endPos = matcher.end() + start;
                    editable.setSpan(emoticonSpan, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private int dpToPx(Context context,float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics()));
    }
}
