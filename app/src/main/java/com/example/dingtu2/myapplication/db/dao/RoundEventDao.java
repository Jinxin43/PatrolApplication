package com.example.dingtu2.myapplication.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
