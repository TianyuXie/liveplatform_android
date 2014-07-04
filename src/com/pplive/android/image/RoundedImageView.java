package com.pplive.android.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pplive.liveplatform.R;

public class RoundedImageView extends AsyncImageView {

    private float mCornerRadiusPixels;

    public RoundedImageView(Context context) {
        this(context, null /* attrs */);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /* defStyle */);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView);

            mCornerRadiusPixels = a.getDimension(R.styleable.RoundedImageView_corner_radius, 0);
            a.recycle();
        }
    }

    @Override
    public void setImageAsync(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().cloneFrom(options);
        if (mCornerRadiusPixels > 0) {
            builder.displayer(new RoundedBitmapDisplayer((int) mCornerRadiusPixels));
        }

        super.setImageAsync(imageUri, builder.build(), listener);
    }
}
