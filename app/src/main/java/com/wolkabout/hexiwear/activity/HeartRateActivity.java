package com.wolkabout.hexiwear.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.model.Globals;
import com.wolkabout.hexiwear.model.HeartRate;
import com.wolkabout.hexiwear.model.MaxHeartRange;
import com.wolkabout.hexiwear.model.MinHeartRange;
import com.wolkabout.hexiwear.service.BluetoothService;



/**
 * Displays the athlete's current heart rate and historical data, provided by Firebase, also has access to
 * the Min and Max ranges for the athlete's heart rate as set by the coach
 *
 * @author Michael Altair
 * @author Sitanun Changhor (Dream)
 */
public class HeartRateActivity extends AppCompatActivity {
    DatabaseReference databaseHeartRate;
    DatabaseReference databaseMinHeartRange;
    DatabaseReference databaseMaxHeartRange;
    DatabaseReference databaseHistoricHeartRate;
    private LineGraphSeries<DataPoint> historicHeart;
    private int i, maxRate;
    private long maxHeartRate, minHeartRate;
    private ArrayList<String> xAxisDay=new ArrayList<String>();
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
        historicHeart.setDrawDataPoints(true);
        historicHeart.setDataPointsRadius(10);
        historicHeart.setDrawBackground(true);
        historicHeart.setBackgroundColor(Color.parseColor("#8087CEEB"));
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);

        viewport.setMinX(0);
        viewport.setMaxX(7);
        viewport.setMinY(0);
        viewport.setMaxY(200);
        graph.setTitle("Historic Max Heart Rate");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Day");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Heart rate");
        viewport.setScrollable(true);

        setTitle("Heart Rate");
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

                // Checking if the heartrate falls within range
                // Does not check if the heartrate is not intialized and there is not set max
                if(rate > 0 && maxHeartRate > 0) {
                    if (rate > maxHeartRate || rate < minHeartRate) {
                        textView.setTextColor(Color.RED);
                        rangeDanger();
                    } else {
                        textView.setTextColor(Color.BLACK);
                    }
                } else {
                    textView.setTextColor(Color.BLACK);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        /**
         * Listens for chages in the minimum heart range
         */
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

                minHeartRate = minHeartRange.getMinHeartRange();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        /**
         * Listens for chages in the maximum heart range
         */
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

                maxHeartRate = maxHeartRange.getMaxHeartRange();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        /**
         * Gets the current days max heart rate from the database and sets the maxRate to the current days maximum rate
         */
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

        /**
         * Gets all of the historical data from the database and graphs it using addEntry
         */
        databaseHistoricHeartRate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Grabbing the data from Firebase
                for (DataSnapshot valuesSnapshot : dataSnapshot.getChildren()) {
                    //The key is the date
                    String d=valuesSnapshot.getKey();

                    GraphView graph = (GraphView) findViewById(R.id.graph);
                    //adds the date to the date array
                    xAxisDay.add(d);

                    //the value is the max heart rate on a given day
                    int point = valuesSnapshot.getValue(int.class);
                    addEntry(point);
                    //Sets the x axis labels to be the dates (key)
                    graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            if (isValueX) {
                                // show x values as dates
                                return xAxisDay.get((int)value);
                            } else {
                                // normal Y values
                                return super.formatLabel(value, isValueX);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Plots the data to the graph
     * @param p an integer that represents the maximum heart rate to be placed on the graph
     */
    private void addEntry(int p) {
        // here, we choose to display max 100 points on the viewport and we scroll to end
        historicHeart.appendData(new DataPoint(i++, p), true, 100);
    }

    /**
     * Launches Whenever the activity is stopped
     */
    public void onStop()
    {
        super.onStop();
        //Creates a date and sets the value in the database on the date to the max rate
        databaseHistoricHeartRate.child(date).setValue(maxRate);
    }

    /**
     * This is triggered when the athlete's heartrate is outside the range set by the coach
     * If the user is the coach, their phone vibrates when out of rate. If the user is an
     * athlete, their watch vibrates
     */
    public void rangeDanger() {
        if(Globals.isCoach()){
            // Vibrates the coach's phone if out of range
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(400);
            addNotification();
        } else {
            // Vibrates the athlete's watch if out of range
            BluetoothService bs = new BluetoothService();
            bs.vibrateWatch(2);
            addNotification();
        }
    }

    /**
     * This, when triggered builds and displays a notification that the athlete's heart rate is out of range
     */
    public void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.hello_kitty)
                        .setContentTitle("Heart Rate Alert!")
                        .setContentText("Heart Rate is out of bounds!");

        Intent notificationIntent = new Intent(this, this.getClass());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}
