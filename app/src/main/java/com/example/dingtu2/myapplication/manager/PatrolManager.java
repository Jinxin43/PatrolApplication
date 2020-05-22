package com.example.dingtu2.myapplication.manager;

import android.util.Log;
import android.widget.Toast;

import com.example.dingtu2.myapplication.AppSetting;
import com.example.dingtu2.myapplication.db.XDbManager;
import com.example.dingtu2.myapplication.db.xEntity.PatrolEntity;
import com.example.dingtu2.myapplication.db.xEntity.PatrolEventEntity;
import com.example.dingtu2.myapplication.db.xEntity.PatrolPointEntity;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2018/4/17.
 */

public class PatrolManager {

    private static PatrolManager instance;
    public PatrolEntity mCurrentRatrol;

    private PatrolManager() {
    }

    public static PatrolManager getInstance() {
        synchronized (PatrolManager.class) {
            if (instance == null) {
                instance = new PatrolManager();
            }

            return instance;
        }
    }

    public List<PatrolEntity> getUnloadPatrols(String userKey) {
        List<PatrolEntity> unloadPatrols = null;
        try {
            unloadPatrols = XDbManager.getDb().selector(PatrolEntity.class).where("uploadStatus", "=", 0).and("userid", "=", userKey).findAll();
        } catch (DbException ex) {
            Log.e("PatrolsManager", "getUnloadPatrols failed," + ex.getMessage());
        }

        return unloadPatrols;
    }

    public PatrolEntity getPatrolById(String patrolId) throws DbException {
        return XDbManager.getDb().selector(PatrolEntity.class).where("id", "=", patrolId).findFirst();
    }

    public PatrolEntity getOnGoingPatrol(String userKey) {
        PatrolEntity currentRatrol = null;

        try {
            currentRatrol = XDbManager.getDb().selector(PatrolEntity.class).where("roundstatus", "=", 0).and("userid", "=", userKey)
                    .orderBy("startTime", true).findFirst();
        } catch (Exception ex) {
            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "查询正在进行的巡护失败：" + ex.getMessage(), Toast.LENGTH_SHORT);
        }

        mCurrentRatrol = currentRatrol;
        return currentRatrol;
    }

    public List<PatrolPointEntity> getUnloadPatrolPoints(String userKey) {
        List<PatrolPointEntity> unloadPoints = null;
        try {
            unloadPoints = XDbManager.getDb().selector(PatrolPointEntity.class).where("uploadStatus", "=", 0).and("userid", "=", userKey).findAll();
        } catch (DbException ex) {
            Log.e("PatrolsManager", "getUnloadPatrolPoints failed," + ex.getMessage());
        }

        return unloadPoints;
    }


    public void savePatrol(PatrolEntity patrol) throws DbException {
        XDbManager.getDb().saveOrUpdate(patrol);
    }

    public List<PatrolEntity> getPatrol()  {
        List<PatrolEntity>   patrol= null;
        try {
            patrol = XDbManager.getDb().findAll(PatrolEntity.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return patrol;
    }


    public boolean savePatrolEvent(PatrolEventEntity patrolEvent) {
        boolean result = true;
        try {
            XDbManager.getDb().saveOrUpdate(patrolEvent);
        } catch (Exception ex) {
            Log.e("save new patrol event", ex.getMessage());
            result = false;
        }

        return result;
    }

    public List<PatrolEventEntity> getPatrolEvents(String patrolId) {
        List<PatrolEventEntity> unloadEvents = new ArrayList<PatrolEventEntity>();
        try {
            unloadEvents = XDbManager.getDb().selector(PatrolEventEntity.class).where("roundID", "=", patrolId).findAll();
        } catch (Exception ex) {
            Log.e("getPatrolEvents", "getPatrolEvents failed," + ex.getMessage());
        }

        return unloadEvents;
    }

    public List<PatrolEventEntity> getUnloadPatrolEvents() {
        List<PatrolEventEntity> unloadEvents = new ArrayList<PatrolEventEntity>();
        try {
            unloadEvents = XDbManager.getDb().selector(PatrolEventEntity.class).where("uploadStatus", "=", 0).findAll();
        } catch (Exception ex) {
            Log.e("getPatrolEvents", "getPatrolEvents failed," + ex.getMessage());
        }

        return unloadEvents;
    }

    public PatrolEventEntity getPatrolEvent(String patrolEventId) throws  DbException
    {
        return XDbManager.getDb().selector(PatrolEventEntity.class).where("Id","=",patrolEventId).findFirst();
    }

    public void savePatrolPoint(PatrolPointEntity patrolPointEntity) throws DbException {
        XDbManager.getDb().saveOrUpdate(patrolPointEntity);
    }

    public List<PatrolPointEntity> getPatrolPoints(String patrolId) {
        List<PatrolPointEntity> unloadPoints = new ArrayList<PatrolPointEntity>();
        try {
            unloadPoints = XDbManager.getDb().selector(PatrolPointEntity.class).where("roundID", "=", patrolId).findAll();
        } catch (DbException ex) {
            Log.e("getPatrolPoints", "getUnloadPatrolPoints failed," + ex.getMessage());
        }

        return unloadPoints;
    }
}
