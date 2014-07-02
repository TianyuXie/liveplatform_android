package com.pplive.android.image;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class CircularBitmapDisplayer implements BitmapDisplayer {

    public CircularBitmapDisplayer() {
        super();

    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        int width = imageAware.getWidth();
        int height = imageAware.getHeight();

        imageAware.setImageDrawable(new RoundedBitmapDisplayer.RoundedDrawable(bitmap, Math.max(width, height) / 2, 0));
    }

}
