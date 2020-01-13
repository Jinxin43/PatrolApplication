package com.example.dingtu2.myapplication.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.example.dingtu2.myapplication.model.Round;

import java.util.Date;

/**
 * 巡护实体类
 * Created by Dingtu2 on 2017/6/15.
 */

@Entity(tableName = "rounds", indices = {
        @Index(value = "id")
})
public class RoundEntity implements Round {
    @PrimaryKey
    private String id;
    private String serverId;
    private String userID;
    private String roundName;
    private int roundType;
    private Date startTime;
    private Date endTime;
    private int roundStatus;
    private String photos;
    private String summary;
    private String reply;
    private String replyUserName;
    private Date replyTime;
    private int uploadStatus;
    private String dutyId;
    private String weather;


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getServerId() {
        return this.serverId;
    }

    @Override
    public void setServerId(String serverId) {
        this.serverId = serverId;
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
    public String getRoundName() {
        return this.roundName;
    }

    @Override
    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    @Override
    public int getRoundType() {
        return this.roundType;
    }

    @Override
    public void setRoundType(int routeType) {
        this.roundType = routeType;
    }

    @Override
    public Date getStartTime() {
        return this.startTime;
    }

    @Override
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public Date getEndTime() {
        return this.endTime;
    }

    @Override
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public int getRoundStatus() {
        return this.roundStatus;
    }

    @Override
    public void setRoundStatus(int status) {
        this.roundStatus = status;
    }

    @Override
    public String getPhotos() {
        return this.photos;
    }

    @Override
    public void setPhotos(String photos) {
        this.photos = photos;
    }

    @Override
    public String getSummary() {
        return this.summary;
    }

    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String getReply() {
        return this.reply;
    }

    @Override
    public void setReply(String reply) {
        this.reply = reply;
    }

    @Override
    public String getReplyUserName() {
        return this.replyUserName;
    }

    @Override
    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    @Override
    public Date getReplyTime() {
        return this.replyTime;
    }

    @Override
    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    @Override
    public int getUploadStatus() {
        return this.uploadStatus;
    }

    @Override
    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    @Override
    public String getDutyId() {
        return this.dutyId;
    }

    @Override
    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    @Override
    public String getWeather() {
        return this.weather;
    }

    @Override
    public void setWeather(String weather) {
        this.weather = weather;
    }
}
