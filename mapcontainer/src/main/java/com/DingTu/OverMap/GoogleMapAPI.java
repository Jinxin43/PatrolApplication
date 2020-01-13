package com.DingTu.OverMap;

import com.DingTu.Enum.lkOverMapType;
import com.DingTu.Map.Param;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class GoogleMapAPI {

    /**
     * 构建google地图的下载地址
     */
    public static String CreateTileUrl(lkOverMapType overMapType, int row, int col, int level)
    {
        //"http://mt3.google.cn/vt/lyrs=t&s=Galile&";
        String lysType = "s";  //卫星影像图
        if (overMapType==lkOverMapType.enGoogle_Satellite)lysType = "s";
        if (overMapType==lkOverMapType.enGoogle_Terrain)lysType = "t";
        if (overMapType==lkOverMapType.enGoogle_Street)lysType = "m";
        String s = "Galileo".substring(0, ((3 * col + row) % 8));

        //String url = "http://tile%1$s.tianditu.com/DataServer?T=A0512_EMap&x=%2$s&y=%3$s&L=%4$s";
        //url = String.format(url, (col%4),col,row,level);

        String url = "http://mt"+(col%4)+".google.cn/vt/lyrs="+lysType+"&"+"x=" + col + "&y=" + row + "&z=" + level+"&s=" + s;
        return url;
    }


    //根据经纬度及级数计算出相应的图片位于的行列数
    public static void GetTileXY(double longitude, double latitude, int Zoom,Param Tile_X, Param Tile_Y)
    {
        longitude = 180 + longitude;

        double longTileSize = 360.0 / (Math.pow(2, (Zoom)));

        double tilex = longitude / longTileSize;

        if (latitude > 90) latitude = latitude - 180;
        if (latitude < -90) latitude = latitude + 180;

        //转换度数到弧度
        double phi = Math.PI * latitude / 180.0;

        //下面这一句是上面的合并
        double res = 0.5 * Math.log((1 + Math.sin(phi)) / (1 - Math.sin(phi)));
        double maxTileY = Math.pow(2, Zoom);
        double tiley = ((1 - res / Math.PI) / 2) * (maxTileY);



        Tile_X.setValue((int)(tilex));

        Tile_Y.setValue((int)(tiley));
    }

    //瓦片的行列数及级数计算出相应瓦片的左上角经纬度坐标值
    public static void GetTileLL(int Tile_X, int Tile_Y, int Zoom, Param longitude, Param latitude)
    {
        double TiteCount = Math.pow(2, (Zoom));

        //经度
        double longTileSize = 360.0 / TiteCount;
        longitude.setValue(Tile_X * longTileSize - 180);

        //纬度

        double res = (1-2 * Tile_Y / TiteCount) * Math.PI;

        //2 * res = Math.Log((1 + Math.Sin(phi)) / (1 - Math.Sin(phi)));
        double EXres = Math.exp(2 * res);
        double phi = Math.asin((EXres-1)/(EXres+1));
        latitude.setValue(phi * 180 / Math.PI);



        //if (latitude > 90) latitude = latitude - 180;
        //if (latitude < -90) latitude = latitude + 180;

        //转换度数到弧度
        //double phi = Math.PI * latitude / 180.0;

        //下面这一句是上面的合并
        //double res = 0.5 * Math.Log((1 + Math.Sin(phi)) / (1 - Math.Sin(phi)));
        //double tiley = ((1 - res / Math.PI) / 2) * (TiteCount);



        //Tile_X = (int)Math.Floor(tilex);

        //Tile_Y = (int)Math.Floor(tiley);
    }
}
