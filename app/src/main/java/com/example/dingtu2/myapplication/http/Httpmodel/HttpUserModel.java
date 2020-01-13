package com.example.dingtu2.myapplication.http.Httpmodel;

/**
 * Created by Dingtu2 on 2017/6/23.
 */

public class HttpUserModel {

    private String Account;
    private String Password;
    private String Device;
    private String nickName;
    private String groupId;

    public String getAccount() {
        return null;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String getDevice() {
        return this.Device;
    }

    public void setDevice(String device) {
        this.Device = device;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


}
