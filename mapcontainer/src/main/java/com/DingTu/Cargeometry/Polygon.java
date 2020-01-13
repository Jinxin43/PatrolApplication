package com.DingTu.Cargeometry;

import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkPartType;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Polygon extends Geometry {

    //初始化
    public Polygon()
    {
    }

    //基础属性

    //内点
    private Coordinate _InnerPoint = null;
    public Coordinate getCenterPoint()
    {
        if (_InnerPoint == null)
        {
            _InnerPoint = this.GetInnerPoint();
        }
        return _InnerPoint;
    }
    /**
     * 更新内点位置
     */
    public void UpdateInnerPoint()
    {
        _InnerPoint = this.GetInnerPoint();
    }

    //长度
    private double _Length = -1;         //长度
    public double getLength(boolean reCal)
    {
        if (reCal) this._Length = -1;
        if (this._Length == -1)
        {
            double AllLen = 0;
            for(int i=0;i<this.getPartCount();i++)
            {
                AllLen += this.GetPartAt(i).CalLength();
            }
            this._Length = AllLen;
        }
        return this._Length;
    }

    //面积
    private double _Area = 0;
    public double getArea(boolean reCal)
    {
        if (reCal) this._Area = -1;
        if (this._Area == -1)
        {
            double AllArea = 0;
            for(int i=0;i<this.getPartCount();i++)
            {
                Part part = this.GetPartAt(i);
                AllArea += Math.abs(part.CalArea())*(part.GetPartType()== lkPartType.enHole?-1:1);
            }
            this._Area = AllArea;
        }
        return this._Area;
    }

    //多边形内点计算
    private Coordinate GetInnerPoint()
    {
        int i,j;
        int n = this.GetPartAt(0).getVertexList().size();
        double ai,atmp=0,xtmp=0,ytmp=0;
        for(i=n-1,j=0;j<n;i=j,j++)
        {
            Coordinate pti = this.GetPartAt(0).getVertexList().get(i);
            Coordinate ptj = this.GetPartAt(0).getVertexList().get(j);
            ai=pti.getX()*ptj.getY()-ptj.getX()*pti.getY();
            atmp+=ai;
            xtmp+=(ptj.getX()+pti.getX())*ai;
            ytmp+=(ptj.getY()+pti.getY())*ai;
        }
        return new Coordinate(xtmp/(3*atmp),ytmp/(3*atmp));

//        List<Coordinate> IntersectPoint = new ArrayList<Coordinate>();
//        double X1, X2, Y1, Y2; Polyline PL = null;boolean Flip = false;
//        if (this.getEnvelope().getWidth() >= this.getEnvelope().getHeight())
//        {
//            Flip=true;
//            PL = new Polyline();
//            X1 = this.getEnvelope().getMinX() + this.getEnvelope().getWidth() / 2;
//            Y1 = this.getEnvelope().getMinY();
//            Y2 = this.getEnvelope().getMaxY();
//            PL.getVertexList().add(new Coordinate(X1, Y1));
//            PL.getVertexList().add(new Coordinate(X1, Y2));
//        }
//        else
//        {
//            PL = new Polyline();
//            Y1 = this.getEnvelope().getMinY() + this.getEnvelope().getHeight() / 2;
//            X1 = this.getEnvelope().getMinX();
//            X2 = this.getEnvelope().getMaxX();
//            PL.getVertexList().add(new Coordinate(X1, Y1));
//            PL.getVertexList().add(new Coordinate(X2, Y1));
//        }
//
//        //计算交点
//        if (this.getBorderLine().Intersect(PL, IntersectPoint))
//        {
//            //此处缺少排序过程，
//        	List<Double> DList = new ArrayList<Double>();
//            for (Coordinate Coor : IntersectPoint)
//            {
//                if (Flip)DList.add(Coor.getY()); else DList.add(Coor.getX());
//            }
//            Collections.sort(DList);
//            IntersectPoint.clear();
//            for (double D : DList)
//            {
//                if (Flip) IntersectPoint.add(new Coordinate(X1, D)); else IntersectPoint.add(new Coordinate(D,Y1));
//            }
//
//            //交点配对
//            double MaxDist = 0;int Index = -1;
//            for (int i = 0; i < IntersectPoint.size() / 2; i++)
//            {
//                int idx = i * 2;
//                double D = Tools.GetTwoPointDistance(IntersectPoint.get(idx), IntersectPoint.get(idx + 1));
//                if (D > MaxDist)
//                {
//                    MaxDist = D; Index = idx;
//                }
//            }
//
//            if (MaxDist != 0)
//            {
//                return new Coordinate((IntersectPoint.get(Index).getX() + IntersectPoint.get(Index + 1).getX()) / 2, (IntersectPoint.get(Index).getY() + IntersectPoint.get(Index + 1).getY()) / 2);
//            }
//        }
//        return this.getEnvelope().getCenter();
    }



    //实体选择

    ///// <summary>计算是否与指定的矩形搭边
    ///// </summary>
    ///// <param name="desEnve"></param>
    ///// <returns></returns>
    //public bool IntersectRect(Envelope desRect)
    //{
    //    //1、判断Polygon的外接矩形是否与指定矩形相交，不相交则返回false
    //    if (desRect.Contain(this.Envelope)) return true;
    //    if (!this.Envelope.Intersect(desRect)) return false;

    //    //2、将Polygon分解为多条直线段，分别与指定矩形判断是否相交
    //    int CoorCount = this.CoorList.Count;
    //    Coordinate StartPoint, EndPoint;
    //    for (int i = 0; i <= CoorCount - 1; i++)
    //    {
    //        StartPoint = (Coordinate)this.CoorList[i];
    //        if (i == CoorCount - 1)
    //        {
    //            EndPoint = (Coordinate)this.CoorList[0];
    //        }
    //        else
    //        {
    //            EndPoint = (Coordinate)this.CoorList[i + 1];
    //        }
    //        if (desRect.IntersectLine(new Line(StartPoint, EndPoint))) return true;
    //    }
    //    return false;
    //}


    @Override
    public Geometry Clone()
    {
        Polygon newPolygon = new Polygon();
        for(int p=0;p<this.getPartCount();p++)
        {
            newPolygon.AddPart(this.GetPartAt(p).Clone());
        }
        return newPolygon;
    }


	/*计算点是否在面内，如果在面内则选中
	    * 思路：将指定的点向右做一条射线，如果此射线与多边形有奇数个交点，则点在多边形内部，
	    *       为0或偶数则在多边形外部
	    */
//	public boolean isPointInPolygonEx(double px, double py)
//	{
//		if (this.IsSimple()) return isPointInPolygonVertexList(px,py,this.GetAllCoordinateList());
//		else
//		{
//			List<Coordinate> CoorList = this._BorderLine.GetPartAt(0);
//			if (isPointInPolygonVertexList(px,py,CoorList))
//			{
//				int parts = this.getPartCount();
//				for(int i=1;i<parts;i++)
//				{
//					if (isPointInPolygonVertexList(px,py,this.GetPartAt(i))) return false;
//				}
//				return true;
//			}
//			return false;
//		}
//
//	}
//
//	  private boolean isPointInPolygonVertexList(double px, double py,List<Coordinate> CoorList)
//	  {
//		 //ArrayList<Double> polygonXA, ArrayList<Double> polygonYA) {
//		boolean isInside = false;
//		 double ESP = 1e-9;
//		int count = 0;
//		double linePoint1x;
//		double linePoint1y;
//		double linePoint2x;
//		 double linePoint2y;
//		linePoint1x = px;
//		linePoint1y = py;
//
//		linePoint2x = linePoint1x * 2;
//		linePoint2y = py;
//
//		 for (int i = 0; i < CoorList.size() - 1; i++)
//		{
//			 double cx1 = CoorList.get(i).getX();
//			double cy1 = CoorList.get(i).getY();
//			double cx2 = CoorList.get(i+1).getX();
//			double cy2 = CoorList.get(i+1).getY();
//			if (isPointOnLine(px, py, cx1, cy1, cx2, cy2)) return true;
//
//
//			if (Math.abs(cy2 - cy1)< ESP) continue;
//
//			if (isPointOnLine(cx1, cy1, linePoint1x, linePoint1y, linePoint2x, linePoint2y))
//			{
//				 if (cy1 > cy2)  count++;
//			}
//			 else if (isPointOnLine(cx2, cy2, linePoint1x, linePoint1y,linePoint2x, linePoint2y))
//			{
//				 if (cy2 > cy1)count++;
//			}
//			 else if (isIntersect(cx1, cy1, cx2, cy2, linePoint1x, linePoint1y, linePoint2x, linePoint2y))
//			 {
//				 count++;
//			 }
//		}
//		if (count % 2 == 1) isInside = true;
//		return isInside;
//	}
//
//	  private boolean isPointOnLine(double px0, double py0, double px1,double py1, double px2, double py2)
//		{
//			boolean flag = false;
//			double ESP = 1e-9;
//			if ((Math.abs(Multiply(px0, py0, px1, py1, px2, py2)) < ESP) && ((px0 - px1) * (px0 - px2) <= 0) && ((py0 - py1) * (py0 - py2) <= 0))
//			{
//				flag = true;
//			}
//			return flag;
//		}
//	  private double Multiply(double px0, double py0, double px1, double py1, double px2, double py2)
//			{
//				 return ((px1 - px0) * (py2 - py0) - (px2 - px0) * (py1 - py0));
//			}



    /*计算点是否在面内，如果在面内则选中
    * 思路：将指定的点向右做一条射线，如果此射线与多边形有奇数个交点，则点在多边形内部，
    *       为0或偶数则在多边形外部
    */
    @Override
    public boolean HitTest(Coordinate HitPoint, double Tolerance)
    {
        //1、判断点是否在多边形的外接矩形内部
        if (!this.getEnvelope().ContainsPoint(HitPoint)) return false;

        //2、分部分判断
        boolean InClick = false;   //在enPoly内部点中
        for (int i = 0; i < this.getPartCount(); i++)
        {
            Part part = this.GetPartAt(i);
            boolean HitOK = part.ContainsPoint(HitPoint);
            if (HitOK)
            {
                InClick = true;
//                if (part.GetPartType()==lkPartType.enHole) return false;
            }
        }
        return InClick;
    }


    @Override
    public boolean Offset(double OffsetX, double OffsetY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public lkGeoLayerType GetType() {
        return lkGeoLayerType.enPolygon;
    }

    /**
     * 面闭合处理
     */
    public void Closed()
    {
        for(int p = 0;p<this.getPartCount();p++)
        {
            this.GetPartAt(p).Closed();
        }
    }
}
