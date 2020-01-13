package com.DingTu.Dataset;

import android.util.Log;

import com.DingTu.Base.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.DingTu.Cargeometry.Envelope;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class DataSource {

    //初始化数据源
    public DataSource(String DatabaseName)
    {
        this.setName(DatabaseName);
        this.Open();   //打开数据源
    }


    //打开数据源
    public boolean Open()
    {
        _EDatabase = new ASQLiteDatabase();
        _EDatabase.setDatabaseName(this.getName());
        return true;
    }

    //关闭数据源
    public boolean Close()
    {
        _EDatabase.Close();
        return true;
    }

    /**
     * 在TAData.dbx中创建新的数据表
     * @param DatasetID 数据集ID，与v1_Layer.GetLayerID相同
     * @return
     */
    public boolean CreateDataset(String DatasetID)
    {
        //创建(_D)数据表以及(_I)索引表
        List<String> createSQL = new ArrayList<String>();
        createSQL.add("CREATE TABLE "+DatasetID+"_D (");
        createSQL.add("SYS_ID integer primary key autoincrement  not null default (0),");
        createSQL.add("SYS_GEO Blob,");					//图形实体
        createSQL.add("SYS_STATUS int,");				//状态
        createSQL.add("SYS_TYPE varchar(50),");			//实体类型
        createSQL.add("SYS_OID varchar(50),");			//唯一值GUID
        createSQL.add("SYS_LABEL varchar(50),");		//标注值
        createSQL.add("SYS_DATE varchar(50),");			//采集时间
        createSQL.add("SYS_PHOTO Text,");				//相片字段
        createSQL.add("SYS_Length double,");			//长度
        createSQL.add("SYS_Area double,");				//面积
        createSQL.add("SYS_BZ1 Text,");					//备注字段1
        createSQL.add("SYS_BZ2 Text,");					//备注字段2
        createSQL.add("SYS_BZ3 Text,");					//备注字段3
        createSQL.add("SYS_BZ4 Text,");					//备注字段4
        createSQL.add("SYS_BZ5 Text,");					//备注字段5

        for(int i=1;i<=512;i++)
        {
            String FName = "F"+i;
            String FType = "varchar(255) default ''";
            createSQL.add(FName+" "+FType+",");
        }
        String EndStr = createSQL.get(createSQL.size()-1);
        createSQL.remove(createSQL.size()-1);
        createSQL.add(EndStr.substring(0,EndStr.length()-1));
        createSQL.add(")");

        String SQL_D = Tools.JoinT("\r\n", createSQL);
        String SQL_I = "CREATE TABLE "+DatasetID+"_I (" +
                "SYS_ID INTEGER PRIMARY KEY NOT NULL,"+
                "RIndex int,CIndex int,MinX double,MinY double,MaxX double,MaxY double)";
        String SQL_Index_D = "CREATE UNIQUE INDEX 'Sys_ID_Index"+UUID.randomUUID().toString()+"' on "+DatasetID+"_D (SYS_ID ASC)";
        String SQL_Index_I = "CREATE INDEX 'Sys_ID_Index"+ UUID.randomUUID().toString()+"' on "+DatasetID+"_I (RIndex ASC,CIndex ASC)";
        return this.ExcuteSQL(SQL_D) && this.ExcuteSQL(SQL_I) && this.ExcuteSQL(SQL_Index_D) && this.ExcuteSQL(SQL_Index_I);
    }

    /**
     * 删除指定名称的数据表
     * @param datasetID
     * @return
     */
    public boolean RemoveDataset(String datasetID)
    {
        boolean OK = this.ExcuteSQL("drop table " + datasetID+"_D") && this.ExcuteSQL("drop table " + datasetID+"_I");
        if (OK)this._Datasets.remove(GetDatasetById(datasetID));
        return OK;
    }

    //数据源的文件名称
    private String _Name = "";
    public String getName()
    {
        return _Name;
    }
    public void setName(String value)
    {
        _Name = value;
    }


    //数据源可编辑性
    private boolean _Editing = false;
    public boolean getEditing()
    {
        return this._Editing;
    }
    public void setEditing(boolean value)
    {
        this._Editing = value;
    }

    //指示数据源是否被编辑过
    public boolean getEdited()
    {
        for (Dataset pDataset : this.getDatasets())
        {
            if (pDataset.getEdited()) return true;
        }
        return false;
    }

    //数据库操作类
    public ASQLiteDatabase  _EDatabase = null;
    public ASQLiteDatabase GetSQLiteDatabase(){return this._EDatabase;};

    // 数据集集合
    private List<Dataset> _Datasets = new ArrayList<Dataset>();
    public List<Dataset> getDatasets()
    {
        return _Datasets;
    }

    /**
     * 从数据库重新计算数据源的最大外接矩形
     * @return
     */
    public Envelope GetEnvelope()
    {
        double MinX = 0,MinY = 0,MaxX = 0,MaxY=0;
        List<String> SQLList = new ArrayList<String>();
        for (Dataset pDataset : this.getDatasets())
        {
            String SQL = "select min(MinX) as MinX,min(MinY) as MinY,max(MaxX) as MaxX,max(MaxY) as MaxY from "+pDataset.getIndexTableName();
            SQLList.add(SQL);
        }
        if (SQLList.size()==0) return new Envelope(MinX,MinY,MaxX,MaxY);
        String SQL = "select min(MinX) as MinX,min(MinY) as MinY,max(MaxX) as MaxX,max(MaxY) as MaxY from("+ Tools.JoinT("\r\nunion\r\n", SQLList)+")";
        SQLiteDataReader DR = this.Query(SQL);
        if (DR!=null)if (DR.Read())
        {
            MinX = DR.GetDouble("MinX");
            MinY = DR.GetDouble("MinY");
            MaxX = DR.GetDouble("MaxX");
            MaxY = DR.GetDouble("MaxY");
        }DR.Close();
        return new Envelope(MinX,MaxY,MaxX,MinY);
    }

    //根据名称返回数据集
    public Dataset GetDatasetById(String DatasetId)
    {
        for (Dataset pDataset : this.getDatasets())
        {
            if (pDataset.getId().toUpperCase().equals(DatasetId.toUpperCase())){
                Log.d("getDateBase","DataBaseID:"+pDataset.getId());
                return pDataset;
            }
        }
        return null;
    }

    //取得指定数据表的结构信息
    public List<FieldInfo> GetTableStruct(String TableName)
    {
        List<FieldInfo> FiList = new ArrayList<FieldInfo>();
        String SQL = "select * from T_TableStruct where LayerId='" + TableName + "'";
        SQLiteDataReader pDR = this.Query(SQL);
        {
            if (pDR != null)
            {
                while (pDR.Read())
                {
                    FieldInfo FI = new FieldInfo();
                    FI.setName(pDR.GetString("FieldName"));
                    FI.setCaption(pDR.GetString("FieldCaption"));
                    FiList.add(FI);
                }
            }
        }
        return FiList;
    }

    public String QueryDataFieldValue(String dataFieldName, int sysID, String tableName)
    {
        String SQL = "select "+dataFieldName+ " from "+ tableName +" where SYS_ID=" + sysID;
        SQLiteDataReader pDR = this.Query(SQL);
        if (pDR.Read())
        {
            return pDR.GetString(dataFieldName);
        }
        return "";
    }

    //查询记录
    public SQLiteDataReader Query(String SQL)
    {
        return _EDatabase.Query(SQL);
    }

    //执行SQL语句
    public boolean ExcuteSQL(String SQL)
    {
        return _EDatabase.ExcuteSQL(SQL);
    }
    //执行SQL语句
    public boolean ExcuteSQL(String SQL,Object[] value)
    {
        return _EDatabase.ExcuteSQL(SQL,value);
    }



    //释放空间
    public void Dispose()
    {
        for(Dataset pDataset :_Datasets)pDataset.Dispose();
        _EDatabase.Close();_EDatabase = null;
    }
}
