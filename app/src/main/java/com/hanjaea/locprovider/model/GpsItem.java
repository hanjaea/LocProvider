package com.hanjaea.locprovider.model;

public class GpsItem {
    int id;
    String latitude;
    String longitude;
    String up_dt;

    public GpsItem(){}

    public GpsItem(int id, String latitude, String longitude, String up_dt){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.up_dt = up_dt;
    }

    public int getId() {
        return id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUp_dt() {
        return up_dt;
    }
}
