package com.wolkabout.hexiwear;

/**
 * Created by Michael on 6/26/2017.
 *
 * This is a singleton design pattern
 */

public class Globals {
    private static Globals instance;



    private static boolean initialized;
    private static boolean coach;

    private Globals() {
        loadData();
    }

    public void updateData(boolean initialized, boolean coach) {
        Globals.initialized = initialized;
        Globals.coach = coach;

        saveData();
    }
    private static void setInitialized (boolean value) {
        Globals.initialized = value;
    }
    private static void setCoach (boolean value) {
        Globals.coach = value;
    }

    public static boolean isInitialized () {
        return initialized;
    }
    public static boolean isCoach () {
        return coach;
    }

    // Ensuring that there is only one
    public static synchronized Globals getInstance() {
        if(instance == null) {
            instance = new Globals();
        }
        return instance;
    }

    // This saves data to the device
    private void saveData() {

    }
    // This loads the saved data to the singleton
    private void loadData() {

    }

} // end of the Globals class
