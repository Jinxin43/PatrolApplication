package com.DingTu.Project;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Enum.lkDatasetSourceType;
import com.DingTu.Enum.lkEditMode;
import com.DingTu.Enum.lkGeoLayersType;
import com.DingTu.Enum.lkRenderType;
import com.DingTu.Layer.GeoLayer;
import com.DingTu.Render.SimpleRender;
import com.DingTu.Render.UniqueValueRender;

import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class LayerRenderExplorer {

    public boolean RenderLayerForAdd(Layer vLayer)
    {
        return this.RenderLayer(vLayer, lkEditMode.enNew);
    }
    public boolean RenderLayerForUpdate(Layer vLayer)
    {
        return this.RenderLayer(vLayer, lkEditMode.enEdit);
    }
    /**
     * 更新数据采集图层的符号化信息
     * @param vLayer
     * @param editMode
     * @return
     */
    private boolean RenderLayer(Layer vLayer,lkEditMode editMode)
    {
        //只要增加新的采集图层
        if (editMode==lkEditMode.enNew)
        {
            //创建新的数据集
            Dataset pDataset = PubVar.m_Workspace.GetDatasetById(vLayer.GetLayerID());

            //创建新的GeoLayer
            GeoLayer pGeoLayer = new GeoLayer(PubVar.m_Map);
            pGeoLayer.setId(pDataset.getId());
            pGeoLayer.setDataset(pDataset);
            pDataset.setBindGeoLayer(pGeoLayer);
            if (pDataset.getSourceType()== lkDatasetSourceType.enBackgroundData)
                PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).AddLayer(pGeoLayer);
            if (pDataset.getSourceType()==lkDatasetSourceType.enEditingData)
                PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).AddLayer(pGeoLayer);

            this.RenderLayer(vLayer, lkEditMode.enEdit);
        }
        if (editMode==lkEditMode.enEdit)
        {
            GeoLayer pGeoLayer = PubVar.m_Workspace.GetDatasetById(vLayer.GetLayerID()).getBindGeoLayer();
            this.RenderLayerForGeoLayer(pGeoLayer,vLayer);
        }
        return true;
    }


//	/**
//	 * 更新指定图层的全部标注信息，也就是从数据库中重新检索标注
//	 * @param vLayer
//	 */
//	public void RenderLayerForUpdateAllLabel(v1_Layer vLayer)
//	{
//		DataSource pDataSource = PubVar.m_Workspace.GetDataSourceByEditing();
//		Dataset pDataset = pDataSource.GetDatasetByName(vLayer.GetLayerID());
//		String SQL = "select SYS_ID,"+vLayer.GetLabelDataField() +" from "+vLayer.GetLayerID();
//		SQLiteDataReader DR = pDataSource.Query(SQL);
//		if (DR==null) return;
//		while(DR.Read())
//		{
//			String SYSID = DR.GetString("SYS_ID");
//			String Label = DR.GetString(vLayer.GetLabelDataField());
//			pDataset.GetGeometry(Integer.parseInt(SYSID)).setTag(Label);
//		}DR.Close();
//	}


//	/**
//	 * 渲染底图图层，注意此时的Dataset已经创建了，在v1_BKVectorLayerExplorer.OpenVectorDataSource()中创建的
//	 * @param vLayer
//	 * @return
//	 */
//	public boolean RenderBKLayerForAdd(v1_Layer vLayer)
//	{
//		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(vLayer.GetLayerID());
//        //创建新的GeoLayer
//		GeoLayer pGeoLayer = new GeoLayer(PubVar.m_Map);
//		pGeoLayer.setDataset(pDataset);
//		pGeoLayer.SetAliasName(vLayer.GetLayerName());   //图层别名，一般为汉字
//        pGeoLayer.setName(pDataset.getName());
//        pGeoLayer.setType(pDataset.getType());
//        pDataset.setBindGeoLayer(pGeoLayer);
//        PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).AddLayer(pGeoLayer);
//        this.RenderLayerForGeoLayer(pGeoLayer, vLayer);
//        return true;
//	}
//


    /**
     * 渲染指定的图层
     * @param pGeoLayer
     */
    private void RenderLayerForGeoLayer(GeoLayer pGeoLayer,Layer vLayer)
    {
        pGeoLayer.SetAliasName(vLayer.GetLayerAliasName());								//图层别名，一般为汉字
        pGeoLayer.setId(vLayer.GetLayerID());											//图层ID
        pGeoLayer.setType(vLayer.GetLayerType());										//图层类型
        pGeoLayer.setVisible(vLayer.GetVisible());          							//图层的可见性

        pGeoLayer.setVisibleScaleMin(vLayer.GetVisibleScaleMin());
        pGeoLayer.setVisibleScaleMax(vLayer.GetVisibleScaleMax());

        //渲染信息，此处这样处理的目的是适应【单值与多值】切换
        if (vLayer.GetRenderType()== lkRenderType.enSimple)
        {
            if (pGeoLayer.getRender()==null) pGeoLayer.setRender(new SimpleRender(pGeoLayer));
            else if (pGeoLayer.getRender().getType()==lkRenderType.enUniqueValue)pGeoLayer.setRender(new SimpleRender(pGeoLayer));
        }

        //是否重新读取唯一值
        boolean ReadUniqueValue = false;
        if (vLayer.GetRenderType()==lkRenderType.enUniqueValue)
        {
            if (pGeoLayer.getRender()==null) {pGeoLayer.setRender(new UniqueValueRender(pGeoLayer));ReadUniqueValue=true;}
            else if (pGeoLayer.getRender().getType()==lkRenderType.enSimple){pGeoLayer.setRender(new UniqueValueRender(pGeoLayer));ReadUniqueValue=true;}
        }

        //标注
        boolean ReadLabel = true;   //是否重新读取标注信息
        if (!vLayer.GetIfLabel())ReadLabel = false;
        else
        {
            if (pGeoLayer.getRender().getIfLabel() && pGeoLayer.getRender().getLabelField().equals(vLayer.GetLabelDataFieldStr()))ReadLabel=false;
        }
        pGeoLayer.getRender().setIfLabel(vLayer.GetIfLabel());
        pGeoLayer.getRender().setLabelField(vLayer.GetLabelDataFieldStr());
        pGeoLayer.getRender().setLabelFont(vLayer.GetLabelFont());
        if (ReadLabel) pGeoLayer.getRender().UpdateAllLabel();

        //可选性
        pGeoLayer.setSelectable(vLayer.GetSelectable());

        //渲染信息
        if (pGeoLayer.getRender().getType()==lkRenderType.enSimple)
        {
            SimpleRender SR = (SimpleRender)pGeoLayer.getRender();
            SR.setSymbol(vLayer.GetSimpleSymbol());
            SR.SetSymbolTransparent(vLayer.GetTransparet());
            SR.UpdateSymbolSet();
        }
        if (pGeoLayer.getRender().getType()==lkRenderType.enUniqueValue)
        {
            UniqueValueRender VR = (UniqueValueRender)pGeoLayer.getRender();
            List<String> oldFieldList = VR.GetUniqueValueFieldList();
            List<String> newFieldList = (List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueField");
            if (!Tools.JoinT(",", oldFieldList).equals(Tools.JoinT(",", newFieldList)))ReadUniqueValue=true;
            VR.SetUniqueValueFieldList((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueField"));
            VR.SetUniqueValueList((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueList"));
            VR.SetUniqueSymbolList((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueSymbolList"));
            VR.SetDefaultSymbol(vLayer.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol")+"");
            VR.SetSymbolTransparent(vLayer.GetTransparet());
            if (ReadUniqueValue)VR.UpdateAllUniqueValue();
        }

        //根据新设置的符号更新显示实体
        for(Geometry pGeometry : pGeoLayer.getDataset().GetGeometryList())
        {
            pGeoLayer.getRender().UpdateSymbol(pGeometry);
        }
    }
}
