package com.DingTu.Project;

import com.DingTu.Dataset.ASQLiteDatabase;
import com.DingTu.Dataset.SQLiteDataReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class UserConfigDB_TransformationParam {

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
     * 得取转换参数列表
     * @param ParamType 参数类型：三参，七参，四参
     * @return
     */
    public List<HashMap<String,Object>> GetTransformationParamList(String ParamType)
    {
        if (ParamType.equals(""))ParamType="1=1";else ParamType = "F1='"+ParamType+"'";
        List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();

        String SQL = "select * from T_TransformationParam where %1$s order by ID DESC";
        SQL = String.format(SQL, ParamType);
        SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
        if (DR==null)return dataList;
        while (DR.Read())
        {
            HashMap<String,Object> coorParaObj = new HashMap<String,Object>();

            coorParaObj.put("ID",DR.GetString("ID"));  //ID
            coorParaObj.put("Type",DR.GetString("F1"));  //类型：三参，七参，四参
            coorParaObj.put("DH",DR.GetString("F2"));  //参数说明
            coorParaObj.put("P1",DR.GetString("F3"));  //参数1
            coorParaObj.put("P2",DR.GetString("F4"));  //参数2
            coorParaObj.put("P3",DR.GetString("F5"));  //参数3
            coorParaObj.put("P4",DR.GetString("F6"));  //参数4
            coorParaObj.put("P5",DR.GetString("F7"));  //参数5
            coorParaObj.put("P6",DR.GetString("F8"));  //参数6
            coorParaObj.put("P7",DR.GetString("F9"));  //参数7

            dataList.add(coorParaObj);
        }DR.Close();

        return dataList;
    }

    /**
     * 保存转换参数
     * @param param [ID],[DH],[P1...pn]
     * @return
     */
    public String SaveTransformationParam(HashMap<String,Object> param)
    {
        //1、判读是否有指定ID的参数
        String SQL = "";
        if (param.containsKey("ID"))
        {
            SQL = "select COUNT(*) as count from T_TransformationParam where ID ='"+param.get("ID").toString()+"'";
            SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
            int Count = 0;
            if (DR!=null)if(DR.Read())Count = Integer.parseInt(DR.GetString("count"));DR.Close();
            if (Count>0)  //有则更新
            {
                SQL = "delete from T_TransformationParam where ID ='"+param.get("ID").toString()+"'";
                if (!this.m_SQLiteDatabase.ExcuteSQL(SQL)) return "更新参数失败！";
            }
        }

        SQL = "insert into T_TransformationParam (F1,F2,F3,F4,F5,F6,F7,F8,F9) values ('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s','%8$s','%9$s')";
        SQL = String.format(SQL,param.get("Type").toString(),
                param.get("DH").toString(),
                param.get("P1").toString(),
                param.get("P2").toString(),
                param.get("P3").toString(),
                param.get("P4").toString(),
                param.get("P5").toString(),
                param.get("P6").toString(),
                param.get("P7").toString());
        if (this.m_SQLiteDatabase.ExcuteSQL(SQL)) return "OK";else return "新增参数失败！";
    }

    /**
     * 删除指定ID的转换参数
     * @param ID
     * @return
     */
    public boolean DeleteTransformationParam(String ID)
    {
        //1、判读是否有指定ID的参数
        String SQL = "delete from T_TransformationParam where ID ='"+ID+"'";
        return this.m_SQLiteDatabase.ExcuteSQL(SQL);
    }
}
