package com.pplive.liveplatform.core.api.exception;

public class LiveHttpException extends Exception {

    private static final long serialVersionUID = -8720008828698180471L;

    private int mError;
    
    private String mMessage;
    
    public LiveHttpException() {
        this(-1, "");
    }
    
    public LiveHttpException(int err) {
        this(err, "");
    }
    
    public LiveHttpException(int err, String msg) {
        mError = err;
        mMessage = msg;
    }
    
    public int getErrorCode() {
        return mError;
    }
    
    @Override
    public String getMessage() {
        return mMessage;
    }
    
    @Override
    public String toString() {
        return "error: " + mError + "; message: " + mMessage;
    }
}
