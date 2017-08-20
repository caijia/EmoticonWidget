package com.caijia.emoticon.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.caijia.emoticon.util.EmoticonHelper;

/**
 * 可以添加和删除emoticon的EditText
 * Created by cai.jia on 2015/11/27.
 */
public class EmoticonEditText extends AppCompatEditText {

    public EmoticonEditText(Context context) {
        super(context);
        init();
    }

    public EmoticonEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public EmoticonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        EmoticonHelper.getInstance().loadEmoticon(getContext());
        new EmoticonHandler(this);
    }

    public void deleteEmoticon() {
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }
}
