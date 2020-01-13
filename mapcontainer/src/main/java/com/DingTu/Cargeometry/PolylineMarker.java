package com.DingTu.Cargeometry;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.DingTu.Base.PubVar;
import com.DingTu.Enum.lkDrawType;
import com.DingTu.Enum.lkMarkerType;
import com.DingTu.Map.Map;
import android.graphics.Point;

import java.util.List;
import java.util.Random;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class PolylineMarker extends Marker {

    public PolylineMarker()
    {
        //默认为随机颜色
        Random rd = new Random();
        int R = rd.nextInt(255); int G = rd.nextInt(255); int B = rd.nextInt(255);

        this._Pen = new Paint();
        this._Pen.setStyle(Paint.Style.STROKE);
        this._Pen.setColor(Color.argb(255, R, G, B));
        this._Pen.setStrokeWidth(2);
        this._Pen.setSubpixelText(true);

        this.SetMarkerType(lkMarkerType.enPolylineMarker);
    }

    //线坐标串
    private Polyline _Polyline = new Polyline();

    //设置坐标串
    public void SetVertexList(List<Coordinate> _vertextList)
    {
        //this._Polyline.getVertexList().clear();
        for(Coordinate Coor : _vertextList)
        {
            //this._Polyline.getVertexList().add(ProjectSystem.WebUTMBLToXY(Coor.getX(),Coor.getY()));
        }
    }

    //设置线符号
    private Paint _Pen = null;
    public void SetPenWidth(float penWidth){this._Pen.setStrokeWidth(penWidth);}
    public void SetPenColor(int R,int G,int B){this._Pen.setARGB(255, R, G, B);}


    @Override
    public void Draw()
    {
        this.Draw(PubVar.m_Map, PubVar.m_Map.getDisplayGraphic(), lkDrawType.enNormal);
    }

    private void Draw(Map map, Canvas g, lkDrawType DrawType)
    {
        //if (pGeometry.getStatus() == lkGeometryStatus.enDelete) return;

        Point[] OPF = null;

        //判断是否需要剪裁操作
        if (map.getExtend().Contains(this._Polyline.getEnvelope()))
        {
            // OPF = map.getViewConvert().MapPointsToScreePoints(this._Polyline.getVertexList(),true, 0, 0);
        }
        else
        {
            //OPF = map.getViewConvert().ClipPolyline(this._Polyline.getVertexList(), 0, 0);
        }
        //

        if (OPF.length == 0) return;
        if (OPF.length >= 2)
        {
            Path p = new Path();
            for(int i=0;i<OPF.length;i++)
            {
                if (i==0)p.moveTo(OPF[i].x, OPF[i].y);
                else p.lineTo(OPF[i].x, OPF[i].y);
            }
            g.drawPath(p, this._Pen);
        }
//		switch (DrawType)
//		{
//		   case enSelected_Editing:   //正在编辑
//		       //画选中线的内节点
//		       int H = map.SetDPI(10), W = map.SetDPI(10);
//		       int PFCount = OPF.length;
//		       Paint pBrush = new Paint();
//		       pBrush.setColor(Color.BLACK);
//		       //using (Brush pBrush = new SolidBrush(Color.Black))
//		       {
//		           for (int i = 1; i < PFCount - 1; i++)
//		           {
//		               //g.FillRectangle(pBrush, );
//		           	g.drawRect(OPF[i].x - W / 2, OPF[i].y - H / 2, OPF[i].x+W/2, OPF[i].y+H/2, pBrush);
//		           }
//		       }
//		       break;
//		}
//
//		if ((DrawType == lkDrawType.enSelected_Editing) || (DrawType == lkDrawType.enSelected_NoEditing))
//		{
//		   int HH = map.SetDPI(14), WW = map.SetDPI(14);
//		   //画起点
//		   Paint pBrush = new Paint();
//		   pBrush.setColor(Color.GREEN);
//		   //using (Brush pBrush = new SolidBrush(Color.Green))
//		   {
//		       g.drawRect(OPF[0].x - WW / 2, OPF[0].y - HH / 2, OPF[0].x + WW / 2, OPF[0].y + HH / 2,pBrush);
//		   }
//
//		   //画止点
//		   //using (Brush pBrush = new SolidBrush(Color.Red))
//		   {
//		   	float x1 = OPF[OPF.length - 1].x;
//		   	float y1 = OPF[OPF.length - 1].y;
//		   	pBrush.setColor(Color.RED);
//		       g.drawRect(x1 - WW / 2, y1 - HH / 2, x1 + WW / 2, y1 + HH / 2,pBrush);
//		   }
//
//		}
    }
}
