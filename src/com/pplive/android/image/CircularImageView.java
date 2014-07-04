package com.pplive.android.image;

import android.content.Context;
import android.util.AttributeSet;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class CircularImageView extends AsyncImageView {

    public CircularImageView(Context context) {
        this(context, null);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageAsync(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().cloneFrom(options).displayer(new CircularBitmapDisplayer());

        super.setImageAsync(imageUri, builder.build(), listener);
    }
}
