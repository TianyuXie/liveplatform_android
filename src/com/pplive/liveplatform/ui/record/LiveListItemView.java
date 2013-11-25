package com.pplive.liveplatform.ui.record;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.model.Program;
import com.pplive.liveplatform.ui.widget.AsyncImageView;
import com.pplive.liveplatform.util.ViewUtil;

public class LiveListItemView extends RelativeLayout {

    private static final String TAG = LiveListItemView.class.getSimpleName();

    private static final DisplayImageOptions DEFAULT_PRELIVE_DISPLAY_OPTIONS = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
            .showImageOnFail(R.drawable.live_record_default_prelive_image).showImageForEmptyUri(R.drawable.live_record_default_prelive_image)
            .showStubImage(R.drawable.live_record_default_prelive_image).build();

    private AsyncImageView mImagePrelive;
    private ImageButton mBtnPreliveDelete;
    private ImageButton mBtnDelete;
    private TextView mTextLiveTitle;

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

    public void setProgram(Program program) {
        mProgram = program;

        if (null != program) {
            mImagePrelive.setImageAsync(program.getCoverUrl(), DEFAULT_PRELIVE_DISPLAY_OPTIONS);
            mTextLiveTitle.setText(program.getTitle());
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
    }

    public void reset() {
        mBtnPreliveDelete.setSelected(false);
        mBtnPreliveDelete.setVisibility(View.INVISIBLE);
        mBtnDelete.setVisibility(View.INVISIBLE);
    }
}
