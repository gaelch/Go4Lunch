package com.cheyrouse.gael.go4lunch.models;

import java.io.Serializable;
import java.util.List;

public class Restaurant implements Serializable {

    private String restaurantUid;
    private String restaurantName;
    private double lat;
    private double lng;
    private List<String> rate;
    private List<String> users;

    public Restaurant(String restaurantUid, String restaurantName, double lat, double lng) {
        this.restaurantUid = restaurantUid;
        this.restaurantName = restaurantName;
        this.lat = lat;
        this.lng = lng;
    }

    public Restaurant(){}

    public String getRestaurantUid() {
        return restaurantUid;
    }

    public void setRestaurantUid(String restaurantUid) {
        this.restaurantUid = restaurantUid;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public List<String> getRate() {
        return rate;
    }

    public void setRate(List<String> rate) {
        this.rate = rate;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public double getLat() { return lat; }

    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }

    public void setLng(double lng) { this.lng = lng; }
}
