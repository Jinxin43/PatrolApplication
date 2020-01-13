package com.DingTu.Spatial;

import com.DingTu.Base.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Part;
import com.DingTu.Cargeometry.Polyline;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class LineTools {

    /**
     * 重新修线形
     * @param MainPolyline
     * @param reshapePointList
     * @return
     */
    public static Polyline StartReshape(Polyline MainPolyline, List<Coordinate> reshapePointList)
    {
        Polyline reshapePolyline = (Polyline)MainPolyline.Clone();

        //处理新绘线形
        Polyline clipPolyline = new Polyline();
        Part clipPart = new Part(reshapePointList);
        clipPolyline.AddPart(clipPart);

        //1、计算修边线与主线的交点
        List<Coordinate> m_InterPointList = new ArrayList<Coordinate>();
        Part reshapePart = null;
        for(int p = 0;p<reshapePolyline.getPartCount();p++)
        {
            List<Coordinate> LSInterPointList = new ArrayList<Coordinate>();
            Part part = reshapePolyline.GetPartAt(p);
            Polyline PL = part.GetBorder();
            boolean OK = PL.Intersect(clipPolyline,LSInterPointList);
            if (OK)
            {
                if (reshapePart!=null)return null;
                else
                {
                    reshapePart = part;
                    for(Coordinate Coor:LSInterPointList)m_InterPointList.add(Coor);
                }
            }
        }
        if (m_InterPointList.size()<=1) return null;

        //2、提取交点在修边线内到起点的距离，也就是进行排序处理
        TreeMap<Double,HashMap<String,Object>> splitPointDisList = new TreeMap<Double,HashMap<String,Object>>();
        for(Coordinate Coor:m_InterPointList)
        {
            for(int i=0;i<clipPart.getVertexList().size()-1;i++)
            {
                Coordinate PT1 = clipPart.getVertexList().get(i);
                Coordinate PT2 = clipPart.getVertexList().get(i+1);
                double JL0 = GetDistance(PT1, PT2);
                double JL1 = GetDistance(PT1, Coor);
                double JL2 = GetDistance(PT2, Coor);
                if (Math.abs(JL0-JL1-JL2)<0.00001)
                {
                    //累计到起点的距离
                    double ToStartDis = 0;
                    for(int j=0;j<i;j++)ToStartDis+=GetDistance(clipPart.getVertexList().get(j),clipPart.getVertexList().get(j+1));
                    ToStartDis+=JL1;
                    HashMap<String,Object> ho = new HashMap<String,Object>();
                    ho.put("BeforeVertexIndex", i);
                    ho.put("Coor", Coor);
                    splitPointDisList.put(ToStartDis, ho);
                }
            }
        }

        //3、提取修边线最后需要保留的节点
        List<Coordinate> SaveClipPointList = new ArrayList<Coordinate>();
        List<HashMap<String,Object>> DisList = new ArrayList<HashMap<String,Object>>();
        for(HashMap<String,Object> ho:splitPointDisList.values())DisList.add(ho);

        //起点信息
        int StartVertexIndex = Integer.parseInt(DisList.get(0).get("BeforeVertexIndex")+"");
        Coordinate InterPoint1 = (Coordinate)DisList.get(0).get("Coor");

        //止点信息
        int EndVertexIndex = Integer.parseInt(DisList.get(DisList.size()-1).get("BeforeVertexIndex")+"");
        Coordinate InterPoint2 = (Coordinate)DisList.get(DisList.size()-1).get("Coor");

        SaveClipPointList.add(InterPoint1);
        for(int i=StartVertexIndex+1;i<=EndVertexIndex;i++)
        {
            SaveClipPointList.add(clipPart.getVertexList().get(i));
        }
        SaveClipPointList.add(InterPoint2);

        //4、计算交点在主线上的位置
        int MainPLStartVetexIndex = -1,MainPLEndVertexIndex = -1;
        for(int p=1;p<=2;p++)
        {
            Coordinate Coor = InterPoint1;
            if (p==2)Coor = InterPoint2;
            for(int i=0;i<reshapePart.getVertexList().size()-1;i++)
            {
                Coordinate PT1 = reshapePart.getVertexList().get(i);
                Coordinate PT2 = reshapePart.getVertexList().get(i+1);
                double JL0 = GetDistance(PT1, PT2);
                double JL1 = GetDistance(PT1, Coor);
                double JL2 = GetDistance(PT2, Coor);
                if (Math.abs(JL0-JL1-JL2)<0.00001)
                {
                    if (p==1)MainPLStartVetexIndex = i;
                    if (p==2)MainPLEndVertexIndex = i;
                }
            }
        }

        //5、整理最后线形
        List<Coordinate> EndSaveCoorList = new ArrayList<Coordinate>();

        //判断方向性
        boolean ChangeFlip = false;
        if (MainPLStartVetexIndex>MainPLEndVertexIndex)
        {
            int LS = MainPLStartVetexIndex;MainPLStartVetexIndex=MainPLEndVertexIndex;MainPLEndVertexIndex=LS;
            ChangeFlip=true;
        }

        for(int i=0;i<=MainPLStartVetexIndex;i++)EndSaveCoorList.add(reshapePart.getVertexList().get(i));
        if (!ChangeFlip)for(Coordinate Coor:SaveClipPointList)EndSaveCoorList.add(Coor);
        else
        {
            for(int i=SaveClipPointList.size()-1;i>=0;i--)
            {
                EndSaveCoorList.add(SaveClipPointList.get(i));
            }
        }

        for(int i=MainPLEndVertexIndex+1;i<reshapePart.getVertexList().size();i++)EndSaveCoorList.add(reshapePart.getVertexList().get(i));

        //构建新实体
        reshapePart.getVertexList().clear();
        reshapePart.setVertext(EndSaveCoorList);

        String CoorStr = GetCoorListStr(EndSaveCoorList);

        return reshapePolyline;
    }

    /**
     * 连接多段线
     * @param MainPolyline
     * @param SubPolylineList
     * @return
     */
    public static Polyline StartConnect(Polyline MainPolyline,List<Polyline> SubPolylineList)
    {
        Polyline newPolyline = (Polyline)MainPolyline.Clone();
        while(SubPolylineList.size()>0)
        {
            //查找与当前主线的两个端点最近的线段
            double NearestDis = Double.MAX_VALUE;
            int NearestSegmentIndex = -1;
            boolean IfStart = false;
            int IfNearestNewPolylineIdx = 1;  //主线上哪个点最近
            for(int i=1;i<=2;i++)
            {
                Coordinate Coor = newPolyline.getStartPoint();
                if (i==2)Coor = newPolyline.getEndPoint();
                for(int l=0;l<SubPolylineList.size();l++)
                {
                    Polyline SubPolyline = SubPolylineList.get(l);
                    double ToStartDis = GetDistance(Coor,SubPolyline.getStartPoint());
                    double ToEndDis = GetDistance(Coor,SubPolyline.getEndPoint());
                    if (ToStartDis<NearestDis){NearestDis = ToStartDis;NearestSegmentIndex=l;IfStart=true;IfNearestNewPolylineIdx=i;}
                    if (ToEndDis<NearestDis){NearestDis = ToEndDis;NearestSegmentIndex=l;IfStart=false;IfNearestNewPolylineIdx=i;}
                }
            }

            //与主线进行连接
            if (NearestSegmentIndex!=-1)
            {
                Polyline PL = SubPolylineList.get(NearestSegmentIndex);
                if (IfStart)
                {
                    for(int i=0;i<PL.GetPartAt(0).getVertexList().size();i++)
                    {
                        Coordinate PT = PL.GetPartAt(0).getVertexList().get(i);

                        //与起点最近
                        if (IfNearestNewPolylineIdx==1)
                        {
                            newPolyline.GetPartAt(0).getVertexList().add(0, PT);
                        }
                        if (IfNearestNewPolylineIdx==2)
                        {
                            newPolyline.GetPartAt(0).getVertexList().add(PT);
                        }
                    }
                }
                else
                {
                    for(int i=PL.GetPartAt(0).getVertexList().size()-1;i>=0;i--)
                    {
                        Coordinate PT = PL.GetPartAt(0).getVertexList().get(i);

                        //与起点最近
                        if (IfNearestNewPolylineIdx==1)
                        {
                            newPolyline.GetPartAt(0).getVertexList().add(0, PT);
                        }
                        if (IfNearestNewPolylineIdx==2)
                        {
                            newPolyline.GetPartAt(0).getVertexList().add(PT);
                        }
                    }
                }
            }
            SubPolylineList.remove(NearestSegmentIndex);
        }

        newPolyline.CalEnvelope();
        newPolyline.getLength(true);

        String CoorStr = GetCoorListStr(newPolyline.GetPartAt(0).getVertexList());
        return newPolyline;
    }



    /**
     * 以画线的方式分割线
     * @param PL
     * @param SplitPointList
     * @return
     */
    public static List<Polyline> StartSplit(Polyline PL,List<Coordinate> SplitPointList)
    {
        Polyline clipPolyline = new Polyline();
        Part clipPart = new Part(SplitPointList);
        clipPolyline.AddPart(clipPart);

        //1、计算边线与分割线的交点
        List<Coordinate> m_InterPointList = new ArrayList<Coordinate>();
        for(int p = 0;p < PL.getPartCount();p++)
        {
            List<Coordinate> LSInterPointList = new ArrayList<Coordinate>();
            boolean OK = clipPolyline.Intersect(PL.GetPartAt(p).GetBorder(),LSInterPointList);
            if (OK)
            {
                for(Coordinate Coor:LSInterPointList)m_InterPointList.add(Coor);
            }
        }

        if (m_InterPointList.size()!=1) return null;
        Polyline PL1=new Polyline(),PL2 = new Polyline();
        if (PL.Split(m_InterPointList.get(0), PL1, PL2))
        {
            List<Polyline> plList = new ArrayList<Polyline>();
            plList.add(PL1);plList.add(PL2);
            return plList;
        }
        return null;
    }

    private static double GetDistance(Coordinate P1,Coordinate P2)
    {
        return Math.sqrt((P1.getX() - P2.getX()) * (P1.getX() - P2.getX()) + (P1.getY() - P2.getY()) * (P1.getY() - P2.getY()));
    }
    private static String GetCoorListStr(List<Coordinate> CoorList)
    {
        List<String> strList = new ArrayList<String>();
        for(Coordinate Coor:CoorList)
        {
            strList.add(Coor.ToString());
        }
        return Tools.JoinT("\r\n", strList);
    }
}
