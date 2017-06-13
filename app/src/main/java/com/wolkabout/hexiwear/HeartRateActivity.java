package com.wolkabout.hexiwear;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HeartRateActivity extends AppCompatActivity {
    TextView heartText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        heartText = (TextView) findViewById(R.id.heartRate1);
        heartText.setText("55");
    }
}
