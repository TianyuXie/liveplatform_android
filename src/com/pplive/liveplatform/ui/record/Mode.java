package com.pplive.liveplatform.ui.record;

enum Mode {
    HOME {
        @Override
        public int flags() {
            return FLAG_BTN_LIVE_HOME | FLAG_EDIT_LIVE_TITLE | FLAG_BTN_LIVE_SHARE | FLAG_BTN_LIVE_PRELIVE;
        }
    },
    EDIT {
        @Override
        public int flags() {
            return FLAG_BTN_LIVE_BACK | FLAG_EDIT_LIVE_SCHEDULE | FLAG_EDIT_LIVE_TITLE | FLAG_BTN_LIVE_COMPLETE | FLAG_DATETIME_PICKER;
        }
    },
    VIEW {
        @Override
        public int flags() {
            return FLAG_BTN_LIVE_BACK | FLAG_BTN_ADD_PRELIVE | FLAG_LIVE_LISTVIEW;
        }
    },
    LIVING {
        @Override
        public int flags() {
            // TODO Auto-generated method stub
            return 0;
        }
    };

    public abstract int flags();

    static final int FLAG_MASK = 0xffffffff;
    static final int FLAG_BTN_LIVE_HOME = 0x1;
    static final int FLAG_BTN_LIVE_BACK = 0x2;
    static final int FLAG_EDIT_LIVE_SCHEDULE = 0x4;
    static final int FLAG_EDIT_LIVE_TITLE = 0x8;
    static final int FLAG_BTN_LIVE_SHARE = 0x10;
    static final int FLAG_BTN_LIVE_PRELIVE = 0x20;
    static final int FLAG_BTN_LIVE_COMPLETE = 0x40;
    static final int FLAG_BTN_ADD_PRELIVE = 0x80;
    static final int FLAG_DATETIME_PICKER = 0x100;
    static final int FLAG_LIVE_LISTVIEW = 0x200;
}
