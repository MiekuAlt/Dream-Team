package com;

import com.wolkabout.hexiwear.activity.StepCount;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Michael on 6/30/2017.
 */

public class StepCountTest {
    @Test
    public void notNegative() throws Exception {
        StepCount hr = new StepCount();
        hr.setStepCount("-10");
        assertFalse(Integer.parseInt(hr.getStepCount()) < 0);
    }
    @Test
    public void notString() throws Exception {
        StepCount hr = new StepCount();
        hr.setStepCount("frogs");
        assertNotNull(Integer.parseInt(hr.getStepCount()));
    }
}
