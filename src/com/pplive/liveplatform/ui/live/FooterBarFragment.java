package com.pplive.liveplatform.ui.live;

import java.util.Calendar;

import android.app.Activity;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.UserManager;
import com.pplive.liveplatform.core.service.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.ProgramService;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.ui.live.event.EventProgramAdded;
import com.pplive.liveplatform.ui.live.event.EventProgramSelected;
import com.pplive.liveplatform.ui.live.event.EventReset;
import com.pplive.liveplatform.ui.widget.DateTimePicker;
import com.pplive.liveplatform.ui.widget.DateTimePicker.OnDateTimeChangedListener;
import com.pplive.liveplatform.util.ViewUtil;

import de.greenrobot.event.EventBus;

public class FooterBarFragment extends Fragment implements OnClickListener, OnTouchListener, OnFocusChangeListener, OnDateTimeChangedListener {

    private static final String TAG = FooterBarFragment.class.getSimpleName();

    private Activity mAttachedActivity;

    private View mParentLayout;

    private ImageButton mBtnLiveHome;
    private ImageButton mBtnLiveBack;

    private TextView mEditLiveSchedule;
    private EditText mEditLiveTitle;

    private ImageButton mBtnLiveShare;
    private ImageButton mBtnLivePrelive;

    private ImageButton mBtnLiveAddComplete;
    private ImageButton mBtnLiveEditComplete;

    private ImageButton mBtnAddPrelive;

    private DateTimePicker mDateTimePicker;
    private LiveListView mLiveListView;

    private Mode mStatus = Mode.INITIAL;

    private Program mSelectedProgram;

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

        mParentLayout = layout;

        mBtnLiveHome = (ImageButton) layout.findViewById(R.id.btn_live_home);
        mBtnLiveBack = (ImageButton) layout.findViewById(R.id.btn_live_back);

        mEditLiveSchedule = (TextView) layout.findViewById(R.id.edit_live_schedule);
        mEditLiveTitle = (EditText) layout.findViewById(R.id.edit_live_title);

        mBtnLiveShare = (ImageButton) layout.findViewById(R.id.btn_live_share);
        mBtnLivePrelive = (ImageButton) layout.findViewById(R.id.btn_live_prelive);

        mBtnLiveAddComplete = (ImageButton) layout.findViewById(R.id.btn_live_add_complete);
        mBtnLiveEditComplete = (ImageButton) layout.findViewById(R.id.btn_live_edit_complete);

        mBtnAddPrelive = (ImageButton) layout.findViewById(R.id.btn_add_prelive);

        mDateTimePicker = (DateTimePicker) layout.findViewById(R.id.datetime_picker);

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DATE, 7);
        mDateTimePicker.setMaxDate(maxDate.getTimeInMillis());

        Calendar minDate = Calendar.getInstance();
        mDateTimePicker.setMinDate(minDate.getTimeInMillis() - 60000);

        mLiveListView = (LiveListView) layout.findViewById(R.id.live_listview);

        mBtnLivePrelive.setOnClickListener(this);
        mBtnLiveHome.setOnClickListener(this);
        mBtnLiveBack.setOnClickListener(this);
        mBtnAddPrelive.setOnClickListener(this);
        mBtnLiveAddComplete.setOnClickListener(this);
        mBtnLiveEditComplete.setOnClickListener(this);

        mEditLiveSchedule.setOnTouchListener(this);
        mEditLiveSchedule.setOnFocusChangeListener(this);
        mEditLiveTitle.setOnFocusChangeListener(this);

        mDateTimePicker.setOnDateTimeChanged(this);

        setMode(Mode.INITIAL);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
        EventBus.getDefault().register(mLiveListView);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(mLiveListView);
    }

    @Override
    public void onResume() {
        super.onResume();

        init();
    }

    public void onBackPressed() {

        if (Mode.INITIAL == mStatus) {
            onClickBtnLiveHome();
        } else {
            onClickBtnLiveBack();
        }

    }

    @Override
    public void onDateTimeChanged(int year, int month, int day, int hour, int minute) {
        Log.d(TAG, "onDateTimeChanged");

        setScheduleDateTimeText(year, month, day, hour, minute);

        mBtnLiveAddComplete.setImageResource(R.drawable.live_record_btn_live_complete_blue);
    }

    private void setScheduleDateTimeText(int year, int month, int day, int hour, int minute) {

        mEditLiveSchedule.setText(String.format("%d/%02d/%02d %02d:%02d", year, month, day, hour, minute));
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {
        case R.id.btn_live_prelive:
            onClickBtnLivePrelive();
            break;
        case R.id.btn_live_home:
            onClickBtnLiveHome();
            break;
        case R.id.btn_live_back:
            onClickBtnLiveBack();
            break;
        case R.id.btn_add_prelive:
            onClickBtnAddPrelive();
            break;
        case R.id.btn_live_add_complete:
            onClickBtnLiveAddComplete();
            break;
        case R.id.btn_live_edit_complete:
            onClickBtnLiveEditComplete();
            break;
        default:
            break;
        }
    }

    private void onClickBtnLivePrelive() {
        setMode(Mode.VIEW_PRELIVES);

        mEditLiveSchedule.requestFocus();
    }

    private void onClickBtnLiveHome() {

        if (null != mAttachedActivity) {
            mAttachedActivity.finish();
        }
    }

    private void onClickBtnLiveBack() {
        reset();

        EventBus.getDefault().post(new EventReset());
    }

    private void onClickBtnAddPrelive() {
        setMode(Mode.ADD_PRELIVE);
    }

    private void onClickBtnLiveAddComplete() {
        setMode(Mode.VIEW_PRELIVES);

        final String title = mEditLiveTitle.getText().toString();
        final long starttime = mDateTimePicker.getTimeInMillis();

        AsyncTask<Void, Void, Program> createLiveTask = new AsyncTask<Void, Void, Program>() {

            @Override
            protected Program doInBackground(Void... params) {
                if (mAttachedActivity != null) {
                    Log.d(TAG, "title: " + (null == title ? "null" : title) + "; starttime: " + starttime);

                    try {
                        String username = UserManager.getInstance(mAttachedActivity).getUsernamePlain();
                        String token = UserManager.getInstance(mAttachedActivity).getToken();
                        Program program = new Program(username, title, starttime);

                        program = ProgramService.getInstance().createProgram(token, program);

                        return program;
                    } catch (LiveHttpException e) {
                        Log.d(TAG, "LiveHttpException");
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Program program) {
                if (program != null) {
                    EventBus.getDefault().post(new EventProgramAdded(program));
                } else {
                    if (mAttachedActivity != null) {
                        Toast.makeText(mAttachedActivity, R.string.toast_prelive_creat_fail, Toast.LENGTH_SHORT).show();
                    }
                }

                if (null != mBtnLiveAddComplete) {
                    mBtnLiveAddComplete.setImageResource(R.drawable.live_record_btn_live_complete);
                }
            }
        };

        createLiveTask.execute();
    }

    private void onClickBtnLiveEditComplete() {
        setMode(Mode.VIEW_PRELIVES);
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

        if (!v.hasFocus()) {
            v.requestFocusFromTouch();
        }

        ViewUtil.showOrHide(mDateTimePicker);

        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(TAG, "onFocusChange: " + v.getId());

        int color;
        switch (v.getId()) {
        case R.id.edit_live_schedule:
            Log.d(TAG, "hasFocus: " + hasFocus);
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

    public void onEvent(EventProgramSelected event) {
        final Program program = event.getObject();

        setPreLiveProgram(program);
    }

    public String getLiveTitle() {
        if (null != mEditLiveTitle) {
            return mEditLiveTitle.getText().toString();
        }

        return null;
    }

    public LiveListView getLiveListView() {
        return mLiveListView;
    }

    private void init() {

        if (null != mEditLiveTitle) {

            String liveTitle = null;
            if (null != mSelectedProgram) {
                liveTitle = mSelectedProgram.getTitle();
            } else {
                if (mAttachedActivity != null) {
                    String username = UserManager.getInstance(mAttachedActivity).getNickname();
                    liveTitle = mAttachedActivity.getResources().getString(R.string.default_live_title_fmt, username);
                }
            }

            mEditLiveTitle.setText(liveTitle);
        }

        if (null != mEditLiveSchedule) {
            setScheduleDateTimeText(mDateTimePicker.getYear(), mDateTimePicker.getMonth(), mDateTimePicker.getDayOfMonth(), mDateTimePicker.getCurrentHour(),
                    mDateTimePicker.getCurrentMinute());
        }

        if (null != mBtnLiveAddComplete) {
            mBtnLiveAddComplete.setImageResource(R.drawable.live_record_btn_live_complete);
        }
    }

    void reset() {
        Log.d(TAG, "reset");

        setMode(Mode.INITIAL);

        mSelectedProgram = null;

        init();
    }

    public void setPreLiveProgram(Program program) {
        setMode(Mode.EDIT_PRELIVE);

        mSelectedProgram = program;

        init();
    }

    public void onLivingStart() {
        setMode(Mode.LIVING);
    }

    public void onLivingStop() {
        reset();
    }

    public void setMode(Mode mode) {
        mStatus = mode;

        setVisibilityByFlags();
    }

    private void setVisibilityByFlags() {
        int flags = mStatus.flags();

        Log.d(TAG, "flags: " + flags);

        ViewUtil.setVisibility(mParentLayout, flags);

        ViewUtil.setVisibility(mBtnLiveHome, flags & Mode.FLAG_BTN_LIVE_HOME);
        ViewUtil.setVisibility(mBtnLiveBack, flags & Mode.FLAG_BTN_LIVE_BACK);
        ViewUtil.setVisibility(mEditLiveSchedule, flags & Mode.FLAG_EDIT_LIVE_SCHEDULE);
        ViewUtil.setVisibility(mEditLiveTitle, flags & Mode.FLAG_EDIT_LIVE_TITLE);
        ViewUtil.setVisibility(mBtnLiveShare, flags & Mode.FLAG_BTN_LIVE_SHARE);
        ViewUtil.setVisibility(mBtnLivePrelive, flags & Mode.FLAG_BTN_LIVE_PRELIVE);
        ViewUtil.setVisibility(mBtnLiveAddComplete, flags & Mode.FLAG_BTN_LIVE_ADD_COMPLETE);
        ViewUtil.setVisibility(mBtnAddPrelive, flags & Mode.FLAG_BTN_ADD_PRELIVE);
        ViewUtil.setVisibility(mDateTimePicker, flags & Mode.FLAG_DATETIME_PICKER);
        ViewUtil.setVisibility(mLiveListView, flags & Mode.FLAG_LIVE_LISTVIEW);
    }

    public void setOnShareBtnClickListener(View.OnClickListener l) {
        mBtnLiveShare.setOnClickListener(l);
    }

}
