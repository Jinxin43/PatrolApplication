package com.DingTu.mapcontainer;

import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Envelope;
import com.DingTu.Enum.lkGeoLayersType;
import com.DingTu.Layer.GeoLayer;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class Select implements IOnTouchCommand{

    private ICommand _Command = null;
    private TrackRectangle _TrackRect = null;
    private MapControl _MapControl = null;

    /**
     * 选择后的回调
     * @param callback
     */
    private ICallback _Callback = null;
    public void SetCallback(ICallback callback)
    {
        this._Callback = callback;
    }

    //是否允许多选
    private boolean _MultiSelect = false;

    public Select(MapControl mapControl, boolean MultiSelect)
    {
        _MultiSelect = MultiSelect;
        _MapControl = mapControl;
        //_MapControl.DoubleClick += new EventHandler(_MapControl_DoubleClick);

        if (_MultiSelect)  //允许多选也就意味着可以框选
        {
            _TrackRect = new TrackRectangle(mapControl);
            _Command = _TrackRect;
        }
        this.m_GestureDetector = new GestureDetector(mapControl.getContext(),this.m_MyOnGestureListener);
    }

    /**
     * 设置触发事件
     * @param event
     */
    public void SetOnTouchEvent(MotionEvent event)
    {
        this.m_GestureDetector.onTouchEvent(event);
        switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
//			case MotionEvent.ACTION_DOWN:
//				this.MapControl_MouseDown(event);
//				break;
            case MotionEvent.ACTION_UP:
                if (this._Moveing)MouseUp(event);
                return;
//			case MotionEvent.ACTION_MOVE:
//				this.MapControl_MouseMove(event);
//				break;
        }

    }

    private boolean m_DoubleClick = false;
    public GestureDetector m_GestureDetector = null;
    private GestureDetector.SimpleOnGestureListener m_MyOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            ClearAllSelection();
            m_DoubleClick = true;
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            m_DoubleClick=false;
            MouseDown(e);
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY)
        {
            // TODO Auto-generated method stub
            //Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY" + velocityY);

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            // TODO Auto-generated method stub
            //Log.i("TEST", "onLongPress");
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY)
        {
            MouseMove(e2);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            MouseUp(e);
            return super.onSingleTapUp(e);
        }
        @Override
        public void onShowPress(MotionEvent e)
        {

        }
    };


    private void MouseDown(MotionEvent e)
    {
        if (_MultiSelect) { _TrackRect.setTrackEnvelope(null); _Command.MouseDown(e); }
    }

    private boolean _Moveing = false;

    private void MouseMove(MotionEvent e)
    {
        this._Moveing = true;
        if (_MultiSelect) _Command.MouseMove(e);
    }


    private void MouseUp(MotionEvent e)
    {
        this._Moveing = false;
        if (this.m_DoubleClick){this.m_DoubleClick=false;return;}
        if (_MultiSelect) _Command.MouseUp(e);
        this.StartSelect(new android.graphics.PointF(e.getX(),e.getY()));
        if (this._Callback!=null)this._Callback.OnClick("OK","");
//        Tools.UpdateShowSelectCount();
    }

    /**
     * 在指定的点处选择实体
     * @param MousePoint
     */
    public void StartSelect(android.graphics.PointF MousePoint)
    {
        //清空全部选择集合
        //this.ClearAllSelection();

        //选择点转换为Map坐标
        Coordinate SelPoint = _MapControl.getMap().getViewConvert().ScreenToMap(MousePoint);
        Envelope SelRect = _TrackRect.getTrackEnvelope();

        //选择容忍距离
        double Tolerance = _MapControl.getMap().ToMapDistance(Tools.DPToPix(20));


        //1-在采集数据层内选择
        int LayerCount = _MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorEditingData).size();
        for (int i = LayerCount - 1; i >= 0; i--)
        {
            GeoLayer pGeoLayer = _MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorEditingData).GetLayerByIndex(i);
            if (this.SelectObjectByGeoLayer(pGeoLayer, SelPoint, SelRect, Tolerance))
            {
                _MapControl.getMap().FastRefresh(); return;
            }
        }

        //1-在背景层内选择
        LayerCount = _MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorBackground).size();
        for (int i = LayerCount - 1; i >= 0; i--)
        {
            GeoLayer pGeoLayer = _MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerByIndex(i);
            if(pGeoLayer.getSelectable() && pGeoLayer.getVisible())
            {
                if (this.SelectObjectByGeoLayer(pGeoLayer, SelPoint, SelRect, Tolerance))
                {
                    _MapControl.getMap().FastRefresh(); return;
                }
            }

        }

        //恢复原始屏幕显示
        _MapControl.getMap().FastRefresh();
    }

    //在指定图层内选择实体
    private boolean SelectObjectByGeoLayer(GeoLayer pGeoLayer,Coordinate SelPoint, Envelope SelRect,double Tolerance)
    {
        DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
        double D = dm.densityDpi * pGeoLayer.getMap().getViewConvert().getZoomScale();

        if (!(pGeoLayer.getVisibleScaleMax() >= D/0.0254 &&
                pGeoLayer.getVisibleScaleMin() <= D/0.0254))
        {
            return false;
        }

        if (!pGeoLayer.getSelectable() || !pGeoLayer.getVisible()) {return false;}

        //开始选择
        if (!_MultiSelect)    //点选
        {
            if (pGeoLayer.getDataset().HitTest(SelPoint,Tolerance , pGeoLayer.getSelSelection()))  //单选
            {
                return true;
            }
        }

        if (_MultiSelect)   //框选
        {
            if (_TrackRect.getTrackEnvelope() != null)
            {
                //判断是单选还是框选，判断方法：看矩形是否大于Tolerance
                if (_TrackRect.getTrackEnvelope().getWidth()<Tolerance && _TrackRect.getTrackEnvelope().getHeight()<Tolerance)
                {
                    if (pGeoLayer.getDataset().HitTest(SelPoint, Tolerance, pGeoLayer.getSelSelection()))  //单选
                    {
                        return true;
                    }
                } else
                {
                    pGeoLayer.getDataset().QueryWithSelEnvelope(SelRect, pGeoLayer.getSelSelection());  //框选
                }
            }
            else
            {
                if (pGeoLayer.getDataset().HitTest(SelPoint, Tolerance, pGeoLayer.getSelSelection()))  //单选
                {
                    return true;
                }
            }
        }
        return false;
    }

    //清除选择集合
    public void ClearAllSelection()
    {
        for (GeoLayer pGeoLayer : _MapControl.getMap().getGeoLayers(lkGeoLayersType.enAll).getList())
        {
            pGeoLayer.getSelSelection().RemoveAll();
        }
        //清除原有样式
        String PointMarkerID = "SelectPointMarkerID";
        PubVar.m_MapControl.getMap().getOverLayer().RemoveMarkerById(PointMarkerID);
        _MapControl.getMap().FastRefresh();

//        Tools.UpdateShowSelectCount();
    }

}
