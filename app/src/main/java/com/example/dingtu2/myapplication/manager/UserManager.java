package com.example.dingtu2.myapplication.manager;

import android.util.Log;
import android.widget.Toast;

import com.example.dingtu2.myapplication.AppSetting;
import com.example.dingtu2.myapplication.db.XDbManager;
import com.example.dingtu2.myapplication.db.xEntity.DutyAreaEntity;
import com.example.dingtu2.myapplication.db.xEntity.UserEntity;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2018/4/21.
 */

public class UserManager {

    private static UserManager mInstance;
    public UserEntity currentUser;

    private UserManager() {
    }

    public static UserManager getInstance() {
        synchronized (UserManager.class) {
            if (mInstance == null) {
                mInstance = new UserManager();
            }
        }

        return mInstance;
    }

    public UserEntity getLoginUser(String userKey) {
        //TODO:update user infomation from server

        currentUser = null;
        try {
            currentUser = XDbManager.getDb().selector(UserEntity.class).where("loginName", "=", userKey).findFirst();
        } catch (Exception ex) {
            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "查询登录用户失败：" + ex.getMessage(), Toast.LENGTH_SHORT);
        }

        return currentUser;
    }

    public boolean SaveLoginUser(UserEntity user) {
        boolean result = true;
        try {
            XDbManager.getDb().saveOrUpdate(user);
        } catch (Exception ex) {
            result = false;
            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "保存用户失败：" + ex.getMessage(), Toast.LENGTH_SHORT);
        }

        return result;
    }

    public List<UserEntity> getAllLoginedUsers() {
        List<UserEntity> mUsers = new ArrayList<UserEntity>();
        try {
//            mUsers = XDbManager.getDb().selector(UserEntity.class).where("isValid","=",1).orderBy("lastLoginTime",true).findAll();
            mUsers = XDbManager.getDb().selector(UserEntity.class).findAll();
        } catch (Exception ex) {
            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "查询用户失败：" + ex.getMessage(), Toast.LENGTH_SHORT);
        }

        return mUsers;
    }

    public boolean saveDutyArea(DutyAreaEntity dutyAreaEntity) {
        try {
            XDbManager.getDb().saveOrUpdate(dutyAreaEntity);
            return true;
        } catch (DbException ex) {
            Log.e("saveDutyArea", ex.getMessage());
            return false;
        }

    }
}
