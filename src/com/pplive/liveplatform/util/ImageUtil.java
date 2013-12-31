package com.pplive.liveplatform.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtil {
    private static Paint sCirclePaint;

    private static Paint sLinePaint;

    private static final int STROKE_WIDTH = 4;

    private static Paint getCirclePaint() {
        if (sCirclePaint == null) {
            sCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        return sCirclePaint;
    }

    private static Paint getLinePaint() {
        if (sLinePaint == null) {
            sLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            sLinePaint.setStyle(Style.STROKE);
            sLinePaint.setStrokeWidth(STROKE_WIDTH);
            sLinePaint.setColor(Color.WHITE);
            sLinePaint.setAntiAlias(true);
        }
        return sLinePaint;
    }

    public static Bitmap getCircleBitmap(Bitmap src) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(centerX, centerY);
        Bitmap result = Bitmap.createBitmap(radius * 2, radius * 2, Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawCircle(radius, radius, radius, getCirclePaint());
        getCirclePaint().setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(src, -(centerX - radius), -(centerY - radius), getCirclePaint());
        getCirclePaint().setXfermode(null);
        canvas.drawCircle(radius, radius, radius - STROKE_WIDTH / 2, getLinePaint());
        return result;
    }

    public static Bitmap getScaledBitmap(Drawable src, float destSize) {
        if (src == null) {
            return null;
        }
        if (src instanceof BitmapDrawable) {
            return getScaledBitmap(((BitmapDrawable) src).getBitmap(), destSize);
        } else {
            throw new UnsupportedException("Unsupported");
        }
    }

    public static Bitmap getScaledBitmap(Bitmap src, float destSize) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        float scaleWidth = ((float) destSize) / width;
        float scaleHeight = ((float) destSize) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
    }

    public static Bitmap getCircleBitmap(Drawable src) {
        if (src instanceof BitmapDrawable) {
            return getCircleBitmap(((BitmapDrawable) src).getBitmap());
        } else {
            throw new UnsupportedException("Unsupported");
        }
    }

    public static Bitmap getCircleBitmap(Resources res, int id) {
        return getCircleBitmap(BitmapFactory.decodeResource(res, id));
    }

    public static Drawable getCircleDrawable(Resources res, Bitmap src) {
        return new BitmapDrawable(res, getCircleBitmap(src));
    }

    public static Drawable getCircleDrawable(Resources res, Drawable src) {
        return new BitmapDrawable(res, getCircleBitmap(src));
    }

    public static Drawable getCircleDrawable(Resources res, int id) {
        return new BitmapDrawable(res, getCircleBitmap(res, id));
    }

    public static Bitmap loadImageFromUrl(String url) throws IOException {
        InputStream inputStream = (InputStream) new java.net.URL(url).getContent();
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容  
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 30f;
        float ww = 40f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率  

        newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设  
        newOpts.inPurgeable = true;// 同时设置才会有效  
        newOpts.inInputShareable = true;//当系统内存不够时候图片自动被回收  

        inputStream = (InputStream) new java.net.URL(url).getContent();
        bitmap = BitmapFactory.decodeStream(inputStream, null, newOpts);
        return bitmap;
    }

    public static void bitmap2File(Bitmap bitmap, String filename) {
        try {
            File f = new File(filename);
            FileOutputStream fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromRes(Context context, int resid) {
        return ((BitmapDrawable) context.getApplicationContext().getResources().getDrawable(resid)).getBitmap();
    }

    public static Bitmap getBitmapFromAssets(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    static class UnsupportedException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public UnsupportedException(String str) {
            super(str);
        }
    }
}
