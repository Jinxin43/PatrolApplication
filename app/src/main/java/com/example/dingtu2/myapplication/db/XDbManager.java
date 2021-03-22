package com.example.dingtu2.myapplication.db;

import android.util.Log;

import com.DingTu.Base.PubVar;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;

/**
 * Created by Dingtu2 on 2018/4/12.
 */

public class XDbManager {

    private static DbManager mDbManager;

    public static DbManager getDb() {
        if (mDbManager == null) {
            synchronized (XDbManager.class) {
                File zbDBFile = new File(PubVar.m_SysAbsolutePath + PubVar.m_SysDictionaryName + "/");
                Log.d("DBpath", PubVar.m_SysAbsolutePath + "/" + PubVar.m_SysDictionaryName + "/");
                final DbManager.DaoConfig dbConfig = new DbManager.DaoConfig().setDbDir(zbDBFile);
                dbConfig.setDbVersion(13);
                dbConfig.setDbName("patrol.dbx");
                dbConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {

                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        if (oldVersion > 9) {
                            try {
                                db.execNonQuery("alter table PatrolEntity add gemoetryId int");
                                Log.d("10", "alter table PatrolEntity add gemoetryId int");
                            } catch (DbException ex) {
                                ex.printStackTrace();
                            }

                        }

                    }
                });
                mDbManager = x.getDb(dbConfig);
                Log.d("DBpath", "patrol.dbx created");
            }
        }
        return mDbManager;
    }


}
