package com.pplive.liveplatform.ui.record.event;

public abstract class Event<T> {

    T mObject;
    
    public Event(T obj) {
        mObject = obj;
    }
    
    public T getObject() {
        return mObject;
    }
}
