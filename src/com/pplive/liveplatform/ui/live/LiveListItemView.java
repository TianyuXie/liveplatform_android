package com.pplive.liveplatform.ui.live;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pplive.android.image.AsyncImageView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.ui.live.event.EventProgramDeleted;
import com.pplive.liveplatform.util.TimeUtil;
import com.pplive.liveplatform.util.ViewUtil;

import de.greenrobot.event.EventBus;

public class LiveListItemView extends RelativeLayout {

    private static final String TAG = LiveListItemView.class.getSimpleName();

    private AsyncImageView mImagePrelive;
    private ImageButton mBtnPreliveDelete;
    private ImageButton mBtnDelete;
    private TextView mTextLiveTitle;
    private TextView mTextPreliveTime;
    private View mViewBtnDeleteMasking;

    private Program mProgram;

    public LiveListItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /* defStyle */);
    }

    public LiveListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImagePrelive = (AsyncImageView) findViewById(R.id.image_prelive);
        mBtnPreliveDelete = (ImageButton) findViewById(R.id.btn_prelive_delete);
        mBtnDelete = (ImageButton) findViewById(R.id.btn_delete);
        mTextLiveTitle = (TextView) findViewById(R.id.text_live_title);
        mTextPreliveTime = (TextView) findViewById(R.id.text_prelive_time);

        mViewBtnDeleteMasking = findViewById(R.id.view_btn_delete_masking);
        
        mBtnPreliveDelete.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClickBtnPreDelete");
                
                showOrHideDeleteBtn();
            }
        });
        
        mBtnDelete.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClickBtnDelete");
                
                EventBus.getDefault().post(new EventProgramDeleted(getProgram()));
            }
        });

    }

    public void setProgram(Program program) {
        mProgram = program;

        if (null != program) {
            mImagePrelive.setImageAsync(program.getRecommendCover(), R.drawable.live_record_default_prelive_image);
            mTextLiveTitle.setText(program.getTitle());
            
            Date date = new Date(program.getStartTime());
            SimpleDateFormat sdf;
            if (TimeUtil.isSameDay(System.currentTimeMillis(), program.getStartTime())) {
                sdf = new SimpleDateFormat("HH:mm", Locale.US);
            } else {
                sdf = new SimpleDateFormat("yyyy MM-dd", Locale.US);
            }
            
            Log.d(TAG, sdf.format(date));
            
            mTextPreliveTime.setText(sdf.format(date));
        }
    }

    public Program getProgram() {
        return mProgram;
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
        ViewUtil.showOrHide(mViewBtnDeleteMasking, false);
    }

    public void reset() {
        Log.d(TAG, "reset");

        mBtnPreliveDelete.setSelected(false);
        mBtnDelete.setVisibility(View.INVISIBLE);
        mViewBtnDeleteMasking.setVisibility(View.INVISIBLE);
    }
}
