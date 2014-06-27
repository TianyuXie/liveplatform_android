package com.pplive.android.pulltorefresh;

import java.util.Collection;

public enum RefreshMode {
    REFRESH {

        @Override
        public <T> void loadData(Refreshable<T> loader, Collection<T> data) {
            loader.refreshData(data);
        }
    },

    APPEND {
        @Override
        public <T> void loadData(Refreshable<T> loader, Collection<T> data) {
            loader.appendData(data);
        }
    };

    public abstract <T> void loadData(Refreshable<T> loader, Collection<T> data);
}
