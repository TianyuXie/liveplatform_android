package com.pplive.liveplatform.ui.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.pplive.liveplatform.Constants;

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
        boolean cacheOnDisc = imageUri != null && !imageUri.startsWith(Constants.LIVE_IMGAE_PREFIX);
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisc(cacheOnDisc);
        if (defaultImage > 0) {
            builder.showStubImage(defaultImage).showImageForEmptyUri(defaultImage).showImageOnFail(defaultImage);
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
        mUrl = null;
    }

    public void setImageAsync(String imageUri, DisplayImageOptions options, ImageLoadingListener listener) {
        if (imageUri == null) {
            setLocalImage(options.getImageForEmptyUri());
        } else if (!imageUri.equals(mUrl)) {
            Log.d(TAG, "imageUri:" + imageUri);
            mUrl = imageUri;
            mImageLoader.displayImage(imageUri, this, options, listener);
        }
    }
}
