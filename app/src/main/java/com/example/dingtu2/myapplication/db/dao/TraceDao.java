package com.example.dingtu2.myapplication.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.dingtu2.myapplication.db.entity.TraceEntity;

import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/17.
 */

@Dao
public interface TraceDao {

    @Query("SELECT * FROM traces where roundID=:roundID")
    LiveData<List<TraceEntity>> getTraces(String roundID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTraces(TraceEntity trace);

    @Query("SELECT * FROM traces where uploadStatus=0 and id=:roundID")
    LiveData<List<TraceEntity>> getUnloadTraces(String roundID);
}
