package com.DingTu.mapcontainer;

import android.view.MotionEvent;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class ZoomOut implements ICommand{

    private ICommand _Command;
    private TrackRectangle TR_X;
    private MapControl _MapControl = null;

    public ZoomOut(MapControl MC)
    {
        TR_X = new TrackRectangle(MC);
        _Command = TR_X;
        _MapControl = MC;
    }

    @Override
    public void MouseDown(MotionEvent e)
    {
        _Command.MouseDown(e);
    }

    @Override
    public void MouseMove(MotionEvent e)
    {
        _Command.MouseMove(e);
    }

    @Override
    public void MouseUp(MotionEvent e)
    {
        _Command.MouseUp(e);
        if (TR_X.getTrackEnvelope() == null)
        {
            this._MapControl.getMap().setExtend(this._MapControl.getMap().getExtend().Scale(2));
        }
        else
        {
            //确定缩小的倍数
            this._MapControl.getMap().setExtend(this._MapControl.getMap().getExtend().Scale(this._MapControl.getMap().getExtend().Factor(TR_X.getTrackEnvelope())));
        }
        //如果需要显示GoogleMap地图，则进行显示范围调整
//        if (this._MapControl.getMap().getGMap().getIfLoadGoogleMap() &&
//            this._MapControl.getMap().getGMap().getZoomOnScale())
//        	this._MapControl.getMap().getGMap().SetCurrentLevel();
//        else
        this._MapControl.getMap().Refresh();
    }
}
