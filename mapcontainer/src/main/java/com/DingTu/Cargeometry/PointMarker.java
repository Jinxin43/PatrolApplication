package com.DingTu.Cargeometry;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.DingTu.Base.PubVar;
import com.DingTu.Enum.lkMarkerType;
import com.DingTu.Map.Map;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class PointMarker extends Marker {

    //中心点坐标
    private Coordinate _Point = null;

    public PointMarker(Coordinate _CenterPoint)
    {
        this.SetMarkerType(lkMarkerType.enPointMarker);
        this._Point = _CenterPoint;
    }

    public void SetGeoPoint(Coordinate _CenterPoint)
    {
        this._Point.setX(_CenterPoint.getX());
        this._Point.setY(_CenterPoint.getY());
    }


    @Override
    public void Draw()
    {
        this.Draw(PubVar.m_Map, PubVar.m_Map.getDisplayGraphic());
    }

    private Paint _Pen = null;
    private Paint _FillPen = null;
    private Paint _TextFont = null;

    private void Draw(Map map, Canvas g)
    {
        //初始化符号
        if (this._Pen==null)
        {
            this._Pen = new Paint();
            this._Pen.setStyle(Paint.Style.STROKE);
            this._Pen.setColor(Color.RED);
            this._Pen.setStrokeWidth(1);
            this._Pen.setAntiAlias(true);
        }

        if (this._FillPen==null)
        {
            this._FillPen = new Paint();
            this._FillPen.setStyle(Paint.Style.FILL);
            this._FillPen.setARGB(125,0,255,0);
            this._FillPen.setAntiAlias(true);
        }

        if (_TextFont==null)
        {
            _TextFont = new Paint();
            _TextFont.setAntiAlias(true);
            _TextFont.setTextSize(24);
            _TextFont.setColor(Color.BLUE);
            Typeface TF = Typeface.create("宋体", Typeface.BOLD);
            _TextFont.setTypeface(TF);
            _TextFont.setShadowLayer(5, 0, 0, Color.WHITE);
        }

        //不在屏幕范围内不显示
        if (!map.getViewConvert().InViewExtend(_Point)) return;

        //图形屏幕坐标
        android.graphics.Point pt = map.getViewConvert().MapToScreen(_Point.getX(), _Point.getY());

        g.drawCircle(pt.x, pt.y, 10, this._FillPen);
        g.drawCircle(pt.x, pt.y, 10 , this._Pen);

        //画文本注记
        float TextX = Float.MIN_VALUE;
        float TextY = pt.y+15;

        String[] Info = this.GetTag().split(",");
        for(String INF :Info)
        {
            float a = _TextFont.measureText(INF);
            if (a>=TextX)TextX = a;
        }

        g.drawRect(pt.x-TextX/2-10, TextY, pt.x+TextX/2+10, TextY+85, this._FillPen);
        g.drawRect(pt.x-TextX/2-10, TextY, pt.x+TextX/2+10, TextY+85, this._Pen);

        for(String INF :Info)
        {
            g.drawText(INF, pt.x-TextX/2,TextY+25, _TextFont);
            TextY+=25;
        }
    }
}
