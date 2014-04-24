package com.pplive.liveplatform.core.record;

public enum Quality {

    High(0x3, 35, 450000, 50000, 25), Normal(0x2, 50, 220000, 30000, 15), Low(0x1, 125, 110000, 15000, 10);

    static {
        High.mNext = High;
        High.mPrevious = Normal;

        Normal.mNext = High;
        Normal.mPrevious = Low;

        Low.mNext = Normal;
        Low.mPrevious = Low;
    }

    private Quality(int intValue, int interval, int videoBitrate, int audioBitrate, int frameRate) {
        mIntValue = intValue;
        mInterval = interval;
        mVideoBitrate = videoBitrate;
        mAudioBitrate = audioBitrate;
        mFrameRate = frameRate;
    }

    private final int mIntValue;

    private final int mInterval;

    private final int mVideoBitrate;

    private final int mAudioBitrate;

    private final int mFrameRate;

    private Quality mNext;

    private Quality mPrevious;

    public final int getIntValue() {
        return mIntValue;
    }

    public final int getInterval() {
        return mInterval;
    }

    public final int getVideoBitrate() {
        return mVideoBitrate;
    }

    public final int getAudioBitrate() {
        return mAudioBitrate;
    }

    public final int getBitrate() {
        return mVideoBitrate + mAudioBitrate;
    }

    public final int getFrameRate() {
        return mFrameRate;
    }

    public final Quality next() {
        return mNext;
    }

    public final Quality previous() {
        return mPrevious;
    }

    public static Quality mapIntToValue(final int qualityInt) {
        for (Quality value : Quality.values()) {
            if (qualityInt == value.getIntValue()) {
                return value;
            }
        }

        return null;
    }

    public static Quality getDefault() {
        return Normal;
    }

    public static Quality selectQuality(float speed /* byte per second */) {
        if (speed >= 80 * 1024) {
            return High;
        } else if (speed >= 40 * 1024 && speed < 80 * 1024) {
            return Normal;
        } else {
            return Low;
        }
    }
}
