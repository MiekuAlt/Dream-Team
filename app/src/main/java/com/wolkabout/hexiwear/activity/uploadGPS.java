package com.wolkabout.hexiwear.activity;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolkabout.hexiwear.R;


import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Obselete class that was transfered over to a service
 */
public class uploadGPS extends AppCompatActivity {

    private static final String TAG = "uploadGPS";

    private Button btn_start, btn_stop;
    private TextView textViewLongitude, textViewLatitude, textViewAltitude, textViewDistance;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<Double> nums = new ArrayList<Double>();
    private Coordinates coordinates1 = null, coordinates2 = null;
    private double average, sum;
    private int num_altitude_vales_to_average = 5;
    //   public Distance firebaseDistance = null;
    public double distanceTraveled = 0;

    DatabaseReference databaseCoordinates;
    DatabaseReference databaseDistance;

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    sum = 0;
                    String[] coordinates = intent.getStringArrayExtra("coordinates");
                    //average the altitude values to gain more accuracy
                    if(nums.size()<num_altitude_vales_to_average)
                        nums.add(Double.parseDouble(coordinates[2]));
                    else{
                        nums.remove(0);
                        nums.add((Double.parseDouble(coordinates[2])));
                    }
                    for(Double i:nums)
                        sum+=i;
                    average = sum/nums.size();
                    //make coordinates object
                    if(coordinates1 == null)
                        coordinates1 = new Coordinates(coordinates[0], coordinates[1], Double.toString(average));
                    else
                        coordinates2 = new Coordinates(coordinates[0], coordinates[1], Double.toString(average));
                    String id = databaseCoordinates.push().getKey();
                    //databaseCoordinates.child(id).setValue(data);
                    databaseCoordinates.setValue(coordinates1);
                    //gets current distance value from FireBase

/*                    databaseDistance.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Distance firebaseDistance = dataSnapshot.getValue(Distance.class);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                    */
                    //calculates the cumulative distance and uploads it to firebase
                    if(coordinates1 != null && coordinates2 != null) {
                        distanceTraveled+=(distance(coordinates1, coordinates2));
                        databaseDistance.setValue(distanceTraveled+"");
                        textViewDistance.setText(distanceTraveled+"");
                    }


                    //set values on activity
                    textViewLongitude.setText(coordinates[0]);
                    textViewLatitude.setText(coordinates[1]);
                    textViewAltitude.setText(Double.toString(average));
                    if(coordinates2 != null)
                        coordinates1 = coordinates2;
                    //data = null;
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseCoordinates = FirebaseDatabase.getInstance().getReference("Coordinates");
        databaseDistance = FirebaseDatabase.getInstance().getReference("Distance");

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        textViewLongitude = (TextView) findViewById(R.id.textViewLongitude);
        textViewAltitude = (TextView) findViewById(R.id.textViewAltitude);
        textViewLatitude = (TextView) findViewById(R.id.textViewLatitude);
        textViewDistance = (TextView) findViewById(R.id.textViewDisance);

        if(!runtime_permissions())
            enable_buttons();

    }

    private void enable_buttons() {
        Log.i(TAG, "Buttons Enabled");
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Intent j = new Intent(uploadGPS.this, MapsActivity.class);
                //startActivity(j);
            }
        });
        //turn off GPS service and reset the distance count to 0
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                stopService(i);
                distanceTraveled = 0;

            }
        });

    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }

    /**
     * @author Created by David George and edited by Neeme Praks
     * This method was taken from stack overflow
     *
     * takes 2 coordinate objects contating longitude, latitude, and latitude and calculates
     * the distance between the two points.
     * @param coordinates1
     * @param coordinates2
     * @return
     */
    public static double distance(Coordinates coordinates1, Coordinates coordinates2) {
        double lat1 = Double.parseDouble(coordinates1.getLatitude());
        double lat2 = Double.parseDouble(coordinates2.getLatitude());
        double lon1 = Double.parseDouble(coordinates1.getLongitude());
        double lon2 = Double.parseDouble(coordinates2.getLongitude());
        double el1 = Double.parseDouble(coordinates1.getAltitude());
        double el2 = Double.parseDouble(coordinates2.getAltitude());

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public String addDistances(double distance1, String distance2){
        return (distance1+Double.parseDouble(distance2))+"";
    }
}