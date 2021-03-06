package com.wolkabout.hexiwear.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Service that receives updates in location from the phone and sends them to the UploadGPS_Service
 */
public class GPS_Service extends Service {

    private static final String TAG = "GPS_Service";
    private final int GPS_time_in_milliseconds = 5000;
    private LocationListener listener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "GPS Service Created");
        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "Location Changed");
                Intent i = new Intent("location_update");
                i.putExtra("coordinates",new String[]{""+location.getLongitude(),""+location.getLatitude(),""+location.getAltitude()});
                sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {
                Log.i(TAG, "Provider Disabled");
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_time_in_milliseconds,0,listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
