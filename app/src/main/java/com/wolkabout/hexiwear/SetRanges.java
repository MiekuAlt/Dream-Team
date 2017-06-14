package com.wolkabout.hexiwear;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolkabout.hexiwear.activity.HeartRange;

import static com.wolkabout.hexiwear.R.layout.activity_set_range;


/**
 * Created by Evan on 2017-06-13.
 */

public class SetRanges extends AppCompatActivity {
    TextView heartText;

    DatabaseReference databaseMinHeartRange;
    DatabaseReference databaseMaxHeartRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_set_range);

        // Initializing the database
        databaseMinHeartRange = FirebaseDatabase.getInstance().getReference("MinHeartRange");
        databaseMaxHeartRange = FirebaseDatabase.getInstance().getReference("MaxHeartRange");

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseMinHeartRange.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Grabbing the data from Firebase
                dataSnapshot.getChildren();
                HeartRange heartRange = dataSnapshot.getValue(HeartRange.class);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        databaseMaxHeartRange.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Grabbing the data from Firebase
                dataSnapshot.getChildren();
                HeartRange heartRange = dataSnapshot.getValue(HeartRange.class);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
    public void changeHeart(View view){
        EditText editText = (EditText) findViewById(R.id.hRange);
        String changeMinHeart= editText.getText().toString();
        databaseMinHeartRange.child("minheartRange").setValue(changeMinHeart);

        EditText editText2 = (EditText) findViewById(R.id.hMaxRange);
        String changeMaxHeart= editText2.getText().toString();
        databaseMaxHeartRange.child("maxheartRange").setValue(changeMaxHeart);

        TextView textView = (TextView) findViewById(R.id.heartRangeDisp);
        textView.setText("Values Changed");
    }
}

