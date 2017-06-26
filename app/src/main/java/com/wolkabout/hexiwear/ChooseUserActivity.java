package com.wolkabout.hexiwear;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

public class ChooseUserActivity extends AppCompatActivity {

    Globals g = Globals.getInstance();

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

        if(g.isInitialized()) { // Not the first time in the app
            if(g.isCoach()) { // User is the coach
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

        g.updateData(true, radioCoach.isChecked());

    }

}
