package com.example.dingtu2.myapplication.http.Httpmodel;

/**
 * Created by Dingtu2 on 2018/4/24.
 */

public class HttpPatrolPointModel {
    private String patrolId;
    private String latitude;
    private String longitude;
    private String height;
    private long gpsTime;
    private double x;
    private double y;
    private String srid;
    private String type;


    public String getPatrolId() {
        return this.patrolId;
    }

    public void setPatrolId(String patrolId) {
        this.patrolId = patrolId;
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

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getSrid() {
        return this.srid;
    }

    public void setSrid(String srid) {
        this.srid = srid;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
