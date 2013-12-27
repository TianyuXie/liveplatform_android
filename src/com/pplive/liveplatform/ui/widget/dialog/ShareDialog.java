package com.pplive.liveplatform.ui.widget.dialog;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.passport.TencentPassport;
import com.pplive.liveplatform.core.service.passport.WeiboPassport;
import com.pplive.liveplatform.util.ImageUtil;
import com.pplive.liveplatform.util.StringUtil;
import com.pplive.liveplatform.util.SysUtil;

public class ShareDialog extends Dialog implements View.OnClickListener {
    static final String TAG = "_ShareDialog";

    private Activity mActivity;

    private String mDialogTitle;
    private String mTargetUrl;
    private String mTitle;
    private String mImageUrl;
    private String mSummary;

    public static final String PARAM_TARGET_URL = "targetUrl";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_SUMMARY = "summary";
    public static final String PARAM_IMAGE_URL = "imageUrl";
    public static final String PARAM_BITMAP = "bitmap";

    private static final int MSG_SHARE_WECHATSNS = 7601;
    private static final int MSG_SHARE_SINA = 7602;

    public ShareDialog(Context context, int theme) {
        this(context, theme, "");
    }

    public ShareDialog(Context context, int theme, String title) {
        super(context, theme);
        mDialogTitle = title;
        mTargetUrl = "";
        mTitle = "";
        mImageUrl = "";
        mSummary = "";
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        ((TextView) findViewById(R.id.text_share_dialog_title)).setText(mDialogTitle);
        findViewById(R.id.btn_share_dialog_close).setOnClickListener(this);
        findViewById(R.id.btn_share_dialog_sina).setOnClickListener(this);
        findViewById(R.id.btn_share_dialog_wechat).setOnClickListener(this);
        findViewById(R.id.btn_share_dialog_wechatSNS).setOnClickListener(this);
        findViewById(R.id.btn_share_dialog_qq).setOnClickListener(this);
    }

    private Bundle getShareQQData() {
        Bundle bundle = new Bundle();
        bundle.putString(com.tencent.tauth.Constants.PARAM_TARGET_URL, mTargetUrl);
        bundle.putString(com.tencent.tauth.Constants.PARAM_TITLE, mTitle);
        bundle.putString(com.tencent.tauth.Constants.PARAM_IMAGE_URL, mImageUrl);
        bundle.putString(com.tencent.tauth.Constants.PARAM_SUMMARY, mSummary);
        return bundle;
    }

    private Bundle getShareSinaData() {
        Bundle bundle = new Bundle();
        bundle.putString(WeiboPassport.PARAM_TARGET_URL, mTargetUrl);
        bundle.putString(WeiboPassport.PARAM_TITLE, mTitle);
        bundle.putString(WeiboPassport.PARAM_SUMMARY, mSummary);
        bundle.putParcelable(WeiboPassport.PARAM_BITMAP, ((BitmapDrawable) (getContext().getResources().getDrawable(R.drawable.ic_launcher))).getBitmap());
        return bundle;
    }

    public void sinaShare() {
        if (mActivity != null) {
            WeiboPassport.getInstance().initShare(mActivity);
            WeiboPassport.getInstance().shareToWeibo(mActivity, getShareSinaData());
            dismiss();
        } else {
            Log.e(TAG, "mActivity == null");
        }
    }

    public void qqShare() {
        if (SysUtil.checkPackage("com.tencent.mobileqq", getContext())) {
            if (mActivity != null) {
                TencentPassport.getInstance().init(mActivity);
                TencentPassport.getInstance().doShareToQQ(mActivity, getShareQQData());
                dismiss();
            } else {
                Log.e(TAG, "mActivity == null");
            }
        } else {
            Toast.makeText(getContext(), R.string.share_qq_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    public void wechatSNSShareDirect() {
        if (SysUtil.checkPackage("com.tencent.mm", getContext())) {
            shareImage(MSG_SHARE_WECHATSNS);
        } else {
            Toast.makeText(getContext(), R.string.share_wechat_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    public void wechatShareDirect() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(comp);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, String.format("%s: %s", mSummary, mTargetUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            dismiss();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.share_wechat_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    public void sinaShareDirect() {
        if (SysUtil.checkPackage("com.sina.weibo", getContext())) {
            shareImage(MSG_SHARE_SINA);
        } else {
            Toast.makeText(getContext(), R.string.share_weibo_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    public void setData(Bundle data) {
        mTitle = StringUtil.safeString(data.getString(PARAM_TITLE));
        mTargetUrl = StringUtil.safeString(data.getString(PARAM_TARGET_URL));
        mSummary = StringUtil.safeString(data.getString(PARAM_SUMMARY));
        mImageUrl = StringUtil.safeString(data.getString(PARAM_IMAGE_URL));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_share_dialog_close:
            dismiss();
            break;
        case R.id.btn_share_dialog_qq:
            qqShare();
            break;
        case R.id.btn_share_dialog_sina:
            sinaShareDirect();
            break;
        case R.id.btn_share_dialog_wechat:
            wechatShareDirect();
            break;
        case R.id.btn_share_dialog_wechatSNS:
            wechatSNSShareDirect();
            break;
        default:
            break;
        }
    }

    private Handler mShareHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            ComponentName comp = null;
            switch (msg.what) {
            case MSG_SHARE_WECHATSNS:
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                break;
            case MSG_SHARE_SINA:
                comp = new ComponentName("com.sina.weibo", "com.sina.weibo.EditActivity");
                break;
            default:
                return;
            }
            try {
                intent.setComponent(comp);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
                intent.putExtra(Intent.EXTRA_TEXT, String.format("%s: %s", mSummary, mTargetUrl));
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File((String) msg.obj)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            dismiss();
        };
    };

    private void shareImage(final int target) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String tmpfile = String.format("%s/%s.png", SysUtil.getShareCachePath(getContext().getApplicationContext()), StringUtil.newGuid());
                try {
                    ImageUtil.bitmap2File(ImageUtil.loadImageFromUrl(mImageUrl), tmpfile);
                } catch (IOException e) {
                    return;
                }
                Message message = new Message();
                message.what = target;
                message.obj = tmpfile;
                mShareHandler.sendMessage(message);
            }
        }).start();
    }
}
