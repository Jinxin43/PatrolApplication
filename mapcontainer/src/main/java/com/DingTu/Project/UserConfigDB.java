package com.DingTu.Project;

import com.DingTu.Base.HashValueObject;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Dataset.ASQLiteDatabase;
import com.DingTu.Dataset.SQLiteDataReader;
import com.DingTu.Enum.lkRenderType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class UserConfigDB {

//    数据库操作类
    private ASQLiteDatabase m_SQLiteDatabase = null;

    //用户参数配置表
    private UserConfigDB_UserParam m_UserParam = null;
    public UserConfigDB_UserParam GetUserParam()
    {
        if (this.m_UserParam ==null)
        {
            //检查T_UserParam表是否存在
            if (this.CheckAndCreateTable("T_UserParam"))
            {
                this.m_UserParam = new UserConfigDB_UserParam();
                this.m_UserParam.SetBindDB(this.GetSQLiteDatabase());
            }
        }
        return this.m_UserParam;
    }

    //我的坐标系配置表
    private UserConfigDB_MyCoordinateSystem m_MyCoordinateSystem = null;
    public UserConfigDB_MyCoordinateSystem GetMyCoodinateSystem()
    {
        if (this.m_MyCoordinateSystem ==null)
        {
            //检查T_MyCoordinateSystem表是否存在
            if (this.CheckAndCreateTable("T_MyCoordinateSystem"))
            {
                this.m_MyCoordinateSystem = new UserConfigDB_MyCoordinateSystem();
                this.m_MyCoordinateSystem.SetBindDB(this.GetSQLiteDatabase());
            }
        }
        return this.m_MyCoordinateSystem;
    }

    //转换参数存储表
    private UserConfigDB_TransformationParam m_TransformationParam = null;
    public UserConfigDB_TransformationParam GetTransformationParam()
    {
        if (this.m_TransformationParam ==null)
        {
            //检查T_TransformationParam表是否存在
            if (this.CheckAndCreateTable("T_TransformationParam"))
            {
                this.m_TransformationParam = new UserConfigDB_TransformationParam();
                this.m_TransformationParam.SetBindDB(this.GetSQLiteDatabase());
            }
        }
        return this.m_TransformationParam;
    }

    //图层模板配置表
    private UserConfigDB_LayerTemplate m_LayerTemplate = null;
    public UserConfigDB_LayerTemplate GetLayerTemplate()
    {
        if (this.m_LayerTemplate ==null)
        {
            //检查T_LayerTemplate表是否存在
            if (this.CheckAndCreateTable("T_LayerTemplate"))
            {
                this.m_LayerTemplate = new UserConfigDB_LayerTemplate();
                this.m_LayerTemplate.SetBindDB(this.GetSQLiteDatabase());

                //保存系统图层模板，也就是新建工程时调用的默认图层模板
                HashMap<String,Object> sysLayerTemplate = new HashMap<String,Object>();
                //模板名称
                sysLayerTemplate.put("Name", "系统默认图层模板");
                //创建时间
                sysLayerTemplate.put("CreateTime",Tools.GetSystemDate());
                //是否覆盖
                sysLayerTemplate.put("OverWrite","true");

                //图层列表
                List<Layer> vLayerList = new ArrayList<Layer>();
                String[] lyrTypeList = new String[]{"面","线","点"};
                for(String lyrType:lyrTypeList)
                {
                    Layer vLayer = new Layer();
                    vLayer.SetLayerAliasName("默认"+lyrType+"层");
                    vLayer.SetLayerTypeName(lyrType);
                    vLayer.SetRenderType(lkRenderType.enSimple);

                    //为字段实体赋值
                    LayerField LF1 = new LayerField();
                    LF1.SetFieldName("名称");
                    LF1.SetDataFieldName("F1");
                    LF1.SetFieldTypeName("字符串");
                    LF1.SetFieldSize(254);
                    vLayer.GetFieldList().add(LF1);

                    LayerField LF2 = new LayerField();
                    LF2.SetFieldName("备注");
                    LF2.SetDataFieldName("F2");
                    LF2.SetFieldTypeName("字符串");
                    LF2.SetFieldSize(254);
                    vLayer.GetFieldList().add(LF2);

                    vLayerList.add(vLayer);
                }
                sysLayerTemplate.put("LayerList",vLayerList);
                this.m_LayerTemplate.SaveLayerTemplate(sysLayerTemplate);
            }
        }
        return this.m_LayerTemplate;
    }

    /**
     * 得到指定的数据库操作类
     * @return
     */
    private ASQLiteDatabase GetSQLiteDatabase()
    {
        if (this.m_SQLiteDatabase==null) this.OpenDatabase();
        return this.m_SQLiteDatabase;
    }

    //打开配置数据库
    private void OpenDatabase()
    {
        String configFileName = PubVar.m_SysAbsolutePath+"/sysfile/UserConfig.dbx";
        if (Tools.ExistFile(configFileName))
        {
            this.m_SQLiteDatabase = new ASQLiteDatabase();
            this.m_SQLiteDatabase.setDatabaseName(configFileName);
        }
    }

    /**
     * 加载系统配置参数，赋到系统变量内，主要是PubVar.m_HashMap内，
     * 系统变量标识：Tag_System_***
     */
    public void LoadSystemConfig()
    {
        //0、系统语言
        HashValueObject HVO_SystemLanguage = PubVar.m_HashMap.GetValueObject("Tag_System_Language", true);
        HashMap<String,String> configItem = this.GetUserParam().GetUserPara("Tag_System_Language");
        if (configItem==null)
        {
            HVO_SystemLanguage.Value = Tools.ToLocale("系统语言");   //默认系统语言
        } else
        {
            HVO_SystemLanguage.Value = configItem.get("F2");
        }

        //1、取GPS采集数据最小时间间隔，最小距离间隔
        HashValueObject HVO_GpsMinTime = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinTime", true);
        HashValueObject HVO_GpsMinDis = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_MinDistance", true);
        configItem = this.GetUserParam().GetUserPara("Tag_System_GPS");
        if (configItem==null)
        {
            HVO_GpsMinTime.Value = "1";   //默认1秒
            HVO_GpsMinDis.Value="1";    //默认1米
        } else
        {
            HVO_GpsMinTime.Value = configItem.get("F2");
            HVO_GpsMinDis.Value = configItem.get("F3");
        }

        //2、顶部坐标显示栏格式，Code格式：GPS_[0=DD°MM'SS.SSSS"，1=DD°MM.MMMMMM′，2=DD.DDDDDD°]_[1=高程,0=无高程]
        //								  PROJECT_[3=XY]_[1=高程,0=无高程]
        HashValueObject HVO_TopXYFormat_Code = PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Code", true);
        HashValueObject HVO_TopXYFormat_Label = PubVar.m_HashMap.GetValueObject("Tag_System_TopXYFormat_Label", true);
        configItem = this.GetUserParam().GetUserPara("Tag_System_TopXYFormat");
        if (configItem==null)
        {
            HVO_TopXYFormat_Code.Value = "GPS_1_1";   //wgs84经纬度
            HVO_TopXYFormat_Label.Value="N:000°00′0000″ E:00°00′0000″ H:0.00";    //经纬度与高程
        } else
        {
            HVO_TopXYFormat_Code.Value = configItem.get("F2");
            HVO_TopXYFormat_Label.Value = configItem.get("F3");
        }

        //3、手动GPS采集平均值计算参数，点及顶点数
        HashValueObject HVO_GpsAveragePointEnable = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_AveragePointEnable", true);
        HashValueObject HVO_GpsPointCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount", true);
        HashValueObject HVO_GpsVertexCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_VertexCount", true);
        configItem = this.GetUserParam().GetUserPara("Tag_System_GPS_AveragePoint");
        if (configItem==null)
        {
            HVO_GpsAveragePointEnable.Value = "true";  //默认开启
            HVO_GpsPointCount.Value = "5";   //默认5个点
            HVO_GpsVertexCount.Value="3";    //默认3个点，线面节点数
        } else
        {
            HVO_GpsAveragePointEnable.Value = configItem.get("F2");
            HVO_GpsPointCount.Value = configItem.get("F3");
            HVO_GpsVertexCount.Value = configItem.get("F4");
        }

        //4、面积单位
        HashValueObject HVO_AreaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit", true);
        configItem = this.GetUserParam().GetUserPara("Tag_System_AreaUnit");
        if (configItem==null)
        {
            HVO_AreaUnit.Value = "平方米";
        } else
        {
            HVO_AreaUnit.Value = configItem.get("F2");
        }

        HashValueObject HVO_LengthUnit = PubVar.m_HashMap.GetValueObject("Tag_System_LengthUnit", true);
        configItem = this.GetUserParam().GetUserPara("Tag_System_LengthUnit");
        if(configItem == null)
        {
            HVO_LengthUnit.Value = "米";
        } else
        {
            HVO_LengthUnit.Value = configItem.get("F2");
        }

        HashValueObject HVO_MapScale = PubVar.m_HashMap.GetValueObject("Tag_System_MapScale", true);
        configItem = this.GetUserParam().GetUserPara("Tag_System_MapScale");
        if(configItem == null)
        {
            HVO_MapScale.Value = "1:1万";
        } else
        {
            HVO_MapScale.Value = configItem.get("F2");
        }

//        //5、放大镜，也就是确精操作模式
//        HashValueObject HVO_ZoomGlass = PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass", true);
//        configItem = this.GetUserParam().GetUserPara("Tag_System_ZoomGlass");
//        if (configItem==null)
//        {
//            HVO_ZoomGlass.Value = "true";
//            if(PubVar.m_DoEvent.m_GlassView!= null)
//            {
//                PubVar.m_DoEvent.m_GlassView.SetVisible(true);
//            }
//
//        } else
//        {
//            HVO_ZoomGlass.Value = configItem.get("F2");
//            if(PubVar.m_DoEvent.m_GlassView != null)
//            {
//                if(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value.equals("true"))
//                {
//                    PubVar.m_DoEvent.m_GlassView.SetVisible(true);
//                }
//                else
//                {
//                    PubVar.m_DoEvent.m_GlassView.SetVisible(false);
//                }
//            }
//        }
//
//        HashValueObject HVO_ZoomGlass_Scale = PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass_Scale", true);
//        configItem = this.GetUserParam().GetUserPara("Tag_System_ZoomGlass_Scale");
//        if (configItem==null)
//        {
//            HVO_ZoomGlass_Scale.Value = "中";
//            if(PubVar.m_DoEvent.m_GlassView!= null)
//            {
//                PubVar.m_DoEvent.m_GlassView.setGlassScale(HVO_ZoomGlass_Scale.Value);
//            }
//
//        } else
//        {
//            HVO_ZoomGlass_Scale.Value = configItem.get("F2");
//            if(PubVar.m_DoEvent.m_GlassView != null)
//            {
//                if(HVO_ZoomGlass_Scale.Value != null)
//                {
//                    PubVar.m_DoEvent.m_GlassView.setGlassScale(HVO_ZoomGlass_Scale.Value);
//                }
//            }
//        }

        //5、照片是否加水印
        HashValueObject hvoWaterMark = PubVar.m_HashMap.GetValueObject("Tag_Photo_WaterMark", true);
        configItem = this.GetUserParam().GetUserPara("Tag_Photo_WaterMark");
        if (configItem==null)
        {
            hvoWaterMark.Value = "true";


        } else
        {
            hvoWaterMark.Value = configItem.get("F2");

        }

    }

    /**
     * 创建指定名称的表
     * @param TableName
     * @return
     */
    private boolean CheckAndCreateTable(String TableName)
    {
        if (this.IsExistTable(TableName)) return true;
        else
        {
            //创建表
            List<String> createSQL = new ArrayList<String>();
            createSQL.add("CREATE TABLE "+TableName+" (");
            createSQL.add("ID integer primary key autoincrement  not null default (0),");

            //分不同名称创建表结构
            if (TableName.equals("T_LayerTemplate"))
            {
                createSQL.add("name text,");
                createSQL.add("createtime text,");
                createSQL.add("layerlist binary");
            }
            if (TableName.equals("T_MyCoordinateSystem"))
            {
                createSQL.add("Name text,");
                createSQL.add("CreateTime text,");
                createSQL.add("Para binary");
            }
            if (TableName.equals("T_UserParam"))
            {
                for(int i=1;i<=49;i++)
                {
                    createSQL.add("F"+i+" text,");
                }
                createSQL.add("F50 text");
            }
            if (TableName.equals("T_TransformationParam"))
            {
                for(int i=1;i<=49;i++)
                {
                    createSQL.add("F"+i+" text,");
                }
                createSQL.add("F50 text");
            }



            createSQL.add(")");
            String SQL = Tools.JoinT("\r\n", createSQL);
            return this.GetSQLiteDatabase().ExcuteSQL(SQL);
        }
    }

    /**
     * 检查指定的表是否在存在
     * @param TableName
     * @return
     */
    private boolean IsExistTable(String TableName)
    {
        String SQL = "SELECT COUNT(*) as count FROM sqlite_master WHERE type='table' and name= '"+TableName+"'";
        SQLiteDataReader DR = this.GetSQLiteDatabase().Query(SQL);
        if (DR==null) return false;
        int Count = 0;
        if(DR.Read())Count = Integer.parseInt(DR.GetString("count"));DR.Close();
        if (Count>0) return true;else return false;
    }
}
