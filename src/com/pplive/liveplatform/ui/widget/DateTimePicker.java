package com.pplive.liveplatform.ui.widget;

import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.pplive.liveplatform.R;

public class DateTimePicker extends LinearLayout {

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private OnDateTimeChangedListener mOnDateTimeChangedListener;

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_datetime_picker, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDatePicker = (DatePicker) findViewById(R.id.date_picker);
        mTimePicker = (TimePicker) findViewById(R.id.time_picker);

        Calendar calendar = Calendar.getInstance();
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                onDateTimeChanged();
            }
        });

        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                onDateTimeChanged();
            }
        });
    }

    public void setOnDateTimeChanged(OnDateTimeChangedListener listener) {
        mOnDateTimeChangedListener = listener;

        onDateTimeChanged();
    }

    private void onDateTimeChanged() {
        if (null != mOnDateTimeChangedListener) {
            mOnDateTimeChangedListener.onDateTimeChanged(getYear(), getMonth(), getDayOfMonth(), getCurrentHour(), getCurrentMinute());
        }
    }

    public int getYear() {
        return mDatePicker.getYear();
    }

    public int getMonth() {
        return mDatePicker.getMonth() + 1;
    }

    public int getDayOfMonth() {
        return mDatePicker.getDayOfMonth();
    }

    public int getCurrentHour() {
        return mTimePicker.getCurrentHour();
    }

    public int getCurrentMinute() {
        return mTimePicker.getCurrentMinute();
    }

    public interface OnDateTimeChangedListener {

        void onDateTimeChanged(int year, int month, int day, int hour, int minute);

    }
}
