package com.example.dingtu2.myapplication.model;

import java.util.Date;

/**
 * Created by Dingtu2 on 2017/6/22.
 */

public interface RoundEvent {
    String getId();

    void setId(String id);

    String getServerId();

    void setServerId(String serverId);

    String getRoundId();

    void setRoundId(String roundId);

    String getEventName();

    void setEventName(String eventName);

    String getEventDescription();

    void setEventDescription(String eventDescription);

    String getEventPOI();

    void setEventPOI(String eventPOI);

    Date getEventTime();

    void setEventTime(Date eventTime);

    double getEventLon();

    void setEventLon(double eventLon);

    double getEventLat();

    void setEventLat(double eventLat);

    String getEventPhotos();

    void setEventPhotos(String eventPhotos);

    int getUploadStatus();

    void setUploadStatus(int uploadStatus);

    int getEventType();

    void setEventType(int eventType);
}
