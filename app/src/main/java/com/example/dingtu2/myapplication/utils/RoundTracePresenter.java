package com.example.dingtu2.myapplication.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Cargeometry.Part;
import com.DingTu.Cargeometry.Polyline;
import com.DingTu.Data.GpsDataObject;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Enum.lkDataCollectType;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkGpsReceiveDataStatus;
import com.DingTu.GPS.LocationEx;
import com.DingTu.Map.StaticObject;
import com.DingTu.mapcontainer.IOnPaint;
import com.DingTu.mapcontainer.Pan;
import com.DingTu.mapcontainer.ZoomInOutPan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/7/5.
 */

public class RoundTracePresenter implements IOnPaint {

    //相关数据集
    private Dataset m_Dataset = null;
    //采集实体
    private GpsDataObject m_GpsBaseObj = new GpsDataObject();

    //移屏类
    private Pan m_Pan = null;
    private ZoomInOutPan m_ZoomPan = null;
    private lkDataCollectType m_DataCollectType = lkDataCollectType.enUnkonw;             //数据采集模式
    private lkGpsReceiveDataStatus m_DataCollectStatus = lkGpsReceiveDataStatus.enStop;  //数据采集状态
    private List<Coordinate> m_GPSTrackPointList = new ArrayList<Coordinate>();             //GPS跟踪线的轨迹点

    //跟踪测量值
    private List<MValue> m_MValueList = new ArrayList<MValue>();
    private long m_BeforeGpsPosUpateTime = 0;
    private ICallback mTraceCallback = null;

    public RoundTracePresenter() {

        this.m_Pan = new Pan(PubVar.m_MapControl);
        this.m_ZoomPan = new ZoomInOutPan(PubVar.m_MapControl);
    }

    //开始采集
    public void Start(lkDataCollectType dct) {
        this.m_DataCollectType = dct;
        this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;

        if (this.m_DataCollectType == lkDataCollectType.enGps_T) {
        }
    }

    //采集实体
//    private v1_CGpsDataObject m_GpsBaseObj = new v1_CGpsDataObject();

    public void Stop() {
        this.Stop(lkGeoLayerType.enPolyline);
    }

    public void Stop(lkGeoLayerType geoType) {
        //检查图层的有效性

        if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop) {
            Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("当前没有正在采集的实体！"));
            return;
        }

        //判断已经采集线点数，如果少于2个，则不存
        if ((this.m_GPSTrackPointList.size() >= 2 && geoType == lkGeoLayerType.enPolyline) ||
                (this.m_GPSTrackPointList.size() >= 3 && geoType == lkGeoLayerType.enPolygon)) {
            //加入最后一个采集点
            if (Tools.ReadyGPS(false)) {
//                this.AddPoint(PubVar.m_DoEvent.m_GPSLocate.getGPSCoordinate());
            }

            int SYS_ID = this.SaveGeoToDb(true);
            Log.d("saveGeo", "GeoID:" + SYS_ID);
            if (SYS_ID != -1) {

            }
        }
        PubVar.m_DoEvent.mGPSSatus.UpdateLineStatus("结束", 0);
        this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStop;
        this.m_GPSTrackPointList.clear();
        this.m_MValueList.clear();
        //this.m_DataCollectType = lkDataCollectType.enUnkonw;

        PubVar.m_Map.Refresh();
    }

    /**
     * 保存图形数据
     *
     * @param EndSave 是否为结束采集后的保存操作
     * @return
     */
    public int SaveGeoToDb(boolean EndSave) {
        Geometry m_GpsGeometry = null;

        double dataLen = 0, dataArea = 0;  //长度与面积

        String SYS_TYPE = "采集线形";       //类型
        List<Coordinate> newCoorList = new ArrayList<Coordinate>();
        for (Coordinate Coor : this.m_GPSTrackPointList) newCoorList.add(Coor.Clone());

        if (this.m_Dataset.getType() == lkGeoLayerType.enPolyline) {
            SYS_TYPE = "采集线形";
            m_GpsGeometry = new Polyline();
            m_GpsGeometry.AddPart(new Part());
            m_GpsGeometry.GetPartAt(0).setVertext(newCoorList);
            dataLen = ((Polyline) m_GpsGeometry).getLength(true);
        }


        m_GpsGeometry.CalEnvelope();
        this.m_GpsBaseObj.SetSYS_TYPE(SYS_TYPE);
        this.m_GpsBaseObj.SetSYS_STATUS("0");

        int SYS_ID = this.m_GpsBaseObj.SaveGeoToDb(m_GpsGeometry, dataLen, dataArea);
        this.m_GpsBaseObj.SetSYS_ID(SYS_ID);

        return SYS_ID;
//        return 0;
    }

    public void UpdateGpsPosition(LocationEx location, boolean isSave) {

        if (this.m_DataCollectType != lkDataCollectType.enGps_T) {
            return;
        }

        Coordinate coord = StaticObject.soProjectSystem.WGS84ToXY(location.GetGpsLongitude(), location.GetGpsLatitude(), location.GetGpsAltitude());
        if (coord == null || coord.getY() == 0 || coord.getX() == 0) {
            return;
        }


        //在此控制采集的时间间隔及最小距离，在系统变量 Tag_System_GPSMinTime,Tag_System_GPSMinDistance 里面

        //需要在此加入过滤条件，也就是距离太的的过滤掉，默认值在v1_UserConfigDB.LoadSystemConfig
        double MinDistance = 0;
        if (this.m_GPSTrackPointList.size() > 0) {
            Coordinate PT = this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size() - 1);
            if (PT != null) {
                MinDistance = Tools.GetTwoPointDistance(PT.getX(), PT.getY(), coord.getX(), coord.getY(), false);
                double LimitDistance = 5;
//            if (!(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value+"").equals("不限"))
//            {
//                LimitDistance = Double.parseDouble(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value+"");
//            }
                if (MinDistance < LimitDistance) {
                    return;
                }
            }

        }


        //GPS更新时间过短去掉，默认值在v1_UserConfigDB.LoadSystemConfig
        double LimitTime = 5000;
        long CurrentTime = System.currentTimeMillis();
//        if (!(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value+"").equals("不限"))
//        {
//            LimitTime = Double.parseDouble(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value+"")*1000;
//        }
        if ((CurrentTime - this.m_BeforeGpsPosUpateTime) >= (LimitTime - 100)) {
            this.m_GPSTrackPointList.add(coord);
            if (isSave) {
                if (mTraceCallback != null) {
                    mTraceCallback.OnClick("", location);
                }
            }

//            this.AddMValue(MinDistance,(this.m_CalArea?this.m_Polygon.getArea(true):0));
            if (Double.isNaN(MinDistance)) {
                Log.e("MinDistance", MinDistance + "");
                MinDistance = 0;
            }


            this.AddMValue(MinDistance, 0);
            this.m_BeforeGpsPosUpateTime = CurrentTime;
//            PubVar.m_DoEvent.m_SoundTool.PlaySound(5);   //连续打点声音
        }
    }

    private void AddMValue(double length, double area) {
        MValue mv = new MValue();
        if (this.m_MValueList.size() == 0) {
            mv.Length = length;
            mv.Area = area;
        } else {
            mv.Length = this.m_MValueList.get(this.m_MValueList.size() - 1).Length + length;
            mv.Area = area;
        }
        this.m_MValueList.add(mv);
    }

    public void setTraceCallback(ICallback callback) {
        mTraceCallback = callback;
    }

    @Override
    public void OnPaint(Canvas canvas) {
        this.m_ZoomPan.OnPaint(canvas);
        if (PubVar.m_Map.getInvalidMap()) return;

        if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop) {
            this.UpdateDataInStatus();
            return;

        }
        //绘制轨迹点信息，形成轨迹线
        Point[] PList = PubVar.m_MapControl.getMap().getViewConvert().MapPointsToScreePoints(this.m_GPSTrackPointList);
        if (PList.length > 0) {
            Path p = new Path();
            for (int i = 0; i < PList.length; i++) {
                if (i == 0) p.moveTo(PList[i].x, PList[i].y);
                else p.lineTo(PList[i].x, PList[i].y);
            }

            Paint pPen = new Paint();
            pPen.setStyle(Paint.Style.STROKE);
            pPen.setStrokeWidth(Tools.DPToPix(5));
            pPen.setColor(Color.RED);
            canvas.drawPath(p, pPen);
        }

        //绘制轨迹节点
        int H = Tools.DPToPix(10);

        Paint pBrush = new Paint();
//        for(int i=0;i<PList.length;i++)
//        {
//            pBrush.setStyle(Paint.Style.FILL);
//            pBrush.setColor(Color.YELLOW);
//            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);
//
//            pBrush.setStyle(Paint.Style.STROKE);
//            pBrush.setStrokeWidth(2);
//            pBrush.setColor(Color.RED);
//            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);
//        }

        if (PList.length > 0) {
            pBrush.setStyle(Paint.Style.FILL);
            pBrush.setColor(Color.RED);
            pBrush.setStrokeWidth(5);
            pBrush.setColor(Color.RED);
            canvas.drawCircle(PList[0].x, PList[0].y, H / 2, pBrush);

        }

        if (PList.length > 1) {

            pBrush.setColor(Color.YELLOW);
            pBrush.setStyle(Paint.Style.STROKE);
            pBrush.setStrokeWidth(Tools.DPToPix(3));
            canvas.drawCircle(PList[PList.length - 1].x, PList[PList.length - 1].y, 8, pBrush);
        }


        //在最后一个节点处画一个圆圈，表示流模式下可以续接的范围
        if (this.m_DataCollectType == lkDataCollectType.enManual) {

        }

        this.UpdateDataInStatus();
    }

    private void UpdateDataInStatus() {

        String mType = "巡护";
        double mValue = 0;
        MValue pMValue = (this.m_MValueList.size() > 0 ? this.m_MValueList.get(this.m_MValueList.size() - 1) : null);
        mValue = (pMValue != null ? pMValue.Length : 0);

//        PubVar.m_DoEvent.mGPSSatus.UpdateLineStatus(mType, mValue);;
//        PubVar.m_DoEvent.mGPSSatus.UpdateRoundStatus();
    }

    private class MValue {
        public double Length = 0;
        public double Area = 0;
    }
}
