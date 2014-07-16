package com.pplive.liveplatform.task;

import java.util.concurrent.ConcurrentHashMap;

public class TaskContext {

    private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();

    public Object get(String key) {
        return map.get(key);
    }

    public Object get(String key, Object defaultVal) {
        Object result = map.get(key);
        if (result == null) {
            return defaultVal;
        } else {
            return result;
        }
    }

    public String getString(String key) {
        Object result = map.get(key);
        if (result == null) {
            return "";
        } else {
            return result.toString();
        }
    }

    public Object set(String key, Object value) {
        return map.put(key, value);
    }

}
