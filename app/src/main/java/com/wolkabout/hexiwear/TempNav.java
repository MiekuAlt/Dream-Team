package com.wolkabout.hexiwear;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wolkabout.hexiwear.activity.MapsActivity;
import com.wolkabout.hexiwear.activity.uploadGPS;

/**
 * Used to provide temporary access to the different activities the team is working on
 *
 * @author Michael Altair
 */
public class TempNav extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_nav);
    }

    /**
     *  Takes the user to the Step Count Activity
     */
    public void goStepCount(View view) {
        Intent intent = new Intent(this, StepCountActivity.class);
        startActivity(intent);
    }
    /**
     *  Takes the user to the Heart Rate Activity
     */
    public void goHeartRate(View view) {
        Intent intent = new Intent(this, HeartRateActivity.class);
        startActivity(intent);
    }
    /**
     *  Takes the user to the Chat Fragment
     */
    public void goChat(View view) {
        getSupportFragmentManager().
                beginTransaction().add(R.id.fragment_container, new ChatFragment()).commit();
    }
    /**
     *  Takes the user to the Map Activity
     */
    public void goMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    /**
     *  Takes the user to the Range Activity
     */
    public void goRange(View view) {
        Intent intent = new Intent(this, SetRanges.class);
        startActivity(intent);
    }
    /**
     *  Takes the user to the User Login Activity
     */
    public void goUserLogin(View view) {
        Intent intent = new Intent(this, ChooseUserActivity.class);
        startActivity(intent);
    }
    /**
     *  Takes the user to the GPS Activity
     */
    public void goGPS(View view) {
        Intent intent = new Intent(this, uploadGPS.class);
        startActivity(intent);
    }

    /**
     * Closes the chat fragment when the user presses the back button
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStack();
    }
}
