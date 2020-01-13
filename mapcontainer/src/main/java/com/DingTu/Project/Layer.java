package com.DingTu.Project;

import android.graphics.Bitmap;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkEditMode;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkRenderType;
import com.DingTu.Render.UniqueValueRender;
import com.DingTu.Symbol.LineSymbol;
import com.DingTu.Symbol.PointSymbol;
import com.DingTu.Symbol.PolySymbol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class Layer {

    public Layer()
    {
        this.SetRenderType(lkRenderType.enSimple);
    }

    public String Tag = "";

    //编辑模式
    private lkEditMode _EditMode = lkEditMode.enUnkonw;

    /**
     * 设置编辑模式
     * @param editMode
     */
    public void SetEditMode(lkEditMode editMode){this._EditMode = editMode;}
    public lkEditMode GetEditMode(){return this._EditMode;}


    //图层名称
    private String _LayerAliasName = "";
    public String GetLayerAliasName(){return this._LayerAliasName;}
    public void SetLayerAliasName(String layerAliasName){this._LayerAliasName=layerAliasName;}

    //图层ID值
    private String _LayerID = "T"+(UUID.randomUUID().toString()).replace("-", "").toUpperCase();
    public String GetLayerID(){return this._LayerID;}
    public void SetLayerID(String lyrID){this._LayerID = lyrID;}

    //取得数据表以及索引表名称
    public String GetDataTableName(){return this._LayerID+"_D";}
    public String GetIndexTableName(){return this._LayerID+"_I";}

    //图层类型
    private lkGeoLayerType _LayerType = lkGeoLayerType.enUnknow;
    public lkGeoLayerType GetLayerType(){return this._LayerType;}
    public void SetLayerType(lkGeoLayerType layerType){this._LayerType = layerType; }

    public String GetLayerTypeName()
    {
        if (this._LayerType == lkGeoLayerType.enPoint) return "点";
        if (this._LayerType == lkGeoLayerType.enPolyline) return "线";
        if (this._LayerType == lkGeoLayerType.enPolygon) return "面";
        return "";
    }
    public void SetLayerTypeName(String _layerType)
    {
        if (_layerType.equals("点"))this._LayerType = lkGeoLayerType.enPoint;
        if (_layerType.equals("线"))this._LayerType = lkGeoLayerType.enPolyline;
        if (_layerType.equals("面"))this._LayerType = lkGeoLayerType.enPolygon;
    }

    //林业工程类型
    private String layerProjectType = "";
    public String GetLayerProjecType()
    {
        if(layerProjectType == null)
        {
            return "自定义工程";
        }

        return layerProjectType;
    }

    public void SetLayerProjectType(String layproType)
    {
        layerProjectType=layproType;
    }

    String tuigengCity ="";
    public void setCity(String city)
    {
        tuigengCity = city;
    }

    public String getCity()
    {
        return tuigengCity;
    }

    String tuigengCounty ="";
    public void setCounty(String county)
    {
        tuigengCounty = county;
    }

    public String getCounty()
    {
        return tuigengCounty;
    }

    String tuigengYear ="";
    public void setYear(String year)
    {
        tuigengYear = year;
    }
    public String getYear()
    {
        return tuigengYear;
    }

    String weipiandataLayer = "";
    public String getWeiPianDataLayer()
    {
        return weipiandataLayer;
    }
    public void setWeipianDataLayer(String dataLayer)
    {
        weipiandataLayer = dataLayer;
    }

    //图层可见性
    private boolean _Visible = true;
    public boolean GetVisible(){return this._Visible;}
    public void SetVisible(boolean visible){this._Visible=visible;}

    //最小可见比例
    private double _VisibleScaleMin = 0;
    public void SetVisibleScaleMin(double min){this._VisibleScaleMin=min;}
    public double GetVisibleScaleMin(){return this._VisibleScaleMin;}

    //最大可见比例
    private double _VisibleScaleMax = Integer.MAX_VALUE;
    public void SetVisibleScaleMax(double max){this._VisibleScaleMax=max;}
    public double GetVisibleScaleMax(){return this._VisibleScaleMax;}

    //符号透明度（目前只针对面符号）
    private int _SymbolTransparent = 0;  //0不透明
    public void SetTransparent(int transparent){this._SymbolTransparent=transparent;}
    public int GetTransparet(){return this._SymbolTransparent;}

    //是否标注
    private boolean _IfLabel = false;
    public void SetIfLabel(boolean ifLabel){this._IfLabel = ifLabel;}
    public boolean GetIfLabel(){return this._IfLabel;}

    //标注字段
    private List<String> _LabelFieldList = new ArrayList<String>();
    private List<String> _LabelDataFieldList = new ArrayList<String>();
    public String GetLabelFieldStr(){return Tools.JoinT(",",this._LabelFieldList);}
    public String GetLabelDataFieldStr(){return Tools.JoinT(",",this._LabelDataFieldList);}

    /**
     * 设置标注字段，格式：字段1,字段2,.....，逗号分隔
     * @param labelField
     */
    public void SetLabelDataField(String labelField)
    {
        this._LabelFieldList.clear();
        this._LabelDataFieldList.clear();

        String[] fieldList = labelField.split(",");
        for(String field:fieldList)
        {
            for(LayerField LF :this.GetFieldList())
            {
                if (LF.GetDataFieldName().equals(field))
                {
                    this._LabelFieldList.add(LF.GetFieldName());
                    this._LabelDataFieldList.add(LF.GetDataFieldName());
                }
            }
        }
    }

    //标注样式（颜色,大小）
    private String _LabelFont = "#000000,25";

    /**
     * 标注符号，格式：颜色,大小
     * @return
     */
    public String GetLabelFont(){if (this._LabelFont.equals(""))_LabelFont = "#000000,10";return this._LabelFont;}
    public void SetLabelFont(String labelFont){this._LabelFont=labelFont;}

    //图层标注的最大最小范围
    private double _LabelScaleMin=0,_LabelScaleMax=Integer.MAX_VALUE;
    public void SetLabelScaleMin(double min){this._LabelScaleMin = min;}
    public double GetLabelScaleMin(){return this._LabelScaleMin;}
    public void SetLabelScaleMax(double max){this._LabelScaleMax = max;}
    public double GetLabelScaleMax(){return this._LabelScaleMax;}

    //图层的最大最小范围
    private double _MinX=0,_MinY=0,_MaxX=0,_MaxY=0;
    public void SetMinX(double minx){this._MinX = minx;}
    public double GetMinX(){return this._MinX;}
    public void SetMinY(double miny){this._MinY = miny;}
    public double GetMinY(){return this._MinY;}
    public void SetMaxX(double maxx){this._MaxX = maxx;}
    public double GetMaxX(){return this._MaxX;}
    public void SetMaxY(double maxy){this._MaxY = maxy;}
    public double GetMaxY(){return this._MaxY;}


    //是否可选择
    private boolean _Selectable = true;
    public boolean GetSelectable(){return this._Selectable;}
    public void SetSelectable(boolean selectable){this._Selectable=selectable;}

    //是否可编辑
    private boolean _Editable = true;
    public boolean GetEditable(){return this._Editable;}
    public void SetEditable(boolean editable){this._Editable=editable;}

    //是否可捕捉
    private boolean _Snapable = true;
    public boolean GetSnapable(){return this._Snapable;}
    public void SetSnapable(boolean snapable){this._Snapable=snapable;}


    //符号类型，默认为单符号
    private lkRenderType _RenderType = lkRenderType.enSimple;
    public void SetRenderType(lkRenderType renderType)
    {
        this._RenderType = renderType;
        if (this._UniqueSymbolInfoList==null)
        {
            this._UniqueSymbolInfoList = new HashMap<String,Object>();
            this._UniqueSymbolInfoList.put("UniqueValueField", new ArrayList<String>());
            this._UniqueSymbolInfoList.put("UniqueValueList", new ArrayList<String>());
            this._UniqueSymbolInfoList.put("UniqueSymbolList", new ArrayList<String>());
            this._UniqueSymbolInfoList.put("UniqueDefaultSymbol", "");
        }
    }
    public void SetRenderTypeInt(int renderTypeInt)
    {
        if (renderTypeInt==1)this.SetRenderType(lkRenderType.enSimple);
        if (renderTypeInt==2)this.SetRenderType(lkRenderType.enUniqueValue);
    }
    public lkRenderType GetRenderType(){return this._RenderType;}
    public int GetRenderTypeInt()
    {
        if (this._RenderType==lkRenderType.enSimple) return 1;
        if (this._RenderType==lkRenderType.enUniqueValue)return 2;
        return 0;
    }

    //唯一值符号信息
    private HashMap<String,Object> _UniqueSymbolInfoList = null;
    public HashMap<String,Object> GetUniqueSymbolInfoList(){return this._UniqueSymbolInfoList;}



    //单值符号
    private String _SimpleSymbol = "";  //注意此处应该为符号实际内容
    public String GetSimpleSymbol()
    {
        if (!this._SimpleSymbol.equals(""))return _SimpleSymbol;
        if (this._LayerType == lkGeoLayerType.enPoint) {this._SimpleSymbol = (new PointSymbol()).ToBase64();}
        if (this._LayerType == lkGeoLayerType.enPolyline) {this._SimpleSymbol = (new LineSymbol()).ToBase64();}
        if (this._LayerType == lkGeoLayerType.enPolygon)
        {
            this._SimpleSymbol = (new PolySymbol()).ToBase64();
        }
        return this._SimpleSymbol;
    }
    public void SetSimpleSymbol(String Sym)
    {
        this._SimpleSymbol=Sym;
    }


    //符号指示图
    public Bitmap GetSymbolFigure()
    {
        //图层符号指示图
        if(this.GetRenderType()==lkRenderType.enSimple)
            return PubVar.m_DoEvent.m_ConfigDB.GetSymbolExplorer().GetSymbolObject(this._SimpleSymbol, this._LayerType).SymbolFigure;
        if (this.GetRenderType()==lkRenderType.enUniqueValue)
            return UniqueValueRender.CreateMSymbolObject(64, 30).SymbolFigure;
        return null;
    }

    //字段列表
    private List<LayerField> _FieldList = new ArrayList<LayerField>();
    public List<LayerField> GetFieldList(){return this._FieldList;}
    public String GetFieldListJsonStr()
    {
        try
        {
            JSONObject parentJSObject = new JSONObject();
            JSONArray jsArray = new JSONArray();
            for(LayerField lf:this._FieldList)
            {
                JSONObject childJSObject = new JSONObject();
                childJSObject.put("FieldName", lf.GetFieldName());
                childJSObject.put("DataFieldName", lf.GetDataFieldName());
                childJSObject.put("FieldTypeName", lf.GetFieldTypeName());
                childJSObject.put("FieldSize", lf.GetFieldSize());
                childJSObject.put("FieldDecimal", lf.GetFieldDecimal());
                childJSObject.put("FieldEnumCode", lf.GetFieldEnumCode());
                childJSObject.put("FieldEnumEdit", lf.GetFieldEnumEdit());
                childJSObject.put("IsSelect", lf.getIsSelect());
                childJSObject.put("FieldShortName", lf.GetFieldShortName());
                jsArray.put(childJSObject);
            }
            parentJSObject.put("Data", jsArray);
            return parentJSObject.toString();

        } catch (JSONException e) {
            return "";
        }
    }
    public String GetFieldNameByDataFieldName(String dataFieldName)
    {
        for(LayerField lf:this._FieldList)
        {
            if (lf.GetDataFieldName().equals(dataFieldName)) return lf.GetFieldName();
        }
        return "";
    }
    public String GetDataFieldNameByFieldName(String fieldName)
    {
        for(LayerField lf:this._FieldList)
        {
            if (lf.GetFieldName().equals(fieldName))
            {
                return lf.GetDataFieldName();
            }
        }
        return "";
    }

    public LayerField GetDataFieldByFieldName(String fieldName)
    {
        for(LayerField lf:this._FieldList)
        {
            if (lf.GetFieldName().equals(fieldName))
            {
                return lf;
            }
        }
        return null;
    }

    public void SetFieldList(List<LayerField> fieldList){this._FieldList = fieldList;}
    /**
     * 设置字段列表
     * @param fieldListJSONStr，格式：字段名称-数据字段名称-类型-大小-精度-数据字典-是否可手动输入字典@。。。。
     */
    public void SetFieldList(String fieldListJSONStr)
    {
        this._FieldList.clear();
        try
        {
            JSONTokener jsonParser = new JSONTokener(fieldListJSONStr);
            JSONObject jsObj = (JSONObject)jsonParser.nextValue();
            JSONArray jsArray = jsObj.getJSONArray("Data");
            for(int i=0;i<jsArray.length();i++)
            {
                JSONObject Obj = jsArray.getJSONObject(i);
                LayerField LF = new LayerField();
                LF.SetFieldName(Obj.getString("FieldName"));        		//字段汉字名称
                LF.SetDataFieldName(Obj.getString("DataFieldName"));		//英文数据字段名
                LF.SetFieldTypeName(Obj.getString("FieldTypeName"));		//字段类型
                LF.SetFieldSize(Obj.getInt("FieldSize"));					//字段大小
                LF.SetFieldDecimal(Obj.getInt("FieldDecimal"));				//字段小数位数
                LF.SetFieldEnumCode(Obj.getString("FieldEnumCode"));		//字段枚举值列表
                LF.SetFieldEnumEdit(Obj.getBoolean("FieldEnumEdit"));		//枚举是否可输入

                try
                {
                    LF.SetIsSelect(Obj.getBoolean("IsSelect"));
                }
                catch(Exception ex)
                {
                    LF.SetIsSelect(true);
                }
                try
                {
                    LF.SetFieldShortName(Obj.getString("FieldShortName"));
                }
                catch(Exception ex)
                {
                    LF.SetFieldShortName("");
                }
                this._FieldList.add(LF);
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 克隆图层类
     * @return
     */
    public Layer Clone()
    {
        Layer lyr = new Layer();
        this.CopyTo(lyr);
        return lyr;
    }

    /**
     * 复制属性
     * @param vLayer
     */
    public void CopyTo(Layer vLayer)
    {
        vLayer.SetLayerAliasName(this.GetLayerAliasName());
        vLayer.SetLayerID(this.GetLayerID());
        vLayer.SetLayerTypeName(this.GetLayerTypeName());
        vLayer.SetVisible(this.GetVisible());
        vLayer.SetTransparent(this.GetTransparet());
        vLayer.SetVisibleScaleMin(this.GetVisibleScaleMin());
        vLayer.SetVisibleScaleMax(this.GetVisibleScaleMax());
        vLayer.SetFieldList(this.GetFieldListJsonStr());
        vLayer.SetIfLabel(this.GetIfLabel());
        vLayer.SetLabelDataField(this.GetLabelDataFieldStr());
        vLayer.SetLabelFont(this.GetLabelFont());
        vLayer.SetMinX(this.GetMinX());
        vLayer.SetMinY(this.GetMinY());
        vLayer.SetMaxX(this.GetMaxX());
        vLayer.SetMaxY(this.GetMaxY());
        vLayer.SetSelectable(this.GetSelectable());
        vLayer.SetEditable(this.GetEditable());
        vLayer.SetSelectable(this.GetSelectable());
        vLayer.SetRenderType(this.GetRenderType());
        vLayer.SetSimpleSymbol(this.GetSimpleSymbol());

        vLayer.SetLayerProjectType(this.GetLayerProjecType());





        List<String> UVF = (List<String>)this.GetUniqueSymbolInfoList().get("UniqueValueField");
        List<String> UVL = (List<String>)this.GetUniqueSymbolInfoList().get("UniqueValueList");
        List<String> USL = (List<String>)this.GetUniqueSymbolInfoList().get("UniqueSymbolList");

        List<String> UVFTo = new ArrayList<String>();for(String v :UVF)UVFTo.add(v);
        List<String> UVLTo = new ArrayList<String>();for(String v :UVL)UVLTo.add(v);
        List<String> USLTo = new ArrayList<String>();for(String v :USL)USLTo.add(v);

        vLayer.GetUniqueSymbolInfoList().put("UniqueValueField", UVFTo);
        vLayer.GetUniqueSymbolInfoList().put("UniqueValueList", UVLTo);
        vLayer.GetUniqueSymbolInfoList().put("UniqueSymbolList", USLTo);
        vLayer.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", this.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol"));
    }
}
