package com.example.dingtu2.myapplication.db.xEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Dingtu2 on 2018/6/27.
 */

@Table(name = "OneWarning")
public class OneWarningEntity {

    @Column(name = "id", isId = true, autoGen = true)
    private long id;
    @Column(name = "userName")
    private String userName;
    @Column(name = "latitude")
    private double latitude;
    @Column(name = "longitude")
    private double longitude;
    @Column(name = "title")
    private String title;
    @Column(name = "X")
    private double x;
    @Column(name = "Y")
    private double y;
    private String srid;
    @Column(name = "height")
    private double height;
    @Column(name = "warningtime")
    private Date warningtime;
    @Column(name = "uploadStatus")
    private int uploadStatus = 0;
    @Column(name = "description")
    private String description;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userID) {
        this.userName = userID;
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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Date getWarningtime() {
        return this.warningtime;
    }

    public void setWarningtime(Date warningtime) {
        this.warningtime = warningtime;
    }

    public int getUploadStatus() {
        return this.uploadStatus;
    }

    public void setUploadStatus(int status) {
        this.uploadStatus = status;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
