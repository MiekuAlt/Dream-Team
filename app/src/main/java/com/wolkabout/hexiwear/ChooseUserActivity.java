package com.wolkabout.hexiwear;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

public class ChooseUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        // Checking and filling the correct toggles
        checkUserState();
    }

    public void checkUserState() {
        RadioButton radioCoach = (RadioButton) findViewById(R.id.checkCoach);
        RadioButton radioAthlete = (RadioButton) findViewById(R.id.checkAthlete);

        if(Globals.isInitialized()) { // Not the first time in the app
            if(Globals.isCoach()) { // User is the coach
                radioCoach.setChecked(true);
                radioAthlete.setChecked(false);
            } else { // User is the athlete
                radioAthlete.setChecked(true);
                radioCoach.setChecked(false);
            }
        } else { // Nothing has been set yet!
            radioCoach.setChecked(false);
            radioAthlete.setChecked(false);
        }
    }

    public void updateUserType(View view) {
        RadioButton radioCoach = (RadioButton) findViewById(R.id.checkCoach);

        Globals.setInitialized(true);
        Globals.setCoach(radioCoach.isChecked());

        // Go to the navigator when updated
        Intent intent = new Intent(this, TempNav.class);
        startActivity(intent);

    }

    public void clearSharedPreferences(View view) {
        Globals.setInitialized(false);
        Globals.setCoach(false);

        // Go to the navigator when updated
        Intent intent = new Intent(this, ChooseUserActivity.class);
        startActivity(intent);
    }

}
