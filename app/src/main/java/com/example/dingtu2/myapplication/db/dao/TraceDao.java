package com.example.dingtu2.myapplication.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
