package com.DingTu.Data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Region;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Cargeometry.Part;
import com.DingTu.Cargeometry.Polygon;
import com.DingTu.Cargeometry.Polyline;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Enum.lkDataCollectType;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkGpsReceiveDataStatus;
import com.DingTu.Enum.lkPartType;
import com.DingTu.Enum.lkReUndoCommand;
import com.DingTu.GPS.LocationEx;
import com.DingTu.Map.StaticObject;
import com.DingTu.mapcontainer.IOnPaint;
import com.DingTu.mapcontainer.IOnTouchCommand;
import com.DingTu.mapcontainer.Pan;
import com.DingTu.mapcontainer.ZoomInOutPan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/2.
 */

public class GpsLine implements IOnTouchCommand,IOnPaint {
    //相关数据集
    private Dataset m_Dataset = null;

    //移屏类
    private Pan m_Pan = null;
    private ZoomInOutPan m_ZoomPan =null;
    /**
     * 设置相关数据集Dataset
     * @param pDataset
     */
    public void SetDataset(Dataset pDataset)
    {
        this.m_Dataset = pDataset;
        this.m_GpsBaseObj.SetDataset(pDataset);
    }
    public Dataset GetDataset(){return this.m_Dataset;}

    /**
     * 检查图层的有效性
     * @return
     */
    public boolean CheckLayerValid()
    {
        //检查图层的有效性
        String LayerID = "";
        if (this.m_Dataset!=null) LayerID = this.m_Dataset.getId();
        return PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID);
    }

    //用于计算面积的面类
    private Polygon m_Polygon = null;
    public GpsLine(){
        this.m_Polygon = new Polygon();
        this.m_Polygon.AddPart(new Part(this.m_GPSTrackPointList));

        this.m_GestureDetector = new GestureDetector(PubVar.m_MapControl.getContext(),this.m_MyOnGestureListener);
        this.m_Pan = new Pan(PubVar.m_MapControl);
        this.m_ZoomPan = new ZoomInOutPan(PubVar.m_MapControl);

    }

    /**
     * 编辑属性
     * @param LayerID 图层ID
     * @param SYS_ID 实体ID
     */
    public void Edit(String LayerID,int SYS_ID)
    {

        Edit(LayerID,SYS_ID,false);
    }

    public void Edit(String LayerID,int SYS_ID,boolean firstEdit)
    {
//        if (!PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID))return;
//
//        String projectType = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(LayerID).GetLayerProjecType();
//        if(projectType != null && projectType.equals(ForestryLayerType.TuigengLayer))
//        {
//            TuiGengData tuiGengData = new TuiGengData(LayerID,SYS_ID,firstEdit);
//        }
//        else if(projectType != null && projectType.equals(ForestryLayerType.LindibiangengLayer))
//        {
//            LinDiBianGengData ldbg = new LinDiBianGengData(LayerID,SYS_ID);
//            ldbg.ShowView();
//
//        }
//        else if(projectType != null && projectType.equals(ForestryLayerType.WeipianJianchaLayer))
//        {
//            WeiPianZhiFaData wpzf = new WeiPianZhiFaData(LayerID,SYS_ID);
//            wpzf.ShowView();
//        }
//        else
//        {
            this.Edit(LayerID, SYS_ID,null);
//        }
//
    }
    public void Edit(String LayerID,int SYS_ID,ICallback cb)
    {
//    	TanhuiDataTemplate _DT = new TanhuiDataTemplate();
//    	_DT.SetEditInfo(LayerID, SYS_ID);
//    	_DT.SetCallback(cb);
//    	_DT.ShowDialog();

//        GeneralDateEditor dataEdit = new GeneralDateEditor(LayerID, SYS_ID);
//        dataEdit.SetCallback(cb);
    }

    //检查是否正在采集路线
    public boolean CheckIfStarting()
    {
        if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop) return false;
        else return true;
    }


    //开始采集
    public void Start(lkDataCollectType dct)
    {
        this.m_DataCollectType = dct;
        this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;

        if (this.m_DataCollectType==lkDataCollectType.enGps_T)
        {
            if (!Tools.ReadyGPS(true)) return;
        }
    }

    //暂停采集
    public void Pause()
    {
//        if (PubVar.m_GPSMap==null) return;
//        if (PubVar.m_GPSMap.GpsReceiveDataStatus == lkmap.Enum.lkGpsReceiveDataStatus.enPause)
//        {
//            PubVar.m_GPSMap.GpsReceiveDataStatus = lkmap.Enum.lkGpsReceiveDataStatus.enStarting;
//            PubVar.m_Map.FastRefresh();
//            return;
//        }
//        if (PubVar.m_GPSMap.GpsReceiveDataStatus == lkmap.Enum.lkGpsReceiveDataStatus.enStarting)
//        {
//            PubVar.m_GPSMap.GpsReceiveDataStatus = lkmap.Enum.lkGpsReceiveDataStatus.enPause;
//            PubVar.m_Map.FastRefresh();
//            return;
//        }
    }

    /**
     * 编辑当前正在采集线的属性
     */
    public void Edit()
    {
        if (!this.CheckLayerValid()) return;
        if (this.m_DataCollectStatus!=lkGpsReceiveDataStatus.enStop)
        {
            if (this.m_GpsBaseObj.GetSYS_ID()!=-1)
            {
                this.Edit(this.m_Dataset.getId(), this.m_GpsBaseObj.GetSYS_ID());
                return;
            }
        }
        Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("无法编辑当前正在采集实体属性！"));
    }

    /**
     * 取消当前正在采集的线形
     */
    public void Cancel()
    {
        if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop)
        {
            Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("当前没有正在采集的实体！"));
            return;
        }
        this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStop;
        this.m_GPSTrackPointList.clear();
        //清空回退栈
        this.m_UndoCoordinateList.clear();
        this.m_MValueList.clear();
        //this.m_DataCollectType = lkDataCollectType.enUnkonw;
        PubVar.m_MapControl.invalidate();
    }

    //绘制线的节点回退栈，为重新做准备
    private List<Coordinate> m_UndoCoordinateList = new ArrayList<Coordinate>();
    /**
     * 回退当前正在采集的线型（1步）
     */
    public boolean Undo()
    {
        if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop)
        {
            Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("当前没有正在采集的实体！"));
            return false;
        }
        if (this.m_GPSTrackPointList.size()>0)
        {
            this.m_UndoCoordinateList.add(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-1));
            this.m_GPSTrackPointList.remove(this.m_GPSTrackPointList.size()-1);
            if(m_MValueList.size()>0)
            {
                this.m_MValueList.remove(this.m_MValueList.size()-1);
            }

            PubVar.m_MapControl.invalidate();
            return true;
        } else
        {
            //lkmap.Tools.Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("节点数已为0，无法回退！"));
            return false;
        }
    }
    /**
     * 重做绘制线
     * @return
     */
    public boolean Redo()
    {
        if (this.m_UndoCoordinateList.size()>0)
        {
            Coordinate ptCoor = this.m_UndoCoordinateList.get(this.m_UndoCoordinateList.size()-1);
            this.m_UndoCoordinateList.remove(ptCoor);
            this.m_GPSTrackPointList.add(ptCoor);
            if (this.m_GPSTrackPointList.size()>1)
            {
                this.AddMValue(Tools.GetTwoPointDistance(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-2),ptCoor),
                        (this.m_CalArea?this.m_Polygon.getArea(true):0));
            }else
            {
                this.AddMValue(0,0);
            }

            PubVar.m_MapControl.invalidate();

            return true;
        }
        return false;
    }

    public void Stop()
    {
        this.Stop(lkGeoLayerType.enPolyline);
    }
    public void Stop(lkGeoLayerType geoType)
    {
        //检查图层的有效性
        if (!PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(this.m_Dataset.getId()))return;
        if (this.m_DataCollectStatus == lkGpsReceiveDataStatus.enStop)
        {
            Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("当前没有正在采集的实体！"));
            return;
        }

        //判断已经采集线点数，如果少于2个，则不存
        if ((this.m_GPSTrackPointList.size() >= 2 && geoType==lkGeoLayerType.enPolyline)||
                (this.m_GPSTrackPointList.size() >= 3 && geoType==lkGeoLayerType.enPolygon))
        {
            //加入最后一个采集点
            if (Tools.ReadyGPS(false))
            {
                this.AddPoint(PubVar.m_DoEvent.m_GPSLocate.getGPSCoordinate());
            }

            int SYS_ID = this.SaveGeoToDb(true);
            if (SYS_ID!=-1)
            {
                this.Edit(this.m_Dataset.getId(),SYS_ID,true);

                //加入回退栈内
//                UnRedoParaStru UnRedoPara = new UnRedoParaStru();
//                UnRedoPara.Command = lkReUndoCommand.enAddDeleteObject;
//                UnRedoDataItem urDataItem = new UnRedoDataItem();
//                urDataItem.Type = lkReUndoFlag.enRedo;
//                IURDataItem_DeleteAdd uiAdd = new IURDataItem_DeleteAdd();
//                uiAdd.LayerId = this.m_Dataset.getId();
//                uiAdd.ObjectIdList.add(SYS_ID);
//                urDataItem.DataList.add(uiAdd);
//                UnRedoPara.DataItemList.add(urDataItem);
//                IUnRedo.AddHistory(UnRedoPara);
            }
        }

        this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStop;
        this.m_GPSTrackPointList.clear();
        //清空回退栈
        this.m_UndoCoordinateList.clear();
        this.m_MValueList.clear();
        //this.m_DataCollectType = lkDataCollectType.enUnkonw;

        PubVar.m_Map.Refresh();



    }

    //采集实体
    private GpsDataObject m_GpsBaseObj = new GpsDataObject();


    /**
     * 保存图形数据
     * @param EndSave 是否为结束采集后的保存操作
     * @return
     */
    public int SaveGeoToDb(boolean EndSave)
    {
        Geometry m_GpsGeometry = null;

        double dataLen = 0,dataArea = 0;  //长度与面积

        String SYS_TYPE = "采集线形";	   //类型
        List<Coordinate> newCoorList = new ArrayList<Coordinate>();
        for(Coordinate Coor :this.m_GPSTrackPointList)newCoorList.add(Coor.Clone());

        if (this.m_Dataset.getType()==lkGeoLayerType.enPolyline)
        {
            SYS_TYPE = "采集线形";
            m_GpsGeometry = new Polyline();
            m_GpsGeometry.AddPart(new Part());
            m_GpsGeometry.GetPartAt(0).setVertext(newCoorList);
            dataLen = ((Polyline)m_GpsGeometry).getLength(true);
        }

        if (this.m_Dataset.getType()==lkGeoLayerType.enPolygon)
        {
            SYS_TYPE = "采集面";
            m_GpsGeometry = new Polygon();
            Part part = new Part();
            m_GpsGeometry.AddPart(part);
            newCoorList.add(this.m_GPSTrackPointList.get(0).Clone()); //面闭合
            m_GpsGeometry.GetPartAt(0).setVertext(newCoorList);

            //结束采集，计算长度与面积
            //这样处理只要是纠正面的方向，面为逆时针方向，洞为顺时针
            m_GpsGeometry.GetPartAt(0).SetPartType(lkPartType.enPoly);
            dataLen = ((Polygon)m_GpsGeometry).getLength(true);
            dataArea = ((Polygon)m_GpsGeometry).getArea(true);
        }

        m_GpsGeometry.CalEnvelope();
        this.m_GpsBaseObj.SetSYS_TYPE(SYS_TYPE);
        this.m_GpsBaseObj.SetSYS_STATUS("0");

        int SYS_ID = this.m_GpsBaseObj.SaveGeoToDb(m_GpsGeometry,dataLen,dataArea);
        this.m_GpsBaseObj.SetSYS_ID(-1);

        return SYS_ID;
    }


    /**
     * 更新GPS位置坐标
     * @param newCoor
     */
    private long m_BeforeGpsPosUpateTime = 0;
    public void UpdateGpsPosition(Coordinate newCoor)
    {
        if (this.m_DataCollectStatus==lkGpsReceiveDataStatus.enStop) return;
        if (this.m_DataCollectType!=lkDataCollectType.enGps_T)return;

        //在此控制采集的时间间隔及最小距离，在系统变量 Tag_System_GPSMinTime,Tag_System_GPSMinDistance 里面

        //需要在此加入过滤条件，也就是距离太的的过滤掉，默认值在v1_UserConfigDB.LoadSystemConfig
        double MinDistance = 0;
        if (this.m_GPSTrackPointList.size()>0)
        {
            Coordinate PT = this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-1);
            MinDistance = Tools.GetTwoPointDistance(newCoor, PT);

            double LimitDistance = 5;
            if (!(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value+"").equals("不限"))
            {
                LimitDistance = Double.parseDouble(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance").Value+"");
            }
            if (MinDistance<LimitDistance) return;
        }


        //GPS更新时间过短去掉，默认值在v1_UserConfigDB.LoadSystemConfig
        double LimitTime = 0;
        long CurrentTime = System.currentTimeMillis();
        if (!(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value+"").equals("不限"))
        {
            LimitTime = Double.parseDouble(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime").Value+"")*1000;
        }
        if ((CurrentTime-this.m_BeforeGpsPosUpateTime)>=(LimitTime-100))
        {
            this.m_GPSTrackPointList.add(newCoor);
            this.AddMValue(MinDistance,(this.m_CalArea?this.m_Polygon.getArea(true):0));
            this.m_BeforeGpsPosUpateTime = CurrentTime;
//            PubVar.m_DoEvent.m_SoundTool.PlaySound(5);   //连续打点声音
            //清空回退栈
            this.m_UndoCoordinateList.clear();
        }
    }

    /**
     * GPS线参数
     */
    private lkDataCollectType m_DataCollectType = lkDataCollectType.enUnkonw;    		 //数据采集模式
    private lkGpsReceiveDataStatus m_DataCollectStatus = lkGpsReceiveDataStatus.enStop;  //数据采集状态
    private List<Coordinate> m_GPSTrackPointList = new ArrayList<Coordinate>();	 		 //GPS跟踪线的轨迹点
    public List<Coordinate> GetTrackPointList(){return this.m_GPSTrackPointList;}

    //跟踪测量值
    private List<MValue> m_MValueList = new ArrayList<MValue>();

    /**
     * 更新测量值
     * @param length
     * @param area
     */
    private void AddMValue(double length,double area)
    {
        MValue mv = new MValue();
        if (this.m_MValueList.size()==0){mv.Length = length;mv.Area = area;}
        else
        {
            mv.Length = this.m_MValueList.get(this.m_MValueList.size()-1).Length+length;
            mv.Area = area;
        }
        this.m_MValueList.add(mv);
    }

    //是否动态计算面积，主要是用于采集面
    private boolean m_CalArea = false;

    /**
     * 动态计算的类型
     * @param calArea
     */
    public void SetIfCalArea(boolean calArea){
        this.m_CalArea = calArea;
    }


    /**
     * 改变绘图方向
     */
    public void ChangeEditDirection()
    {
        if (this.m_GPSTrackPointList.size()<=1) return;
        List<Coordinate> CoorList = new ArrayList<Coordinate>();
        for(int i=this.m_GPSTrackPointList.size()-1;i>=0;i--)
        {
            CoorList.add(this.m_GPSTrackPointList.get(i));
        }
        this.m_GPSTrackPointList.clear();
        for(Coordinate Coor:CoorList)
        {
            this.m_GPSTrackPointList.add(Coor);
        }

        //重新整理测量值
        this.m_MValueList.clear();

        if (this.m_GPSTrackPointList.size()>1)
        {
            for(int i=0;i<this.m_GPSTrackPointList.size()-1;i++)
            {
                this.AddMValue(Tools.GetTwoPointDistance(this.m_GPSTrackPointList.get(i),this.m_GPSTrackPointList.get(i+1)),
                        (this.m_CalArea?this.m_Polygon.getArea(true):0));
            }
        }else
        {
            if (this.m_GPSTrackPointList.size()==1)this.AddMValue(0,0);
        }

        this.m_Pan.SetNewCenter(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-1));
        //清空回退栈
        this.m_UndoCoordinateList.clear();
    }

    /**
     * 手动画线接口
     */
    @Override
    public void OnPaint(Canvas canvas)
    {
        this.m_ZoomPan.OnPaint(canvas);
        if (PubVar.m_Map.getInvalidMap()) return;

        if (this.m_DataCollectStatus==lkGpsReceiveDataStatus.enStop)
        {
            this.UpdateDataInStatus();
            return;

        }
        //绘制轨迹点信息，形成轨迹线
        Point[] PList = PubVar.m_MapControl.getMap().getViewConvert().MapPointsToScreePoints(this.m_GPSTrackPointList);
        if (PList.length > 0)
        {
            Path p = new Path();
            for(int i=0;i<PList.length;i++)
            {
                if (i==0)p.moveTo(PList[i].x, PList[i].y);
                else p.lineTo(PList[i].x, PList[i].y);
            }
            if(PList.length>2)
            {
                p.lineTo(PList[0].x,PList[0].y);
            }
            Paint pPen = new Paint();
            pPen.setStyle(Paint.Style.STROKE);
            pPen.setStrokeWidth(Tools.DPToPix(3));
            pPen.setColor(Color.RED);
            canvas.drawPath(p, pPen);
        }

        //绘制轨迹节点
        int H = Tools.DPToPix(5);
        Paint pBrush = new Paint();
        for(int i=0;i<PList.length;i++)
        {
            pBrush.setStyle(Paint.Style.FILL);
            pBrush.setColor(Color.YELLOW);
            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);

            pBrush.setStyle(Paint.Style.STROKE);
            pBrush.setStrokeWidth(1);
            pBrush.setColor(Color.RED);
            canvas.drawCircle(PList[i].x, PList[i].y, H/2, pBrush);
        }

        //在最后一个节点处画一个圆圈，表示流模式下可以续接的范围
        if (this.m_DataCollectType==lkDataCollectType.enManual)
        {
            if (PList.length<1) return;
            pBrush.setColor(Color.BLUE);
            pBrush.setStrokeWidth(Tools.DPToPix(3));
            canvas.drawCircle(PList[PList.length-1].x, PList[PList.length-1].y, this.m_Tolerance/2, pBrush);
        }

        this.UpdateDataInStatus();
    }

    /**
     * 更新数据采集状态
     */
    private void UpdateDataInStatus()
    {
        //刷新采集状态
        MValue pMValue = (this.m_MValueList.size()>0?this.m_MValueList.get(this.m_MValueList.size()-1):null);
        String mType = "";double mValue = 0;
        if (this.m_DataCollectStatus==lkGpsReceiveDataStatus.enStop ||this.m_DataCollectType==lkDataCollectType.enUnkonw )
        {
            mType = "停止";mValue=0;
        }
        else
        {
            if (this.m_DataCollectType==lkDataCollectType.enManual)
            {
                mType = "手绘";mValue=0;
                if (this.m_CalArea) mValue = (pMValue!=null?pMValue.Area:0);
                else mValue = (pMValue!=null?pMValue.Length:0);
            }
            if (this.m_DataCollectType==lkDataCollectType.enGps_P || this.m_DataCollectType==lkDataCollectType.enGps_T )
            {
                mType = "GPS";mValue=0;
                if (this.m_CalArea) mValue = (pMValue!=null?pMValue.Area:0);
                else mValue = (pMValue!=null?pMValue.Length:0);
            }
        }

        if (this.m_CalArea)
        {
            PubVar.m_DoEvent.mGPSSatus.UpdatePolyStatus(mType, mValue);return;
        }
        else
        {
            PubVar.m_DoEvent.mGPSSatus.UpdateLineStatus(mType, mValue);return;
        }
    }

    /**
     * 增加平均点
     */
//    public void AddAveragePoint()
//    {
//        v1_Data_Template _DT = new v1_Data_Template();
//        _DT.SetCalAveragePoint(true);
//        _DT.SetEditInfo(this.m_Dataset.getId(),this.m_GpsBaseObj.GetSYS_ID());
//        _DT.ShowDialog();
//    }

    /**
     * 增加点位，GPS定点
     * @param ptCoor
     */
    public void AddPoint(Coordinate ptCoor)
    {
        if (this.m_DataCollectStatus==lkGpsReceiveDataStatus.enStop) return;
        if (this.m_DataCollectType== lkDataCollectType.enGps_P)
        {
            this.m_GPSTrackPointList.add(ptCoor);
            if (this.m_GPSTrackPointList.size()>1)
            {
                this.AddMValue(Tools.GetTwoPointDistance(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-2),ptCoor),
                        (this.m_CalArea?this.m_Polygon.getArea(true):0));
            }else
            {
                this.AddMValue(0,0);
            }
            //清空回退栈
            this.m_UndoCoordinateList.clear();
            PubVar.m_MapControl.invalidate();
        }
    }

    private void AddPointByHandOnMouseDown(MotionEvent e)
    {
        //PubVar.m_DoEvent.m_GlassView.SetVisible(false);
        //if (this.m_DataCollectStatus==lkGpsReceiveDataStatus.enStop) return;
        if (this.m_DataCollectStatus==lkGpsReceiveDataStatus.enStop)
        {
            this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;
        }
        this.m_DataCollectStatus = lkGpsReceiveDataStatus.enStarting;
        if (this.m_DataCollectType== lkDataCollectType.enManual)
        {
            PointF pt = new PointF(e.getX(),e.getY());

            Coordinate mPoint = PubVar.m_Map.getViewConvert().ScreenToMap(pt);


            if(this.m_GPSTrackPointList.size()>=1)
            {

                PointF ptLast = PubVar.m_Map.getViewConvert().MapToScreenF(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-1).getX(),
                        this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-1).getY());

                //抽稀的px大小
                int buff = 20;

                if(Math.abs((int)ptLast.x-(int)pt.x)>=buff||Math.abs((int)ptLast.y-(int)pt.y)>=buff)
                {

                    this.m_GPSTrackPointList.add(mPoint);
                    this.AddMValue(Tools.GetTwoPointDistance(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-2),mPoint),
                            (this.m_CalArea?this.m_Polygon.getArea(true):0));

                }
                else
                {

                }

            }
            else
            {
                this.m_GPSTrackPointList.add(mPoint);
                this.AddMValue(0,0);
            }

//			Coordinate mPoint = PubVar.m_Map.getViewConvert().ScreenToMap(pt);
//		    //将Map坐标转换为地理坐标，只适用于手动加点的情况
//		    //Coordinate lb84 = StaticObject.soProjectSystem.XYToWGS84(mPoint);
//		    //mPoint.setGeoX(lb84.getX());mPoint.setGeoY(lb84.getY());
//
//			this.m_GPSTrackPointList.add(mPoint);
//    		if (this.m_GPSTrackPointList.size()>1)
//    		{
//    			this.AddMValue(Tools.GetTwoPointDistance(this.m_GPSTrackPointList.get(this.m_GPSTrackPointList.size()-2),mPoint),
//    						  (this.m_CalArea?this.m_Polygon.getArea(true):0));
//    		}else
//    		{
//    			this.AddMValue(0,0);
//    		}
//    		//清空回退栈
            this.m_UndoCoordinateList.clear();
            PubVar.m_MapControl.invalidate();

        }
    }

//	public void MouseDown(MotionEvent e)
//	{
//		this.AddPointByHandOnMouseDown(e);
//	}


    //是否启用流模式绘图
    private boolean m_StreamMode = true;
    /**
     * 设置是否启用流模式绘图
     * @param streamMode
     */
    public void SetStreamMode(boolean streamMode)
    {
        this.m_StreamMode = streamMode;
    }

    //指示是移屏还是流绘图，1-流绘图，2-移屏，判断的依据为MouseDown内是否与最后一节点靠近
    private int m_DrawOrPan = 1;
    private boolean isLongPressed = false;
    //表示流模式下可以续接的范围
    private int m_Tolerance = Tools.DPToPix(20);

    private GestureDetector m_GestureDetector = null;
    private GestureDetector.SimpleOnGestureListener m_MyOnGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onDown(MotionEvent e)
        {

            if (!m_StreamMode)
            {
                m_DrawOrPan=2;
            }
            else
            {
                m_DrawOrPan=1;
                if (m_GPSTrackPointList.size()>0)
                {
                    Coordinate mPoint = m_GPSTrackPointList.get(m_GPSTrackPointList.size()-1);
                    PointF pf = PubVar.m_Map.getViewConvert().MapToScreenF(mPoint.getX(), mPoint.getY());
                    if (Math.abs(pf.x-e.getX())>=m_Tolerance || Math.abs(pf.y-e.getY())>=m_Tolerance)
                    {
                        m_DrawOrPan = 2;
                    }
                }
                else
                {
                    AddPointByHandOnMouseDown(e);
                }
            }

            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY)
        {
            if (m_DrawOrPan==1)
            {
                AddPointByHandOnMouseDown(e2);;
            }

            if (m_DrawOrPan==2)
            {
                m_Pan.MouseDown(e1);
                m_Pan.MouseMove(e2);

            }

            //m_ZoomPan.Scroll(e1, e2, distanceX, distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            //AddPointByHandOnMouseDown(e);
            isLongPressed = true;
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            AddPointByHandOnMouseDown(e);
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            m_ZoomPan.Scroll(e1, e2, velocityX, velocityY);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

    };

    @Override
    public void SetOnTouchEvent(MotionEvent e)
    {
        this.m_GestureDetector.onTouchEvent(e);

        if ((e.getAction() & MotionEvent.ACTION_MASK)==MotionEvent.ACTION_UP)
        {
            this.m_Pan.MouseUp(e);
            if(isLongPressed)
            {
                AddPointByHandOnMouseDown(e);
                isLongPressed = false;
            }
        }

        if(e.getPointerCount()>1)
        {
            m_ZoomPan.SetOnTouchEvent(e);
        }

    }

    private class MValue
    {
        public double Length = 0;
        public double Area = 0;
    }
}
