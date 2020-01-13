package com.example.dingtu2.myapplication.db.entity;

/**
 * Created by Dingtu2 on 2017/11/22.
 */

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "patrolCommand")
public class PatrolCommandEntity {

    @Column(name = "id", isId = true, autoGen = true)
    int Id;

    @Column(name = "title")
    String title;

    @Column(name = "userId")
    String userId;

    @Column(name = "geomId")
    String geomId;

    @Column(name = "patrolDescription")
    String patrolDescription;

    @Column(name = "geomType")
    String geomType;

    @Column(name = "sys_geo")
    byte[] geomByte;

    @Column(name = "sendType")
    String sendType;


    @Column(name = "creatorUser")
    String creatorUser;

    @Column(name = "startTime")
    String startTime;

    @Column(name = "endTime")
    String endTime;

    @Column(name = "readStatus")
    Boolean readStatus;

    @Column(name = "createTime")
    String createTime;


    @Column(name = "attachments")
    String attachments;

    public int getId() {
        return this.Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGeomId() {
        return this.geomId;
    }

    public void setGeomId(String geomId) {
        this.geomId = geomId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSendType() {
        return this.sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public byte[] getGeomByte() {
        return this.geomByte;
    }

    public void setGeomByte(byte[] geomByte) {
        this.geomByte = geomByte;
    }


    public String getPatrolDescription() {
        return this.patrolDescription;
    }

    public void setPatrolDescription(String patrolDescription) {
        this.patrolDescription = patrolDescription;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean getReadStatus() {
        return this.readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGeomType() {
        return this.geomType;
    }

    public void setGeomType(String geomType) {
        this.geomType = geomType;
    }

    public String getAttachments() {
        return this.attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getCreatorUser() {
        return this.creatorUser;
    }

    public void setCreatorUser(String creatorUser) {
        this.creatorUser = creatorUser;
    }
}
