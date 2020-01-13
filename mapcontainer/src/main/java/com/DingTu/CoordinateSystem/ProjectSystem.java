package com.DingTu.CoordinateSystem;

import com.DingTu.Enum.lkCoorTransMethod;

import com.DingTu.Cargeometry.Coordinate;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class ProjectSystem {

    //坐标系统参数
    private CoorSystem _CoorSystem = null;
    /**
     * 设置坐标系统
     * @param coorSystem
     */
    public void SetCoorSystem(CoorSystem coorSystem)
    {

        this._CoorSystem = coorSystem;
    }
    /**
     * 获取坐标系统
     * @return
     */
    public CoorSystem GetCoorSystem(){return this._CoorSystem;}

    /**
     * WGS84坐标转到当前坐标系下
     * @param JD
     * @param WD
     * @return
     */
    public Coordinate WGS84ToXY(double L1,double B1,double H1)
    {
        //只有WGS84坐标可直接转换，其它坐标系均需要转换
        if (this._CoorSystem.GetName().equals("WGS-84坐标"))
        {
            Coordinate xy = Project_Web.Web_BLToXY(L1, B1);
            xy.setZ(H1);
            return xy;
        }
        else
        {
            //空间转换
            if (this._CoorSystem.GetCoorTransMethod()==lkCoorTransMethod.enThreePara)
            {
                //84经纬度转84空间坐标
                CoorSystem CS84 = new CoorSystem();CS84.ToWGS84();
                Coordinate XYZ84 = Project_XYZ.XYZ_BLHToXYZ(L1, B1, H1, CS84);

                //加入转换参数
                XYZ84.setX(XYZ84.getX()-this._CoorSystem.GetTransToP31());
                XYZ84.setY(XYZ84.getY()-this._CoorSystem.GetTransToP32());
                XYZ84.setZ(XYZ84.getZ()-this._CoorSystem.GetTransToP33());

                //转换为目标坐标系统的空间坐标
                Coordinate BLHTo = Project_XYZ.XYZ_XYZToBLH(XYZ84.getX(), XYZ84.getY(), XYZ84.getZ(),this._CoorSystem);
                Coordinate xy = Project_GK.GK_BLToXY(BLHTo.getX(), BLHTo.getY(), this._CoorSystem);
                xy.setZ(H1);

                if (this._CoorSystem.GetPMTransMethod()==lkCoorTransMethod.enFourPara)
                {
                    xy = this.FourParaChange(xy.getX(), xy.getY(), H1, 2);
                }

                return xy;
            }

            if (this._CoorSystem.GetCoorTransMethod()==lkCoorTransMethod.enServenPara)
            {
                //84经纬度转84空间坐标
                CoorSystem CS84 = new CoorSystem();CS84.ToWGS84();
                Coordinate XYZ84 = Project_XYZ.XYZ_BLHToXYZ(L1, B1, H1, CS84);

                double k = (1+this._CoorSystem.GetTransToP77()/1000000);  //比例因子(ppm->10e-6)
                double a2 = k * this._CoorSystem.GetTransToP74()/3600*Math.PI/180;  //X旋转 (秒->弧度)
                double a3 = k * this._CoorSystem.GetTransToP75()/3600*Math.PI/180;  //Y旋转
                double a4 = k * this._CoorSystem.GetTransToP76()/3600*Math.PI/180;  //Z旋转
                double newX = this._CoorSystem.GetTransToP71() + k * XYZ84.getX() + 0 - a3 * XYZ84.getZ() + a4 * XYZ84.getY();
                double newY = this._CoorSystem.GetTransToP72() + k * XYZ84.getY() + a2 * XYZ84.getZ() + 0 - a4 * XYZ84.getX();
                double newZ = this._CoorSystem.GetTransToP73() + k * XYZ84.getZ() - a2 * XYZ84.getY() + a3 * XYZ84.getX() + 0;
                XYZ84.setX(newX);XYZ84.setY(newY);XYZ84.setZ(newZ);

                //转换为目标坐标系统的空间坐标
                Coordinate BLHTo = Project_XYZ.XYZ_XYZToBLH(XYZ84.getX(), XYZ84.getY(), XYZ84.getZ(),this._CoorSystem);
                Coordinate xy = Project_GK.GK_BLToXY(BLHTo.getX(), BLHTo.getY(), this._CoorSystem);
                xy.setZ(H1);

                if (this._CoorSystem.GetPMTransMethod()== lkCoorTransMethod.enFourPara)
                {
                    xy = this.FourParaChange(xy.getX(), xy.getY(), H1, 2);
                }

                return xy;
            }

            //平面转换
            if (this._CoorSystem.GetPMTransMethod()==lkCoorTransMethod.enFourPara)
            {
                return this.FourParaChange(L1,B1, H1, 1);
            }

            Coordinate xy = Project_GK.GK_BLToXY(L1,B1, this._CoorSystem);
            if(xy != null)
            {
                xy.setZ(H1);
            }


            return xy;
        }
    }

    /**
     * 四参变换
     * @param CoorX
     * @param Coor
     * @param ChangeType  1-经纬度，2-平面
     * @return
     */
    private Coordinate FourParaChange(double L1,double B1,double H1,int ChangeType)
    {
        double Dx = this._CoorSystem.GetTransToP41();
        double Dy = this._CoorSystem.GetTransToP42();
        double A = this._CoorSystem.GetTransToP43()/3600*Math.PI/180;;  //旋转 (秒->弧度)
        double K = this._CoorSystem.GetTransToP44();
        Coordinate xy = new Coordinate(L1,B1);

        if (ChangeType==1)xy = Project_GK.GK_BLToXY(L1,B1, this._CoorSystem);

        if(xy==null)
        {
            return null;
        }
        double X0 = Dx + xy.getX() * K * Math.cos(A) - xy.getY() * K * Math.sin(A);
        double Y0 = Dy + xy.getX() * K * Math.sin(A) + xy.getY() * K * Math.cos(A);
        xy.setX(X0);xy.setY(Y0);xy.setZ(H1);
        return xy;
    }


    /**
     * 当前坐标系下坐标点转到WGS84坐标，注意：在中央经线不正确的时候，可能无法正确反解经纬度坐标
     * @param x
     * @param y
     * @return
     */
    public Coordinate XYToWGS84(Coordinate xyCoor)
    {
        return this.XYToWGS84(xyCoor.getX(),xyCoor.getY(),xyCoor.getZ());
    }
    public Coordinate XYToWGS84(double x,double y,double z)
    {
        //只有WGS84坐标可直接转换，其它坐标系均需要转换
        if (this._CoorSystem.GetName().equals("WGS-84坐标"))
        {
            Coordinate lb = Project_Web.Web_XYToBL(x, y);
            lb.setZ(z);
            return lb;
        }
        else
        {
            if (this._CoorSystem.GetCoorTransMethod()==lkCoorTransMethod.enThreePara)
            {
                //平面坐标反解为经纬度坐标
                Coordinate XYToBL = Project_GK.GK_XYToBL(x, y, this._CoorSystem);

                //将经纬度坐标转换空间坐标
                Coordinate XYZ = Project_XYZ.XYZ_BLHToXYZ(XYToBL.getX(), XYToBL.getY(),z,this._CoorSystem);

                //加入转换参数转换为WGS84空间坐标
                XYZ.setX(XYZ.getX()+this._CoorSystem.GetTransToP31());
                XYZ.setY(XYZ.getY()+this._CoorSystem.GetTransToP32());
                XYZ.setZ(XYZ.getZ()+this._CoorSystem.GetTransToP33());

                //84空间坐标转84经纬度
                CoorSystem CS84 = new CoorSystem();CS84.ToWGS84();
                Coordinate lb84 = Project_XYZ.XYZ_XYZToBLH(XYZ.getX(),XYZ.getY(),XYZ.getZ(), CS84);
                return lb84;
            }

            if (this._CoorSystem.GetCoorTransMethod()==lkCoorTransMethod.enServenPara)
            {
                double k = (1+this._CoorSystem.GetTransToP77()/1000000);  //比例因子
                double a2 = k * this._CoorSystem.GetTransToP74()/3600*Math.PI/180;  //X旋转
                double a3 = k * this._CoorSystem.GetTransToP75()/3600*Math.PI/180;  //Y旋转
                double a4 = k * this._CoorSystem.GetTransToP76()/3600*Math.PI/180;  //Z旋转

                //1、将平面坐标转换为经纬度
                Coordinate lb = Project_GK.GK_XYToBL(x, y, this._CoorSystem);

                //2、将经纬度换算为空间坐标
                Coordinate XZY = Project_XYZ.XYZ_BLHToXYZ(lb.getX(), lb.getY(), z,this._CoorSystem);

//	            double k = (1+this._CoorSystem.GetTransToP77()/1000000);  //比例因子(ppm->10e-6)
//	            double a2 = k * this._CoorSystem.GetTransToP74()/3600*Math.PI/180;  //X旋转 (秒->弧度)
//	            double a3 = k * this._CoorSystem.GetTransToP75()/3600*Math.PI/180;  //Y旋转
//	            double a4 = k * this._CoorSystem.GetTransToP76()/3600*Math.PI/180;  //Z旋转
//	            double newX = this._CoorSystem.GetTransToP71() + k * XYZ84.getX() + 0 - a3 * XYZ84.getZ() + a4 * XYZ84.getY();
//	            double newY = this._CoorSystem.GetTransToP72() + k * XYZ84.getY() + a2 * XYZ84.getZ() + 0 - a4 * XYZ84.getX();
//	            double newZ = this._CoorSystem.GetTransToP73() + k * XYZ84.getZ() - a2 * XYZ84.getY() + a3 * XYZ84.getX() + 0;
//	            XYZ84.setX(newX);XYZ84.setY(newY);XYZ84.setZ(newZ);

                //3、根据七参数反解出WGS84空间坐标，这是利用克拉默法则（克拉默法则）求方程解的
                double A1,B1,C1,D1,A2,B2,C2,D2,A3,B3,C3,D3;
                A1 = k;B1=a4;C1=-a3;D1=XZY.getX()-this._CoorSystem.GetTransToP71();
                A2 = -a4;B2=k;C2=a2;D2=XZY.getY()-this._CoorSystem.GetTransToP72();
                A3 = a3;B3=-a2;C3=k;D3=XZY.getZ()-this._CoorSystem.GetTransToP73();
                double D=A1*B2*C3+B1*C2*A3+C1*A2*B3-C1*B2*A3-B1*A2*C3-A1*C2*B3;
                double E=D1*B2*C3+B1*C2*D3+C1*D2*B3-C1*B2*D3-B1*D2*C3-D1*C2*B3;
                double F=A1*D2*C3+D1*C2*A3+C1*A2*D3-C1*D2*A3-D1*A2*C3-A1*C2*D3;
                double G=A1*B2*D3+B1*D2*A3+D1*A2*B3-D1*B2*A3-B1*A2*D3-A1*D2*B3;
                double WGS84_X=E/D;
                double WGS84_Y=F/D;
                double WGS84_Z=G/D;

                //4、将WGS84空间坐标转换为经纬度
                CoorSystem CS84 = new CoorSystem();CS84.ToWGS84();
                Coordinate lb84 = Project_XYZ.XYZ_XYZToBLH(WGS84_X, WGS84_Y, WGS84_Z, CS84);
                return lb84;
            }

//			 if (this._CoorSystem.GetCoorTransMethod()==lkCoorTransMethod.enFourPara)
//			 {
//				 double Dx = this._CoorSystem.GetTransToP41();
//				 double Dy = this._CoorSystem.GetTransToP42();
//				 double A = this._CoorSystem.GetTransToP43();
//				 double K = this._CoorSystem.GetTransToP44();
//
//		         //1、根据四参数反解出WGS84平面坐标
//		         double a1,b1,c1,a2,b2,c2;
//		         a1 =K * Math.cos(A);
//		         b1 = -K * Math.sin(A);
//		         c1 = x - Dx;
//		         a2 = K * Math.sin(A);
//		         b2 = K * Math.cos(A);
//		         c2 = y - Dy;
//		         double X = (c1 - c2 * (b1 / b2)) / (a1 - a2 * (b1 / b2));
//		         double Y = (c1 - a1 * X) / b1;
//
//		         //2、将WGS84平面坐标转换为经纬度
//		         Coordinate lb84 = Project_GK.GK_XYToBL(X,Y, this._CoorSystem);
//		         lb84.setZ(z);
//		         return lb84;
//			 }

            //无改正数
            Coordinate lb = Project_GK.GK_XYToBL(x,y, this._CoorSystem);
            lb.setZ(z);
            return lb;
        }
    }



    /**
     * 根据经纬度坐标计算中央经线3度分带
     * @param X
     * @param Y
     * @return
     */
    public static int AutoCalCenterJX(double JD, double WD)
    {
        //6度分带
        //int n=Convert.ToInt32(L / 6+1);
        //int L0 = 6 * n - 3;

        //3度分带
        int n = (int)(JD / 3 + 0.5);
        int L0 = 3 * n;
        return L0;

        //return Convert.ToInt32(Math.Truncate(L));
    }

    /**
     * 根据经度，计算带号
     * @param JD  经度
     * @param FD  分带类型，3，6
     * @return
     */
    public static int GetDH(double JD,int FDType)
    {
        //floor ： 返回不大于它的最大整
        int dh = (int) Math.floor(JD/FDType);
        if (FDType==6)dh++;
        return dh;
    }

    /**
     * 根据带号，换算中央经线
     * @param DH  带号
     * @param FDType 分带类型，3，6
     * @return
     */
    public static int GetCenterJX(int DH,int FDType)
    {
        int centerjx = DH*FDType;
        if (FDType==6)centerjx-=3;
        return centerjx;
    }

//     //将圆球坐标转换成椭球坐标
//     public static Coordinate WebXYToUtmXY(double x1, double y1)
//     {
//         Coordinate LB = WebUTMXYToBL(x1, y1);
//         return UTMBLToXY(LB.getX(), LB.getY());
//     }
//
//     //84平面转换成经纬度，UTM--84经纬度转84平面(正解)，L,B--84经纬度坐标; X1,Y1--84平面坐标
//     public static Coordinate UTMBLToXY(double L1, double B1)
//     {
//         return UTMBLToXY(L1, B1, GlobleCenterJX);
//     }




//
//     //UTM--84平面转84经纬度(反解)
//     public static Coordinate UTMXYToBL(double X, double Y)
//     {
//         return UTMXYToBL(X, Y,GlobleCenterJX);
//     }

}
