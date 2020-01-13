package com.example.dingtu2.myapplication.db.xEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Dingtu2 on 2018/5/11.
 */

@Table(name = "DutyArea")
public class DutyAreaEntity {
    @Column(name = "id", isId = true)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "DepId")
    private String DepId;
    @Column(name = "DutyUserNames")
    private String DutyUserNames;
    @Column(name = "Dutymanager")
    private String Dutymanager;
    @Column(name = "Description")
    private String Description;
    @Column(name = "updateTime")
    private Date updateTime;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDutymanager() {
        return this.Dutymanager;
    }

    public void setDutymanager(String dutymanager) {
        this.Dutymanager = dutymanager;
    }

    public String getDepId() {
        return this.DepId;
    }

    public void setDepId(String depId) {
        this.DepId = depId;
    }

    public String getDutyUserNames() {
        return this.DutyUserNames;
    }

    public void setDutyUserNames(String dutyUserNames) {
        this.DutyUserNames = dutyUserNames;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date date) {
        this.updateTime = date;
    }


}
