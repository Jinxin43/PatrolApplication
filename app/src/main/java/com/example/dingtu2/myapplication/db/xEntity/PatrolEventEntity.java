package com.example.dingtu2.myapplication.db.xEntity;

import com.example.dingtu2.myapplication.model.RoundEvent;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Dingtu2 on 2018/4/16.
 */

@Table(name = "PatrolEventEntity")
public class PatrolEventEntity implements RoundEvent {

    @Column(name = "id", isId = true)
    private String id;
    @Column(name = "serverId")
    private String serverId;
    @Column(name = "roundId")
    private String roundId;
    @Column(name = "eventName")
    private String eventName;
    @Column(name = "description")
    private String eventDescription;
    @Column(name = "eventPOI")
    private String eventPOI;
    @Column(name = "eventLat")
    private double eventLat;
    @Column(name = "eventLon")
    private double eventLon;
    @Column(name = "altitude")
    private double altitude;
    @Column(name = "eventTime")
    private Date eventTime;
    @Column(name = "photos")
    private String eventPhotos;
    @Column(name = "uploadStatus")
    private int uploadStatus;
    @Column(name = "eventType")
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

    public double getAltitude() {
        return this.altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
