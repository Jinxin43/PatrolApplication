package com.DingTu.Project;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
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

public class BKLayerExplorer {

    private ProjectDB m_ProjectDB = null;

    /**
     * 绑定工程操作类
     * @param projectDB
     */
    public void SetBindProjectDB(ProjectDB projectDB)
    {
        this.m_ProjectDB = projectDB;
    }

    //背景矢量图
    private BKVectorLayerExplorer m_BKVectorLayerExplorer = new BKVectorLayerExplorer();
    public BKVectorLayerExplorer GetVectorLayerExplorer(){return this.m_BKVectorLayerExplorer;}

    //背景栅格图
    private BKGridLayerExplorer m_BKGridLayerExplorer = new BKGridLayerExplorer();
    public BKGridLayerExplorer GetGridLayerExplorer(){return this.m_BKGridLayerExplorer;}

    /**
     * 打开底图数据库，在DoEvent_Project.DoCommand("工程_打开")中调用
     */
    public void OpenBKDataSource()
    {
        this.m_BKVectorLayerExplorer.OpenVectorDataSource();
        this.m_BKGridLayerExplorer.OpenGridDataSource();
        //this.m_BKVectorLayerExplorer.SetBKVisible(true);
        this.m_BKGridLayerExplorer.SetBKVisible(true);
        this.m_BKVectorLayerExplorer.SetVectorSelectable(false);
    }

    /**
     * 加载此工程对应的底图图层名称，在v1_ProjectDB.OpenProject()中调用，加载之后才调用OpenBKDataSource();
     * Type text,  矢量 、栅格
     * BKMapFile text, 文件名
     * MinX text,MinY text,MaxX text,MaxY text,  最大范围
     * CoorSystem text, 坐标系统
     * Transparent text,	透明度
     * Sort text,	排序号
     * Visible text,	可见性
     * F1 text,F2 text,F3 text,F4 text,F5 text,F6 text,F7 text,F8 text,F9 text,F10 text)	备作字段
     *
     */
    public void LoadBKLayer()
    {
        //读取此工程对应的背景图层信息
        String[] BKMapTypeList = {"矢量","栅格"};
        for(String BKMapType:BKMapTypeList)
        {
            String SQL = "select * from T_BKLayer where Type='%1$s' order by Sort";
            SQL = String.format(SQL, BKMapType);
            SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query(SQL);
            if (DR==null) return;
            List<HashMap<String,Object>> BkMapFileList = new ArrayList<HashMap<String,Object>>();
            while(DR.Read())
            {
                HashMap<String,Object> ho = new HashMap<String,Object>();
                ho.put("Type", "");   //背景图类型：矢量、栅格
                ho.put("BKMapFile", "");
                ho.put("MinX", "");
                ho.put("MinY", "");
                ho.put("MaxX", "");
                ho.put("MaxY", "");
                ho.put("CoorSystem", "");
                ho.put("Transparent", "");
                ho.put("Sort", "");
                ho.put("Visible", "");
                for(String key:ho.keySet())ho.put(key, DR.GetString(key));
                String path = DR.GetString("F1");//存储路径
                if(path == null || path.length() == 0)
                {
                    path = PubVar.m_SysAbsolutePath+"/Map/";
                }
                ho.put("F1",path);
                ho.put("Select",true);
                BkMapFileList.add(ho);
            }DR.Close();
            if (BKMapType.equals("矢量"))this.m_BKVectorLayerExplorer.SetBKFileList(BkMapFileList);
            if (BKMapType.equals("栅格"))this.m_BKGridLayerExplorer.SetBKFileList(BkMapFileList);
        }
    }

    /**
     * 保存底图文件名称
     * @return
     */
    public boolean SaveBKLayer(String BKMapType,List<HashMap<String,Object>> BKMapFileList)
    {
        if (BKMapType.equals("矢量"))
        {
            //保存底图文件信息
            String[] FieldList = {"Type","BKMapFile","MinX","MinY","MaxX","MaxY","CoorSystem","Transparent","Sort","Visible","F1"};
            String SQL_DEL = "delete from T_BKLayer where Type = '矢量'";

            //保存并更新
            if (this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_DEL))
            {
                String SQL_INS = "insert into T_BKLayer (%1$s) values ('%2$s')";
                for(HashMap<String,Object> ho:BKMapFileList)
                {
                    SQL_INS = "insert into T_BKLayer (%1$s) values ('%2$s')";

                    List<String> ValueList = new ArrayList<String>();
                    for(String field:FieldList)ValueList.add(ho.get(field)+"");
                    SQL_INS = String.format(SQL_INS,Tools.Joins(",", FieldList), Tools.JoinT("','", ValueList));
                    this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_INS);
                }
            } else return false;
            this.m_BKVectorLayerExplorer.SetBKFileList(BKMapFileList);
            this.m_BKVectorLayerExplorer.ClearVectorLayer();
            this.m_BKVectorLayerExplorer.OpenVectorDataSource();
            return true;
        }
        if (BKMapType.equals("栅格"))
        {
            this.m_BKGridLayerExplorer.SetBKFileList(BKMapFileList);
            if (this.m_BKGridLayerExplorer.SaveBKLayer())
            {
                this.m_BKGridLayerExplorer.OpenGridDataSource();
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两个列表是否相同
     * @param list1
     * @param list2
     * @return
     */
    private boolean ListEqual(List<HashMap<String,Object>> list1,List<HashMap<String,Object>> list2)
    {
        if (list1==null && list2==null) return true;
        if (list1==null || list2==null) return false;
        if (list1.size()!=list2.size()) return false;
        for(int i=0;i<list1.size();i++)
        {
            if (!list1.get(i).equals(list2.get(i))) return false;
        }
        return true;
    }

    /**
     * 转换背景实体到列表
     * @param josnObjectStr
     * @return
     */
    private List<HashMap<String,Object>> JSONObjectToList(String josnObjectStr)
    {
        List<HashMap<String,Object>> BKMapFileList = new ArrayList<HashMap<String,Object>>();
        try
        {
            JSONTokener jsonParser = new JSONTokener(josnObjectStr);
            JSONObject AllLayerJSON = (JSONObject)jsonParser.nextValue();

            // 接下来的就是JSON对象的操作了
            JSONArray lyrJSONList = AllLayerJSON.getJSONArray("AllBKMapList");
            for(int i=0;i<lyrJSONList.length();i++)
            {
                HashMap<String,Object> hmObj = new HashMap<String,Object>();
                JSONObject lyrJSON = lyrJSONList.getJSONObject(i);
                hmObj.put("Select", lyrJSON.getBoolean("Select"));				//是否选择，默认true
                hmObj.put("GridTransparent", (lyrJSON.has("GridTransparent")?lyrJSON.getString("GridTransparent"):255));//栅格图的透明度
                hmObj.put("MapFileName", lyrJSON.getString("MapFileName"));		//背景图文件名称（矢量[*.vmx]，栅格[*.imx])
                hmObj.put("MinX", lyrJSON.getString("MinX"));					//最大范围，对于栅格图用它做为判断是否加载的依据
                hmObj.put("MinY", lyrJSON.getString("MinY"));
                hmObj.put("MaxX", lyrJSON.getString("MaxX"));
                hmObj.put("MaxY", lyrJSON.getString("MaxY"));
                hmObj.put("CoorSystem", lyrJSON.getString("CoorSystem"));		//坐标系统，格式形如：西安80【129】，或WGS84
                BKMapFileList.add(hmObj);
            }
        } catch (JSONException ex) {
            return null;
        }
        return BKMapFileList;
    }

    /**
     * 将背景列表转换为String
     * @return
     */
    public static String ListToJSONObject(List<HashMap<String,Object>> BKMapFileList)
    {
        try
        {
            JSONObject LyrAllJSON = new JSONObject();
            JSONArray LyrJSONList = new JSONArray();

            for(HashMap<String,Object> bmMapFile:BKMapFileList)
            {
                JSONObject LyrJSON = new JSONObject();
                LyrJSON.put("Select", true);
                LyrJSON.put("GridTransparent", bmMapFile.get("GridTransparent"));
                LyrJSON.put("MapFileName", bmMapFile.get("MapFileName"));
                LyrJSON.put("MinX", bmMapFile.get("MinX")==null?0:bmMapFile.get("MinX"));
                LyrJSON.put("MinY", bmMapFile.get("MinY")==null?0:bmMapFile.get("MinY"));
                LyrJSON.put("MaxX", bmMapFile.get("MaxX")==null?0:bmMapFile.get("MaxX"));
                LyrJSON.put("MaxY", bmMapFile.get("MaxY")==null?0:bmMapFile.get("MaxY"));
                LyrJSON.put("CoorSystem", bmMapFile.get("CoorSystem")==null?"":bmMapFile.get("CoorSystem"));
                LyrJSONList = LyrJSONList.put(LyrJSON);
            }
            LyrAllJSON.put("AllBKMapList", LyrJSONList);
            return LyrAllJSON.toString();
        } catch (JSONException ex) {
            return "";
        }

    }
}
