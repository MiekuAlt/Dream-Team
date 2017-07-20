package com.wolkabout.hexiwear.model;

/**
 * Created by Justin on 6/27/2017.
 */

/**
 * Route object used to send and receive values form Firebase
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
