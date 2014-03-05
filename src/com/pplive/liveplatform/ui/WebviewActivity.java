package com.pplive.liveplatform.ui;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

import com.pplive.liveplatform.R;

public class WebviewActivity extends Activity {
    private static final long CACHE_SIZE = 10 * 1024 * 1024;

    protected WebView webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        setContentView(R.layout.activity_webview);

        webview = (WebView) findViewById(R.id.image_webview);
        webview.getSettings().setSupportZoom(false);
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.getSettings().setAppCacheMaxSize(CACHE_SIZE);
        webview.setWebChromeClient(new SimpleWebChromeClient());
        webview.setWebViewClient(new SimpleWebViewClient());
        Intent intent = getIntent();
        webview.loadUrl(intent.getStringExtra("url"));
    }

    protected class SimpleWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setProgress(newProgress * 100);
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            setTitle(title);
            super.onReceivedTitle(view, title);
        }
    }

    protected class SimpleWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    protected ZoomDensity getZoomDensity() {
        int screenDensity = getResources().getDisplayMetrics().densityDpi;
        ZoomDensity zoomDensity;
        switch (screenDensity) {
        case DisplayMetrics.DENSITY_LOW:
            zoomDensity = ZoomDensity.CLOSE;
            break;
        case DisplayMetrics.DENSITY_MEDIUM:
            zoomDensity = ZoomDensity.MEDIUM;
            break;
        case DisplayMetrics.DENSITY_HIGH:
            zoomDensity = ZoomDensity.FAR;
            break;
        case DisplayMetrics.DENSITY_XHIGH:
            zoomDensity = ZoomDensity.FAR;
            break;
        default:
            zoomDensity = ZoomDensity.MEDIUM;
            break;
        }
        return zoomDensity;
    }

    protected void setZoomControlGone(View view) {
        try {
            Class<WebView> classType = WebView.class;
            Field field = classType.getDeclaredField("mZoomButtonsController");
            field.setAccessible(true);
            ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);
            mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
            field.set(view, mZoomButtonsController);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
