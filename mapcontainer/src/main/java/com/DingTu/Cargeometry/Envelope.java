package com.DingTu.Cargeometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Envelope {
    public Envelope(Coordinate lefttop, Coordinate rightbottom)
    {
        _LeftTop = lefttop;
        _RightBottom = rightbottom;
    }
    public Envelope(double LeftTopX, double LeftTopY, double RightBottomX, double RightBottomY)
    {
        _LeftTop = new Coordinate(LeftTopX, LeftTopY);
        _RightBottom = new Coordinate(RightBottomX, RightBottomY);
    }


    //左上角坐标
    private Coordinate _LeftTop = null;
    public Coordinate getLeftTop()
    {
        return _LeftTop;
    }
    public void setLeftTop(Coordinate coor)
    {
        _LeftTop = coor;
    }


    //右下角坐标
    private Coordinate _RightBottom = null;
    public Coordinate getRightBottom()
    {
        return _RightBottom;
    }
    public void setRightBottom(Coordinate coor)
    {
        _RightBottom=coor;
    }

    //Envelope的类型，True正向矩形，也就是从左向右画的。False反向矩形，也就是从右向左画的。
    private boolean _Type = true;
    public boolean getType()
    {
        return _Type;
    }
    public void setType(boolean type)
    {
        _Type=type;
    }

    //最小X坐标
    public double getMinX()
    {
        return _LeftTop.getX();
    }

    //最小Y坐标
    public double getMinY()
    {
        return _RightBottom.getY();
    }

    //最大X坐标
    public double getMaxX()
    {
        return _RightBottom.getX();
    }

    //最大Y坐标
    public double getMaxY()
    {
        return _LeftTop.getY();
    }

    //宽度
    public double getHeight()
    {
        return getMaxY() - getMinY();
    }

    //高度
    public double getWidth()
    {
        return getMaxX() - getMinX();
    }

    //矩形中心点坐标
    public Coordinate getCenter()
    {
        return new Coordinate((_RightBottom.getX() + _LeftTop.getX()) / 2, (_LeftTop.getY() + _RightBottom.getY()) / 2);
    }
    public void setCenter(Coordinate Coor)
    {
        Coordinate cd = this.getCenter();
        Coordinate to = Coor;
        _LeftTop.setX(_LeftTop.getX()+(to.getX()-cd.getX()));
        _LeftTop.setY(_LeftTop.getY()+(to.getY()-cd.getY()));

        _RightBottom.setX(_RightBottom.getX()+(to.getX()-cd.getX()));
        _RightBottom.setY(_RightBottom.getY()+(to.getY()-cd.getY()));

    }

    //判断是否为0边矩形
    public boolean IsZero()
    {
        double MapDis = 0.00000001;

        if ((this.getWidth() <= MapDis || this.getHeight() <= MapDis) && Math.abs(this.getLeftTop().getX())<=MapDis) return true; else return false;

    }

    //修正矩形，使之成为正方形，以最大边长为基础边
    public Envelope ExtendEnvelope()
    {
        if (this.getWidth() > this.getHeight())
        {
            double OffsetH = (this.getWidth() - this.getHeight()) / 2;
            //this.LeftTop.Y += OffsetH;
            //this.RightBottom.Y -= OffsetH;
            return new Envelope(this.getLeftTop().getX(), this.getLeftTop().getY() + OffsetH, this.getRightBottom().getX(), this.getRightBottom().getY() - OffsetH);
        }
        else
        {
            double OffsetW = (this.getHeight() - this.getWidth()) / 2;
            //this.LeftTop.X -= OffsetW;
            //this.RightBottom.X += OffsetW;
            return new Envelope(this.getLeftTop().getX() - OffsetW, this.getLeftTop().getY(), this.getRightBottom().getX() + OffsetW, this.getRightBottom().getY());
        }
    }

    //合并两矩形,desEnvelop=被合并矩形
    public Envelope Merge(Envelope desEnvelop)
    {
        if (desEnvelop.IsZero()) return this;
        if (this.Contains(desEnvelop)) return new Envelope(this.getLeftTop().Clone(), this.getRightBottom().Clone());
        if (desEnvelop.Contains(this)) return new Envelope(desEnvelop.getLeftTop().Clone(), desEnvelop.getRightBottom().Clone());

        double TwoEnveMinx = Math.min(this.getMinX(), desEnvelop.getMinX());
        double TwoEnveMinY = Math.min(this.getMinY(), desEnvelop.getMinY());
        double TwoEnveMaxX = Math.max(this.getMaxX(), desEnvelop.getMaxX());
        double TwoEnveMaxY = Math.max(this.getMaxY(), desEnvelop.getMaxY());
        return new Envelope(TwoEnveMinx, TwoEnveMaxY, TwoEnveMaxX, TwoEnveMinY);
    }

    //矩形倍数,"desEnvelop"=小矩形
    public double Factor(Envelope desEnvelop)
    {
        if (desEnvelop.getWidth() <= 0 || desEnvelop.getHeight() <= 0) return 1;
        double WidthScale = this.getWidth() / desEnvelop.getWidth();
        double HeightScale = this.getHeight() / desEnvelop.getHeight();
        if (WidthScale < HeightScale) return HeightScale;
        else return WidthScale;
    }

    //按指定的缩放比例缩放矩形
    public Envelope Scale(double Factor)
    {
        Coordinate CenterPT = this.getCenter();
        return new Envelope((CenterPT.getX() - (this.getWidth()/2) * Factor), (CenterPT.getY() + (this.getHeight()/2) * Factor),
                (CenterPT.getX() + (this.getWidth() / 2) * Factor), (CenterPT.getY() - (this.getHeight() / 2) * Factor));
    }

    //向外伸展指定米数
    public void ExtendTo(double distance)
    {
        this._LeftTop.setX(this._LeftTop.getX()-distance);
        this._LeftTop.setY(this._LeftTop.getY()+distance);
        this._RightBottom.setX(this._RightBottom.getX()+distance);
        this._RightBottom.setY(this._RightBottom.getY()-distance);
    }

    //判断两个矩形是否相交，完全包含关系也属于相交情况
    public boolean Intersect(Envelope desEnvelop)
    {
        double TwoEnveMinX = Math.max(this.getMinX(), desEnvelop.getMinX());
        double TwoEnveMinY = Math.max(this.getMinY(), desEnvelop.getMinY());
        double TwoEnveMaxX = Math.min(this.getMaxX(), desEnvelop.getMaxX());
        double TwoEnveMaxY = Math.min(this.getMaxY(), desEnvelop.getMaxY());
        if (TwoEnveMinX > TwoEnveMaxX || TwoEnveMinY > TwoEnveMaxY)
        {
            return this.Contains(desEnvelop);
        }
        else
        {
            return true;
        }

    }

    //返回两矩形的相交区域，如果不相交则返回空
    public Envelope GetIntersectEnvelope(Envelope desEnvelop)
    {
        double TwoEnveMinx = Math.max(this.getMinX(), desEnvelop.getMinX());
        double TwoEnveMinY = Math.max(this.getMinY(), desEnvelop.getMinY());
        double TwoEnveMaxX = Math.min(this.getMaxX(), desEnvelop.getMaxX());
        double TwoEnveMaxY = Math.min(this.getMaxY(), desEnvelop.getMaxY());
        if (TwoEnveMinx > TwoEnveMaxX || TwoEnveMinY > TwoEnveMaxY)
        {
            return null;
        }
        else
        {
            return new Envelope(TwoEnveMinx, TwoEnveMaxY, TwoEnveMaxX, TwoEnveMinY);
        }
    }

    //计算两个矩形是否属于完全包含关系,innerEnvelope为被包含矩形（在内部）
    public boolean Contains(Envelope innerEnvelope)
    {
        if (!(innerEnvelope.getMinX() >= this.getMinX() && innerEnvelope.getMinX() <= this.getMaxX())) return false;
        if (!(innerEnvelope.getMaxX() >= this.getMinX() && innerEnvelope.getMaxX() <= this.getMaxX())) return false;
        if (!(innerEnvelope.getMinY() >= this.getMinY() && innerEnvelope.getMinY() <= this.getMaxY())) return false;
        if (!(innerEnvelope.getMaxY() >= this.getMinY() && innerEnvelope.getMaxY() <= this.getMaxY())) return false;
        return true;
    }

    //判断指定点是否包含在矩形内部
    public boolean ContainsPoint(Coordinate Pt)
    {
        return this.ContainsPoint(Pt.getX(), Pt.getY());
    }
    public boolean ContainsPoint(double X, double Y)
    {
        if (X >= this.getMinX() && X <= this.getMaxX())
        {
            if (Y >= this.getMinY() && Y <= this.getMaxY())
            {
                return true;
            }
        }
        return false;
    }

    //将矩形转换成线实体
    public Polyline ConvertToPolyline()
    {
        Polyline PL = new Polyline();
        List<Coordinate> CoorList = new ArrayList<Coordinate>();
        CoorList.add(new Coordinate(this.getMinX(), this.getMinY()));
        CoorList.add(this.getLeftTop());
        CoorList.add(new Coordinate(this.getMaxX(), this.getMaxY()));
        CoorList.add(this.getRightBottom());
        CoorList.add(new Coordinate(this.getMinX(), this.getMinY()));
        PL.AddPart(new Part(CoorList));
        return PL;
    }

    //克隆
    public Envelope Clone()
    {
        return new Envelope(this.getLeftTop().Clone(), this.getRightBottom().Clone());
    }

    //是否相同
    public boolean Equal(Envelope pEnvelope)
    {
        return (this.getLeftTop().Equal(pEnvelope.getLeftTop()) && this.getRightBottom().Equal(pEnvelope.getRightBottom()));
    }
}
