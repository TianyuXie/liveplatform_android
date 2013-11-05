package com.pplive.liveplatform.core.task;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TaskContext {

    private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();

    public Object get(String key) {
        return map.get(key);
    }

    public Object set(String key, Object value) {
        return map.put(key, value);
    }

    public Set<String> getKeySet() {
        return map.keySet();
    }
}
