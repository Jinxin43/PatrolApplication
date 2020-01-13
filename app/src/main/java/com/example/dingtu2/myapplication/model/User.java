package com.example.dingtu2.myapplication.model;

import java.util.Date;

/**
 * Created by Dingtu2 on 2017/6/9.
 */

public interface User {
    String getUserID();

    void setUserID(String userID);

    String getLoginName();

    void setLoginName(String loginName);

    String getPassword();

    void setPassword(String password);

    String getUserName();

    void setUserName(String userName);

    String getMobilePhone();

    void setMobilePhone(String phoneNumber);

    String getOrganization();

    void setOrganization(String organization);

    String getScope();

    void setScope(String scope);

    String getAvator();

    void setAvator(String avatar);

    Date getLastLoginTime();

    void setLastLoginTime(Date loginTime);

    int getInProgress();

    void setInProgress(int inProgress);

    int getIsValid();

    void setIsValid(int isValid);

    String getPost();

    void setPost(String post);
}
