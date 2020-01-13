package com.DingTu.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/5/31.
 * 图层级的网格索引，与Map的全局索引相一致，也就是划成Cells*Cells网格，
 * 在每个网格中间记录着本层实体的索引值，方便查询与检索
 */

public class LayerCellIndex {

    //图层级网格索引，形如:CellIndex[0]=List<ObjectIndex>，解释:CellIndex[网络索引号]=List<实体在列表中的索引号>
    private List<ArrayList<Integer>> _CellIndex = null;

    public List<ArrayList<Integer>> getCellIndex()
    {
        return _CellIndex;
    }
    public void setCellIndex(List<ArrayList<Integer>> value)
    {
        _CellIndex=value;
    }


    public LayerCellIndex()
    {
        //初始化图层的格网索引
        int Cells = 32;
        _CellIndex = new ArrayList<ArrayList<Integer>>(Cells*Cells);
        for (int i = 0; i < Cells * Cells; i++)
        {
            _CellIndex.add(new ArrayList<Integer>());
        }
    }

    //更新指定网格索引中的实体索引值
    public void SetIndex(List<Integer> IndexList, int ObjectIndex)
    {
        for (int IL : IndexList)
        {
            _CellIndex.get(IL).add(ObjectIndex);
        }
    }

    //在图层级的索引中删除指定实体的索引
    public void RemoveIndex(int ObjectIndex)
    {
        for (List<Integer> IdxList : this._CellIndex)
        {
            int idx = IdxList.indexOf(ObjectIndex);
            if (idx >= 0)
            {
                IdxList.remove(idx);
            }
        }
    }

    //更新图层级的指定的实体的索引值
    public void UpdateIndex(List<Integer> IndexList, int ObjectIndex)
    {
        this.RemoveIndex(ObjectIndex);
        this.SetIndex(IndexList, ObjectIndex);
    }

    //清空所有图层级索引
    public void ClearAllIndex()
    {
        for (List<Integer> pIndex : this._CellIndex)
        {
            pIndex.clear();
        }
    }

    //当前视口所跨距的网络索引
    private List<Integer> _CurrentCellIndex = new ArrayList<Integer>();
    public List<Integer> getCurrentCellIndex()
    {
        return _CurrentCellIndex;
    }
    public void setCurrentCellIndex(List<Integer> value)
    {
        _CurrentCellIndex = value;
    }
}
