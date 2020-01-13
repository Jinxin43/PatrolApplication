package com.DingTu.Symbol;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkDrawType;
import com.DingTu.Enum.lkGeometryStatus;
import com.DingTu.Enum.lkSelectionType;
import com.DingTu.Enum.lkTextPosition;
import com.DingTu.Map.Map;

import java.util.ArrayList;
import java.util.List;

import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Cargeometry.Part;
import com.DingTu.Cargeometry.Polygon;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class PolySymbol extends ISymbol {

    public PolySymbol()
    {
        this.setName("默认");
        this.setPStyle(new Paint(Color.GREEN));
        this._PBrush.setColor(Color.GREEN);
        this.setLStyle(new Paint(Color.RED));
        this._LBrush.setColor(Color.RED);
        this._LBrush.setStrokeWidth(1);
        this.SetTransparent(125);
    }

    /**
     * Base64字符串转换面符号
     */
    public void CreateByBase64(String value)
    {
        if (value.equals("")) return;
        //面色,边线色,边线宽
        String[] PSInfo = value.split(",");

        //面色
        this._PBrush.setColor(Color.parseColor(PSInfo[0]));

        //边线色
        this._LBrush.setColor(Color.parseColor(PSInfo[1]));

        //边线宽
        this._LBrush.setStrokeWidth(Float.parseFloat(PSInfo[2]));
    }

    /**
     * 将面符号转换成Base64字符串，//面色,边线色,边线宽
     * @return
     */
    public String ToBase64()
    {
//        String PColor = Tools.ColorToHexStr(this._PBrush.getColor());
//        String LColor = Tools.ColorToHexStr(this._LBrush.getColor());
        String PColor = Tools.ColorToHexStr2(this._PBrush.getColor());
        String LColor = Tools.ColorToHexStr2(this._LBrush.getColor());
        String LW = this._LBrush.getStrokeWidth()+"";
        return PColor + "," + LColor + "," + LW;
    }

    /**
     * 生成指定大小的符号指示图
     * @param Width
     * @param Height
     * @return
     */
    public Bitmap ToFigureBitmap(int Width, int Height)
    {
        Bitmap bp = Bitmap.createBitmap(Width,Height, Bitmap.Config.ARGB_8888);
        Canvas g = new Canvas(bp);
        g.drawRect(0, 4, Width, Height-4, this.getPStyle());
        if (this.getLStyle().getStrokeWidth()>0)g.drawRect(0, 4, Width, Height-4, this.getLStyle());

        return bp;
    }

    //符号刷-内面
    private Paint _PBrush = null;
    public Paint getPStyle()
    {
        return this._PBrush;
    }
    public void setPStyle(Paint value)
    {
        this._PBrush = value;
        this._PBrush.setStyle(Paint.Style.FILL);
        this._PBrush.setAntiAlias(true);
        this.UpdateTransparent();
    }

    //符号透明度
    private int _Transparent = 0;
    public void SetTransparent(int transparent)
    {
        this._Transparent = 255-transparent;
        this.UpdateTransparent();
    }
    private void UpdateTransparent()
    {
        this._PBrush.setAlpha(this._Transparent);
    }

    //符号刷-边线
    private Paint _LBrush = null;
    public Paint getLStyle()
    {
        return this._LBrush;
    }
    public void setLStyle(Paint value)
    {
        this._LBrush = value;
        this._LBrush.setStyle(Paint.Style.STROKE);
        this._LBrush.setAntiAlias(true);
        this._LBrush.setStrokeJoin(Paint.Join.BEVEL);
    }

    //绘制符号
    @Override
    public void Draw(Map map, Geometry pGeometry)
    {
        this.Draw(map, map.getDisplayGraphic(), pGeometry, 0, 0, lkDrawType.enNormal);
    }

    @Override
    public void Draw(Map map, Canvas g, Geometry pGeometry, int OffsetX, int OffsetY, lkDrawType DrawType)
    {
//		List<String> strList = new ArrayList<String>();
//		for(Coordinate Coor:pGeometry.GetPartAt(0).getVertexList())
//		{
//			strList.add(Coor.ToString());
//		}
//		String CoorStr = Tools.JoinT("\r\n", strList);
//
        if (pGeometry.getStatus() == lkGeometryStatus.enDelete) return;
        this._VertexList.clear();
        Polygon PLY = (Polygon)pGeometry;
        Path pathAll = new Path();
        for(int i=0;i<PLY.getPartCount();i++)
        {
            Point[] OPF = null;
            Part part = PLY.GetPartAt(i);
            if (map.getExtend().Contains(part.getEnvelope())) OPF = map.getViewConvert().MapPointsToScreePoints(part.getVertexList());
            else OPF = map.getViewConvert().ClipPolygon(part.getVertexList());
            pathAll.addPath(this.CreatePath(OPF,OffsetX,OffsetY));
        }

        if (DrawType==lkDrawType.enNormal)
        {
            g.drawPath(pathAll, this.getPStyle());
            if (this.getLStyle().getStrokeWidth()>0)g.drawPath(pathAll, this.getLStyle());
        }

        if (DrawType == lkDrawType.enSelected_NoEditing)
        {
            g.drawPath(pathAll, this.getPStyle());
            Paint pBrush = new Paint();
            pBrush.setStyle(Paint.Style.STROKE);
            pBrush.setColor(Color.rgb(0, 255, 255));
            pBrush.setStrokeWidth(Tools.DPToPix(3));
            pBrush.setAntiAlias(true);
            g.drawPath(pathAll, pBrush);
        }

        if ((DrawType == lkDrawType.enSelected_Editing))
        {
            g.drawPath(pathAll, this.getPStyle());
            g.drawPath(pathAll, this.getLStyle());


            //画选中线的内节点
            int H = Tools.DPToPix(8), W = H;
            Paint pBrush = new Paint();
            pBrush.setColor(Color.BLACK);
            for (Point pt:this._VertexList)
            {
                float ox = pt.x + OffsetX;
                float oy = pt.y + OffsetY;
                g.drawRect(ox - W / 2, oy - H / 2, ox+W/2, oy+H/2, pBrush);
            }
            this._VertexList.clear();
            pBrush.setColor(Color.BLUE);
        }


    }

    private List<Point> _VertexList = new ArrayList<Point>();
    private Path CreatePath(Point[] OPF,int OffsetX,int OffsetY)
    {
        Path p = new Path();
        for(int i=0;i<OPF.length;i++)
        {
            if (i==0)p.moveTo(OPF[i].x+OffsetX, OPF[i].y+OffsetY);
            else p.lineTo(OPF[i].x+OffsetX, OPF[i].y+OffsetY);
            this._VertexList.add(OPF[i]);
        }
        if (OPF.length>0)p.lineTo(OPF[0].x+OffsetX, OPF[0].y+OffsetY);  //面的闭合处理
        return p;
    }

    @Override
    public void DrawLabel(Map map, Canvas g, Geometry pGeometry, int OffsetX,
                          int OffsetY, lkSelectionType pSelectionType)
    {
        String LabelText = pGeometry.getTag();
        Point _CenterCoorPix = map.getViewConvert().MapToScreen(((Polygon)pGeometry).getCenterPoint());
        this.getTextSymbol().Draw(g, _CenterCoorPix.x, _CenterCoorPix.y, LabelText, lkTextPosition.enCenter, pSelectionType);

    }
}
