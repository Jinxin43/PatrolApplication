package com.DingTu.Cargeometry;

import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkGeometryStatus;
import com.DingTu.Spatial.SpatialRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Polyline extends Geometry {

    //初始化线实体
    public Polyline()
    {
        this.setStatus(lkGeometryStatus.enNormal);
    }



    //长度
    private double _Length = -1;         //长度
    public double getLength(boolean reCal)  //计算长度
    {
        if (reCal) _Length = this.CalLength();
        if (_Length == -1) _Length = this.CalLength();
        return _Length;
    }

    //计算线的长度
    private double CalLength()
    {
        double AllLen = 0;
        for(int i=0;i<this.getPartCount();i++)
        {
            AllLen += this.GetPartAt(i).CalLength();
        }
        return AllLen;
    }
//    public double CalLengthToVertex(int VertexIndex)
//    {
//        double LineLength = 0;
//        Coordinate LP1, LP2;
//        for (int i = 0; i < VertexIndex; i++)
//        {
//            LP1 = this.getVertexList().get(i);
//            LP2 = this.getVertexList().get(i + 1);
//            LineLength += Tools.GetTwoPointDistance(LP1, LP2);
//        }
//        return LineLength;
//    }
//    public double CalLengthToVertex(Coordinate Coor)
//    {
//        int VIndex=0;
//        for (Coordinate pCoor : this.getVertexList())
//        {
//            if (Math.abs(pCoor.getX() - Coor.getX()) < 1 && Math.abs(pCoor.getY() - Coor.getY()) < 1) break;
//            //if (pCoor.Equal(Coor)) break;
//            VIndex++;
//        }
//        if (VIndex == this.getVertexList().size()) VIndex = 0;
//        return CalLengthToVertex(VIndex);
//    }


    //线的起点坐标
    public Coordinate getStartPoint()
    {
        return this.GetPartAt(0).getVertexList().get(0);
    }

    //线的止点坐标
    public Coordinate getEndPoint()
    {
        Part endPart = this.GetPartAt(this.getPartCount() - 1);
        return endPart.getVertexList().get(endPart.getVertexList().size()-1);
    }

    public Coordinate getCenterPoint()
    {
        int currentPoint = 0;
        int allPointsCount = this.getVertexCount();
        int middleIndex = allPointsCount/2 +1;
        int curPointSum = 0;
        for(Part p:_PartList)
        {
            curPointSum = curPointSum + p.getVertexList().size();
            if(curPointSum<middleIndex)
            {
                currentPoint += p.getVertexList().size();
                continue;
            }
            else
            {
                if(curPointSum == middleIndex)
                {
                    return p.getVertexList().get(p.getVertexList().size()-1);
                }
                else
                {
                    for(int i = 0;i<p.getVertexList().size();i++)
                    {
                        currentPoint++;
                        if( currentPoint == middleIndex)
                        {
                            return p.getVertexList().get(i);
                        }
                    }
                }

            }

        }

        //应该执行不到，但是还是要检查是否为空
        return null;

    }

    //空间操作

    //    //实体空间操作
//    private SpatialOperator _SpatialOperator = null;
//    public SpatialOperator SpatialOperator
//    {
//        get
//        {
//            if (_SpatialOperator == null) _SpatialOperator = new SpatialOperator();
//            return _SpatialOperator;
//        }
//    }
//
    //实体空间关系
    private SpatialRelation _SpatialRelation = null;
    public SpatialRelation getSpatialRelation()
    {
        if (_SpatialRelation == null) _SpatialRelation = new SpatialRelation(this);
        return _SpatialRelation;
    }


    //改变线的方向，线段翻转操作
    public boolean Flip()
    {
        for(int p=0;p<this.getPartCount();p++)
        {
            Part part = this.GetPartAt(p);
            Tools.ReverseList(part.getVertexList());
        }
        return true;
    }

    //计算两条线的交点
    public boolean Intersect(Polyline InterPolyline,List<Coordinate> InterPointList)
    {
        //1、判断两Polyline的外接矩形是否与指定矩形相交，不相交则返回false
        if (!this.getEnvelope().Intersect(InterPolyline.getEnvelope()))
        {
            if (!this.getEnvelope().Contains(InterPolyline.getEnvelope())) return false;
        }

        //2、将两条Polyline分解为多条直线段，分别判断是否相交
        int CoorCount1 = this.GetPartAt(0).getVertexList().size();
        int CoorCount2 = InterPolyline.GetPartAt(0).getVertexList().size();
        Line L1, L2;
        for (int i = 0; i <= CoorCount1 - 2; i++)
        {
            L1 = new Line(this.GetPartAt(0).getVertexList().get(i), this.GetPartAt(0).getVertexList().get(i + 1));
            for (int j = 0; j <= CoorCount2 - 2; j++)
            {
                L2 = new Line(InterPolyline.GetPartAt(0).getVertexList().get(j), InterPolyline.GetPartAt(0).getVertexList().get(j + 1));
                if (L1.Intersect(L2))
                {
                    Coordinate P1 = new Coordinate();
                    Coordinate P2 = new Coordinate();
                    int IntersectCount = L1.Intersect(L2, P1, P2);
                    if (IntersectCount == 1) InterPointList.add(P1.Clone());
                    if (IntersectCount == 2) { InterPointList.add(P1.Clone()); InterPointList.add(P2.Clone()); }
                }
            }
        }
        if (InterPointList.size() > 0) return true; else return false;
    }

    /**
     * 线打断
     * @param SplitPoint
     * @param PL1
     * @param PL2
     * @return
     */
    public boolean Split(Coordinate SplitPoint,Polyline PL1,Polyline PL2)
    {
        int VertexIndex = -1;
        Part reshapePart = this.GetPartAt(0);
        for(int i=0;i<reshapePart.getVertexList().size()-1;i++)
        {
            Coordinate PT1 = reshapePart.getVertexList().get(i);
            Coordinate PT2 = reshapePart.getVertexList().get(i+1);
            double JL0 = GetDistance(PT1, PT2);
            double JL1 = GetDistance(PT1, SplitPoint);
            double JL2 = GetDistance(PT2, SplitPoint);
            if (Math.abs(JL0-JL1-JL2)<0.00001)
            {
                VertexIndex = i;break;
            }
        }
        if (VertexIndex==-1) return false;

        Part newPart1 = new Part();PL1.AddPart(newPart1);
        Part newPart2 = new Part();PL2.AddPart(newPart2);
        for(int i=0;i<=VertexIndex;i++)
        {
            newPart1.getVertexList().add(reshapePart.getVertexList().get(i));
        }
        newPart1.getVertexList().add(SplitPoint.Clone());

        newPart2.getVertexList().add(SplitPoint.Clone());
        for(int i=VertexIndex+1;i<reshapePart.getVertexList().size();i++)
        {
            newPart2.getVertexList().add(reshapePart.getVertexList().get(i));
        }
        return true;
    }


    //计算点到直线的距离，如果距离在指定的范围内则选中
    @Override
    public boolean HitTest(Coordinate HitPoint, double Tolerance)
    {
        for(int p=0;p<this.getPartCount();p++)
        {
            if (this.GetPartAt(p).HitSegmentTest(HitPoint, Tolerance)!=-1) return true;
        }
        return false;
    }

//    //检测是否在指定点处选中实体，并返回Segment索引值
//    public boolean HitTestSegment(Coordinate SelPoint, double Tolerance, Param SegmentIndex)
//    {
//        Coordinate LinePoint1, LinePoint2;
//        Line SegmentLine = null; SegmentIndex.setValue(-1);
//
//        //判断是否在最大外接矩形内部，如果在外部则退出
//        if (!this.getEnvelope().ContainsPoint(SelPoint)) return false;
//        for (int i = 0; i <= this.GetPartAt(0).getVertexList().size() - 2; i++)
//        {
//            SegmentIndex.setValue(i);
//            LinePoint1 = this.GetPartAt(0).getVertexList().get(i);
//            LinePoint2 = this.GetPartAt(0).getVertexList().get(i + 1);
//            SegmentLine = new Line(LinePoint1, LinePoint2);
//            if (SegmentLine.PointToLineDistance(SelPoint,Tolerance))
//            {
//                return true;
//            }
//        }
//        return false;
//    }

//    //返回指定线上点的前一个内点的索引值
//    public boolean HitTestSegment(Coordinate SelPoint, Param SegmentIndex)
//    {
//        Coordinate LinePoint1, LinePoint2;
//        Line SegmentLine = null; SegmentIndex.setValue(-1);
//        for (int i = 0; i <= this.getVertexList().size() - 2; i++)
//        {
//            SegmentIndex.setValue(i);
//            LinePoint1 = this.getVertexList().get(i);
//            LinePoint2 = this.getVertexList().get(i + 1);
//            SegmentLine = new Line(LinePoint1, LinePoint2);
//            if (SegmentLine.PointOnLine(SelPoint))
//            {
//                return true;
//            }
//        }
//        return false;
//    }

//    //得到距离起点的距离
//    public boolean GetToStartDistance(Coordinate SelPoint, double Tolerance,Param ToStartDistanceT)
//    {
//        double LineLength = 0; double ToStartDistance=0;
//        Coordinate LinePoint1, LinePoint2; Param PerDistanceT = new Param();
//        Line SegmentLine = null;
//
//        for (int i = 0; i <= this.getVertexList().size() - 2; i++)
//        {
//            if (i >= 1) ToStartDistance += LineLength;
//            LinePoint1 = this.getVertexList().get(i);
//            LinePoint2 = this.getVertexList().get(i + 1);
//            SegmentLine = new Line(LinePoint1, LinePoint2);
//            LineLength = SegmentLine.Length();
//            if (SegmentLine.PointToLineDistance(SelPoint, Tolerance, PerDistanceT))
//            {
//                ToStartDistance += PerDistanceT.getDouble();
//                ToStartDistanceT.setValue(ToStartDistance);
//                return true;
//            }
//        }
//        ToStartDistance = 0;ToStartDistanceT.setValue(ToStartDistance);
//        return false;
//    }

//    //得到距起点指距离的坐标点
//    public boolean GetToStartCoordinate(double ToStartDistance, Param BeforeVertexIndexT,Coordinate ToStartCoordinate)
//    {
//        double LineLength = 0; double StartDistance = 0;
//        Coordinate LinePoint1, LinePoint2; int BeforeVertexIndex = -1; ToStartCoordinate = null;
//        Line SegmentLine = null;
//
//        for (int i = 0; i <= this.getVertexList().size() - 2; i++)
//        {
//            LinePoint1 = this.getVertexList().get(i);
//            LinePoint2 = this.getVertexList().get(i + 1);
//
//            //累计长度
//            LineLength = Tools.GetTwoPointDistance(LinePoint1, LinePoint2);
//            StartDistance += LineLength;
//            if (StartDistance >= ToStartDistance)
//            {
//                SegmentLine = new Line(LinePoint1, LinePoint2);
//                BeforeVertexIndex = i;
//                ToStartCoordinate = SegmentLine.GetToStartCoordinate(StartDistance-ToStartDistance);
//                BeforeVertexIndexT.setValue(BeforeVertexIndex);
//                return true;
//            }
//        }
//        BeforeVertexIndexT.setValue(BeforeVertexIndex);
//        return false;
//    }


    //内节点操作

//    //判断指定的坐标点是否在坐标串内
//    public int InVertexList(Coordinate pPoint)
//    {
//        //不在线的外接矩形内部
//        if (!this.getEnvelope().ContainsPoint(pPoint)) return -1;
//
//        int index = -1;
//        for (Coordinate Coor : this.GetAllCoordinateList())
//        {
//            index++;
//            if (Coor.Equal(pPoint)) return index;
//        }
//        return -1;
//    }

//    //检测指定的HitPoint点与实体内点最近点的索引值
//    public boolean HitVertexTest(Coordinate HitPoint, double Tolerance, Param VertexIndexT)
//    {
//        int VertexIndex = -1;
//        double NearestDist = Double.MAX_VALUE;
//
//        for (int i = 0; i < this.getVertexList().size(); i++)
//        {
//            double D = Tools.GetTwoPointDistance(HitPoint, this.getVertexList().get(i));
//            if (D<NearestDist)
//            {
//                NearestDist = D; VertexIndex = i;
//            }
//        }
//        VertexIndexT.setValue(VertexIndex);
//        if (NearestDist <= Tolerance) return true; else {VertexIndexT.setValue(-1);return  false;}
//
//    }


    //克隆实体
    @Override
    public Geometry Clone()
    {
        Polyline PL = new Polyline();
        for (int i=0;i<this.getPartCount();i++)
        {
            PL.AddPart(this.GetPartAt(i).Clone());
        }
        return PL;
    }

    @Override
    public boolean Offset(double OffsetX, double OffsetY)
    {
        return true;
    }



    @Override
    public lkGeoLayerType GetType() {
        // TODO Auto-generated method stub
        return lkGeoLayerType.enPolyline;
    }

    /**
     * 平行线
     * @param offsetDis
     * @return
     */
    public List<Coordinate> Offset(double offsetDis)
    {
        int VertexCount = this.getVertexCount();
        List<Coordinate> CoorList = this.GetPartAt(0).getVertexList();
        List<Coordinate> LTList = new ArrayList<Coordinate>();

        //起端点
        Coordinate StartCoor = this.GetInnerPointOffsetDistance(CoorList.get(0),CoorList.get(1),offsetDis,false);
        LTList.add(StartCoor);

        //右侧
        for (int i = 0; i < VertexCount - 2; i++)
        {
            LTList.add(this.GetMidOffsetPoint(CoorList.get(i), CoorList.get(i + 1), CoorList.get(i + 2), offsetDis, false));
        }

        //止端点
        Coordinate EndCoor = this.GetInnerPointOffsetDistance(CoorList.get(CoorList.size()-1),CoorList.get(CoorList.size()-2),offsetDis,false);
        LTList.add(EndCoor);

        return LTList;
    }

    /// <summary>根据三点坐标求指定偏移距离的中间坐标点，应用角平分线法
    /// </summary>
    /// <param name="PointA">第一点</param>
    /// <param name="PointB">中间点</param>
    /// <param name="PointC">第三点</param>
    /// <param name="OffsetDistance">偏移距离</param>
    /// <param name="Flip">方向，前进方向左=true,前进方向右=false</param>
    /// <returns></returns>
    private Coordinate GetMidOffsetPoint(Coordinate PointA, Coordinate PointB, Coordinate PointC, double OffsetDistance, boolean Flip)
    {
        //1、计算AB，BC的方位角
        double A_ba = Bearing(PointB, PointA);
        double A_bc = Bearing(PointB, PointC);
        //double A_ba = (A_ab < 180) ? A_ab + 180 : 360 - A_ab;

        //2、计算Qb,Bb
        double Qb = (A_bc > A_ba) ? A_bc - A_ba : A_bc - A_ba + 360;
        double Bb = A_ba + Qb / 2;

        //3、计算Dr
        double Dr = Math.abs(OffsetDistance / (Math.sin(JDtoHD(Qb / 2))));

        //4、计算X，Y的偏移量
        double DeltX = Math.cos(JDtoHD(Bb)) * Dr;
        double DeltY = Math.sin(JDtoHD(Bb)) * Dr;

        //5、计算坐标点
        if (Flip) return new Coordinate(PointB.getX() + DeltX, PointB.getY() + DeltY);
        else return new Coordinate(PointB.getX() - DeltX, PointB.getY() - DeltY);
    }

    //返回两点的方位角，以第一个点为基点
    private double Bearing(Coordinate BasePoint, Coordinate SecondPoint)
    {
        double Dx = SecondPoint.getX()-BasePoint.getX();
        double Dy = SecondPoint.getY()-BasePoint.getY();
        double DD = Math.abs(HDtoJD(Math.atan(Dx / Dy)));
        if (Dx > 0 && Dy > 0) return 90-DD;
        if (Dx < 0 && Dy > 0) return 90 + DD;
        if (Dx < 0 && Dy < 0) return 270 - DD;
        if (Dx > 0 && Dy < 0) return 270 + DD;
        return DD;
    }
    /// <summary>根据直线上两点坐标中A点，求在AB线上并指定偏移距离的坐标点
    /// </summary>
    /// <param name="PointA"></param>
    /// <param name="PointB"></param>
    /// <param name="OffsetDistance"></param>
    /// <param name="InnerOut"></param>
    /// <returns></returns>
    private Coordinate GetInnerPointOffsetDistance(Coordinate PointA, Coordinate PointB, double OffsetDistance, boolean InnerOut)
    {
        //求AB两点的夹角
        double A = Math.atan((PointB.getY() - PointA.getY()) / (PointB.getX() - PointA.getX()));

        //求偏移距离
        double DeltX = Math.sin(A) * OffsetDistance;
        double DeltY = Math.cos(A) * OffsetDistance;

        double X=0,Y=0;
        if (InnerOut)
        {
            X = PointA.getX() + DeltX;
            Y = PointA.getY() - DeltY;
        }
        else
        {
            X = PointA.getX() - DeltX;
            Y = PointA.getY() + DeltY;
        }
        return new Coordinate(X, Y);

    }
    private double HDtoJD(double HD)
    {
        return HD * 180.0 / Math.PI;
    }

    private double JDtoHD(double JD)
    {
        return JD * Math.PI / 180.0;
    }
    private double GetDistance(Coordinate P1,Coordinate P2)
    {
        return Math.sqrt((P1.getX() - P2.getX()) * (P1.getX() - P2.getX()) + (P1.getY() - P2.getY()) * (P1.getY() - P2.getY()));
    }
}
