package com.example.dingtu2.myapplication.db.dao;

/**
 * Created by Dingtu2 on 2017/6/9.
 */


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
