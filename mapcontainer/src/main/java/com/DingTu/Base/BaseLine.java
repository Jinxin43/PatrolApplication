package com.DingTu.Base;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.mapcontainer.IOnPaint;
import com.DingTu.mapcontainer.Pan;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BaseLine implements IOnPaint {

    private List<Coordinate> m_GPSTrackPointList;// GPS跟踪线的轨迹点
    private String Id ;
    private Pan pan = null;


    public void UpdateData(List<Coordinate> m_trackPointList) {
        if (m_GPSTrackPointList != null && m_GPSTrackPointList.size() > 0) {
            m_GPSTrackPointList.clear();
        }
        m_GPSTrackPointList.addAll(m_trackPointList);
    }


    public void startCanvas() {
        Id=UUID.randomUUID().toString();
        PubVar.m_MapControl.AddOnPaint(Id, this);
        PubVar.m_MapControl.invalidate();
    }


       public void clearPaint()
    {
        if(m_GPSTrackPointList!=null) {
            m_GPSTrackPointList.clear();
        }
        PubVar.m_MapControl.invalidate();
        PubVar.m_MapControl.ClearOnPaint(Id);
    }

     public BaseLine() {
        pan = new Pan(PubVar.m_MapControl);
        m_GPSTrackPointList = new ArrayList<Coordinate>();
    }



    @Override
    public void OnPaint(Canvas canvas) {
        this.pan.OnPaint(canvas);
        if (PubVar.m_Map.getInvalidMap()) return;
        if (this.m_GPSTrackPointList.size() == 0) return;
        //绘制轨迹点信息，形成轨迹线
        Point[] PList = PubVar.m_MapControl.getMap().getViewConvert().MapPointsToScreePoints(this.m_GPSTrackPointList);

        Path p = new Path();

        //绘制轨迹节点
        int H = Tools.DPToPix(8);
        for (int i = 0; i < PList.length; i++) {
            //绘制单个节点
            Paint pBrush = new Paint();
            pBrush.setStrokeWidth(Tools.DPToPix(5));
            if (i == 0) pBrush.setColor(Color.GREEN);  //起
            canvas.drawCircle(PList[0].x, PList[0].y, H / 2, pBrush);
            if (i == PList.length - 1) pBrush.setColor(Color.YELLOW);  //终
            canvas.drawCircle(PList[PList.length - 1].x, PList[PList.length - 1].y, H / 2, pBrush);
            //if (i > 0 && i < PList.length - 1) pBrush.setColor(Color.RED);
//            canvas.drawCircle(PList[i].x, PList[i].y, H / 2, pBrush);
            pBrush.setStyle(Paint.Style.STROKE);
            pBrush.setColor(Color.BLUE);
            pBrush.setStrokeWidth(Tools.DPToPix(2));
            canvas.drawCircle(PList[i].x, PList[i].y, H / 2, pBrush);

            if (i == 0) p.moveTo(PList[i].x, PList[i].y);
            else p.lineTo(PList[i].x, PList[i].y);
        }
        Paint pPen = new Paint();
        pPen.setStrokeWidth(Tools.DPToPix(3));
        pPen.setColor(Color.BLUE);
        pPen.setStyle(Paint.Style.STROKE);
        canvas.drawPath(p, pPen);

    }
}
