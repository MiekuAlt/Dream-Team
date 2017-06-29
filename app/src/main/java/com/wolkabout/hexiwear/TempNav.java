package com.wolkabout.hexiwear;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wolkabout.hexiwear.activity.MapsActivity;
import com.wolkabout.hexiwear.activity.uploadGPS;

public class TempNav extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_nav);
    }

    public void goStepCount(View view) {
        Intent intent = new Intent(this, StepCountActivity.class);
        startActivity(intent);
    }
    public void goHeartRate(View view) {
        Intent intent = new Intent(this, HeartRateActivity.class);
        startActivity(intent);
    }
    public void goChat(View view) {
        getSupportFragmentManager().
                beginTransaction().add(R.id.fragment_container, new ChatFragment()).commit();
    }
    public void goMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void goRange(View view) {
        Intent intent = new Intent(this, SetRanges.class);
        startActivity(intent);
    }

    public void goUserLogin(View view) {
        Intent intent = new Intent(this, ChooseUserActivity.class);
    }
    public void goGPS(View view) {
        Intent intent = new Intent(this, uploadGPS.class);
        startActivity(intent);
    }

    //may not need this
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getFragmentManager().popBackStack();
    }
}
