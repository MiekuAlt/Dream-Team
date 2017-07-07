package com.wolkabout.hexiwear;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.wolkabout.hexiwear.R.layout.activity_set_range;


/**
 * Created by Evan on 2017-06-13.
 */

public class SetRanges extends AppCompatActivity {
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

    public void changeHeart(View view){
        TextView textView = (TextView) findViewById(R.id.heartRangeDisp);
        EditText minText = (EditText) findViewById(R.id.hMinRange);
        EditText maxText = (EditText) findViewById(R.id.hMaxRange);

        String minHeartString = minText.getText().toString();
        String maxHeartString = maxText.getText().toString();

        //Empty/null input check
        if(!minHeartString.equals("") && !minHeartString.isEmpty() && !maxHeartString.equals("") && !maxHeartString.isEmpty()){
            int changeMinHeart = Integer.parseInt(minHeartString);
            int changeMaxHeart = Integer.parseInt(maxHeartString);

            if(changeMinHeart < 0){
                textView.setText("Min heart range must be higher than 0");
            }
            else if(changeMaxHeart > 300){
                textView.setText("Max heart range should be lower than 300");
            }
            else if(changeMaxHeart < changeMinHeart || changeMinHeart > changeMaxHeart) {
                textView.setText("Max heart range must be higher than " + changeMinHeart);
            }
            else {
                databaseMinHeartRange.child("minHeartRange").setValue(changeMinHeart);
                databaseMaxHeartRange.child("maxHeartRange").setValue(changeMaxHeart);
                textView.setText("Range(s) Changed");
            }
        }
        //One of the input is empty
        else if((minHeartString.equals("") && minHeartString.isEmpty() && !maxHeartString.equals("") && !maxHeartString.isEmpty())
                || (!minHeartString.equals("") && !minHeartString.isEmpty() && maxHeartString.equals("") && maxHeartString.isEmpty())
                || (minHeartString.equals("") && minHeartString.isEmpty() && maxHeartString.equals("") && maxHeartString.isEmpty())) {
            textView.setText("Please put in a value for the Min and Max heart rate");
        }
        //Some other unknown error
        else{
            textView.setText("ERROR");
        }
    }
}

