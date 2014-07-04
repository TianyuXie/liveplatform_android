package com.pplive.android.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
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
        setImageAsync(imageUri, DEFALUT_DISPLAY_OPTIONS);
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options) {
        setImageAsync(imageUri, options, null);
    }

    public void setImageAsync(String imageUri, ImageLoadingListener listener) {
        setImageAsync(imageUri, DEFALUT_DISPLAY_OPTIONS, listener);
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {
        boolean cacheOnDisk = imageUri != null && !imageUri.startsWith(Constants.LIVE_IMGAE_PREFIX);
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().cloneFrom(options).cacheInMemory(true).cacheOnDisk(cacheOnDisk);

        if (!TextUtils.isEmpty(imageUri)) {
            mImageLoader.displayImage(imageUri, this, builder.build(), listener);
        }
    }
}
