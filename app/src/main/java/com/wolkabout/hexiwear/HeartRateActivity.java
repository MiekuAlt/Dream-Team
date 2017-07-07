package com.wolkabout.hexiwear;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.wolkabout.hexiwear.activity.HeartRate;
import com.wolkabout.hexiwear.activity.MinHeartRange;
import com.wolkabout.hexiwear.activity.MaxHeartRange;
import com.wolkabout.hexiwear.activity.HistoricHeartRate;
import com.wolkabout.hexiwear.activity.HeartRate;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


/**
 * Displays the athlete's current heart rate, provided by Firebase, also has access to the Min and Max
 * ranges for the athlete's heart rate as set by the coach
 *
 * @author Michael Altair
 * @author Dream Lawson
 */
public class HeartRateActivity extends AppCompatActivity {
    DatabaseReference databaseHeartRate;
    DatabaseReference databaseMinHeartRange;
    DatabaseReference databaseMaxHeartRange;
    DatabaseReference databaseHistoricHeartRate;
    private LineGraphSeries<DataPoint> historicHeart;
    private int i, maxRate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
    private String date = dateFormat.format(new Date());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        
        // Initializing the database
        databaseHeartRate = FirebaseDatabase.getInstance().getReference("HeartRate");
        databaseMinHeartRange = FirebaseDatabase.getInstance().getReference("MinHeartRange");
        databaseMaxHeartRange = FirebaseDatabase.getInstance().getReference("MaxHeartRange");
        databaseHistoricHeartRate= FirebaseDatabase.getInstance().getReference("HistoricHeartRate");


        //Creates the viewport of a line graph outlining the max heart rate each day over a 7 day period
        GraphView graph = (GraphView) findViewById(R.id.graph);
        historicHeart = new LineGraphSeries<DataPoint>();
        graph.addSeries(historicHeart);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);

        viewport.setMinX(0);
        viewport.setMaxX(6);
        viewport.setMinY(0);
        viewport.setMaxY(200);
        graph.setTitle("Historic Max Heart Rate");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Heart rate");

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseHeartRate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Grabbing the data from Firebase
                dataSnapshot.getChildren();
                HeartRate heartRate = dataSnapshot.getValue(HeartRate.class);

                // Posting the data to appear on the GUI
                TextView textView = (TextView) findViewById(R.id.heartRateDisp);
                String output = heartRate.getHeartRate();
                textView.setText(output);

                //Gets the current heart rate from the watch and if it is higher than maxRate it changes it
                //to the current rate
                String heartNum[]=output.split(" ");
                int rate=Integer.parseInt(heartNum[0]);
                if(rate > maxRate){
                    maxRate=rate;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        //Listens for chages in the minimum heart range
        databaseMinHeartRange.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Grabbing the data from Firebase
                dataSnapshot.getChildren();
                MinHeartRange minHeartRange = dataSnapshot.getValue(MinHeartRange.class);

                // Posting the data to appear on the GUI
                TextView textView = (TextView) findViewById(R.id.minRange);
                Long output = minHeartRange.getMinHeartRange();
                textView.setText(output.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        //Listens for chages in the maximum heart range
        databaseMaxHeartRange.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Grabbing the data from Firebase
                dataSnapshot.getChildren();
                MaxHeartRange maxHeartRange = dataSnapshot.getValue(MaxHeartRange.class);

                // Posting the data to appear on the GUI
                TextView textView = (TextView) findViewById(R.id.maxRange);
                Long output = maxHeartRange.getMaxHeartRange();
                textView.setText(output.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //Gets the current days max heart rate from the database and sets the maxRate to the current days maximum rate
        databaseHistoricHeartRate.child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Grabbing the data from Firebase
                if(dataSnapshot.getValue(int.class) != null) {
                    int heartMax = dataSnapshot.getValue(int.class);
                    maxRate = heartMax;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        //Gets all of the historical data from the database and graphs it using addEntry
        databaseHistoricHeartRate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Grabbing the data from Firebase
                for (DataSnapshot valuesSnapshot : dataSnapshot.getChildren()) {
                    //The key is the date
                    String d=valuesSnapshot.getKey();
                    //the value is the max heart rate on a given day
                    int point = valuesSnapshot.getValue(int.class);
                    addEntry(d, point);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    //Plots the data to the graph
    private void addEntry(String d, int p) {
        // here, we choose to display max 100 points on the viewport and we scroll to end
        historicHeart.appendData(new DataPoint(i++, p), true, 100);
    }

    //Launches Whenever the activity is stopped
    public void onStop()
    {
        super.onStop();
        //Creates a date and sets the value in the database on the date to the max rate
        databaseHistoricHeartRate.child(date).setValue(maxRate);

    }
}
