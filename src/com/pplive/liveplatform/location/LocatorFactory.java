package com.pplive.liveplatform.location;

import java.util.Hashtable;
import java.util.Map;

import android.content.Context;

public class LocatorFactory {
    private static Map<LocatorType, Locator> instances = new Hashtable<LocatorType, Locator>();

    public enum LocatorType {
        BAIDU, GOOGLE
    }

    public static Locator createLocator(Context context, LocatorType type) {
        if (instances.containsKey(type)) {
            return instances.get(type);
        }
        switch (type) {
        case BAIDU:
            instances.put(type, new BaiduLocator(context));
            break;
        case GOOGLE:
            instances.put(type, new GoogleLocator(context));
            break;
        default:
            return instances.get(LocatorType.GOOGLE);
        }
        return instances.get(type);
    }

}
