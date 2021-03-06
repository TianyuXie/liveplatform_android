package com.pplive.liveplatform.core.dac.stat;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DacStat implements Serializable {

    private static final long serialVersionUID = -2416309042028184210L;

    static final String TAG = DacStat.class.getSimpleName();

    private Map<String, String> mMetaItems = new LinkedHashMap<String, String>();

    private Map<String, String> mValueItems = new LinkedHashMap<String, String>();

    protected void addMetaItem(String key, Object value) {
        addItem(mMetaItems, key, value);
    }

    protected void addValueItem(String key, Object value) {
        addItem(mValueItems, key, value);
    }

    private void addItem(Map<String, String> map, String key, Object value) {
        addItem(map, key, null != value ? value.toString() : "unknown");
    }

    private void addItem(Map<String, String> map, String key, String value) {
        //        Log.d(TAG, "key: " + key + "; value: " + value);

        map.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public Map.Entry<String, String>[] getMetaItems() {
        return mMetaItems.entrySet().toArray(new Map.Entry[0]);
    }

    @SuppressWarnings("unchecked")
    public Map.Entry<String, String>[] getValueItems() {
        return mValueItems.entrySet().toArray(new Map.Entry[0]);
    }
}
