package com.DingTu.Base;

import android.util.DisplayMetrics;

import com.DingTu.Dataset.Workspace;
import com.DingTu.GPS.GPSLocate;
import com.DingTu.GPS.GPSMap;
import com.DingTu.Map.Map;
import com.DingTu.mapcontainer.MapControl;

import java.util.Locale;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class PubVar {

    public static Workspace m_Workspace = null;

    //DisplayMetrics
    public static DisplayMetrics m_DisplayMetrics = null;

    public static float m_WindowScaleW = 0.8f;
    public static float m_WindowScaleH = 0.8f;

    public static int MinTanhuiIndex = 8;
    public static int maxTanhuiIndex = 38;

    public static DoEvent m_DoEvent = null;

    public static GPSLocate m_GPSLocate = null;

    public static GPSMap m_GPSMap=null;

    public static boolean m_Photo_LockGPS = true;  //拍照时是否需要GPS支持，也就是否在相片中存储GPS信息

    //是否记录航迹
    public static boolean recordGPS = true;

    //优化全局变量的使用方法(Key-变量名称，Value-自定义类）
    public static HashMapEx m_HashMap = new HashMapEx();

    //系统的语言
    public static Locale m_AppLocale = Locale.CHINESE;

    public static MapControl m_MapControl = null;

    public static Map m_Map = null;

    public static ICallback m_Callback = null;

    //采集数据文件名称
    public static String m_SysDataName="TAData";

    //系统主目录名称
    public static String m_SysDictionaryName="";  //该名称从String.xml->app_name获取

    //系统完整路径
    public static String m_SysAbsolutePath = "";


    //自动存盘的点数
    public static int AutoSavePoints = 20;
    //GPS采样间隔，也就是数据内点的疏密程度
    public static double GPSIntervalDistance = 10;

    //数据采集时间
    public static String SaveDataDate= "";

    //是否自动移屏
    public static boolean AutoPan = true;

    public static BaseLine mBaseLine=new BaseLine();
}
