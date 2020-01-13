package com.example.dingtu2.myapplication.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.example.dingtu2.myapplication.model.User;

import java.util.Date;

/**
 * Created by Dingtu2 on 2017/6/9.
 */

@Entity(tableName = "users", indices = {
        @Index(value = "loginName")
})
public class UserEntity implements User {
    private String userID;
    @PrimaryKey
    private String loginName;
    private String password;
    private String userName;
    private String mobilePhone;
    private String organization;
    private String scope;
    private String avator;
    private Date lastLoginTime;
    private int inProgress;
    private int isValid;
    private String post;


    public UserEntity(String loginName, String password) {
        this.loginName = loginName;
        this.password = password;
    }

    @Override
    public String getUserID() {

        return userID;
    }

    @Override
    public void setUserID(String userID) {

        this.userID = userID;
    }

    @Override
    public String getLoginName() {
        return loginName;
    }

    @Override
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String getPassword() {

        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUserName() {

        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getMobilePhone() {
        return mobilePhone;
    }

    @Override
    public void setMobilePhone(String phoneNumber) {
        this.mobilePhone = phoneNumber;
    }

    @Override
    public String getOrganization() {
        return organization;
    }

    @Override
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String getAvator() {
        return this.avator;
    }

    @Override
    public void setAvator(String avatar) {
        this.avator = avatar;
    }

    @Override
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    @Override
    public void setLastLoginTime(Date loginTime) {
        this.lastLoginTime = loginTime;
    }

    @Override
    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    @Override
    public int getIsValid() {
        return isValid;
    }

    @Override
    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    @Override
    public String getPost() {
        return post;
    }

    @Override
    public void setPost(String post) {
        this.post = post;
    }
}
