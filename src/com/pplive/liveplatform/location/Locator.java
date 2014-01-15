package com.pplive.liveplatform.location;

import android.content.Context;

public abstract class Locator {

    public enum Provider {
        GPS, WIFI, MOBILE, NONE
    };

    protected int gpsScanSpan = 20000;

    protected int networkScanSpan = 5000;

    protected OnLocationUpdateListener listener;

    protected Provider provider = Provider.MOBILE;

    public abstract boolean isAvailable(Context context);

    public abstract boolean setProvider(Context context);

    public abstract void start(Context context);

    public abstract void stop();

    public void setOnLocationUpdateListener(OnLocationUpdateListener listener) {
        this.listener = listener;
    }

    public interface OnLocationUpdateListener {
        void onLocationUpdate(LocationData location);

        void onLocationError(String message);
    }

    public class LocationData {
        public final static int VALID = 90;

        public final static int INVALID = 91;

        private double longitude;

        private double latitude;

        private String country;

        private String province;

        private String city;

        private String district;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        @Override
        public String toString() {
            String result = new StringBuffer().append(province).append(',').append(city).append(',').append(district).toString();
            return result.equals(",,") || result.equals("null,null,null") ? "unknown" : result;
        }

    }
}
