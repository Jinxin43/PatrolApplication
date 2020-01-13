package com.example.dingtu2.myapplication.db.xEntity;

import com.example.dingtu2.myapplication.model.Round;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Dingtu2 on 2018/4/16.
 */
@Table(name = "PatrolEntity")
public class PatrolEntity implements Round {

    @Column(name = "id", isId = true)
    private String id;
    @Column(name = "serverId")
    private String serverId;
    @Column(name = "userID")
    private String userID;
    @Column(name = "roundName")
    private String roundName;
    @Column(name = "roundType")
    private int roundType;
    @Column(name = "startTime")
    private Date startTime;
    @Column(name = "endTime")
    private Date endTime;
    @Column(name = "roundStatus")
    private int roundStatus = 0;
    @Column(name = "photos")
    private String photos;
    @Column(name = "summary")
    private String summary;
    @Column(name = "reply")
    private String reply;
    @Column(name = "replyUserName")
    private String replyUserName;
    @Column(name = "replyTime")
    private Date replyTime;
    @Column(name = "uploadStatus")
    private int uploadStatus = 0;
    @Column(name = "dutyId")
    private String dutyId;
    @Column(name = "weather")
    private String weather;
    @Column(name = "userNames")
    private String userNames;

    @Column(name = "content")
    private String content;
    @Column(name = "lineOrZone")
    private String lineOrZone;
    @Column(name = "gemoetryId")
    private int geometryId;

    private PatrolPointEntity startPoint;
    private PatrolPointEntity endPoint;
    private TraceEntity lastTrace;

    public PatrolEntity() {

    }

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

    public String getDutyId() {
        return this.dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    public String getWeather() {
        return this.weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getUserNames() {
        return this.userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLineOrZone() {
        return this.lineOrZone;
    }

    public void setLineOrZone(String lineOrZone) {
        this.lineOrZone = lineOrZone;
    }


    public int getGeometryId() {
        return this.geometryId;
    }

    public void setGeometryId(int geometryId) {
        this.geometryId = geometryId;
    }

    public PatrolPointEntity getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(PatrolPointEntity pointEntity) {
        this.startPoint = pointEntity;
    }

    public PatrolPointEntity getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PatrolPointEntity pointEntity) {
        this.endPoint = pointEntity;
    }


    public TraceEntity getLastTrace() {
        return lastTrace;
    }

    public void setLastTrace(TraceEntity traceEntity) {
        this.lastTrace = traceEntity;
    }

}
