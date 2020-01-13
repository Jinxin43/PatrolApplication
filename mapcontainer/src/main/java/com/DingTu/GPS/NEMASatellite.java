package com.DingTu.GPS;

import android.location.GpsSatellite;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class NEMASatellite {

    private String _OID = "";
    public void SetOID(String oid){this._OID=oid;}
    public String GetOID(){return this._OID;}

    GpsSatellite GpsSate;

    //卫星编号
    private String _Prn = "";
    public void setPrn(String Prn) {this._Prn = Prn;}
    public String getPrn(){return this._Prn;}

    //信燥比
    private String _Snr = "0";
    public void setSnr(String Snr) {this._Snr = Snr;}
    public String getSnr(){return this._Snr;}

    //方位角
    private String _Azimuth = "0";
    public void setAzimuth(String Azimuth) {this._Azimuth = Azimuth;}
    public String getAzimuth(){return this._Azimuth;}

    //高度角
    private String _Elevation = "90";
    public void setElevation(String Elevation) {this._Elevation = Elevation;}
    public String getElevation(){return this._Elevation;}

    //是否参于解算
    private boolean _InFix = false;
    public void setUsedInFix(boolean InFix) {this._InFix = InFix;}
    public boolean getUsedInFix(){return this._InFix;}
}
