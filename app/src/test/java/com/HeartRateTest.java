package com;

import com.wolkabout.hexiwear.activity.HeartRate;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Michael on 6/30/2017.
 */

public class HeartRateTest {
    @Test
    public void notNegative() throws Exception {
        HeartRate hr = new HeartRate();
        hr.setHeartRate("-10");
        assertFalse(Integer.parseInt(hr.getHeartRate()) < 0);
    }
    @Test
    public void notString() throws Exception {
        HeartRate hr = new HeartRate();
        hr.setHeartRate("frogs");
        assertNotNull(Integer.parseInt(hr.getHeartRate()));
    }
}
