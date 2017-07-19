package com.wolkabout.hexiwear.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * service that receives coordinates from the GPS_Service and uploads them to Firebase
 */
public class UploadGPS_Service extends Service {
    private static final String TAG = "UploadGPS_Service";


    private BroadcastReceiver broadcastReceiver;
    private ArrayList<Double> nums = new ArrayList<Double>();
    private Coordinates coordinates1 = null, coordinates2 = null;
    private double average, sum;
    private int num_altitude_vales_to_average = 5;
    public double distanceTraveled = 0;

    DatabaseReference databaseCoordinates;
    DatabaseReference databaseDistance;
    public UploadGPS_Service() {
    }

    @Override
    public void onCreate(){
        Log.i(TAG, "UploadGPS_Service Created");
        databaseCoordinates = FirebaseDatabase.getInstance().getReference("Coordinates");
        databaseDistance = FirebaseDatabase.getInstance().getReference("Distance");
        Intent i =new Intent(getApplicationContext(),GPS_Service.class);
        startService(i);
        Log.i(TAG, "service Started");
        //register broadcast receiver to receive GPS data from GPS_Service
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    sum = 0;
                    String[] coordinates = intent.getStringArrayExtra("coordinates");
                    processCoordinate(coordinates);
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    /**
     * process the coordinate and upload it to firebase
     * @param coordinates Coordiantes object
     */
    private void processCoordinate(String[] coordinates){
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
        databaseCoordinates.setValue(coordinates1);
        //calculates the cumulative distance and uploads it to firebase
        if(coordinates1 != null && coordinates2 != null) {
            distanceTraveled+=(distance(coordinates1, coordinates2));
            databaseDistance.setValue(distanceTraveled+"");
        }
        if(coordinates2 != null)
            coordinates1 = coordinates2;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }
    @Override
    public IBinder onBind(Intent intent) {return null;}

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

    /**
     * add distances and return them as a string
     * @param distance1
     * @param distance2
     * @return sum of distances
     */
    public String addDistances(double distance1, String distance2){
        return (distance1+Double.parseDouble(distance2))+"";
    }
}
