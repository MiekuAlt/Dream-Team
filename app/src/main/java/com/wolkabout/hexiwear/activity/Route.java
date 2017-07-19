package com.wolkabout.hexiwear.activity;

/**
 * Created by Justin on 6/27/2017.
 */

/**
 * distance object used to receive distance form firebase
 */
public class Route {
    private String route;
    private String distance;
    public Route(){}
    public Route(String route, String distance){
        this.route = route;
        this.distance = distance;
    }
    public String getRoute(){return route;}
    public String getDistance(){return  distance;}
}
