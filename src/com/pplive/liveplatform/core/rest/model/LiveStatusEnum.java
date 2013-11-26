package com.pplive.liveplatform.core.rest.model;

import com.google.gson.annotations.SerializedName;

public enum LiveStatusEnum {

    @SerializedName("notstart")
    NOT_START {

        @Override
        public String toString() {
            return "notstart";
        }
    },

    @SerializedName("init")
    INIT {

        @Override
        public String toString() {
            return "init";
        }
    },

    @SerializedName("preview")
    PREVIEW {

        @Override
        public String toString() {
            return "preview";
        }
    },

    @SerializedName("living")
    LIVING {

        @Override
        public String toString() {
            return "living";
        }
    },

    @SerializedName("stopped")
    STOPPED {

        @Override
        public String toString() {
            return "stopped";
        }
    },

    @SerializedName("deleted")
    DELETED {

        @Override
        public String toString() {
            return "deleted";
        }
    },

    @SerializedName("sysdeleted")
    SYS_DELETED {

        @Override
        public String toString() {
            return "sysdeleted";
        }
    };

}
