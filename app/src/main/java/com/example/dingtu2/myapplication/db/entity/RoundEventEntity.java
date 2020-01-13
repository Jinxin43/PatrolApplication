package com.example.dingtu2.myapplication.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.example.dingtu2.myapplication.model.RoundEvent;

import java.util.Date;

/**
 * Created by Dingtu2 on 2017/6/22.
 */

@Entity(tableName = "roundEvents", indices = {
        @Index(value = "id")
})
public class RoundEventEntity implements RoundEvent {

    @PrimaryKey
    private String id;
    private String serverId;
    private String roundId;
    private String eventName;
    private String eventDescription;
    private String eventPOI;
    private double eventLat;
    private double eventLon;
    private Date eventTime;
    private String eventPhotos;
    private int uploadStatus;
    private int eventType;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getServerId() {
        return this.serverId;
    }

    @Override
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String getRoundId() {
        return this.roundId;
    }

    @Override
    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String getEventDescription() {
        return this.eventDescription;
    }

    @Override
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    @Override
    public String getEventPOI() {
        return this.eventPOI;
    }

    @Override
    public void setEventPOI(String eventPOI) {
        this.eventPOI = eventPOI;
    }

    @Override
    public Date getEventTime() {
        return this.eventTime;
    }

    @Override
    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public double getEventLon() {
        return this.eventLon;
    }

    @Override
    public void setEventLon(double eventLon) {
        this.eventLon = eventLon;
    }

    @Override
    public double getEventLat() {
        return this.eventLat;
    }

    @Override
    public void setEventLat(double eventLat) {
        this.eventLat = eventLat;
    }

    @Override
    public String getEventPhotos() {
        return this.eventPhotos;
    }

    @Override
    public void setEventPhotos(String eventPhotos) {
        this.eventPhotos = eventPhotos;
    }

    @Override
    public int getEventType() {
        return this.eventType;
    }

    @Override
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    @Override
    public int getUploadStatus() {
        return this.uploadStatus;
    }

    @Override
    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
}
