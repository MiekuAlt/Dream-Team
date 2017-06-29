package com.wolkabout.hexiwear;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Michael on 6/27/2017.
 */

public class Globals {
    private static SharedPreferences mSharedPref;

    private Globals() {}

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static void setInitialized (boolean value) {

        write("init", value);
    }
    public static void setCoach (boolean value) {

        write("coach", value);
    }

    public static boolean isInitialized () {

        return read("init", false);
    }
    public static boolean isCoach () {

        return read("coach", false);
    }
}
