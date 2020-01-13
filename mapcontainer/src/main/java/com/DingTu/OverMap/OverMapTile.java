package com.DingTu.OverMap;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class OverMapTile implements Serializable {

    public int Row = 0;
    public int Col = 0;
    public int Level = 0;

    private String TileName = "";  //格式：col@row@level

    public String TableName = "";   //切片在哪个在表内

    public String Url = "";   //切片的下载地址
    public String CachePath = "";   //缓存路径


    /**
     * 设置切片的名称，格式：col@row@level
     * @param RowColLevel
     */
    public void SetTileName(String RowColLevel,String WhichTableName)
    {
        String[] tiInfo = RowColLevel.split("@");
        this.Col = Integer.valueOf(tiInfo[0]);
        this.Row = Integer.valueOf(tiInfo[1]);
        this.Level = Integer.valueOf(tiInfo[2]);

        this.TileName = RowColLevel;
        this.TableName = WhichTableName;
    }

    /**
     * 获取切片的名称
     * @return
     */
    public String GetTileName(){return this.TileName;}

    public Bitmap TileBitmap = null;
}
