package com.pplive.liveplatform.ui.widget;

import java.util.Calendar;

import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class DateTimePicker {
    
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private OnDateTimeChangedListener mOnDateTimeChangedListener;
    
    public DateTimePicker(DatePicker date, TimePicker time, OnDateTimeChangedListener listener) {
        mDatePicker = date;
        mTimePicker = time;
        mOnDateTimeChangedListener = listener;
        
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
