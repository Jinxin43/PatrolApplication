package com.example.dingtu2.myapplication.http.Httpmodel;

/**
 * Created by Dingtu2 on 2017/6/26.
 */

public class HttpRoundModel {


    private String userId;
    private String roundName;
    private int roundType;
    private long startTime;
    private long endTime;
    private int roundStatus;
    private String description;
    private String weather;
    private String dutyId;
    private String userNames;
    private String content;
    private String lineOrZone;


    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoundName() {
        return this.roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public int getRoundType() {
        return this.roundType;
    }

    public void setRoundType(int routeType) {
        this.roundType = routeType;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getRoundStatus() {
        return this.roundStatus;
    }

    public void setRoundStatus(int status) {
        this.roundStatus = status;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeather() {
        return this.weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDutyId() {
        return this.dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }


    public String getUserNames() {
        return this.getUserNames();
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLineOrZone() {
        return this.lineOrZone;
    }

    public void setLineOrZone(String lineOrZone) {
        this.lineOrZone = lineOrZone;
    }


}
