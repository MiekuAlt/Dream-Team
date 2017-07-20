package com.wolkabout.hexiwear.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.model.StepCount;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Displays the athlete's current step count, provided by Firebase
 *
 * @author Michael Altair
 */
public class StepCountActivity extends AppCompatActivity {

    DatabaseReference databaseStepCount;
    DatabaseReference databaseHistoricStepCount;
    private LineGraphSeries<DataPoint> historicStep;
    private int i, steps;
    private int weekCounter;
    private double weekAvg[]=new double[7];
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
    private String date = dateFormat.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        // Initializing the database
        databaseStepCount = FirebaseDatabase.getInstance().getReference("StepCount");
        databaseHistoricStepCount= FirebaseDatabase.getInstance().getReference("HistoricStepCount");

        //Creates the viewport of a line graph outlining the max heart rate each day over a 7 day period
        GraphView graph = (GraphView) findViewById(R.id.graph);
        historicStep = new LineGraphSeries<DataPoint>();
        graph.addSeries(historicStep);
        historicStep.setDrawDataPoints(true);
        historicStep.setDataPointsRadius(10);
        historicStep.setDrawBackground(true);
        historicStep.setBackgroundColor(Color.parseColor("#8087CEEB"));
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        //test changes
        viewport.setMinX(0);
        viewport.setMaxX(6);
        viewport.setMinY(0);
        viewport.setMaxY(16000);
        graph.setTitle("Historic Step Count");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Day");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Steps Taken");
        //hello

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseStepCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Grabbing the data from Firebase
                dataSnapshot.getChildren();
                StepCount stepCount = dataSnapshot.getValue(StepCount.class);

                // Posting the data to appear on the GUI
                TextView textView = (TextView) findViewById(R.id.numStepsDisp);
                String output = stepCount.getStepCount();
                textView.setText(output);

                String stepNum[]=output.split(" ");

                steps=Integer.parseInt(stepNum[0]);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        //Gets all of the historical data from the database and graphs it using addEntry
        databaseHistoricStepCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Grabbing the data from Firebase
                for (DataSnapshot valuesSnapshot : dataSnapshot.getChildren()) {
                    //The key is the date
                    String d=valuesSnapshot.getKey();
                    //the value is the max Step Count on a given day
                    int point = valuesSnapshot.getValue(int.class);
                    addEntry(d, point);

                    // Creates an array that stores the past 7 days of data
                    // if the count reaches 6 (7 days) it resets the counter and the earliest entry
                    // will be removed so the past 7 days will always be represented
                    if(weekCounter!=6)
                    {
                        weekAvg[weekCounter]=point;
                        weekCounter++;
                    }
                    else
                    {
                        weekCounter=0;
                    }
                    double weekly=stepAvgWeek(weekAvg);
                    TextView view = (TextView) findViewById(R.id.avgDisplay);
                    view.setText(String.valueOf(weekly));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    //Plots the data to the graph
    //d is not currently used but passes the date
    private void addEntry(String d, int p) {
        // Display max 100 points on the viewport
        historicStep.appendData(new DataPoint(i++, p), true, 100);
    }

    //A method to calculate the average number of steps taken in the past 7 days
    public double stepAvgWeek(double weekAvg[]){
        int count=0;
        double total=0;
        while(count !=7)
        {
            total=total+weekAvg[count];
            count++;
        }
        total=total/7;
        total=Math.floor(total*100) / 100;
        return total;
    }

    //Launches Whenever the activity is stopped
    public void onStop()
    {
        super.onStop();
        //Creates a date and sets the value in the database on the date to the max rate
        databaseHistoricStepCount.child(date).setValue(steps);

    }

}
