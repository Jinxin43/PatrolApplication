package com.DingTu.Cargeometry;

import com.DingTu.Base.Tools;
import com.DingTu.CoordinateSystem.ProjectSystem;
import com.DingTu.CoordinateSystem.Project_GK;
import com.DingTu.CoordinateSystem.Project_Web;
import com.DingTu.Enum.lkPartType;
import com.DingTu.Map.StaticObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Part {

    public Part() { }
    public Part(List<Coordinate> vertexList)
    {
        this._VertexList = vertexList;
    }



    //坐标序列
    private List<Coordinate> _VertexList = new ArrayList<Coordinate>();
    public List<Coordinate> getVertexList(){return this._VertexList;}
    public void setVertext(List<Coordinate> vertexList){this._VertexList = vertexList;}

    /**
     * 获取Part边线
     * @return
     */
    public Polyline GetBorder()
    {
        Polyline PL = new Polyline();
        Part part = new Part();
        for(Coordinate Coor:this._VertexList)part.getVertexList().add(Coor);
        PL.AddPart(part);
        return PL;
    }

    //外接矩形
    private Envelope _Envelope = null;
    public Envelope getEnvelope()
    {
        if (this._Envelope == null)this.UpdateEnvelope(); return this._Envelope;
    }
    //public void setEnvelope(Envelope env){this._Envelope = env;}

    /**
     * 更新最大外接矩形
     */
    public void UpdateEnvelope()
    {
        double MinX = -1, MinY = -1, MaxX = -1, MaxY = -1;
        boolean First = true;
        for(Coordinate Pt : this._VertexList)
        {
            if (First) { MinX = Pt.getX(); MinY = Pt.getY(); MaxX = MinX; MaxY = MinY; First = false; }
            if (MinX > Pt.getX()) MinX = Pt.getX();
            if (MaxX < Pt.getX()) MaxX = Pt.getX();
            if (MinY > Pt.getY()) MinY = Pt.getY();
            if (MaxY < Pt.getY()) MaxY = Pt.getY();
        }
        //更新最大外接矩形
        this._Envelope = new Envelope(MinX, MaxY, MaxX, MinY);
    }

    /**
     * 计算长度
     * @return
     */
    public double CalLength()
    {
        int VertexCount = this._VertexList.size();
        double LineLength = 0;
        for (int i = 0; i < VertexCount-1; i++)
        {
            LineLength += Tools.GetTwoPointDistance(this._VertexList.get(i), this._VertexList.get(i+1));
        }
        return LineLength;
    }

    /**
     * 计算面积
     * @return
     */
    public double CalArea()
    {
        return Math.abs(this.CalAreaContainFlip());
    }

    /**
     * 闭合处理
     */
    public void Closed()
    {
        if(this.getVertexList().size()>0)
        {
            Coordinate PStart = this.getVertexList().get(0);
            Coordinate EStart = this.getVertexList().get(this.getVertexList().size()-1);
            if (!PStart.Equal(EStart))this.getVertexList().add(PStart);
        }

    }

    /**
     * 计算原始面积，也就是带方向性
     * @return
     */
    private double CalAreaContainFlip()
    {
        double PolyArea = 0;
        int NumPoint = this.getVertexList().size();

        if (NumPoint >= 3)
        {
            List<Coordinate> vertextList = null;

            //如果为经纬度坐标，则需转换为投影坐标计算面积
            if (StaticObject.soProjectSystem.GetCoorSystem().GetName().equals("WGS-84坐标"))
            {
                //中央经线
                Coordinate Coor1 = this.getVertexList().get(this.getVertexList().size()/2);
                Coordinate MidPoint = Project_Web.Web_XYToBL(Coor1.getX(),Coor1.getY());
                StaticObject.soProjectSystem.GetCoorSystem().SetCenterMeridian(ProjectSystem.AutoCalCenterJX(MidPoint.getX(),MidPoint.getY()));
                vertextList = new ArrayList<Coordinate>();

                //转换为投影坐标
                for(Coordinate jwCoor:this.getVertexList())
                {
                    Coordinate jwd = Project_Web.Web_XYToBL(jwCoor.getX(),jwCoor.getY());
                    vertextList.add(Project_GK.GK_BLToXY(jwd.getX(), jwd.getY(), StaticObject.soProjectSystem.GetCoorSystem()));
                }
            } else
            {
                vertextList = this.getVertexList();
            }

            double sum = 0; int m = 1;int StartPointIndex = 0;
            Coordinate LP0, LPm, LPm1;
            double X1, Y1, X2, Y2;
            for (; ; )
            {
                if (NumPoint < 3) break;
                LP0 = vertextList.get(StartPointIndex);
                LPm = vertextList.get(StartPointIndex + m);
                LPm1 = vertextList.get(StartPointIndex + m + 1);
                //  R1.X = (LPm.X - LP0.X);
                X1 = ((double)(LPm.getX() - LP0.getX()));

                //    R1.Y = (LPm.Y - LP0.Y);
                Y1 = ((double)(LPm.getY() - LP0.getY()));

                //    R2.X = (LPm1.X - LPm.X);
                X2 = ((double)(LPm1.getX() - LPm.getX()));

                //    R2.Y = (LPm1.Y - LPm.Y);
                Y2 = ((double)(LPm1.getY() - LPm.getY()));

                //sum += (R1.X * R2.Y - R2.X * R1.Y);
                sum += (X1 * Y2 - X2 * Y1);

                m++;
                NumPoint--;
            }
            //sum = (sum > 0) ? (sum / 2.0) : (-sum / 2.0);  //通过正负可判断多边的时序方向，也就是正还是负
            PolyArea = sum/2;
        }
        return PolyArea;
    }

    //自动设置Part的类型，主要用于读取数据
    public void AutoSetPartType()
    {
        double area = this.CalAreaContainFlip();
        if (area<0) this._PartType=lkPartType.enHole;
        else this._PartType=lkPartType.enPoly;
    }

    private lkPartType _PartType = lkPartType.enPoly;
    /**
     * 设置part类型，如果为面则自动修正面节点方向，逆时针
     */
    public void SetPartType(lkPartType partType)
    {
        double area = this.CalAreaContainFlip();
        if (partType==lkPartType.enPoly && area<0) Tools.ReverseList(this._VertexList);
        if (partType==lkPartType.enHole && area>0)Tools.ReverseList(this._VertexList);
        this._PartType = partType;
    }
    public lkPartType GetPartType(){return this._PartType;}

    /**
     * 复制Part
     * @return
     */
    public Part Clone()
    {
        List<Coordinate> newCoorList = new ArrayList<Coordinate>();
        for(Coordinate Coor:this.getVertexList())newCoorList.add(Coor.Clone());
        Part newPart = new Part();
        newPart.setVertext(newCoorList);
        //newPart.SetPartType(this.GetPartType());
        newPart._PartType = this.GetPartType();
        return newPart;
    }

    /**
     * 检测指定的HitPoint点与实体内点最近点的索引值
     * @param HitPoint 检测点
     * @param Tolerance 容忍距离
     * @return 最近点的索引值，-1表示指定距离内没有选中节点
     */
    public int HitVertexTest(Coordinate HitPoint, double Tolerance)
    {
        int VertexIndex = -1;
        double NearestDist = Double.MAX_VALUE;

        for (int i = 0; i < this.getVertexList().size(); i++)
        {
            double D = Tools.GetTwoPointDistance(HitPoint, this.getVertexList().get(i),false);
            if (D<NearestDist)
            {
                NearestDist = D; VertexIndex = i;
            }
        }
        if (NearestDist <= Tolerance) return VertexIndex; else {return -1;}
    }

    /**
     * 检测是否在指定点处选中实体，并返回Segment索引值
     * @param SelPoint 检测点
     * @param Tolerance 距离
     * @return 选中的Segment片段的索引，-1表示没有选中
     */
    public int HitSegmentTest(Coordinate SelPoint, double Tolerance)
    {
        //判断是否在最大外接矩形内部，如果在外部则退出
        if (!this.getEnvelope().ContainsPoint(SelPoint)) return -1;
        for (int i = 0; i <= this.getVertexList().size() - 2; i++)
        {
            Coordinate LinePoint1 = this.getVertexList().get(i);
            Coordinate LinePoint2 = this.getVertexList().get(i + 1);
            Line SegmentLine = new Line(LinePoint1, LinePoint2);
            if (SegmentLine.PointToLineDistance(SelPoint,Tolerance)) return i;
        }
        return -1;
    }

    /*计算点是否在面内，如果在面内则选中
     * 思路：将指定的点向右做一条射线，如果此射线与多边形有奇数个交点，则点在多边形内部，
     *       为0或偶数则在多边形外部
     */
    public boolean ContainsPoint(Coordinate HitPoint)
    {
        //1、判断点是否在多边形的外接矩形内部
        if (!this.getEnvelope().ContainsPoint(HitPoint)) return false;

        //2、构造向右射线
        Coordinate EndPoint = new Coordinate(this.getEnvelope().getMaxX() + 10, HitPoint.getY());
        //Line newLine = new Line(HitPoint, EndPoint);

        //3、循环判断多边形的边是否射线具有交点
        Coordinate LinePoint1, LinePoint2; int TotalInterPoint = 0;
        //Coordinate Inter1 = new Coordinate(); Coordinate Inter2 = new Coordinate();
        for (int i = 0; i < this.getVertexList().size(); i++)
        {
            LinePoint1 = (Coordinate)this.getVertexList().get(i);

            if (i == this.getVertexList().size() - 1)
            {
                LinePoint2 = (Coordinate)this.getVertexList().get(0);
            }
            else
            {
                LinePoint2 = (Coordinate)this.getVertexList().get(i + 1);
            }
            //Line PolyBoundLine = new Line(LinePoint1, LinePoint2);

            //计算两直线段交点
            if (this.isIntersect(HitPoint.getX(),HitPoint.getY(),EndPoint.getX(),EndPoint.getY(),
                    LinePoint1.getX(), LinePoint1.getY(), LinePoint2.getX(), LinePoint2.getY()))
//             int InterPoints = newLine.Intersect(PolyBoundLine, Inter1, Inter2);
//             if (InterPoints == 1)
                TotalInterPoint++;
//             //如果有两个交点则判断射线起点横坐标是否位于边坐标的横坐标范围内部
//             if (InterPoints == 2)
//             {
//                 if (HitPoint.getX() >= PolyBoundLine.getEnvelope().getMinX() &&
//                 	HitPoint.getX() >= PolyBoundLine.getEnvelope().getMaxX())
//                 {
//                     return true;
//                 }
//             }
        }

        if ((TotalInterPoint % 2) == 0) return false; else return true;
    }
    private boolean isIntersect(double px1, double py1, double px2, double py2,double px3, double py3, double px4, double py4)
    {
        boolean flag = false;
        double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);
        if (d != 0)
        {
            double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3))/d;
            double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1))/d;
            if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) { flag = true; }
        }
        return flag;
    }
}
