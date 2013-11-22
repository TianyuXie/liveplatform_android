package com.pplive.liveplatform.ui.record;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.DateTimePicker;
import com.pplive.liveplatform.ui.widget.DateTimePicker.OnDateTimeChangedListener;
import com.pplive.liveplatform.ui.widget.HorizontalListView;
import com.pplive.liveplatform.util.ViewUtil;

public class FooterBarFragment extends Fragment implements OnClickListener, OnTouchListener, OnFocusChangeListener, OnDateTimeChangedListener {

    private static final String TAG = FooterBarFragment.class.getSimpleName();

    private Activity mAttachedActivity;
    
    private ImageButton mBtnLiveHome;
    private ImageButton mBtnLiveBack;

    private EditText mEditLiveSchedule;
    private EditText mEditLiveTitle;

    private ImageButton mBtnLiveShare;
    private ImageButton mBtnLivePrelive;

    private ImageButton mBtnLiveComplete;
    private ImageButton mBtnAddPrelive;

    private DateTimePicker mDateTimePicker;
    private HorizontalListView mLiveListView;
    
    private Mode mStatus = Mode.HOME;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
        
        mAttachedActivity = activity;
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        
        mAttachedActivity = null;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.layout_footerbar_fragment, container, false);

        mBtnLiveHome = (ImageButton) layout.findViewById(R.id.btn_live_home);
        mBtnLiveBack = (ImageButton) layout.findViewById(R.id.btn_live_back);

        mEditLiveSchedule = (EditText) layout.findViewById(R.id.edit_live_schedule);
        mEditLiveTitle = (EditText) layout.findViewById(R.id.edit_live_title);

        mBtnLiveShare = (ImageButton) layout.findViewById(R.id.btn_live_share);
        mBtnLivePrelive = (ImageButton) layout.findViewById(R.id.btn_live_prelive);

        mBtnLiveComplete = (ImageButton) layout.findViewById(R.id.btn_live_complete);
        mBtnAddPrelive = (ImageButton) layout.findViewById(R.id.btn_add_prelive);

        mDateTimePicker = (DateTimePicker) layout.findViewById(R.id.datetime_picker);

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DATE, 7);
        mDateTimePicker.setMaxDate(maxDate.getTimeInMillis());

        Calendar minDate = Calendar.getInstance();
        mDateTimePicker.setMinDate(minDate.getTimeInMillis() - 1000);

        mLiveListView = (HorizontalListView) layout.findViewById(R.id.live_listview);

        mBtnLivePrelive.setOnClickListener(this);
        mBtnLiveHome.setOnClickListener(this);
        mBtnLiveBack.setOnClickListener(this);
        mBtnAddPrelive.setOnClickListener(this);
        mBtnLiveComplete.setOnClickListener(this);

        mEditLiveSchedule.setOnTouchListener(this);
        mEditLiveSchedule.setOnFocusChangeListener(this);
        mEditLiveTitle.setOnFocusChangeListener(this);

        mDateTimePicker.setOnDateTimeChanged(this);

        setStatus(Mode.HOME);

        return layout;
    }

    @Override
    public void onDateTimeChanged(int year, int month, int day, int hour, int minute) {
        mEditLiveSchedule.setText(String.format("%d/%d/%d %d:%d", year, month, day, hour, minute));
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
        case R.id.btn_live_prelive:
            onClickBtnLivePrelive(v);
            break;
        case R.id.btn_live_home:
            onClickBtnLiveHome(v);
            break;
        case R.id.btn_live_back:
            onClickBtnLiveBack(v);
            break;
        case R.id.btn_add_prelive:
            onClickBtnAddPrelive(v);
            break;
        case R.id.btn_live_complete:
            onClickBtnLiveComplete(v);
            break;
        default:
            break;
        }
    }

    private void onClickBtnLivePrelive(View v) {
        setStatus(Mode.VIEW);

        mEditLiveSchedule.requestFocus();
    }
    
    private void onClickBtnLiveHome(View v) {
        
        if (null != mAttachedActivity) {
            mAttachedActivity.finish();
        }
    }

    private void onClickBtnLiveBack(View v) {
        setStatus(Mode.HOME);
    }
    
    private void onClickBtnAddPrelive(View v) {
        setStatus(Mode.EDIT);
    }

    private void onClickBtnLiveComplete(View v) {
        setStatus(Mode.VIEW);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.requestFocus();

        switch (v.getId()) {
        case R.id.edit_live_schedule:
            return onTouchEditLiveShedule(v, event);
        default:
            break;
        }

        return false;
    }

    private boolean onTouchEditLiveShedule(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: " + (event.getAction() & MotionEvent.ACTION_MASK));

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (MotionEvent.ACTION_UP != action) {
            return true;
        }

        ViewUtil.showOrHide(mDateTimePicker);

        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        int color;
        switch (v.getId()) {
        case R.id.edit_live_schedule:
            color = getResources().getColor(hasFocus ? R.color.edit_focused : R.color.edit_normal);
            mEditLiveSchedule.setTextColor(color);
            break;
        case R.id.edit_live_title:
            color = getResources().getColor(hasFocus ? R.color.edit_focused : R.color.edit_normal);
            mEditLiveTitle.setTextColor(color);
            break;
        default:
            break;
        }
    }

    public HorizontalListView getLiveListView() {
        return mLiveListView;
    }

    private void setStatus(Mode status) {
        mStatus = status;

        setVisibilityByFlags();
    }

    private void setVisibilityByFlags() {
        int flags = mStatus.flags();

        Log.d(TAG, "flags: " + flags);

        ViewUtil.setVisibility(mBtnLiveHome, flags & Mode.FLAG_BTN_LIVE_HOME);
        ViewUtil.setVisibility(mBtnLiveBack, flags & Mode.FLAG_BTN_LIVE_BACK);
        ViewUtil.setVisibility(mEditLiveSchedule, flags & Mode.FLAG_EDIT_LIVE_SCHEDULE);
        ViewUtil.setVisibility(mEditLiveTitle, flags & Mode.FLAG_EDIT_LIVE_TITLE);
        ViewUtil.setVisibility(mBtnLiveShare, flags & Mode.FLAG_BTN_LIVE_SHARE);
        ViewUtil.setVisibility(mBtnLivePrelive, flags & Mode.FLAG_BTN_LIVE_PRELIVE);
        ViewUtil.setVisibility(mBtnLiveComplete, flags & Mode.FLAG_BTN_LIVE_COMPLETE);
        ViewUtil.setVisibility(mBtnAddPrelive, flags & Mode.FLAG_BTN_ADD_PRELIVE);
        ViewUtil.setVisibility(mDateTimePicker, flags & Mode.FLAG_DATETIME_PICKER);
        ViewUtil.setVisibility(mLiveListView, flags & Mode.FLAG_LIVE_LISTVIEW);
    }

}
