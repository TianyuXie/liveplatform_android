package com.pplive.liveplatform.ui.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
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

    public void setImageAsync(String imageUri, int defaultImage) {
        setImageAsync(imageUri, defaultImage, null);
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options) {
        setImageAsync(imageUri, options, null);
    }

    public void setImageAsync(String imageUri, int defaultImage, ImageLoadingListener listener) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565);
        if (defaultImage > 0) {
            builder.showStubImage(defaultImage).showImageForEmptyUri(defaultImage).showImageOnFail(defaultImage);
        }
        DisplayImageOptions options = builder.build();
        mImageLoader.displayImage(imageUri, this, options, listener);
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {
        mImageLoader.displayImage(imageUri, this, options, listener);
    }
}
