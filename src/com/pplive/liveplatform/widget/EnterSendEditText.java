package com.pplive.liveplatform.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EnterSendEditText extends EditText {

    static final String TAG = EnterSendEditText.class.getSimpleName();

    private OnEnterListener mOnEnterListener;

    public void setOnEnterListener(OnEnterListener l) {
        this.mOnEnterListener = l;
    }

    public EnterSendEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EnterSendEditText(Context context) {
        this(context, null);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (focused) {
            Log.d(TAG, "get focus");
            postDelayed(new Runnable() {
                public void run() {
                    imm.showSoftInput(EnterSendEditText.this, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 200);
        } else {
            Log.d(TAG, "clear focus");
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
            }
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mOnEnterListener != null && mOnEnterListener.onEnter(this)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        int length = text.length();
        if (length > 0 && text.charAt(length - 1) == '\n') {
            setText(text.subSequence(0, length - 1));
            if (mOnEnterListener != null) {
                mOnEnterListener.onEnter(this);
            }
        } else {
            super.onTextChanged(text, start, lengthBefore, lengthAfter);
        }
    }

    public interface OnEnterListener {
        public boolean onEnter(View v);
    }
}
