package com.pplive.liveplatform.ui.widget.image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class CircularBitmapDisplayer implements BitmapDisplayer {

    public CircularBitmapDisplayer() {
        super();

    }

    @Override
    public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {

        int width = imageView.getWidth();
        int height = imageView.getHeight();

        Bitmap roundedBitmap = RoundedBitmapDisplayer.roundCorners(bitmap, imageView, Math.max(width, height) / 2);

        imageView.setImageBitmap(roundedBitmap);

        return roundedBitmap;
    }

}
