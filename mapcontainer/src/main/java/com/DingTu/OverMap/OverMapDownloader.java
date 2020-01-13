package com.DingTu.OverMap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.DingTu.Base.PubVar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class OverMapDownloader {

    public OverMapDownloader()
    {
        //初始化下载图片的线程
        for(int i=1;i<=m_DownloadThreadCount;i++)
        {
            OverMapDownload omd = new OverMapDownload();
            omd.setCallbackHandler(this.myHander);
            this.m_DownloadThreadList.add(omd);
        }
    }

    //地图的下载进程数
    private int m_DownloadThreadCount = 5;
    //下载数据的线程列表
    private List<OverMapDownload> m_DownloadThreadList = new ArrayList<OverMapDownload>();

    //绑定OverMap主程序
    private OverMap _OverMap = null;
    public void setBindGoogleMap(OverMap gm)
    {
        this._OverMap = gm;
    }



    //下载文件列表
    private List<OverMapTile> m_DownLoadFileList = new ArrayList<OverMapTile>();

    //设置下载文件列表
    public void setUpLoadFileList(List<OverMapTile> _DownloadFileList)
    {
        this.m_DownLoadFileList = _DownloadFileList;
    }


    //保存线程
    private Timer _SaveTimer = null;
    private OverMapSaveCache _GoogleMapSaveCache = null;
    TimerTask _saveTask = new TimerTask()
    {
        public void run()
        {
            if (_GoogleMapSaveCache==null)
            {
                _GoogleMapSaveCache=new OverMapSaveCache();
                _GoogleMapSaveCache.SetCacheFilePath(_OverMap.GetOverMapPath());
            }
            _GoogleMapSaveCache.run();
        }
    };


    //开始上传
    public void StartUpLoad()
    {
        if (this._SaveTimer==null)
        {
            this._SaveTimer=new Timer();
            this._SaveTimer.schedule(_saveTask, 0, 1000*2);
        }

        //根据上传的线程数量进行文件分配，平均分配
        int FileIndex = 0;
        boolean exit=true;
        do
        {
            for(int i=0;i<this.m_DownloadThreadList.size();i++)
            {
                if (FileIndex<=this.m_DownLoadFileList.size()-1)
                {
                    this.m_DownloadThreadList.get(i).AddDownLoadFile(this.m_DownLoadFileList.get(FileIndex));
                    FileIndex++;
                }
                else
                {
                    exit=false;break;
                }
            }
        }while(exit);

        for(int i=0;i<this.m_DownloadThreadList.size();i++)
        {
            Thread t = new Thread(this.m_DownloadThreadList.get(i));
            t.start();
        }
    }

    //保存缓存图片
    //private GoogleMapSaveCache _GoogleMapSaveCache = new GoogleMapSaveCache();
    //下载完成后的回调
    Handler myHander = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (msg.what==1)  //更新UI
            {
                Bundle data = msg.getData();
                OverMapTile tile = (OverMapTile)data.getSerializable("Tile");
                byte[] ImageByte = data.getByteArray("ImageByte");
                _OverMap.ShowImage(tile, ImageByte);
                PubVar.m_Map.FastRefresh();
                //_GoogleMapSaveCache.Save(_GoogleMap.getSQLiteDB(),_GoogleMap.getLocalGoogleMapPath(), dbFileName, _GoogleMap.getGoogleMapType()+"", ImageByte);
            }
            super.handleMessage(msg);
        }
    };
}
