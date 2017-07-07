package com.wolkabout.hexiwear.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.wolkabout.hexiwear.R;

/**
 * activity that allows the user to enable tracking. Once it is enabled the phone's GPS
 * coordinates are submitted to Firebase on a consistent basis and then dispalyed on the
 * MapsActivity
 */
public class Tracking extends AppCompatActivity {
    private Button button_tracking;
    boolean isTracking = false;
    private static final String TAG = "tracking";
    public static final String MyPreferences = "MyPrefs";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        Log.i(TAG, "On Create Done successfully");
        button_tracking = (Button) findViewById(R.id.button_tracking);
        if (savedInstanceState != null){
            Log.i(TAG, "Saved instance is not NULL");
            button_tracking.setText(savedInstanceState.getString("btn_text"));
        }
        //allows state to be maintained on activity being destroyed
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        button_tracking.setText(sharedPreferences.getString("btn_name","START TRACKING"));
        isTracking = sharedPreferences.getBoolean("isTracking", false);

        button_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Button is Clicked");
                if(!runtime_permissions()){
                    if(!isTracking) {
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
                        isTracking = false;
                        button_tracking.setText("Start Tracking");
                    }
                }
            }
        });
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

    /**
     * saves the state of the activity should it be partially obstructed or have its orientation changed
     * @param outstate
     */
    @Override
    protected void onSaveInstanceState(Bundle outstate){
        Log.i(TAG, "in on save state");
        outstate.putString("btn_text", button_tracking.getText().toString());
        super.onSaveInstanceState(outstate);
    }

    /**
     * restored the state of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        Log.i(TAG, "in on restore");
        button_tracking.setText(savedInstanceState.getString("btn_text"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("btn_name", button_tracking.getText().toString());
        editor.putBoolean("isTracking", isTracking);
        editor.commit();
        Toast.makeText(getApplicationContext(), "Saved Preferences", Toast.LENGTH_LONG).show();
    }
}
