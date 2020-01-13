package com.DingTu.Project;

import com.DingTu.Dataset.ASQLiteDatabase;
import com.DingTu.Dataset.SQLiteDataReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class UserConfigDB_LayerTemplate {

    //数据库操作类
    private ASQLiteDatabase m_SQLiteDatabase = null;

    /**
     * 绑定数据库操作类
     * @param _db
     */
    public void SetBindDB(ASQLiteDatabase _db)
    {
        this.m_SQLiteDatabase = _db;
    }

    /**
     * 保存图层模板信息
     * @param LayerTemplateInfo
     * @return
     */
    public String SaveLayerTemplate(HashMap<String,Object> LayerTemplateInfo)
    {
        //模板名称
        String Name = LayerTemplateInfo.get("Name").toString();
        //创建时间
        String CreateTime = LayerTemplateInfo.get("CreateTime").toString();
        //是否覆盖
        boolean OverWrite = Boolean.parseBoolean(LayerTemplateInfo.get("OverWrite").toString());
        //图层列表
        List<Layer> vLayerList = (List<Layer>)LayerTemplateInfo.get("LayerList");

        //将图层列表转换为JSON
        JSONObject LYRJSON = this.LayerListToJSONObject(vLayerList);
        String LYRStr = LYRJSON.toString();

        //保存
        //2、判读是否有指定名称的模板
        String SQL = "select COUNT(*) as count from T_LayerTemplate where name ='"+Name+"'";
        SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
        int Count = 0;
        if (DR!=null)if(DR.Read())Count = Integer.parseInt(DR.GetString("count"));DR.Close();
        if (Count>0)  //有则更新
        {
            if (!OverWrite)return "已存在同名模板！";
            SQL = "delete from T_LayerTemplate where name='"+Name+"'";
            if (!this.m_SQLiteDatabase.ExcuteSQL(SQL)) return "更新同名模板失败！";
        }

        SQL = "insert into T_LayerTemplate (name,createtime,layerlist) values ('%1$s','%2$s',?)";
        SQL = String.format(SQL,Name,CreateTime);
        Object[] value =new Object[]{LYRStr.getBytes()};
        if (this.m_SQLiteDatabase.ExcuteSQL(SQL, value)) return "OK";
        else return "新增模板失败！";
    }

    /**
     * 读取指定名称的图层模板
     * @param TemplateName
     * @return
     */
    public List<Layer> ReadLayerTemplate(String TemplateName)
    {
        String SQL = "select * from T_LayerTemplate where name='"+TemplateName+"'";
        SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
        if (DR==null)return null;
        String layStr = "";
        if (DR.Read())
        {
            byte[] layerlist = DR.GetBlob("layerlist");
            layStr = new String(layerlist);
        }DR.Close();

        if (layStr.equals("")) return null;
        return this.JSONObjectToLayerList(layStr);
    }

    /**
     * 读取图层模板名称列表
     * @param TemplateType 系统，用户，全部
     * @return List<模板名称【创建时间】
	 */
    public List<String> ReadTemplateList(String TemplateType)
    {
        String whereTemp = "1=1";
        if (TemplateType.equals("系统"))whereTemp = "name='系统默认图层模板'";
        if (TemplateType.equals("用户"))whereTemp = "name<>'系统默认图层模板'";
        List<String> nameList = new ArrayList<String>();
        String SQL = "select name,createtime from T_LayerTemplate where %1$s order by id desc";
        SQL = String.format(SQL, whereTemp);
        SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
        if (DR==null)return nameList;
        while(DR.Read())
        {
            String name = DR.GetString("name");
            String time = DR.GetString("createtime");
            nameList.add(name+"【"+time+"】");
        }DR.Close();
        return nameList;
    }

    /**
     * 删除指定名称的模板
     * @param tempName
     * @return
     */
    public boolean DeleteTemplateByName(String tempName)
    {
        String SQL = "delete from T_LayerTemplate where name='"+tempName+"'";
        return this.m_SQLiteDatabase.ExcuteSQL(SQL);
    }

    /**
     * 将JSONObject转换为图层列表
     * @param josnObject
     * @return
     */
    private List<Layer> JSONObjectToLayerList(String josnObjectStr)
    {
        List<Layer> vLayerList = new ArrayList<Layer>();
        try
        {
            JSONTokener jsonParser = new JSONTokener(josnObjectStr);
            JSONObject AllLayerJSON = (JSONObject)jsonParser.nextValue();

            // 接下来的就是JSON对象的操作了
            JSONArray lyrJSONList = AllLayerJSON.getJSONArray("AllLayer");
            for(int i=0;i<lyrJSONList.length();i++)
            {
                Layer vLayer = new Layer();
                JSONObject lyrJSON = lyrJSONList.getJSONObject(i);
                vLayer.SetLayerAliasName(lyrJSON.getString("Name"));
                vLayer.SetLayerID(lyrJSON.getString("LayerId"));
                vLayer.SetLayerTypeName(lyrJSON.getString("Type"));
                vLayer.SetVisible(lyrJSON.getBoolean("Visible"));
                vLayer.SetTransparent(lyrJSON.getInt("Transparent"));
                vLayer.SetIfLabel(lyrJSON.getBoolean("IfLabel"));
                vLayer.SetLabelDataField(lyrJSON.getString("LabelField"));
                vLayer.SetLabelFont(lyrJSON.getString("LabelFont"));
                vLayer.SetLabelScaleMin(lyrJSON.getDouble("LabelScaleMin"));
                vLayer.SetLabelScaleMax(lyrJSON.getDouble("LabelScaleMax"));
                vLayer.SetFieldList(lyrJSON.getString("FieldList"));
                vLayer.SetVisibleScaleMin(lyrJSON.getDouble("VisibleScaleMin"));
                vLayer.SetVisibleScaleMax(lyrJSON.getDouble("VisibleScaleMax"));
                vLayer.SetSelectable(lyrJSON.getBoolean("Selectable"));
                vLayer.SetEditable(lyrJSON.getBoolean("Editable"));
                vLayer.SetSnapable(lyrJSON.getBoolean("Snapable"));
                vLayer.SetRenderTypeInt(lyrJSON.getInt("RenderType"));
                vLayer.SetSimpleSymbol(lyrJSON.getString("SimpleRender"));
                String projectType = lyrJSON.getString("F1");
                if(projectType == null || projectType.isEmpty())
                {
                    projectType ="自定义工程";
                }
                vLayer.SetLayerProjectType(lyrJSON.getString("F1"));

                vLayer.GetUniqueSymbolInfoList().put("UniqueValueField", this.JSONArrayToList(lyrJSON.getJSONArray("UniqueValueField")));
                vLayer.GetUniqueSymbolInfoList().put("UniqueValueList", this.JSONArrayToList(lyrJSON.getJSONArray("UniqueValueList")));
                vLayer.GetUniqueSymbolInfoList().put("UniqueSymbolList", this.JSONArrayToList(lyrJSON.getJSONArray("UniqueSymbolList")));
                vLayer.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", lyrJSON.getString("UniqueDefaultSymbol"));
                vLayerList.add(vLayer);
            }
        } catch (JSONException ex) {
            return null;
        }
        return vLayerList;
    }

    /**
     * 将图层列表转换为JSONObject
     * @return
     */
    private JSONObject LayerListToJSONObject(List<Layer> vLayerList)
    {
        try
        {
            JSONObject LyrAllJSON = new JSONObject();
            JSONArray LyrJSONList = new JSONArray();

            for(Layer vLayer:vLayerList)
            {
                JSONObject LyrJSON = new JSONObject();
                LyrJSON.put("LayerId", vLayer.GetLayerID());
                LyrJSON.put("Name", vLayer.GetLayerAliasName());
                LyrJSON.put("Type", vLayer.GetLayerTypeName());
                LyrJSON.put("Visible", vLayer.GetVisible());
                LyrJSON.put("Transparent", vLayer.GetTransparet());
                LyrJSON.put("IfLabel", vLayer.GetIfLabel());
                LyrJSON.put("LabelField", vLayer.GetLabelFieldStr());
                LyrJSON.put("LabelFont", vLayer.GetLabelFont());
                LyrJSON.put("LabelScaleMin", vLayer.GetLabelScaleMin());
                LyrJSON.put("LabelScaleMax", vLayer.GetLabelScaleMax());
                LyrJSON.put("FieldList", vLayer.GetFieldListJsonStr());
                LyrJSON.put("VisibleScaleMin", vLayer.GetVisibleScaleMin());
                LyrJSON.put("VisibleScaleMax", vLayer.GetVisibleScaleMax());

                LyrJSON.put("Selectable", vLayer.GetSelectable());
                LyrJSON.put("Editable", vLayer.GetEditable());
                LyrJSON.put("Snapable", vLayer.GetSnapable());

                LyrJSON.put("RenderType", vLayer.GetRenderTypeInt());
                LyrJSON.put("SimpleRender", vLayer.GetSimpleSymbol());
                LyrJSON.put("F1", vLayer.GetLayerProjecType());
                LyrJSON.put("UniqueValueField", this.ListToJSONArray((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueField")));
                LyrJSON.put("UniqueValueList", this.ListToJSONArray((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueValueList")));
                LyrJSON.put("UniqueSymbolList", this.ListToJSONArray((List<String>)vLayer.GetUniqueSymbolInfoList().get("UniqueSymbolList")));
                LyrJSON.put("UniqueDefaultSymbol", vLayer.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol"));

                LyrJSONList = LyrJSONList.put(LyrJSON);
            }
            LyrAllJSON.put("AllLayer", LyrJSONList);
            return LyrAllJSON;
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }

    }

    private JSONArray ListToJSONArray(List<String> list)
    {
        JSONArray jsList = new JSONArray();
        for(String str:list)jsList.put(str);
        return jsList;
    }

    private List<String> JSONArrayToList(JSONArray jsList)
    {
        List<String> list = new ArrayList<String>();
        for(int i=0;i<jsList.length();i++)
        {
            try {
                list.add(jsList.getString(i));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return list;
    }
}
