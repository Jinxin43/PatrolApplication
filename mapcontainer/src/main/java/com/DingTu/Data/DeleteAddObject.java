package com.DingTu.Data;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Enum.lkGeoLayersType;
import com.DingTu.Enum.lkGeometryStatus;
import com.DingTu.Layer.GeoLayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/7/27.
 */

public class DeleteAddObject {

    public boolean Delete()
    {
        String ProInfo = "";
        for  (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList())
        {
            if (pGeoLayer.getDataset().getDataSource().getEditing() && pGeoLayer.getSelSelection().getCount()>0)
            {
                ProInfo += pGeoLayer.GetAliasName() + "："+pGeoLayer.getSelSelection().getCount()+"个\n";
            }
        }
        if (ProInfo == "")
        {
            Tools.ShowMessageBox("请在可编辑图层中选择需要删除的实体！");
            return true;
        }

        Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context,Tools.ToLocale(ProInfo+"\r\n是否确定删除以上被选择实体？\r\n"), new ICallback(){
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                if (Str.equals("YES"))
                {
                    List<IURDataItem_DeleteAdd> daList = new ArrayList<IURDataItem_DeleteAdd>();
                    for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList())
                    {
                        if (pGeoLayer.getDataset().getDataSource().getEditing() && pGeoLayer.getSelSelection().getCount()>0)
                        {
                            IURDataItem_DeleteAdd da = new IURDataItem_DeleteAdd();
                            da.LayerId = pGeoLayer.getDataset().getId();
                            for(int ObjId:pGeoLayer.getSelSelection().getGeometryIndexList())
                            {
                                da.ObjectIdList.add(ObjId);
                            }
                            daList.add(da);
                        }
                    }
                    Delete(daList,true);
                }
            }});
        return true;
    }

    //删除实体，可恢复
    public void Delete(List<IURDataItem_DeleteAdd> urDeleteAddList,boolean AddIUnRedo)
    {
        for(IURDataItem_DeleteAdd IurDa : urDeleteAddList)
        {
            Dataset pDataset = PubVar.m_Workspace.GetDatasetById(IurDa.LayerId);
            List<Integer> SYS_IDList = IurDa.ObjectIdList;

            //在数据库中打上删除标识，方便以后恢复
            String SQL_Del = "update %1$s set SYS_STATUS='1' where SYS_ID in (%2$s)";
            //String SQL_Del = "delete %1$s where SYS_ID in (%2$s)";
            SQL_Del = String.format(SQL_Del, pDataset.getDataTableName(),Tools.Join(",", SYS_IDList));
            if (pDataset.getDataSource().ExcuteSQL(SQL_Del))
            {
                //更改实体的状态
                for(int SYS_ID:SYS_IDList)
                {
                    if(pDataset.GetGeometry(SYS_ID) != null)
                    {
                        pDataset.GetGeometry(SYS_ID).setStatus(lkGeometryStatus.enDelete);
                    }
                }
            }
        }

        if (AddIUnRedo)
        {
            UnRedoParaStru UnRedoPara = new UnRedoParaStru();
            UnRedoPara.Command = ReUndoCommand.enAddDeleteObject;
            UnRedoDataItem urDataItem = new UnRedoDataItem();
            urDataItem.Type = ReUndoFlag.enUndo;
            for(IURDataItem_DeleteAdd da:urDeleteAddList)urDataItem.DataList.add(da);
            UnRedoPara.DataItemList.add(urDataItem);
            IUnRedo.AddHistory(UnRedoPara);
        }
        //清空选择集合
        PubVar.m_Map.ClearSelection();
        PubVar.m_Map.FastRefresh();
    }
}
