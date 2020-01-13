package com.DingTu.GPS;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Map.StaticObject;

import java.util.HashMap;

import com.DingTu.Cargeometry.Coordinate;

/**
 * Created by Dingtu2 on 2017/6/2.
 */

public class GpsInfoManage {

    /**
     * 更新GPS位置
     * @param gpsCoor
     */
    public void UpdateGpsPosition(Coordinate gpsCoor)
    {
        String ShowFormateStr = "";
        HashMap<String,String> CoorStr = ConvertCoordinateFormate(gpsCoor);
        if (CoorStr.containsKey("X"))ShowFormateStr=CoorStr.get("X")+" ";
        if (CoorStr.containsKey("Y"))ShowFormateStr+=CoorStr.get("Y")+" ";
        if (CoorStr.containsKey("Z"))ShowFormateStr+=CoorStr.get("Z")+" ";
        this._mainheader_pos.setText(ShowFormateStr);
        this.UpdateGpsUseTime();
    }

    public static HashMap<String,String> ConvertCoordinateFormate(Coordinate gpsCoor)
    {
        HashMap<String,String> ConvertCoordinate = new HashMap<String,String>();

        //根据系统配置确定显示格式，编码格式：GPS_1_1
//        String FormateCode = PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Code").Value+"";
        String FormateCode="GPS_1_1";
        String[] Code = FormateCode.split("_");

        if (Code[0].equals("GPS"))
        {
            //0=DD°MM'SS.SSSS"，1=DD°MM.MMMMMM'，2=DD.DDDDDD°
            if (Code[1].equals("0"))
            {
                ConvertCoordinate.put("X", "经度："+GetDDMMSS(gpsCoor.getX()));
                ConvertCoordinate.put("Y", "纬度："+ GetDDMMSS(gpsCoor.getY()));
            }
            if (Code[1].equals("1"))
            {
                ConvertCoordinate.put("X","经度："+GetDDMM(gpsCoor.getX()));
                ConvertCoordinate.put("Y","纬度："+GetDDMM(gpsCoor.getY()));
            }
            if (Code[1].equals("2"))
            {
                ConvertCoordinate.put("X", "经度："+ Tools.ConvertToDigi(gpsCoor.getX()+"", 6)+"°");
                ConvertCoordinate.put("Y", "纬度："+Tools.ConvertToDigi(gpsCoor.getY()+"", 6)+"°");
            }
        }
        if (Code[0].equals("PROJECT"))
        {
            Coordinate PrjCoor = StaticObject.soProjectSystem.WGS84ToXY(gpsCoor.getX(), gpsCoor.getY(),gpsCoor.getZ());
            ConvertCoordinate.put("X","X："+Tools.ConvertToDigi(PrjCoor.getX()+"", 3));
            ConvertCoordinate.put("Y","Y："+Tools.ConvertToDigi(PrjCoor.getY()+"", 3));
        }

        if (Code[2].equals("1"))ConvertCoordinate.put("Z", "H："+Tools.ConvertToDigi(gpsCoor.getZ()+"",2));
        return ConvertCoordinate;
    }

    /**
     * 将度转换为度分秒
     * @param DDD
     * @return
     */
    private static String GetDDMMSS(double DDD)
    {
        //DD°MM'SS.SSSS″
        int dd = (int)Math.floor(DDD);
        double MM = (DDD-dd)*60;
        int mm = (int)Math.floor(MM);

        double SS = (MM-mm)*60;
        String ss = Tools.ConvertToDigi(SS+"", 4);
        return dd+"°"+mm+"'"+ss+"″";

    }
    /**
     * 将度转换为度分分
     * @param DDD
     * @return
     */
    private static String GetDDMM(double DDD)
    {
        //DD°MM.MMMMMM"
        int dd = (int)Math.floor(DDD);
        double MM = (DDD-dd)*60;
        return dd+"°"+Tools.ConvertToDigi(MM+"",6)+"'";

    }

    private long m_BeforeTime = 0;
    /**
     * 更新GPS状态
     * @param status
     */
    public void UpdateGPSStatus(String status)
    {
        this.SetInVisible();
//        this._gps_jd.setText("0.0m");

        if (status.equals("Music_GPS开启"))
        {
            this.m_GpsBeforeOpenTime = (new java.util.Date()).getTime()+"";
//            PubVar.m_DoEvent.m_SoundTool.PlaySound(1);
        }

        if (status.equals("Music_GPS卫星事件"))
        {
            if (this.m_GpsBeforeOpenTime=="")
            {
                this.m_GpsBeforeOpenTime = (new java.util.Date()).getTime()+"";
//                PubVar.m_DoEvent.m_SoundTool.PlaySound(1);
            }
        }

        if (status.equals("Music_卫星丢失"))
        {
//            PubVar.m_DoEvent.m_SoundTool.PlaySound(2);
        }
        if (status.equals("Music_定位"))
        {
//            PubVar.m_DoEvent.m_SoundTool.PlaySound(3);
        }
        if (status.equals("Music_未定位"))
        {
//            PubVar.m_DoEvent.m_SoundTool.PlaySound(4);
        }

        if (status.equals("关闭"))
        {
            this.m_GpsBeforeOpenTime="";
//            this._gps_close.setVisibility(View.VISIBLE);
//            this._gpsxh_close.setVisibility(View.VISIBLE);
        }
        if (status.equals("已定位"))
        {
//            this._gps_open.setVisibility(View.VISIBLE);
//            //根据信息强弱设置级数
//            int Level = PubVar.m_GPSLocate.GetLevelForAlwaysFix();
//            if (Level==0)this._gpsxh_0.setVisibility(View.VISIBLE);
//            if (Level==1)this._gpsxh_1.setVisibility(View.VISIBLE);
//            if (Level==2)this._gpsxh_2.setVisibility(View.VISIBLE);
//            if (Level==3)this._gpsxh_3.setVisibility(View.VISIBLE);
//            if (Level==4)this._gpsxh_4.setVisibility(View.VISIBLE);
//            String Unit = "m";
//            if (PubVar.m_GPSLocate.m_NEMALocate.GetUseNEMA())Unit="P";
//            this._gps_jd.setText(PubVar.m_GPSLocate.getAccuracy()+Unit);
        }
        if (status.equals("定位中"))
        {
            //GPS状态根据时间进行切换
            long currentTime = System.currentTimeMillis();
            if ((currentTime-m_BeforeTime)>2000)
            {
//                this._gps_open.setVisibility(View.VISIBLE);
//                this._gps_close.setVisibility(View.GONE);
                this.m_BeforeTime = currentTime;
            } else
            {
//                this._gps_open.setVisibility(View.GONE);
//                this._gps_close.setVisibility(View.VISIBLE);
            }

            //GPS信息为关状态
//            this._gpsxh_close.setVisibility(View.VISIBLE);
        }
        this.UpdateGpsUseTime();
    }

    /**
     * 状态条视图
     */
    private TextView _mainheader_pos = null;  //坐标信息条
    private TextView _gps_jd = null;  		  //GPS精度条

    private ImageView _gps_open = null;       //GPS开状态图标
    private ImageView _gps_close = null;       //GPS关状态图标

    private ImageView _gpsxh_close = null;    //GPS信号关状态图标
    private ImageView _gpsxh_0 = null;       //GPS信号0级状态图标
    private ImageView _gpsxh_1 = null;       //GPS信号1级状态图标
    private ImageView _gpsxh_2 = null;       //GPS信号2级状态图标
    private ImageView _gpsxh_3 = null;       //GPS信号3级状态图标
    private ImageView _gpsxh_4 = null;       //GPS信号4级状态图标

    private View m_MainHeaderBar = null;
    public void SetHeaderViewBar(View view)
    {
//        this.m_MainHeaderBar = view;
//        this._mainheader_pos = (TextView)view.findViewById(R.id.ll_coorinfo);
//        this._mainheader_pos.setText(PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Label").Value);
//
//        this._gps_jd = (TextView)view.findViewById(R.id.tv_gps_jd);
//
//        this._gps_open = (ImageView)view.findViewById(R.id.iv_gpsopen);
//        this._gps_close = (ImageView)view.findViewById(R.id.iv_gpsclose);
//        this._gpsxh_close = (ImageView)view.findViewById(R.id.iv_gpsxh_close);
//        this._gpsxh_0 = (ImageView)view.findViewById(R.id.iv_gpsxh0);
//        this._gpsxh_1 = (ImageView)view.findViewById(R.id.iv_gpsxh1);
//        this._gpsxh_2 = (ImageView)view.findViewById(R.id.iv_gpsxh2);
//        this._gpsxh_3 = (ImageView)view.findViewById(R.id.iv_gpsxh3);
//        this._gpsxh_4 = (ImageView)view.findViewById(R.id.iv_gpsxh4);
//        this.SetInVisible();
//
//        //默认为关状态
//        this._gps_close.setVisibility(View.VISIBLE);
//        this._gpsxh_close.setVisibility(View.VISIBLE);

    }

    /**
     * 设置当前选中的图层名称
     * @param
     */
//    public void SetCurrentLayerName(v1_Layer pLayer)
//    {
//        TextView tv = (TextView)this.m_MainHeaderBar.findViewById(R.id.ll_layer);
//        tv.setText("  当前图层："+pLayer.GetLayerAliasName());
//        tv.setTag(pLayer.GetLayerID());
//    }
    public String GetCurrentLayerId()
    {
//        TextView tv = (TextView)this.m_MainHeaderBar.findViewById(R.id.ll_layer);
//        if (tv.getTag()!=null)return tv.getTag()+"";
        return "";
    }

    /**
     * 刷新比例尺显示
     */
    public void UpdateScaleBar()
    {
        //1英寸代表的距离
        DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
        double D = dm.densityDpi * PubVar.m_Map.getViewConvert().getZoomScale();
        D = D / 0.0254;
//        TextView tv = (TextView)this.m_MainHeaderBar.findViewById(R.id.ll_scalebar);
//        tv.setText("比例尺=1："+Tools.ConvertToDigi(D+"", 0).replace(".", ""));
    }

    private String m_GpsBeforeOpenTime = "";

    /**
     * 更新GPS使用时间
     */
    public void UpdateGpsUseTime()
    {
//        TextView tv = (TextView)this.m_MainHeaderBar.findViewById(R.id.ll_gpstime);
//        if (this.m_GpsBeforeOpenTime==""){tv.setText("");return;}
//
//        String GpsStr = "GPS已开启：%1$s";
//        long JS = (new java.util.Date()).getTime()-Long.parseLong(this.m_GpsBeforeOpenTime);
//        int H = 0,M = 0,S = 0;
//        if ((JS/1000/60/60)>=1){H = Integer.parseInt((JS/1000/60/60)+"");JS-=H*1000*60*60;}
//        if ((JS/1000/60)>=1){M = Integer.parseInt((JS/1000/60)+"");JS-=M*1000*60;}
//        if ((JS/1000)>=1)S = Integer.parseInt((JS/1000)+"");
//
//        String Result = "";
//        if (H!=0)Result+=H+"小时";
//        if (M!=0)Result+=M+"分钟";
//        if (S!=0)Result+=S+"秒";
//
//        GpsStr = String.format(GpsStr, Result);
//        tv.setText(GpsStr);
    }

    private void SetInVisible()
    {
//        _gps_open.setVisibility(View.GONE);
//        _gps_close.setVisibility(View.GONE);
//        _gpsxh_close.setVisibility(View.GONE);
//        _gpsxh_0.setVisibility(View.GONE);
//        _gpsxh_1.setVisibility(View.GONE);
//        _gpsxh_2.setVisibility(View.GONE);
//        _gpsxh_3.setVisibility(View.GONE);
//        _gpsxh_4.setVisibility(View.GONE);
    }
}
