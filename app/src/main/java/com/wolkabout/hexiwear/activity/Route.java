package com.wolkabout.hexiwear.activity;

/**
 * Created by Justin on 6/27/2017.
 */

/**
 * distance object used to receive distance form firebase
 */
public class Route {
    private String route;
    public Route(){}
    public Route(String route){this.route = route;}
    public String getRoute(){return route;}
}
