package com.wolkabout.hexiwear.activity;

/**
 * Heart rate container class to be used with Firebase
 *
 * @author Michael Altair
 * @author Justin Finley
 */
public class HeartRate {
    public String heartRate;
    public HeartRate() {}
    public HeartRate(String heartRate){
        this.heartRate = heartRate;
    }

    /**
     * Sets the heart rate within the container
     * @param heartRate The athlete's current heart rate
     */
    public void setHeartRate(String heartRate){
//        int checkedValue;
//        // Making sure it is not a string
//        try {
//            checkedValue = Integer.parseInt(heartRate);
//        } catch (NumberFormatException e){
//            checkedValue = 0;
//        }
//        // Making sure it is not negative
//        if(checkedValue < 0) {
//            checkedValue = 0;
//        }
//        this.heartRate = "" + checkedValue;
        this.heartRate = heartRate;
    }

    /**
     * Gets the stored heart rate within the container
     * @return The stored heart rate
     */
    public String getHeartRate() {return heartRate; }
}
