package com.pplive.liveplatform.ui.homepage.program;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.homepage.IAnswerItemView;
import com.pplive.liveplatform.vo.program.Program;

public class ProgramItemView implements IAnswerItemView, View.OnClickListener {
    static final String TAG = "ProgramItemView";

    private Program mProgram;

    private ImageView mPreviewImageView;

    private TextView mStatusTextView;

    private TextView mTimedownTextView;

    private TextView mTitleTextView;

    private TextView mOwnerTextView;

    private TextView mViewcountTextView;

    @Override
    public View getView(Context context) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_program_itemview, null);
        mPreviewImageView = (ImageView) layout.findViewById(R.id.imageview_program_preview);
        mStatusTextView = (TextView) layout.findViewById(R.id.textview_program_status);
        mTimedownTextView = (TextView) layout.findViewById(R.id.textview_program_timedown);
        mTitleTextView = (TextView) layout.findViewById(R.id.textview_program_title);
        mOwnerTextView = (TextView) layout.findViewById(R.id.textview_program_owner);
        mViewcountTextView = (TextView) layout.findViewById(R.id.textview_program_viewcount);
        layout.setOnClickListener(this);
        return layout;
    }

    @Override
    public void setData(Object data) {
        mProgram = (Program) data;
        Log.d(TAG, data.toString());
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

}
