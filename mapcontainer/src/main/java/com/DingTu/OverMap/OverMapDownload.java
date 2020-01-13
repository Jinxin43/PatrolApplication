package com.DingTu.OverMap;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class OverMapDownload implements Runnable {

    //需要下载的切片列表
    List<OverMapTile> _TileList = new ArrayList<OverMapTile>();

    /**
     * 增加切片
     * @param tile
     */
    public void AddDownLoadFile(OverMapTile tile)
    {
        this._TileList.add(0, tile);
    }

    //开始批量下载
    public void StartDownload()
    {
        if (this._TileList.size()>0)
        {
            this.StartDownloadFile(this._TileList.get(0));
        }
    }

    //开始下载单个文件
    private boolean StartDownloadFile(OverMapTile tileInfo)
    {
        //String urlStr="http://mt1.google.cn/vt/lyrs=s&x=29&y=13&z=5&s=Gali";

        //Png文件名称：列@行@级数@表名
        String PNGName = tileInfo.Col+"@"+tileInfo.Row+"@"+tileInfo.Level+"@"+tileInfo.TableName;
        String CacheName = tileInfo.CachePath+"/"+PNGName+".png";

        try
        {
            URL myUrl = new URL(tileInfo.Url);
            try {
                HttpURLConnection urlConn = (HttpURLConnection)myUrl.openConnection();
                urlConn.setRequestMethod("GET");
                int code = urlConn.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK)
                {
                    urlConn.connect();
                    InputStream is = urlConn.getInputStream();
                    int fileSize = urlConn.getContentLength();
                    //FileOutputStream FOS = new FileOutputStream(CacheName);
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int numread = 0;
                    while ((numread = is.read(buffer)) != -1)
                    {
                        //FOS.write(buffer, 0, numread);
                        outStream.write(buffer, 0, numread);
                    }
                    //FOS.close();

                    byte[] imgData = outStream.toByteArray();
                    outStream.close();
                    is.close();

                    //this._GoogleMap.ShowImage(PNGName, outStream.toByteArray());
                    //outStream.close();
                    //this._GoogleMap.Map.FastRefresh();

                    //更新进度条
                    Message msg = new Message();
                    msg.what=1;
                    Bundle mBundle = new Bundle();
                    mBundle.putByteArray("ImageByte", imgData);
                    mBundle.putSerializable("Tile", tileInfo);
                    msg.setData(mBundle);
                    this._Handler.sendMessage(msg);  //抛出消息

                    //写入临时文件中
                    FileOutputStream FOS = new FileOutputStream(CacheName);
                    FOS.write(imgData);
                    FOS.close();
                    //_GoogleMapSaveCache.Save(this._GoogleMap.getSQLiteDB(),this._CachePath, PNGName, "2", imgData);

                    this._TileList.remove(tileInfo);
                    if (this._TileList.size()>0)this.StartDownloadFile(this._TileList.get(0));


                    //保存已下载的图片到缓存文件中
//					GoogleMapSaveCache sc = new GoogleMapSaveCache();
//					sc.Save(CacheName, "2");
                    return true;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run()
    {
        this.StartDownload();
    }

    //设置上传完成后的回调
    private Handler _Handler = null;
    public void setCallbackHandler(Handler pHandler)
    {
        this._Handler = pHandler;
    }
}
