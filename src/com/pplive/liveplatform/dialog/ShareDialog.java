package com.pplive.liveplatform.dialog;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.passport.thirdparty.TencentPassport;
import com.pplive.liveplatform.core.api.passport.thirdparty.ThirdpartyShareListener;
import com.pplive.liveplatform.core.api.passport.thirdparty.WeChatShare;
import com.pplive.liveplatform.core.api.passport.thirdparty.WeiboPassport;
import com.pplive.liveplatform.task.Task;
import com.pplive.liveplatform.task.TaskCancelEvent;
import com.pplive.liveplatform.task.TaskContext;
import com.pplive.liveplatform.task.TaskFailedEvent;
import com.pplive.liveplatform.task.TaskProgressChangedEvent;
import com.pplive.liveplatform.task.TaskSucceedEvent;
import com.pplive.liveplatform.task.TaskTimeoutEvent;
import com.pplive.liveplatform.task.share.LoadImageTask;
import com.pplive.liveplatform.util.DirManager;
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

    private Dialog mRefreshDialog;

    public static final String PARAM_TARGET_URL = "targetUrl";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_SUMMARY = "summary";
    public static final String PARAM_IMAGE_URL = "imageUrl";
    public static final String PARAM_BITMAP = "bitmap";

    private static final int MSG_SHARE_SINA_DIRECT = 7602;
    private static final int MSG_SHARE_WECHAT = 7603;
    private static final int MSG_SHARE_WECHATSNS = 7604;
    private static final int MSG_SHARE_WECHATSNS_DIRECT = 7605;

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
        mRefreshDialog = new RefreshDialog(context);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        ((TextView) findViewById(R.id.text_share_dialog_title)).setText(mDialogTitle);
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
        bundle.putParcelable(WeiboPassport.PARAM_BITMAP, ImageUtil.getBitmapFromAssets(getContext(), "default_share.png"));
        return bundle;
    }

    public void sinaShare() {
        if (mActivity != null) {
            WeiboPassport.getInstance().initShare(mActivity);
            WeiboPassport.getInstance().shareToWeibo(mActivity, getShareSinaData());
            dismiss();
        }
    }

    public void qqShare() {
        if (SysUtil.checkPackage("com.tencent.mobileqq", getContext())) {
            if (mActivity != null) {
                mRefreshDialog.show();
                TencentPassport.getInstance().init(mActivity);
                TencentPassport.getInstance().setShareListener(qqShareListener);
                TencentPassport.getInstance().doShareToQQ(mActivity, getShareQQData());
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

    public void wechatShare(int target) {
        if (SysUtil.checkPackage("com.tencent.mm", getContext())) {
            shareImage(target);
        } else {
            Toast.makeText(getContext(), R.string.share_wechat_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    public void sinaShareDirect() {
        if (SysUtil.checkPackage("com.sina.weibo", getContext())) {
            shareImage(MSG_SHARE_SINA_DIRECT);
        } else {
            Toast.makeText(getContext(), R.string.share_weibo_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    private void sinaShareDirect(Bitmap bitmap) {
        try {
            String tmpfile = String.format("%s/%s.png", DirManager.getShareCachePath(), StringUtil.newGuid());
            ImageUtil.bitmap2File(bitmap, tmpfile);
            Intent intent = new Intent(Intent.ACTION_SEND);
            Context context = getContext().createPackageContext("com.sina.weibo", Context.CONTEXT_IGNORE_SECURITY);
            intent.setClassName(context, "com.sina.weibo.EditActivity");
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
            intent.putExtra(Intent.EXTRA_TEXT, String.format("%s: %s", mSummary, mTargetUrl));
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(tmpfile)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            mRefreshDialog.dismiss();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.share_general_not_install, Toast.LENGTH_SHORT).show();
        } catch (NameNotFoundException e) {
            Toast.makeText(getContext(), R.string.share_general_not_install, Toast.LENGTH_SHORT).show();
        } finally {
            dismiss();
        }
    }

    private void wechatSNSShareDirect(Bitmap bitmap) {
        try {
            String tmpfile = String.format("%s/%s.png", DirManager.getShareCachePath(), StringUtil.newGuid());
            Intent intent = new Intent(Intent.ACTION_SEND);
            Context context = getContext().createPackageContext("com.tencent.mm", Context.CONTEXT_IGNORE_SECURITY);
            intent.setClassName(context, "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
            intent.putExtra(Intent.EXTRA_TEXT, String.format("%s: %s", mSummary, mTargetUrl));
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(tmpfile)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.share_general_not_install, Toast.LENGTH_SHORT).show();
        } catch (NameNotFoundException e) {
            Toast.makeText(getContext(), R.string.share_general_not_install, Toast.LENGTH_SHORT).show();
        } finally {
            dismiss();
        }
    }

    private void wechatShare(Bitmap bitmap) {
        boolean result = WeChatShare.getInstance().sendToWeChat(mActivity, mSummary, mTargetUrl, mTitle, bitmap, WeChatShare.SHARE_WECHAT);
        if (!result) {
            Toast.makeText(getContext(), R.string.share_failed, Toast.LENGTH_SHORT).show();
        }
        dismiss();
        mRefreshDialog.dismiss();
    }

    private void wechatSNSShare(Bitmap bitmap) {
        boolean result = WeChatShare.getInstance().sendToWeChat(mActivity, mSummary, mTargetUrl, mTitle, bitmap, WeChatShare.SHARE_SNS);
        if (!result) {
            Toast.makeText(getContext(), R.string.share_failed, Toast.LENGTH_SHORT).show();
        }
        dismiss();
        mRefreshDialog.dismiss();
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
        case R.id.btn_share_dialog_qq:
            qqShare();
            break;
        case R.id.btn_share_dialog_sina:
            sinaShareDirect();
            break;
        case R.id.btn_share_dialog_wechat:
            wechatShare(MSG_SHARE_WECHAT);
            break;
        case R.id.btn_share_dialog_wechatSNS:
            wechatShare(MSG_SHARE_WECHATSNS);
            break;
        }
    }

    private void shareImage(final int target) {
        mRefreshDialog.show();
        LoadImageTask task = new LoadImageTask();
        task.setTimeout(10000);
        TaskContext taskContext = new TaskContext();
        taskContext.set(LoadImageTask.KEY_URL, mImageUrl);
        taskContext.set(LoadImageTask.KEY_TARGET, target);
        task.setReturnContext(taskContext);
        task.execute(taskContext);
        task.addTaskListener(taskListener);
    }

    private void shareImage(int target, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        switch (target) {
        case MSG_SHARE_SINA_DIRECT:
            sinaShareDirect(bitmap);
            break;
        case MSG_SHARE_WECHATSNS_DIRECT:
            wechatSNSShareDirect(bitmap);
            break;
        case MSG_SHARE_WECHAT:
            wechatShare(bitmap);
            break;
        case MSG_SHARE_WECHATSNS:
            wechatSNSShare(bitmap);
            break;
        }
        bitmap.recycle();
    }

    private Task.TaskListener taskListener = new Task.BaseTaskListener() {

        @Override
        public void onTimeout(Task sender, TaskTimeoutEvent event) {
            int target = (Integer) event.getContext().get(LoadImageTask.KEY_TARGET);
            Bitmap bitmap = ImageUtil.getBitmapFromAssets(getContext(), "default_share.png");
            shareImage(target, bitmap);
        }

        @Override
        public void onTaskSucceed(Task sender, TaskSucceedEvent event) {
            int target = (Integer) event.getContext().get(LoadImageTask.KEY_TARGET);
            Bitmap bitmap = (Bitmap) event.getContext().get(LoadImageTask.KEY_RESULT);
            shareImage(target, bitmap);
        }

        @Override
        public void onTaskFailed(Task sender, TaskFailedEvent event) {
            int target = (Integer) event.getContext().get(LoadImageTask.KEY_TARGET);
            Bitmap bitmap = ImageUtil.getBitmapFromAssets(getContext(), "default_share.png");
            shareImage(target, bitmap);
        }

        @Override
        public void onTaskCancel(Task sender, TaskCancelEvent event) {
            dismiss();
            mRefreshDialog.dismiss();
        }

        @Override
        public void onProgressChanged(Task sender, TaskProgressChangedEvent event) {
        }
    };

    private ThirdpartyShareListener qqShareListener = new ThirdpartyShareListener() {

        @Override
        public void shareSuccess() {
            dismiss();
            mRefreshDialog.dismiss();
        }

        @Override
        public void shareFailed(final String message) {
            new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }).sendEmptyMessage(0);
            dismiss();
            mRefreshDialog.dismiss();
        }

        @Override
        public void shareCanceled() {
            dismiss();
            mRefreshDialog.dismiss();
        }
    };
}
