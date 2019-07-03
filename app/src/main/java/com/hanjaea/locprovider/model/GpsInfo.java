package com.hanjaea.locprovider.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GpsInfo {
    private static final long serialVersionUID = -8215780609748500123L;

    @JsonProperty("latitude")
    public String latitude;              // TEXT 위도

    @JsonProperty("longitude")
    public String longitude;            // TEXT 경도

    @JsonProperty("up_dt")
    private String up_dt;            // TEXT GPS 수신날짜시간

    //@androidx.annotation.NonNull
    @Override
    public String toString() {
        return super.toString();
    }


    /**
     * 생성자
     * @param latitude
     * @param longitude
     * @param up_dt
     */
    public GpsInfo(String latitude, String longitude, String up_dt){
        this.latitude = latitude;
        this.longitude = longitude;
        this.up_dt = up_dt;
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
