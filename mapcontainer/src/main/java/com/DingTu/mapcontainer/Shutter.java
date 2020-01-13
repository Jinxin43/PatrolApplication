package com.DingTu.mapcontainer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.DingTu.Base.Tools;
import com.DingTu.Layer.GridLayer;
import com.DingTu.Layer.GridLayers;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class Shutter implements IOnPaint,IOnTouchCommand{

    private MapControl _MapControl;

    //最上层详图
    private Bitmap m_TopMaskImage=null;

    public Shutter(MapControl MC)
    {
        _MapControl = MC;
    }
    public void StartShutter()
    {
        _MapControl.getMap().Refresh();


        //最上层详图
        if (this.m_TopMaskImage==null)
        {
            this.m_TopMaskImage = Bitmap.createBitmap(_MapControl.getMap().getViewConvert().getSize().getWidth(),
                    _MapControl.getMap().getViewConvert().getSize().getHeight(), Bitmap.Config.ARGB_8888);
        }
        (new Canvas(this.m_TopMaskImage)).drawBitmap(_MapControl.getMap().MaskBitmap, 0, 0, null);

        //下层图，也就是隐藏最上面栅格图的图
        GridLayer TopGridLayer = null;
        GridLayers pGridLayers = _MapControl.getMap().GetGridLayers();
        for(int i=0;i<pGridLayers.GetList().size();i++)
        {
            GridLayer pGridLayer = pGridLayers.GetList().get(i);
            if (pGridLayer.GetShowGird())TopGridLayer = pGridLayer;
        }
        if (TopGridLayer!=null)
        {
            TopGridLayer.SetShowGrid(false);
            _MapControl.getMap().FastRefresh();
            TopGridLayer.SetShowGrid(true);
        }
        _MapControl.getMap().setInvalidMap(true);
        this._MapControl.invalidate();
    }

    //移动起止点
    private android.graphics.PointF m_MoveStart;
    private android.graphics.PointF m_MoveEnd;

    public boolean _MouseDown = false;
    public void MouseDown(MotionEvent e)
    {
        if (this._MouseDown) return;
        //起点
        m_MoveStart = new android.graphics.PointF(e.getX(), e.getY());
        this._MouseDown = true;
    }

    private boolean _MouseMoving = false;
    public void MouseMove(MotionEvent e)
    {
        this._MouseMoving = true;
        m_MoveEnd = new android.graphics.PointF(e.getX(), e.getY());
        this._MapControl.invalidate();
    }

    public void MouseUp(MotionEvent e)
    {
        if (this._MouseDown)
        {

        }

        this._MouseDown = false;
        this._MouseMoving = false;
        _MapControl.getMap().setInvalidMap(false);
        this._MapControl.invalidate();
        mDragFlip = 0;
        //强制回收垃圾
        System.gc();

    }

    int mDragFlip = 0;   //0是未确定，1是横向，2是纵向
    @Override
    public void OnPaint(Canvas g)
    {
        if (this.m_TopMaskImage!=null)
        {

            if (this._MouseMoving)
            {
                g.save();
                Path clipPath = new Path();
                clipPath.addRect(new RectF(0,0,g.getWidth(),g.getHeight()), Path.Direction.CCW);
                RectF drawRect = null;


                //判断是横向拉动，还是纵向拉动
                //int DragFlip = 1;   //横向
                float DelY = Math.abs(this.m_MoveEnd.y-this.m_MoveStart.y);
                float DelX = Math.abs(this.m_MoveEnd.x-this.m_MoveStart.x);
                if(mDragFlip == 0)
                {
                    if(DelY>DelX)
                    {
                        mDragFlip = 2;
                    }
                    else
                    {
                        mDragFlip = 1;
                    }
                }


                //if (DelY>DelX)DragFlip=2;   //纵向

                if (mDragFlip==1)
                {
                    if (this.m_MoveStart.x<=g.getWidth()/2)
                    {
                        drawRect = new RectF(0,0,this.m_MoveEnd.x,g.getHeight());
                    }
                    else
                    {
                        drawRect = new RectF(this.m_MoveEnd.x,0,g.getWidth(),g.getHeight());
                    }
                }
                if (mDragFlip==2)
                {
                    if (this.m_MoveStart.y<=g.getHeight()/2)
                    {
                        drawRect = new RectF(0,0,g.getWidth(),this.m_MoveEnd.y);
                    }
                    else
                    {
                        drawRect = new RectF(0,this.m_MoveEnd.y,g.getWidth(),g.getHeight());
                    }
                }


                //有效矩形范围
                clipPath.addRect(drawRect, Path.Direction.CW);
                g.clipPath(clipPath);

                Paint p = new Paint();
                p.setAlpha(255);
                g.drawBitmap(this.m_TopMaskImage,0, 0,p);

                //绘制分割线
                p.setStrokeWidth(Tools.DPToPix(4));
                p.setColor(Color.RED);
                p.setStyle(Paint.Style.STROKE);
                g.drawRect(drawRect, p);
                g.restore();
            }
            else
            {
                g.drawBitmap(this.m_TopMaskImage,0, 0,null);
            }


        }
    }

    @Override
    public void SetOnTouchEvent(MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                this.MouseDown(event);
                break;
            case MotionEvent.ACTION_UP:
                this.MouseUp(event);
                break;
            case MotionEvent.ACTION_MOVE:
                this.MouseMove(event);
                break;
        }
    }
}
