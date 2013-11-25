package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EnterSendEditText extends EditText {
    private OnEnterListener mOnEnterListener;
    private MotionEvent me1;
    private MotionEvent me2;

    public void setOnEnterListener(OnEnterListener l) {
        this.mOnEnterListener = l;
    }

    public EnterSendEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        me1 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
        me2 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0);
    }

    public EnterSendEditText(Context context) {
        this(context, null);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        Log.d("focus", "forcus");

        if (focused) {
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    dispatchTouchEvent(me1);
                    dispatchTouchEvent(me2);
                }
            }, 200);
        } else {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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

    @Override
    protected void onDetachedFromWindow() {
        me1.recycle();
        me2.recycle();
        super.onDetachedFromWindow();
    }
}
