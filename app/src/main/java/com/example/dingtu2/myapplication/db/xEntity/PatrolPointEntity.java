package com.example.dingtu2.myapplication.db.xEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Dingtu2 on 2018/4/24.
 */

@Table(name = "PatrolPoint")
public class PatrolPointEntity {
    @Column(name = "id", isId = true, autoGen = true)
    private long id;
    @Column(name = "userID")
    private String userID;
    @Column(name = "roundID")
    private String roundID;
    @Column(name = "latitude")
    private double latitude;
    @Column(name = "longitude")
    private double longitude;
    @Column(name = "pointName")
    private String pointName;
    @Column(name = "X")
    private String x;
    @Column(name = "Y")
    private String y;
    private String srid;
    @Column(name = "height")
    private double height;
    @Column(name = "gpsTime")
    private Date gpsTime;
    @Column(name = "saveTime")
    private Date saveTime;
    @Column(name = "uploadStatus")
    private int uploadStatus = 0;
    @Column(name = "pointType")
    private String pointType;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRoundID() {
        return this.roundID;
    }

    public void setRoundID(String roundID) {
        this.roundID = roundID;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return this.longitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getPointName() {
        return this.pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }


    public Date getGpsTime() {
        return this.gpsTime;
    }

    public void setGpsTime(Date gpsTime) {
        this.gpsTime = gpsTime;
    }

    public int getUploadStatus() {
        return this.uploadStatus;
    }

    public void setUploadStatus(int status) {
        this.uploadStatus = status;
    }

    public Date getSaveTime() {
        return this.saveTime;
    }

    public void setSaveTime(Date saveTime) {
        this.saveTime = saveTime;
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

    public String getPointType() {
        return this.pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }
}
