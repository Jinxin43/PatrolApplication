package com.DingTu.Project;

import com.DingTu.Base.Tools;
import com.DingTu.Dataset.ASQLiteDatabase;
import com.DingTu.Dataset.SQLiteDataReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class UserConfigDB_UserParam {

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
     * 根据指定的配置取配置值列表
     * @param ItemName
     * @return
     */
    public HashMap<String,String> GetUserPara(String ItemName)
    {
        HashMap<String,String> configInfo = null;
        String SQL = "select * from T_UserParam where F1='"+ItemName+"'";
        SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
        if (DR==null)return configInfo;
        while (DR.Read())
        {
            if (configInfo==null)configInfo=new HashMap<String,String>();
            String[] fieldList = DR.GetFieldNameList();
            for(String field:fieldList)
            {
                configInfo.put(field, DR.GetString(field));
            }
        }DR.Close();
        return configInfo;
    }

    /**
     * 保存配置参数
     * @param ItemName
     * @param Param
     * @return
     */
    public boolean SaveUserPara(String ItemName,HashMap<String,String> Param)
    {
        //检查已经存在指定配置项
        boolean UpdateMode = false;
        String SQL = "Select count(*) as TCount from T_UserParam where F1='"+ItemName+"'";
        SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
        if (DR.Read())
        {
            if (Integer.parseInt(DR.GetString("TCount"))==1)UpdateMode=true;
        }DR.Close();



        //提取配置项目的值
        List<String> KeyList = new ArrayList<String>();
        List<String> ValueList = new ArrayList<String>();
        List<String> KeyValueList = new ArrayList<String>();
        Iterator iter = Param.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            KeyList.add(entry.getKey().toString());
            ValueList.add(entry.getValue().toString());
            KeyValueList.add(KeyList.get(KeyList.size()-1)+"='"+ValueList.get(ValueList.size()-1)+"'");
        }

        //构造插入语句
        if (UpdateMode)  //更新
        {
            SQL = "update T_UserParam set %2$s where F1='%1$s'";
            SQL = String.format(SQL,ItemName,Tools.JoinT(",", KeyValueList));
        }
        else
        {
            SQL = "insert into T_UserParam (F1,%2$s) values ('%1$s','%3$s')";
            SQL = String.format(SQL,ItemName, Tools.JoinT(",", KeyList),Tools.JoinT("','", ValueList));
        }


        return this.m_SQLiteDatabase.ExcuteSQL(SQL);
    }


//	/**
//	 * 读取上次打开的工程信息
//	 * @return HashMap<String,String> 格式:Name=工程名称，Time=上次打开时间
//	 */
//	public HashMap<String,String> ReadBeforeOpenProjectInfo()
//	{
//		HashMap<String,String> prjInfo = null;
//		String SQL = "select * from T_UserParam where F1='上次打开工程'";
//		SQLiteDataReader DR = this.m_SQLiteDatabase.Query(SQL);
//		if (DR==null)return prjInfo;
//		while (DR.Read())
//		{
//			prjInfo = new HashMap<String,String>();
//			prjInfo.put("Name", DR.GetString("F2"));
//			prjInfo.put("Time", DR.GetString("F3"));
//		}DR.Close();
//
//		return prjInfo;
//	}
//
//	/**
//	 * 存储上次打开的工程信息
//	 * @param HashMap<String,String> 格式:Name=工程名称，Time=上次打开时间
//	 * @return
//	 */
//	public boolean SaveBeforeOpenProjectInfo(HashMap<String,String> prjInfo)
//	{
//		//删除原有的上次打开工程信息
//		String SQL = "delete from T_UserParam where F1='"+prjInfo.get("Name")+"'";
//		if (!this.m_SQLiteDatabase.ExecuteSQL(SQL)) return false;
//
//		//插入新的上次打开工程信息
//	    SQL = "insert into T_UserParam (F1,F2,F3) values ('%1$s','%2$s','%3$s')";
//	    SQL = String.format(SQL,"上次打开工程",prjInfo.get("Name"),prjInfo.get("Time"));
//	    return this.m_SQLiteDatabase.ExecuteSQL(SQL);
//	}
}
