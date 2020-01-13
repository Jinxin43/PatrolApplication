package com.DingTu.Project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.DingTu.Base.Tools;
import com.DingTu.Dataset.ASQLiteDatabase;
import com.DingTu.Dataset.SQLiteDataReader;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Symbol.LineSymbol;
import com.DingTu.Symbol.PointSymbol;
import com.DingTu.Symbol.PolySymbol;
import com.DingTu.Symbol.SymbolObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class SymbolExplorer {

    private ASQLiteDatabase m_ASQLiteDatabase = null;

    /**
     * 绑定配置操作类
     * @param asqlitedb
     */
    public void SetBindSQLiteDatabase(ASQLiteDatabase asqlitedb)
    {
        this.m_ASQLiteDatabase = asqlitedb;
    }

    /**
     * 获取符号示例图形
     * @param SymbolBase64Str  符号内容
     * @param geoLayerType  符号类型
     * @return
     */
    public SymbolObject GetSymbolObject(String SymbolBase64Str, lkGeoLayerType geoLayerType)
    {
        return this.GetSymbolObject(SymbolBase64Str, geoLayerType,64);
    }
    public SymbolObject GetSymbolObject(String SymbolBase64Str,lkGeoLayerType geoLayerType,int Width)
    {
        SymbolObject SO = new SymbolObject();
        int H = 40,W = Width;
        if (geoLayerType==lkGeoLayerType.enPoint)
        {
            PointSymbol PS = new PointSymbol();
            PS.CreateByBase64(SymbolBase64Str);
            SO.SymbolFigure = PS.ToFigureBitmap(W,H);
            SO.SymbolBase64Str = SymbolBase64Str;
        }
        if (geoLayerType==lkGeoLayerType.enPolyline)
        {
            LineSymbol LS = new LineSymbol();
            LS.CreateByBase64(SymbolBase64Str);
            SO.SymbolFigure = LS.ToFigureBitmap(W, H);
            SO.SymbolBase64Str = SymbolBase64Str;
        }
        if (geoLayerType==lkGeoLayerType.enPolygon)
        {
            PolySymbol PS = new PolySymbol();
            PS.CreateByBase64(SymbolBase64Str);
            SO.SymbolFigure = PS.ToFigureBitmap(W, H);
            SO.SymbolBase64Str = SymbolBase64Str;
        }
        return SO;
    }

    /**
     * 获取符号示例图形
     * @param SymbolNameList  符号名称数组，如果为string[]则为读取全部符号
     * @param geoLayerType  符号类型
     * @return
     */
    public List<SymbolObject> GetSymbolObjectList(String[] SymbolNameList,lkGeoLayerType geoLayerType)
    {
        //小图片的大小
        int H = 40,W = 64;

        //查询条件
        String WhereName = "1=1";
        if (SymbolNameList!=null)if (SymbolNameList.length>0)WhereName = " name in ('"+ Tools.Joins("','", SymbolNameList)+"')";

        //点符号
        if (geoLayerType==lkGeoLayerType.enPoint)
        {
            List<SymbolObject> SymbolFigureList = new ArrayList<SymbolObject>();
            String SQL = "Select Name,Symbol from T_PointSymbol where "+WhereName;
            SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
            if (DR==null)return null;
            while(DR.Read())
            {
                SymbolObject SO = new SymbolObject();
                byte[] symByte = DR.GetBlob("Symbol");
                SO.SymbolName = DR.GetString("Name");
                if (symByte!=null)
                {
                    Bitmap sbp = BitmapFactory.decodeByteArray(symByte, 0, symByte.length);
                    PointSymbol PS = new PointSymbol();
                    PS.setIcon(sbp);
                    SO.SymbolFigure = PS.ToFigureBitmap(sbp.getWidth(),sbp.getHeight());
                    SO.SymbolBase64Str = PS.ToBase64();
                }
                SymbolFigureList.add(SO);

            }DR.Close();
            return SymbolFigureList;
        }

        //线符号
        if (geoLayerType==lkGeoLayerType.enPolyline)
        {
            List<SymbolObject> SymbolFigureList = new ArrayList<SymbolObject>();
            String SQL = "Select Name,Symbol from T_LineSymbol where "+WhereName;
            SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
            if (DR==null)return null;
            while(DR.Read())
            {
                SymbolObject SO = new SymbolObject();
                String SymStr = DR.GetString("Symbol");
                SO.SymbolName = DR.GetString("Name");
                if (!SymStr.equals(""))
                {
                    LineSymbol LS = new LineSymbol();
                    LS.CreateByBase64(SymStr);
                    SO.SymbolFigure = LS.ToFigureBitmap(120, H);
                    SO.SymbolBase64Str = LS.ToBase64();
                }
                SymbolFigureList.add(SO);

            }DR.Close();
            return SymbolFigureList;
        }

        //面符号
        if (geoLayerType==lkGeoLayerType.enPolygon)
        {
            List<SymbolObject> SymbolFigureList = new ArrayList<SymbolObject>();
            String SQL = "Select Name,PColor,LColor,LWidth from T_PolySymbol where "+WhereName;
            SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
            if (DR==null)return null;
            while(DR.Read())
            {
                SymbolObject SO = new SymbolObject();
                String PColor = DR.GetString("PColor");
                String LColor = DR.GetString("LColor");
                String LWidth = DR.GetString("LWidth");
                SO.SymbolName = DR.GetString("Name");
                PolySymbol PS = new PolySymbol();
                PS.CreateByBase64(PColor+","+LColor+","+LWidth);
                SO.SymbolBase64Str = PS.ToBase64();
                SO.SymbolFigure = PS.ToFigureBitmap(W, H);
                SymbolFigureList.add(SO);

            }DR.Close();
            return SymbolFigureList;
        }

        return null;

    }

//	/**
//	 * 获取点符号
//	 * @param PointSymbolName
//	 * @return
//	 */
//	public PointSymbol GetPointSymbol(String PointSymbolName)
//	{
//		//读取符号
//		PointSymbol PS = new PointSymbol();
//		if (PointSymbolName.equals(""))PointSymbolName = "默认";
//		byte[] symByte = null;
//		String SQL = "Select Symbol from T_PointSymbol where name = '"+PointSymbolName+"'";
//		SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
//		if (DR==null)return PS;
//		if(DR.Read())
//		{
//			symByte = DR.GetBlob("Symbol");
//		}DR.Close();
//		if (symByte!=null) {PS.setIcon(BitmapFactory.decodeByteArray(symByte, 0, symByte.length));return PS;}
//		else return PS;
//	}
//
//	/**
//	 * 获取线符号
//	 * @param LineSymbolName
//	 * @return
//	 */
//	public LineSymbol GetLineSymbol(String LineSymbolName)
//	{
//		//读取线符号，
//        LineSymbol LS = new LineSymbol();
//        if (LineSymbolName.equals(""))LineSymbolName = "默认";
//
//        //Symbol格式：颜色,宽度@颜色,宽度@颜色,宽度...
//        String symbolInfo = "";
//        String SQL = "Select Symbol from T_LineSymbol where name = '"+LineSymbolName+"'";
//		SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
//		if (DR==null)return LS;
//		if(DR.Read())
//		{
//			symbolInfo = DR.GetString("Symbol");
//		}DR.Close();
//
//		String[] symList = symbolInfo.split("@");
//		List<Paint> pPenList = new ArrayList<Paint>();
//		for(String sym:symList)  //格式：颜色,宽度,线样式
//		{
//	        Paint pPen = new Paint();
//	        pPen.setAntiAlias(true);
//	        pPen.setStyle(android.graphics.Paint.Style.STROKE);
//	        if (!sym.equals(""))
//	        {
//	        	String[] symStyle = sym.split(",");
//		        pPen.setColor(Color.parseColor(symStyle[0]));
//		        pPen.setStrokeWidth(Float.valueOf(symStyle[1]));
//		        if (symStyle.length==3)
//		        {
//		        	String[] syList = symStyle[2].split("-");
//		        	float[] syFList = new float[syList.length];
//		        	for(int i=0;i<syList.length;i++)syFList[i]=Float.parseFloat(syList[i]);
//			        PathEffect effects = new DashPathEffect(syFList,0);
//			        pPen.setPathEffect(effects);
//		        }
//	        }
//	        pPenList.add(pPen);
//		}
//		LS.setStyle(pPenList);
//        return LS;
//	}
//
//	/**
//	 * 获取线符号
//	 * @param LineSymbolName
//	 * @return
//	 */
//	public PolySymbol GetPolySymbol(String PolySymbolName)
//	{
//		//读取面符号，
//		PolySymbol PS = new PolySymbol();
//        if (PolySymbolName.equals(""))PolySymbolName = "默认";
//
//        String SQL = "Select * from T_PolySymbol where name = '"+PolySymbolName+"'";
//		SQLiteDataReader DR = this.m_ASQLiteDatabase.Query(SQL);
//		if (DR==null)return PS;
//		if(DR.Read())
//		{
//			String PColor = DR.GetString("PColor");
//			String LColor = DR.GetString("LColor");
//			float LWidth = Float.parseFloat(DR.GetString("LWidth"));
//			Paint PBrush = new Paint();Paint LBrush = new Paint();
//			PBrush.setColor(Color.parseColor(PColor));
//			PBrush.setStyle(Paint.Style.FILL);
//			PBrush.setAntiAlias(true);
//
//			LBrush.setColor(Color.parseColor(LColor));
//			LBrush.setStyle(Paint.Style.STROKE);
//			LBrush.setAntiAlias(true);
//			LBrush.setStrokeWidth(LWidth);
//
//			PS.setPStyle(PBrush);PS.setLStyle(LBrush);
//		}DR.Close();
//
//        return PS;
//	}
}
