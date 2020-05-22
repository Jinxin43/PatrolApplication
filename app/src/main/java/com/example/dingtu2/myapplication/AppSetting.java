package com.example.dingtu2.myapplication;

import android.app.Application;

import com.example.dingtu2.myapplication.db.xEntity.PatrolEntity;
import com.example.dingtu2.myapplication.db.xEntity.UserEntity;

import org.xutils.DbManager;

import java.util.HashMap;

/**
 * Created by Dingtu2 on 2017/6/9.
 */

public class AppSetting {
    public static final String baseUrl = "http://111.20.63.66:8001";//周保
//    public static final String baseUrl = "http://114.115.255.125:8001";//华为云
    public static UserEntity curUser;
    public static PatrolEntity curRound;
    public static String curUserKey;
    public static String SpecialPatrolPolyLayerId = "T1D11AE4BC2F2497D9EED62FCABBE1F8C";
    public static String SpecialPatrolLineLayerId = "T44289A081F084633931C95343DF06949";
    public static String SpecialPatrolPointLayerId = "T1348D267F62E497E8550A18C52327C55";
    public static Application applicaton;
    public static String photoPath;
    public static String smallPhotoPath;
//    public static final String baseUrl = "http://192.168.1.104:8001";
    public static Boolean isReUpload = false;
    public static HashMap<String, String> myDutyArea = new HashMap<String, String>();
    private static Object mLock = new Object();
    private static DbManager.DaoConfig daoConfig;
    public static Boolean mIsRounding=false;

//    public static DbManager.DaoConfig getDbConfig() {
//        if (daoConfig == null) {
//            synchronized (mLock) {
//                daoConfig = new DbManager.DaoConfig()
//                        .setDbName("project.dbx")
//                        // 不设置dbDir时, 默认存储在app的私有目录.
//                        .setDbDir(new File(PubVar.m_SysAbsolutePath+"/Data/周保巡护")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
//                        .setDbVersion(3)
//                        .setDbOpenListener(new DbManager.DbOpenListener() {
//                            @Override
//                            public void onDbOpened(DbManager db) {
//                                // 开启WAL, 对写入加速提升巨大
//                                db.getDatabase().enableWriteAheadLogging();
//                            }
//                        })
//                        .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
//                            @Override
//                            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
//                                // TODO: ...
//                                try
//                                {
//                                    db.addColumn(PatrolCommandEntity.class,"title");
//                                }
//                                catch (Exception ex)
//                                {
//                                    Log.e("dbupgrade",ex.getMessage());
//                                }
//                            }
//                        });
//            }
//        }
//        return daoConfig;
//    }
}
