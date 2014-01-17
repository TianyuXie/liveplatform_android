package com.pplive.liveplatform.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
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

    public static Bitmap getCircleBitmap(Drawable src) {
        if (src == null) {
            return null;
        }
        if (src instanceof BitmapDrawable) {
            return getCircleBitmap(((BitmapDrawable) src).getBitmap());
        } else {
            return null;
        }
    }

    public static Bitmap getCircleBitmap(Context context, int id) {
        return getCircleBitmap(BitmapFactory.decodeResource(context.getResources(), id));
    }

    public static Bitmap getScaledBitmap(Drawable src, float destSize) {
        if (src == null) {
            return null;
        }
        if (src instanceof BitmapDrawable) {
            return getScaledBitmap(((BitmapDrawable) src).getBitmap(), destSize);
        } else {
            return null;
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

    public static Bitmap getScaledBitmap(Context context, int id, float destSize) {
        return getScaledBitmap(BitmapFactory.decodeResource(context.getResources(), id), destSize);
    }

    public static boolean bitmap2File(Bitmap bitmap, String filename) {
        try {
            File f = new File(filename);
            FileOutputStream fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static Bitmap getCircleBitmap(Context context, int id, float size) {
        Bitmap tmp = getScaledBitmap(context, id, size);
        Bitmap result = getCircleBitmap(tmp);
        if (tmp != null) {
            tmp.recycle();
        }
        return result;
    }

    public static Bitmap getCircleBitmap(Drawable src, float size) {
        Bitmap tmp = getScaledBitmap(src, size);
        Bitmap result = getCircleBitmap(tmp);
        if (tmp != null) {
            tmp.recycle();
        }
        return result;
    }

    public static Bitmap getCircleBitmap(Bitmap src, float size) {
        Bitmap tmp = getScaledBitmap(src, size);
        Bitmap result = getCircleBitmap(tmp);
        if (tmp != null) {
            tmp.recycle();
        }
        return result;
    }

    public static Bitmap getBitmapFromRes(Context context, int resid) {
        return BitmapFactory.decodeResource(context.getResources(), resid);
    }

    public static Bitmap getBitmapFromAssets(Context context, String fileName) {
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(fileName);
            Bitmap image = BitmapFactory.decodeStream(is);
            is.close();
            return image;
        } catch (IOException e) {
            return null;
        }
    }

    public static Bitmap getBitmapFromUrl(String url, float width, float height) {
        byte[] buffer = null;
        try {
            InputStream inputStream = (InputStream) new java.net.URL(url).getContent();
            buffer = IOUtils.inputStream2Bytes(inputStream);
            inputStream.close();
        } catch (IOException e) {
            return null;
        }

        if (buffer != null && buffer.length > 0) {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(buffer, 0, buffer.length, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            int be = 1;
            if (w >= h && w > width) {
                be = (int) (w / width);
            } else if (w < h && h > height) {
                be = (int) (h / height);
            }
            if (be <= 0) {
                be = 1;
            }
            newOpts.inSampleSize = be;
            newOpts.inPreferredConfig = Config.RGB_565;
            newOpts.inPurgeable = true;
            newOpts.inInputShareable = true;
            return BitmapFactory.decodeByteArray(buffer, 0, buffer.length, newOpts);
        } else {
            return null;
        }
    }
}
