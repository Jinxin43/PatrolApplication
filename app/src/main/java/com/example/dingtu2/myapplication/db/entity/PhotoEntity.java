package com.example.dingtu2.myapplication.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.example.dingtu2.myapplication.model.Photo;

/**
 * Created by Dingtu2 on 2017/7/6.
 */

@Entity(tableName = "photos", indices = {
        @Index(value = "photoId")
})
public class PhotoEntity implements Photo {


    @PrimaryKey(autoGenerate = true)
    private int photoId;
    private String photoName;
    private String photoType;
    private String relatedId;
    private String photoPath;
    private String exitInfo;
    private int uploadStatus;

    @Override
    public int getPhotoId() {
        return photoId;
    }

    @Override
    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    @Override
    public String getPhotoName() {
        return this.photoName;
    }

    @Override
    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    @Override
    public String getPhotoType() {
        return this.photoType;
    }

    @Override
    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    @Override
    public String getRelatedId() {
        return this.relatedId;
    }

    @Override
    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    @Override
    public String getPhotoPath() {
        return this.photoPath;
    }

    @Override
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public String getExitInfo() {
        return this.exitInfo;
    }

    @Override
    public void setExitInfo(String exitInfo) {
        this.exitInfo = exitInfo;
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
