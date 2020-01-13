package com.DingTu.Project;

import android.util.Log;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Envelope;
import com.DingTu.Dataset.DataSource;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Dataset.SQLiteDataReader;
import com.DingTu.Enum.lkDatasetSourceType;
import com.DingTu.Enum.lkGeoLayersType;
import com.DingTu.Enum.lkRenderType;
import com.DingTu.Layer.GeoLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class BKVectorLayerExplorer {

    //底图数据源对应的图层列表
    private List<Layer> m_LayerList = new ArrayList<Layer>();
    public List<Layer> GetLayerList(){return this.m_LayerList;}

    //工程对应的矢量底图文件列表
    private List<HashMap<String,Object>> m_BKFileLlist = new ArrayList<HashMap<String,Object>>();
    public List<HashMap<String,Object>> GetBKFileList(){return this.m_BKFileLlist;}
    public String GetBKFileListStr()
    {
        List<String> bkFileList = new ArrayList<String>();
        for(HashMap<String,Object> hmObj:m_BKFileLlist)
        {
            bkFileList.add(hmObj.get("MapFileName")+"");
        }
        return "【"+bkFileList.size()+"】"+Tools.JoinT(",", bkFileList);
    }
    public void SetBKFileList(List<HashMap<String,Object>> bkFileList){this.m_BKFileLlist=bkFileList;}

    private boolean m_VectorVisible = true;
    public boolean GetBKVisible(){return this.m_VectorVisible;}

    /**
     * 设置矢量图层可见性
     * @param visible
     */
    public void SetBKVisible(boolean visible)
    {
        this.m_VectorVisible = visible;
        //整理可见性问题
        Log.d("LMGK", "设置底图可见性："+this.m_LayerList.size());
        for(int idx=0;idx<this.m_LayerList.size();idx++)
        {
            Layer vLayer = this.m_LayerList.get(idx);
            vLayer.SetVisible(visible);
            PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerById(vLayer.GetLayerID()).setVisible(visible);
        }
    }

    public void SetVectorSelectable(boolean isSelect)
    {
        Log.d("LMGK", "设置矢量地图可选择性："+this.m_LayerList.size());
        for(int idx=0;idx<this.m_LayerList.size();idx++)
        {
            Layer vLayer = this.m_LayerList.get(idx);
            vLayer.SetSelectable(isSelect);
            PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerById(vLayer.GetLayerID()).setSelectable(isSelect);
        }
    }

    /**
     * 检查指定的图层ID是否存在
     * @param LayerID
     * @return
     */
    public boolean ExistLayerByID(String LayerID)
    {
        for(Layer vLayer : this.m_LayerList)
        {
            if (vLayer.GetLayerID().equals(LayerID)) return true;
        }
        return false;
    }

    /**
     * 根据图层ID返回图层
     * @param LayerID
     * @return
     */
    public Layer GetLayerByID(String LayerID)
    {
        for(Layer vLayer : this.m_LayerList)
        {
            if (vLayer.GetLayerID().toUpperCase().equals(LayerID.toUpperCase())) return vLayer;
        }
        return null;
    }

    /**
     * 打开矢量底图数据源
     * 1-矢量背景，创建Dataset
     */
    public void OpenVectorDataSource()
    {
        if (this.m_BKFileLlist==null){this.ClearVectorLayer();return;}
        if (this.m_BKFileLlist.size()==0){this.ClearVectorLayer();return;}

        //清空原有的底图图层信息
        this.ClearVectorLayer();

        //打开矢量数据底图
        for(HashMap<String,Object> hmObj:this.m_BKFileLlist)
        {
            String path = hmObj.get("F1")+"";
            if(path.isEmpty())
            {
                path=PubVar.m_SysAbsolutePath+"/Map/";
            }
            String VectorFileFullName = path+"/"+hmObj.get("BKMapFile")+"";
            if (Tools.ExistFile(VectorFileFullName))
            {
                Log.d("DT", "开始加载新的矢量底图："+VectorFileFullName);
                DataSource pDataSource = new DataSource(VectorFileFullName);
                pDataSource.setEditing(false);
                PubVar.m_Workspace.GetDataSourceList().add(pDataSource);

                //读取底图数据源对应的图层信息，形成了List<Layer>
                List<Layer> vLyrList = this.LoadLayerForVectorBKDataSource(pDataSource);

                //创建数据集Dataset
                for(Layer vLayer:vLyrList)
                {
                    Envelope pEnv = new Envelope(vLayer.GetMinX(),vLayer.GetMaxY(),vLayer.GetMaxX(),vLayer.GetMinY());
                    Dataset pDataset = new Dataset(pDataSource);
                    pDataset.setSourceType(lkDatasetSourceType.enBackgroundData);
                    pDataset.setId(vLayer.GetLayerID());
                    pDataset.setType(vLayer.GetLayerType());
                    pDataset.setEnvelope(pEnv);
                    pDataset.GetMapCellIndex().setEnvelope(pEnv);
                    pDataSource.getDatasets().add(pDataset);

                    this.m_LayerList.add(vLayer);
                }

                //创建GeoLayer，并渲染
                for(Layer vLayer:vLyrList)
                {
                    PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForAdd(vLayer);
                }

                //设置矢量数据偏移量
                HashMap<String,Object> OffsetParam = this.ReadVectorOffset(pDataSource);
                this.SetOffset(pDataSource,Double.parseDouble(OffsetParam.get("OffsetX")+""),Double.parseDouble(OffsetParam.get("OffsetY")+""));
            }
        }
    }



    /**
     * 清空背景底图图层
     */
    public void ClearVectorLayer()
    {
        this.m_LayerList.clear();
        PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).Clear();
        List<DataSource> pDataSourceList = this.GetBKDataSourceList();
        for(DataSource pDataSource:pDataSourceList)
        {
            pDataSource.getDatasets().clear();
            PubVar.m_Workspace.GetDataSourceList().remove(pDataSource);
        }
    }

    /**
     * 保存底图矢量图层的配置信息
     * @param pLayer
     */
    public boolean SaveVectorLayerInfo(Layer pLayer)
    {
        GeoLayer pGeoLayer = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorBackground).GetLayerById(pLayer.GetLayerID());
        DataSource pDataSource = pGeoLayer.getDataset().getDataSource();
        String SQL = "Update T_Layer set Transparent='%2$s',Visible='%3$s' where LayerID='%1$s'";
        SQL = String.format(SQL, pLayer.GetLayerID(),pLayer.GetTransparet(),pLayer.GetVisible());
        Log.d("LMGK", "保存底图矢量图层设置："+SQL);
        if(pDataSource.ExcuteSQL(SQL))
        {
            for(Layer layer:this.m_LayerList)
            {
                if(layer.GetLayerID().equals(pLayer.GetLayerID()))
                {
                    layer = pLayer;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean SaveVectorBKLayer()
    {
        //保存底图文件信息
        String[] FieldList = {"Type","BKMapFile","MinX","MinY","MaxX","MaxY","CoorSystem","Transparent","Sort","Visible"};
        String SQL_DEL = "delete from T_BKLayer where Type = '%1$s'";

        //保存并更新
        SQL_DEL = String.format(SQL_DEL,"矢量");

        if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_DEL))
        {
            for(HashMap<String,Object> ho:this.m_BKFileLlist)
            {
                String SQL_INS = "insert into T_BKLayer (%1$s) values ('%2$s')";
                List<String> ValueList = new ArrayList<String>();
                for(String field:FieldList)ValueList.add(ho.get(field)+"");
                SQL_INS = String.format(SQL_INS,Tools.Joins(",", FieldList),Tools.JoinT("','", ValueList));
                boolean OK = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_INS);
                if (!OK) return false;
            }
        } else return false;
        return true;
    }

    /**
     * 读取底图数据源内的图层信息
     */
    private List<Layer> LoadLayerForVectorBKDataSource(DataSource BKDataSource)
    {
        List<Layer> vLayerList = new ArrayList<Layer>();

        //读取底图数据源内对应的图层信息
        SQLiteDataReader DR = BKDataSource.Query("select * from T_Layer order by Id");
        if (DR==null) return vLayerList;
        while(DR.Read())
        {
            Layer lyr = new Layer();
            lyr.SetLayerAliasName(DR.GetString("Name"));     								//图层名称，汉字
            lyr.SetLayerID(DR.GetString("LayerId"));    									//图层Id
            lyr.SetLayerTypeName(DR.GetString("Type"));  									//图层类型（点，线，面）
            lyr.SetVisible(Boolean.parseBoolean(DR.GetString("Visible")));					//可见性
            lyr.SetTransparent(Integer.parseInt(DR.GetString("Transparent")));  			//透明度（面层）
            lyr.SetVisibleScaleMin(Double.parseDouble(DR.GetString("VisibleScaleMin")));  	//最小可见比例
            lyr.SetVisibleScaleMax(Double.parseDouble(DR.GetString("VisibleScaleMax")));  	//最大可见比例

            lyr.SetFieldList(DR.GetString("FieldList"));									//字段列表
            lyr.SetIfLabel(Boolean.parseBoolean(DR.GetString("IfLabel")));					//是否标注
            lyr.SetLabelDataField(DR.GetString("LabelField"));								//标注字段
            lyr.SetLabelFont(DR.GetString("LabelFont"));									//标注样式

            lyr.SetMinX(Double.parseDouble(DR.GetString("MinX")));							//外接矩形
            lyr.SetMinY(Double.parseDouble(DR.GetString("MinY")));
            lyr.SetMaxX(Double.parseDouble(DR.GetString("MaxX")));
            lyr.SetMaxY(Double.parseDouble(DR.GetString("MaxY")));

            lyr.SetSelectable(Boolean.parseBoolean(DR.GetString("Selectable")));			//是否可选择
            lyr.SetEditable(Boolean.parseBoolean(DR.GetString("Editable")));				//是否可编辑
            lyr.SetSnapable(Boolean.parseBoolean(DR.GetString("Snapable")));				//是否可捕捉

            String renderType = DR.GetString("RenderType");               					//渲染类型1-单值，2-多值
            if (renderType.equals("2"))  //多值符号
            {
                lyr.SetRenderType(lkRenderType.enUniqueValue);
                String UVF = DR.GetString("UniqueValueField");
                String UVL = DR.GetString("UniqueValueList");
                String USL = DR.GetString("UniqueSymbolList");
                lyr.GetUniqueSymbolInfoList().put("UniqueValueField",Tools.JSONStrToList(UVF));  			//唯一值字段
                lyr.GetUniqueSymbolInfoList().put("UniqueValueList",Tools.JSONStrToList(UVL));				//唯一值列表
                lyr.GetUniqueSymbolInfoList().put("UniqueSymbolList", Tools.JSONStrToList(USL));  		 	//唯一值符号列表
                lyr.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", DR.GetString("UniqueDefaultSymbol"));    	//唯一值缺省符号
            } else lyr.SetSimpleSymbol(DR.GetString("SimpleRender"));

            vLayerList.add(lyr);
        }DR.Close();
        return vLayerList;
    }

    //底图偏移量
    private double m_OffsetX = 0,m_OffsetY = 0;
    public double GetOffsetX(){return this.m_OffsetX;}
    public double GetOffsetY(){return this.m_OffsetY;}

    /**
     * 设置偏移量
     * @param offsetX 实际值（米）
     * @param offsetY 实际值（米）
     */
    private void SetOffset(DataSource pDataSource,double offsetX,double offsetY)
    {
        //double H1 = PubVar.m_Map.getViewConvert().getZoom();
        //double H2 = Tools.GetTwoPointDistance(PubVar.m_Map.getExtend().getMinX(),PubVar.m_Map.getExtend().getMaxY(),
        //									  PubVar.m_Map.getExtend().getMinX(),PubVar.m_Map.getExtend().getMinY());

        double S = 1;
        if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName().equals("WGS-84坐标"))S = 1.424;

        this.m_OffsetX = offsetX;this.m_OffsetY = offsetY;
        for(Dataset pDatset :pDataSource.getDatasets())
        {
            if (pDatset!=null)pDatset.SetOffset(this.m_OffsetX*S,this.m_OffsetY*S);
        }
    }

    /**
     * 保存矢量数据的偏移量
     * @param OffsetX  实际值（米）
     * @param OffsetY 实际值（米）
     * @return
     */
    public boolean SaveVectorOffset(double OffsetX,double OffsetY)
    {
        boolean OK = true;
        List<DataSource> pDataSourceList = this.GetBKDataSourceList();
        for(DataSource pDataSource:pDataSourceList)
        {
            String TableName = "T_MyConfig";
            if (this.CheckAndCreateTable(pDataSource,TableName))
            {
                //首先删除
                String SQL = "delete from "+TableName+" where Name='偏移量'";
                if (pDataSource.ExcuteSQL(SQL))
                {
                    SQL = "insert into "+TableName+" (Name,F1,F2) values ('偏移量','%1$s','%2$s')";
                    SQL = String.format(SQL, OffsetX+"",OffsetY+"");
                    if (pDataSource.ExcuteSQL(SQL))
                    {
                        this.SetOffset(pDataSource,OffsetX,OffsetY);
                    } else OK = false;
                }
            }
        }
        return OK;
    }

    /**
     * 读取偏移量
     * @return
     */
    private HashMap<String,Object> ReadVectorOffset(DataSource pDataSource)
    {
        HashMap<String,Object> offsetParam = new HashMap<String,Object>();
        offsetParam.put("OffsetX", 0);offsetParam.put("OffsetY", 0);

        String TableName = "T_MyConfig";

        if (this.CheckAndCreateTable(pDataSource,TableName))
        {
            String SQL = "select F1,F2 from "+TableName+" where Name='偏移量'";
            SQLiteDataReader DR = pDataSource.Query(SQL);
            if (DR!=null)if (DR.Read())
            {
                offsetParam.put("OffsetX", DR.GetString("F1"));
                offsetParam.put("OffsetY", DR.GetString("F2"));
            }DR.Close();
        }

        return offsetParam;
    }

    /**
     * 得到当前背景数据源
     * @return
     */
    private List<DataSource> GetBKDataSourceList()
    {
        List<DataSource> BKDataSourceList = new ArrayList<DataSource>();
        for(DataSource pDataSource :PubVar.m_Workspace.GetDataSourceList())
        {
            if (!pDataSource.getEditing())BKDataSourceList.add(pDataSource);
        }
        return BKDataSourceList;
    }

    /**
     * 是否已经加载数据源
     * @return
     */
    public boolean AlwaysLoadDataSource()
    {
        if (this.GetBKDataSourceList().size()==0) return false;else return true;
    }

    /**
     * 动态创建指定名称的表
     * @param TableName
     * @return
     */
    private boolean CheckAndCreateTable(DataSource pDataSource, String TableName)
    {
        boolean CreateTable = false;
        String SQL = "SELECT COUNT(*) as count FROM sqlite_master WHERE type='table' and name= '"+TableName+"'";
        SQLiteDataReader DR = pDataSource.Query(SQL);
        if (DR==null) CreateTable = true;
        int Count = 0;
        if(DR.Read())Count = Integer.parseInt(DR.GetString("count"));DR.Close();
        if (Count<=0) CreateTable = true;
        if (CreateTable)
        {
            //创建表
            List<String> createSQL = new ArrayList<String>();
            createSQL.add("CREATE TABLE "+TableName+" (");
            createSQL.add("ID integer primary key autoincrement not null default (0),");

            //分不同名称创建表结构
            if (TableName.equals("T_MyConfig"))
            {
                createSQL.add("Name text,");
                for(int i=1;i<=49;i++)createSQL.add("F"+i+" text,");
                createSQL.add("F50 text");
            }createSQL.add(")");
            SQL = Tools.JoinT("\r\n", createSQL);
            return pDataSource.ExcuteSQL(SQL);
        } else return true;
    }

}
