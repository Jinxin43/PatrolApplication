package com.DingTu.GPS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkGpsFixMode;
import com.DingTu.Enum.lkGpsLocationType;
import com.DingTu.Map.StaticObject;
import com.DingTu.mapcontainer.MapControl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.DingTu.Cargeometry.Coordinate;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class GPSLocate {

    public Context m_Context = null;
    public MapControl m_MapControl = null;
    public GPSMap m_GPSMap = null;
    public LocationManager m_LTManager = null;
    public LocationEx m_LocationEx = null;
    public NEMALocate m_NEMALocate = null;

    //巡护页面位置状态回调
    private ICallback m_GPSPositionCallback = null;
    public void SetGpsSetCallback(ICallback callback)
    {
        this.m_GPSPositionCallback = callback;
    }



    //主页面位置状态回调
    private  ICallback mGpsCallback = null;
    public void setGPSCallback(ICallback callback)
    {
        mGpsCallback  = callback;
    }

    public GPSLocate(MapControl mapControl)
    {
        this.m_Context = PubVar.m_DoEvent.m_Context;
        this.m_MapControl = mapControl;
        this.m_GPSMap = new GPSMap(this.m_MapControl);
        //this.m_GPSMap.SetGpsMeasure(new WeiPianZhiFa_GPSMeasure());
        PubVar.m_GPSMap = this.m_GPSMap;
        m_GPSMap.SetGpsInfoManage(PubVar.m_DoEvent.m_GpsInfoManage);
        this.m_NEMALocate = new NEMALocate();
        this.m_NEMALocate.SetCallback(new ICallback(){
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                m_LocationEx = (LocationEx)ExtraStr;
                m_GPSMap.UpdateGPSStatus(m_LocationEx);

                if (m_GPSPositionCallback !=null) m_GPSPositionCallback.OnClick("", null);

                if(mGpsCallback != null)
                {
                    mGpsCallback.OnClick("",m_LocationEx);
                }
                if(m_GPSPositionCallback != null)
                {
                    m_GPSPositionCallback.OnClick("",m_LocationEx);
                }
            }});
    }

    //打开GPS，开始时行接收数据，注意此时的GPS设备要处于打开状态
    public boolean GPS_OpenClose = false;   //GPS的开关状态，true-开，close-关
    public boolean OpenGPS()
    {
        //获取位置管理服务
        this.m_LTManager = (LocationManager)this.m_Context.getSystemService(Context.LOCATION_SERVICE);

        if (!this.m_LTManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //试图自动打开GPS选项，但没成功
//			Intent GPSIntent = new Intent();
//			GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//			GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
//			GPSIntent.setData(Uri.parse("custom:3"));
//	        try
//	        {
//	            PendingIntent.getBroadcast(m_Context, 0, GPSIntent, 0).send();
//
//	        } catch (CanceledException e)
//	        {
            // 创建AlertDialog
            AlertDialog.Builder menuDialog = new AlertDialog.Builder(PubVar.m_DoEvent.m_Context);
            menuDialog.setTitle("系统提示");
            menuDialog.setMessage("获取精确的位置服务，需在位置设置中打开GPS，是否需要打开设置界面？");
            menuDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    PubVar.m_DoEvent.m_Context.startActivity(myIntent);
                    dialog.dismiss();
                }
            });
            menuDialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public   void  onClick(DialogInterface dialog,  int  which)
                {
                    dialog.dismiss();
                }
            });
            menuDialog.show();
            this.GPS_OpenClose = false;
            this.m_GPSMap.UpdateGPSStatus(null);
            return false;

        }

        //配置位置的证书
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   	//设置经纬度的精准度 可选参数有ACCURACY_FINE 准确 ACCURACY_COARSE 粗略
        criteria.setAltitudeRequired(true);    			    //设置是否需要获取海拔数据
        //criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH); //设置速度精度
        criteria.setBearingRequired(false);     			//设置是否需要获得方向信息
        criteria.setCostAllowed(false);     				//设置是否允许定位过程中产生资费，比如流量等
        criteria.setPowerRequirement(Criteria.POWER_LOW);   //设置耗电量的级别
        String provider = this.m_LTManager.getBestProvider(criteria, true);

        //获取最近一次GPS定位信息
        //this.m_Location = m_LTManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (this.m_LocationEx!=null)this.m_GPSMap.UpdateGPSStatus(this.m_LocationEx);

        this.m_LTManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,0,this.m_LocateListener);
        //android 5.1.1有问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //NEMA解析器
            this.m_LTManager.addNmeaListener(this.m_NemaMessageListener);
        } else {
            //NEMA解析器
            this.m_LTManager.addNmeaListener(this.m_NemaListener);
        }

        //GPS状态侦听器
        this.m_LTManager.addGpsStatusListener(this.m_GpsStatusListener);

        GPS_OpenClose = true;this.m_GPSMap.UpdateGPSStatus(null);
        return true;
    }

    private OnNmeaMessageListener m_NemaMessageListener = new OnNmeaMessageListener() {
        @Override
        public void onNmeaMessage(String arg1, long arg0) {
            m_NEMALocate.onNmeaReceived(arg0, arg1);
        }
    };

    //NEMA解析器
    private NmeaListener m_NemaListener = new NmeaListener(){
        @Override
        public void onNmeaReceived(long arg0, String arg1) {
            m_NEMALocate.onNmeaReceived(arg0, arg1);
        }};


    //位置改变侦听器
    private LocationListener m_LocateListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            if (!m_NEMALocate.GetUseNEMA())
            {
                if (location.hasAccuracy() && location.hasAltitude())
                {
                    if(location.getLongitude()>0&&location.getLatitude()>0){
                        m_LocationEx.SetType(lkGpsLocationType.enInGps);
                        m_LocationEx.SetInGpsLocate(location);
                        m_LocationEx.SetGpsLongitude(location.getLongitude());
                        m_LocationEx.SetGpsLatitude(location.getLatitude());
                        m_LocationEx.SetGpsAltitude(location.getAltitude());
                        m_LocationEx.SetGpsSpeed(location.getSpeed());
//                    m_LocationEx.SetGpsTime(location.getTime());
                        m_GPSMap.UpdateGPSStatus(m_LocationEx);
                        Log.d("onLocationChanged", "m_GPSMap已更新");
                        try
                        {
                            //TODO:记录到数据库
//                        记录到数据库LogDB logDB = new LogDB();
//                        logDB.logDBGps(m_LocationEx.GetGpsDate().substring(0,4),m_LocationEx.GetGpsDate().replace("-", ""), m_LocationEx);
                            Log.d("onLocationChanged", "gps记录被写进数据库");

                            if(mGpsCallback != null)
                            {
                                mGpsCallback.OnClick("",location);
                            }
                        }
                        catch (Exception e) {
                            Tools.ShowMessageBox(e.getMessage());
                        }
                        if (m_GPSPositionCallback !=null) m_GPSPositionCallback.OnClick("", null);
                    }

                }
            }

//            PubVar.m_DoEvent.m_Navigate.refreshNavigationData(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("不可用");
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("GPS开启");
        }

        @Override
        public void onStatusChanged(String provider, int status,Bundle extras) {
        }};


    /**
     * GPS状态侦听器
     */
    private boolean m_Sound_HaveSatellite = false;
    private boolean m_Sound_Fix = false;
    private GpsStatus.Listener m_GpsStatusListener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event)
        {
            Log.d("GPS状态", event+"");
            switch(event)
            {
                case GpsStatus.GPS_EVENT_STARTED:
                    m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_GPS开启");
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    if (m_LTManager==null) return;
                    if (m_LTManager.getGpsStatus(null)==null)return;
                    m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_GPS卫星事件");
                    int SateCount = 0;
                    Iterable<GpsSatellite> GpsSateList = m_LTManager.getGpsStatus(null).getSatellites();
                    for(GpsSatellite GpsSate :GpsSateList)SateCount++;
                    if (SateCount==0 && m_Sound_HaveSatellite)
                    {
                        m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_卫星丢失");
                        m_Sound_HaveSatellite = false;
                    }
                    if (SateCount>0)m_Sound_HaveSatellite = true;

                    //定位声音
                    if (AlwaysFix())
                    {
                        if(!m_Sound_Fix)m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_定位");
                        m_Sound_Fix = true;
                    } else
                    {
                        if(m_Sound_Fix)m_GPSMap.GetGpsInfoManage().UpdateGPSStatus("Music_未定位");
                        m_Sound_Fix=false;
                    }
            }

            m_GPSMap.UpdateGPSStatus(null);
            PubVar.m_MapControl.invalidate();
        }};


    //关闭GPS
    public boolean CloseGPS()
    {
        if(this.m_LTManager!=null) {
            this.m_LTManager.removeUpdates(this.m_LocateListener);
            this.m_LTManager.removeGpsStatusListener(this.m_GpsStatusListener);
            this.m_LTManager.removeNmeaListener(this.m_NemaListener);
            this.GPS_OpenClose = false;
            this.m_GPSMap.UpdateGPSStatus(null);
            this.m_LTManager = null;
        }
        return true;
    }

    /**
     * GPS是否已经定位，条件参于解算的卫星数大于4粒，定位精度在15米以内
     * @return
     */
    public boolean AlwaysFix()
    {

        if (this.m_LTManager==null || this.m_LocationEx==null)
        {
            return false;
        }

        if (!this.m_NEMALocate.GetUseNEMA())
        {
            //计算参于解算的卫星数
            int FixSateCount = 0;
            Iterable<GpsSatellite> GpsSateList = this.m_LTManager.getGpsStatus(null).getSatellites();
            for(GpsSatellite GpsSate :GpsSateList)
            {
                if (GpsSate.usedInFix())FixSateCount++;
            }

            if (FixSateCount>=4 && this.m_LocationEx.GetInGpsLocate().hasAccuracy() && this.m_LocationEx.GetInGpsLocate().hasAltitude() &&
                    this.m_LocationEx.GetInGpsLocate().getAccuracy()<=15)
            {
                return true;
            }
        }
        else
        {
            if (this.m_LocationEx.GetGpsFixMode()== lkGpsFixMode.en3DFix) return true;

//			int FixSateCount = 0;
//			List<NEMASatellite> nemaSateList = this.m_NEMALocate.GetSatelliteList();
//			for(NEMASatellite GpsSate :nemaSateList)if (GpsSate.getUsedInFix())FixSateCount++;
//
//			if (this.m_LocationEx.GetGpsFixMode()==lkGpsFixMode.en2DFix && FixSateCount>=5 && this.m_LocationEx.GetGpsPDOP()<=3) return true;
        }
        return false;
    }

    /**
     * 根据定位精度及当前卫星信息强弱区分定位级数，共分5级
     * @return
     */
    public int GetLevelForAlwaysFix()
    {
        //计算参于解算的卫星数
        int FixSateCount = 0;
        if (!this.m_NEMALocate.GetUseNEMA())
        {
            Iterable<GpsSatellite> GpsSateList = this.m_LTManager.getGpsStatus(null).getSatellites();
            for(GpsSatellite GpsSate :GpsSateList)
            {
                if (GpsSate.usedInFix())FixSateCount++;
            }

            float jd = this.m_LocationEx.GetInGpsLocate().getAccuracy();

        }
        else
        {
            List<NEMASatellite> nemaSateList = this.m_NEMALocate.GetSatelliteList();
            for(NEMASatellite GpsSate :nemaSateList)
            {
                if (GpsSate.getUsedInFix())FixSateCount++;
            }
        }

        if (FixSateCount==0) return 0;
        if (FixSateCount>=1 && FixSateCount<=4) return 1;
        if (FixSateCount>=5 && FixSateCount<=7) return 2;
        if (FixSateCount>=8 && FixSateCount<=11) return 3;
        if (FixSateCount>=12) return 4;
        return 0;
    }




    /**
     * 得取GPS平面坐标
     * @return
     */
    public Coordinate getGPSCoordinate()
    {
        return StaticObject.soProjectSystem.WGS84ToXY(this.m_LocationEx.GetGpsLongitude(), this.m_LocationEx.GetGpsLatitude(),this.m_LocationEx.GetGpsAltitude());
    }

    /**
     * 得取GPS经纬度坐标
     * @return
     */
    public String getJWGPSCoordinate()
    {
        DecimalFormat df = new DecimalFormat("#.000000");
        return df.format(this.m_LocationEx.GetGpsLongitude())+","+ df.format(this.m_LocationEx.GetGpsLatitude());
    }

    /**
     * 返回高程值
     * @return
     */
    public String getGC()
    {
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(this.m_LocationEx.GetGpsAltitude());
    }

    /**
     * 定位精度
     * @return
     */
    public String getAccuracy()
    {
        if (this.m_NEMALocate.GetUseNEMA())
        {
            return this.m_LocationEx.GetGpsPDOP()+"";
        }
        else
        {
            if (this.m_LocationEx.GetInGpsLocate()==null) return "0";
            return this.m_LocationEx.GetInGpsLocate().getAccuracy()+"";
        }
    }

    /**
     * 得到GPS速度
     * @return
     */
    public String getGPSSpeed()
    {
        float _SValue = 0;
        if (!this.m_NEMALocate.GetUseNEMA())
        {
            _SValue=((float)this.m_LocationEx.GetGpsSpeed())*3.6f; //meters/s->km/h
        }
        else
        {
            _SValue=(float)this.m_LocationEx.GetGpsSpeed();
        }
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(_SValue).toString();
    }

    //得到GPS时间
    public String getGPSDate()
    {
        if (!this.m_NEMALocate.GetUseNEMA())
        {
            Date DT = new Date(this.m_LocationEx.GetInGpsLocate().getTime()+24*60*60);
            SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return SDF.format(DT);
        }
        else
        {
            return this.m_LocationEx.GetGpsDate()+" "+this.m_LocationEx.GetGpsTime();
        }
    }

    public String[] getGPSDateForPhotoFormat()
    {
        String[] Dt = this.getGPSDate().split(" ");
        Dt[0] = Dt[0].replaceAll("-",":");
        Dt[1] = Dt[1].replaceAll(":","/1,")+"/1";
        return Dt;
    }
}
