package com.wolkabout.hexiwear.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolkabout.hexiwear.model.Coordinates;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Service that receives updates in location from the phone and sends them to the UploadGPS_Service
 */
public class GetCoordinates_Service extends Service {

    private static final String TAG = "GetCoordinates_Service";
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Coordinates");
    SharedPreferences sharedPreferences;
    BroadcastReceiver broadcastReceiver;
    boolean isMapsActivityOpen = false;
    ArrayList<Coordinates> coordinatesArray = new ArrayList<>();
    private boolean isFirstCallToFirebase = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //receives stored data from the sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        coordinatesArray = stringToArray(sharedPreferences.getString("coordinates",""));
        Log.i(TAG, "GetCoordinates_Service Created");
        //listens for coordinate updates in firebase and sends them to the MapsActivity if it is open
        //otherwise it stores them
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isFirstCallToFirebase)
                    isFirstCallToFirebase = false;
                else {
                    Coordinates coordinates = dataSnapshot.getValue(Coordinates.class);
                    coordinatesArray.add(coordinates);
                    //sends the Maps Activity the next Coordinates if it is open
                    if (isMapsActivityOpen) {
                        Intent i = new Intent("GetCoordinates_Service");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("coordinates", coordinates);
                        i.putExtras(bundle);
                        sendBroadcast(i);
                        Log.i(TAG, "Individual Coordinates Sent");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        //receives message from MapsActivity that it is open and sends it all stored coordinates or that
        //it is requesting that the list of coordinates on the map be cleared
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String result = intent.getStringExtra("Status");
                    //parse communications from the MapsActivity
                    //MapsActivity is now running
                    if(result.equals("true")) {
                        isMapsActivityOpen = true;
                        Intent i = new Intent("GetCoordinates_Service");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("array", coordinatesArray);
                        i.putExtras(bundle);
                        sendBroadcast(i);
                        Log.i(TAG, "Array of Coordinates Sent");
                    }
                    //Maps Activity requested that the stored data be cleared
                    else if(result.equals("clear"))
                        coordinatesArray.clear();
                    //MapsActivity has closed
                    else if(result.equals("false"))
                        isMapsActivityOpen = false;
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("Status"));
    }

    /**
     * Converts the current list of displayed coordinates to a string so that they can be
     * retained after the app has been closed;
     * @return
     */
    private String arrayToString(){
        String result = "";
        for(Coordinates coordinates: coordinatesArray){
            result += coordinates.getLongitude()+ " " + coordinates.getLatitude() + " " + coordinates.getAltitude() +" ";
        }
        return result;
    }

    /**
     * reverts the changes of arrayToString() by converting the string into an arraylist of coordinates.
     * @param s
     * @return
     */
    private ArrayList<Coordinates> stringToArray(String s){
        ArrayList<Coordinates> list = new ArrayList<Coordinates>();
        Scanner in = new Scanner(s);
        while(in.hasNext()){
            list.add(new Coordinates(in.next(), in.next(), in.next()));
        }
        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("coordinates", arrayToString());
        editor.commit();
    }
}
