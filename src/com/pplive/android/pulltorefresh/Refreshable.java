package com.pplive.android.pulltorefresh;

import java.util.Collection;

public interface Refreshable<T> {

    void refreshData(Collection<T> data);

    void appendData(Collection<T> data);

}
