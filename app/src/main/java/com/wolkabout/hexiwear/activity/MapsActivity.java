package com.wolkabout.hexiwear.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolkabout.hexiwear.model.Globals;
import com.wolkabout.hexiwear.R;
import com.wolkabout.hexiwear.model.Coordinates;
import com.wolkabout.hexiwear.model.Route;
import com.wolkabout.hexiwear.service.UploadGPS_Service;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Google Maps activity that allows the user to track their route in real-time on the map
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Route");
    ArrayList<Coordinates> coordinateList;
    SharedPreferences sharedPreferences;
    private static final String TAG = "Maps Activity";
    Polyline historicLine;
    PolylineOptions historicLineOptions;
    Polyline updatetLine;
    PolylineOptions updateLineOptions;
    private int numCoordinates = 0;
    private LatLng mostRecent;
    private LatLng sMostRecent;
    private double distance = 0;
    private LatLng mostReventMarker = null;
    private LatLng sMostRecentMarker = null;
    private PolylineOptions routeLineOptions = new PolylineOptions();
    private Polyline routeLine = null;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private boolean isMakingRoute = false;
    private Button send_Button;
    private BroadcastReceiver broadcastReceiver;
    private boolean isfirstRun = true;
    private Marker currentPosition;
    private ArrayList<LatLng> routePoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        historicLineOptions = new PolylineOptions().color(Color.BLUE).width(20);
        updateLineOptions = new PolylineOptions().color(Color.BLUE).width(20);
        send_Button = (Button) findViewById(R.id.button_send);
        if(!Globals.isCoach()){
            Button make_Button = (Button) findViewById(R.id.button_make);
            make_Button.setVisibility(View.INVISIBLE);
        }
        //inform GetCoordinates_Service that the activity is open
        Intent i = new Intent("Status");
        i.putExtra("Status", "true");
        sendBroadcast(i);
        Log.i(TAG, "Opening Status Sent");

        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG, "Received intent from GetCoordinates_Service");
                    //if(intent.getAction().equals("array")) {
                    //checks if this is the first time receiving a broadcast since the activity started
                    if(isfirstRun) {
                        Log.i(TAG, "received array from Get_CoordinatesService");
                        Bundle bundle = intent.getExtras();
                        coordinateList = (ArrayList<Coordinates>) bundle.getSerializable("array");
                        for (Coordinates c : coordinateList)
                            addCoordinatesToPolyLine(c);
                        isfirstRun = false;
                    }
                    else {
                        Log.i(TAG, "received individual coordinates from Get_CoordinatesService");
                        Bundle bundle = intent.getExtras();
                        Coordinates coordinates = (Coordinates) bundle.getSerializable("coordinates");
                        addCoordinatesToPolyLine(coordinates);
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("GetCoordinates_Service"));

        //adds event listener for a new route being sent to athlete
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Route route = dataSnapshot.getValue(Route.class);
                if(route != null)
                    routePoints = stringToArray(route.getRoute());
                for (LatLng l : routePoints) {
                    routeLineOptions.add(l).color(Color.GREEN).width(20);
                    Log.i(TAG, "parsing element of received route" + l.toString());
                }
                routeLine = mMap.addPolyline(routeLineOptions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /**
     * instantiate the eventlistner that registers when coordinates are updated
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.clear();

        // Add a marker in Sydney and move the camera
        LatLng halifax = new LatLng(44.651070, -63.582687);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(halifax,13));
        //allows user to create custom routes on Map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(isMakingRoute)
                    addRouteMarker(latLng);
            }
        });

    }

    /**
     * method called when the coach wants to clear the route that they are currently editing
     */
    public void clearRoutPolylines(){
        routeLineOptions = new PolylineOptions();
        distance = 0;
        mostReventMarker = null;
        sMostRecentMarker = null;
        for(Marker m: markers)
            m.remove();
        routePoints.clear();
        if(routeLine != null) {
            routeLine.remove();
            routeLine = null;
        }
    }

    /**
     * if the coach is editing a route then the route is erased, otherwise the whole map is cleared
     * @param view
     */
    public void clearMap(View view){
        if(isMakingRoute) {
            clearRoutPolylines();
            send_Button.setVisibility(View.INVISIBLE);
        }
        else {
            mMap.clear();
            sharedPreferences.edit().remove("coordinates").commit();
            Intent i = new Intent("Status");
            i.putExtra("Status", "clear");
            sendBroadcast(i);
            Log.i(TAG, "Clear Request Sent");
        }
    }
    /**
     * used to set the route and disable editing also, uploads the route to firebase
     * @param view
     */
    public void sendRoute(View view){
        if(markers.size() >0) {
            for(Marker m: markers)
                m.remove();
            //upload the route to firebase
            routeLineOptions.color(Color.GREEN).width(20);
            routeLine.remove();
            routeLine = mMap.addPolyline(routeLineOptions);
        }
        ref.setValue(new Route(arrayToString(), Math.floor(distance)/1000+" KM"));
        send_Button.setVisibility(View.INVISIBLE);
        isMakingRoute = false;
    }
    public void makeRoute(View view){
        isMakingRoute = true;
        clearRoutPolylines();
        send_Button.setVisibility(View.VISIBLE);
    }
    /**
     * adds marker to the map that the user selected and updates the polyline and the distance ignoring altitude values
     * @param latLng
     */
    private void addRouteMarker(LatLng latLng) {
        if(mostReventMarker == null) {
            mostReventMarker = latLng;
        }
        else{
            sMostRecentMarker = mostReventMarker;
            mostReventMarker = latLng;
            //estimates the distance between the two coordinates without using altitude
            distance += UploadGPS_Service.distance(
                    new Coordinates(mostReventMarker.longitude+"", mostReventMarker.latitude+"", 0.0+""),
                    new Coordinates(sMostRecentMarker.longitude+"", sMostRecentMarker.latitude+"", 0.0+""));
        }
        //add the marker with the updated distance
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Distance").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        marker.setSnippet(Math.floor(distance)/1000+" KM");
        marker.showInfoWindow();
        markers.add(marker);
        routePoints.add(latLng);
        routeLineOptions.add(mostReventMarker).color(Color.YELLOW).width(20);
        if(routeLine != null)
            routeLine.remove();
        routeLine = mMap.addPolyline(routeLineOptions);
    }

    /**
     * method that updates the polyLine objects with the latest coordinate from FireBase
     * @param coordinates a coordinate object with longitude, latitude and altitude
     */
    private void addCoordinatesToPolyLine(Coordinates coordinates){
        if(currentPosition!= null)
            currentPosition.remove();
        currentPosition = mMap.addMarker(new MarkerOptions().position(coordinates.toLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.hello_kitty)));
        Log.i(TAG, coordinates.toLatLng().toString());
        if(numCoordinates <2){
            if(numCoordinates == 0) {
                sMostRecent = coordinates.toLatLng();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sMostRecent,15));
            }
            else {
                mostRecent = coordinates.toLatLng();
                historicLineOptions.add(sMostRecent);
            }
            updateLineOptions.add(coordinates.toLatLng());
            numCoordinates++;
        }
        else{
            historicLineOptions.add(mostRecent);
            sMostRecent = mostRecent;
            mostRecent = coordinates.toLatLng();
            updateLineOptions = new PolylineOptions().add(sMostRecent).add(mostRecent).color(Color.BLUE).width(20);
        }
        if(historicLine != null)
            historicLine.remove();
        if(updatetLine != null)
            updatetLine.remove();
        historicLine = mMap.addPolyline(historicLineOptions);
        updatetLine = mMap.addPolyline(updateLineOptions);
    }
    protected void onResume() {
        super.onResume();
    }

    /**
     * Converts the current list of displayed coordinates to a string so that they can be
     * retained after the app has been closed;
     * @return string representation of the array of points in routePoints
     */
    private String arrayToString(){
        String result = "";
        for(LatLng l: routePoints){
            result += l.latitude + " " + l.longitude + " ";
        }
        return result;
    }

    /**
     * reverts the changes of arrayToString() by converting the string into an arraylist of coordinates.
     * @param s
     * @return
     */
    private ArrayList<LatLng> stringToArray(String s){
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        Scanner in = new Scanner(s);
        while(in.hasNext()){
            list.add(new LatLng(Double.parseDouble(in.next()), Double.parseDouble(in.next())));
            Log.i(TAG, "parsing element of route");
        }
        return list;
    }

    /**
     * unregister the broadcast receiver and send message to GetCoordinates_Service telling it that
     * this activity is closing
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        Intent i = new Intent("Status");
        i.putExtra("Status", "false");
        sendBroadcast(i);
        Log.i(TAG, "Closing Status Sent");
    }
}