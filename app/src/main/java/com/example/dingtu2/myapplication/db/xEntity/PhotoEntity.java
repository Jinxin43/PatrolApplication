package com.example.dingtu2.myapplication.db.xEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Dingtu2 on 2019/3/20.
 */

@Table(name = "Photo")
public class PhotoEntity {

    @Column(name = "id", isId = true, autoGen = true)
    private int id;

    @Column(name = "userID")
    private String userID;

    @Column(name = "photoName")
    private String photoName;

    @Column(name = "belongTo")
    private String belongTo;

    @Column(name = "photoType")
    private String photoType;

    @Column(name = "saveTime")
    private Date saveTime;

    //0为未上传
    @Column(name = "uploadStatus")
    private int uploadStatus = 0;

    @Column(name = "uploadTime")
    private Date uploadTime;

    public int getId() {
        return id;
    }

    public String getPhotoName() {
        return this.photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoType() {
        return this.photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    public String getBelongTo() {
        return this.belongTo;
    }

    public void setBelongTo(String parentId) {
        this.belongTo = parentId;
    }

    public String getUserID() {

        return this.userID;
    }

    public void setUserID(String userID) {

        this.userID = userID;
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

    public Date getUploadTime() {
        return this.uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }
}
