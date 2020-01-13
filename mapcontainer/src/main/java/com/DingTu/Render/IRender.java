package com.DingTu.Render;

import com.DingTu.Enum.lkRenderType;

import com.DingTu.Cargeometry.Geometry;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public abstract class IRender {

    //渲染类型
    private lkRenderType _Type = lkRenderType.enSimple;
    public lkRenderType getType()
    {
        return _Type;
    }
    public void setType(lkRenderType value)
    {
        _Type = value;
    }

    //图层是否标注
    private boolean _IfLabel = false;
    public boolean getIfLabel()
    {
        return _IfLabel;
    }
    public void setIfLabel(boolean value)
    {
        _IfLabel = value;
    }

    //图层标注字段
    private String _LabelField = "";
    public String getLabelField()
    {
        return _LabelField;
    }
    public void setLabelField(String value)
    {
        _LabelField = value;
    }

    //图层标注符号样式
    private String _LabelStyleName = "NULL";  //颜色，大小
    public String getLabelFont()
    {
        return _LabelStyleName;
    }
    public void setLabelFont(String value)
    {
        _LabelStyleName = value;
    }

    //图层标注最小显示比例
    private double _LabelScaleMin = 0;
    public double getLabelScaleMin()
    {
        return _LabelScaleMin;
    }
    public void setLabelScaleMin(double value)
    {
        _LabelScaleMin = value;
    }

    //图层标注最大显示比例
    private double _LabelScaleMax = Double.MAX_VALUE;
    public double getLabelScaleMax()
    {
        return _LabelScaleMax;
    }
    public void setLabelScaleMax(double value)
    {
        _LabelScaleMax = value;
    }

//    public void RenderLabel(LKMap.Layers.GeoLayer _GeoLayer, List<int> ObjectIDList)
//    {
    ////更新符号的标注信息
    //string[] FieldList = this.LabelField.Split(',');
    //if (this.IfLabel)
    //{
    //    string SQL = "select [SYS_ID]," + this.LabelField + " from " + _GeoLayer.Dataset.TableName;
    //    if (ObjectIDList != null)
    //    {
    //        string IDList = "";
    //        foreach (int oid in ObjectIDList)
    //        {
    //            IDList += oid.ToString() + "\r\n";
    //        }
    //        //System.IO.File.(_GeoLayer.Map.TempDataFileFullName, IDList);
    //        string where = "SYS_ID in (SELECT F1 FROM [Text;FMT=Delimited;HDR=No;DATABASE=" + _GeoLayer.Map.TempDataFilePath + "]." + _GeoLayer.Map.TempDataFileName + ")";
    //        SQL += " where " + where;
    //    }

    //    SQL+=" order by SYS_ID";
    //    using (System.Data.DataSet pDataSet = _GeoLayer.Dataset.DataSource.Query(SQL))
    //    {
    //        LKMap.CartoGeometry.IGeometry pGeometry = null;
    //        foreach (System.Data.DataRow pDataRow in pDataSet.Tables[0].Rows)
    //        {
    //            pGeometry = _GeoLayer.Dataset.GetGeometry(((int)pDataRow["SYS_ID"]-1));
    //            string LabelText = "";
    //            foreach (string FL in FieldList)
    //            {
    //                LabelText += pDataRow[FL].ToString() + ",";
    //            }
    //            LabelText = LabelText.Substring(0, LabelText.Length - 1);
    //            pGeometry.Tag = LabelText;
    //        }
    //    }
    //}
//    }
//    protected void RenderLabel(LKMap.Layers.GeoLayer _GeoLayer)
//    {
    ////更新符号的标注信息
    //string[] FieldList = this.LabelField.Split(',');
    //if (this.IfLabel)
    //{
    //    string SQL = "select [SYS_ID]," + this.LabelField + " from " + _GeoLayer.Dataset.TableName + " order by SYS_ID";
    //    using (System.Data.DataSet pDataSet = _GeoLayer.Dataset.DataSource.Query(SQL))
    //    {
    //        int idx = 0; LKMap.CartoGeometry.IGeometry pGeometry = null;
    //        foreach (System.Data.DataRow pDataRow in pDataSet.Tables[0].Rows)
    //        {
    //            pGeometry = _GeoLayer.Dataset.GetGeometry(idx);
    //            string LabelText = "";
    //            foreach (string FL in FieldList)
    //            {
    //                LabelText += pDataRow[FL].ToString() + ",";
    //            }
    //            LabelText = LabelText.Substring(0, LabelText.Length - 1);
    //            pGeometry.Tag = LabelText;
    //            idx++;
    //        }
    //    }
    //}
//    }

    public abstract void UpdateSymbol(Geometry pGeometry);
    public abstract void UpdateAllLabel();
    public abstract void UpdateSymbolSet();
}
