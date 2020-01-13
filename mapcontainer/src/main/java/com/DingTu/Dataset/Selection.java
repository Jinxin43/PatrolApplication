package com.DingTu.Dataset;

import com.DingTu.Enum.lkGeometryStatus;
import com.DingTu.Enum.lkSelectionType;
import com.DingTu.Symbol.ISymbol;

import java.util.ArrayList;
import java.util.List;

import com.DingTu.Cargeometry.Envelope;
import com.DingTu.Cargeometry.Geometry;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class Selection {
    public Selection() {}

    //基本成员变量
    //选择集中的实体列表
    private List<Integer> _GeometryIndexList = new ArrayList<Integer>();
    public List<Integer> getGeometryIndexList()
    {
        return _GeometryIndexList;
    }

    //选择集的类型
    public lkSelectionType _Type = lkSelectionType.enUnKnow;
    public lkSelectionType getType()
    {
        return _Type;
    }
    public void setType(lkSelectionType value)
    {
        _Type = value;
    }

    //选择集中实体的个数
    public int getCount()
    {
        int Count = 0;
        //此处可能有已经删除的实体，所以需要重新过滤
        for(int SYS_ID:this._GeometryIndexList)
        {
            Geometry pGeometry = this._Dataset.GetGeometry(SYS_ID);
            if (pGeometry==null){Count++;continue;}
            if (pGeometry.getStatus()== lkGeometryStatus.enNormal)Count++;
        }
        return Count;
    }

    //选择的实体样式
    private ISymbol _Style = null;
    public ISymbol getStyle()
    {
        return _Style;
    }
    public void setStyle(ISymbol value)
    {
        _Style = value;
    }

    //选择集所属的数据集
    private Dataset _Dataset = null;
    public Dataset getDataset()
    {
        return _Dataset;
    }

    public void setDataset(Dataset value)
    {
        this._Dataset=value;
        ////根据数据集的类型确定选择集的符号样式
        //if (_Dataset.Type == LKMap.Enum.lkGeoLayerType.enPoint)
        //    this.Style = Stylelib.GetDefaultSymbol(lkSymbolType.enPointSymbol);

        //if (_Dataset.Type == LKMap.Enum.lkGeoLayerType.enPolyline)
        //    this.Style = Stylelib.GetDefaultSymbol(lkSymbolType.enLineSymbol);

        //if (_Dataset.Type == LKMap.Enum.lkGeoLayerType.enPolygon)
        //    this.Style = Stylelib.GetDefaultSymbol(lkSymbolType.enPolygonSymbol);

    }

    //选择集的最大外接矩形
    public Envelope getEnvelope()
    {
        if (this.getGeometryIndexList().size() == 0) return null;
        Envelope pEnvelope = this._Dataset.GetGeometry(this.getGeometryIndexList().get(0)).getEnvelope();
        for (int i = 1; i < this.getGeometryIndexList().size(); i++)
        {
            pEnvelope = pEnvelope.Merge(this._Dataset.GetGeometry(this.getGeometryIndexList().get(i)).getEnvelope());
        }
        return pEnvelope;
    }

    //向选择集中增加新实体
    public boolean Add(Geometry newGeometry)
    {
        return this.Add(newGeometry.getSysId());
    }
    //向选择集中增加新实体
    public boolean Add(int GeometryIndex)
    {
        //判断是否已经在列表中，如果存在则不增加
        if (this.InList(GeometryIndex)) return true;
        this.getGeometryIndexList().add(GeometryIndex);
        return true;
    }

    /**
     * 指定的实体索引是否在列表内部
     * @param GeometryIndex
     * @return
     */
    public boolean InList(int GeometryIndex)
    {
        if (this.getGeometryIndexList().indexOf(GeometryIndex) >= 0) return true; else return false;
    }


    //从选择集中删除指定ObjectID的实体
    public boolean Remove(int GeometryIndex)
    {
        return this.getGeometryIndexList().remove((Integer)GeometryIndex);
    }

    //从选择集中删除指定的实体
    public boolean Remove(Geometry pGeometry)
    {
        return Remove(pGeometry.getSysId());
    }

    //从选择集中删除所有实体
    public boolean RemoveAll()
    {
        _GeometryIndexList.clear(); return true;
    }

    //将选择集转换成Recordset
//    public Recordset ToRecordset()
//    {
//        string SQL = "select * from " + this.Dataset.TableName + " where [SYS_ID] in (";
//        foreach(int ID in this.GeometryIndexList)
//        {
//            SQL+=(ID+1).ToString()+",";
//        }
//        SQL = SQL.Substring(0,SQL.Length-1)+")";
//        return null;// new Recordset(this.Dataset, this.Dataset.DataSource.Query(SQL));
//    }

    //将Recordset转换成Selection
    public boolean FromRecordset() { return true; }

}
