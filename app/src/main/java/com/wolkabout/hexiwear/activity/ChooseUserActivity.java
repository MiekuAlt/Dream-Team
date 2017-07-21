package com.wolkabout.hexiwear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import com.wolkabout.hexiwear.model.Globals;
import com.wolkabout.hexiwear.R;

/**
 * This activity's purpose is to allow the user to choose whether they are accessing the app as either the Coach or the Athlete
 *
 * @author Michael Altair
 * @author Chenxin Shu
 */
public class ChooseUserActivity extends AppCompatActivity {

    /**
     * Sets up the layout for the user and checks if the user has already chosen if they are a coach or athlete, then displays it
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        // Checking and filling the correct toggles
        checkUserState();
        setTitle("User Login");
    }

    /**
     * This checks the SharedPreferences singleton {@link Globals} to see if the user type has been initialized or not, if it has been it displays it on the GUI
     *
     */
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

    /**
     * This updates the {@link Globals} as to what kind of user is using the app based on their selections on the GUI
     *
     */
    public void updateUserType(View view) {
        RadioButton radioCoach = (RadioButton) findViewById(R.id.checkCoach);

        Globals.setInitialized(true);
        Globals.setCoach(radioCoach.isChecked());

        // Go to the navigator when updated
        Intent intent = new Intent(this, NavActivity.class);
        startActivity(intent);
    }

    /**
     * This clears out the values stored in {@link Globals}, this is used for testing purposes
     *
     */
    public void clearSharedPreferences(View view) {
        Globals.setInitialized(false);
        Globals.setCoach(false);

        // Go to the navigator when updated
        Intent intent = new Intent(this, ChooseUserActivity.class);
        startActivity(intent);
    }

} // end of ChooseUserActivity class
