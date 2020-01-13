package com.DingTu.CoordinateSystem;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Envelope;
import com.DingTu.Cargeometry.Size;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class ViewConvert {

    //Map的当前视图外接矩形，单位（米），即Map.Extend
    private Envelope _Extend;
    public Envelope getExtend()
    {
        if (_Center == null) return null;
        return _Extend;
    }
    public void setExtend(Envelope value)
    {
        Envelope pEnv = value;
        if (pEnv == null) return;

        _Center = pEnv.getCenter();
        if (pEnv.getHeight()>0)this.setZoom((double)pEnv.getHeight());
        //double ExtendHeight = (_Center.Y - MapHeight * 0.5) - (_Center.Y + MapHeight * 0.5);
        //Zoom *= Math.Abs((double)pEnv.Height / ExtendHeight);
        //更新当前视图外接矩形
        this.CalExtend();
    }


    public Envelope CalExtend()
    {
        //0.05目的是四舍五入
        return _Extend = new Envelope((_Center.getX() - getMapWidth() * 0.5), (_Center.getY() + getMapHeight() * 0.5),
                (_Center.getX() + getMapWidth() * 0.5), (_Center.getY() - getMapHeight() * 0.5));
    }

    //Map的最大视图外接矩形，也就是全屏范围，单位（米），即Map.FullExtend;
    private Envelope _FullExtend;
    public Envelope getFullExtend()
    {
        return _FullExtend;
    }
    public void setFullExtend(Envelope value)
    {
        _FullExtend = value;
        this.setZoom(_FullExtend.getHeight());
        //Zoom *= (double)_Size.Width / (double)_Size.Height;
        this.setCenter(_FullExtend.getCenter());
    }

    //实际单位表示的Map的宽度
    private double getMapWidth()
    {
        return this.getZoomScale() * _Size.getWidth();
    }

    //实际单位表示的Map的高度
    private double getMapHeight()
    {
        //return (MapWidth / _Size.Width) * _Size.Height;
        return _Zoom;
    }

    //以像素为单位的Map尺寸，也就是MapControl控件的尺寸
    private Size _Size;
    public Size getSize()
    {
        return _Size;
    }
    public void setSize(Size value)
    {
        _Size = value;
        _ZoomScale = _Zoom / (double)_Size.getHeight();
        _ScaleZoom = 1/_ZoomScale;
        if (_Size.getWidth() == 0 || _Size.getHeight() == 0) return;
    }

    //Map.Extend的实际单位（米）中心坐标，即Map.Center
    private Coordinate _Center;
    public Coordinate getCenter()
    {
        return _Center;
    }
    public void setCenter(Coordinate value)
    {
        _Center = value;
    }

    //Map缩放比例Zoom，默认值为整个Map的实际单位(米)代表的高度
    private double _Zoom;
    public double getZoom()
    {
        return _Zoom;
    }
    public void setZoom(double value)
    {
        _Zoom = value;
        int d = (_Size.getHeight());
        _ZoomScale = _Zoom / (double)d;
        _ScaleZoom = 1/_ZoomScale;
    }

    //中间变量值，为坐标转换做准备，实际意义就是每个像素代表的高度
    private double _ZoomScale;
    private double _ScaleZoom;
    public double getZoomScale()
    {
        return _ZoomScale;
    }

    //将Map坐标转换成绘图坐标（MapToScreen）
    public android.graphics.Point MapToScreen(Coordinate MapCoordinate)
    {
        return this.MapToScreen(MapCoordinate.getX(), MapCoordinate.getY());
    }

    public android.graphics.Point MapToScreen(double X, double Y)
    {
        android.graphics.PointF PF = this.MapToScreenF(X,Y);
        return new android.graphics.Point((int)PF.x, (int)PF.y);
    }

    public android.graphics.PointF MapToScreenF(double X, double Y)
    {
//        double left = _Center.getX() - this.getMapWidth() * 0.5;
//        double top = _Center.getY() + this.getMapHeight() * 0.5;
        double left = this.getExtend().getMinX();
        double top = this.getExtend().getMaxY();


        float X1 = (float)((X - left) * (this._ScaleZoom));
        float Y1 = (float)((top - Y) * (this._ScaleZoom));
        return new android.graphics.PointF(X1, Y1);
    }



    //批量将Map点坐标转成屏幕点坐标数组，CompressMPoint:是否压缩相同像素点,deltX,deltY偏移量
    public android.graphics.Point[] MapPointsToScreePoints(List<Coordinate> _MapCoorList,
                                                           boolean CompressMPoint/*是否压缩相同像素点*/,
                                                           int deltPixX, int deltPixY)
    {
        //System.Drawing.Point[] OPFList = new System.Drawing.Point[_MapCoorList.Count];
        List<android.graphics.Point> OPFList = new ArrayList<Point>();

        int ScreenX = 0, ScreenY = 0;
        for (int i = 0; i < _MapCoorList.size(); i++)
        {
            android.graphics.Point agp = this.MapToScreen(_MapCoorList.get(i));
            ScreenX = agp.x;ScreenY=agp.y;
            if (CompressMPoint)
            {
                int pCount = OPFList.size();
                if (pCount > 0)
                {
                    if ((OPFList.get(pCount - 1).x == ScreenX && OPFList.get(pCount - 1).y == ScreenY)) continue;
                }
            }

            OPFList.add(new android.graphics.Point(ScreenX + deltPixX, ScreenY + deltPixY));
        }
        return (Point[]) OPFList.toArray(new Point[0]);
    }
    public android.graphics.Point[] MapPointsToScreePoints(List<Coordinate> _MapCoorList)
    {
        return MapPointsToScreePoints(_MapCoorList, true,0, 0);
    }
    public android.graphics.Point[] MapPointsToScreePoints(List<Coordinate> _MapCoorList, boolean CompressMPoint)
    {
        return MapPointsToScreePoints(_MapCoorList, CompressMPoint, 0, 0);
    }

    //将绘图坐标转换成Map坐标 (ScreenToMap)
    //将绘图坐标转换成Map坐标
    public Coordinate ScreenToMap(android.graphics.Point ScreenCoor)
    {
        return this.ScreenToMap(ScreenCoor.x, ScreenCoor.y);
    }
    public Coordinate ScreenToMap(android.graphics.PointF ScreenCoor)
    {
        return this.ScreenToMap(ScreenCoor.x, ScreenCoor.y);
    }

    public Coordinate ScreenToMap(int CoorX, int CoorY)
    {
        return ScreenToMap((float)CoorX,(float)CoorY);
    }
    public Coordinate ScreenToMap(float CoorX, float CoorY)
    {
        if (this.getExtend()== null) return null;
        double MapX = this.getExtend().getMinX() + CoorX * (this.getZoomScale());
        double MapY = this.getExtend().getMaxY() - CoorY * (this.getZoomScale());
        return new Coordinate(MapX, MapY);
    }


    //开窗裁剪 (Clipline[线段的开窗裁剪]、ClipPolygon[面的开窗裁剪])
    //1、线段的开窗裁剪
    //    //先用开窗裁切的算法去掉视窗之外的点
    //    //  1001|       1000    |    1010
    //    //______|_______________|________
    //    //      |               |
    //    //  0001|       0000    |    0010
    //    //______|_______________|________
    //    //      |               |
    //    //  0101|       0100    |    0110

    public android.graphics.Point[] ClipPolyline(List<Coordinate> _MapCoorList, int deltX, int deltY)
    {
        //在此进行截切操作，目的是不画屏幕之外的部分，作用是为动态标注做准备
        List<android.graphics.Point> m_DrawPoint = new ArrayList<android.graphics.Point>();
        int PointInView = -1;  //-1:状态未知，0:点第一次在视图内部，1:点在视图外部
        int PointCount = _MapCoorList.size();//, ClipType = 0;
        Coordinate Pt11 = new Coordinate();
        Coordinate Pt22 = new Coordinate();
        android.graphics.Point Pt1, Pt2, Pt00 = null;
        for (int i = 0; i < PointCount - 1; i++)
        {
            if (this.Clipline(_MapCoorList.get(i), _MapCoorList.get(i + 1),  Pt11,  Pt22))
            {
                Pt1 = this.MapToScreen(Pt11); Pt1.x += deltX; Pt1.y += deltY;
                Pt2 = this.MapToScreen(Pt22); Pt2.x += deltX; Pt2.y += deltY;
                if (Pt00==null) Pt00 = Pt2;

                if (Pt1.x == Pt00.x && Pt1.y == Pt00.y) m_DrawPoint.add(Pt2);
                else { m_DrawPoint.add(Pt1); m_DrawPoint.add(Pt2); }
                Pt00 = Pt2;

                //解决由内穿向外，再由外穿向了内部，从而产生的折角情况

                if (PointInView == 1)   //由内穿向外，再由外穿向了内部
                {
                    if (m_DrawPoint.size() >= 3)
                    {
                        //判断最后两点的状态，以确定是否需要加入角点来抹去折角
                        android.graphics.Point P1 = m_DrawPoint.get(m_DrawPoint.size()-2);
                        android.graphics.Point P2 = m_DrawPoint.get(m_DrawPoint.size()-3);
                        if (P1.x == P2.x || P1.y == P2.y) { }  //平行情况，不需要处理
                        else
                        {
                            Coordinate CP1 = this.ScreenToMap(P1);
                            Coordinate CP2 = this.ScreenToMap(P2);
                            Envelope P1P2Enve = new Envelope(Math.min(CP1.getX(),CP2.getX()),Math.max(CP1.getY(),CP2.getY()),Math.max(CP1.getX(),CP2.getX()),Math.min(CP1.getY(),CP2.getY()));

                            double newX = -1, newY = -1;
                            Coordinate CPt00 = this.ScreenToMap(new android.graphics.Point(0,0));
                            Coordinate CPtW0 = this.ScreenToMap(new android.graphics.Point(this.getSize().getWidth(),0));
                            Coordinate CPtWH = this.ScreenToMap(new android.graphics.Point(this.getSize().getWidth(), this.getSize().getHeight()));
                            Coordinate CPt0H = this.ScreenToMap(new android.graphics.Point(0, this.getSize().getHeight()));
                            if (P1P2Enve.ContainsPoint(CPt00))
                            {
                                newX = this.getClipExtendMinX(); newY = this.getClipExtendMaxY();
                                m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(new Coordinate(newX, newY)));

                            }
                            if (P1P2Enve.ContainsPoint(CPtW0))
                            {
                                newX = this.getClipExtendMaxX(); newY = this.getClipExtendMaxY();
                                m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(new Coordinate(newX, newY)));

                            }
                            if (P1P2Enve.ContainsPoint(CPtWH))
                            {
                                newX = this.getClipExtendMaxX(); newY = this.getClipExtendMinY();
                                m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(new Coordinate(newX, newY)));

                            }
                            if (P1P2Enve.ContainsPoint(CPt0H))
                            {
                                newX = this.getClipExtendMinX(); newY = this.getClipExtendMinY();
                                m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(new Coordinate(newX, newY)));

                            }
                            if (newX == -1 || newY == -1)
                            {
                                //处理特殊情况，就是环路
                                if (Math.abs(CP1.getY() - CP2.getY()) >= Math.abs(CPt0H.getY() - CPt00.getY()))
                                {
                                    CPt00.setX(CPt00.getX()-this.getZoomScale() * 10);
                                    CPt00.setY(CPt00.getY()+ this.getZoomScale() * 10);
                                    CPt0H.setX(CPt0H.getX()-this.getZoomScale() * 10);
                                    CPt0H.setY(CPt0H.getY()-this.getZoomScale() * 10);
                                    if ((CP1.getY() - CP2.getY()) < 0)
                                    {
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt00));
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt0H));
                                    }
                                    else
                                    {
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt0H));
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt00));
                                    }

                                }
                                else
                                {
                                    CPt00.setX(CPt00.getX()- this.getZoomScale() * 10);
                                    CPt00.setY(CPt00.getY()+ this.getZoomScale() * 10);
                                    CPtW0.setX(CPtW0.getX()+ this.getZoomScale() * 10);
                                    CPtW0.setY(CPtW0.getY()+ this.getZoomScale() * 10);
                                    if ((CP1.getX() - CP2.getX()) > 0)
                                    {
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt00));
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPtW0));
                                    }
                                    else
                                    {
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPtW0));
                                        m_DrawPoint.add(m_DrawPoint.size() - 2, this.MapToScreen(CPt00));
                                    }


                                }
                            }
                        }
                    }
                }

                PointInView = 0;
            }
            else
            {
                if (PointInView == 0) PointInView = 1;
            }
        }
        return (Point[]) m_DrawPoint.toArray(new Point[0]);
    }
    /// <summary> 求指定直线段是否需要在当前视窗内显示 [Cohen－Sutherland算法]
    /// </summary>
    /// <param name="Pt1"></param>
    /// <param name="Pt2"></param>
    /// <param name="Pt11"></param>
    /// <param name="Pt12"></param>
    /// <returns>true 显示线段 false 不显示线段</returns>
    private double getClipExtendMinX() { return this.getExtend().getMinX() - this.getZoomScale() * 10; }
    private double getClipExtendMinY() { return this.getExtend().getMinY() - this.getZoomScale() * 10; }
    private double getClipExtendMaxX() { return this.getExtend().getMaxX() + this.getZoomScale() * 10; }
    private double getClipExtendMaxY() { return this.getExtend().getMaxY() + this.getZoomScale() * 10; }

    public boolean Clipline(Coordinate Pt1, Coordinate Pt2,  Coordinate Pt11, Coordinate Pt22)
    {
        double X1 = Pt1.getX(), Y1 = Pt1.getY(), X2 = Pt2.getX(), Y2 = Pt2.getY();
        double X, Y;
        boolean Chp = false;
        while (true)
        {
            int C1 = this.Codec(X1, Y1);
            int C2 = this.Codec(X2, Y2);
            if (C1 == 0 && C2 == 0)          //全部在视窗内部
            {
                if (Chp) { double t; t = X1; X1 = X2; X2 = t; t = Y1; Y1 = Y2; Y2 = t; }
                Pt11.setX(X1);Pt11.setY(Y1);
                Pt22.setX(X2);Pt22.setY(Y2);
                return true;
            }

            if ((C1 & C2) != 0)    //全部在视窗外部
            {
                return false;
            }

            //为了规范算法，令线段的端点Pt1为外端点，如果不是这样，就需要Pt1和Pt2交换端点。
            //判断Pt1与Pt2是否需要交换位置
            if (C1 == 0) { Chp = true; double t; t = X1; X1 = X2; X2 = t; t = Y1; Y1 = Y2; Y2 = t; }

            if ((C1 & 0x01) != 0) //左边界
            {
                X = this.getClipExtendMinX();// this._Extend.MinX;
                Y = ((X - X1) / (X2 - X1)) * (Y2 - Y1) + Y1;
                X1 = X; Y1 = Y; continue;
            }
            if ((C1 & 0x02) != 0) //右边界
            {
                X = this.getClipExtendMaxX();// this._Extend.MaxX;
                Y = ((X - X1) / (X2 - X1)) * (Y2 - Y1) + Y1;
                X1 = X; Y1 = Y; continue;
            }
            if ((C1 & 0x04) != 0) //下边界
            {
                Y = this.getClipExtendMinY();// this._Extend.MinY;
                X = ((Y - Y1) / (Y2 - Y1)) * (X2 - X1) + X1;
                X1 = X; Y1 = Y; continue;
            }
            if ((C1 & 0x08) != 0) //上边界
            {
                Y = this.getClipExtendMaxY();// this._Extend.MaxY;
                X = ((Y - Y1) / (Y2 - Y1)) * (X2 - X1) + X1;
                X1 = X; Y1 = Y; continue;
            }
        }
    }

    /// <summary> 给指定点的坐标编码
    /// 四位二制数：第一位置1：区域在左边界外侧、第二位置1：区域在右边界外侧
    ///             第三位置1：区域在下边界外侧、第四位置1：区域在上边界外侧
    /// </summary>
    /// <param name="Pt"></param>
    /// <returns></returns>
    private int Codec(Coordinate Pt)
    {
        return Codec(Pt.getX(), Pt.getY());
    }
    private int Codec(double X, double Y)
    {
        int c = 0;
        //if (Y > Extend.MaxY) 　　//上边界 （xmin，ymin）和（xmax，ymax）为窗口左下角、右上角坐标。
        //    c = c | 0x08;
        //else if (Y < Extend.MinY)   //下边界
        //    c = c | 0x04;
        //if (X > Extend.MaxX)   //右边界
        //    c = c | 0x02;
        //else if (X < Extend.MinX)  //左边界
        //    c = c | 0x01;

        if (Y > this.getClipExtendMaxY())  //上边界 （xmin，ymin）和（xmax，ymax）为窗口左下角、右上角坐标。
            c = c | 0x08;
        else if (Y < this.getClipExtendMinY())   //下边界
            c = c | 0x04;
        if (X > this.getClipExtendMaxX())   //右边界
            c = c | 0x02;
        else if (X < this.getClipExtendMinX())  //左边界
            c = c | 0x01;
        return c;
    }


    //2、面的开窗裁剪
    /// <summary>面的开窗裁剪
    /// </summary>
    /// <param name="_MapCoorList"></param>
    /// <param name="deltX"></param>
    /// <param name="deltY"></param>
    /// <returns></returns>
    public android.graphics.Point[] ClipPolygon(List<Coordinate> _MapCoorList)
    {
        return this.ClipPolygon(_MapCoorList, 0, 0);
    }
    public android.graphics.Point[] ClipPolygon(List<Coordinate> _MapCoorList, int deltX, int deltY)
    {
        //Sutherland-Hodgman算法也叫逐边裁剪法
        List<Coordinate> LSide = this.ClipSide(_MapCoorList, 1);  //左边
        List<Coordinate> TSide = this.ClipSide(LSide, 2);         //上边
        List<Coordinate> RSide = this.ClipSide(TSide, 3);         //右边
        List<Coordinate> BSide = this.ClipSide(RSide, 4);         //下边

        LSide.clear(); LSide = null; TSide.clear(); TSide = null; RSide.clear(); RSide = null;

        android.graphics.Point[] OPF = new android.graphics.Point[BSide.size()];
        int j = 0;

        for (Coordinate Pt : BSide)
        {
            android.graphics.Point adp = this.MapToScreen(Pt);
            OPF[j++] = new android.graphics.Point(adp.x + deltX, adp.y + deltY);
        }
        BSide.clear(); BSide = null;
        return OPF;
    }

    /// <summary>用指定的边切指定的坐标串
    /// </summary>
    /// <param name="CoorList"></param>
    /// <param name="WhichSide"></param>
    /// <returns></returns>
    private List<Coordinate> ClipSide(List<Coordinate> CoorList, int WhichSide)
    {
        int Flag = 0;   //前一点是否在待切边的内侧(0-内侧，1-外侧)
        List<Coordinate> SideList = new ArrayList<Coordinate>();
        if (CoorList.size() == 0) return SideList;

        //最后一点即是第一个点的前一点，计算最后一个点是否在内侧
        Coordinate S = CoorList.get(CoorList.size() - 1);   //前一点
        Flag = this.InnerSide(S, WhichSide);
        for (Coordinate P : CoorList)
        {
            if (this.InnerSide(P, WhichSide) == 0)  //在内侧
            {
                if (Flag == 1)   //前一个点在外侧
                {
                    Flag = 0;  /*从外到内的情况，将标志置0,作为下一次循环的前一点标志*/
                    SideList.add(this.GetSideIntersect(P, S, WhichSide));
                }
                SideList.add(P);
            }
            else
            {
                if (Flag == 0)  /*前一个点在内侧吗*/
                {
                    Flag = 1;    /*从内到外的情况，将标志置1,作为下一次循环的前一点标志*/
                    SideList.add(this.GetSideIntersect(P, S, WhichSide));
                }
            }
            S = P;
        }

        return SideList;
    }

    /// <summary>求线段与指定边的直线的交点
    /// </summary>
    /// <param name="S"></param>
    /// <param name="P"></param>
    /// <param name="WhichSide"></param>
    /// <returns></returns>
    private Coordinate GetSideIntersect(Coordinate S, Coordinate P, int WhichSide)
    {
        double X1 = S.getX(), Y1 = S.getY(), X2 = P.getX(), Y2 = P.getY();
        double X = 0, Y = 0;
        switch (WhichSide)
        {
            case 1:   //左边
                X = this.getClipExtendMinX();// this.Extend.MinX;
                Y = ((X - X1) / (X2 - X1)) * (Y2 - Y1) + Y1;
                break;
            case 2:   //上边
                Y = this.getClipExtendMaxY();  //this.Extend.MaxY;
                X = ((Y - Y1) / (Y2 - Y1)) * (X2 - X1) + X1;
                break;
            case 3:   //右边
                X = this.getClipExtendMaxX();// this.Extend.MaxX;
                Y = ((X - X1) / (X2 - X1)) * (Y2 - Y1) + Y1;
                break;
            case 4:   //下边
                Y = this.getClipExtendMinY();// this.Extend.MinY;
                X = ((Y - Y1) / (Y2 - Y1)) * (X2 - X1) + X1;
                break;
        }
        return new Coordinate(X, Y);


    }

    /// <summary>判断指定点在指定边的哪一侧[当前视口](0-内侧，1-外侧)
    /// </summary>
    /// <param name="Pt"></param>
    /// <param name="WhichSide">1-左边，2-上边，3-右边，4-下边</param>
    /// <returns></returns>
    private int InnerSide(Coordinate Pt, int WhichSide)
    {
        switch (WhichSide)
        {
            case 1:   //左边
                if (Pt.getX() >= this.getExtend().getMinX()) return 0; else return 1;
            case 2:   //上边
                if (Pt.getY() <= this.getExtend().getMaxY()) return 0; else return 1;
            case 3:   //右边
                if (Pt.getX() <= this.getExtend().getMaxX()) return 0; else return 1;
            case 4:   //下边
                if (Pt.getY() >= this.getExtend().getMinY()) return 0; else return 1;
        }
        return -1;
    }

    /// <summary>判断是否在当前视窗内部
    /// </summary>
    /// <param name="ptCoor"></param>
    /// <returns></returns>
    public boolean InViewExtend(Coordinate ptCoor)
    {
        if (ptCoor.getX() >= this.getExtend().getMinX() && ptCoor.getX() <= this.getExtend().getMaxX())
            if (ptCoor.getY() >= this.getExtend().getMinY() && ptCoor.getY() <= this.getExtend().getMaxY())
            {
                return true;
            }
        return false;
    }
}
