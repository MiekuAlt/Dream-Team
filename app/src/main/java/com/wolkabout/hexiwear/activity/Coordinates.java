package com.wolkabout.hexiwear.activity;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Class used to receive objects form Firebase and convert them to LngLat Objects to be used in
 * Google Maps API
 */
public class Coordinates implements Serializable{
    private String longitude, latitude, altitude;

    public Coordinates(){}

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

    public LatLng toLatLng(){
        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }
}
