package com.example.dingtu2.myapplication.manager;

import android.util.Log;
import android.widget.Toast;

import com.example.dingtu2.myapplication.AppSetting;
import com.example.dingtu2.myapplication.db.XDbManager;
import com.example.dingtu2.myapplication.db.xEntity.PatrolPointEntity;
import com.example.dingtu2.myapplication.db.xEntity.PhotoEntity;
import com.example.dingtu2.myapplication.model.Photo;
import  org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dingtu2 on 2019/3/20.
 */

public class PhotoManager {
    private static PhotoManager instance;
    public PhotoManager mCurrentRatrol;

    private PhotoManager() {
    }

    public static PhotoManager getInstance() {
        synchronized (PhotoManager.class) {
            if (instance == null) {
                instance = new PhotoManager();
            }
            return instance;
        }
    }

    public void savePhoto(PhotoEntity photo) throws DbException {
        XDbManager.getDb().saveOrUpdate(photo);
    }

    public PhotoEntity getPhotoEntity(String photoName) throws  DbException{
        return XDbManager.getDb().selector(PhotoEntity.class).where("photoName", "=", photoName).findFirst();
    }

    public void updateUpload(PhotoEntity photo){
        try {
            KeyValue key1 = new KeyValue("uploadStatus", 1);
            KeyValue key2 = new KeyValue("uploadTime", photo.getUploadTime());
            XDbManager.getDb().update(PhotoEntity.class, WhereBuilder.b("photoName","=", photo.getPhotoName()),key1,key2);

        }
        catch (Exception ex)
        {
            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "更新照片失败"+ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public List<PhotoEntity> getUnuploadPhoto(String type, String parentId)
    {
        List<PhotoEntity> unloadPhotos = new ArrayList<PhotoEntity>();
        try {
            unloadPhotos = XDbManager.getDb().selector(PhotoEntity.class).where("type", "=", type).
                    and("belongTo","=",parentId).findAll();
        } catch (DbException ex) {
            Log.e("getPatrolPoints", "getUnloadPatrolPoints failed," + ex.getMessage());
        }

        return unloadPhotos;
    }

    public List<PhotoEntity> getUnuploadPhoto()
    {
        List<PhotoEntity> unloadPhotos = new ArrayList<PhotoEntity>();
        try {
            unloadPhotos = XDbManager.getDb().selector(PhotoEntity.class).where("uploadStatus", "=", 0).findAll();
        } catch (DbException ex) {
            Log.e("getPatrolPoints", "getUnloadPatrolPoints failed," + ex.getMessage());
        }

        return unloadPhotos;
    }
}
