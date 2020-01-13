package com.DingTu.Data;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Dataset.Dataset;
import com.DingTu.mapcontainer.IOnPaint;
import com.DingTu.mapcontainer.IOnTouchCommand;
import com.DingTu.mapcontainer.Pan;

import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Point;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class GpsPoint implements IOnTouchCommand,IOnPaint {

    public GpsPoint()
    {
        this.m_GestureDetector = new GestureDetector(PubVar.m_MapControl.getContext(),this.m_MyOnGestureListener);
        _Pan = new Pan(PubVar.m_MapControl);
    }

    private Pan _Pan = null;

    //相关数据集
    private Dataset m_Dataset = null;

    /**
     * 设置相关数据集Dataset
     * @param pDataset
     */
    public void SetDataset(Dataset pDataset){this.m_Dataset = pDataset;}
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
//        return PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID);
        return true;
    }

    /**
     * 编辑属性
     * @param LayerID 图层ID
     * @param SYS_ID 实体ID
     */
    public void Edit(String LayerID,int SYS_ID)
    {
        this.Edit(LayerID, SYS_ID,null);
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

    /**
     * 增加GPS点位
     */
    public void AddGPSPoint()
    {
        if (!Tools.ReadyGPS(true)) return;

        //是否启用平均值采点
        boolean averageEnable = Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_AveragePointEnable").Value);
        if (!averageEnable)this.AddPoint(PubVar.m_GPSLocate.getGPSCoordinate(),"GPS点位");
        else
        {
//        	TanhuiDataTemplate _DT = new TanhuiDataTemplate();
//        	_DT.SetEditInfo(this.GetDataset().getId(),-1);	//新增
//        	_DT.SetCalAveragePoint(true);
//        	_DT.ShowDialog();

//            GeneralDateEditor dataEdit = new GeneralDateEditor(this.GetDataset().getId(), -1);
//            dataEdit.SetCalAveragePoint(true);

//    		v1_Data_Gps_AveragePoint dsap = new v1_Data_Gps_AveragePoint();
//    		dsap.SetDataType(lkGeoLayerType.enPoint);
//    		dsap.SetCallback(new ICallback(){
//				@Override
//				public void OnClick(String Str, Object ExtraStr) {
//					AddPoint((Coordinate)ExtraStr,"GPS点位");
//				}});
//    		dsap.ShowDialog();
        }
    }

    /**
     * 通过手动输入坐标加点
     */
    public void AddPointByInputCoor()
    {
//        v1_Data_Point_InputCoor dpi = new v1_Data_Point_InputCoor();
//        dpi.SetCallback(new ICallback(){
//            @Override
//            public void OnClick(String Str, Object ExtraStr) {
//                AddPoint((Coordinate)ExtraStr,"坐标绘点");
//            }});
//        dpi.ShowDialog();
    }

    /**
     * 增加点位
     * @param ptCoor
     */
    private void AddPoint(Coordinate ptCoor,String SYSTYPE)
    {
//		Point ptGeo = new Point(ptCoor);
//		v1_BaseDataObject _GpsBaseObj = new v1_BaseDataObject();
//		_GpsBaseObj.SetBaseObjectRelateTable(this.m_TableName);
//		_GpsBaseObj.SetSYS_TYPE(SYSTYPE);
//
//		int SYS_ID = _GpsBaseObj.SaveGeoToDb(ptGeo);

        int SYS_ID  = this.SaveGeoToDb(ptCoor, SYSTYPE);
        if (SYS_ID!=-1)
        {
//            this.Edit(this.GetDataset().getId(),SYS_ID);
//            //加入回退栈内
//            UnRedoParaStru UnRedoPara = new UnRedoParaStru();
//            UnRedoPara.Command = lkmap.Enum.lkReUndoCommand.enAddDeleteObject;
//            UnRedoDataItem urDataItem = new UnRedoDataItem();
//            urDataItem.Type = lkReUndoFlag.enRedo;
//            IURDataItem_DeleteAdd uiAdd = new IURDataItem_DeleteAdd();
//            uiAdd.LayerId = this.m_Dataset.getId();
//            uiAdd.ObjectIdList.add(SYS_ID);
//            urDataItem.DataList.add(uiAdd);
//            UnRedoPara.DataItemList.add(urDataItem);
//            IUnRedo.AddHistory(UnRedoPara);
        }
        PubVar.m_Map.Refresh();
    }

    /**
     * 保存点实体
     * @param ptCoor
     * @param SYSTYPE
     * @return
     */
    public int SaveGeoToDb(Coordinate ptCoor,String SYSTYPE)
    {
        Point ptGeo = new Point(ptCoor);
        GpsDataObject _GpsBaseObj = new GpsDataObject();
        _GpsBaseObj.SetDataset(this.GetDataset());
        _GpsBaseObj.SetSYS_TYPE(SYSTYPE);
        int SYS_ID = _GpsBaseObj.SaveGeoToDb(ptGeo,0,0);


//        List<String> newObjIdx = new ArrayList<String>();newObjIdx.add(SYS_ID+"");
//
//        if (this.GetDataset().QueryGeometryFromDB(newObjIdx))
//        {
//        	Geometry pGeometryNew = this.GetDataset().GetGeometry(SYS_ID);
//        	//pDataset.UpdateLayerIndex(pGeometryNew);
//        	//pDataset.CalEnvelope();    //更新Dataset的Envelope使之包含新增实体
//        	PubVar.m_Map.ClearSelection();
//        	PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll).GetLayerById(this.GetDataset().getId()).getSelSelection().Add(pGeometryNew);
//        	PubVar.m_Map.Refresh();
//        }
        return SYS_ID;
    }

    /**
     * 下面为交互接口，为手绘线做准备
     * @param canvas
     */
    @Override
    public void OnPaint(Canvas canvas) {
        // TODO Auto-generated method stub

    }

    public void MouseDown(MotionEvent e)
    {

//		if (Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value+""))
//		{
//			PubVar.m_DoEvent.m_GlassView.SetVisible(true);
//			PubVar.m_DoEvent.m_GlassView.SetGlassPoint(e.getX(), e.getY());
//		}
//		else
//		{
        this.AddPointByHandOnMouseDown(e);
//		}
    }

    public void MouseMove(MotionEvent e) {
//		if (Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value+""))
//		{
//			PubVar.m_DoEvent.m_GlassView.SetVisible(true);
//			PubVar.m_DoEvent.m_GlassView.SetGlassPoint(e.getX(), e.getY());
//		}
    }

    public void MouseUp(MotionEvent e)
    {
//		if (Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value+""))
//		{
//			this.AddPointByHandOnMouseDown(e);
//		}
    }

    private Coordinate m_HandPoint = null;
    private void AddPointByHandOnMouseDown(final MotionEvent e)
    {
//		double JD = 102.384812;
//		double WD = 25.292934;
//
//		//输入经纬度转平面坐标
//		Coordinate XYCoor = StaticObject.soProjectSystem.WGS84ToXY(JD, WD, 0);
//
//
//		//平面坐标反解经纬度
//		Coordinate JWCoor = StaticObject.soProjectSystem.XYToWGS84(XYCoor);
//
//		Log.d("坐标反解", "原坐标："+JD+","+WD+" -> "+JWCoor.ToString());

        PointF pt = new PointF(e.getX(),e.getY());
        this.m_HandPoint = PubVar.m_Map.getViewConvert().ScreenToMap(pt);
        //PubVar.m_DoEvent.m_GlassView.SetVisible(false);
        Tools.OpenDialog("正在处理新加点位...", new ICallback(){
            @Override
            public void OnClick(String Str, Object ExtraStr) {

                //将Map坐标转换为地理坐标，只适用于手动加点的情况
                //Coordinate lb84 = StaticObject.soProjectSystem.XYToWGS84(m_HandPoint);
                //m_HandPoint.setGeoX(lb84.getX());m_HandPoint.setGeoY(lb84.getY());
                AddPoint(m_HandPoint,"手绘点位");
            }});
    }

    public GestureDetector m_GestureDetector = null;
    private GestureDetector.SimpleOnGestureListener m_MyOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

        @Override
        public boolean onDown(MotionEvent e)
        {
            //MouseDown(e);
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY)
        {
            //MouseMove(e2);
//        	_Pan.MouseDown(e1);
//        	_Pan.MouseMove(e2);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            return super.onSingleTapUp(e);
        }
    };

    @Override
    public void SetOnTouchEvent(MotionEvent event) {

        switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                MouseDown(event);
                break;
            case MotionEvent.ACTION_UP:
                MouseUp(event);
                break;
            case MotionEvent.ACTION_MOVE:
                MouseMove(event);
                break;
        }
        //this.m_GestureDetector.onTouchEvent(e);
    }
}
