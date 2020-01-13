package com.DingTu.Dataset;

import com.DingTu.CoordinateSystem.CoorSystem;
import com.DingTu.Map.StaticObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class Workspace {

    //初始化工作空间
    public Workspace()
    {
        //全局索引类
        //StaticObject.MapCellIndex = new lkmap.MapCellIndex();
    }

    /**
     * 设置坐标系统信息
     * @param coorSystem  坐标系统类型
     */
    public void SetCoorSystemInfo(CoorSystem coorSystem)
    {
        StaticObject.soProjectSystem.SetCoorSystem(coorSystem);
    }

    /**
     * 根据Id返回数据集
     * @param datasetId
     * @return
     */
    public Dataset GetDatasetById(String datasetId)
    {
        for (DataSource pDataSource : this.GetDataSourceList())
        {
            Dataset pDataset = pDataSource.GetDatasetById(datasetId);
            if (pDataset!=null) return pDataset;
        }
        return null;
    }

    public void SetAllGeoLayerNoSelectable()
    {
        for (DataSource pDataSource : this.GetDataSourceList())
        {
            if (pDataSource.getEditing())
            {
                for(Dataset pDataset:pDataSource.getDatasets())
                {
                    pDataset.getBindGeoLayer().setSelectable(false);
                }
            }
        }
    }

    //释放工作空间
    public void FreeWorkSpace()
    {
        for (DataSource pDataSource : this._DataSourceList)
        {
            pDataSource.Dispose();
        }
        this._DataSourceList.clear();
    }

    //工作空间属性

    //数据源
    private List<DataSource> _DataSourceList = new ArrayList<DataSource>();
    public List<DataSource> GetDataSourceList()
    {
        return _DataSourceList;
    }


    //关闭数据源
    public boolean CloseDataSource(DataSource pDataSource,boolean IfSave)
    {
        //if (IfSave) { pDataSource.Commit(true); }
        pDataSource.Dispose(); this.GetDataSourceList().remove(pDataSource);
        return true;
    }

//    //设置数据源的可编辑性
//    public boolean SetDataSourceEditing(DataSource pDataSource, boolean Editing)
//    {
//        //1、验证当前工作空间中其它数据源的状态，同时只可以编辑一个数据源
//        for (DataSource dDataSource : this.getDataSourceList())
//        {
//            dDataSource.setEditing(false);
//        }
//
//        pDataSource.setEditing(Editing);
//        return true;
//    }


    //根据数据源的名称查找数据源
    public DataSource GetDataSourceByName(String DataSourceName)
    {
        for (DataSource pDataSource : this.GetDataSourceList())
        {
            int FileIndex= pDataSource.getName().lastIndexOf("/")+1;
            int PotIndex =  pDataSource.getName().lastIndexOf(".");if (PotIndex==-1)PotIndex = pDataSource.getName().length();
            String ShortName = pDataSource.getName().substring(FileIndex,PotIndex);
            if (ShortName.equals(DataSourceName)) return pDataSource;
        }
        return null;
    }

//    //根据数据源的实际名称查找数据源
//    public DataSource GetDataSourceByActualName(String DataSourceActualName)
//    {
//        for (DataSource pDataSource : this.getDataSourceList())
//        {
//            if (pDataSource.getActualName() == DataSourceActualName) return pDataSource;
//        }
//        return null;
//    }

    /**
     * 返回当前正在编辑的数据源
     */
    public DataSource GetDataSourceByEditing()
    {
        for (DataSource pDataSource : this.GetDataSourceList())
        {
            if (pDataSource.getEditing()) return pDataSource;
        }
        return null;
    }

//    /**
//     * 返回当前不可编辑的数据源，也就是底图数据源
//     */
//    public DataSource GetDataSourceByUnEditing()
//    {
//        for (DataSource pDataSource : this.GetDataSourceList())
//        {
//            if (!pDataSource.getEditing()) return pDataSource;
//        }
//        return null;
//    }
}
