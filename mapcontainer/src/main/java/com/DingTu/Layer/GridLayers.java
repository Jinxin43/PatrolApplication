package com.DingTu.Layer;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.DingTu.Enum.lkSelectionType;
import com.DingTu.Enum.lkTextPosition;
import com.DingTu.Map.Map;
import com.DingTu.Symbol.TextSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;

import com.DingTu.Cargeometry.Envelope;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class GridLayers {

    private Map m_Map = null;
    /**
     * 初始化GridLayers对象
     * @param _map
     */
    public GridLayers(Map _map)
    {
        this.m_Map = _map;
    }

    private List<GridLayer> List = new ArrayList<GridLayer>();

    public List<GridLayer> GetList()
    {
        return List;
    }

    /**
     * 获取栅格图层的最大外接矩形，可能有多幅情况
     * @return
     */
    public Envelope GetExtend()
    {
        if (!this._ShowGird) return null;

        //栅格图的最大外接矩形
        Envelope _ExtendForView = new Envelope(0,0,0,0);
        for(HashMap<String,Object> hmObj:this.m_MapFileList)
        {
            double MinX = Double.parseDouble(hmObj.get("MinX")+"");
            double MinY = Double.parseDouble(hmObj.get("MinY")+"");
            double MaxX = Double.parseDouble(hmObj.get("MaxX")+"");
            double MaxY = Double.parseDouble(hmObj.get("MaxY")+"");
            Envelope gridExtend = new Envelope(MinX, MaxY, MaxX, MinY);

            if (_ExtendForView.IsZero())_ExtendForView = gridExtend;
            else _ExtendForView = _ExtendForView.Merge(gridExtend);
        }
        return _ExtendForView;
    }

    //显示
    private boolean _ShowGird = false;
    public void SetShowGrid(boolean visible)
    {
        this._ShowGird = visible;
        for(GridLayer gLayer:this.List)
        {
            gLayer.SetShowGrid(visible);
        }
    }


    //背景底图文件列表
    private List<HashMap<String,Object>> m_MapFileList = null;

    /**
     * 设置需要动态加载的背景底图文件列表，格式详见v1_BKLayerExplorer.SaveBKLayer()
     */
    public void SetMapFileList(List<HashMap<String,Object>> mapFileList)
    {
        if (this.m_MapFileList==null)this.m_MapFileList = new ArrayList<HashMap<String,Object>>();
        this.m_MapFileList.clear();
        for(int i=mapFileList.size()-1;i>=0;i--)
        {
            this.m_MapFileList.add(mapFileList.get(i));
        }
    }

    //列表的最大长度，当超过最大长度后，只显示栅格图的索引信息，也就是栅格图范围框及注记
    private int LIST_MAX = 4;

    public void Refresh()
    {
        if (!this._ShowGird) return;
        //动态标识码
        String DynamicFilterStr = UUID.randomUUID().toString();
        this.m_OnlyShowGridIndex=false;

        //动态判断需要加载哪些栅格图，条件为当前显示范围内包含的栅格图
        if (this.m_MapFileList==null){for(GridLayer gLayer:List)gLayer.UnloadGird();return;}
        this.m_NeedLoadGridFileList.clear();
        List<HashMap<String,Object>> NewNeedLoadGridFileList = new ArrayList<HashMap<String,Object>>();
        for(HashMap<String,Object> hmObj:this.m_MapFileList)
        {
            //栅格图的最大外接矩形
            double MinX = Double.parseDouble(hmObj.get("MinX")+"");
            double MinY = Double.parseDouble(hmObj.get("MinY")+"");
            double MaxX = Double.parseDouble(hmObj.get("MaxX")+"");
            double MaxY = Double.parseDouble(hmObj.get("MaxY")+"");
            Envelope gridExtend = new Envelope(MinX, MaxY, MaxX, MinY);

            //获取当前视图下的最大外接矩形范围
            Envelope evp = this.m_Map.getExtend();

            //判断是否在当前视图范围内
            if (evp.Intersect(gridExtend))
            {
                this.m_NeedLoadGridFileList.add(hmObj);

                //在当前的List<GridLayer>设置动态加载标识，也就是List<GridLayer>已经存在需要显示的
                boolean inList = false;
                for(GridLayer gLayer:this.List)
                {
                    if (gLayer.GetGridDataFile().equals(hmObj.get("MapFileName")+""))
                    {
                        gLayer.DynamicFilterStr = DynamicFilterStr;
                        inList = true;
                    }
                }
                if (!inList)NewNeedLoadGridFileList.add(hmObj);   //这个是需要重新读取的
            }
        }

        //判断是否超过最大显示列表数，超过则只显示图幅索引信息
        if (this.m_NeedLoadGridFileList.size()>this.LIST_MAX)
        {
            //隐藏之前显示过的栅格图
            for(GridLayer gLayer:List)gLayer.ClearAllCache();

            //绘制栅格图幅索引信息
            this.m_OnlyShowGridIndex = true;

        }
        else   //没有超过最大显示列表数，进行动态分配
        {
            int KYGridLayerCount = 0;   //可用的数量
            for(GridLayer gLayer:this.List)if (!gLayer.DynamicFilterStr.equals(DynamicFilterStr)){gLayer.UnloadGird();KYGridLayerCount++;}

            //判断是否需要动态创建
            for(int i=1;i<=NewNeedLoadGridFileList.size()-KYGridLayerCount;i++)
            {
                this.List.add(new GridLayer(this.m_Map));
            }

            //动态分配
            for(HashMap<String,Object> hmObj:NewNeedLoadGridFileList)
            {
                for(GridLayer gLayer:this.List)
                {
                    if (!gLayer.DynamicFilterStr.equals(DynamicFilterStr))
                    {
                        gLayer.DynamicFilterStr = DynamicFilterStr;
                        gLayer.SetGridDataFile(hmObj.get("BKMapFile")+"",hmObj.get("F1")+"");

                        if (hmObj.containsKey("Transparent"))
                        {
                            gLayer.SetTransparent(Integer.parseInt(hmObj.get("Transparent")+""));
                            gLayer.SetShowGrid(Boolean.parseBoolean(hmObj.get("Visible")+""));
                        }
                        break;
                    }
                }
            }

            //刷新显示
            for(GridLayer gLayer:this.List)
            {
                if (gLayer.DynamicFilterStr.equals(DynamicFilterStr))gLayer.Refresh();
            }
        }
    }

    //是否只显示图幅索引信息
    private boolean m_OnlyShowGridIndex = true;
    private List<HashMap<String,Object>> m_NeedLoadGridFileList = new ArrayList<HashMap<String,Object>>();
    public void FastRefresh()
    {
        if (!this._ShowGird) return;
        if (this.m_OnlyShowGridIndex)
        {
            for(HashMap<String,Object> hmObj:this.m_NeedLoadGridFileList)
            {
                this.DrawGridFileIndex(hmObj);
            }
        }
        for(GridLayer gLayer:List)gLayer.FastRefresh();
    }


    private Paint m_Pen = null;
    private TextSymbol m_TextSymbol = null;

    /**
     * 绘制栅格图幅的索引框
     * @param mapFile
     */
    private void DrawGridFileIndex(HashMap<String,Object> mapFile)
    {
        //栅格图的最大外接矩形
        double MinX = Double.parseDouble(mapFile.get("MinX")+"");
        double MinY = Double.parseDouble(mapFile.get("MinY")+"");
        double MaxX = Double.parseDouble(mapFile.get("MaxX")+"");
        double MaxY = Double.parseDouble(mapFile.get("MaxY")+"");
        PointF LT = this.m_Map.getViewConvert().MapToScreenF(MinX,MaxY);
        PointF RB = this.m_Map.getViewConvert().MapToScreenF(MaxX,MinY);

        //绘制最大范围框
        if (this.m_Pen==null)
        {
            this.m_Pen = new Paint();
            this.m_Pen.setStrokeWidth(3);
            this.m_Pen.setColor(Color.RED);
            this.m_Pen.setStyle(Paint.Style.STROKE);
        }
        if (this.m_TextSymbol == null)
        {
            this.m_TextSymbol = new TextSymbol();
        }

        this.m_Map.getDisplayGraphic().drawRect(LT.x, LT.y, RB.x, RB.y, this.m_Pen);

        //绘制图幅名称
        float TextX = (LT.x+RB.x)/2;
        float TextY = (LT.y+RB.y)/2;
        this.m_TextSymbol.Draw(this.m_Map.getDisplayGraphic(), TextX, TextY, mapFile.get("BKMapFile")+"", lkTextPosition.enCenter, lkSelectionType.enShow);
    }
}
