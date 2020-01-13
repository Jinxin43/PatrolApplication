package com.DingTu.Project;

import com.DingTu.Base.Tools;
import com.DingTu.Dataset.ASQLiteDatabase;
import com.DingTu.Dataset.SQLiteDataReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class UserConfigDB_MyCoordinateSystem {

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
     * 得取我的坐标系列表
     * @return
     */
    public List<HashMap<String,Object>> GetMyCoordinateSystemList()
    {
        List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
        try
        {
            String SQL = "select * from T_MyCoordinateSystem order by ID DESC";
            SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
            if (DR==null)return dataList;
            while (DR.Read())
            {
                HashMap<String,Object> coorParaObj = new HashMap<String,Object>();
                coorParaObj.put("ID", DR.GetString("ID"));
                coorParaObj.put("D1",false);  //是否选中
                coorParaObj.put("D2",DR.GetString("Name"));  //名称
                String josnObjectStr = new String(DR.GetBlob("Para"));
                JSONTokener jsonParser = new JSONTokener(josnObjectStr);
                JSONObject ParaJSON = (JSONObject)jsonParser.nextValue();
                coorParaObj.put("D3", ParaJSON.getString("CoorSystem"));  //坐标系统
                coorParaObj.put("D4", ParaJSON.getString("CenterJX"));   //中央经线
                coorParaObj.put("D5", ParaJSON.getString("TransMethod"));   //椭球转换方法

                coorParaObj.put("CoorSystem", coorParaObj.get("D3"));  //坐标系统
                coorParaObj.put("CenterJX", coorParaObj.get("D4"));  //中央经线
                coorParaObj.put("TransMethod", coorParaObj.get("D5"));  //转换方法

                if (ParaJSON.has("PMTransMethod"))
                    coorParaObj.put("PMTransMethod", ParaJSON.getString("PMTransMethod"));
                else coorParaObj.put("PMTransMethod", "无");

                String[] keys = new String[]{"P31","P32","P33","P34","P35","P41","P42","P43","P44","P71","P72","P73","P74","P75","P76","P77"};
                for(String key:keys)
                {
                    if (ParaJSON.has(key))coorParaObj.put(key, ParaJSON.getString(key));   //转换参数
                    else coorParaObj.put(key, 0);
                }

                dataList.add(coorParaObj);
            }DR.Close();
        }
        catch (JSONException ex)
        {
            throw new RuntimeException(ex);
        }
        return dataList;
    }

    /**
     * 保存新的【我的坐标系】
     * @param Name
     * @param coorSystem
     * @return
     */
    public String SaveMyCoordinateSystem(String Name,HashMap<String,String> coorSystem)
    {
        try
        {
            JSONObject coorParaObj = new JSONObject();
            coorParaObj.put("CoorSystem", coorSystem.get("CoorSystem"));
            coorParaObj.put("CenterJX", coorSystem.get("CenterJX"));
            coorParaObj.put("TransMethod", coorSystem.get("TransMethod"));
            coorParaObj.put("PMTransMethod", coorSystem.get("PMTransMethod"));
            String[] keys = new String[]{"P31","P32","P33","P34","P35","P41","P42","P43","P44","P71","P72","P73","P74","P75","P76","P77"};
            for(String key:keys){coorParaObj.put(key, coorSystem.get(key));}

            //2、判读是否有指定名称的模板
            String SQL = "insert into T_MyCoordinateSystem (Name,CreateTime,Para) values ('%1$s','%2$s',?)";
            SQL = String.format(SQL,Name,Tools.GetSystemDate());
            Object[] value =new Object[]{coorParaObj.toString().getBytes()};
            if (this.m_SQLiteDatabase.ExcuteSQL(SQL, value)) return "OK";else return "新增我的坐标系失败！";
        }
        catch (JSONException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 删除指定ID的坐标系记录
     * @param idList
     * @return
     */
    public boolean DeleteMyCoordinateSystem(List<String> idList)
    {
        String SQL = "delete from T_MyCoordinateSystem where ID in ("+ Tools.JoinT(",", idList)+")";
        return this.m_SQLiteDatabase.ExcuteSQL(SQL);
    }
}
