package com.pplive.liveplatform.adapter;

import java.util.Collection;

public interface LoadDataInterface<T> {

    void refreshData(Collection<T> data);

    void appendData(Collection<T> data);

    public enum LoadMode {
        REFRESH {

            @Override
            public <T> void loadData(LoadDataInterface<T> loader, Collection<T> data) {
                loader.refreshData(data);
            }
        },

        APPEND {
            @Override
            public <T> void loadData(LoadDataInterface<T> loader, Collection<T> data) {
                loader.appendData(data);
            }
        };

        public abstract <T> void loadData(LoadDataInterface<T> loader, Collection<T> data);
    }
}
