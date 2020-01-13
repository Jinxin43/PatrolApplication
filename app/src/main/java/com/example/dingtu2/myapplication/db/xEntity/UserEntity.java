package com.example.dingtu2.myapplication.db.xEntity;

import com.example.dingtu2.myapplication.model.User;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Dingtu2 on 2018/4/18.
 */
@Table(name = "User")
public class UserEntity implements User {

    @Column(name = "userID")
    private String userID;
    @Column(name = "loginName", isId = true)
    private String loginName;
    @Column(name = "password")
    private String password;
    @Column(name = "userName")
    private String userName;
    @Column(name = "mobilePhone")
    private String mobilePhone;
    @Column(name = "organization")
    private String organization;
    @Column(name = "scope")
    private String scope;
    @Column(name = "avator")
    private String avator;
    @Column(name = "lastLoginTime")
    private Date lastLoginTime;
    @Column(name = "inProgress")
    private int inProgress;
    @Column(name = "isValid")
    private int isValid = 1;
    @Column(name = "post")
    private String post;
    @Column(name = "postDiscription")
    private String postDiscription;
    @Column(name = "departmentId")
    private String departmentId;


    public UserEntity() {
    }

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

    public String getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getPostDiscription() {
        return this.getPostDiscription();
    }

    public void setPostDiscription(String postDiscription) {
        this.postDiscription = postDiscription;
    }
}
