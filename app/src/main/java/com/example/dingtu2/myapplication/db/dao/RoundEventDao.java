package com.example.dingtu2.myapplication.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dingtu2.myapplication.db.entity.RoundEventEntity;

/**
 * Created by Dingtu2 on 2017/6/22.
 */

@Dao
public interface RoundEventDao {

    @Query("SELECT * FROM roundEvents where id=:roundEventID")
    LiveData<RoundEventEntity> getRoundEventEntity(String roundEventID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoundEvent(RoundEventEntity event);
}
