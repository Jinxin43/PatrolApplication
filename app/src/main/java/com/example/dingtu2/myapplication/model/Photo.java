package com.example.dingtu2.myapplication.model;

/**
 * Created by Dingtu2 on 2017/7/6.
 */

public interface Photo {

    int getPhotoId();

    void setPhotoId(int photoId);

    String getPhotoName();

    void setPhotoName(String photoName);

    String getPhotoType();

    void setPhotoType(String photoType);

    String getRelatedId();

    void setRelatedId(String relatedId);

    String getPhotoPath();

    void setPhotoPath(String photoPath);

    String getExitInfo();

    void setExitInfo(String exitInfo);

    int getUploadStatus();

    void setUploadStatus(int uploadStatus);

}
