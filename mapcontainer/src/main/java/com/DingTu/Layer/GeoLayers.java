package com.DingTu.Layer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class GeoLayers {

    private List<GeoLayer> List = new ArrayList<GeoLayer>();

    public List<GeoLayer> getList()
    {
        return List;
    }
    public int size()
    {
        return List.size();
    }

    //加入图层
    public void AddLayer(GeoLayer pGeoLayer)
    {
        List.add(pGeoLayer);
    }

    /**
     * 移除指定索引的图层
     * @param index
     */
    public void RemoveAt(int index)
    {
        List.remove(index);
    }

    /**
     * 移除指定“图层”的图层
     * @param LayerId
     */
    public void Remove(String LayerId)
    {
        List.remove(this.GetLayerById(LayerId));
    }

    /**
     * 移除指定“图层”的图层
     * @param pGeoLayer
     */
    public void Remove(GeoLayer pGeoLayer)
    {
        List.remove(pGeoLayer);
    }


    /**
     * 指定的图层的索引值
     * @param pGeoLayer
     * @return
     */
    public int IndexOf(GeoLayer pGeoLayer)
    {
        return List.indexOf(pGeoLayer);
    }

    /**
     * 指定索引值的图层
     * @param index
     * @return
     */
    public GeoLayer GetLayerByIndex(int index)
    {
        return (GeoLayer)List.get(index);
    }

    /**
     * 指定图层Id的图层
     * @param layerId
     * @return
     */
    public GeoLayer GetLayerById(String layerId)
    {
        for (GeoLayer layer : List)
        {
            if (layer.getId().indexOf(layerId) >= 0) return layer;
        }
        return null;
    }

    /**
     * 改变图层的索引顺序
     * @param GeoLayerName
     * @param newIndex
     */
    public void MoveTo(String GeoLayerName,int newIndex)
    {
        GeoLayer pGeoLayer = this.GetLayerById(GeoLayerName);
        this.List.remove(pGeoLayer);
        this.List.add(newIndex,pGeoLayer);
    }

//    private void InsertLayer(int index, GeoLayer pLayer)
//    {
//        List.add(index, pLayer);
//    }

//    //调整图层的顺序
//    public void SetLayerLevel(int FromIndex, int ToIndex)
//    {
//        GeoLayer pLayer = this.GetLayerByIndex(FromIndex);
//        this.RemoveAt(FromIndex);
//        this.InsertLayer(ToIndex, pLayer);
//    }

//    //调整图层的顺序-向上
//    public boolean MoveLayerUp(String LayerName)
//    {
//        int FromIndex = this.IndexOf(this.GetLayerByName(LayerName));
//        this.SetLayerLevel(FromIndex, FromIndex - 1);
//        return true;
//    }
//
//    //调整图层的顺序-向下
//    public boolean MoveLayerDown(String LayerName)
//    {
//        int FromIndex = this.IndexOf(this.GetLayerByName(LayerName));
//        this.SetLayerLevel(FromIndex, FromIndex + 1);
//        return true;
//    }

//    //重新调整图层顺序
//    public boolean ReJustLayerIndex(List<String> newLayerSort)
//    {
//        List<GeoLayer> GeoList = new ArrayList<GeoLayer>(List.size());
//        for (GeoLayer pGeoLayer : List)
//        {
//            GeoList.add(pGeoLayer);
//        }
//        List.clear();
//
//        for (String LayerName : newLayerSort)
//        {
//            for (GeoLayer pGeoLayer : GeoList)
//            {
//                if (pGeoLayer.getName() == LayerName) List.add(pGeoLayer);
//            }
//        }
//        return true;
//    }


    public void Clear()
    {
        this.List.clear();
    }
}
