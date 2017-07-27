package com.wolkabout.hexiwear.model;

/**
 * Step count container to be used with Firebase
 *
 * @author Michael Altair
 * @author Justin Finley
 */
public class StepCount {
    String stepCount;

    public StepCount(){}
    public StepCount(String stepCount){
        this.stepCount = stepCount;
    }

    /**
     * Sets the step count within the container
     * @param stepCount The athlete's current step count
     */
    public void setStepCount(String stepCount){
//        int checkedValue;
//        // Making sure it is not a string
//        try {
//            checkedValue = Integer.parseInt(stepCount);
//        } catch (NumberFormatException e){
//            checkedValue = 0;
//        }
//        // Making sure it is not negative
//        if(checkedValue < 0) {
//            checkedValue = 0;
//        }
//        this.stepCount = "" + checkedValue;
        this.stepCount = stepCount;
    }

    /**
     * Gets the stored step count within the container
     * @return The stored step count
     */
    public String getStepCount() {
        return stepCount;
    }

}