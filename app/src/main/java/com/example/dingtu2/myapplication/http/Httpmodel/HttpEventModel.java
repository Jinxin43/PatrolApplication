package com.example.dingtu2.myapplication.http.Httpmodel;

/**
 * Created by Dingtu2 on 2017/6/28.
 */

public class HttpEventModel {

    private String roundId;
    private String title;
    private String description;
    private String eventPOI;
    private String latitude;
    private String longitude;
    private long eventTime;
    private long gpsTime;
    private String userId;
    private String type;
    private String height;
    private String srid;


    public String getRoundId() {
        return this.roundId;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventPOI() {
        return this.eventPOI;
    }

    public void setEventPOI(String eventPOI) {
        this.eventPOI = eventPOI;
    }

    public long getGpsTime() {
        return this.gpsTime;
    }

    public void setGpsTime(long eventTime) {
        this.gpsTime = eventTime;
    }

    public long getEventTime() {
        return this.eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSrid() {
        return srid;
    }

    public void setSrid(String srid) {
        this.srid = srid;
    }
}
