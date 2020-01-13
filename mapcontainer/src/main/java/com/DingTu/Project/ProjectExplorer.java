package com.DingTu.Project;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Envelope;
import com.DingTu.CoordinateSystem.CoorSystem;
import com.DingTu.Dataset.SQLiteDataReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class ProjectExplorer {

    private ProjectDB m_ProjectDB = null;

    /**
     * 绑定工程操作类
     * @param projectDB
     */
    public void SetBindProjectDB(ProjectDB projectDB)
    {
        this.m_ProjectDB = projectDB;
    }

    //工程名称
    private String m_ProjectName ="";

    /**
     * 设置工程文件名
     * @param prjName
     */
    public void SetProjectName(String prjName){this.m_ProjectName = prjName;}


    /**
     * 得到工程路径,路径+工程目录名
     * @return
     */
    public String GetProjectFullName()
    {
        return PubVar.m_SysAbsolutePath+"/Data/"+this.m_ProjectName;
    }

    public String GetProjectShortName()
    {
        return this.m_ProjectName;
    }

    /**
     * 得到采集数据文件,TAData.dbx全路径名
     * @return
     */
    public String GetProjectDataFileName()
    {
        return this.GetProjectFullName()+"/TAData.dbx";
    }

    /**
     * 得到采集数据预览图
     * @return
     */
    public String GetProjectDataPreviewImageName()
    {
        return this.GetProjectFullName()+"/DataPreview.jpg";
    }

    //工程 创建时间
    private String m_Project_CreateTime = "";
    /**
     * 得到工程 创建时间
     * @return 2013-07-30 16:43:13
     */
    public String GetProjectCreateTime(){return this.m_Project_CreateTime;}

    //坐标系名称
    private CoorSystem m_CoorSystem = null;
    /**
     * 得到工程坐标系统
     * @return
     */
    public CoorSystem GetCoorSystem()
    {
        return m_CoorSystem;
    }

    /**
     * 加载工程信息
     */
    public void LoadProjectInfo()
    {
        //坐标系统
        if (this.m_CoorSystem ==null) {
            this.m_CoorSystem = new CoorSystem();
        }
        //读取此工程对应的图层信息，注意SYS_ID=1的表示模板工程信息，也就是上次创建的工程信息
        SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query("select * from T_Project where id=2");
        if(DR.Read())
        {
            this.m_Project_CreateTime = DR.GetString("CreateTime");
            this.m_CoorSystem.SetName(DR.GetString("CoorSystem"));
            this.m_CoorSystem.SetCenterMeridian(Float.parseFloat(DR.GetString("CenterMeridian")));
            this.m_CoorSystem.SetCoorTransMethodName(DR.GetString("TransMethod"));
            this.m_CoorSystem.SetPMTransMethodName(DR.GetString("PMTransMethod"));
            this.m_CoorSystem.SetTransToP31(DR.GetString("P31"));
            this.m_CoorSystem.SetTransToP32(DR.GetString("P32"));
            this.m_CoorSystem.SetTransToP33(DR.GetString("P33"));
            this.m_CoorSystem.SetTransToP41(DR.GetString("P41"));
            this.m_CoorSystem.SetTransToP42(DR.GetString("P42"));
            this.m_CoorSystem.SetTransToP43(DR.GetString("P43"));
            this.m_CoorSystem.SetTransToP44(DR.GetString("P44"));
            this.m_CoorSystem.SetTransToP71(DR.GetString("P71"));
            this.m_CoorSystem.SetTransToP72(DR.GetString("P72"));
            this.m_CoorSystem.SetTransToP73(DR.GetString("P73"));
            this.m_CoorSystem.SetTransToP74(DR.GetString("P74"));
            this.m_CoorSystem.SetTransToP75(DR.GetString("P75"));
            this.m_CoorSystem.SetTransToP76(DR.GetString("P76"));
            this.m_CoorSystem.SetTransToP77(DR.GetString("P77"));
            this.m_CoorSystem.SetIsAutoCalc(DR.GetString("F1"));
        }DR.Close();

        //读取坐标系统的详细信息
        SQLiteDataReader DRT = PubVar.m_DoEvent.m_ConfigDB.GetSQLiteDatabase().Query("select * from T_CoorSystem where name = '"+this.m_CoorSystem.GetName()+"'");
        if (DRT.Read())
        {
            this.m_CoorSystem.SetA(Double.parseDouble(DRT.GetString("a")));
            this.m_CoorSystem.SetB(Double.parseDouble(DRT.GetString("b")));
            this.m_CoorSystem.SetEasting(Double.parseDouble(DRT.GetString("Easting")));
        }DRT.Close();

    }

    public boolean saveProjectCoorSystem(String coorSys,String centerMeridian)
    {
        String sql = "update T_Project set CoorSystem='"+coorSys+"',CenterMeridian='"+centerMeridian+"' where id=2";
        return this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql);
    }

    /**
     * 保存工程的显示范围，方便下次显示快速定位
     * @param pEnv
     * @return
     */
    public boolean SaveShowExtend(Envelope pEnv)
    {
        //自动创建配置表
        String TableName = "T_ProjectUserConfig";
        if (this.CheckAndCreateTable(TableName))
        {
            try
            {
                JSONObject ParaObj = new JSONObject();
                ParaObj.put("LeftTopX", pEnv.getLeftTop().getX());
                ParaObj.put("LeftTopY", pEnv.getLeftTop().getY());
                ParaObj.put("RightBottomX", pEnv.getRightBottom().getX());
                ParaObj.put("RightBottomY", pEnv.getRightBottom().getY());

                //首先删除上次的范围记录
                String SQL = "delete from "+TableName+" where Name = '上次视图范围'";
                if (this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL))
                {
                    SQL = "insert into "+TableName+" (Name,Para) values ('上次视图范围',?)";
                    Object[] value =new Object[]{ParaObj.toString().getBytes()};
                    return this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL, value);
                }
            }
            catch (JSONException ex)
            {
                throw new RuntimeException(ex);
            }
        }
        return false;
    }

    /**
     * 读取工程的视图范围
     */
    public Envelope ReadShowExtend()
    {
        //自动创建配置表
        String TableName = "T_ProjectUserConfig";
        if (this.CheckAndCreateTable(TableName))
        {
            try
            {
                String SQL = "select * from "+TableName+" where Name='上次视图范围'";
                SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query(SQL);
                if (DR==null)return null;
                if (DR.Read())
                {
                    String josnObjectStr = new String(DR.GetBlob("Para"));
                    JSONTokener jsonParser = new JSONTokener(josnObjectStr);
                    JSONObject ParaJSON = (JSONObject)jsonParser.nextValue();

                    double LeftTopX,LeftTopY,RightBottomX,RightBottomY;
                    String LTX = ParaJSON.getString("LeftTopX");if (Tools.IsDouble(LTX))LeftTopX = Double.parseDouble(LTX); else return null;
                    String LTY = ParaJSON.getString("LeftTopY");if (Tools.IsDouble(LTY))LeftTopY = Double.parseDouble(LTY);else return null;
                    String RBX = ParaJSON.getString("RightBottomX");if (Tools.IsDouble(RBX))RightBottomX = Double.parseDouble(RBX);else return null;
                    String RBY = ParaJSON.getString("RightBottomY");if (Tools.IsDouble(RBY))RightBottomY = Double.parseDouble(RBY);else return null;
                    return new Envelope(LeftTopX,LeftTopY,RightBottomX,RightBottomY);
                }DR.Close();
            }
            catch (JSONException ex)
            {
                return null;
            }
            return null;
        }
        return null;
    }

    /**
     * 动态创建指定名称的表
     * @param TableName
     * @return
     */
    private boolean CheckAndCreateTable(String TableName)
    {
        boolean CreateTable = false;
        String SQL = "SELECT COUNT(*) as count FROM sqlite_master WHERE type='table' and name= '"+TableName+"'";
        SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query(SQL);
        if (DR==null) CreateTable = true;
        int Count = 0;
        if(DR.Read())Count = Integer.parseInt(DR.GetString("count"));DR.Close();
        if (Count<=0) CreateTable = true;
        if (CreateTable)
        {
            //创建表
            List<String> createSQL = new ArrayList<String>();
            createSQL.add("CREATE TABLE "+TableName+" (");
            createSQL.add("ID integer primary key autoincrement  not null default (0),");

            //分不同名称创建表结构
            if (TableName.equals("T_ProjectUserConfig"))
            {
                createSQL.add("Name text,");
                createSQL.add("Para binary");
                for(int i=1;i<=49;i++)
                {
                    createSQL.add("F"+i+" text,");
                }
                createSQL.add("F50 text");
            }

            createSQL.add(")");
            SQL = Tools.JoinT("\r\n", createSQL);
            return this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL);
        } else return true;
    }

    public  boolean CheckAndCreateTanhuiTable()
    {
        String sql = "CREATE TABLE if not exists T_MeiMuJianChi ("+
                "JianChiID    TEXT            PRIMARY KEY,"+
                "YangDiHao    TEXT,"+
                "BiaoZhunDiHao Text,"+
                "XiaoBanHao  Text,"+
                "JianChiCode  INT,"+
                "ShuZhongCode TEXT,"+
                "ShuZhong     TEXT,"+
                "XiongJing    TEXT,"+
                "XuJiLiang    DECIMAL (10, 3))";

        return this.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql);
    }

    public int  GetMaxJianChiCode(String yangdihao,String xiaobanhao,String biaozhundihao )
    {
        String sql = "Select Max(JianChiCode) as maxCode from T_MeiMuJianChi where YangDiHao='"+yangdihao+
                "' and BiaoZhunDiHao='"+biaozhundihao+"' and xiaobanhao ='"+xiaobanhao+"'";
        SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query(sql);
        if(DR == null)
        {
            return 0 ;
        }

        if (DR.Read())
        {
            return DR.GetInt32(0);
        }
        else
        {
            return 0;
        }
    }
}
