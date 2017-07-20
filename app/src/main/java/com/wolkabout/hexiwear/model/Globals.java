package com.wolkabout.hexiwear;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 * This is a Singleton which handles SharedPreferences (see {@link android.content.SharedPreferences})
 * The Singleton is first created within the MainActivity so that there will always be an instance of if running.
 * Any changes to and accessing any the SharedPreference variables are done through this class
 *
 * @author Michael Altair
 * @author Chenxin Shu
 *
 */
public class Globals {
    private static SharedPreferences mSharedPref;

    private Globals() {}

    /**
     *  This initializes the singleton, as long as it already doesn't exist
     *
     */
    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    /**
     * Accesses the stored booleans locally on the user's device through their {@link android.content.SharedPreferences}
     *
     * @param key The identifier used to access the stored value
     * @param defValue The default value that the stored value will be
     * @return The value that was stored referenced by its key
     */
    private static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    /**
     * Stores a boolean value into the user's local device under their {@link android.content.SharedPreferences} which is accessible through its key value
     *
     * @param key The identifier used for the stored value to be accessed
     * @param value The stored boolean that is referenced by its key
     */
    private static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    /**
     * A public access to set the isInitialized {@link android.content.SharedPreferences}
     *
     * @param value Whether the type of user (Athlete or Coach) has been set yet
     */
    public static void setInitialized (boolean value) {

        write("init", value);
    }

    /**
     * A public access to set the isCoach {@link android.content.SharedPreferences}
     *
     * @param value Whether the user is a Coach or not a Coach (Therefore, the Athlete)
     */
    public static void setCoach (boolean value) {

        write("coach", value);
    }

    /**
     * A public way to check {@link android.content.SharedPreferences} if the type of user has been initialized yet
     *
     * @return Returns whether it was initialized of not
     */
    public static boolean isInitialized () {

        return read("init", false);
    }

    /**
     * A public way to check {@link android.content.SharedPreferences} if the type of user is a Coach or not
     *
     * @return Returns if they are a Coach, or if false they are therefore an Athlete
     */
    public static boolean isCoach () {

        return read("coach", false);
    }

} // end of Globals class
