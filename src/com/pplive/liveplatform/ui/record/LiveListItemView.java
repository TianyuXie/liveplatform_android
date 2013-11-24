package com.pplive.liveplatform.ui.record;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.util.ViewUtil;

public class LiveListItemView extends RelativeLayout {
    
    private static final String TAG = LiveListItemView.class.getSimpleName();
    
    private ImageView mImagePrelive;
    private ImageButton mBtnPreliveDelete;
    private ImageButton mBtnDelete;
    private TextView mTextLiveTitle;

    public LiveListItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /* defStyle */);
    }

    public LiveListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        mImagePrelive = (ImageView) findViewById(R.id.image_prelive);
        mBtnPreliveDelete = (ImageButton) findViewById(R.id.btn_prelive_delete);
        mBtnDelete = (ImageButton) findViewById(R.id.btn_delete);
        mTextLiveTitle = (TextView) findViewById(R.id.text_live_title);
        
        mBtnPreliveDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                showOrHideDeleteBtn();
            }
        });
        
        mBtnDelete.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClickBtnDelete");
            }
        });
    }
    
    @Override
    public void setSelected(boolean selected) {
        if (selected) {
            mBtnPreliveDelete.setVisibility(View.VISIBLE);
        } else {
            reset();
        }
    }

    public void showOrHideDeleteBtn() {
        boolean selected = mBtnPreliveDelete.isSelected();

        mBtnPreliveDelete.setSelected(!selected);

        ViewUtil.showOrHide(mBtnDelete, false);
    }

    public void reset() {
        mBtnPreliveDelete.setSelected(false);
        mBtnPreliveDelete.setVisibility(View.INVISIBLE);
        mBtnDelete.setVisibility(View.INVISIBLE);
    }
}
