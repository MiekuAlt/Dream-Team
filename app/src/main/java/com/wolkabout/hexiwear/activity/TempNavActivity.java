package com.wolkabout.hexiwear.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.fragment.ChatFragment;
import com.wolkabout.hexiwear.model.Globals;
import com.wolkabout.hexiwear.service.GetCoordinates_Service;

import static com.wolkabout.hexiwear.R.id.filler7;
import static com.wolkabout.hexiwear.R.id.uploadGPS;

/**
 * Used to provide temporary access to the different activities the team is working on
 *
 * @author Michael Altair
 */
public class TempNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_nav);
        //starts the getCoordinates_Service if it is not already running
        if(!isMyServiceRunning(GetCoordinates_Service.class)){
            Intent i = new Intent(getApplicationContext(), GetCoordinates_Service.class);
            startService(i);
        }
    }

    /**
     * On starting or resuming the activity, hides the coach-specific buttons from the athlete
     */
    @Override
    protected void onResume() {
        super.onResume();
        Button GPSButton;
        View space;

        if(Globals.isCoach()) { //if the user is the coach
            GPSButton = (Button) findViewById(uploadGPS);
            space = (View) findViewById(filler7);

            GPSButton.setVisibility(View.GONE); //get rid of the GPS button
            space.setVisibility(View.GONE); //get rid of the filler space
        }
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChatFragment chatFragment = new ChatFragment();

        fragmentTransaction.add(R.id.fragment_container, chatFragment);
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack(null);
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
        Intent intent = new Intent(this, SetRangesActivity.class);
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
        Intent intent = new Intent(this, TrackingActivity.class);
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

    /**
     * checks if a given service is running or not
     * @param serviceClass the service in question
     * @return a boolean, true if it is running, false otherwise
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
