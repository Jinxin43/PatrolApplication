package com.DingTu.Base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.DingTu.CoordinateSystem.CoorSystem;
import com.DingTu.CoordinateSystem.ProjectSystem;
import com.DingTu.CoordinateSystem.Project_GK;
import com.DingTu.Dataset.ASQLiteDatabase;
import com.DingTu.Dataset.SQLiteDataReader;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkGeoLayersType;
import com.DingTu.Enum.lkMapFileType;
import com.DingTu.Layer.GeoLayer;
import com.DingTu.Map.Param;
import com.DingTu.Map.StaticObject;
import com.DingTu.mapcontainer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Envelope;
import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Cargeometry.Part;
import com.DingTu.Cargeometry.Polygon;
import com.DingTu.Cargeometry.Polyline;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Tools {/**
 * 列表转换为JSON字符串
 * @param StrList
 * @return
 */
public static String ListToJSONStr(List<String> StrList)
{
    try
    {
        JSONObject parentJSObject = new JSONObject();
        JSONArray jsArray = new JSONArray();
        for(String Str:StrList)jsArray.put(Str);
        parentJSObject.put("Data", jsArray);
        return parentJSObject.toString();
    } catch (JSONException e) {
        return "";
    }
}

    /**
     * JSONStr字符串转List<String>
     * @param JsonStr
     * @return
     */
    public static List<String> JSONStrToList(String JsonStr)
    {
        List<String> result = new ArrayList<String>();
        try
        {
            JSONTokener jsonParser = new JSONTokener(JsonStr);
            JSONObject jsObj = (JSONObject)jsonParser.nextValue();
            JSONArray jsArray = jsObj.getJSONArray("Data");
            for(int i=0;i<jsArray.length();i++)result.add(jsArray.getString(i));
            return result;
        } catch (JSONException e)
        {
            return result;
        }
    }

    /**
     * 转换为十六进制
     * @return
     */
    public static String ColorToHexStr(int intColor)
    {
        String RStr = Integer.toHexString(Color.red(intColor));
        if (RStr.length()==1)RStr="0"+RStr;
        String GStr = Integer.toHexString(Color.green(intColor));
        if (GStr.length()==1)GStr="0"+GStr;
        String BStr = Integer.toHexString(Color.blue(intColor));
        if (BStr.length()==1)BStr="0"+BStr;
        return "#"+(RStr+GStr+BStr).toUpperCase();
    }

    public static String ColorToHexStr2(int intColor)
    {
        return String.format("#%06X", (0xFFFFFF & intColor));
    }

    public static String Color2String(Color color) {
        String R = Integer.toHexString(color.RED);
        R = R.length() < 2 ? ('0' + R) : R;
        String B = Integer.toHexString(color.BLUE);
        B = B.length() < 2 ? ('0' + B) : B;
        String G = Integer.toHexString(color.GREEN);
        G = G.length() < 2 ? ('0' + G) : G;
        return '#' + R + B + G;
    }

    /**
     * 根据系统提语言转换文字
     * @param text
     * @return
     */
    public static String ToLocale(String text)
    {
        if (PubVar.m_AppLocale== Locale.ENGLISH)
        {
            List<String> LocaleTextList = new ArrayList<String>();
            LocaleTextList.add("返回,Back");
            LocaleTextList.add("回退,Back");
            LocaleTextList.add("系统提示,Message");
            LocaleTextList.add("确定,OK");
            LocaleTextList.add("取消,Cancel");
            LocaleTextList.add("提示,Message");
            LocaleTextList.add("正在加载数据，请稍候,Loading data，Please wait");
            LocaleTextList.add("系统没有加载任何工程信息，无法完成操作！,First，Please open the project！");
            LocaleTextList.add("是否确定退出采集系统？,Whether you want to quit system？");


            LocaleTextList.add("工程管理,Project Manage");
            LocaleTextList.add("选择,Select");
            LocaleTextList.add("名称,Name");
            LocaleTextList.add("工程名称,Name");
            LocaleTextList.add("工程信息,Project Information");
            LocaleTextList.add("创建时间,Create Time");
            LocaleTextList.add("详细,Detail");
            LocaleTextList.add("详细信息,Detail Information");

            LocaleTextList.add("新建,New");
            LocaleTextList.add("新建工程,New Project");
            LocaleTextList.add("创建,Create");

            LocaleTextList.add("删除,Delete");
            LocaleTextList.add("打开,Open");
            LocaleTextList.add("打开工程,Open");
            LocaleTextList.add("放大,Zoom");

            LocaleTextList.add("属性,Feature");
            LocaleTextList.add("采集,Data");
            LocaleTextList.add("保存,Save");
            LocaleTextList.add("工具,Tools");
            LocaleTextList.add("工程,Project");
            LocaleTextList.add("图层,Layer");
            LocaleTextList.add("数据预览图,Data Preview");

            LocaleTextList.add("当前打开工程,Current Open Project");
            LocaleTextList.add("工程图层信息,Layer List");
            LocaleTextList.add("图层列表,Layer List");
            LocaleTextList.add("工程列表,Project List");

            LocaleTextList.add("工程坐标信息,Coordinate system");
            LocaleTextList.add("坐标系统,Coordinate System");
            LocaleTextList.add("中央经线,Central Meridian");
            LocaleTextList.add("转换方法,Transfor Method");
            LocaleTextList.add("转换参数,Transfor Parameters");
            LocaleTextList.add("图层名称,Layer Name");
            LocaleTextList.add("图层类型,Layer Type");
            LocaleTextList.add("图层模板,Layer Template");
            LocaleTextList.add("图层管理,Layer Manage");
            LocaleTextList.add("数据量,Records");
            LocaleTextList.add("请勾选需要删除的工程,Please check the need to delete the projects");
            LocaleTextList.add("是否确定要删除以下工程,Whether you want to delete the following project");
            LocaleTextList.add("正在打开工程,Open project");
            LocaleTextList.add("是否打开上次工程,Whether you want to open the last project");
            LocaleTextList.add("上次时间,Last Time");
            LocaleTextList.add("当前没有创建任何工程，是否需要创建工程,Whether you need to create a project");
            LocaleTextList.add("我的坐标系,My System");
            LocaleTextList.add("模板名称,Template Name");
            LocaleTextList.add("设置为默认模板,Set Default Template");
            LocaleTextList.add("我的坐标系参数,My System Information");
            LocaleTextList.add("当前工程坐标系参数,Current Project System Information");

            LocaleTextList.add("渲染,Render");
            LocaleTextList.add("向上,MoveUp");
            LocaleTextList.add("向下,MoveDown");
            LocaleTextList.add("存模板,Save");
            LocaleTextList.add("调模板,Load");
            LocaleTextList.add("矢量底图,Vector Map");
            LocaleTextList.add("栅格底图,    Grid Map");
            LocaleTextList.add("显示,Visible");
            LocaleTextList.add("类型,Type");
            LocaleTextList.add("符号,Symbol");
            LocaleTextList.add("采集数据图层,Data Layer");
            LocaleTextList.add("底图图层,Background Layer");
            LocaleTextList.add("关闭,Close");
            LocaleTextList.add("信息,Information");
            LocaleTextList.add("星历信息,Satellite Information");
            LocaleTextList.add("位置信息,Location Information");
            LocaleTextList.add("速度,Speed");
            LocaleTextList.add("高程,Elevation");
            LocaleTextList.add("精度,Precision");
            LocaleTextList.add("状态,Status");
            LocaleTextList.add("经度,Longitude");
            LocaleTextList.add("纬度,Latitude");
            LocaleTextList.add("可见卫星数,Visible Satellites");
            LocaleTextList.add("解算卫星数,Calculate satellite");
            LocaleTextList.add("颗,");
            LocaleTextList.add("北,N");
            LocaleTextList.add("南,S");
            LocaleTextList.add("西,W");
            LocaleTextList.add("东,E");
            LocaleTextList.add("东北,NE");
            LocaleTextList.add("东南,SE");
            LocaleTextList.add("西南,WS");
            LocaleTextList.add("西北,NW");
            LocaleTextList.add("米,m");
            LocaleTextList.add("平方米,㎡");
            LocaleTextList.add("公里,km");
            LocaleTextList.add("平方公里,k㎡");
            LocaleTextList.add("已定位,Located");
            LocaleTextList.add("未定位,No Locate");

            LocaleTextList.add("量距,Length");
            LocaleTextList.add("长度,Length");
            LocaleTextList.add("量面,Area");
            LocaleTextList.add("面积,Area");
            LocaleTextList.add("坐标,Coordinate");
            LocaleTextList.add("刷屏,Clear");
            LocaleTextList.add("截图,Capture");
            LocaleTextList.add("更多,More");
            LocaleTextList.add("退出,Quit");

            LocaleTextList.add("坐标绘点,By XY");
            LocaleTextList.add("手绘点,By Manual");
            LocaleTextList.add("GPS点位,By GPS");

            LocaleTextList.add("手绘,Manual");
            LocaleTextList.add("GPS定点,GPS Point");
            LocaleTextList.add("GPS轨迹,GPS Track");

            LocaleTextList.add("取消,Cancel");
            LocaleTextList.add("生成,Generate");

            LocaleTextList.add("数据采集,Data Mode");
            LocaleTextList.add("采集模式 【当前[面]图层：,Mode【Current[Polygon]Layer：");
            LocaleTextList.add("采集模式 【当前[线]图层：,Mode【Current[Line]Layer：");
            LocaleTextList.add("采集模式 【当前[点]图层：,Mode【Current[Point]Layer：");
            LocaleTextList.add("选择图层,Select Layer");
            LocaleTextList.add("正在采集数据图层,Collecting Data Layer");
            LocaleTextList.add("请选择有效的数据图层！,Please select valid data layer！");

            LocaleTextList.add("点,Point");
            LocaleTextList.add("点图层,Point Layer");
            LocaleTextList.add("线,Line");
            LocaleTextList.add("线图层,Line Layer");
            LocaleTextList.add("面图层,Polygon Layer");
            LocaleTextList.add("面,Polygon");
            LocaleTextList.add("手绘,Manual");

            LocaleTextList.add("坐标信息,Coordinate Information");
            LocaleTextList.add("坐标信息,Coordinate Information");
            LocaleTextList.add(" X坐标, X");
            LocaleTextList.add(" Y坐标, Y");
            LocaleTextList.add("照片,Photo");
            LocaleTextList.add("基本属性,Base Feature");
            LocaleTextList.add("数据信息,Data Information");
            LocaleTextList.add("备注,Note");

            LocaleTextList.add("移动,Move");
            LocaleTextList.add("加节点,Add Vertex");
            LocaleTextList.add("删节点,Del Vertex");
            LocaleTextList.add("移节点,Move Vertex");

            LocaleTextList.add("请首先开启GPS设备！,Please first open the GPS device！");
            LocaleTextList.add("GPS没定位，请等待...,GPS positioning，please wait...");
            LocaleTextList.add("当前没有正在采集的实体！,Currently there is no collecting objects！");
            LocaleTextList.add("节点数已为0，无法回退！,Number of vertexs was 0, not back！");
            LocaleTextList.add("无可恢复操作记录！,No restore operation record！");
            LocaleTextList.add("是否确定删除以上被选择实体？,Is sure to delete the selected objects？");
            LocaleTextList.add("工具箱,Tools Box");
            LocaleTextList.add("工具选项,Tools Option");

            LocaleTextList.add("系统设置,Settings");
            LocaleTextList.add("采集统计,Statistics");
            LocaleTextList.add("保存数据,Save");
            LocaleTextList.add("数据导出,Export");
            LocaleTextList.add("关于系统,About");
            LocaleTextList.add("退出系统,Quit");


            LocaleTextList.add("选择语言,Select Language");
            LocaleTextList.add("系统语言,System Language");
            LocaleTextList.add("中文,Chinese");
            LocaleTextList.add("英文,English");
            LocaleTextList.add("系统将在下一次启动时切换至,The system will be in the next time you start switch to");
            LocaleTextList.add("语言环境,language");


            LocaleTextList.add("系统信息,System Information");
            LocaleTextList.add("软件名,SoftName");
            LocaleTextList.add("版本号,Version");
            LocaleTextList.add("授    权,Authorization");
            LocaleTextList.add("软件码,SoftCode");
            LocaleTextList.add("开发者,Developers");
            LocaleTextList.add("语    言,Language");
            LocaleTextList.add("已授权,Authorized");
            LocaleTextList.add("未授权,No Authorized");
            LocaleTextList.add("试用用户,Trial User");
            LocaleTextList.add("临时用户,Temporary User");
            LocaleTextList.add("正式用户,Formal User");
            LocaleTextList.add("试用期至,Trial Date To");
            LocaleTextList.add("数量,Numbers");
            LocaleTextList.add("增加,Add New");
            LocaleTextList.add("张,Pieces");
            LocaleTextList.add("正在保存数据,Saving data");
            LocaleTextList.add("数据保存成功！,Data save successfully！");
            LocaleTextList.add("数据截图,Data Capture");
            LocaleTextList.add("请选择需要查询的实体！,Please select need query objects！");
            LocaleTextList.add("基本信息,Base Information");
            LocaleTextList.add("字段信息,Field List");
            LocaleTextList.add("大小,Size");
            LocaleTextList.add("数据字典,Dictionary");
            LocaleTextList.add("字段属性,Field Information");
            LocaleTextList.add("字段数据关联,Field Dictionary");
            LocaleTextList.add("值域,Domain");
            LocaleTextList.add("是否可输入其它值,Enter other value");
            LocaleTextList.add("是否删除字段,Whether delete fields");
            LocaleTextList.add("是否删除图层,Whether delete selected layer");

            LocaleTextList.add("图层渲染,Render");
            LocaleTextList.add("图层符号,Symbol");
            LocaleTextList.add("透明度：,Transparent：");
            LocaleTextList.add("透明度,Transparent");
            LocaleTextList.add("图层标注,Label");
            LocaleTextList.add("是否标注,IF Label");
            LocaleTextList.add("标注字段,Field");
            LocaleTextList.add("标注颜色,Color");
            LocaleTextList.add("标注大小,Size");
            LocaleTextList.add("示例文字,Text");

            LocaleTextList.add("图层模板,Layer Template");
            LocaleTextList.add("图层模板信息,Template Information");
            LocaleTextList.add("模板名称,Name");
            LocaleTextList.add("创建时间,Create Time");
            LocaleTextList.add("图层数量,Layer Numbers");
            LocaleTextList.add("图层数,Layers");

            LocaleTextList.add("覆盖相同名称模板,Overwrite Template");
            LocaleTextList.add("底图文件,Background Map");
            LocaleTextList.add("无底图,No Map");
            LocaleTextList.add("文件名称,Name");
            LocaleTextList.add("矢量图,Vector Map");
            LocaleTextList.add("栅格图,Grid Map");
            LocaleTextList.add("影像图,Grid Map");
            LocaleTextList.add("矢量底图设置,Vector Map Set");
            LocaleTextList.add("面层透明度,Polygon Transparent");
            LocaleTextList.add("显示,Visible");

            LocaleTextList.add("GPS数据流采样时间与距离,GPS Sampling Time and Distance");
            LocaleTextList.add("GPS数据点捕获模式,GPS Data Capture Mode");
            LocaleTextList.add("顶部坐标栏显示,Head Bar Coordinate Display");
            LocaleTextList.add("平均值,Average");
            LocaleTextList.add("保存配置,Save");
            LocaleTextList.add("采样时间,Time");
            LocaleTextList.add("间隔,Distance");
            LocaleTextList.add("采样距离,Distance");
            LocaleTextList.add("节点,Vertex");
            LocaleTextList.add("个,");
            LocaleTextList.add("秒,s");
            LocaleTextList.add("平均【点】数：,Average Points");
            LocaleTextList.add("平均【节点】数,Average Vertexs");
            LocaleTextList.add("坐标数据,Data");
            LocaleTextList.add("坐标格式,Formate");
            LocaleTextList.add("显示高程,Elevation");
            LocaleTextList.add("显示格式,Show Formate");
            LocaleTextList.add("导出数据,Export");
            LocaleTextList.add("数据导出,Data Export");
            LocaleTextList.add("导出目录,Directory");
            LocaleTextList.add("导出格式,Formate");
            LocaleTextList.add("导出图层,Layers");
            LocaleTextList.add("导出,Export");

            LocaleTextList.add("点层,Point");
            LocaleTextList.add("线层,Line");
            LocaleTextList.add("面层,Polygon");



            LocaleTextList.add("数据统计,Statistic");
            LocaleTextList.add("长度(米),Length(m)");
            LocaleTextList.add("面积(平方米),Area(㎡)");

            LocaleTextList.add("符号列表,Symbol List");
            LocaleTextList.add("新增,Add");
            LocaleTextList.add("符号库,Symbol Library");

            for(String localeText:LocaleTextList)
            {
                String[] textInfo = localeText.split(",");
                if (textInfo[0].equals(text)) if(textInfo.length==2)return textInfo[1];else return "";

                //尾部有"："情况
                if (text.contains("："))
                {
                    if (textInfo[0].equals(text.replace("：", ""))) return textInfo[1]+"：";
                }

                //属部有空格情况
                if (text.length()!=text.trim().length())
                {
                    //提取属部空格
                    if (textInfo[0].equals(text.trim()))
                    {
                        return text.replace(text.trim(), textInfo[1]);
                    }
                }
            }
        }
        return text;
    }

    /**
     * 根据系统语言转换文字
     * @param
     * @return
     */
    public static void ToLocale(View view)
    {
        if (view==null) return;
        String VType = view.getClass().getName();
        if (VType.equals("android.widget.TextView"))
        {
            TextView tv = (TextView) view;
            tv.setText(ToLocale(tv.getText()+""));
        }
        if (VType.equals("android.widget.CheckBox"))
        {
            CheckBox cb = (CheckBox) view;
            cb.setText(ToLocale(cb.getText()+""));
        }
        if (VType.equals("android.widget.Button"))
        {
            Button bt = (Button) view;
            bt.setText(ToLocale(bt.getText()+""));
        }
    }

    /**
     * 获取string.xml资源文件中对应ID的文字
     * @param id
     * @return
     */
    public static String GetResourceStr(int id)
    {
        return PubVar.m_DoEvent.m_Context.getResources().getString(id);
    }

    /**
     * 设置系统语言环境
     * @param context
     */
    public static void SetLocale(Context context)
    {
        Resources resources = context.getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        DisplayMetrics dm = resources .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
        config.locale = PubVar.m_AppLocale;
        resources.updateConfiguration(config, dm);
    }

    /**
     * 自动获取系统配置的语言环境
     *
     */
    public static void AutoGetSystemLanguage()
    {
        //设置系统的语言环境
        PubVar.m_AppLocale = Locale.CHINESE;
        String SystemLanguage = PubVar.m_HashMap.GetValueObject("Tag_System_Language").Value;
        if ((SystemLanguage.equals("中文") || SystemLanguage.equals("Chinese")))PubVar.m_AppLocale = Locale.CHINESE;
        if ((SystemLanguage.equals("英文") || SystemLanguage.equals("English")))PubVar.m_AppLocale = Locale.ENGLISH;
        if ((SystemLanguage.equals("系统语言") || SystemLanguage.equals("System Language")))
        {
            String language = Locale.getDefault().getLanguage();
            if (language.endsWith("en"))PubVar.m_AppLocale = Locale.ENGLISH;
            else PubVar.m_AppLocale = Locale.CHINESE;
        }
    }

    /**
     * 倒序坐标串
     * @param list
     */
    public static void ReverseList(List<Coordinate> list)
    {
        List<Coordinate> newList = new ArrayList<Coordinate>();
        for(int i=0;i<list.size();i++)newList.add(0, list.get(i));
        list.clear();
        for(int i=0;i<newList.size();i++)list.add(newList.get(i));
    }

    /**
     * 保存图片到文件
     * @param fileName
     * @param bp
     * @return
     */
    public static boolean SaveBitmapTo(String fileName,Bitmap bp)
    {
        File f = new File(fileName);
        try {
            f.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace(); return false;
        }
        bp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 补前导空格
     * @param Str
     * @param Len
     * @return
     */
    public static String PadLeft(String Str,int Len)
    {
        if (Tools.CalStrLength(Str)>=Len) return Str;
        String space = "";
        for(int i=1;i<=Len-Tools.CalStrLength(Str);i++)space+="  ";
        return space+Str;
    }

    /**
     * 计算字符中长度，可包含中文
     * @param value
     * @return
     */
    public static int CalStrLength(String value)
    {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < value.length(); i++) {
            // 获取一个字符
            String temp = value.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 2;
            } else {
                // 其他字符长度为0.5
                valueLength += 1;
            }
        }
        //进位取整
        return (int)valueLength;
    }

    /**
     * 显示Toast信息
     * @param context
     * @param Message
     */
    public static void ShowToast(Context context,String Message)
    {
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 是否为整数
     * @param str
     * @return
     */
    public static boolean IsInteger(String str)
    {
        if (str.trim().equals("")) return false;
        Matcher isNum = Pattern.compile("[0-9]*").matcher(str);;
        return isNum.matches();
    }

    /**
     * 是否为浮点数
     * @param Str
     * @return
     */
    public static boolean IsFloat(String Str)
    {
        try
        {
            float f = Float.parseFloat(Str);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * 是否为浮点数
     * @param Str
     * @return
     */
    public static boolean IsDouble(String Str)
    {
        if (Str.equals("NaN")) return false;
        try
        {
            double f = Double.parseDouble(Str);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * 打开对话框，带有进度框
     * @param pCallback
     */
    public static void OpenDialog(final ICallback pCallback)
    {
        OpenDialog(Tools.ToLocale("正在加载数据，请稍候")+"...",pCallback);
    }
    public static void OpenDialog(String CaptionInfo,final ICallback pCallback)
    {
        //创建ProgressDialog对象
        final ProgressDialog m_pDialog = new ProgressDialog(PubVar.m_DoEvent.m_Context);

        // 设置进度条风格，风格为圆形，旋转的
        m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        m_pDialog.setIcon(R.drawable.messageinfo);

        // 设置ProgressDialog 标题
        m_pDialog.setTitle(Tools.ToLocale("提示"));

        // 设置ProgressDialog 提示信息
        m_pDialog.setMessage(CaptionInfo);

        // 设置ProgressDialog 的进度条是否不明确
        m_pDialog.setIndeterminate(false);

        // 设置ProgressDialog 是否可以按退回按键取消
        m_pDialog.setCancelable(false);
        m_pDialog.show();
        new Handler().postDelayed(new Runnable()
                                  {
                                      public void run()
                                      {
                                          pCallback.OnClick("OK", null);
                                          m_pDialog.cancel();
                                      }}
                ,100*1);

    }

    /**
     * 使带有文字和图片的按钮居中显示，横向居中
     * @param btn
     */
    public static void SetButtonImageAndTextOnCenter(final Button btn,int imageId)
    {
        Html.ImageGetter imgGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable = PubVar.m_DoEvent.m_Context.getResources().getDrawable(Integer.parseInt(source));
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                return drawable;
            }
        };

        StringBuffer sb = new StringBuffer();
        sb.append("<img src=\"").append(imageId).append("\"/>").append("<font color=\"000000\">"+btn.getText()+"</font>");;
        Spanned span = Html.fromHtml(sb.toString(), imgGetter, null);
        btn.setText(span);
        sb = null;
    }

    /**
     * 获取系统时间
     * @return 格式：2011-10-1 23:23:22
     */
    public static String GetSystemDate()
    {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //HH表示24进制
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    //关闭输入法
    public static void CloseInputMethod(Activity C)
    {
        //关闭输入法
        InputMethodManager inputMethodManager = (InputMethodManager)C.getSystemService(Context.INPUT_METHOD_SERVICE);
        View V = C.getCurrentFocus();
        if (V!=null)
        {
            inputMethodManager.hideSoftInputFromWindow(V.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return;
        }
    }

    //关闭指定的Activity
    public static void CloseActivity(Activity A)
    {
        Tools.CloseInputMethod(A);
        A.finish();
    }

    //显示是否对话框
    public static void ShowYesNoMessage(Context C,String Message,final ICallback callback)
    {
        //创建AlertDialog
        AlertDialog.Builder menuDialog = new AlertDialog.Builder(C);
        menuDialog.setTitle(("系统提示"));
        menuDialog.setIcon(R.drawable.messageinfo);
        menuDialog.setCancelable(true);
        menuDialog.setMessage(Message);
        menuDialog.setPositiveButton(("确定"), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                callback.OnClick("YES", "");
                dialog.dismiss();
            }
        });
        menuDialog.setNegativeButton(("取消"), new DialogInterface.OnClickListener()
        {
            @Override
            public  void  onClick(DialogInterface dialog,  int  which)
            {
                dialog.dismiss();
            }
        });
        menuDialog.show();
    }

    public static void ShowYesOrOtherMessage(Context C,String Message,final ICallback callback)
    {
        //创建AlertDialog
        AlertDialog.Builder menuDialog = new AlertDialog.Builder(C);
        menuDialog.setTitle(("系统提示"));
        menuDialog.setIcon(R.drawable.messageinfo);
        menuDialog.setCancelable(true);
        menuDialog.setMessage(Message);
        menuDialog.setPositiveButton(("继续巡护"), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                callback.OnClick("YES", "");
                dialog.dismiss();
            }
        });
        menuDialog.setNegativeButton(("完成巡护"), new DialogInterface.OnClickListener()
        {
            @Override
            public  void  onClick(DialogInterface dialog,  int  which)
            {
                callback.OnClick("NO", "");
                dialog.dismiss();
            }
        });
        menuDialog.show();
    }

    public static void ShowYesContinuMessage(Context C,String Message,final ICallback callback)
    {
        //创建AlertDialog
        AlertDialog.Builder menuDialog = new AlertDialog.Builder(C);
        menuDialog.setTitle(("系统提示"));
        menuDialog.setIcon(R.drawable.messageinfo);
        menuDialog.setCancelable(true);
        menuDialog.setMessage(Message);
        menuDialog.setPositiveButton(("好的"), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                callback.OnClick("YES", "");
                dialog.dismiss();
            }
        });
        menuDialog.setNegativeButton(("继续保存"), new DialogInterface.OnClickListener()
        {
            @Override
            public  void  onClick(DialogInterface dialog,  int  which)
            {
                callback.OnClick("NO", "");
                dialog.dismiss();
            }
        });
        menuDialog.show();
    }


    /**
     * 将小数度数转换为度分秒格式
     * @param numStr （主要用于图片中存储经纬度）
     * @return
     */
    public static String ConvertToSexagesimal(String numStr){
        double num = Double.parseDouble(numStr);
        int du=(int)Math.floor(Math.abs(num));    //获取整数部分
        double temp=GetdPoint(Math.abs(num))*60;
        int fen=(int)Math.floor(temp); //获取整数部分
        double miao=GetdPoint(temp)*60;

        int miaoI = (int)Math.floor(miao*1000000);
        if(num<0)
            return "-"+du+"/1,"+fen+"/1,"+miaoI+"/1000000";

        return du+"/1,"+fen+"/1,"+miaoI+"/1000000";

    }
    //获取小数部分
    private static double GetdPoint(double num){
        double d = num;
        int fInt = (int) d;
        BigDecimal b1 = new BigDecimal(Double.toString(d));
        BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
        double dPoint = b1.subtract(b2).floatValue();
        return dPoint;
    }

    public static String CalcDuration(Date startTime)
    {
        String result = "";
        long startMillions = startTime.getTime();
        long durationMillions = new Date().getTime()- startTime.getTime();
        if(durationMillions>3600000)
        {
            long hours = durationMillions/3600000;
            long leafMillions = durationMillions%360000;
            result += hours+"小时";
            long minitues = leafMillions/60000;
            result += minitues+"分钟";
        }
        else
        {
            long minitues = durationMillions/60000;
            long second = (durationMillions%60000)/1000;
            result += minitues+"分钟"+second+"秒";
        }
        return result;
    }
    //规化长度测量值
    public static String ReSetDistance(double Distance)
    {
        return ReSetDistance(Distance,true);
    }
    public static String ReSetDistance(double Distance,boolean HaveUnit)
    {
        DecimalFormat df1 = new DecimalFormat("0.0");
        DecimalFormat df2 = new DecimalFormat("0.000");
        if (Distance < 1000) return df1.format(Distance) + (HaveUnit? "("+Tools.ToLocale("米")+")":"");
        else return df2.format((Distance / 1000)) + (HaveUnit? "("+Tools.ToLocale("公里")+")":"");
    }
    public static String ReSetArea(double Area,boolean HaveUnit)
    {
        DecimalFormat df1 = new DecimalFormat("0.0");
        DecimalFormat df2 = new DecimalFormat("0.0000");

        String areaUnit="平方公里";
//        String areaUnit = PubVar.m_HashMap.GetValueObject("Tag_System_AreaUnit").Value+"";
        if (areaUnit.equals("平方米"))
        {
            return df1.format(Area) + (HaveUnit? "("+areaUnit+")":"");
        }
        if (areaUnit.equals("平方公里"))
            {
            return df2.format(Area / (1000*1000)) + (HaveUnit? "("+areaUnit+")":"");
        }
        if (areaUnit.equals("亩"))
        {
            return df1.format(Area/666.6666666667) + (HaveUnit? "("+areaUnit+")":"");
        }
        if (areaUnit.equals("公顷"))
        {
            return df2.format(Area/10000) + (HaveUnit? "("+areaUnit+")":"");
        }
        return df1.format(Area) + (HaveUnit? "("+areaUnit+")":"");
//        if (Area < 1000 * 1000) return df1.format(Area) + (HaveUnit? "("+Tools.ToLocale("平方米")+")":"");
//        else return df2.format((Area / (1000*1000))) + (HaveUnit? "("+Tools.ToLocale("平方公里")+")":"");
    }

    public static double MToKM(double M)
    {
        return Save3Point(M/1000);
//    	DecimalFormat df2 = new DecimalFormat("#.000");
//    	return Double.valueOf(df2.format(M / 1000));
    }

    public static double Save3Point(double M)
    {
        DecimalFormat df2 = new DecimalFormat("#.000");
        return Double.valueOf(df2.format(M));
    }

    public static float ParseFloat(String value,int decimal)
    {
        if (value.equals("")) return 0;
        DecimalFormat df2 = new DecimalFormat("#.000");
        if (decimal==2)df2 = new DecimalFormat("#.00");
        if (decimal==1)df2 = new DecimalFormat("#.0");
        return Float.valueOf(df2.format(Double.parseDouble(value)));
    }

    public static int ParseInt(String value)
    {
        if (value.equals("")) return 0;
        return Integer.parseInt(value);
    }

    public static double GetTwoPointDistance(Coordinate FirstPoint, Coordinate SencordPoint)
    {
        return GetTwoPointDistance(FirstPoint.getX(),FirstPoint.getY(),SencordPoint.getX(),SencordPoint.getY(),true);
    }
    public static double GetTwoPointDistance(Coordinate FirstPoint, Coordinate SencordPoint,boolean IfConvert)
    {
        return GetTwoPointDistance(FirstPoint.getX(),FirstPoint.getY(),SencordPoint.getX(),SencordPoint.getY(),IfConvert);
    }

    public static double GetTwoPointDistance(double x1,double y1,double x2,double y2,boolean IfConvert)
    {
        if (IfConvert)
        {
//            if (StaticObject.soProjectSystem.GetCoorSystem().GetName().equals("WGS-84坐标"))
//            {
                Coordinate Coor1 = StaticObject.soProjectSystem.XYToWGS84(x1, y1, 0);
                Coordinate Coor2 = StaticObject.soProjectSystem.XYToWGS84(x2, y2, 0);
                StaticObject.soProjectSystem.GetCoorSystem().SetCenterMeridian(ProjectSystem.AutoCalCenterJX(Coor1.getX(),Coor1.getY()));
                Coor1 = Project_GK.GK_BLToXY(Coor1.getX(), Coor1.getY(), StaticObject.soProjectSystem.GetCoorSystem());
                Coor2 = Project_GK.GK_BLToXY(Coor2.getX(), Coor2.getY(), StaticObject.soProjectSystem.GetCoorSystem());
                if(Coor1== null)
                {
                    return 0;
                }
                //兼容第一点无效的情况
                if(Coor2 == null)
                {
                    return 10;
                }

                x1 = Coor1.getX();
                y1 = Coor1.getY();
                x2=Coor2.getX();
                y2=Coor2.getY();
//            }
        }
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    //得到坐标列表的长度值
    public static double GetListDistance(List<Coordinate> CoorList)
    {
        double D = 0;
        if (CoorList.size() <= 1) return D;
        for (int i = 0; i < CoorList.size() - 1; i++)
        {
            D += GetTwoPointDistance(CoorList.get(i), CoorList.get(i+1));
        }
        return D;
    }

    /// <summary>已知三点坐标，计算中间点夹角
    /// </summary>
    /// <param name="PointA"></param>
    /// <param name="PointB"></param>
    /// <param name="PointC"></param>
    /// <returns></returns>
    public static double AngleB(Coordinate PointA, Coordinate PointB, Coordinate PointC)
    {
        double AB = GetTwoPointDistance(PointA, PointB);
        double AC = GetTwoPointDistance(PointA, PointC);
        double BC = GetTwoPointDistance(PointB, PointC);
        double CosB = (BC * BC + AB * AB - AC * AC) / (2 * BC * AB);
        return Math.acos(CosB) * (180 / Math.PI);
    }

    //将List<int>形式转换成字符串，按sperate分割
    public static String Join(String sperate, List<Integer> intList)
    {
        String JStr = "";
        for (Integer it : intList)
        {
            JStr += String.valueOf(it) + sperate;
        }
        return JStr.substring(0, JStr.length() - sperate.length());
    }
    public static String JoinT(String sperate, List<String> StrList)
    {
        if (StrList.size()==0) return "";
        String JStr = "";
        for (String it : StrList)
        {
            JStr += String.valueOf(it) + sperate;
        }
        return JStr.substring(0, JStr.length() - sperate.length());
    }
    public static String Joins(String sperate, String[] StrList)
    {
        String JStr = "";
        for (String it : StrList)
        {
            JStr += String.valueOf(it) + sperate;
        }
        return JStr.substring(0, JStr.length() - sperate.length());
    }

    public static int[] IntListToArray(List<Integer> StrList)
    {
        int[] aStringList = new int[StrList.size()];
        int i=0;
        for (Integer it : StrList)
        {
            aStringList[i] = it;i++;
        }
        return aStringList;
    }

    public static String[] StrListToArray(List<String> StrList)
    {
        String[] aStringList = new String[StrList.size()];
        int i=0;
        for (String it : StrList)
        {
            aStringList[i] = it;i++;
        }
        return aStringList;
    }


    public static List<String> StrArrayToList(String[] StrArray)
    {
        List<String> StrList = new ArrayList<String>();
        for (String it : StrArray)
        {
            StrList.add(it);
        }
        return StrList;
    }
    public static String StrListToStr(List<String> StrList)
    {
        String aStringList = "";
        int i=0;
        for (String it : StrList)
        {
            aStringList=aStringList+it+",";
        }
        if (aStringList.length()==0) return "";
        return aStringList.substring(0, aStringList.length()-1);
    }

    //更改文件的扩展名
    public static String ChangeExName(String FilePathAndName,String NewExName)
    {
        String FilePath = GetFilePath(FilePathAndName);
        return FilePath+"/"+GetFileName_NoEx(FilePathAndName)+"."+NewExName;
    }

    //从文件路径中提取文件名称，包括扩展名
    public static String GetFileName(String FilePathAndName)
    {
        int start=FilePathAndName.lastIndexOf("/");
        int end=FilePathAndName.lastIndexOf(".");
        if (start!=-1 && end!=-1)
        {
            return FilePathAndName.substring(start+1, end+4);
        }
        else
        {
            return null;
        }
    }
    //从文件路径中提取文件名称，不包括扩展名
    public static String GetFileName_NoEx(String FilePathAndName)
    {
        int start=FilePathAndName.lastIndexOf("/");
        int end=FilePathAndName.lastIndexOf(".");
        if (start!=-1 && end!=-1)
        {
            return FilePathAndName.substring(start+1, end);
        }
        else
        {
            return null;
        }
    }
    //从文件路径中提取文件路径
    public static String GetFilePath(String FilePathAndName)
    {
        int start=FilePathAndName.lastIndexOf("/");
        if (start!=-1)
        {
            return FilePathAndName.substring(0,start);
        }
        else
        {
            return null;
        }
    }

    //将图片转换为BYte[]
    public static byte[] readStream(String FileName)
    {
        FileInputStream inStream=null;
        try
        {
            inStream = new FileInputStream(FileName);
            byte[] buffer = new byte[1024];
            int len = -1;
            ByteArrayOutputStream outStream;
            try {
                outStream = new ByteArrayOutputStream();
                while((len = inStream.read(buffer)) != -1)
                {
                    outStream.write(buffer, 0, len);
                }
                byte[] data = outStream.toByteArray();
                outStream.close();
                inStream.close();
                return data;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

//    /**
//     * 读取系统的扩展目录，此方法有问题
//     * @return
//     */
//	private static List<String> getExternalPathList()
//	{
//		List<String> pathList = new ArrayList<String>();
//		try
//		{
//			String HEAD = "dev_mount";
//			File VOLD_FSTAB = new File(Environment.getRootDirectory().getAbsoluteFile()
//										+ File.separator
//										+ "etc"
//										+ File.separator
//										+ "vold.fstab");
//			BufferedReader br = new BufferedReader(new FileReader(VOLD_FSTAB));
//			String tmp = null;
//			while ((tmp = br.readLine()) != null)
//			{
//				if (tmp.startsWith(HEAD))
//				{
//					boolean Have = false;
//					String[] sinfo = tmp.trim().split(" ");
//					for(String PathStr:pathList)
//					{
//						if (PathStr.equals(sinfo[2]))Have = true;
//					}
//					if (!Have)pathList.add(sinfo[2]);
//				}
//			}
//			br.close();
//			return pathList;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return pathList;
//	}

    /**
     * 得到所有存储卡的信息列表，
     * @return
     */
    public static List<HashMap<String,Object>> GetAllSDCardInfoList(Context C)
    {
        //SDPath=完整路径,AllSize=全部容量,FreeSize=可用容量
        List<HashMap<String,Object>> SDCardInfoList = new ArrayList<HashMap<String,Object>>();

        //提取存储卡路径
        StorageManager SM = (StorageManager) C.getSystemService(Context.STORAGE_SERVICE);
        List<String> SDCardPathList = new ArrayList<String>();

        try
        {
            Object[] pArgs = null;

            String[] paths = (String[]) SM.getClass().getMethod("getVolumePaths").invoke(SM);
            SDCardPathList = Arrays.asList(paths);
        }
        catch (NoSuchMethodException e1) {}
        catch (IllegalArgumentException e) {}
        catch (IllegalAccessException e) {}
        catch (InvocationTargetException e) {}
        catch(Exception e){};

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            String _SystemPath=Environment.getExternalStorageDirectory().getPath();
            if (!SDCardPathList.contains(_SystemPath))SDCardPathList.add(_SystemPath);
        }

        //判断存储卡路径的有效性
        for(String SDPath:SDCardPathList)
        {
            try
            {
                StatFs sf = new StatFs(SDPath); //获取单个数据块的大小
                long blockSize = sf.getBlockSize(); //获取所有数据块数
                long allBlocks = sf.getBlockCount(); //返回SD卡大小 //
                float allsize = (allBlocks * blockSize)/1024f/1024f/1024f; //G

                String FreeUnit = "MB";
                float allfreen = (blockSize * sf.getAvailableBlocks())/1024f/1024f;  //M
                if (allfreen>1)
                {
                    if (allfreen>1000){allfreen = allfreen / 1024f;FreeUnit = "GB";}  //G
                    HashMap<String,Object> SDCarInfo = new HashMap<String,Object>();
                    SDCarInfo.put("SDPath", SDPath);
                    SDCarInfo.put("AllSize", Tools.ConvertToDigi(allsize+"",3)+" GB");
                    SDCarInfo.put("FreeSize", Tools.ConvertToDigi(allfreen+"",3)+" "+FreeUnit);
                    boolean canWrite = Environment.getExternalStorageDirectory().canWrite();
                    //boolean canWrite = (new File(SDPath)).canWrite();
                    SDCarInfo.put("CanWrite",canWrite);
                    if (canWrite)SDCarInfo.put("Status","正常");
                    else SDCarInfo.put("Status","【只读，无法创建系统目录】");
                    SDCardInfoList.add(SDCarInfo);
                }
            }
            catch (IllegalArgumentException e) {}
            catch(Exception e){}
        }

        return SDCardInfoList;
    }



    public static boolean CopyToFileFromRawID(Context C,String fileName,int rawID)
    {
        if (!Tools.ExistFile(fileName))
        {
            //复制系统文件
            try
            {
                OutputStream out = new FileOutputStream(new File(fileName));
                Resources r = C.getResources();
                InputStream in = r.openRawResource(rawID);
                int length = in.available();
                byte [] buffer = new byte[length];
                in.read(buffer);
                out.write(buffer);
                in.close();
                out.close();
                return true;
            }
            catch(Exception e)
            {
                return false;
            }
        } else return true;

    }



    //检测文件是否存在
    public static boolean ExistFile(String FileName)
    {
        File file = new File(FileName);
        return file.exists();
    }
    private static String FileNameT="";
    public static boolean ExistFileEx(String Path,String FileName)
    {
        FileNameT=FileName;
        File f = new File(Path);
        File[] files=f.listFiles(new Tools.UUIDfileFilter());
        if (files.length>0) return true;else return false;
    }

    /**
     * 删除所有文件
     */
    public static boolean DeleteAll(File f)
    {
        if(!f.exists())
        {
            System.out.println("指定目录不存在:"+f.getName());
        }
        else
        {
            boolean rslt=true;// 保存中间结果
            // 若文件夹非空。枚举、递归删除里面内容
            File subs[] = f.listFiles();
            for (int i = 0; i <= subs.length - 1; i++)
            {
                if (subs[i].isDirectory()) DeleteAll(subs[i]);// 递归删除子文件夹内容
                rslt = subs[i].delete();// 删除子文件夹
            }
            rslt = f.delete();//删除文件夹本身
        }
        return true;
    }


    public static boolean DeleteAllSub(File f)
    {
        if(!f.exists())
        {
            System.out.println("指定目录不存在:"+f.getName());
        }
        else
        {
            boolean rslt=true;// 保存中间结果
            // 若文件夹非空。枚举、递归删除里面内容
            File subs[] = f.listFiles();
            for (int i = 0; i <= subs.length - 1; i++)
            {
                if (subs[i].isDirectory()) DeleteAll(subs[i]);// 递归删除子文件夹内容
                rslt = subs[i].delete();// 删除子文件夹
            }
        }
        return true;
    }

    public static boolean DeleteAllData()
    {

        return false;
    }

    static class UUIDfileFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename)
        {
            if (filename.indexOf(FileNameT)>=0) return true;else return false;
        }
    }


    //复制文件
    public static boolean CopyFile(String From,String To)
    {
        File toFile = new File(To);
        if (!toFile.getParentFile().exists()) toFile.getParentFile().mkdirs();

        InputStream is;OutputStream op;
        try {
            is=new FileInputStream(From);
            op=new FileOutputStream(To);
            BufferedInputStream bis=new BufferedInputStream(is);
            BufferedOutputStream bos=new BufferedOutputStream(op);

            byte[] bt=new byte[8192];
            int len;
            try {
                len = bis.read(bt);
                while(len!=-1)
                {
                    bos.write(bt,0,len);
                    len = bis.read(bt);
                }
                bis.close(); bos.close();
            } catch (IOException e) {

                Log.d("Copy file", "IOException: "+From);
                return false;
            }
            return true;

        } catch (FileNotFoundException e) {
            Log.d("Copy file", "file not found: "+From);
            return false;
        }


    }

    //将List<PointF>转换成float[]
    public static float[] PointListToFloatList(List<PointF> PointList)
    {
        int pStart = 0;
        float[] floatList = new float[PointList.size()* 2];
        for(PointF pt :PointList)
        {
            floatList[pStart] = pt.x;floatList[pStart+1] = pt.y;
            pStart+=2;
        }
        return floatList;

    }

    //将Point[] 转换成  float[]
    public static float[] PointListToFloatList(Point[] PointList)
    {
        int pStart = 0;
        float[] floatList = new float[PointList.length* 2];
        for(Point pt :PointList)
        {
            floatList[pStart] = pt.x;
            floatList[pStart+1] = pt.y;
            pStart+=2;
        }
        return floatList;
    }
    public static float[] PointListToFloatList(PointF[] PointList)
    {
        int pStart = 0;
        float[] floatList = new float[PointList.length* 2];
        for(PointF pt :PointList)
        {
            floatList[pStart] = pt.x;floatList[pStart+1] = pt.y;
            pStart+=2;
        }
        return floatList;
    }

//    /// <summary>将字节流转换成实体，主要用于背景地图的转换TP.dbx
//    /// </summary>
//    /// <param name="bytes"></param>
//    /// <param name="lktype"></param>
//    /// <returns></returns>
//    public static Geometry ByteToGeometryEx(byte[] bytes, lkGeoLayerType lktype)
//    {
//    	Geometry m_Geometry = null;
//    	int Offset = 0;
//      int PartCount = BitConverter.ToInt(BitConverter.Reverse(bytes,0,4), 0);  Offset+=4;      //多部分数量
//      if (lktype == lkGeoLayerType.enPoint)
//      {
//    	  //int FirstPartIndex = BitConverter.ToInt(bytes, 0);
//    	  Offset+=4;      //起始部分的索引
//          double X = BitConverter.ToDouble(bytes, Offset);
//          double Y = BitConverter.ToDouble(bytes, Offset+8);
//          lkmap.Point P = new lkmap.Point(X, Y);
//          m_Geometry = P;
//      }
//      else
//      {
//          List<Integer> PartIndexList = null;
//          if (PartCount > 1)   //表示多部分
//          {
//              PartIndexList = new ArrayList<Integer>();
//              for (int i = 1; i <= PartCount; i++)
//              {
//                  int PartStartIndex = BitConverter.ToInt(BitConverter.Reverse(bytes,Offset,4), 0); Offset+=4;  //部分的起始索引
//                  PartIndexList.add(PartStartIndex);
//              }
//          }
//          else Offset+=4;
//
//          //读取坐标信息
//          List<Coordinate> CoorList = new ArrayList<Coordinate>();
//          int CoorCount = (bytes.length-Offset) / 16;
//          for (int idx = 0; idx <CoorCount ; idx++)
//          {
//              double X = BitConverter.ToDouble(bytes, Offset);Offset+=8;
//              double Y = BitConverter.ToDouble(bytes, Offset);Offset+=8;
//              CoorList.add(new Coordinate(X, Y));
//          }
//
//          //转换为实体
//          if (lktype == lkGeoLayerType.enPolygon)
//          {
//              Polygon PlY = new Polygon();
//              PlY.SetAllCoordinateList(CoorList);
//              if (PartCount > 1){ PlY.ConvertToPolyline().setPartIndex(PartIndexList);PlY.IsSimple(false);PlY.ConvertToPolyline().IsSimple(false);}
//              PlY.ConvertToPolyline().SetAllCoordinateList(CoorList);
//              m_Geometry = PlY;
//          }
//          else
//          {
//              Polyline PL = new Polyline();
//              if (PartCount > 1){ PL.setPartIndex(PartIndexList);PL.IsSimple(false);}
//              PL.SetAllCoordinateList(CoorList);
//              m_Geometry = PL;
//
//          }
//
//      }
//      return m_Geometry;
//    }

    /**
     * 将字节流转换成实体
     * @param bytes 字节数组
     * @param lktype 图形类型
     * @return
     */
    public static Geometry ByteToGeometry(byte[] bytes, lkGeoLayerType lktype)
    {
        int Offset = 0;
        int PartCount = BitConverter.ToInt(bytes,0);  Offset+=4;      //多部分数量

        //读取多部分信息
        List<Integer> partIndexList = new ArrayList<Integer>();
        for (int i = 1; i <= PartCount; i++)
        {
            int partIndex = BitConverter.ToInt(bytes,Offset); Offset+=4;  //部分的起始索引
            partIndexList.add(partIndex);
        }

        //读取坐标信息
        int Step = 24;
        List<Coordinate> CoorList = new ArrayList<Coordinate>();
        for (int idx = 0; idx < (bytes.length-Offset) / Step; idx++)
        {
            double X = BitConverter.ToDouble(bytes, Step * idx+Offset);
            double Y = BitConverter.ToDouble(bytes, Step * idx+Offset + 8);
            double Z = BitConverter.ToDouble(bytes, Step * idx+Offset + 16);
            Coordinate Coor = new Coordinate(X, Y,Z);
            CoorList.add(Coor);
        }

        //构建图形实体
        if (lktype == lkGeoLayerType.enPoint)
        {
            com.DingTu.Cargeometry.Point P = new com.DingTu.Cargeometry.Point(CoorList.get(0));
            return P;
        }
        else
        {
            if (lktype == lkGeoLayerType.enPolyline)
            {
                Polyline PL = new Polyline();

                //分部分增加
                partIndexList.add(CoorList.size());
                for (int idx = 0; idx < partIndexList.size() - 1; idx++)
                {
                    int startIdx = partIndexList.get(idx);
                    int endIdx = partIndexList.get(idx+1);
                    Part pPart = new Part(CoorList.subList(startIdx, endIdx));
                    PL.AddPart(pPart);
                }

                return PL;
            }
            else
            {
                Polygon PLY = new Polygon();
                //分部分增加
                partIndexList.add(CoorList.size());
                for (int idx = 0; idx < partIndexList.size() - 1; idx++)
                {
                    int startIdx = partIndexList.get(idx);
                    int endIdx = partIndexList.get(idx+1);
                    Part pPart = new Part();
                    for(int pi = startIdx;pi<endIdx;pi++)
                    {
                        pPart.getVertexList().add(CoorList.get(pi));
                    }
                    pPart.AutoSetPartType();
                    PLY.AddPart(pPart);
                }
                return PLY;
            }
        }
    }

    //将实体转换为字节流
    public static byte[] GeometryToByte(Geometry pGeometry)
    {
        int SingleCoorSize = 24;

        //所有坐标点的长度
        int byteSize = pGeometry.getVertexCount() * SingleCoorSize;

        //多部分
        int PartCount = pGeometry.getPartCount();
        List<Integer> partIndexList = pGeometry.GetPartIndexList();
        byteSize+=(PartCount+1)*4;

        //写入多部分总数
        int idx = 0;
        byte[] _Tb  = new byte[byteSize];
        byte[] b1 = BitConverter.GetBytes(PartCount);
        ArrayCopy(b1, _Tb, idx);idx+=4;

        //写入多部分索引
        for(int partIdx:partIndexList)
        {
            byte[] b2 = BitConverter.GetBytes(partIdx);
            ArrayCopy(b2, _Tb, idx);idx+=4;
        }

        //写入坐标
        for(int p=0;p<pGeometry.getPartCount();p++)
        {
            Part part = pGeometry.GetPartAt(p);
            for (Coordinate Coor : part.getVertexList())
            {
                byte[] Xb = BitConverter.GetBytes(Coor.getX());
                byte[] Yb = BitConverter.GetBytes(Coor.getY());
                byte[] Zb = BitConverter.GetBytes(Coor.getZ());
                //byte[] geoXb = BitConverter.GetBytes(Coor.getGeoX());
                //byte[] geoYb = BitConverter.GetBytes(Coor.getGeoY());

                ArrayCopy(Xb, _Tb, idx);
                ArrayCopy(Yb, _Tb, idx + 8);
                ArrayCopy(Zb, _Tb, idx + 16);
                //ArrayCopy(geoXb, _Tb, idx + 24);
                //ArrayCopy(geoYb, _Tb, idx + 32);
                idx += SingleCoorSize;
            }
        }
        return _Tb;
    }


    public static void ArrayCopy(byte[] from,byte[] to,int toIndex)
    {
        for(int i=0;i<from.length;i++)
        {
            to[toIndex+i]=from[i];
        }
    }

    //得到唯一选择的实体
    public static boolean GetSelectOneObjectInfo(Param GeoLayerName, Param SYSID)
    {
        //判断是否已经有实体被选中
        int ObjCount = 0;
        List<GeoLayer> pGeoLayerList = PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enAll).getList();
        for (GeoLayer pGeoLayer : pGeoLayerList)
        {
            ObjCount += pGeoLayer.getSelSelection().getCount();
            if (pGeoLayer.getSelSelection().getCount() == 1)
            {
                SYSID.setValue(pGeoLayer.getSelSelection().getGeometryIndexList().get(0));
                GeoLayerName.setValue(pGeoLayer.getId());
            }
        }
        if (ObjCount != 1) { GeoLayerName = null; SYSID =null;return false; } else return true;
    }


    /**
     * 得到选中实体的数量
     * DataSourceType:-1=全部，1=不可编辑，2=可编辑
     * LayerType=-1，全部，LayerType=0,点，LayerTYpe=1，线,LayerTYpe=2，面
     */
    public static int GetSelectObjectsCount(int DataSourceType,int LayerType)
    {
        int ObjCount = 0;
        List<GeoLayer> pGeoLayerList = PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enAll).getList();
        for (GeoLayer pGeoLayer : pGeoLayerList)
        {
            boolean CalCount = false;
            if (DataSourceType==1 && !pGeoLayer.getDataset().getDataSource().getEditing())CalCount=true;
            if (DataSourceType==2 && pGeoLayer.getDataset().getDataSource().getEditing())CalCount=true;
            if (DataSourceType==-1)CalCount=true;
            if (CalCount)
            {
                if (LayerType==-1)ObjCount += pGeoLayer.getSelSelection().getCount();
                else
                {
                    if ((pGeoLayer.getType()== lkGeoLayerType.enPoint && LayerType==0))
                        ObjCount += pGeoLayer.getSelSelection().getCount();
                    if ((pGeoLayer.getType()== lkGeoLayerType.enPolyline && LayerType==1))
                        ObjCount += pGeoLayer.getSelSelection().getCount();
                    if ((pGeoLayer.getType()== lkGeoLayerType.enPolygon && LayerType==2))
                        ObjCount += pGeoLayer.getSelSelection().getCount();
                }
            }
        }

        return ObjCount;
    }


    public static int GetSelectObjectsCount()
    {
        return GetSelectObjectsCount(-1,-1);
    }

    //提示框
    public static void ShowMessageBox(Context C,String Message,final ICallback callBack)
    {
        // 创建AlertDialog
        AlertDialog.Builder menuDialog = new AlertDialog.Builder(C);
        menuDialog.setIcon(R.drawable.messageinfo);
        menuDialog.setTitle(("系统提示"));
        menuDialog.setCancelable(true);
        //menuDialog.setView(listView);
        menuDialog.setMessage(Message);
        menuDialog.setCancelable(false);   //不响应回退键
        menuDialog.setNegativeButton(("确定"), new DialogInterface.OnClickListener()
        {
            @Override
            public   void  onClick(DialogInterface dialog,  int  which)
            {
                if (callBack!=null)callBack.OnClick("OK", "");
                dialog.dismiss();

            }
        });

        menuDialog.show();
    }
    public static void ShowMessageBox(Context C,String Message)
    {
        ShowMessageBox(C,Message,null);
    }
    public static void ShowMessageBox(String Message)
    {
        ShowMessageBox(PubVar.m_DoEvent.m_Context,Message);
    }

    public static void DeletePhoto(Context C,String Filename,final ICallback callback)
    {
        final String FileName = Filename;
        final Context cc = C;
        // 创建AlertDialog
        AlertDialog.Builder menuDialog = new AlertDialog.Builder(C);
        menuDialog.setIcon(R.drawable.messageinfo);
        menuDialog.setTitle(Tools.ToLocale("系统提示"));
        //menuDialog.setView(listView);
        menuDialog.setMessage("是否确定删除此照片？");
        menuDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public   void  onClick(DialogInterface dialog,  int  which)
            {
                File f=new File(FileName);
                f.delete();callback.OnClick("OK", "");
                //((Photo)cc).StartLookImage();
                dialog.dismiss();
            }
        });
        menuDialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public   void  onClick(DialogInterface dialog,  int  which)
            {
                dialog.dismiss();
            }
        });
        menuDialog.show();

    }


    //根据ID号取得控件的文本值
    public static String GetTextValueOnID(Activity A,int Controlid)
    {
        TextView tv = (TextView)A.findViewById(Controlid);
        return String.valueOf(tv.getText());
    }

    //根据ID号取得控件的文本值
    public static String GetTextValueOnID(View A,int Controlid)
    {
        TextView tv = (TextView)A.findViewById(Controlid);
        return String.valueOf(tv.getText());
    }

    public static String GetTextValueOnID(Dialog A,int Controlid)
    {
        TextView tv = (TextView)A.findViewById(Controlid);
        return String.valueOf(tv.getText());
    }
    //根据ID号取得文本控件
    public static TextView GetTextViewOnID(Activity A,int Controlid)
    {
        TextView tv = (TextView)A.findViewById(Controlid);
        return tv;
    }
    public static TextView GetTextViewOnID(View A,int Controlid)
    {
        TextView tv = (TextView)A.findViewById(Controlid);
        return tv;
    }
    public static TextView GetTextViewOnID(Dialog A,int Controlid)
    {
        TextView tv = (TextView)A.findViewById(Controlid);
        return tv;
    }
    public static void SetTextViewValueOnID(Activity A,int Controlid,String Value)
    {
        Tools.GetTextViewOnID(A,Controlid).setText(Value);
    }

    public static void SetTextViewValueOnID(View A,int Controlid,String Value)
    {
        Tools.GetTextViewOnID(A,Controlid).setText(Value);
    }

    public static void SetTextViewValueOnID(Dialog A,int Controlid,String Value)
    {
        Tools.GetTextViewOnID(A,Controlid).setText(Value);
    }

    //根据控件类型赋值
    public static void SetValueToView(String Value,View v)
    {
        if (v==null) return;
        String VType = v.getClass().getName();
        if (VType.equals("android.widget.EditText"))
        {
            ((EditText)v).setText(Value);
        }
        if (VType.equals("android.widget.TextView"))
        {
            ((TextView)v).setText(Value);
        }
        if (VType.equals("android.widget.Spinner"))
        {
            Spinner sn = (Spinner)v;
            if ((ArrayAdapter<CharSequence>)sn.getAdapter()==null) return;
            int p = ((ArrayAdapter<CharSequence>)sn.getAdapter()).getPosition(Value);
            sn.setSelection(p,true);
        }
//        if (VType.equals("lkmap.ZRoadMapData.DataCombox"))
//        {
//            ((lkmap.ZRoadMap.Data.DataCombox)v).setText(Value);
//        }
//
//        if (VType.equals("lkmap.ZRoadMap.MyControl.v1_SpinnerDialog"))
//        {
//            Spinner sp = (Spinner)v;
//            v1_DataBind.SetBindListSpinner(v.getContext(), "", Tools.StrArrayToList(new String[]{Value}), sp);
//        }
//        if (VType.equals("lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog"))
//        {
//            lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog sp = (lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog)v;
//            sp.setText(Value);
//        }
    }

    //得到控件值
    public static String GetViewValue(View v)
    {
        if (v==null)return "";
        String VType = v.getClass().getName();
        if (VType.equals("android.widget.EditText"))
        {
            return String.valueOf(((EditText)v).getText());
        }
        if (VType.equals("android.widget.Spinner"))
        {
            Spinner sp = (Spinner)v;
            String value = String.valueOf(sp.getSelectedItem());
            if(value.startsWith("(") && value.indexOf(")")>-1 && value.indexOf(")")<(value.length()-1))
            {
                value = value.substring(1, value.indexOf(")"));
            }
            return value;
        }
        if (VType.equals("android.widget.TextView"))
        {
            return String.valueOf(((TextView)v).getText());
        }
//        if (VType.equals("lkmap.ZRoadMapData.DataCombox"))
//        {
//            return ((lkmap.ZRoadMap.Data.DataCombox)v).getText();
//        }
//        if (VType.equals("lkmap.ZRoadMap.MyControl.v1_SpinnerDialog"))
//        {
//            Spinner sp = (Spinner)v;
//            String value = String.valueOf(sp.getSelectedItem());
//            if(value.startsWith("(") && value.indexOf(")")>-1 && value.indexOf(")")<(value.length()-1))
//            {
//                value = value.substring(1, value.indexOf(")"));
//            }
//            return value;
//        }
//        if (VType.equals("lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog"))
//        {
//            lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog sp = (lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog)v;
//            String value = sp.getText();
//            if(value.startsWith("(") && value.indexOf(")")>-1 && value.indexOf(")")<(value.length()-1))
//            {
//                value = value.substring(1, value.indexOf(")"));
//            }
//            return value;
//        }
        return "";
    }

    //根据ID号取得控件的文本值
    public static String GetSpinnerValueOnID(Activity A, int Controlid)
    {
        Spinner tv = (Spinner)A.findViewById(Controlid);
        return String.valueOf(tv.getSelectedItem());
    }
    public static String GetSpinnerValueOnID(View A,int Controlid)
    {
        Spinner tv = (Spinner)A.findViewById(Controlid);
        return String.valueOf(tv.getSelectedItem());
    }
    public static String GetSpinnerValueOnID(Dialog A, int Controlid)
    {
        Spinner tv = (Spinner)A.findViewById(Controlid);
        return String.valueOf(tv.getSelectedItem());
    }

    public static boolean GetCheckBoxValueOnID(Dialog A,int Controlid)
    {
        CheckBox tv = (CheckBox)A.findViewById(Controlid);
        return tv.isChecked();
    }

    //根据ID号取得Spinner控件
    public static void SetSpinnerValueOnID(Activity A,int Controlid,String Value)
    {
        Spinner tv = (Spinner)A.findViewById(Controlid);
        if ((ArrayAdapter<CharSequence>)tv.getAdapter()==null) return;
        int p = ((ArrayAdapter<CharSequence>)tv.getAdapter()).getPosition(Value);
        tv.setSelection(p,true);
    }
    public static void SetSpinnerValueOnID(Dialog A,int Controlid,String Value)
    {
        Spinner tv = (Spinner)A.findViewById(Controlid);
        if ((ArrayAdapter<CharSequence>)tv.getAdapter()==null) return;
        int p = ((ArrayAdapter<CharSequence>)tv.getAdapter()).getPosition(Value);
        tv.setSelection(p,true);
    }
    public static void SetCheckValueOnID(Dialog A,int Controlid,boolean Value)
    {
        CheckBox tv = (CheckBox)A.findViewById(Controlid);
        tv.setChecked(Value);
    }

    //根据ID号取得Spinner控件
    public static Spinner GetSpinnerViewOnID(Activity A,int Controlid)
    {
        Spinner tv = (Spinner)A.findViewById(Controlid);
        return tv;
    }


    //检查GPS状态
    public static boolean ReadyGPS(){return ReadyGPS(false);}
    public static boolean ReadyGPS(boolean ShowMessage)
    {
        if (PubVar.m_GPSLocate==null)
        {
            if (ShowMessage)Tools.ShowMessageBox(Tools.ToLocale("请首先开启GPS设备！"));
            return false;
        }
        if (PubVar.m_GPSLocate.m_LTManager==null)
        {
            if (ShowMessage)Tools.ShowMessageBox(Tools.ToLocale("请首先开启GPS设备！"));
            return false;
        }

        if (!PubVar.m_GPSLocate.AlwaysFix())
        {
            if (ShowMessage)Tools.ShowMessageBox(Tools.ToLocale("GPS没定位，请等待..."));
            return false;
        }

        //获取试用用户的到期时间
//        return PubVar.m_DoEvent.m_AuthorizeTools.isExpired(PubVar.SaveDataDate, ShowMessage);
        return true;

    }


    public static String GetDDMMSS(double DDD)
    {
        //DD°MM'SS.SSSS″
        int dd = (int)Math.floor(DDD);
        double MM = (DDD-dd)*60;
        int mm = (int)Math.floor(MM);

        double SS = (MM-mm)*60;
        String ss = Tools.ConvertToDigi(SS+"", 4);
        return dd+"°"+mm+"'"+ss+"″";

    }
    /**
     * 将度转换为度分分
     * @param DDD
     * @return
     */
    public static String GetDDMM(double DDD)
    {
        //DD°MM.MMMMMM"
        int dd = (int)Math.floor(DDD);
        double MM = (DDD-dd)*60;
        return dd+"°"+Tools.ConvertToDigi(MM+"",6)+"'";

    }


    //判断是否数值
    public static float ConvertToFloat(String Str)
    {
        if (Str==null)return 0;
        try
        {
            return Float.parseFloat(Str);
        }
        catch(NumberFormatException e){ return 0;}
    }

    //判断是否数值
    public static double ConvertToDouble(String Str)
    {
        if (Str==null)return 0;
        try
        {
            return Double.parseDouble(Str);
        }
        catch(NumberFormatException e){ return 0;}
    }


    /**
     * 保留任意小数位，并避免科学计数法显示
     * @param V
     * @param Digi
     * @return
     */
    public static String ConvertToDigi(String V,int Digi)
    {
        char[] ch = new char[Digi];
        Arrays.fill(ch, '0');
        String DigiStr = new String(ch);
        DecimalFormat df = new DecimalFormat("0."+DigiStr);
        return df.format(Double.valueOf(V));
    }
    public static String ConvertToDigi(String V)
    {
        BigDecimal bd = new BigDecimal(V);
        return bd.toString();
    }
    public static String ConvertToDigi(double d)
    {
        BigDecimal bd = new BigDecimal(d+"");
        return bd.toString();
    }

    public static String ConvertToDigi(double d,int Digi)
    {
        char[] ch = new char[Digi];
        Arrays.fill(ch, '0');
        String DigiStr = new String(ch);
        DecimalFormat df = new DecimalFormat("0."+DigiStr);
        return df.format(d);
    }

    public static String CalcTuFuHao(Coordinate coord,String scale)
    {
        Coordinate gpsCoor = StaticObject.soProjectSystem.XYToWGS84(coord);
        //DD°MM'SS.SSSS″
        int xdd = (int)Math.floor(gpsCoor.getX());
        double xMM = (gpsCoor.getX()-xdd)*60;
        int xmm = (int)Math.floor(xMM);

        double xSS = (xMM-xmm)*60;
        int xss = (int)Math.floor(xSS);

        int ydd = (int)Math.floor(gpsCoor.getY());
        double yMM = (gpsCoor.getY()-ydd)*60;
        int ymm= (int)Math.floor(yMM);

        double ySS = (yMM-ymm)*60;
        int yss = (int)Math.floor(ySS);


        String firstChar = getFirstChar(ydd,scale);
        int firstNum = (int)Math.floor(xdd/6)+31;

        int secondNum = 0;
        int thirdNum = 0;
        String result = "";

        if(scale.equals("1:1万"))
        {
            secondNum = 96-(int)Math.floor(((ydd*60+ymm+yss/60)%240)/2.5);
            thirdNum= (int)Math.floor(((xdd*60+xmm+xss/60)%360)/3.75)+1;
            result = firstChar+firstNum+"G"+getThreeChar(secondNum)+getThreeChar(thirdNum);
        }
        if(scale.equals("1:5万"))
        {
            secondNum = 240/10-(int)Math.floor(((ydd*60+ymm+yss/60)%240)/10);
            thirdNum= (int)Math.floor(((xdd*60+xmm+xss/60)%360)/15)+1;
            result = firstChar+firstNum+"E"+getThreeChar(secondNum)+getThreeChar(thirdNum);
        }

        //return xdd+"°"+xmm+"'"+xss+"″";
        return result;
    }
    public static String getThreeChar(int number)
    {
        if(number<10)
        {
            return "00"+number;
        }
        else
        {
            if(100>number)
            {
                return "0"+number;
            }
            else
            {
                return number+"";
            }
        }

    }

    public static String getFirstChar(int wd, String scalc)
    {
        int result = (int)Math.floor(wd/4);
        if(scalc.equals("1:1万"))
        {
            if(result == 3)
            {
                return "D";
            }
            if(result == 4)
            {
                return "E";
            }
            if(result == 5)
            {
                return "F";
            }
            if(result == 6)
            {
                return "G";
            }
            if(result == 7)
            {
                return "H";
            }
            if(result == 8)
            {
                return "I";
            }
            if(result == 9)
            {
                return "J";
            }
        }

        if(scalc.equals("1:5万"))
        {
            if(result == 3)
            {
                return "D";
            }
            if(result == 4)
            {
                return "E";
            }
            if(result == 5)
            {
                return "F";
            }
            if(result == 6)
            {
                return "G";
            }
            if(result == 7)
            {
                return "H";
            }
            if(result == 8)
            {
                return "I";
            }
            if(result == 9)
            {
                return "J";
            }
        }

        return "";
    }

    //读取Map目录下有效的图层，有效指定的是目录下要有TP.dbx,TP.idx文件
    public static List<String> GetValidMapSubPath()
    {
        List<String> ValidMapSubPath = new ArrayList<String>();
        String MapPath = PubVar.m_SysAbsolutePath+"/Map";
        File f = new File(MapPath);
        File[] files = f.listFiles();// 列出所有文件
        for(File ff:files)
        {
            if (ff.isDirectory())
            {
                if (Tools.ExistFile(ff+"/TP.dbx") &&
                        Tools.ExistFile(ff+"/TP.idx"))
                {
                    ValidMapSubPath.add(ff.getName());
                }
            }
        }
        return ValidMapSubPath;
    }

    /**
     * 读取系统目录下有效的工程，有效指的是目录下要有Project.dbx及Data文件
     * @return List<文件名称,创建时间>
     */
    public static List<String> GetProjectList()
    {
        List<String> ValidProjectPath = new ArrayList<String>();
        String MapPath = PubVar.m_SysAbsolutePath+"/Data";
        File f = new File(MapPath);
        File[] files = f.listFiles();// 列出所有文件
        for(File ff:files)
        {
            if (ff.isDirectory())
            {
                if (Tools.ExistFile(ff+"/Project.dbx") &&
                        Tools.ExistFile(ff+"/TAData.dbx"))
                {
                    ValidProjectPath.add(ff.getName()+","+ff.lastModified());
                }
            }
        }
        Collections.sort(ValidProjectPath, new FileComparator());//通过重写Comparator的实现类FileComparator来实现按文件创建时间排序。
        return ValidProjectPath;
    }

    public static List<HashMap<String,Object>> GetBKMapListFromFolder(String folderPath,lkMapFileType mapFileType)
    {
        List<HashMap<String,Object>> ValidMapFileList = new ArrayList<HashMap<String,Object>>();
        File f = new File(folderPath);
        File[] files = f.listFiles();// 列出所有文件

        for(File ff:files)
        {
            if (ff.isFile())
            {
                String FileName = ff.getAbsolutePath();
                if (FileName.length()<5) continue;
                String ExtName = FileName.substring(FileName.length()-4,FileName.length());

                if (((mapFileType== lkMapFileType.enVector) && ExtName.toUpperCase().equals(".VMX")) ||
                        ((mapFileType==lkMapFileType.enGrid) && ExtName.toUpperCase().equals(".IMX")))
                {
                    //当前坐标系统
                    CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
                    String CoorSystemInfo = CS.GetName()+"【"+CS.GetCenterMeridian()+"】";
                    if (CS.GetName().equals("WGS-84坐标"))CoorSystemInfo = CS.GetName();

                    //读取背景图信息
                    ASQLiteDatabase bkDB = new ASQLiteDatabase();

                    bkDB.setDatabaseName(FileName);
                    SQLiteDataReader DR = bkDB.Query("select * from MapInfo");
                    String sqlSave="";
                    bkDB.equals(sqlSave);
                    if (DR!=null)while(DR.Read())
                    {
                        String CoorSystemName = DR.GetString("CoorType");
                        String CenterJX = DR.GetString("CenterJX");
                        if (CenterJX.equals(""))CenterJX="0";

                        double MinX = 0,MinY = 0,MaxX=0,MaxY=0;
                        if (mapFileType==lkMapFileType.enGrid)
                        {
                            MinX = DR.GetDouble("Min_X");
                            MinY = DR.GetDouble("Min_Y");
                            MaxX = DR.GetDouble("Max_X");
                            MaxY = DR.GetDouble("Max_Y");
                        }

                        if (CS.GetName().equals("WGS-84坐标"))
                        {
                            if (!CS.GetName().equals(CoorSystemName))continue;
                        }
                        else
                        {
                            if (!(CS.GetName().equals(CoorSystemName) && CS.GetCenterMeridian()==Integer.parseInt(CenterJX)))continue;
                        }

                        //加入返回列表
                        HashMap<String,Object> hm = new HashMap<String,Object>();
                        String BKMapType = "";
                        if (mapFileType==lkMapFileType.enVector)BKMapType = "矢量";
                        if (mapFileType==lkMapFileType.enGrid)BKMapType = "栅格";
                        hm.put("Type", BKMapType);
                        hm.put("Select", true);
                        hm.put("BKMapFile", ff.getName());
                        hm.put("CoorSystem", CoorSystemInfo);
                        hm.put("MinX", MinX);hm.put("MinY", MinY);
                        hm.put("MaxX", MaxX);hm.put("MaxY", MaxY);
                        hm.put("Transparent", 0);
                        hm.put("Visible", true);
                        hm.put("Sort", Integer.MAX_VALUE);
                        hm.put("F1", folderPath);
                        ValidMapFileList.add(hm);
                    }DR.Close();bkDB.Close();
                }

            }
        }
        // Collections.sort(ValidMapFileList, new FileComparator());//通过重写Comparator的实现类FileComparator来实现按文件创建时间排序。
        return ValidMapFileList;
    }

    /**
     * 读取系统目录下有效的底图文件列表
     * @return List<HashMap<String,Object>>
     */
    public static List<HashMap<String,Object>> GetBKMapList(String SubPath,lkMapFileType mapFileType)
    {
        List<HashMap<String,Object>> ValidMapFileList = new ArrayList<HashMap<String,Object>>();
        String MapPath = PubVar.m_SysAbsolutePath+"/Map";
        if (!SubPath.equals(""))MapPath+="/"+SubPath;
        if (!Tools.ExistFile(MapPath)) return ValidMapFileList;
        File f = new File(MapPath);
        File[] files = f.listFiles();// 列出所有文件
        for(File ff:files)
        {
            if (ff.isFile())
            {
                String FileName = ff.getAbsolutePath();
                if (FileName.length()<5) continue;
                String ExtName = FileName.substring(FileName.length()-4,FileName.length());

                if (((mapFileType==lkMapFileType.enVector) && ExtName.toUpperCase().equals(".VMX")) ||
                        ((mapFileType==lkMapFileType.enGrid) && ExtName.toUpperCase().equals(".IMX")))
                {
                    //当前坐标系统
                    CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
                    String CoorSystemInfo = CS.GetName()+"【"+CS.GetCenterMeridian()+"】";
                    if (CS.GetName().equals("WGS-84坐标"))CoorSystemInfo = CS.GetName();

                    //读取背景图信息
                    ASQLiteDatabase bkDB = new ASQLiteDatabase();
                    bkDB.setDatabaseName(FileName);
                    SQLiteDataReader DR = bkDB.Query("select * from MapInfo");
                    if (DR!=null)while(DR.Read())
                    {
                        String CoorSystemName = DR.GetString("CoorType");
                        String CenterJX = DR.GetString("CenterJX");
                        if (CenterJX.equals(""))CenterJX="0";

                        double MinX = 0,MinY = 0,MaxX=0,MaxY=0;
                        if (mapFileType==lkMapFileType.enGrid)
                        {
                            MinX = DR.GetDouble("Min_X");
                            MinY = DR.GetDouble("Min_Y");
                            MaxX = DR.GetDouble("Max_X");
                            MaxY = DR.GetDouble("Max_Y");
                        }

                        if (CS.GetName().equals("WGS-84坐标"))
                        {
                            if (!CS.GetName().equals(CoorSystemName))continue;
                        }
                        else
                        {
                            if (!(CS.GetName().equals(CoorSystemName) && CS.GetCenterMeridian()==Integer.parseInt(CenterJX)))continue;
                        }

                        //加入返回列表
                        HashMap<String,Object> hm = new HashMap<String,Object>();
                        String BKMapType = "";
                        if (mapFileType==lkMapFileType.enVector)BKMapType = "矢量";
                        if (mapFileType==lkMapFileType.enGrid)BKMapType = "栅格";
                        hm.put("Type", BKMapType);
                        hm.put("Select", true);
                        hm.put("BKMapFile", (SubPath.equals("")?ff.getName():SubPath+"/"+ff.getName()));
                        hm.put("CoorSystem", CoorSystemInfo);
                        hm.put("MinX", MinX);hm.put("MinY", MinY);
                        hm.put("MaxX", MaxX);hm.put("MaxY", MaxY);
                        hm.put("Transparent", 0);
                        hm.put("Visible", true);
                        hm.put("F1", MapPath);
                        hm.put("Sort", Integer.MAX_VALUE);
                        ValidMapFileList.add(hm);
                    }DR.Close();bkDB.Close();
                }

            }
        }
        // Collections.sort(ValidMapFileList, new FileComparator());//通过重写Comparator的实现类FileComparator来实现按文件创建时间排序。
        return ValidMapFileList;
    }


    //保存视图范围，
    public static boolean SaveViewExtend()  //ViewExtend=1,2,3,4
    {
        String LeftTop = PubVar.m_Map.getViewConvert().getExtend().getLeftTop().ToString();
        String RightBottom = PubVar.m_Map.getViewConvert().getExtend().getRightBottom().ToString();
        return SaveConfigItem("ViewExtend",LeftTop+","+RightBottom);
    }

    //恢复视图范围
    public static boolean RestoreViewExtend()
    {
        String StrExtend = ReadConfigItem("ViewExtend");
        if (!StrExtend.equals(""))
        {
            String[] ViewExtend = StrExtend.split(",");
            if (ViewExtend.length==4)
            {
                Envelope env = new Envelope(Double.parseDouble(ViewExtend[0]),Double.parseDouble(ViewExtend[1]),Double.parseDouble(ViewExtend[2]),Double.parseDouble(ViewExtend[3]));
                PubVar.m_Map.setExtend(env);
                PubVar.m_Map.Refresh();
                return true;
            }
        }
        return false;
    }

    //配置文件统一放到SysFile/Config.CF中，文件格式：标识=Value
    public static String ReadConfigItem(String ItemName)
    {
        try
        {
            String FPath = PubVar.m_Map.getSystemPath()+"/SysFile/Config.CF";
            if (Tools.ExistFile(FPath))
            {
                FileReader fr = new FileReader(FPath);
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                String ItemValue = "";
                while((line = br.readLine())!=null)
                {
                    if (line.indexOf(ItemName)==0)ItemValue = line.split("=")[1];
                }
                br.close();
                fr.close();
                return ItemValue;
            }
            return "";
        }
        catch(Exception e)
        {
            return "";
        }
    }

    //配置文件统一放到SysFile/Config.CF中，文件格式：标识=Value
    public static boolean SaveConfigItem(String ItemName,String ItemValue)
    {
        try
        {
            String FPath = PubVar.m_Map.getSystemPath()+"/SysFile/Config.CF";
            if (Tools.ExistFile(PubVar.m_Map.getSystemPath())) return false;
            List<String> ItemList = new ArrayList<String>();
            if (Tools.ExistFile(FPath))
            {
                //读取原有的配置项
                FileReader fr = new FileReader(FPath);
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                while((line = br.readLine())!=null)
                {
                    if (line.indexOf(ItemName)<0)
                    {
                        ItemList.add(line);
                    }
                }
                br.close();
                fr.close();
            }

            //保存配置项
            ItemList.add(ItemName+"="+ItemValue);
            FileWriter fw = new FileWriter(FPath);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String Item : ItemList) bw.write(Item+"\r\n");
            bw.close();
            fw.close();

            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public static List<HashMap<String,Object>> ListHMSSToListHMSO(List<HashMap<String,String>> hmList)
    {
        List<HashMap<String,Object>> hmobjList = new ArrayList<HashMap<String,Object>>();
        for(HashMap<String,String> hm:hmList)
        {
            HashMap<String,Object> ho = new HashMap<String,Object>();
            for(String key:hm.keySet())ho.put(key, hm.get(key));
            hmobjList.add(ho);
        }
        return hmobjList;
    }
    public static List<HashMap<String,String>> ListHMSOToListHMSS(List<HashMap<String,Object>> hoList)
    {
        List<HashMap<String,String>> hmobjList = new ArrayList<HashMap<String,String>>();
        for(HashMap<String,Object> hm:hoList)
        {
            HashMap<String,String> ho = new HashMap<String,String>();
            for(String key:hm.keySet())ho.put(key, hm.get(key)+"");
            hmobjList.add(ho);
        }
        return hmobjList;
    }

    /**
     * SP转象素坐标
     * @param sp
     * @return
     */
    public static int SPToPix(int sp)
    {
        return (int) (sp * PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics().scaledDensity+ 0.5f);
    }
    public static int DPToPix(int dp)
    {
        return (int) (dp * PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 设置工具条项目的状态
     * @param buttonView
     * @param ifSelect
     */
    public static void SetToolsBarItemSelect(View buttonView,boolean ifSelect)
    {
//    	int resourceId = R.drawable.buttonstyle_edittoolbar;
//    	if (ifSelect)resourceId = R.drawable.buttonstyle_edittoolbar_select;
//
//    	//获取资源
//    	Resources res = PubVar.m_DoEvent.m_Context.getResources();
//    	Drawable draw=res.getDrawable(resourceId);
//
//    	//设置背景图
//    	buttonView.setBackgroundDrawable(draw);
        buttonView.setSelected(ifSelect);
    }

    /**
     * 根据指定资源Id获取图片
     * @param resourceId
     * @return
     */
    public static Bitmap GetBitmapByResources(int resourceId)
    {
        return BitmapFactory.decodeResource(PubVar.m_DoEvent.m_Context.getResources(), resourceId);
    }


//    /**
//     * 更新工具条上选择按钮的上面的被选择实体数量值
//     */
//    public static void UpdateShowSelectCount()
//    {
//        if (PubVar.m_DoEvent.m_MainBottomToolBar!=null)
//            PubVar.m_DoEvent.m_MainBottomToolBar.UpdateShowSelectCount();
//    }
//
//    public static void UpdateScaleBar()
//    {
//        PubVar.m_DoEvent.m_GpsInfoManage.UpdateScaleBar();
//    }
}