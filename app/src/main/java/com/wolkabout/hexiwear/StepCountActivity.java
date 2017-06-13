package com.wolkabout.hexiwear;

/**
 * Created by Michael on 6/13/2017.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolkabout.hexiwear.activity.StepCount;


public class StepCountActivity extends AppCompatActivity {

    DatabaseReference databaseStepCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        // Initializing the database
        databaseStepCount = FirebaseDatabase.getInstance().getReference("StepCount");

    } // end of onCreate method

    @Override
    protected void onStart() {
        super.onStart();

        databaseStepCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Grabbing the data from Firebase
                dataSnapshot.getChildren();
                StepCount stepCount = dataSnapshot.getValue(StepCount.class);

                // Posting the data to appear on the GUI
                TextView textView = (TextView) findViewById(R.id.numStepsDisp);
                String output = stepCount.getStepCount();
                textView.setText(output);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

}
