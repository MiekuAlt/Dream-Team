package com.wolkabout.hexiwear.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wolkabout.hexiwear.Globals;
import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.service.GPS_Service;
import com.wolkabout.hexiwear.service.UploadGPS_Service;

/**
 * activity that allows the user to enable tracking. Once it is enabled the phone's GPS
 * coordinates are submitted to Firebase on a consistent basis and then dispalyed on the
 * MapsActivity
 */
public class TrackingActivity extends AppCompatActivity {
    private Button button_tracking;
    boolean isTracking = false;
    private static final String TAG = "tracking";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        Log.i(TAG, "On Create Done successfully");
        button_tracking = (Button) findViewById(R.id.button_tracking);
        //only athlete is allowed to be tracked
        if(Globals.isCoach())
            button_tracking.setVisibility(View.INVISIBLE);
        //sets initial value for text of Tracking Button
        if(!isMyServiceRunning(UploadGPS_Service.class))
            button_tracking.setText("Start Tracking");
        else
            button_tracking.setText("Stop Tracking");

        //sets onClicklistener for the tracking button
        button_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Button is Clicked");
                if(!runtime_permissions()){
                    if(!isMyServiceRunning(UploadGPS_Service.class)) {
                        Log.i(TAG, "Should now be tracking");
                        Intent i = new Intent(getApplicationContext(), UploadGPS_Service.class);
                        startService(i);
                        isTracking = true;
                        button_tracking.setText("Stop Tracking");
                        Toast.makeText(getApplicationContext(), "Now Tracking", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Log.i(TAG, "Not Tracking Anymore");
                        Intent i = new Intent(getApplicationContext(),UploadGPS_Service.class);
                        stopService(i);
                        Intent j = new Intent(getApplicationContext(),GPS_Service.class);
                        stopService(j);
                        isTracking = false;
                        button_tracking.setText("Start Tracking");
                        Toast.makeText(getApplicationContext(), "Not Tracking", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /**
     * Class the determines if a service is running or not
     * @param serviceClass service in question
     * @return true if service is running, false otherwise
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    /**
     * determine if the runtime persmissions required are granted, if they are not then it will request them
     * @return false if all persmissions are granted, true, if they need to be acquired
     */
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "Do not have permissions needed");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        Log.i(TAG, "Have all permissions needed");
        return false;
    }

    /**
     * requests the persmissons from the user
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "Permissions should be granted");
            }else {
                runtime_permissions();
            }
        }
    }
}
