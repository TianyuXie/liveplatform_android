package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class BlurRelativeLayout extends RelativeLayout {
    
    private Paint mPaint;
    
    private MaskFilter mBlur;
    
    public BlurRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0 /* defStyle */);
        
        mPaint = new Paint();
        mPaint.setAlpha(80);
        mPaint.setTextSize(100);
        mPaint.setColor(Color.WHITE);
        mPaint.setMaskFilter(mBlur);
        mBlur = new BlurMaskFilter(10, Blur.NORMAL);
    }
    
    public BlurRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.drawColor(Color.WHITE);
    }
}
