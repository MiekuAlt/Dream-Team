package com.wolkabout.hexiwear.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.wolkabout.hexiwear.R;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Google Maps activity that allows the user to track their route in real-time on the map
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Coordinates");
    ArrayList<LatLng> coordinateList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        coordinateList= stringToArray(sharedPreferences.getString("coordinates",""));

        historicLineOptions = new PolylineOptions().color(Color.BLUE).width(20);
        updateLineOptions = new PolylineOptions().color(Color.GREEN).width(20);
        send_Button = (Button) findViewById(R.id.button_send);
        //coordinateList = new ArrayList<>();
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
        //mMap.addMarker(new MarkerOptions().position(halifax).title("Marker in Halifax"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(halifax,13));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                Coordinates coordinates = dataSnapshot.getValue(Coordinates.class);
                addCoordinatesToPolyLine(coordinates);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        //allows user to create custom routes on Map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(isMakingRoute)
                    addRouteMarker(latLng);
            }
        });
    }
    public void clearRoutPolylines(){

        routeLineOptions = new PolylineOptions();
        distance = 0;
        mostReventMarker = null;
        sMostRecentMarker = null;
        for(Marker m: markers)
            m.remove();
        if(routeLine != null) {
            routeLine.remove();
            routeLine = null;
        }
    }
    //clears the map
    public void clearMap(View view){
        if(isMakingRoute) {
            clearRoutPolylines();
            send_Button.setVisibility(View.INVISIBLE);
        }
        else
            mMap.clear();
    }
    /**
     * used to set the route and disable editing
     * @param view
     */
    public void sendRoute(View view){
        if(markers.size() >0) {
            for (Marker m : markers)
                m.remove();
            routeLineOptions.color(Color.GREEN).width(20);
            routeLine.remove();
            routeLine = mMap.addPolyline(routeLineOptions);
            send_Button.setVisibility(View.INVISIBLE);
            isMakingRoute = false;
        }
    }
    public void makeRoute(View view){
        isMakingRoute = true;
        clearRoutPolylines();
    }
    /**
     * adds marker to the map that the user selected and updates the polyline and the distance ignoring altitude values
     * @param latLng
     */
    private void addRouteMarker(LatLng latLng) {
        send_Button.setVisibility(View.VISIBLE);
        if(mostReventMarker == null) {
            mostReventMarker = latLng;
            //mMap.addMarker(new MarkerOptions().position(latLng).title("My Click").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).setSnippet(distance+"");
        }
        else{
            sMostRecentMarker = mostReventMarker;
            mostReventMarker = latLng;
            //estimates the distance between the two coordinates without using altitude
            distance += UploadGPS_Service.distance(
                    new Coordinates(mostReventMarker.longitude+"", mostReventMarker.latitude+"", 0.0+""),
                    new Coordinates(sMostRecentMarker.longitude+"", sMostRecentMarker.latitude+"", 0.0+""));
        }
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Distance").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        marker.setSnippet(Math.floor(distance)/1000+" KM");
        marker.showInfoWindow();
        markers.add(marker);
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
        //mMap.addMarker(new MarkerOptions().position(coordinates.toLatLng()));
        coordinateList.add(coordinates.toLatLng());
        mMap.addMarker(new MarkerOptions().position(coordinates.toLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.hello_kitty)));
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
            updateLineOptions = new PolylineOptions().add(sMostRecent).add(mostRecent).color(Color.GREEN).width(20);
        }
        historicLine = mMap.addPolyline(historicLineOptions);
        updatetLine = mMap.addPolyline(updateLineOptions);
    }
    protected void onResume() {
        super.onResume();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Coordinates coordinates = dataSnapshot.getValue(Coordinates.class);
                mMap.addMarker(new MarkerOptions().position(coordinates.toLatLng()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    /**
     * Converts the current list of displayed coordinates to a string so that they can be
     * retained after the app has been closed;
     * @return
     */
    private String arrayToString(){
        String result = "";
        for(LatLng l: coordinateList){
            result += l.longitude + " " + l.latitude + " ";
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
        }
        return list;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("coordinates", arrayToString());
        editor.commit();
        //Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_LONG).show();

    }
}
