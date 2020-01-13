package com.DingTu.CoordinateSystem;

import com.DingTu.Enum.lkCoorTransMethod;
import com.DingTu.Base.Tools;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import com.DingTu.Cargeometry.Coordinate;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class CoorParamTools {

    /**
     * 计算四参数，至少两上控制点，如果只有一个控制点则为单点校正，直接做差即可
     * @param CoorList
     * @return
     */
    public static HashMap<String,Object> CalFourPara(List<Coordinate> CoorList,CoorSystem CS)
    {
        //只有一对匹配点
        if (CoorList.size()==2)
        {
            Coordinate P1 = CoorList.get(0);

            Coordinate P2 = CoorList.get(1);
//            CoorSystem CS = (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem());
            if (CS.GetPMTransMethod()== lkCoorTransMethod.enFourPara)
            {
                P2.setX(P2.getX()+CS.GetTransToP41());
                P2.setY(P2.getY()+CS.GetTransToP42());
            }
            //判读误差值（目标-源）
            String DX = Tools.ConvertToDigi((P2.getX()-P1.getX())+"",3);
            String DY = Tools.ConvertToDigi((P2.getY()-P1.getY())+"",3);
            HashMap<String,Object> result = new HashMap<String,Object>();
            result.put("DX", DX); result.put("DY", DY);
            result.put("R", 0);result.put("K", 1);
            return result;
        }

        //两对匹配点
        if (CoorList.size()==4)
        {
            return CalFourParaBy2MatchPoint(CoorList);
        }

        //三对以上用七参计算方法
        if (CoorList.size()>=5)
        {
            Coordinate[] aPtSource = new Coordinate[CoorList.size()/2];
            Coordinate[] aPtTo = new Coordinate[CoorList.size()/2];
            int idx = 0;
            for(int i=0;i<CoorList.size()/2;i++)
            {
                aPtSource[i] = CoorList.get(idx);aPtTo[i]=CoorList.get(idx+1);idx+=2;
            }
            HashMap<String,Object> sevenParam = CalSevenPara(aPtSource,aPtTo);

            HashMap<String,Object> result = new HashMap<String,Object>();
            if (!Tools.IsDouble(sevenParam.get("DX")+""))sevenParam.put("DX","0");
            if (!Tools.IsDouble(sevenParam.get("DY")+""))sevenParam.put("DY","0");
            if (!Tools.IsDouble(sevenParam.get("WZ")+""))sevenParam.put("WZ","0");
            if (!Tools.IsDouble(sevenParam.get("K")+""))sevenParam.put("K","1");
            result.put("DX", (Tools.ConvertToDigi(Double.parseDouble(sevenParam.get("DX")+""))));
            result.put("DY", (Tools.ConvertToDigi(Double.parseDouble(sevenParam.get("DY")+""))));
            result.put("R", (Tools.ConvertToDigi(-Double.parseDouble(sevenParam.get("WZ")+"")*180/Math.PI * 3600)));
            result.put("K", (Tools.ConvertToDigi(Double.parseDouble(sevenParam.get("K")+""))));
            return result;
        }
        return null;
    }

    /**
     * 通过两个匹配点计算四参数
     * @param CoorList
     * @return
     */
    private static HashMap<String,Object> CalFourParaBy2MatchPoint(List<Coordinate> CoorList)
    {

        Coordinate OP1 = CoorList.get(0);
        Coordinate OP2 = CoorList.get(2);

        Coordinate NP1 = CoorList.get(1);
        Coordinate NP2 = CoorList.get(3);

        double delX1 = NP1.getX() - NP2.getX();
        double delX2 = OP1.getX() - OP2.getX();
        double delY3 = NP1.getY() - NP2.getY();
        double delY4 = OP1.getY() - OP2.getY();

        double delxy = (delX2 * delY3 - delX1 * delY4) / (delX1 * delX2 + delY3 * delY4);

        double TA = Math.atan(delxy);
        double K = delX1 / (delX2 * Math.cos(TA) - delY4 * Math.sin(TA));

        double XP = NP1.getX() - (OP1.getX() * Math.cos(TA) - OP1.getY() * Math.sin(TA)) * K;
        double YP = NP1.getY() - (OP1.getY() * Math.cos(TA) + OP1.getX() * Math.sin(TA)) * K;

        HashMap<String,Object> result = new HashMap<String,Object>();
        if (!Tools.IsDouble(XP+""))XP=0;
        if (!Tools.IsDouble(YP+""))YP=0;
        if (!Tools.IsDouble(TA+""))TA=0;
        if (!Tools.IsDouble(K+""))K=1;

        DecimalFormat df = new DecimalFormat("0.000");

//        result.put("DX", XP);
//        result.put("DY", YP);
//        result.put("R", TA*180/Math.PI * 3600);   //弧度->秒
//        result.put("K", K);

        result.put("DX", df.format(XP));
        result.put("DY", df.format(YP));
        result.put("R", df.format(TA*180/Math.PI * 3600));   //弧度->秒
        result.put("K", df.format(K));

        //MessageBox.Show("XP=" + XP + "\r\n" + "YP=" + YP + "\r\nTA=" + TA + "\r\nK=" + K + "\r\nX=" + X + "\r\nY=" + Y);
        return result;
    }

    /**
     * 根据3个或者3个以上的点的两套坐标系的坐标计算7参数(最小二乘法) 适用于小角度转换 bursa模型
     * @param aPtSource 已知点的源坐标系的坐标
     * @param aPtTo 已知点的新坐标系的坐标
     * @return 输出: 7参数
     */
    public static HashMap<String,Object> CalSevenPara(Coordinate[] aPtSource, Coordinate[] aPtTo)
    {
        //给A B 矩阵赋值
        int arrALen = aPtSource.length * 3;
        double[][] arrA = new double[arrALen][7]; // 如果是4个已知点， 12 * 7矩阵  A*X=B中的矩阵A
        for (int i = 0; i < arrALen; i++)
        {
            if (i % 3 == 0)
            {
                arrA[i][0] = 1;
                arrA[i][1] = 0;
                arrA[i][2] = 0;
                arrA[i][3] = aPtSource[i / 3].getX();
                arrA[i][4] = 0;
                arrA[i][5] = -aPtSource[i / 3].getZ();
                arrA[i][6] = aPtSource[i / 3].getY();
            }
            else if (i % 3 == 1)
            {
                arrA[i][0] = 0;
                arrA[i][1] = 1;
                arrA[i][2] = 0;
                arrA[i][3] = aPtSource[i / 3].getY();
                arrA[i][4] = aPtSource[i / 3].getZ();
                arrA[i][5] = 0;
                arrA[i][6] = -aPtSource[i / 3].getX();
            }
            else if (i % 3 == 2)
            {
                arrA[i][0] = 0;
                arrA[i][1] = 0;
                arrA[i][2] = 1;
                arrA[i][3] = aPtSource[i / 3].getZ();
                arrA[i][4] = -aPtSource[i / 3].getY();
                arrA[i][5] = aPtSource[i / 3].getX();
                arrA[i][6] = 0;
            }
        }

        int arrBLen = aPtSource.length * 3;
        double[][] arrB = new double[arrBLen][1]; // A * X = B 中的矩阵B, 如果有4个点，就是 12*1矩阵
        for (int i = 0; i <arrBLen; i++)
        {
            if (i % 3 == 0)
            {
                arrB[i][0] = aPtTo[i / 3].getX();
            }
            else if (i % 3 == 1)
            {
                arrB[i][0] = aPtTo[i / 3].getY();
            }
            else if (i % 3 == 2)
            {
                arrB[i][0] = aPtTo[i / 3].getZ();
            }
        }

        LKMatrix mtrA = new LKMatrix(arrA); // A矩阵
        LKMatrix mtrAT = mtrA.Transpose(); // A的转置
        LKMatrix mtrB = new LKMatrix(arrB); // B矩阵

        LKMatrix mtrATmulA = mtrAT.Multiply(mtrA); // A的转置×A

        //// 求(A的转置×A)的逆矩阵
        mtrATmulA.Inv();

        //// A的转置 × B

        LKMatrix mtrATmulB = mtrAT.Multiply(mtrB); // A的转置 * B

        //// 结果
        LKMatrix mtrResult = mtrATmulA.Multiply(mtrATmulB);

        HashMap<String,Object> result = new HashMap<String,Object>();
        result.put("DX", mtrResult.GetData()[0][0]);
        result.put("DY", mtrResult.GetData()[1][0]);
        result.put("DZ", mtrResult.GetData()[2][0]);
        result.put("K", mtrResult.GetData()[3][0]);
        result.put("WX", mtrResult.GetData()[4][0]);
        result.put("WY", mtrResult.GetData()[5][0]);
        result.put("WZ", mtrResult.GetData()[6][0]);


        return result;
//        return new ServerPara(mtrResult.GetData()[0, 0], mtrResult.GetData()[1, 0], mtrResult.GetData()[2, 0], mtrResult.GetData()[3, 0],
//                          mtrResult.GetData()[4, 0], mtrResult.GetData()[5, 0], mtrResult.GetData()[6, 0]);
//        // PS: 必须考虑cosA = 0 不能作为分母的情况
//        // Add code
    }
}
