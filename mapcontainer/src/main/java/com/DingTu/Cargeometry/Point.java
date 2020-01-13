package com.DingTu.Cargeometry;

import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkGeometryStatus;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Point extends Geometry {

    public Point()
    {
        //初始原始状态
        this.setStatus(lkGeometryStatus.enNormal);
    }

    //简单点
    public Point(double X,double Y)
    {
        Part part = new Part();
        part.getVertexList().add(new Coordinate(X, Y));
        this.AddPart(part);
    }
    public Point(Coordinate Coor)
    {
        Part part = new Part();
        part.getVertexList().add(Coor);
        this.AddPart(part);
    }

    //X坐标-此属性只对IsSimple=true有效
    public Coordinate getCoordinate()
    {
        return this.GetPartAt(0).getVertexList().get(0);
    }

    public Coordinate getCenterPoint()
    {
        return getCoordinate();
    }

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
//    //实体空间关系
//    public SpatialRelation _SpatialRelation = null;
//    public SpatialRelation SpatialRelation
//    {
//        get
//        {
//            if (_SpatialRelation == null) _SpatialRelation = new SpatialRelation(this);
//            return _SpatialRelation;
//        }
//    }


    //计算两点之间的距离
    public double DistanceTo(Coordinate desPoint)
    {
        //简单实体
//        if (this.IsSimple())
//        {
        return Tools.GetTwoPointDistance(this.GetPartAt(0).getVertexList().get(0), desPoint);
//        }
//        else   //如果多部分点则分别计算，返回最近距离
//        {
//            double MinDistance = Double.MAX_VALUE;
//            for (Coordinate Pt : this.getItems())
//            {
//                double D = Tools.GetTwoPointDistance(Pt, desPoint);
//                if (D < MinDistance) MinDistance = D;
//            }
//            return MinDistance;
//        }
    }


    //克隆实体
    @Override
    public Geometry Clone() {
//        List<Coordinate> ptList = new ArrayList<Coordinate>();
//        for(Coordinate Coor : this.GetAllCoordinateList())
//        {
//            ptList.add(Coor.Clone());
//        }
        Point newPoint = new Point(this.GetPartAt(0).getVertexList().get(0).getX(),this.GetPartAt(0).getVertexList().get(0).getY());
        return newPoint;
        //return null;
    }

    //点选实体
    @Override
    public boolean HitTest(Coordinate HitPoint, double Tolerance) {
        if (this.DistanceTo(HitPoint) <= Tolerance) return true; else return false;

    }

    @Override
    public boolean Offset(double OffsetX, double OffsetY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public lkGeoLayerType GetType() {
        // TODO Auto-generated method stub
        return lkGeoLayerType.enPoint;
    }
}
