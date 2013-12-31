package com.pplive.liveplatform.ui.widget.dialog;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.passport.WeChatShare;
import com.pplive.liveplatform.core.service.passport.thirdparty.TencentPassport;
import com.pplive.liveplatform.core.service.passport.thirdparty.ThirdpartyShareListener;
import com.pplive.liveplatform.core.service.passport.thirdparty.WeiboPassport;
import com.pplive.liveplatform.util.ImageUtil;
import com.pplive.liveplatform.util.StringUtil;
import com.pplive.liveplatform.util.SysUtil;

public class ShareDialog extends Dialog implements View.OnClickListener, ThirdpartyShareListener {
    static final String TAG = "_ShareDialog";

    private static final int MSG_THIRDPARTY_ERROR = 2401;

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
    private static final int MSG_SHARE_WECHAT = 7603;

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
                TencentPassport.getInstance().setShareListener(this);
                TencentPassport.getInstance().doShareToQQ(mActivity, getShareQQData());
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

    public void wechatShare() {
        if (SysUtil.checkPackage("com.tencent.mm", getContext())) {
            shareImage2(MSG_SHARE_WECHAT);
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
            //            wechatShareDirect();
            wechatShare();
            break;
        case R.id.btn_share_dialog_wechatSNS:
            wechatSNSShareDirect();
            break;
        default:
            break;
        }
    }

    private Handler mShareHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SHARE_WECHAT:
                WeChatShare.getInstance().sendToWeChat(mActivity, mSummary, mTargetUrl, mTitle, (Bitmap) msg.obj, WeChatShare.SHARE_WECHAT);
                break;
            }
        };
    };

    private Handler mShareHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                Context context = null;
                switch (msg.what) {
                case MSG_SHARE_WECHATSNS:
                    context = getContext().createPackageContext("com.tencent.mm", Context.CONTEXT_IGNORE_SECURITY);
                    intent.setClassName(context, "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                    break;
                case MSG_SHARE_SINA:
                    context = getContext().createPackageContext("com.sina.weibo", Context.CONTEXT_IGNORE_SECURITY);
                    intent.setClassName(context, "com.sina.weibo.EditActivity");
                    break;
                default:
                    return;
                }
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
                intent.putExtra(Intent.EXTRA_TEXT, String.format("%s: %s", mSummary, mTargetUrl));
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File((String) msg.obj)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), R.string.share_general_not_install, Toast.LENGTH_SHORT).show();
            } catch (NameNotFoundException e) {
                Toast.makeText(getContext(), R.string.share_general_not_install, Toast.LENGTH_SHORT).show();
            } finally {
                dismiss();
            }
        };
    };

    private void shareImage(final int target) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String tmpfile = String.format("%s/%s.png", SysUtil.getShareCachePath(getContext()), StringUtil.newGuid());
                try {
                    ImageUtil.bitmap2File(ImageUtil.loadImageFromUrl(mImageUrl), tmpfile);
                } catch (IOException e) {
                    ImageUtil.bitmap2File(ImageUtil.getBitmapFromRes(getContext(), R.drawable.ic_launcher), tmpfile);
                }
                Message message = new Message();
                message.what = target;
                message.obj = tmpfile;
                mShareHandler.sendMessage(message);
            }
        }).start();
    }

    private void shareImage2(final int target) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap cover = null;
                try {
                    cover = ImageUtil.loadImageFromUrl(mImageUrl);
                } catch (IOException e) {
                    return;
                }
                Message message = new Message();
                message.what = target;
                message.obj = cover;
                mShareHandler2.sendMessage(message);
            }
        }).start();
    }

    @Override
    public void shareSuccess() {
        dismiss();
    }

    @Override
    public void shareFailed(String message) {
        Message msg = new Message();
        msg.what = MSG_THIRDPARTY_ERROR;
        msg.obj = TextUtils.isEmpty(message) ? getContext().getString(R.string.share_failed) : message;
        mErrorHandler.sendMessage(msg);
        dismiss();
    }

    private Handler mErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_THIRDPARTY_ERROR:
                Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void shareCanceled() {
        dismiss();
    }
}
