package com.pplive.liveplatform.core.rest;

public enum Protocol {
    HTTP {
        @Override
        public String toString() {
            return "http://";
        }
    }, 
    HTTPS {
        @Override
        public String toString() {
            return "https://";
        }
    }, 
    RTMP {
        @Override
        public String toString() {
            return "rtmp://";
        }
    },
    RTSP {
        @Override
        public String toString() {
            return "rtsp://";
        }
    }, 
    CONTENT {
        @Override
        public String toString() {
            return "content://";
        }
    }, 
    FILE {
        @Override
        public String toString() {
            return "file://";
        }
    };
    
    public abstract String toString();
}