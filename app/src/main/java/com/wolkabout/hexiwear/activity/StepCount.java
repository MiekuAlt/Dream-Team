package com.wolkabout.hexiwear.activity;

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
     * @param setpCount The athlete's current step count
     */
    public void setStepCount(String setpCount){
        this.stepCount = setpCount;
    }

    /**
     * Gets the stored step count within the container
     * @return The stored step count
     */
    public String getStepCount() {
        return stepCount;
    }

}