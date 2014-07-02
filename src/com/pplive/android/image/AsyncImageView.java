package com.pplive.android.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pplive.liveplatform.Constants;

public class AsyncImageView extends ImageView {

    static final String TAG = AsyncImageView.class.getSimpleName();

    protected static final DisplayImageOptions DEFALUT_DISPLAY_OPTIONS = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).build();

    protected ImageLoader mImageLoader = ImageLoader.getInstance();

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
        boolean cacheOnDisk = imageUri != null && !imageUri.startsWith(Constants.LIVE_IMGAE_PREFIX);
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().cloneFrom(DEFALUT_DISPLAY_OPTIONS).cacheInMemory(true).cacheOnDisk(cacheOnDisk);

        if (defaultImage > 0) {
            builder.showImageOnLoading(defaultImage).showImageForEmptyUri(defaultImage).showImageOnFail(defaultImage);
        }

        setImageAsync(imageUri, builder.build(), listener);
    }

    @Override
    @Deprecated
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    public void setLocalImage(int resid) {
        setImageResource(resid);
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {
        if (imageUri == null) {

        } else {
            Log.d(TAG, "imageUri:" + imageUri);
            mImageLoader.displayImage(imageUri, this, options, listener);
        }
    }
}
