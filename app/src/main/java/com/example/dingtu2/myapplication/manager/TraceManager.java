package com.example.dingtu2.myapplication.manager;

import com.example.dingtu2.myapplication.db.XDbManager;
import com.example.dingtu2.myapplication.db.xEntity.TraceEntity;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2018/4/21.
 */

public class TraceManager {
    private static TraceManager mInstance;

    private TraceManager() {
    }

    public static TraceManager getInstance() {
        synchronized (TraceManager.class) {
            if (mInstance == null) {
                mInstance = new TraceManager();
            }
        }
        return mInstance;
    }

    public void SaveTrace(TraceEntity traceEntiry) throws DbException {
        XDbManager.getDb().saveOrUpdate(traceEntiry);
    }

    public List<TraceEntity> getTracesByPatrolId(String patrolId) throws DbException {
        return XDbManager.getDb().selector(TraceEntity.class).where("roundID", "=", patrolId).orderBy("gpsTime").findAll();

    }

    public List<TraceEntity> getUnloadTraces() {
        List<TraceEntity> traceEnties = new ArrayList<>();
        try {
            traceEnties = XDbManager.getDb().selector(TraceEntity.class).where("uploadStatus", "=", 0).orderBy("gpsTime").findAll();
        } catch (Exception ex) {

        }
        return traceEnties;
    }

    public List<TraceEntity> getUnloadTracesByPatrolId(String patrolId) {
        List<TraceEntity> traceEnties = new ArrayList<>();
        try {
            traceEnties = XDbManager.getDb().selector(TraceEntity.class).where("uploadStatus", "=", 0).and("roundID", "=", patrolId).orderBy("gpsTime").findAll();
        } catch (Exception ex) {

        }
        return traceEnties;
    }
}
