package com.example.dingtu2.myapplication.db.xEntity;

import com.example.dingtu2.myapplication.model.Trace;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Dingtu2 on 2018/4/16.
 */

@Table(name = "Traces")
public class TraceEntity implements Trace {

    @Column(name = "id", isId = true, autoGen = true)
    private long id;
    @Column(name = "userID")
    private String userID;
    @Column(name = "roundID")
    private String roundID;
    @Column(name = "serverRoundID")
    private String serverRoundId;
    @Column(name = "latitude")
    private double latitude;
    @Column(name = "longitude")
    private double longitude;
    @Column(name = "X")
    private String x;
    @Column(name = "Y")
    private String y;
    @Column(name = "srid")
    private String srid;
    @Column(name = "height")
    private double height;
    @Column(name = "gpsTime")
    private Date gpsTime;
    @Column(name = "saveTime")
    private Date saveTime;
    @Column(name = "uploadStatus")
    private int uploadStatus = 0;


    public TraceEntity() {

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

    public void setX(String x) {
        this.x = x;
    }

    @Override
    public String getY() {
        return y;
    }

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

    public String getServerRoundId() {
        return serverRoundId;
    }

    public void setServerRoundId(String serverRoundId) {
        this.serverRoundId = serverRoundId;
    }
}
