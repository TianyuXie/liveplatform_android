package com.pplive.liveplatform.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pplive.liveplatform.util.ImageUtil;

public class CircularImageView extends AsyncImageView {

    static final String TAG = CircularImageView.class.getSimpleName();

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
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }

            mBitmap = ImageUtil.getCircleBitmap(drawable, getWidth());

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

    @Override
    public void setImageAsync(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {
        this.mRounded = true;
        super.setImageAsync(imageUri, options, listener);
    }

    public void release() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @Override
    public void setLocalImage(int resid) {
        setLocalImage(resid, true);
    }

    public void setLocalImage(int resid, boolean rounded) {
        this.mRounded = rounded;
        super.setLocalImage(resid);
    }

}