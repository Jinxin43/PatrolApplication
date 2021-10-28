package com.example.dingtu2.myapplication.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dingtu2.myapplication.db.entity.RoundEntity;

/**
 * Created by Dingtu2 on 2017/6/15.
 */

@Dao
public interface RoundDao {

    @Query("SELECT * FROM rounds where id=:roundID")
    LiveData<RoundEntity> getRound(String roundID);

    @Query("SELECT * FROM rounds where userid=:userKey and roundstatus = 0 Order By startTime DESC")
    LiveData<RoundEntity> getGoingRound(String userKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRound(RoundEntity round);

    @Query("SELECT * FROM rounds where uploadStatus=0 and roundStatus=1")
    LiveData<RoundEntity> getUnloadRound();

}
