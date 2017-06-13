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

    DatabaseReference databaseStepCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        databaseStepCount = FirebaseDatabase.getInstance().getReference("HeartRate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseStepCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.getChildren();
                HeartRate heartRate = dataSnapshot.getValue(HeartRate.class);

                TextView textView = (TextView) findViewById(R.id.heartRateDisp);
                String output = heartRate.getHeartRate();
                textView.setText(output);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

}
