package com.DingTu.mapcontainer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.DingTu.Cargeometry.Envelope;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class TrackRectangle implements ICommand,IOnPaint{

    private MapControl _MapControl;

    //动态刷新样式
    private Paint m_TrackPen = null;


    //成员变量
    private android.graphics.PointF m_StartPoint, m_MovePoint;

    //返回动态矩形
    private Envelope _TrackEnvelope = null;
    private RectF _TrackRectF = null;
    public Envelope getTrackEnvelope()
    {
        return _TrackEnvelope;
    }
    public void setTrackEnvelope(Envelope value)
    {
        _TrackEnvelope = value;
    }

    public TrackRectangle(MapControl MC)
    {
        _MapControl = MC;

        // 让颜色50%透明
        m_TrackPen = new Paint();
        m_TrackPen.setStyle(Paint.Style.STROKE);
        m_TrackPen.setColor(Color.RED);
        m_TrackPen.setStrokeWidth(2);
        int opacity = 127;
        m_TrackPen.setAlpha(opacity);
    }

    //鼠标按下事件
    private boolean LeftDown = false;
    @Override
    public void MouseDown(MotionEvent e)
    {
        _TrackEnvelope = null;
        _MapControl.SetOnPaint(this);
        m_StartPoint = new android.graphics.PointF(e.getX(),e.getY());
        LeftDown = true;
    }

    //鼠标移动事件
    @Override
    public void MouseMove(MotionEvent e)
    {
        if (LeftDown)  //左键按下
        {
            m_MovePoint = new android.graphics.PointF(e.getX(), e.getY());
            _MapControl.invalidate(this.GetRefreshRect(m_StartPoint, m_MovePoint));
        }
    }

    //得到两点构成的矩形
    private Rect GetRefreshRect(android.graphics.PointF Pt1, android.graphics.PointF Pt2)
    {
        float MinX = Math.min(Pt1.x, Pt2.x);
        float MinY = Math.min(Pt1.y, Pt2.y);
        float MaxX = Math.max(Pt1.x, Pt2.x);
        float MaxY = Math.max(Pt1.y, Pt2.y);

        float Offset = 10;
        if (_TrackRectF==null)
        {
            _TrackRectF=new RectF(MinX,MinY,MaxX,MaxY);
        }
        else
        {
            _TrackRectF.left = MinX;_TrackRectF.top = MinY;
            _TrackRectF.right = MaxX;_TrackRectF.bottom=MaxY;
        }
        return new Rect((int)(_TrackRectF.left-Offset),(int)(_TrackRectF.top-Offset),
                (int)(_TrackRectF.right+Offset*2),(int)(_TrackRectF.bottom+Offset*2));
    }

    //鼠标松开事件
    @Override
    public void MouseUp(MotionEvent e)
    {
        LeftDown=false;
        //判断最小、最大值，构造返回的矩形框，实际单位（米）
        if (_TrackRectF!=null)
        {
            this._TrackEnvelope = new Envelope(_MapControl.getMap().getViewConvert().ScreenToMap(_TrackRectF.left, _TrackRectF.top),
                    _MapControl.getMap().getViewConvert().ScreenToMap(_TrackRectF.right, _TrackRectF.bottom));

            //确定矩形的类型，是正向矩形，还是反向矩形
            if (e.getX() > m_StartPoint.x) this._TrackEnvelope.setType(true);
            else this._TrackEnvelope.setType(false);
        }
        _TrackRectF=null;
        _MapControl.invalidate();

        //强制回收垃圾
        //GC.Collect();
    }

    //动态刷新矩形框
    @Override
    public void OnPaint(Canvas g)
    {
        if (_TrackRectF==null) return;
        g.drawRect(_TrackRectF, m_TrackPen);
        //m_TrackPen.Color = Color.Red; m_TrackPen.DashStyle = System.Drawing.Drawing2D.DashStyle.Dot;
//        pe.Graphics.Clip = new Region(pe.ClipRectangle);
//        System.Drawing.Point[] DrawLine = m_TrackLine.ToArray();
//        pe.Graphics.DrawLines(m_TrackPen, DrawLine);

    }

}
