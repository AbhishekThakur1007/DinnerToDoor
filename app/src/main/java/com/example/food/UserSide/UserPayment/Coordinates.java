package com.example.food.UserSide.UserPayment;

public class Coordinates {

    String Longitude,Latitude;

    public Coordinates(){}

    public Coordinates(String longitude, String latitude) {
        this.Longitude = longitude;
        this.Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

}
