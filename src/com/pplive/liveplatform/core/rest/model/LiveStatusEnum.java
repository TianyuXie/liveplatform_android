package com.pplive.liveplatform.core.rest.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.pplive.liveplatform.R;

public enum LiveStatusEnum {
    
    @SerializedName("notstart")
    NOT_START {

        @Override
        public String toString() {
            return "notstart";
        }
        
        @Override
        public String toFriendlyString(Context context) {
            return context.getString(R.string.program_status_notstart);
        }
    },

    @SerializedName("init")
    INIT {

        @Override
        public String toString() {
            return "init";
        }
        
        @Override
        public String toFriendlyString(Context context) {
            return context.getString(R.string.program_status_notstart);
        }
    },

    @SerializedName("preview")
    PREVIEW {

        @Override
        public String toString() {
            return "preview";
        }
        
        @Override
        public String toFriendlyString(Context context) {
            return context.getString(R.string.program_status_notstart);
        }
    },

    @SerializedName("living")
    LIVING {

        @Override
        public String toString() {
            return "living";
        }
        
        @Override
        public String toFriendlyString(Context context) {
            return context.getString(R.string.program_status_living);
        }
    },

    @SerializedName("stopped")
    STOPPED {

        @Override
        public String toString() {
            return "stopped";
        }
        
        @Override
        public String toFriendlyString(Context context) {
            return context.getString(R.string.program_status_stopped);
        }
    },

    @SerializedName("deleted")
    DELETED {

        @Override
        public String toString() {
            return "deleted";
        }
        
        @Override
        public String toFriendlyString(Context context) {
            return context.getString(R.string.program_status_deleted);
        }
    },

    @SerializedName("sysdeleted")
    SYS_DELETED {

        @Override
        public String toString() {
            return "sysdeleted";
        }

        @Override
        public String toFriendlyString(Context context) {
            return context.getString(R.string.program_status_deleted);
        }
    };
    
    public abstract String toFriendlyString(Context context);

}
