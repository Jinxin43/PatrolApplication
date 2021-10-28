package com.example.dingtu2.myapplication.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.dingtu2.myapplication.db.dao.RoundDao;
import com.example.dingtu2.myapplication.db.dao.RoundEventDao;
import com.example.dingtu2.myapplication.db.dao.TraceDao;
import com.example.dingtu2.myapplication.db.dao.UserDao;
import com.example.dingtu2.myapplication.db.entity.RoundEntity;
import com.example.dingtu2.myapplication.db.entity.RoundEventEntity;
import com.example.dingtu2.myapplication.db.entity.TraceEntity;
import com.example.dingtu2.myapplication.db.entity.UserEntity;

/**
 * Created by Dingtu2 on 2017/6/9.
 */

@Database(entities = {UserEntity.class, RoundEntity.class, TraceEntity.class, RoundEventEntity.class}, version = 11)
@TypeConverters(DateConverter.class)
public abstract class GenDataBase extends RoomDatabase {

    static final String DATABASE_NAME = "gendb";

    public abstract UserDao userDao();

    public abstract RoundDao roundDao();

    public abstract TraceDao traceDao();

    public abstract RoundEventDao roundEventDao();

}
