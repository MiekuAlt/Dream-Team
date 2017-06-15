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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wolkabout.hexiwear.R;


import org.w3c.dom.Text;

import java.util.ArrayList;

public class uploadGPS extends AppCompatActivity {

    private Button btn_start, btn_stop;
    private TextView textViewLongitude, textViewLatitude, textViewAltitude;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<Double> nums = new ArrayList<Double>();
    private double average, sum;

    DatabaseReference databaseCoordinates;

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    sum = 0;
                    String[] coordinates = intent.getStringArrayExtra("coordinates");
                    if(nums.size()<5)
                        nums.add(Double.parseDouble(coordinates[2]));
                    else{
                        nums.remove(0);
                        nums.add((Double.parseDouble(coordinates[2])));
                    }
                    for(Double i:nums)
                        sum+=i;
                    average = sum/nums.size();
                    Coordinates data = new Coordinates(coordinates[0], coordinates[1], Double.toString(average));
                    String id = databaseCoordinates.push().getKey();
                    databaseCoordinates.child(id).setValue(data);

                    textViewLongitude.setText(coordinates[0]);
                    textViewLatitude.setText(coordinates[1]);
                    textViewAltitude.setText(Double.toString(average));
                    data = null;
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
        setContentView(R.layout.activity_main);

        databaseCoordinates = FirebaseDatabase.getInstance().getReference("Coordinates");

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        textViewLongitude = (TextView) findViewById(R.id.textViewLongitude);
        textViewAltitude = (TextView) findViewById(R.id.textViewAltitude);
        textViewLatitude = (TextView) findViewById(R.id.textViewLatitude);

        if(!runtime_permissions())
            enable_buttons();

    }

    private void enable_buttons() {

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);
                Intent j = new Intent(uploadGPS.this, MapsActivity.class);
                startActivity(j);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),GPS_Service.class);
                stopService(i);

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
}