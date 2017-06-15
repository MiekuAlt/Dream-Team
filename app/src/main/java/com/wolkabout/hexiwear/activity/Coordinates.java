package com.wolkabout.hexiwear.activity;

/**
 * Created by Justin on 6/2/2017.
 */

public class Coordinates {
    private String longitude, latitude, altitude;

    public Coordinates(String longitude, String latitude, String altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getAltitude() {
        return altitude;
    }
}
