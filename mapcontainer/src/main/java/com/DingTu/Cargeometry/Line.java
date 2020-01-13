package com.DingTu.Cargeometry;

import com.DingTu.Base.Tools;
import com.DingTu.Map.Param;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Line {

    private Coordinate _StartPoint, _EndPoint;
    public Coordinate getStartPoint()
    {
        return _StartPoint;
    }
    public Coordinate getEndPoint()
    {
        return _EndPoint;
    }
    public Line(Coordinate Pt1, Coordinate Pt2)
    {
        _StartPoint = Pt1; _EndPoint = Pt2;
    }

    //最大外接矩形
    private Envelope _Envelope;
    public Envelope getEnvelope()
    {
        if (_Envelope == null)
        {
            //计算合理的外接矩形
            double MinX, MinY, MaxX, MaxY;
            if (_StartPoint.getX() > _EndPoint.getX())
            {
                MinX = _EndPoint.getX(); MaxX = _StartPoint.getX();
            }
            else
            {
                MinX = _StartPoint.getX(); MaxX = _EndPoint.getX();
            }

            if (_StartPoint.getY() > _EndPoint.getY())
            {
                MinY = _EndPoint.getY(); MaxY = _StartPoint.getY();
            }
            else
            {
                MinY = _StartPoint.getY(); MaxY = _EndPoint.getY();
            }
            _Envelope = new Envelope(MinX, MaxY, MaxX, MinY);
        }
        return _Envelope;
    }


    //计算选择点是否在指定的距离内被选中，如果选中则返回选择点的垂点及垂点距离线段起点的距离

    //重载1
    public boolean PointToLineDistance(Coordinate SelPoint, double Tolerance)
    {

        Coordinate PerPoint = null; Param LineInnerPoint = new Param();
        double PerDistance = this.PointToLineNearestDistance(SelPoint, LineInnerPoint, PerPoint);
        if (PerDistance <= Tolerance) return true; else return false;
        //return this.PointToLineDistance(SelPoint, Tolerance, out PerDistance, out PerPoint, out LineInnerPoint);
    }

    //重载1
    public boolean PointToLineDistance(Coordinate SelPoint, double Tolerance,
                                       Param PerDistanceT/*垂点距离起点的距离*/)
    {
        //double PerDistance = PerDistanceT.getDouble();
        Coordinate PerPoint = null; boolean LineInnerPoint = false;Param LineInnerPointT = new Param();
        double PerDistance = this.PointToLineNearestDistance(SelPoint, LineInnerPointT, PerPoint);
        LineInnerPoint = LineInnerPointT.getBoolean();
        if (PerDistance <= Tolerance)
        {
            if (LineInnerPoint) PerDistance = Tools.GetTwoPointDistance(this._StartPoint, PerPoint);
            PerDistanceT.setValue(PerDistance);
            return true;
        }
        else
        {
            PerDistanceT.setValue(PerDistance);
            return false;
        }
        //return this.PointToLineDistance(SelPoint, Tolerance, out PerDistance, out PerPoint, out LineInnerPoint);
    }
    //重载2
    public boolean PointToLineDistance(Coordinate SelPoint, double Tolerance,
                                       Param PerDistanceT/*垂点距离起点的距离*/,
                                       Coordinate PerPoint /*垂直点坐标*/,
                                       boolean LineInnerPoint /*垂点是否为线内点*/)
    {
        Param LineInnerPointT = new Param();LineInnerPointT.setValue(LineInnerPoint);
        double PerDistance = this.PointToLineNearestDistance(SelPoint, LineInnerPointT, PerPoint);
        if (PerDistance <= Tolerance)
        {
            if (LineInnerPoint) PerDistance = Tools.GetTwoPointDistance(this._StartPoint, PerPoint);
            PerDistanceT.setValue(PerDistance);
            return true;
        }
        else
        {
            PerDistanceT.setValue(PerDistance);
            return false;
        }

        //return this.PointToLineDistance(SelPoint.X, SelPoint.Y, Tolerance, out PerDistance, out PerPoint, out LineInnerPoint);
    }
    ////重载3
    //public bool PointToLineDistance(double xx, double yy, double Tolerance,
    //                                out double PerDistance/*垂点距离起点的距离*/,
    //                                out Coordinate PerPoint /*垂直点坐标*/,
    //                                out bool LineInnerPoint /*垂点是否为线内点*/)
    //{
    //    PerDistance = double.MaxValue;
    //    PerPoint = null; LineInnerPoint = false;
    //    //x1 , y1 , x2 , y2 : 为线段两端点坐标
    //    //xx , yy : 为线外一点的坐标
    //    //pt1 , pt2 的斜率为 ：k = ( y2 - y1 ) / (x2 - x1 );
    //    //该直线方程为： y = k* ( x - x1) + y1
    //    //其垂线的斜率为 - 1 / k，
    //    //垂线方程为：  y = (-1/k) * (x - xx) + yy
    //    //联立两直线方程解得：
    //    //x  =  ( k^2 * x1 + k * (yy - y1 ) + xx ) / ( k^2 + 1)
    //    //y  =  k * ( x - x1) + y1;

    //    double x1 = this._StartPoint.X;
    //    double y1 = this._StartPoint.Y;

    //    double x2 = this._EndPoint.X;
    //    double y2 = this._EndPoint.Y;


    //    //求线段外一点到线段的垂足X,Y坐标
    //    double XX, YY, LK;
    //    if (y1 == y2)      // 平行于X轴
    //    {
    //        XX = xx; YY = y1;
    //    }
    //    else if (x1 == x2)  //平行于Y轴
    //    {
    //        XX = x1; YY = yy;
    //    }
    //    else
    //    {
    //        //注释部分，此方法计算不准确
    //        //LK = (y2 - y1) / (x2 - x1);  //求已知线段的斜率
    //        //double XX1 = (LK * LK * x1 + LK * (yy - y1) + xx) / (LK * LK + 1);
    //        //double YY1 = LK * (XX1 - x1) + y1;

    //        double abx = x2 - x1;
    //        double aby = y2 - y1;
    //        double acx = xx - x1;
    //        double acy = yy - y1;
    //        double ff = (abx * acx + aby * acy) / (abx * abx + aby * aby);     //   注意ab必须是直线上的两个不同点
    //        XX = x1 + ff * abx;
    //        YY = y1 + ff * aby;
    //    }


    //    double a = Tools.GetTwoPointDistance(x1, y1, xx, yy);  //线外一点与第一端点的距离
    //    double b = Tools.GetTwoPointDistance(x2, y2, xx, yy);  //线外一点与第二端点的距离
    //    double c = Tools.GetTwoPointDistance(x1, y1, x2, y2);  //线段两端点距离
    //    double d = Tools.GetTwoPointDistance(XX, YY, x1, y1);  //投影点到第一端点距离
    //    double e = Tools.GetTwoPointDistance(XX, YY, x2, y2);  //投影点到第二端点距离
    //    double f = Tools.GetTwoPointDistance(XX, YY, xx, yy);  //线外一点与其投影点的距离

    //    //投影点与两端点距离相加与两端点距离相等,则投影点在线段之内,否则投影点在线段延长线上
    //    //1.当投影点在线段内,那么线外点到线段的距离就是线外点与投影点的距离
    //    //2.当投影点在线段外,那么选择线外点与两端点距离近的为线外点与线段的距离,如果投影点与
    //    //  某一个端点距离小于0.5米,并且线外点与这个端点的距离满足设定范围,则取这个端点作为投影点
    //    if (Math.Abs(d + e - c) < 0.00000001 )
    //    {
    //        PerPoint = new Coordinate(XX, YY);
    //        PerDistance = d;
    //        LineInnerPoint = true;
    //        if (f <= Tolerance) return true; else return false;
    //    }
    //    else  //投影点在线延长线上
    //    {
    //        if (d <= e)  //投影点靠近第一个端点
    //        {
    //            PerPoint = new Coordinate(x1, y1);
    //            PerDistance = d;
    //            LineInnerPoint = false;
    //            if (a <= Tolerance) return true; else return false;
    //        }
    //        else
    //        {
    //            PerPoint = new Coordinate(x2, y2);
    //            PerDistance = c+e;
    //            LineInnerPoint = false;
    //            if (b <= Tolerance) return true; else return false;
    //        }
    //    }
    //}



    /// <summary>
    /// 计算点到线段的最短距离
    /// </summary>
    /// <param name="SelPoint"></param>
    /// <returns></returns>
    public double PointToLineNearestDistance(Coordinate SelPoint)
    {
        Param LineInnerPoint=new Param();LineInnerPoint.setValue(false);Coordinate PerPoint=null;
        return this.PointToLineNearestDistance(SelPoint, LineInnerPoint, PerPoint);
    }

    /// <summary>计算点到线段的最短距离
    /// </summary>
    /// <param name="SelPoint">选择点</param>
    /// <param name="LineInnerPoint">是否为线上点</param>
    /// <param name="PerPoint">垂点坐标，如果不是线上点，则返回与选择点最近的端点坐标</param>
    /// <returns></returns>
    //public double PointToLineNearestDistance(Coordinate SelPoint, out bool LineInnerPoint, out Coordinate PerPoint)
    //{
    //    LineInnerPoint = false; PerPoint = null;
    //    Coordinate a = this.StartPoint;
    //    Coordinate b = this.EndPoint;
    //    Coordinate c = SelPoint;

    //    Coordinate ab = this.aDb(b, a);
    //    Coordinate ac = this.aDb(c, a);
    //    double f = aXb(ab, ac);

    //    if (f < 0) { PerPoint = a; return MapTools.Tools.GetTwoPointDistanceHD(c, a); }
    //    double d = this.aXb(ab, ab);
    //    if (f > d) { PerPoint = b; return MapTools.Tools.GetTwoPointDistanceHD(c, b); }
    //    f = f / d;
    //    Coordinate D = this.aAb(a, this.TXb(f, ab));   // c在ab线段上的投影点
    //    PerPoint = D; LineInnerPoint = true;
    //    return MapTools.Tools.GetTwoPointDistanceHD(c, D);
    //}

    /**
     * 计算指定的点是否线上，并返回垂点坐标
     */
    public double PointToLineNearestDistance(Coordinate SelPoint, Param LineInnerPoint, Coordinate PerPoint)
    {
        LineInnerPoint.setValue(false);
        Coordinate PerPt = null;
        Coordinate a = this._StartPoint;
        Coordinate b = this._EndPoint;
        Coordinate c = SelPoint;

        Coordinate ab = this.aDb(b, a);
        Coordinate ac = this.aDb(c, a);
        double f = aXb(ab, ac);

        if (f <= 0) { PerPt = a; return Tools.GetTwoPointDistance(c, a,false); }
        double d = this.aXb(ab, ab);
        if (f > d) { PerPt = b; return Tools.GetTwoPointDistance(c, b,false); }
        f = f / d;
        Coordinate D = this.aAb(a, this.TXb(f, ab));   // c在ab线段上的投影点
        PerPt = D; LineInnerPoint.setValue(true);
        if (PerPoint==null)PerPoint = new Coordinate();
        PerPoint.setX(PerPt.getX());PerPoint.setY(PerPt.getY());
        return Tools.GetTwoPointDistance(c, D,false);
    }

    private Coordinate aDb(Coordinate A, Coordinate B)
    {
        return new Coordinate(A.getX() - B.getX(), A.getY() - B.getY());
    }
    private Coordinate aAb(Coordinate A, Coordinate B)
    {
        return new Coordinate(A.getX() + B.getX(), A.getY() + B.getY());
    }
    private double aXb(Coordinate A, Coordinate B)
    {
        return A.getX() * B.getX() + A.getY() * B.getY();
    }
    private Coordinate TXb(double T, Coordinate A)
    {
        return new Coordinate(T * A.getX(), T * A.getY());
    }

    //判断两条线段是否相交
    ////叉积
    //double mult(Coordinate a, Coordinate b, Coordinate c)
    //{
    //    return (a.X-c.X)*(b.Y-c.Y)-(b.X-c.X)*(a.Y-c.Y);
    //}
    public boolean Intersect(Line InLine)
    {
        Coordinate P1 = InLine._StartPoint; Coordinate P2 = InLine._EndPoint;
        Coordinate Q1 = this._StartPoint; Coordinate Q2 = this._EndPoint;

        //基于几何向量跨立试验
        //( P1 - Q1 ) × ( Q2 - Q1 ) * ( Q2 - Q1 ) × ( P2 - Q1 ) >= 0
        //( Q1 - P1 )×( P2 - P1) * ( P2 - P1)×(Q2 - P1) >= 0
        double P1Q1_X = (P1.getX() - Q1.getX()); double P1Q1_Y = (P1.getY() - Q1.getY());
        double Q2Q1_X = (Q2.getX() - Q1.getX()); double Q2Q1_Y = (Q2.getY() - Q1.getY());
        double P2Q1_X = (P2.getX() - Q1.getX()); double P2Q1_Y = (P2.getY() - Q1.getY());
        double P1Q1Q2Q1 = (P1Q1_X * Q2Q1_Y - P1Q1_Y * Q2Q1_X);
        double Q2Q1P2Q1 = (Q2Q1_X * P2Q1_Y - Q2Q1_Y * P2Q1_X);
        if (P1Q1Q2Q1 * Q2Q1P2Q1 < 0) return false;

        double Q1P1_X = Q1.getX() - P1.getX(); double Q1P1_Y = Q1.getY() - P1.getY();
        double P2P1_X = P2.getX() - P1.getX(); double P2P1_Y = P2.getY() - P1.getY();
        double Q2P1_X = Q2.getX() - P1.getX(); double Q2P1_Y = Q2.getY() - P1.getY();
        double Q1P1P2P1 = (Q1P1_X * P2P1_Y - Q1P1_Y * P2P1_X);
        double P2P1Q2P1 = (P2P1_X * Q2P1_Y - P2P1_Y * Q2P1_X);
        if (Q1P1P2P1 * P2P1Q2P1 < 0) return false;
        return true;

    }

    //判断两直线是否相交，相交则返回交点坐标，返回 0-不相交，1-相交，只有一个交点 2-相交，有两个交点
    public int Intersect(Line InLine,  Coordinate IP1, Coordinate IP2)
    {
        //判断两条直线段的外接矩形是否相交
        if (!this.getEnvelope().Intersect(InLine.getEnvelope())) return 0;

        //判断两线段是否相交
        if (!this.Intersect(InLine)) return 0;

        double x1,y1,x2,y2,x3,y3,x4,y4;
        x1 = this._StartPoint.getX();
        y1 = this._StartPoint.getY();
        x2 = this._EndPoint.getX();
        y2 = this._EndPoint.getY();
        x3 = InLine._StartPoint.getX();
        y3 = InLine._StartPoint.getY();
        x4 = InLine._EndPoint.getX();
        y4 = InLine._EndPoint.getY();

        //首先判断d   =   (y2-y1)(x4-x3)-(y4-y3)(x2-x1)，
        //若d=0，则直线AB与CD平行或重合，
        //若d!=0，则直线AB与CD有交点，设交点为E(x0,y0)：

        //1、判断两条直线是否平行，或重叠
        double D = (y2 - y1)*(x4 - x3) - (y4 - y3)*(x2 - x1);
        if (D == 0) //表示平行或重合
        {
            return 0;
        }

        //2、求交点
        double x0 = ((x2 - x1) * (x4 - x3) * (y3 - y1) + (y2 - y1) * (x4 - x3) * x1 - (y4 - y3) * (x2 - x1) * x3) / D;
        double y0 = ((y2 - y1) * (y4 - y3) * (x3 - x1) + (x2 - x1) * (y4 - y3) * y1 - (x4 - x3) * (y2 - y1) * y3) / (-D);

        //3、用判断交点是否在线上
        IP1.setX(x0);IP1.setY(y0);


        if (this.PointOnLine(IP1)) return 1; else return 0;



        //老算法，有问题

        ////判断两条直线段的位置情况
        //double x1, y1, xx1, yy1, x2, y2, xx2, yy2;
        //double n1, n2, n3, n4, k1, k2;
        //if (this._StartPoint.X>this._EndPoint.X)
        //{
        //    xx1=this._EndPoint.X;
        //    yy1=this._EndPoint.Y;
        //    xx2=this._StartPoint.X;
        //    yy2=this._StartPoint.Y;
        //} else
        //{
        //    xx1=this._StartPoint.X;
        //    yy1=this._StartPoint.Y;
        //    xx2=this._EndPoint.X;
        //    yy2=this._EndPoint.Y;
        //}

        //if (xx1==xx2 && yy1==yy2) return 0;   //此直线为一个点

        //if (InLine._StartPoint.X>InLine._EndPoint.X)
        //{
        //    x1=InLine._EndPoint.X;
        //    y1=InLine._EndPoint.Y;
        //    x2=InLine._StartPoint.X;
        //    y2=InLine._StartPoint.Y;
        //} else
        //{
        //    x1=InLine._StartPoint.X;
        //    y1=InLine._StartPoint.Y;
        //    x2=InLine._EndPoint.X;
        //    y2=InLine._EndPoint.Y;
        //}

        //if (x1==x2 && y1==y2) return 0;    //此直线为一个点

        ////1、两条直线段都是竖直线 ||
        //if ((x1 == x2) && (xx1 == xx2))
        //{
        //    if (x1 != xx1) return 0;   //两线段横坐标不同，则不相交
        //    IP1.X = (int)x1; IP2.X = (int)x1;
        //    n1 = Math.Min(yy1, yy2);
        //    n2 = Math.Max(yy1, yy2);
        //    n3 = Math.Min(y1, y2);
        //    n4 = Math.Max(y1, y2);

        //    if ((n1 > n4) || (n2 < n3)) return 0;//两线段纵坐标没有相交，而返回不相交
        //    else
        //    {
        //        if (n1 == n4 || n2 == n3)  //只有一点坐标相同
        //        {
        //            if (n1 == n4)
        //            {
        //                IP1.Y = (int)n1;
        //                return 1;
        //            }
        //            if (n2 == n3)
        //            {
        //                IP1.Y = (int)n2;
        //                return 1;
        //            }
        //        }
        //        else     //纵坐标有重叠
        //        {
        //            IP1.Y = (int)Math.Max(n1, n3);
        //            IP2.Y = (int)Math.Min(n2, n4);
        //            return 2;
        //        }
        //    }
        //}
        //else
        //{
        //    if ((x1==x2) && (xx1!=xx2))   //一条直线，一条斜线
        //    {
        //        IP1.X = (int)x1;
        //        if (!(x1>=xx1 && x1<=xx2)) return 0;    //不相交
        //        IP1.Y = (int)(yy1 + (x1 - xx1) / (xx2 - xx1) * (yy2 - yy1));
        //        if (IP1.Y>=y1 && IP1.Y<=y2 || IP1.Y>=y2 && IP1.Y<=y1)
        //        return 1;else return 0;

        //    }

        //    if ((x1!=x2) && (xx1==xx2))   //一条直线，一条斜线
        //    {
        //        IP1.X = (int)xx1;
        //        if (!(xx1>=x1 && xx1<=x2)) return 0;    //不相交
        //        IP1.Y = (int)(y1 + (y2 - y1) / (x2 - x1) * (xx1 - x1));
        //        if (!(IP1.Y>=yy1 && IP1.Y<=yy2 || IP1.Y>=yy2 && IP1.Y<=yy1))
        //        return 1;else return 0;
        //    }

        //}


        ////计算两条直线的斜率
        //k1 = (y2 - y1) / (x2 - x1);
        //k2 = (yy2 - yy1) / (xx2 - xx1);
        //if (k1 == k2)   //两直线平线
        //{
        //    if (x1 > xx2 || x2 < xx1)
        //        return 0;

        //    else
        //    {
        //        n1 = y1 + (0 - x1) * k1;
        //        n2 = yy1 + (0 - x1) * k2;
        //        if (n1 != n2)
        //            return 0;
        //        else
        //        {
        //            n3 = Math.Max(xx1, x1);
        //            n4 = Math.Min(xx2, x2);
        //            IP1.X = (int)n3;
        //            IP1.Y = (int)(y1 + (n2 - x1) * k1);
        //            if (n3 == n4)
        //                return 1;
        //            IP2.X = (int)n4;
        //            IP2.Y = (int)(y1 + (n4 - x1) * k1);
        //            return 2;
        //        }
        //    }
        //}
        //else
        //{
        //    IP1.X = (int)((yy1 - y1 + x1 * k1 - xx1 * k2) / (k1 - k2));
        //    IP1.Y = (int)(y1 + (IP1.X - x1) * k1);
        //    if ((IP1.X >= x1 && IP1.X <= x2) && (IP1.X >= xx1 && IP1.X <= xx2))
        //        return 1;
        //    else return 0;
        //}

//        return 0;

    }

    //判断指定点是否在线上
    public boolean PointOnLine(Coordinate Pt)
    {
        //if (!this.Envelope.ContainPoint(Pt)) return false;
        boolean InnerPoint = false;Coordinate PerPoint = new Coordinate();
        Param InnerPointT = new Param();
        PointToLineNearestDistance(Pt, InnerPointT, PerPoint);
        InnerPoint = InnerPointT.getBoolean();
        if (InnerPoint && Tools.GetTwoPointDistance(Pt, PerPoint) < 0.5) return true;
        return false;

        //1、第一种算法，向量
        //( Q - P1 ) × ( P2 - P1 ) = 0 且 Q 在以 P1，P2为对角顶点的矩形内。前者保证Q点在直线P1P2上，后者是保证Q点不在线段P1P2的延长线或反向延长线上


        //double QP1_X = Pt.X - this._StartPoint.X;
        //double QP1_Y = Pt.Y - this._StartPoint.Y;

        //double P2P1_X = this._EndPoint.X - this._StartPoint.X;
        //double P2P1_Y = this._EndPoint.Y - this._StartPoint.Y;

        //double QP1P2P1 = (QP1_X * P2P1_Y - QP1_Y * P2P1_X);
        //if (Math.Abs(QP1P2P1) <= 0.00001)
        //{
        //    //if min(xi,xj) <= xk <= max(xi,xj) and min(yi,yj) <= yk <= max(yi,yj)
        //    //判断是否在延长线上
        //    if ((Math.Min(this._StartPoint.X, this._EndPoint.X) <= Pt.X && Math.Max(this._StartPoint.X, this._EndPoint.X) >= Pt.X) &&
        //        (Math.Min(this._StartPoint.Y, this._EndPoint.Y) <= Pt.Y && Math.Max(this._StartPoint.Y, this._EndPoint.Y) >= Pt.Y))
        //        return true;
        //    else return false;
        //}
        //else return false;

        //2、第二种算法，判断垂直距离<=根号(2)
        //double X0 = Pt.X;
        //double Y0 = Pt.Y;
        //double X1 = this._StartPoint.X;
        //double Y1 = this._StartPoint.Y;
        //double X2 = this._EndPoint.X;
        //double Y2 = this._EndPoint.Y;

        ////S = ((x1-x0)*(y2-y0)-(x2-x0)*(y1-y0))/2 三点求面积向量表示
        //double S = ((X1 - X0) * (Y2 - Y0) - (X2 - X0) * (Y1 - Y0));
        //double PerH = S / Tools.GetTwoPointDistance(_StartPoint, _EndPoint);
        //if (PerH <= Math.Sqrt(2)) return true; else return false;


        //第三种算法，判断斜率
        ////判断是否在断点上
        //if (X0 == X1 || X0 == X2) return true;
        //double K1 = (Y1 - Y0) / (X1 - X0);
        //double K2 = (Y2-Y0)/(X2-X0);
        //double K = K1 - K2;
        //if (Math.Abs(K) <= 0.1) return true; else return false;
    }


    //计算距起点指定距离的坐标点
    public Coordinate GetToStartCoordinate(double ToStartDistance)
    {
        //定比分点公式：x=（x1+λx2）/（1+λ），y=（y1+λy2）/（1+λ）
        double X1 = _StartPoint.getX(); double Y1 = _StartPoint.getY();
        double X2 = _EndPoint.getX(); double Y2 = _EndPoint.getY();
        double S1 = ToStartDistance;
        double S2 = this.Length() - ToStartDistance;
        double S = S1 / S2;
        double X = (X1 + S * X2) / (1 + S);
        double Y = (Y1 + S * Y2) / (1 + S);
        return new Coordinate(X, Y);
    }

    //计算直线外

    public double Length()
    {
        return Tools.GetTwoPointDistance(_StartPoint, _EndPoint);
    }
}
