package com;

/**
 * Created by Evan on 2017-07-03.
 */
import org.junit.Test;
import static org.junit.Assert.*;
import com.wolkabout.hexiwear.StepCountActivity;

public class HistoricStepCountTest {
    @Test
    public void checkAvg() throws Exception {
        StepCountActivity step=new StepCountActivity();
        double array[]=new double[7];
        int count=0;
        while(count != 6)
        {
            array[count]=10*count;
            count++;
        }
        double weekly=step.stepAvgWeek(array);
        assertTrue(weekly==30.00 || weekly == 30);
    }
}
