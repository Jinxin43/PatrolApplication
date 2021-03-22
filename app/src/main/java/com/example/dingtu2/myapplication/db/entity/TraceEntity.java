package com.example.dingtu2.myapplication.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.example.dingtu2.myapplication.model.Trace;

import java.util.Date;

/**
 * Created by Dingtu2 on 2017/6/9.
 */
@Entity(tableName = "traces", indices = {
        @Index(value = "roundID")
})
public class TraceEntity implements Trace {

    @PrimaryKey
    private long id;
    private String userID;
    private String roundID;
    private double latitude;
    private double longitude;
    private double height;
    private String x;
    private String y;
    private Date gpsTime;
    private Date saveTime;
    private int uploadStatus = 0;
    private String srid;

    public TraceEntity() {

    }

    public TraceEntity(Trace trace) {
        id = trace.getId();
        userID = trace.getUserID();
        roundID = trace.getRoundID();
        latitude = trace.getLatitude();
        longitude = trace.getLongitude();
        height = trace.getHeight();
        gpsTime = trace.getGpsTime();
        uploadStatus = trace.getUploadStatus();
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getUserID() {
        return this.userID;
    }

    @Override
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String getRoundID() {
        return this.roundID;
    }

    @Override
    public void setRoundID(String roundID) {
        this.roundID = roundID;
    }

    @Override
    public double getLatitude() {
        return this.latitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public Date getGpsTime() {
        return this.gpsTime;
    }

    @Override
    public void setGpsTime(Date gpsTime) {
        this.gpsTime = gpsTime;
    }

    @Override
    public int getUploadStatus() {
        return this.uploadStatus;
    }

    @Override
    public void setUploadStatus(int status) {
        this.uploadStatus = status;
    }

    @Override
    public Date getSaveTime() {
        return this.saveTime;
    }

    @Override
    public void setSaveTime(Date saveTime) {
        this.saveTime = saveTime;
    }

    @Override
    public String getX() {
        return x;
    }

    @Override
    public void setX(String x) {
        this.x = x;
    }

    @Override
    public String getY() {
        return y;
    }

    @Override
    public void setY(String y) {
        this.y = y;
    }

    @Override
    public String getSrid() {
        return this.srid;
    }

    @Override
    public void setSrid(String srid) {
        this.srid = srid;
    }
}
