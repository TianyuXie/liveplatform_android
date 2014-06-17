package com.pplive.liveplatform.ui.widget.image;

import android.content.Context;
import android.util.AttributeSet;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class RoundedImageView extends AsyncImageView {

    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageAsync(String imageUri, int defaultImage) {

        DisplayImageOptions options = new DisplayImageOptions.Builder().cloneFrom(DEFALUT_DISPLAY_OPTIONS).cacheOnDisc(true).cacheInMemory(true)
                .displayer(new CircularBitmapDisplayer()).build();

        super.setImageAsync(imageUri, options, null);
    }
}