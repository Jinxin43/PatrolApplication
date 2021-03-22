package com.example.dingtu2.myapplication.model;

import java.util.Date;

/**
 * Created by Dingtu2 on 2017/6/9.
 */

public interface Trace {
    long getId();

    void setId(long id);

    String getUserID();

    void setUserID(String userID);

    String getRoundID();

    void setRoundID(String roundID);

    double getLatitude();

    void setLatitude(double latitude);

    double getLongitude();

    void setLongitude(double longitude);

    double getHeight();

    void setHeight(double height);

    Date getGpsTime();

    void setGpsTime(Date gpsTime);

    Date getSaveTime();

    void setSaveTime(Date saveTime);

    int getUploadStatus();

    void setUploadStatus(int status);

    String getX();

    void setX(String x);

    String getY();

    void setY(String y);

    String getSrid();

    void setSrid(String srid);


}
