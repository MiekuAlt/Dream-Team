package com.wolkabout.hexiwear;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
        Intent intent = new Intent(this, ChatFragment.class);
        startActivity(intent);
    }
    public void goMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
