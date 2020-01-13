package com.DingTu.Symbol;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.util.Log;

import com.DingTu.Enum.lkDrawType;
import com.DingTu.Enum.lkGeometryStatus;
import com.DingTu.Enum.lkSelectionType;
import com.DingTu.Enum.lkTextPosition;
import com.DingTu.Map.Map;
import com.DingTu.Base.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Cargeometry.Polyline;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class LineSymbol extends ISymbol {
    public LineSymbol()
    {
        //默认符号
        this.setName("NULL");

        //默认为随机颜色
        Random rd = new Random();
        int R = rd.nextInt(255); int G = rd.nextInt(255); int B = rd.nextInt(255);

        Paint pPen = new Paint();
        pPen.setStyle(Paint.Style.STROKE);
        pPen.setARGB(255, R, G, B);
        pPen.setStrokeWidth(3);
        // pPen.LineJoin = System.Drawing.Drawing2D.LineJoin.Round;   //消除尖角
        //float[] f = { float.MaxValue };
        // pPen.DashPattern = f;
        _PenList.add(pPen);

        this._SymbolBase64Str = Tools.ColorToHexStr(pPen.getColor())+","+pPen.getStrokeWidth();
    }

    private String _SymbolBase64Str = "";
    /**
     * Base64字符串转换线符号
     * @param base64
     */
    public void CreateByBase64(String value)
    {
        this._SymbolBase64Str = value;
        String[] symList = value.split("@");
        List<Paint> pPenList = new ArrayList<Paint>();

        for(String sym:symList)  //格式：颜色1,宽度1,线样式1@.....，线样式1=float[偶数个]
        {
            Paint pPen = new Paint();
            pPen.setAntiAlias(true);
            pPen.setStyle(android.graphics.Paint.Style.STROKE);
            if (!sym.equals(""))
            {
                String[] symStyle = sym.split(",");
                pPen.setColor(Color.parseColor(symStyle[0]));
                pPen.setStrokeWidth(Float.valueOf(symStyle[1]));
                if (symStyle.length==3)
                {
                    String[] syList = symStyle[2].split("\\*");
                    float[] syFList = new float[syList.length];
                    Log.d("符号解析", symStyle[2]);
                    for(int i=0;i<syList.length;i++)syFList[i]=Float.parseFloat(syList[i]);
                    PathEffect effects = new DashPathEffect(syFList,0);
                    pPen.setPathEffect(effects);
                }
            }
            pPenList.add(pPen);
        }
        this.setStyle(pPenList);
    }

    /**
     * 将点符号转换成Base64字符串
     * @return
     */
    public String ToBase64()
    {
        return this._SymbolBase64Str;
    }

    /**
     * 生成指定大小的符号指示图
     * @param Width
     * @param Height
     * @return
     */
    public Bitmap ToFigureBitmap(int Width,int Height)
    {
        Bitmap bp = Bitmap.createBitmap(Width,Height, Bitmap.Config.ARGB_8888);
        Canvas g = new Canvas(bp);
        for(Paint pPen:this.getStyle())  //格式：颜色1,宽度1,线样式1
        {
            g.drawLine(0, Height/2, Width, Height/2, pPen);
        }
        return bp;
    }

    //画笔集
    private List<Paint> _PenList = new ArrayList<Paint>();           //画笔集合
    public List<Paint> getStyle()
    {
        return _PenList;
    }
    public void setStyle(List<Paint> value)
    {
        _PenList = value;
    }


    //绘制符号
    @Override
    public void Draw(Map map, Geometry pGeometry)
    {
        this.Draw(map, map.getDisplayGraphic(), pGeometry, 0, 0, lkDrawType.enNormal);
    }

    @Override
    public void Draw(Map map, Canvas g, Geometry pGeometry, int OffsetX,
                     int OffsetY, lkDrawType DrawType)
    {
        if (pGeometry.getStatus() == lkGeometryStatus.enDelete) return;

        Polyline PL = (Polyline)pGeometry;
        Point[] OPF = null;

        //判断是否需要剪裁操作
        if (map.getExtend().Contains(pGeometry.getEnvelope()))
        {
            OPF = map.getViewConvert().MapPointsToScreePoints(PL.GetPartAt(0).getVertexList(),true, OffsetX, OffsetY);
        }
        else
        {
            OPF = map.getViewConvert().ClipPolyline(PL.GetPartAt(0).getVertexList(), OffsetX, OffsetY);
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
            //p.setFillType(FillType.EVEN_ODD);
            //g.drawLines(Tools.PointListToFloatList(OPF), this.getStyle());

//        	//叠加Google地图后线型加边框
//        	if (PubVar.m_Map.getGMap().getIfLoadGoogleMap())
//        	{
//	            for (Paint pPen : this.getStyle())
//	            {
//	                float PW = pPen.getStrokeWidth()+2;
//	                this.getBKLinePaint().setStrokeWidth(map.SetDPI(PW));
//	                this.getBKLinePaint().setSubpixelText(true);
//	                this.getBKLinePaint().setAntiAlias(true);
//	                g.drawPath(p, this.getBKLinePaint());
//	            }
//        	}


            if (DrawType== lkDrawType.enNormal)
            {
                for (Paint pPen : this.getStyle())
                {
                    float PW = pPen.getStrokeWidth();
                    pPen.setStrokeWidth(PW);
                    pPen.setSubpixelText(true);
                    pPen.setAntiAlias(true);
                    pPen.setStrokeJoin(Paint.Join.BEVEL);
                    g.drawPath(p, pPen);
                    pPen.setStrokeWidth(PW);
                }
            }
            else
            {

                Paint pBrush = new Paint();
                pBrush.setStyle(Paint.Style.STROKE);
                if (DrawType==lkDrawType.enSelected_NoEditing)
                {
                    pBrush.setColor(Color.rgb(0, 255, 255));
                    pBrush.setStrokeWidth(Tools.DPToPix(8));
                } else
                {
                    pBrush.setColor(Color.BLUE);
                    pBrush.setStrokeWidth(Tools.DPToPix(5));
                }
                pBrush.setAntiAlias(true);
                pBrush.setStrokeJoin(Paint.Join.BEVEL);
                g.drawPath(p, pBrush);
            }


        }
        switch (DrawType)
        {
            case enSelected_Editing:   //正在编辑
                //画选中线的内节点
                int H = Tools.DPToPix(8), W = H;
                int PFCount = OPF.length;
                Paint pBrush = new Paint();
                pBrush.setColor(Color.BLACK);
                for (int i = 1; i < PFCount - 1; i++)
                {
                    //g.FillRectangle(pBrush, );
                    g.drawRect(OPF[i].x - W / 2, OPF[i].y - H / 2, OPF[i].x+W/2, OPF[i].y+H/2, pBrush);
                }
                pBrush.setColor(Color.BLUE);

                break;
        }

        if ((DrawType == lkDrawType.enSelected_Editing) || (DrawType == lkDrawType.enSelected_NoEditing))
        {
            int HH = Tools.DPToPix(10), WW = HH;
            //画起点
            Paint pBrush = new Paint();
            pBrush.setColor(Color.GREEN);
            //using (Brush pBrush = new SolidBrush(Color.Green))
            {
                g.drawRect(OPF[0].x - WW / 2, OPF[0].y - HH / 2, OPF[0].x + WW / 2, OPF[0].y + HH / 2,pBrush);
            }

            //画止点
            //using (Brush pBrush = new SolidBrush(Color.Red))
            {
                float x1 = OPF[OPF.length - 1].x;
                float y1 = OPF[OPF.length - 1].y;
                pBrush.setColor(Color.RED);
                g.drawRect(x1 - WW / 2, y1 - HH / 2, x1 + WW / 2, y1 + HH / 2,pBrush);
            }
        }


    }


    @Override
    public void DrawLabel(Map map, Canvas g, Geometry pGeometry, int OffsetX, int OffsetY, lkSelectionType pSelectionType)
    {
//        String LabelText = pGeometry.getTag();
//        int VertexCount = pGeometry.GetPartAt(0).getVertexList().size();
//        Coordinate midPoint = pGeometry.GetPartAt(0).getVertexList().get(VertexCount/2);
//        if (VertexCount % 2==0)
//        {
//        	Coordinate PT2 = pGeometry.GetPartAt(0).getVertexList().get(VertexCount/2-1);
//        	midPoint = new Coordinate((midPoint.getX()+PT2.getX())/2,(midPoint.getY()+PT2.getY())/2);
//        }
//
//        Point _CenterCoorPix = map.getViewConvert().MapToScreen(midPoint);
//        this.getTextSymbol().Draw(g, _CenterCoorPix.x, _CenterCoorPix.y, LabelText, lkTextPosition.enCenter, pSelectionType);


        Point[] OPF = null;

        //判断是否需要剪裁操作
        if (map.getExtend().Contains(pGeometry.getEnvelope()))
        {
            OPF = map.getViewConvert().MapPointsToScreePoints(pGeometry.GetPartAt(0).getVertexList(),true, OffsetX, OffsetY);
        }
        else
        {
            OPF = map.getViewConvert().ClipPolyline(pGeometry.GetPartAt(0).getVertexList(), OffsetX, OffsetY);
        }


        try
        {
            String Text =  pGeometry.getTag();
            if (OPF.length == 0) return;


            //整个注记文本的宽度
            float pWidth = this.getTextSymbol().getTextFont().measureText(Text);

            //单个文字宽度
            double pOneTextWidth = this.getTextSymbol().getTextFont().measureText("国");

            //文字之间的间距
            double pTextMargin = pOneTextWidth / 2;

            //从中间向下串的距离，保证文字最后居中显示
            double AllLen = this.GetLength(OPF)/2-pWidth / 2 - pTextMargin * Text.length()/2;

            //计算线段中点处的坐标
            int StartTextPointIndex = 0;
            for (int idx = 0; idx < OPF.length - 1; idx++)
            {
                Point PT1 = OPF[idx];
                Point PT2 = OPF[idx + 1];
                double DIST = this.GetTowPointDistance(PT1, PT2);

                if ((AllLen-DIST)<0)
                {
                    Point textPoint = this.GetToStartCoordinate(PT1, PT2, Math.abs(AllLen));
                    OPF[idx] = textPoint;
                    StartTextPointIndex = idx;
                    break;
                } AllLen -= DIST;

            }

            //计算每一个文字的坐标位置
            int textIndex = 0;double lsDist = 0;
            List<Point> textPointList = new ArrayList<Point>();
            for (int idx = StartTextPointIndex; idx < OPF.length - 1; idx++)
            {
                Point PT1 = OPF[idx];
                Point PT2 = OPF[idx + 1];
                double DIST = this.GetTowPointDistance(PT1, PT2);
                if ((lsDist + DIST) > (pOneTextWidth+pTextMargin))
                {
                    Point textPoint = this.GetToStartCoordinate(PT1, PT2, pOneTextWidth+pTextMargin - lsDist);
                    textPointList.add(textPoint);
                    textIndex++;
                    if (textIndex >= Text.length()) break;
                    lsDist = 0; OPF[idx] = textPoint; idx--;
                }
                else lsDist += DIST;
            }

            //调整文字的输出顺序
            boolean ChangeFlip = false;
            Point AP1 = textPointList.get(0);
            Point AP2 = textPointList.get(textPointList.size() - 1);
            if (AP1.x >= AP2.x) ChangeFlip = true;


            for(int i=0;i<textPointList.size();i++)
            {
                Point textPoint = textPointList.get(i);
                int txtIdx = i; if (ChangeFlip) txtIdx = textPointList.size() - 1 - i;
                this.getTextSymbol().Draw(g, textPoint.x, textPoint.y, Text.substring(txtIdx, txtIdx+1), lkTextPosition.enCenter, pSelectionType);
            }

        }
        catch(Exception e)
        {

        }

    }


    //定比分点
    private double GetLength(Point[] pfList)
    {
        double Len = 0;
        for (int i = 0; i < pfList.length - 1; i++)
        {
            Len += this.GetTowPointDistance(pfList[i], pfList[i + 1]);
        }
        return Len;
    }

    private Point GetToStartCoordinate(Point StartPoint, Point EndPoint, double ToStartDistance)
    {
        //定比分点公式：x=（x1+λx2）/（1+λ），y=（y1+λy2）/（1+λ）
        double X1 = StartPoint.x; double Y1 = StartPoint.y;
        double X2 = EndPoint.x; double Y2 = EndPoint.y;
        double S1 = ToStartDistance;
        double S2 = this.GetTowPointDistance(StartPoint,EndPoint) - ToStartDistance;
        if (ToStartDistance == 0) return StartPoint;
        if (S2 == 0) return EndPoint;
        double S = S1 / S2;
        double X = (X1 + S * X2) / (1 + S);
        double Y = (Y1 + S * Y2) / (1 + S);

        //验证是否正确
        Point newPointF = new Point();newPointF.set((int)X,(int)Y);

        return newPointF;
    }

    private double GetTowPointDistance(Point P1, Point P2)
    {
        return Math.sqrt((P1.x - P2.x) * (P1.x - P2.x) + (P1.y - P2.y) * (P1.y - P2.y));
    }
}
