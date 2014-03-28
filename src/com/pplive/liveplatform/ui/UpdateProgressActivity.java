package com.pplive.liveplatform.ui;

import java.lang.reflect.Field;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.update.DownManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateProgressActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(this.getString(R.string.update_title));
        dialog.setIndeterminate(false);
        dialog.setMessage(this.getString(R.string.update_ing));
        dialog.setMax(100);
        dialog.setProgress(0);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // dialog.setCancelable(false);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                finish();
            }
        });

        // 隐藏数字
        try {
            Class<ProgressDialog> clazz = ProgressDialog.class;
            Field mProgressNumber = clazz.getDeclaredField("mProgressNumber");
            mProgressNumber.setAccessible(true);
            TextView textView = (TextView) mProgressNumber.get(dialog);
            if (textView != null) {
                textView.setVisibility(View.INVISIBLE);
            }
        } catch (SecurityException e) {

        } catch (NoSuchFieldException e) {

        } catch (IllegalArgumentException e) {

        } catch (IllegalAccessException e) {

        }

        new Thread() {
            @Override
            public void run() {

                while (true && !isFinishing() && DownManager.beginUpload) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (DownManager.len > 0 && DownManager.cLen > 0) {
                                dialog.setProgress(((int) (1f * DownManager.cLen / DownManager.len * 10000)) / 100);
                            }
                        }
                    });

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {

                    }
                }

                if (DownManager.error && !isFinishing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UpdateProgressActivity.this, R.string.update_download_error, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                dialog.dismiss();
                finish();

            }
        }.start();
    }

    class ProgressView extends View {
        private final Paint mPaint;

        private final int w;

        private final int h;

        public ProgressView(Context cx) {
            super(cx);
            mPaint = new Paint();
            mPaint.setColor(Color.BLUE);
            mPaint.setTextSize(16);
            DisplayMetrics dm = cx.getApplicationContext().getResources().getDisplayMetrics();
            w = dm.widthPixels;
            h = dm.heightPixels;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.WHITE);
            Paint p0 = new Paint();
            p0.setColor(Color.GREEN);
            canvas.drawRect(10, h / 2 - 10, w - 10, h / 2 + 10, p0);
            Paint p1 = new Paint();
            p1.setColor(Color.YELLOW);
            canvas.drawRect(10, h / 2 - 10, (w - 20) * (1f * DownManager.cLen / DownManager.len) + 10, h / 2 + 10, p1);
            canvas.drawText(DownManager.cLen + "/" + DownManager.len + " " + ((int) (1f * DownManager.cLen / DownManager.len * 10000)) / 100f + "%",
                    w / 2 - 50, h / 2 - 8, mPaint);
            invalidate();
        }

    }
}
