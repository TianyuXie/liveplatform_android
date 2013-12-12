package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import com.pplive.liveplatform.util.ImageUtil;

public class CircularImageView extends AsyncImageView {
    static final String TAG = "_CircularImageView";

    private Paint mPaint;

    private Rect mRect;

    private boolean mRounded;

    private Rect getRect(Bitmap bitmap) {
        if (mRect == null) {
            mRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
        return mRect;
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable && mRounded) {
            Log.d(TAG, "onDraw Rounded");
            Bitmap bitmap = ImageUtil.getCircleBitmap(ImageUtil.scaleBitmap(((BitmapDrawable) drawable).getBitmap(), getWidth()));
            Rect rect = getRect(bitmap);
            canvas.drawBitmap(bitmap, rect, rect, mPaint);
        } else {
            super.onDraw(canvas);
        }
    }

    public void setRounded(boolean rounded) {
        this.mRounded = rounded;
    }
}