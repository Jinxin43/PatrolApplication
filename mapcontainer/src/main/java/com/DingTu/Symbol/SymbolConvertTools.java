package com.DingTu.Symbol;

import com.DingTu.Enum.lkGeoLayerType;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class SymbolConvertTools {

    /**
     * 设置图层符号，点符号格式：则Value为Base64格式，
     * 				线符号格式：颜色1,宽度1,线型定义1@颜色2,宽度2,线型定义2.....
     * 				面符号格式：面色,边线色,边线宽
     * @param value
     */
    public static ISymbol StrToSymbol(String value,lkGeoLayerType geoLayerType)
    {
        if (geoLayerType==lkGeoLayerType.enPoint)
        {
            PointSymbol PS = new PointSymbol();
            PS.CreateByBase64(value);
            return PS;
        }
        if (geoLayerType==lkGeoLayerType.enPolyline)
        {
            LineSymbol LS = new LineSymbol();
            LS.CreateByBase64(value);
            return LS;
        }
        if (geoLayerType== lkGeoLayerType.enPolygon)
        {
            //面色,边线色,边线宽
            PolySymbol PS = new PolySymbol();
            PS.CreateByBase64(value);
            return PS;
        }

        return null;
    }

//    //字符串转换成颜色
//    private static int StrToColor(String StrColor)
//    {
//        return StrToColor(StrColor,255);
//
//    }
//    private static int StrToColor(String StrColor,int TranValue)
//    {
//        String[] ColorInfo = StrColor.split("-");
//        return Color.argb(TranValue, Integer.valueOf(ColorInfo[0]),
//        					 		 Integer.valueOf(ColorInfo[1]),
//        					 		 Integer.valueOf(ColorInfo[2]));
//    }
}
