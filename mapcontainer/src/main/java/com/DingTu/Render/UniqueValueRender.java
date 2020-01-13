package com.DingTu.Render;

import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import com.DingTu.Symbol.SymbolObject;
import com.DingTu.Symbol.TextSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.DingTu.Cargeometry.Geometry;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class UniqueValueRender extends IRender {
    //图层
    private GeoLayer _GeoLayer = null;
    public UniqueValueRender(GeoLayer pGeoLayer)
    {
        //渲染器类型
        this.setType(lkRenderType.enUniqueValue);

        //此渲染器所渲染的图层
        this._GeoLayer = pGeoLayer;

        //缺省符号
        if (this._GeoLayer.getType()== lkGeoLayerType.enPoint)this._DefaultSymbol = new PointSymbol();
        if (this._GeoLayer.getType()==lkGeoLayerType.enPolyline)this._DefaultSymbol = new LineSymbol();
        if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)this._DefaultSymbol = new PolySymbol();

    }

    /**
     * 创建彩条显示符号
     * @return
     */
    public static SymbolObject CreateMSymbolObject(int W,int H)
    {
        //创建彩条样式，表示为多值符号
        SymbolObject SO = new SymbolObject();
        Bitmap bp = Bitmap.createBitmap(W,H, Bitmap.Config.ARGB_8888);
        Canvas g = new Canvas(bp);
        int[] colorList = new int[]{Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.BLACK};
        Paint paint = new Paint();paint.setStyle(Paint.Style.FILL);
        for(int i=0;i<5;i++)
        {
            float subW = (float)W / 5f;
            paint.setAlpha(125);
            paint.setColor(colorList[i]);
            g.drawRect(i*subW, 2, (i+1)*subW, H-2, paint);
        }
        SO.SymbolFigure = bp;
        return SO;
    }

    //唯一值字段
    private List<String> _UniqueValueFieldList = new ArrayList<String>();    //等级，编码,....
    public List<String> GetUniqueValueFieldList(){return this._UniqueValueFieldList;}
    public void SetUniqueValueFieldList(List<String> valueList){this._UniqueValueFieldList = valueList;}

    //唯一值
    private List<String> _UniqueValueList= new ArrayList<String>();     //一级,G201|二级,G201|三级,G301|....
    public List<String> GetUniqueValueList() {return this._UniqueValueList; }
    public void SetUniqueValueList(List<String> value) {this._UniqueValueList = value;}

    //唯一值符号名列表
    private List<ISymbol> _SymbolList = new ArrayList<ISymbol>();

    public List<ISymbol> GetUniqueSymbolList(){return this._SymbolList;}
    /**
     * 设置唯一值符号,Base641&Base642&Base643...
     */
    public void SetUniqueSymbolList(List<String> base64List)
    {
        this._SymbolList.clear();
        for(String sym:base64List)
        {
            this._SymbolList.add(SymbolConvertTools.StrToSymbol(sym, this._GeoLayer.getType()));
        }
        this.SetSymbolTransparent(this._SymbolTransparent);
    }

    //缺省符号
    private ISymbol _DefaultSymbol = null;
    public void SetDefaultSymbol(String symbolValue)
    {
        this._DefaultSymbol = SymbolConvertTools.StrToSymbol(symbolValue, this._GeoLayer.getType());
    }
    public ISymbol getDefaultSymbol()
    {
        return this._DefaultSymbol;
    }

    //符号透明度（目前只针对面符号）
    private int _SymbolTransparent = 0;
    public void SetSymbolTransparent(int transparent)
    {
        this._SymbolTransparent = transparent;
        if (this._GeoLayer.getType()==lkGeoLayerType.enPolygon)
        {
            for(ISymbol pSymbol:this._SymbolList)
            {
                if (pSymbol!=null)((PolySymbol)pSymbol).SetTransparent(this._SymbolTransparent);
            }
            if (this._DefaultSymbol!=null)((PolySymbol)this._DefaultSymbol).SetTransparent(this._SymbolTransparent);
        }
    }

    //更新图层实体的符号
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
            for(ISymbol pSymbol : this._SymbolList)pSymbol.setTextSymbol(TS);
        }
    }

    //只更新指定索引实体的符号
    @Override
    public void UpdateSymbol(Geometry pGeometry)
    {
        if (pGeometry==null) return;
        if(this._UniqueValueList==null)
        {
            pGeometry.setSymbol(this.getDefaultSymbol());
        }
        else
        {
            for (int idx = 0;idx<this._UniqueValueList.size();idx++)
            {
                if (this._UniqueValueList.get(idx).equals(pGeometry.getTagForUniqueSymbol()))
                {
                    pGeometry.setSymbol(this._SymbolList.get(idx)); return;
                }
            }
            pGeometry.setSymbol(this.getDefaultSymbol());
        }
    }

    /**
     * 更新所有实体的唯一值
     */
    public void UpdateAllUniqueValue()
    {
        //在此加唯一值渲染值
        String SQL = "select %1$s from "+this._GeoLayer.getDataset().getDataTableName()+" where SYS_ID in (%2$s) and SYS_STATUS='0'";
        String SelectField = "SYS_ID";
        if (this.GetUniqueValueFieldList().size()==0) return;
        SelectField += ",(" + Tools.JoinT("||','||", this.GetUniqueValueFieldList()) + ") as UniqueValueField";
        List<String> SYSID = new ArrayList<String>();
        Collection<Geometry> geometryList = this._GeoLayer.getDataset().GetGeometryList();
        for(Geometry pGeometry:geometryList)SYSID.add(pGeometry.getSysId()+"");
        if (SYSID.size()==0) return;
        SQL = String.format(SQL, SelectField,Tools.JoinT(",", SYSID));
        SQLiteDataReader DR = this._GeoLayer.getDataset().getDataSource().Query(SQL);
        if (DR!=null)while(DR.Read())
        {
            String SYS_ID = DR.GetString("SYS_ID");
            Geometry pGeometry = this._GeoLayer.getDataset().GetGeometry(Integer.parseInt(SYS_ID));
            pGeometry.setTagForUniqueSymbol(DR.GetString("UniqueValueField"));
        }DR.Close();
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
        for(Geometry pGeometry:geometryList)SYSID.add(pGeometry.getSysId()+"");
        if (SYSID.size()==0) return;
        SQL = String.format(SQL, SelectField,Tools.JoinT(",", SYSID));
        SQLiteDataReader DR = this._GeoLayer.getDataset().getDataSource().Query(SQL);
        if (DR!=null)while(DR.Read())
        {
            String SYS_ID = DR.GetString("SYS_ID");
            Geometry pGeometry = this._GeoLayer.getDataset().GetGeometry(Integer.parseInt(SYS_ID));
            pGeometry.setTag(DR.GetString("LabelField"));
        }DR.Close();
    }
}
