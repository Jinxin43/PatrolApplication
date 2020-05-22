package com.DingTu.Render;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.DingTu.Base.Tools;
import com.DingTu.Dataset.SQLiteDataReader;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkRenderType;
import com.DingTu.Layer.GeoLayer;
import com.DingTu.Symbol.ISymbol;
import com.DingTu.Symbol.LineSymbol;
import com.DingTu.Symbol.PointSymbol;
import com.DingTu.Symbol.PolySymbol;
import com.DingTu.Symbol.SymbolConvertTools;
import com.DingTu.Symbol.TextSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.DingTu.Cargeometry.Geometry;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class SimpleRender extends IRender {

    //符号
    private ISymbol _Symbol = null;
    private GeoLayer _GeoLayer = null;
    public SimpleRender(GeoLayer pGeoLayer)
    {
        //渲染器类型
        this.setType(lkRenderType.enSimple);

        //此渲染器所渲染的图层
        this._GeoLayer = pGeoLayer;

        //分类读取符号
        if (this._GeoLayer.getType()== lkGeoLayerType.enPoint)this._Symbol = new PointSymbol();
        if (this._GeoLayer.getType()==lkGeoLayerType.enPolyline)this._Symbol = new LineSymbol();
        if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)this._Symbol = new PolySymbol();
    }

    //图层符号
    public ISymbol getSymbol()
    {
        return this._Symbol;
    }

    /**
     * 设置图层符号，点符号格式：则Value为Base64格式，
     * 				线符号格式：颜色1,宽度1,线型定义1@颜色2,宽度2,线型定义2.....
     * 				面符号格式：面色,边线色,边线宽
     * @param value
     */
    public void setSymbol(String value)
    {
        this._Symbol = SymbolConvertTools.StrToSymbol(value,this._GeoLayer.getType());

        if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)
        {
            //透明度
            ((PolySymbol)this._Symbol).SetTransparent(this._SymbolTransparent);
        }
    }

    //符号透明度（目前只针对面符号）
    private int _SymbolTransparent = 0;
    public void SetSymbolTransparent(int transparent)
    {
        this._SymbolTransparent = transparent;
        if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)
        {
            if (this._Symbol!=null)((PolySymbol)this._Symbol).SetTransparent(this._SymbolTransparent);
        }
    }


    /**
     * 根据图层配置，更新图层符号Symbol
     */
    @Override
    public void UpdateSymbolSet()
    {
        //标注符号
        if (this.getIfLabel())
        {
            String labelFont = this.getLabelFont();  //格式：颜色,大小
            Paint _Font = new Paint();
            _Font.setAntiAlias(true);
            _Font.setTextSize(Float.valueOf(labelFont.split(",")[1]));
            Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
            _Font.setTypeface(TF);
            _Font.setColor(Color.parseColor(labelFont.split(",")[0]));
            TextSymbol TS = new TextSymbol();
            TS.setTextFont(_Font);
            if(_Symbol!=null) {
                this._Symbol.setTextSymbol(TS);
            }
        }
    }

    //只更新指定索引实体的符号
    @Override
    public void UpdateSymbol(Geometry pGeometry)
    {
        if (pGeometry!=null) {
            pGeometry.setSymbol(this._Symbol);
        }
    }

    //更新标注信息
    @Override
    public void UpdateAllLabel()
    {
        //在此加读取标注值
        String SQL = "select %1$s from "+this._GeoLayer.getDataset().getDataTableName()+" where SYS_ID in (%2$s) and SYS_STATUS='0'";
        String SelectField = "SYS_ID,(" + this.getLabelField().replace(",","||','||") + ") as LabelField";
        List<String> SYSID = new ArrayList<String>();
        Collection<Geometry> geometryList = this._GeoLayer.getDataset().GetGeometryList();
        for(Geometry pGeometry:geometryList) {
            SYSID.add(pGeometry.getSysId() + "");
        }
        if (SYSID.size()==0) return;
        SQL = String.format(SQL, SelectField, Tools.JoinT(",", SYSID));
        SQLiteDataReader DR = this._GeoLayer.getDataset().getDataSource().Query(SQL);
        if (DR!=null)while(DR.Read())
        {
            String SYS_ID = DR.GetString("SYS_ID");
            Geometry pGeome = this._GeoLayer.getDataset().GetGeometry(Integer.parseInt(SYS_ID));
            pGeome.setTag(DR.GetString("LabelField"));
        }

        DR.Close();
    }
}
