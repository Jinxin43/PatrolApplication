package com.DingTu.GPS;

import android.location.Location;
import android.util.Log;

import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkGpsFixMode;
import com.DingTu.Enum.lkGpsLocationType;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class LocationEx {

    //位置类型，内置GPS，NEMA
    private lkGpsLocationType m_GpsLocationType = lkGpsLocationType.enInGps;
    public void SetType(lkGpsLocationType gpsLocationType)
    {
        this.m_GpsLocationType = gpsLocationType;
    }
    public lkGpsLocationType GetGpsLocationType(){return this.m_GpsLocationType;}

    //内置GPS解算所用的位置实体
    private Location m_InGpsLocation = null;
    public void SetInGpsLocate(Location location)
    {
        this.m_InGpsLocation = location;
    }
    public Location GetInGpsLocate(){return this.m_InGpsLocation;}

    //GPS时间UTC格式
    private String m_GpsTime = "";
    public String GetGpsTime(){return this.m_GpsTime;}
    public void SetGpsTime(String gpsTime)
    {
        //UTC时间，hhmmss（时分秒）格式
        if (gpsTime.length()>=6)
        {
            String HHStr  = gpsTime.substring(0,2);
            String MMStr  = gpsTime.substring(2,4);
            String SSStr  = gpsTime.substring(4,6);
            Log.i("GPS time:",gpsTime);
            if (Tools.IsInteger(HHStr) && Tools.IsInteger(MMStr) && Tools.IsInteger(SSStr))
            {
                this.m_GpsTime = (Integer.parseInt(HHStr)+8)+":"+MMStr+":"+SSStr;
            }
        }
        else
        {
            this.m_GpsTime="00:00:00";
        }
    }
//    public void SetGpsTime(long gpsTime)
//    {
//    }

    //GPS日期UTC格式
    private String m_GpsDate = "";
    public String GetGpsDate(){return this.m_GpsDate;}
    public void SetGpsDate(String gpsDate)
    {
        //UTC日期，ddmmyy（日月年）格式
        if (gpsDate.length()==6)
        {
            String DDStr  = gpsDate.substring(0,2);
            String MMStr  = gpsDate.substring(2,4);
            String YYStr  = gpsDate.substring(4,6);
            Log.i("GPS data:",gpsDate);
            if (Tools.IsInteger(YYStr) && Tools.IsInteger(MMStr) && Tools.IsInteger(DDStr))
            {
                gpsDate = (Integer.parseInt(YYStr)+2000)+"-"+MMStr+"-"+DDStr;
                this.m_GpsDate = gpsDate;
            }
        }
    }

    //GPS经度
    private double m_GpsLongitude = 0;
    public double GetGpsLongitude()
    {
        return this.m_GpsLongitude;
    }
    public void SetGpsLongitude(double gpsLongitude)
    {
        this.m_GpsLongitude = gpsLongitude;
    }
    public void SetGpsLongitudeStr(String gpsLongitudeStr)
    {
        this.m_GpsLongitude = this.ToJWD(gpsLongitudeStr, 2);
    }

    //GPs经度类型，经度半球E（东经）或W（西经）
    private String m_GpsLongitudeType = "N";
    public void SetGpsLongitudeType(String gpsLongitudeType){this.m_GpsLongitudeType = gpsLongitudeType;}
    public String GetGpsLongitudeType(){return this.m_GpsLongitudeType;}


    //GPS纬 度
    private double m_GpsLatitude = 0;
    public double GetGpsLatitude()
    {
        return this.m_GpsLatitude;
    }
    public void SetGpsLatitude(double gpsLatitude)
    {
        this.m_GpsLatitude = gpsLatitude;
    }
    public void SetGpsLatitudeStr(String gpsLatitudeStr)
    {

        this.m_GpsLatitude = this.ToJWD(gpsLatitudeStr, 1);
    }

    //GPs纬 度类型，纬度半球N（北半球）或S（南半球）
    private String m_GpsLatitudeType = "N";
    public void SetGpsLatitudeType(String gpsLatitudeType){this.m_GpsLatitudeType = m_GpsLatitudeType;}
    public String GetGpsLatitudeType(){return this.m_GpsLatitudeType;}

    //GPS海拔高度
    private double m_GpsAltitude =0;
    public double GetGpsAltitude()
    {
        return this.m_GpsAltitude;
    }
    public void SetGpsAltitude(double gpsAltitude)
    {
        this.m_GpsAltitude = gpsAltitude;
    }

    //定位模式，1 = 未定位， 2 = 二维定位， 3 = 三维定位。
    private lkGpsFixMode m_GpsFixMode = lkGpsFixMode.enNoFix;
    public lkGpsFixMode GetGpsFixMode()
    {
        return this.m_GpsFixMode;
    }
    public void SetGpsFixMode(String gpsFixMode)
    {
        if (gpsFixMode.trim().equals("1"))this.m_GpsFixMode = lkGpsFixMode.enNoFix;
        if (gpsFixMode.trim().equals("2"))this.m_GpsFixMode = lkGpsFixMode.en2DFix;
        if (gpsFixMode.trim().equals("3"))this.m_GpsFixMode = lkGpsFixMode.en3DFix;
    }

    //PDOP
    private double m_GpsPDOP = 99.99;
    public double GetGpsPDOP()
    {
        return this.m_GpsPDOP;
    }
    public void SetGpsPDOP(double gpsPDOP)
    {
        this.m_GpsPDOP = gpsPDOP;
    }

    //地面速度
    private double m_GpsSpeed = 0;
    public double GetGpsSpeed()
    {
        return this.m_GpsSpeed;
    }
    public void SetGpsSpeed(double gpsSpeed)
    {
        this.m_GpsSpeed = gpsSpeed;
    }

    //地面航向
    private String m_GpsLandDirection = "";
    public String GetGpsLandDirection()
    {
        return this.m_GpsLandDirection;
    }
    public void SetGpsLandDirection(String gpsLandDirection)
    {
        this.m_GpsLandDirection = gpsLandDirection;
    }


    //将字符中转换为经纬度，1-纬度，2-经度
    private double ToJWD(String JWDStr,int Type)
    {
        int BeforeLen = 4,DDLen = 2;
        if (Type==1){BeforeLen = 4;DDLen=2;}
        if (Type==2){BeforeLen = 5;DDLen=3;}
        //纬度ddmm.mmmm（度分）格式（前面的0也将被传输）
        if (Tools.ConvertToDouble(JWDStr)>1)
        {
            String fStr = JWDStr.substring(0,JWDStr.indexOf("."));
            if (fStr.length()==BeforeLen)
            {
                String DD  = JWDStr.substring(0,DDLen);
                String MM = JWDStr.substring(DDLen,JWDStr.length());
                return Tools.ConvertToDouble(DD)+ Tools.ConvertToDouble(MM)/60;
            }
        }
        return 0;
    }
}
