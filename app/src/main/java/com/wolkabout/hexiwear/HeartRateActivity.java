package com.wolkabout.hexiwear;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolkabout.hexiwear.activity.HeartRate;


public class HeartRateActivity extends AppCompatActivity {
    TextView heartText;

    DatabaseReference databaseHeartRate;
    //DatabaseReference databaseMinHeartRange;
    //DatabaseReference databaseMaxHeartRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        
        // Initializing the database
        databaseHeartRate = FirebaseDatabase.getInstance().getReference("HeartRate");
        //databaseMinHeartRange = FirebaseDatabase.getInstance().getReference("MinHeartRange");
        //databaseMaxHeartRange = FirebaseDatabase.getInstance().getReference("MaxHeartRange");

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseHeartRate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Grabbing the data from Firebase
                dataSnapshot.getChildren();
                HeartRate heartRate = dataSnapshot.getValue(HeartRate.class);

                // Posting the data to appear on the GUI
                TextView textView = (TextView) findViewById(R.id.heartRateDisp);
                String output = heartRate.getHeartRate();
                textView.setText(output);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
//        databaseMinHeartRange.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                // Grabbing the data from Firebase
//                dataSnapshot.getChildren();
//                MinHeartRange minHeartRange = dataSnapshot.getValue(MinHeartRange.class);
//
//                // Posting the data to appear on the GUI
//                TextView textView = (TextView) findViewById(R.id.minRange);
//                String output = minHeartRange.getMinHeartRange();
//                textView.setText(output);
//
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });
//        databaseMaxHeartRange.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                // Grabbing the data from Firebase
//                dataSnapshot.getChildren();
//                MaxHeartRange maxHeartRange = dataSnapshot.getValue(MaxHeartRange.class);
//
//                // Posting the data to appear on the GUI
//                TextView textView = (TextView) findViewById(R.id.maxRange);
//                String output = maxHeartRange.getMaxHeartRange();
//                textView.setText(output);
//
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });

    }
}
