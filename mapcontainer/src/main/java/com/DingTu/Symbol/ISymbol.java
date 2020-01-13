package com.DingTu.Symbol;

import android.graphics.Canvas;

import com.DingTu.Enum.lkDrawType;
import com.DingTu.Enum.lkSelectionType;
import com.DingTu.Map.Map;

import com.DingTu.Cargeometry.Geometry;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public abstract class ISymbol {

    //符号名称
    private String _Name = "";
    public String getName()
    {
        return _Name;
    }
    public void setName(String value)
    {
        _Name = value;
    }

    //文本符号
    private TextSymbol _TextSymbol = null;
    public TextSymbol getTextSymbol()
    {
        if (_TextSymbol==null) _TextSymbol = new TextSymbol();
        return _TextSymbol;
    }
    public void setTextSymbol(TextSymbol value)
    {
        _TextSymbol = value;
    }


    //绘制图形
    abstract public void Draw(Map map,Geometry pGeometry);
    abstract public void Draw(Map map, Canvas g,Geometry pGeometry, int OffsetX, int OffsetY,lkDrawType DrawType);
    abstract public void DrawLabel(Map map, Canvas g, Geometry pGeometry, int OffsetX, int OffsetY, lkSelectionType pSelectionType);
}
