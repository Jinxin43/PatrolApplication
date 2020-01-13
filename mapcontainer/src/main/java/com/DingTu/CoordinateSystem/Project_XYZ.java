package com.DingTu.CoordinateSystem;

import com.DingTu.Cargeometry.Coordinate;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Project_XYZ {

    /**
     * 经纬度转空间直角坐标
     * @param L 经度
     * @param B 纬度
     * @param H 大地高
     * @param coorSystem 坐标系统
     * @return
     */
    public static Coordinate XYZ_BLHToXYZ(double L,double B,double H,CoorSystem coorSystem)
    {
        double hd = Math.PI / 180;
        double a = coorSystem.GetA();
        double b = coorSystem.GetB();

        double e2 = (a * a - b * b) / (a * a);
        double w = Math.sqrt(1 - e2 * Math.sin(B * hd) * Math.sin(B * hd));
        double n = a / w;

        double X = (n + H) * Math.cos(B * hd) * Math.cos(L * hd);
        double Y = (n + H) * Math.cos(B * hd) * Math.sin(L * hd);
        double Z = (n * (1 - e2) + H) * Math.sin(B * hd);

        return new Coordinate(X,Y,Z);
    }


    /**
     * 空间直角坐标转经纬度坐标
     * @param X
     * @param Y
     * @param Z
     * @param coorSystem 坐标系统
     * @return
     */
    public static Coordinate XYZ_XYZToBLH(double X, double Y, double Z, CoorSystem coorSystem)
    {
        double jd = 180 / Math.PI;
        double a = coorSystem.GetA();
        double b = coorSystem.GetB();
        double e2 = (a * a - b * b) / (a * a);

        //由空间直角坐标计算空间大地坐标（采用迭代方法）
        double bR = a * Math.sqrt(1 - e2);//椭球短半轴
        double n0 = a;
        double h0 = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2) + Math.pow(Z, 2)) - Math.sqrt(a * bR);
        double b0 = Math.atan(Z / Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / (1 - e2 * n0 / (n0 + h0)));
        while (true)
        {
            n0 = a / Math.sqrt(1 - e2 * Math.pow(Math.sin(b0), 2));
            double h1 = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / Math.cos(b0) - n0;
            double b1 = Math.atan(Z / Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / (1 - e2 * n0 / (n0 + h1)));
            if ((b1 - b0) < 1e-10 && (h1 - h0) < 0.0001)
            {
                h0 = h1;
                b0 = b1;
                break;
            }
            else
            {
                h0 = h1;
                b0 = b1;
            }
        }

        double H = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / Math.cos(b0) - n0;
        double L = Math.atan(Y / X);
        double B = Math.atan(Z / Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)) / (1 - e2 * n0 / (n0 + H)));

        return new Coordinate((L * jd)<0?180+L*jd:L*jd,B * jd,H);

    }
}
