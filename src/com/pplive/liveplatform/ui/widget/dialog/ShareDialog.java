package com.pplive.liveplatform.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.passport.TencentPassport;
import com.pplive.liveplatform.core.service.passport.WeiboPassport;
import com.pplive.liveplatform.util.StringUtil;
import com.tencent.tauth.Constants;

public class ShareDialog extends Dialog implements View.OnClickListener {
    static final String TAG = "_ShareDialog";

    private String mDialogTitle;

    private Activity mActivity;

    private String mTargetUrl;
    private String mTitle;
    private String mImageUrl;
    private String mSummary;

    public static final String PARAM_TARGET_URL = "targetUrl";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_SUMMARY = "summary";
    public static final String PARAM_IMAGE_URL = "imageUrl";
    public static final String PARAM_BITMAP = "bitmap";

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
        bundle.putString(Constants.PARAM_TARGET_URL, mTargetUrl);
        bundle.putString(Constants.PARAM_TITLE, mTitle);
        bundle.putString(Constants.PARAM_IMAGE_URL, mImageUrl);
        bundle.putString(Constants.PARAM_SUMMARY, mSummary);
        return bundle;
    }

    private Bundle getShareSinaData() {
        Bundle bundle = new Bundle();
        bundle.putString(WeiboPassport.PARAM_TARGET_URL, mTargetUrl);
        bundle.putString(WeiboPassport.PARAM_TITLE, mTitle);
        bundle.putString(WeiboPassport.PARAM_SUMMARY, mSummary);
        return bundle;
    }

    public void sinaShare() {
        WeiboPassport.getInstance().setActivity(mActivity);
        WeiboPassport.getInstance().init(mActivity);
        WeiboPassport.getInstance().initShare(mActivity);
        WeiboPassport.getInstance().shareToWeibo(getShareSinaData());
    }

    public void qqShare() {
        TencentPassport.getInstance().init(getContext());
        TencentPassport.getInstance().setActivity(mActivity);
        TencentPassport.getInstance().doShareToQQ(getShareQQData());
    }

    public void wechatSNSShare() {
        try {
            //TODO add image
            Intent intent = new Intent(Intent.ACTION_SEND);
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
            intent.putExtra(Intent.EXTRA_TEXT, String.format("%s: %s", mSummary, mTargetUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.share_wechat_not_install, Toast.LENGTH_SHORT).show();
        }
    }

    public void wechatShare() {
        try {
            //TODO add image
            Intent intent = new Intent(Intent.ACTION_SEND);
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(comp);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
            intent.putExtra(Intent.EXTRA_TEXT, String.format("%s: %s", mSummary, mTargetUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), R.string.share_wechat_not_install, Toast.LENGTH_SHORT).show();
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
            sinaShare();
            break;
        case R.id.btn_share_dialog_wechat:
            wechatShare();
            break;
        case R.id.btn_share_dialog_wechatSNS:
            wechatSNSShare();
            break;
        default:
            break;
        }
    }
}
