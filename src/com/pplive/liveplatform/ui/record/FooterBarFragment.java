package com.pplive.liveplatform.ui.record;

import java.util.Calendar;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.ui.widget.DateTimePicker;
import com.pplive.liveplatform.ui.widget.DateTimePicker.OnDateTimeChangedListener;
import com.pplive.liveplatform.ui.widget.HorizontalListView;
import com.pplive.liveplatform.util.ViewUtil;

public class FooterBarFragment extends Fragment implements OnClickListener, OnTouchListener, OnFocusChangeListener, OnDateTimeChangedListener {

    private static final String TAG = FooterBarFragment.class.getSimpleName();

    private ImageButton mBtnLiveHome;
    private ImageButton mBtnLiveBack;

    private EditText mEditLiveSchedule;
    private EditText mEditLiveTitle;

    private ImageButton mBtnLiveShare;
    private ImageButton mBtnLivePrelive;

    private ImageButton mBtnLiveComplete;
    private ImageButton mBtnLiveAdd;

    private DateTimePicker mDateTimePicker;
    private HorizontalListView mLiveListView;

    private FooterBarFragment.Status mStatus = Status.HOME;

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
        mBtnLiveAdd = (ImageButton) layout.findViewById(R.id.btn_live_add);

        mDateTimePicker = (DateTimePicker) layout.findViewById(R.id.datetime_picker);

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DATE, 7);
        mDateTimePicker.setMaxDate(maxDate.getTimeInMillis());

        Calendar minDate = Calendar.getInstance();
        mDateTimePicker.setMinDate(minDate.getTimeInMillis() - 1000);

        mLiveListView = (HorizontalListView) layout.findViewById(R.id.live_listview);

        mBtnLivePrelive.setOnClickListener(this);
        mBtnLiveBack.setOnClickListener(this);
        mBtnLiveComplete.setOnClickListener(this);

        mEditLiveSchedule.setOnTouchListener(this);
        mEditLiveSchedule.setOnFocusChangeListener(this);
        mEditLiveTitle.setOnFocusChangeListener(this);

        mDateTimePicker.setOnDateTimeChanged(this);

        setStatus(Status.HOME);

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
        case R.id.btn_live_back:
            onClickBtnLiveBack(v);
            break;
        case R.id.btn_live_complete:
            onClickBtnLiveComplete(v);
            break;
        default:
            break;
        }
    }

    private void onClickBtnLivePrelive(View v) {
        setStatus(Status.EDIT);

        mEditLiveSchedule.requestFocus();
    }

    private void onClickBtnLiveBack(View v) {
        setStatus(Status.HOME);
    }

    private void onClickBtnLiveComplete(View v) {
        setStatus(Status.VIEW);
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

    private void setStatus(Status status) {
        mStatus = status;

        setVisibilityByFlags();
    }

    private void setVisibilityByFlags() {
        int flags = mStatus.flags();

        Log.d(TAG, "flags: " + flags);

        ViewUtil.setVisibility(mBtnLiveHome, flags & Status.FLAG_BTN_LIVE_HOME);
        ViewUtil.setVisibility(mBtnLiveBack, flags & Status.FLAG_BTN_LIVE_BACK);
        ViewUtil.setVisibility(mEditLiveSchedule, flags & Status.FLAG_EDIT_LIVE_SCHEDULE);
        ViewUtil.setVisibility(mEditLiveTitle, flags & Status.FLAG_EDIT_LIVE_TITLE);
        ViewUtil.setVisibility(mBtnLiveShare, flags & Status.FLAG_BTN_LIVE_SHARE);
        ViewUtil.setVisibility(mBtnLivePrelive, flags & Status.FLAG_BTN_LIVE_PRELIVE);
        ViewUtil.setVisibility(mBtnLiveComplete, flags & Status.FLAG_BTN_LIVE_COMPLETE);
        ViewUtil.setVisibility(mBtnLiveAdd, flags & Status.FLAG_BTN_LIVE_ADD);
        ViewUtil.setVisibility(mDateTimePicker, flags & Status.FLAG_DATETIME_PICKER);
        ViewUtil.setVisibility(mLiveListView, flags & Status.FLAG_LIVE_LISTVIEW);
    }

    enum Status {
        HOME {
            @Override
            public int flags() {
                return FLAG_BTN_LIVE_HOME | FLAG_EDIT_LIVE_TITLE | FLAG_BTN_LIVE_SHARE | FLAG_BTN_LIVE_PRELIVE;
            }
        },
        EDIT {
            @Override
            public int flags() {
                return FLAG_BTN_LIVE_BACK | FLAG_EDIT_LIVE_SCHEDULE | FLAG_EDIT_LIVE_TITLE | FLAG_BTN_LIVE_COMPLETE | FLAG_DATETIME_PICKER;
            }
        },
        VIEW {
            @Override
            public int flags() {
                return FLAG_BTN_LIVE_BACK | FLAG_BTN_LIVE_ADD | FLAG_LIVE_LISTVIEW;
            }
        },
        LIVING {
            @Override
            public int flags() {
                // TODO Auto-generated method stub
                return 0;
            }
        };

        public abstract int flags();

        static final int FLAG_MASK = 0xffffffff;
        static final int FLAG_BTN_LIVE_HOME = 0x1;
        static final int FLAG_BTN_LIVE_BACK = 0x2;
        static final int FLAG_EDIT_LIVE_SCHEDULE = 0x4;
        static final int FLAG_EDIT_LIVE_TITLE = 0x8;
        static final int FLAG_BTN_LIVE_SHARE = 0x10;
        static final int FLAG_BTN_LIVE_PRELIVE = 0x20;
        static final int FLAG_BTN_LIVE_COMPLETE = 0x40;
        static final int FLAG_BTN_LIVE_ADD = 0x80;
        static final int FLAG_DATETIME_PICKER = 0x100;
        static final int FLAG_LIVE_LISTVIEW = 0x200;
    }

}
