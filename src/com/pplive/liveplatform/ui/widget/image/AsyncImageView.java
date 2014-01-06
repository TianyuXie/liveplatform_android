package com.pplive.liveplatform.ui.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class AsyncImageView extends ImageView {
    static final String TAG = "_AsyncImageView";

    private ImageLoader mImageLoader = ImageLoader.getInstance();

    private String mUrl;

    public AsyncImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /* defStyle */);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageAsync(String imageUri) {
        setImageAsync(imageUri, null);
    }

    public void setImageAsync(String imageUri, int defaultImage) {
        setImageAsync(imageUri, defaultImage, null);
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options) {
        setImageAsync(imageUri, options, null);
    }

    public void setImageAsync(String imageUri, int defaultImage, ImageLoadingListener listener) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisc(!imageUri.startsWith("http://live2image"));
        if (defaultImage > 0) {
            builder.showStubImage(defaultImage).showImageForEmptyUri(defaultImage).showImageOnFail(defaultImage);
        }
        setImageAsync(imageUri, builder.build(), listener);
    }

    @Override
    public void setImageResource(int resId) {
        mUrl = null;
        super.setImageResource(resId);
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {
        if (imageUri != null && !imageUri.equals(mUrl)) {
            mUrl = imageUri;
            Log.d(TAG, "imageUri:" + imageUri);
            mImageLoader.displayImage(imageUri, this, options, listener);
        }
    }
}
