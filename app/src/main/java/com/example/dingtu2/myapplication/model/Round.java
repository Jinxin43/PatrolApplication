package com.example.dingtu2.myapplication.model;

import java.util.Date;

/**
 * 巡护实体类接口
 * Created by Dingtu2 on 2017/6/15.
 */

public interface Round {

    String getId();

    void setId(String id);

    String getServerId();

    void setServerId(String serverId);

    String getUserID();

    void setUserID(String userID);

    String getRoundName();

    void setRoundName(String roundName);

    int getRoundType();

    void setRoundType(int roundType);

    Date getStartTime();

    void setStartTime(Date startTime);

    Date getEndTime();

    void setEndTime(Date endTime);

    int getRoundStatus();

    void setRoundStatus(int status);

    String getPhotos();

    void setPhotos(String photos);

    String getSummary();

    void setSummary(String summary);

    String getReply();

    void setReply(String reply);

    String getReplyUserName();

    void setReplyUserName(String replyUserName);

    Date getReplyTime();

    void setReplyTime(Date replyTime);

    int getUploadStatus();

    void setUploadStatus(int uploadStatus);

    String getDutyId();

    void setDutyId(String dutyId);

    String getWeather();

    void setWeather(String weather);

}
