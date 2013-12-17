package com.pplive.liveplatform.ui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.passport.TencentPassport;
import com.pplive.liveplatform.core.service.passport.WeiboPassport;
import com.tencent.tauth.Constants;


public class ShareDialog extends Dialog {
    private String mtitle;
    private Button mWechart;
    private Button mWechartSNS;
    private Button mSinaShare;
    private Button mQQShare;
    private Activity mActivity;

    public ShareDialog(Context context) {
        super(context);
        mtitle = "";
    }

    public ShareDialog(Context context, int theme) {
        this(context, theme, "");
    }

    public ShareDialog(Context context, int theme, String title) {
        super(context, theme);
        mtitle = title;
    }
    
    public void setActivity(Activity activity){
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        Button closeBtn = (Button) findViewById(R.id.btn_share_dialog_close);
        TextView titleTextView = (TextView) findViewById(R.id.text_share_dialog_title);
        mWechart = (Button) findViewById(R.id.btn_sharedialog_wechatShare);
        mWechartSNS = (Button) findViewById(R.id.btn_sharedialog_wechatSNSShare);
        mSinaShare = (Button) findViewById(R.id.btn_sharedialog_sinaShare);
        mQQShare = (Button) findViewById(R.id.btn_sharedialog_qqShare);
        titleTextView.setText(mtitle);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mWechart.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "主题");
                intent.putExtra(Intent.EXTRA_TEXT, "分享内容");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(Intent.createChooser(intent, "liveplatform"));
            }
        });
        mWechartSNS.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
            }
        });
        mSinaShare.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sinaShare();
            }
        });
        mQQShare.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                qqShare();
            }
        });
    }
    
    public void qqShare() {
        TencentPassport.getInstance().init(getContext());
        TencentPassport.getInstance().setActivity(mActivity);
        TencentPassport.getInstance().doShareToQQ(doShareQQURL());
    }
    
    private Bundle doShareQQURL(){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PARAM_TARGET_URL, "http://connect.qq.com/");
        bundle.putString(Constants.PARAM_TITLE, "我在测试");
        bundle.putString(Constants.PARAM_IMAGE_URL, "http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg");
        bundle.putString(Constants.PARAM_SUMMARY, "测试");
        return bundle;
    }
    
    private Bundle doShareWeiboURL(){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PARAM_TARGET_URL, "http://open.weibo.com/");
        bundle.putString(Constants.PARAM_TITLE, "我在测试");
        
        bundle.putString(Constants.PARAM_SUMMARY, "测试");
        return bundle;
    }
    
    public void sinaShare() {
        WeiboPassport.getInstance().setActivity(mActivity);
        WeiboPassport.getInstance().init(mActivity);
        WeiboPassport.getInstance().initShare(mActivity);
        WeiboPassport.getInstance().shareToWeibo(doShareWeiboURL());

    }
    
    public void wechatSNSShare() {
        
        
    }
    
    public void wechatShare() {
        
        
    }

}
