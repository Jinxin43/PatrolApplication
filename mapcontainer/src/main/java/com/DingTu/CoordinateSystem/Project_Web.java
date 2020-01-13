package com.DingTu.CoordinateSystem;

import com.DingTu.Cargeometry.Coordinate;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Project_Web {

    //投影圆球坐标系（Web Google)
    public static Coordinate Web_BLToXY(double L1, double B1)
    {
        //lon:经度,lat：纬度
        double originShift = 2 * Math.PI * 6378137 / 2.0;
        double X = L1 * originShift / 180.0;
        double Y = Math.log(Math.tan((90 + B1) * Math.PI / 360.0)) / (Math.PI / 180.0);
        Y = Y * originShift / 180.0;
        return new Coordinate(X,Y);
    }

    public static Coordinate Web_XYToBL(double X, double Y)
    {
        //lon:经度,lat：纬度
        double originShift = 2 * Math.PI * 6378137 / 2.0;
        double L = X * 180.0 / originShift;
        double y = Y * 180 / originShift;
        double B = Math.atan(Math.exp(y * (Math.PI / 180.0))) / (Math.PI / 360.0) - 90;
        return new Coordinate(L,B);
    }
}
