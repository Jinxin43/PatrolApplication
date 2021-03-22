package com.example.dingtu2.myapplication.http.Httpmodel;

/**
 * Created by Dingtu2 on 2017/6/27.
 */

public class HttpTraceModel {

    private String userId;
    private String roundId;
    private String latitude;
    private String longitude;
    private String height;
    private long gpsTime;
    private String x;
    private String y;
    private String srid;

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoundId() {
        return this.roundId;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public long getGpsTime() {
        return this.gpsTime;
    }

    public void setGpsTime(long gpsTime) {
        this.gpsTime = gpsTime;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getSrid() {
        return this.srid;
    }

    public void setSrid(String srid) {
        this.srid = srid;
    }
}
