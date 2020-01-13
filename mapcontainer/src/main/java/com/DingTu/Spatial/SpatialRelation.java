package com.DingTu.Spatial;

import com.DingTu.Cargeometry.Envelope;
import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Cargeometry.Line;
import com.DingTu.Cargeometry.Polyline;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class SpatialRelation {
    private Geometry _Geometry = null;

    public SpatialRelation(Geometry pGeometry)
    {
        _Geometry = pGeometry;
    }

    private int GetGeometryType(Geometry pGeometry)
    {
        //判断基实体的类型
        switch(pGeometry.GetType())
        {
            case enPoint:
                return 0;        //点
            case enPolyline:
                return 1;		 //线
            case enPolygon:
                return 2;	     //面
        }
        return -1;
    }

    //将基实体转换成线实体
    private Polyline ConvertToPolyline(Geometry pGeometry)
    {
        switch (this.GetGeometryType(pGeometry))
        {
            case 1:
                return (Polyline)pGeometry;
            case 2:
                //return ((Polygon)(pGeometry)).ConvertToPolyline();
        }
        return null;
    }
    private Polyline ConvertToPolyline()
    {
        return this.ConvertToPolyline(_Geometry);
    }


    //包含关系[Contains(Envelope)]

    //判断是否在指定的矩形内部
    //主要思路：判断实体的外接矩形是在指定矩形的内部，如果在指定矩形内部则属于全完包含关系
    public boolean Contains(Envelope pEnvelope)
    {
        return (pEnvelope.Contains(_Geometry.getEnvelope()));
    }

    //相交关系[Intersect]

    /*判断实体与指定多坐标点实体是否搭边
     思路：判断直线段与坐标点实体是否相交，又可折分为两直线段是否相交
     步骤：1、判断Polyline的外接矩形是否与指定实体的外接矩形相交，不相交则返回false
           2、将Polyline分解为多条直线段，分别与指定实体拆分的线段判断是否相交
           3、可首先判断一条直线段的外接矩形是否与指定矩形相交，如果不相交则返回false
           4、再继续判断每条直线段是否与指定矩形相交（分段为直线段与直线相交情况）
     */
    public boolean Intersect(Geometry pGeometry /*对线状实体有效*/)
    {
        Polyline pPolyline1 = this.ConvertToPolyline();
        Polyline pPolyline2 = this.ConvertToPolyline(pGeometry);

        //1、判断两Polyline的外接矩形是否与指定矩形相交，不相交则返回false
        if (!pPolyline2.getEnvelope().Intersect(pPolyline1.getEnvelope())) return false;

        //2、将两条Polyline分解为多条直线段，分别判断是否相交
        int CoorCount1 = pPolyline1.GetPartAt(0).getVertexList().size();
        int CoorCount2 = pPolyline2.GetPartAt(0).getVertexList().size();

        Line L1, L2;
        for (int i = 0; i <= CoorCount1 - 2; i++)
        {
            L1 = new Line(pPolyline1.GetPartAt(0).getVertexList().get(i), pPolyline1.GetPartAt(0).getVertexList().get(i + 1));
            for (int j = 0; j <= CoorCount2 - 2; j++)
            {
                L2 = new Line(pPolyline2.GetPartAt(0).getVertexList().get(j), pPolyline2.GetPartAt(0).getVertexList().get(j + 1));
                if (L1.Intersect(L2))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
