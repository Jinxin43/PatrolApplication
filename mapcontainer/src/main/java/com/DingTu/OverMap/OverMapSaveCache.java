package com.DingTu.OverMap;

import com.DingTu.Base.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class OverMapSaveCache implements Runnable {
    OverMapSQLiteDataBase _CSQLiteDatabase = null;

    //设置缓存文件路径
    private String _CacheFilePath = "";
    public void SetCacheFilePath(String chFilePath)
    {
        this._CacheFilePath = chFilePath;
    }

    //保存缓存文件到数据库,CacheFileName全路径"/X@Y@Z@表名.png
    private boolean Save(String CacheFileName)
    {
        if (this._CSQLiteDatabase==null)this._CSQLiteDatabase=new OverMapSQLiteDataBase();
        //数据库名称
        String dbFilePath = this._CacheFilePath;//lkmap.Tools.Tools.GetFilePath(CacheFileName);
        String dbFileName = Tools.GetFileName_NoEx(CacheFileName);
        String[] FileInfo = dbFileName.split("@");
        _CSQLiteDatabase.setDatabaseName(dbFilePath+"/MapBase"+FileInfo[2]+".dbx");
        return _CSQLiteDatabase.InsertImage(FileInfo[3], FileInfo[0]+"@"+FileInfo[1]+"@"+FileInfo[2], Tools.readStream(CacheFileName));
    }



    boolean _Saving = false;
    public void StartSave()
    {
        if (this._Saving) return;
        //得到需要入库的缓存文件
        this._Saving= true;
        List<String> _CahFileList = this.getCacheList(10);
        for(String chFileName :_CahFileList)
        {
            if (this.Save(chFileName))
            {
                File f=new File(chFileName);
                f.delete();
            }
        }
        this._Saving= false;
    }

    @Override
    public void run()
    {
        this.StartSave();
    }

    //获取当前的缓存文件列表
    private List<String> getCacheList(int MaxFileCount)
    {
       /* 设定目前所在路径 */
        List<String> it=new ArrayList<String>();

        File f=new File(this._CacheFilePath);
        if (!f.exists())f.mkdir();
        File[] files=f.listFiles();

       /* 将所有文件存入ArrayList中 */
        for(int i=0;i<files.length;i++)
        {
            File file=files[i];
            String fName = file.getPath();
            if (fName.indexOf("@")>=0)
            {
                String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase();
                if(end.equals("png"))
                {
                    it.add(fName);
                    if (MaxFileCount>0)
                    {
                        if (it.size()>=MaxFileCount) return it;
                    }
                }
            }
        }
        return it;
    }
}
