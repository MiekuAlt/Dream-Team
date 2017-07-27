package com;

/**
 * Created by Evan on 2017-07-03.
 */
import org.junit.Test;
import static org.junit.Assert.*;
import com.wolkabout.hexiwear.activity.StepCountActivity;

public class HistoricStepCountTest {
    @Test
    public void checkAvg() throws Exception {
        StepCountActivity step=new StepCountActivity();
        double array[]=new double[7];
        int count=0;
        while(count != 7)
        {
            array[count]=10*count;
            count++;
        }
        double weekly=step.stepAvgWeek(array);
        System.out.println (weekly);
        assertTrue(weekly==30.00 || weekly == 30);
    }
}
