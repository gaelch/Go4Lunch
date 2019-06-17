package com.cheyrouse.gael.go4lunch.models;

import java.util.List;

public class Restaurant {

    private String restaurantUid;
    private String restaurantName;
    private int rate;
    private List<User> users;

    public Restaurant(String restaurantUid, String restaurantName) {
        this.restaurantUid = restaurantUid;
        this.restaurantName = restaurantName;
    }

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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
