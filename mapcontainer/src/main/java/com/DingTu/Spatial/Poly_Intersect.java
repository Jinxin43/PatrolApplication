package com.DingTu.Spatial;

import com.DingTu.Enum.lkPartType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Line;
import com.DingTu.Cargeometry.Part;
import com.DingTu.Cargeometry.Polygon;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class Poly_Intersect {

    /**
     * 计算两面相交部分
     * @param Ply1
     * @param Ply2
     * @return
     */
    public List<HashMap<String,Object>> Poly_Intersect(Polygon Ply1, Polygon Ply2)
    {
        List<HashMap<String,Object>> interPartList = new ArrayList<HashMap<String,Object>>();

        //是否有效面积，只有主面与主面相交才算是有效面积
        boolean voidArea = false;

        for(int i=0;i<Ply1.getPartCount();i++)
        {
            Part Pa1 = Ply1.GetPartAt(i).Clone();
            Pa1.SetPartType(lkPartType.enPoly);
            for(int j=0;j<Ply2.getPartCount();j++)
            {
                Part Pa2 = Ply2.GetPartAt(j).Clone();
                Pa2.SetPartType(lkPartType.enPoly);
                if (i==0 && j==0)voidArea=true;else voidArea=false;

                //计算两Part相交部分
                List<List<Coordinate>> subPartVertexList = this.Part_Intersect(Pa1, Pa2);
                if (subPartVertexList!=null)
                {
                    for(List<Coordinate> CoorList:subPartVertexList)
                    {
                        Part part = new Part();
                        part.setVertext(CoorList);
                        double Area = part.CalArea();
                        if (!voidArea)Area *= -1;
                        HashMap<String,Object> HMSub = new HashMap<String,Object>();
                        HMSub.put("Area", Area);
                        HMSub.put("Part", part);
                        interPartList.add(HMSub);
                    }
                }
            }
        }
        return interPartList;
    }

    /**
     * 面相交分析
     */
    private List<List<Coordinate>> Part_Intersect(Part part1,Part part2)
    {
        List<Coordinate> CoorList1 = null;
        List<Coordinate> CoorList2 = null;

        //判断两部分的相交情况，如果不相交直接返回
        if (!part1.getEnvelope().Intersect(part2.getEnvelope()))return null;

        //预处理面
        HashMap<String,Object> PredoInfo = this.PreDoPolygon(part1,part2);
        if (PredoInfo.get("关系").equals("")) return null;
        if (PredoInfo.get("关系").equals("P2inP1"))  //判断Part1是否完全包含Part2，Part2在Part1内部
        {
            List<List<Coordinate>> SubPolyList1 = new ArrayList<List<Coordinate>>();
            SubPolyList1.add(part2.getVertexList());
            return SubPolyList1;
        }
        if (PredoInfo.get("关系").equals("P1inP2"))  //判断Part2是否完全包含Part1，Part1在Part2内部
        {
            List<List<Coordinate>> SubPolyList1 = new ArrayList<List<Coordinate>>();
            SubPolyList1.add(part1.getVertexList());
            return SubPolyList1;
        }

        CoorList1 = (List<Coordinate>)PredoInfo.get("CoorList1");
        CoorList2 = (List<Coordinate>)PredoInfo.get("CoorList2");

        //计算两多边形边线交点
        List<InterPoint> ary1 = new ArrayList<InterPoint>();
        List<InterPoint> ary2 = new ArrayList<InterPoint>();
        for(int i=0;i<CoorList1.size()-1;i++)
        {
            Line L1 = new Line(CoorList1.get(i),CoorList1.get(i+1));

            for(int j=0;j<CoorList2.size()-1;j++)
            {
                Line L2 = new Line(CoorList2.get(j),CoorList2.get(j+1));

                Coordinate jp1=new Coordinate();Coordinate jp2=new Coordinate();
                int IPCount = L1.Intersect(L2, jp1, jp2);
                if (IPCount==1)
                {
                    InterPoint ip1 = new InterPoint();
                    ip1.DH = ary1.size()+1;
                    ip1.mPoint = jp1;
                    ip1.beforeVertexIndex = i;
                    ip1.ToBeforeVertexDistance = this.CalDistance(CoorList1.get(i), jp1);
                    ary1.add(ip1);

                    InterPoint ip2 = new InterPoint();
                    ip2.DH = ary2.size()+1;
                    ip2.mPoint = jp1;
                    ip2.beforeVertexIndex = j;
                    ip2.ToBeforeVertexDistance = this.CalDistance(CoorList2.get(j), jp1);
                    ary2.add(ip2);
                }
            }
        }

        //对交点集合进行逆时针排序
        this.InterPointSort(ary1);this.InterPointSort(ary2);


        //确定交点的出入状态，1-出，2-入
        this.OutInStatus(CoorList1, CoorList2, ary1, ary2);
        this.OutInStatus(CoorList2, CoorList1, ary2, ary1);

        //遍历交点数组，提取相交多边形
        List<List<Coordinate>> SubPolyList = new ArrayList<List<Coordinate>>();


        List<InterPoint> ary = ary1;
        List<Coordinate> coorList = CoorList1;
        int CalListIndex = 1;
        //相交多边形的坐标列表
        List<Coordinate> SubPoly = new ArrayList<Coordinate>();

        //提取一面的入点交点
        InterPoint inStartPoint = this.GetUnCheckInPoint(ary);
        InterPoint inCircleStartPoint = this.GetUnCheckInPoint(ary);
        do
        {
            SubPoly.add(inStartPoint.mPoint);inStartPoint.check=true;

            //下一个没有处理过的交点
            InterPoint nextInterPoint = this.GetNextPoint(inStartPoint, ary);

            if (nextInterPoint!=null)
            {
                nextInterPoint.check=true;

                //将交点对之间的节点加入
                for(int c=inStartPoint.beforeVertexIndex+1;c<=nextInterPoint.beforeVertexIndex;c++)
                {
                    SubPoly.add(coorList.get(c));
                }

                //交换坐标串，使其移入到另一个多边形
                if (CalListIndex==1) {ary = ary2;coorList = CoorList2;CalListIndex=2;}
                else if (CalListIndex==2) {ary = ary1;coorList = CoorList1;CalListIndex=1;}

                if (inCircleStartPoint.mPoint.Equal(nextInterPoint.mPoint))
                {
                    SubPolyList.add(SubPoly);
                    SubPoly = new ArrayList<Coordinate>();

                    //获取没有处理过的入交点
                    inStartPoint = this.GetUnCheckInPoint(ary1);
                    inCircleStartPoint = this.GetUnCheckInPoint(ary1);
                    CalListIndex = 1;ary = ary1;coorList = CoorList1;
                    continue;
                }

                //得到另一列表中具有相同坐标的交点
                inStartPoint = this.GetEqualInterPointByCoordinate(nextInterPoint.mPoint, ary);
            } else
            {
                //处理入点以后没有交点的情况
                for(int idx=inStartPoint.beforeVertexIndex+1;idx<coorList.size();idx++)
                {
                    SubPoly.add(coorList.get(idx));
                }
                SubPolyList.add(SubPoly);

                break;
            }

        }while(inStartPoint!=null);

        return SubPolyList;
//		//提取相交多边形的坐标点及面积
//		String ResultInfo = "";
//		for(List<Coordinate> CoorList:SubPolyList)
//		{
//			Part part = new Part();
//			part.setVertext(CoorList);
//			double a = part.CalArea();
//
//			List<String> CoorStrList = new ArrayList<String>();
//			for(Coordinate Coor:CoorList)CoorStrList.add(Coor.ToString());
//			ResultInfo += "面积："+a+"\n坐标串："+CoorStrList.size()+"\n"+Tools.JoinT("\n", CoorStrList)+"\r\n";
//
//		}
    }

    //得到指定交点的下一个交点
    private InterPoint GetNextPoint(InterPoint ip,List<InterPoint> ary)
    {
        for(int i=0;i<ary.size()-1;i++)
        {
            if (ary.get(i).mPoint.Equal(ip.mPoint))
            {
                return ary.get(i+1);
            }
        }
        return null;
    }

    //得到入状态的交点
    private InterPoint GetUnCheckInPoint(List<InterPoint> ary)
    {
        for(InterPoint ip:ary)
        {
            if (!ip.check && ip.OutInt==2)   //没有处理过且是入状态
            {
                return ip;
            }
        }
        return null;
    }

    //获取具有相同坐标点的InterPoint
    private InterPoint GetEqualInterPointByCoordinate(Coordinate Coor,List<InterPoint> ary)
    {
        for(InterPoint ip :ary)
        {
            if (ip.mPoint.Equal(Coor)) return ip;
        }
        return null;
    }

    /**
     * 预处理面
     * @param part1
     * @param part2
     * @return
     */
    private HashMap<String,Object> PreDoPolygon(Part part1, Part part2)
    {
        HashMap<String,Object> result = new HashMap<String,Object>();
        result.put("关系", "相交");

        //判断两面位置关系，包含，分离
        int OutVertexIndex1 = -1;  //第一个在面外节点索引
        int InnerVertexCount1 = 0;  //在面内点的个数
        for(int i=0;i<part2.getVertexList().size();i++)
        {
            if (part1.ContainsPoint(part2.getVertexList().get(i)))InnerVertexCount1++;
            else if (OutVertexIndex1==-1)OutVertexIndex1 = i;
        }
        if (InnerVertexCount1==part2.getVertexList().size())  //判断Part1是否完全包含Part2
        {
            result.put("关系", "P2inP1"); return result;
        }

        int OutVertexIndex2 = -1;  //第一个在面外节点索引
        int InnerVertexCount2 = 0;  //在面内点的个数
        for(int i=0;i<part1.getVertexList().size();i++)
        {
            if (part2.ContainsPoint(part1.getVertexList().get(i)))InnerVertexCount2++;
            else if (OutVertexIndex2==-1)OutVertexIndex2 = i;
        }
        if (InnerVertexCount2==part1.getVertexList().size())  //判断Part2是否完全包含Part1
        {
            result.put("关系", "P1inP2"); return result;
        }

        //不相交，分离情况
        if (InnerVertexCount1==0 && InnerVertexCount2==0){result.put("关系", ""); return result;}

        //整理端点在面内的情况
        List<Coordinate> CoorList1 = new ArrayList<Coordinate>();
        List<Coordinate> CoorList2 = new ArrayList<Coordinate>();
        for(Coordinate Coor :part1.getVertexList())CoorList1.add(Coor);CoorList1.add(part1.getVertexList().get(0));
        for(Coordinate Coor :part2.getVertexList())CoorList2.add(Coor);CoorList2.add(part2.getVertexList().get(0));

        if (OutVertexIndex1!=0)
        {
            CoorList2.clear();
            for(int i=OutVertexIndex1;i<part2.getVertexList().size();i++)CoorList2.add(part2.getVertexList().get(i));
            for(int i=0;i<=OutVertexIndex1;i++)CoorList2.add(part2.getVertexList().get(i));
        }
        if (OutVertexIndex2!=0)
        {
            CoorList1.clear();
            for(int i=OutVertexIndex2;i<part1.getVertexList().size();i++)CoorList1.add(part1.getVertexList().get(i));
            for(int i=0;i<=OutVertexIndex2;i++)CoorList1.add(part1.getVertexList().get(i));
        }

        result.put("CoorList1", CoorList1);
        result.put("CoorList2", CoorList2);
        return result;

    }

    //交点集合出入状态
    private void OutInStatus(List<Coordinate> CoorList1,List<Coordinate> CoorList2, List<InterPoint> ary1,List<InterPoint> ary2)
    {
        //第一个相交点的出入状态
        InterPoint ip1 = ary1.get(0);
        InterPoint ip2 = null;
        for(InterPoint interp:ary2)
        {
            if (ip1.mPoint.Equal(interp.mPoint))ip2 = interp;
        }


        Coordinate P1 = CoorList1.get(ip1.beforeVertexIndex);
        Coordinate P2 = CoorList1.get(ip1.beforeVertexIndex+1);

        Coordinate Q1 = CoorList2.get(ip2.beforeVertexIndex);
        Coordinate Q2 = CoorList2.get(ip2.beforeVertexIndex+1);

        Coordinate P1P2 = new Coordinate(P1.getX()-P2.getX(),P1.getY()-P2.getY());

        Coordinate Q1Q2 = new Coordinate(Q1.getX()-Q2.getX(),Q1.getY()-Q2.getY());

        double P1P2xQ1Q2 = P1P2.getX() * Q1Q2.getY()-P1P2.getY()*Q1Q2.getX();
        if (P1P2xQ1Q2 > 0) ip1.OutInt=1; else ip1.OutInt=2;


        for(int i=1;i<ary1.size();i++)
        {
            if (ary1.get(i-1).OutInt==1)ary1.get(i).OutInt=2;
            else ary1.get(i).OutInt=1;
        }
    }

    //对交点集合进行排序处理
    private void InterPointSort(List<InterPoint> interPointList)
    {
        for(int i=0;i<interPointList.size();i++)
        {
            InterPoint MinPt = interPointList.get(i);
            for(int j=i;j<interPointList.size();j++)
            {
                InterPoint ip2 = interPointList.get(j);
                if (MinPt.beforeVertexIndex>ip2.beforeVertexIndex)MinPt = ip2;
                if (MinPt.beforeVertexIndex==ip2.beforeVertexIndex)
                {
                    if (MinPt.ToBeforeVertexDistance>ip2.ToBeforeVertexDistance)MinPt = ip2;
                }
            }
            interPointList.remove(MinPt);interPointList.add(i, MinPt);
        }
    }

    private double CalDistance(Coordinate Coor1, Coordinate Coor2)
    {
        double d = (Coor1.getX() - Coor2.getX())*(Coor1.getX() - Coor2.getX())+
                (Coor1.getY() - Coor2.getY())*(Coor1.getY() - Coor2.getY());

        return Math.sqrt(d);
    }


    private class InterPoint
    {
        public Coordinate mPoint = null;   //交点坐标
        public int beforeVertexIndex;    //交点所在线段的交一节点索引
        public double ToBeforeVertexDistance=0;   //适用一个线段上有多个交点的情况

        public int OutInt = 0;    //1-出，2-入
        public boolean check = false;   //是否处理过

        public int DH = -1;
    }
}
