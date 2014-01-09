package com.pplive.liveplatform.ui.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import com.pplive.liveplatform.util.ImageUtil;

public class CircularImageView extends AsyncImageView {
    static final String TAG = "_CircularImageView";

    private Bitmap mBitmap;

    private Paint mPaint;

    private Rect mRect;

    private boolean mRounded;

    public CircularImageView(Context context) {
        this(context, null);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mRounded = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable && mRounded) {
            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
            mBitmap = ImageUtil.getCircleBitmap(ImageUtil.getScaledBitmap(drawable, getWidth()));
            Rect rect = getRect(mBitmap);
            canvas.drawBitmap(mBitmap, rect, rect, mPaint);
        } else {
            super.onDraw(canvas);
        }
    }

    private Rect getRect(Bitmap bitmap) {
        if (mRect == null) {
            mRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
        return mRect;
    }

    public void setRounded(boolean rounded) {
        try {
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setRounded:" + rounded);
        this.mRounded = rounded;
    }

    public void release() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }
}