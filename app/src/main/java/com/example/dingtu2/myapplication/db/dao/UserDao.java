package com.example.dingtu2.myapplication.db.dao;

/**
 * Created by Dingtu2 on 2017/6/9.
 */

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.dingtu2.myapplication.db.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    LiveData<List<UserEntity>> loadLoginedUsers();

    @Query("SELECT * FROM users where loginName=:loginName")
    LiveData<UserEntity> loadUser(String loginName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);
}
