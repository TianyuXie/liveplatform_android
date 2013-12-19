package com.pplive.liveplatform.ui.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.ui.LivePlayerActivity;
import com.pplive.liveplatform.ui.widget.RefreshGridView;

public class ProgramContainer extends RelativeLayout {
    static final String TAG = "_ProgramsContainer";

    private List<Program> mPrograms;
    private HomeProgramAdapter mAdapter;
    private RefreshGridView mGridView;
    private RadioGroup mRadioGroup;

    private boolean mItemClickable;

    public ProgramContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPrograms = new ArrayList<Program>();
        mAdapter = new HomeProgramAdapter(context, mPrograms);
        mItemClickable = true;

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_home_container, this);
        mRadioGroup = (RadioGroup) root.findViewById(R.id.layout_grid_header);
        mGridView = (RefreshGridView) root.findViewById(R.id.grid_home_results);
        LinearLayout pullHeader = (LinearLayout) root.findViewById(R.id.layout_pull_header);
        pullHeader.addView(mGridView.getPullView(), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        mGridView.setAdapter(mAdapter);
        mGridView.setHeader(mRadioGroup);
        mGridView.setOnItemClickListener(onItemClickListener);
    }

    public ProgramContainer(Context context) {
        this(context, null);
    }

    public void refreshData(List<Program> data) {
        mPrograms.clear();
        mPrograms.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    public void appendData(List<Program> data) {
        mPrograms.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mGridView.canClick() && mItemClickable) {
                Program program = mPrograms.get(position);
                if (program != null){
                    Intent intent = new Intent();
                    intent.putExtra(LivePlayerActivity.EXTRA_PROGRAM, program);
                    intent.setClass(getContext(), LivePlayerActivity.class);
                    getContext().startActivity(intent);
                }
            }
        }
    };

    public void setItemClickable(boolean clickable) {
        this.mItemClickable = clickable;
    }

    public void onRefreshComplete() {
        mGridView.onRefreshComplete();
    }

    public void setOnUpdateListener(RefreshGridView.OnUpdateListener l) {
        mGridView.setOnUpdateListener(l);
    }

    public void setOnStatusChangeListener(RadioGroup.OnCheckedChangeListener l) {
        mRadioGroup.setOnCheckedChangeListener(l);
    }

}
