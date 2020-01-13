package com.DingTu.Cargeometry;

/**
 * Created by Dingtu2 on 2017/5/31.
 */
import java.text.DecimalFormat;

public class Coordinate {

    double _x;double _y;double _z;
    public Coordinate(double x, double y)
    {
        this._x = x; this._y = y; this._z = 0;
    }
    public Coordinate(double x, double y,double z)
    {
        this._x = x; this._y = y; this._z = z;
    }

    public Coordinate()
    {
    }

    public double getX()
    {
        return this._x;
    }
    public void setX(double x)
    {
        this._x=x;
    }

    public double getY()
    {
        return this._y;
    }
    public void setY(double y)
    {
        this._y=y;
    }


//    /**
//     * 设置地理坐标（经度）
//     * @param L
//     */
//    public void setGeoX(double L) {this._geoX = L;}
//    public double getGeoX() {return this._geoX;}
//
//    /**
//     * 设置地理坐标（纬度）
//     * @param B
//     */
//    public void setGeoY(double B) {this._geoY = B;}
//    public double getGeoY() {return this._geoY;}

    /**
     * 设置大地高
     * @param z
     */
    public void setZ(double z){this._z = z;}
    public double getZ(){return this._z;}


    public String ToString()
    {
        DecimalFormat df1 = new DecimalFormat("0.000");
        return df1.format(_x)+","+df1.format(_y);
    }

    public Coordinate Clone()
    {
        Coordinate newCoor = new Coordinate(this._x, this._y,this._z);
        //newCoor.setGeoX(this.getGeoX());newCoor.setGeoY(this.getGeoY());
        return newCoor;
    }

    //判断两坐标点是否相同
    public boolean Equal(Coordinate Coor)
    {
        double T = 0.00000001;
        if (Math.abs(this._x - Coor.getX())<T &&
                Math.abs(this._y - Coor.getY())<T)
            return true; else return false;
    }

    public boolean IsValid()
    {
        if (this._x <0 && this._y <0) return true;
        else {
            return false;
        }
    }
}
