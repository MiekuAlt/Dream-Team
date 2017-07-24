package com.wolkabout.hexiwear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.model.Globals;

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

        restartApp();
    }

    private void restartApp() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

} // end of ChooseUserActivity class
