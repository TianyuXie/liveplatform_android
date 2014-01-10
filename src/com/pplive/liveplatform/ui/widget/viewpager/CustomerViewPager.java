package com.pplive.liveplatform.ui.widget.viewpager;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomerViewPager extends ViewPager {

    private boolean isScrollable;

    private Context context;

    public CustomerViewPager(Context context) {
        super(context);
        this.context = context;
        this.isScrollable = true;
    }

    public CustomerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.isScrollable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScrollable == false) {
            return false;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isScrollable == false) {
            return false;
        } else {
            return super.onInterceptTouchEvent(ev);
        }

    }

    public boolean isScrollable() {
        return isScrollable;
    }

    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }

    public void resetSwitchDuration() {
        setSwitchDuration(0);
    }

    public void setSwitchDuration(int duration) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            SpeedScroller scroller = new SpeedScroller(context);
            scroller.setDuration(duration);
            mScroller.set(this, scroller);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    
}
