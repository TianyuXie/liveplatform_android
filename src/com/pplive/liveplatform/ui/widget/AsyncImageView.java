package com.pplive.liveplatform.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class AsyncImageView extends ImageView {

    private ImageLoader mImageLoader = ImageLoader.getInstance();

    public AsyncImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /* defStyle */);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageAsync(String imageUri) {
        mImageLoader.displayImage(imageUri, this);
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options) {
        mImageLoader.displayImage(imageUri, this, options);
    }

    public void setImageAsync(String imageUri, ImageLoadingListener listener) {
        mImageLoader.displayImage(imageUri, this, listener);
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {
        mImageLoader.displayImage(imageUri, this, options, listener);
    }
}
