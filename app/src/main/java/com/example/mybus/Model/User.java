package com.example.mybus.Model;

public
class User {
    double currentLat=30.0428794  , currentLng=31.0058576;
    CustomerLocation user2;



    public User() {
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public CustomerLocation getUser2() {
        return user2;
    }

    public void setUser2(CustomerLocation user2) {
        this.user2 = user2;
    }
}
