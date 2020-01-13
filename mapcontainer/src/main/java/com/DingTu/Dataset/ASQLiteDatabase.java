package com.DingTu.Dataset;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class ASQLiteDatabase {

    private SQLiteDatabase _SQLiteDatabase = null;
    private String _DatabaseName = "";


    public SQLiteDatabase GetSQLiteDatabase()
    {
        return this._SQLiteDatabase;
    }
    public void setDatabaseName(String value)
    {
        try
        {
            if (_SQLiteDatabase!=null)
                if (_SQLiteDatabase.isOpen())_SQLiteDatabase.close();
            _DatabaseName = value;
            _SQLiteDatabase = SQLiteDatabase.openDatabase(_DatabaseName, null,SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        }
        catch(SQLiteException e)
        {
            String aa = e.getMessage();
        }
    }


    /**
     * 返回记录集
     * @param SQL
     * @return
     */
    public SQLiteDataReader Query(String SQL)
    {
        Cursor CR =  _SQLiteDatabase.rawQuery(SQL, null);
        return new SQLiteDataReader(CR);
    }

    /**
     * 执行SQL语句
     * @param SQL
     * @return
     */
    public boolean ExcuteSQL(String SQL)
    {
        try
        {
            _SQLiteDatabase.execSQL(SQL);
            return true;
        }
        catch (SQLiteException e)
        {
            //TODO:write Log
            //Tools.ShowToast(PubVar.m_DoEvent.m_Context, e.getMessage());
            Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context,e.getMessage());
            //MessageBox.Show("数据更新失败！\r原因:" + e.Message, "系统", MessageBoxButtons.OK, MessageBoxIcon.Exclamation, MessageBoxDefaultButton.Button1);
            return false;
        }
    }

    /**
     * 执行SQL语句
     * @param SQL
     * @param value 参数
     * @return
     */
    public boolean ExcuteSQL(String SQL,Object[] value)
    {
        try
        {
            _SQLiteDatabase.execSQL(SQL, value);
            return true;
        }
        catch (SQLiteException e)
        {
            Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context,"保存失败！\r\n原因："+e.getMessage());
//            MessageBox.Show("数据更新失败！\r原因:" + e.getMessage());
            return false;
        }
    }

    //关闭数据库
    public void Close()
    {
        if (_SQLiteDatabase.isOpen())_SQLiteDatabase.close();
    }
}
