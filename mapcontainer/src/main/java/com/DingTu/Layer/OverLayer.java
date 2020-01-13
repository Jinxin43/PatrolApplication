package com.DingTu.Layer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.DingTu.Cargeometry.Marker;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class OverLayer {

    //图层名称
    private String _Name = UUID.randomUUID().toString();
    public void SetName(String name)
    {
        this._Name = name;
    }
    public String GetName()
    {
        return this._Name;
    }

    //标记列表
    private List<Marker> _MarkerList = new ArrayList<Marker>();
    public List<Marker> GetMarkerList()
    {
        return _MarkerList;
    }

    //增加标记
    public void AddMarker(Marker _Marker)
    {
        this._MarkerList.add(_Marker);
    }

    //删除标记
    public void RemoveMarker(Marker _Marker)
    {
        this.RemoveMarkerById(_Marker.GetID());
    }
    public void RemoveMarkerById(String _MarkerId)
    {
        int MarkerCount = this._MarkerList.size();
        for(int i=MarkerCount-1;i>=0;i--)
        {
            if (_MarkerList.get(i).GetID().equals(_MarkerId))_MarkerList.remove(i);
        }
    }

    public Marker GetMarker(String _MarkerID)
    {
        for(Marker m:_MarkerList)
        {
            if (m.GetID().equals(_MarkerID)) return m;
        }
        return null;
    }
    //删除所有的标记
    public void RemoveAllMarker()
    {
        this._MarkerList.clear();
    }

    //刷新显示
    public void Refresh()
    {
        for(Marker marker : this._MarkerList)
        {
            marker.Draw();
        }
    }
}
