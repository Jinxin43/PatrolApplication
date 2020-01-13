package com.DingTu.Symbol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;

import com.DingTu.Enum.lkDrawType;
import com.DingTu.Enum.lkSelectionType;
import com.DingTu.Enum.lkTextPosition;
import com.DingTu.Map.Map;

import java.io.ByteArrayOutputStream;

import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Cargeometry.Point;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class PointSymbol extends ISymbol {

    public PointSymbol()
    {
        //默认符号样式
        String DefaultSymbol = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAAAZiS0dEAAAAAAAA+UO7fwAAAAlwSFlzAAAASAAAAEgARslrPgAAAAl"+
                "2cEFnAAAAEAAAABAAXMatwwAAAk5JREFUOMulkj1PVEEUhp/du4CgGAnyuXpBUElU0MJKEws6EGPpP7A1RoONjRaakEiBikCMv8BKEn6BVhhhE8iifLkgRORDFn"+
                "bvvTNzZ8Zil+VDLYwnOTknkznP+87kRKy1/E/EDh6svhm6HikuehqJRl2rtcAYrNElVsgFnck8sloP1z94KP8IWBl82XmormrwSFNd3HEsqBCMAWvQ0rSmk6m32"+
                "fnUPaB3Zyay84SVVy86imsq+4+2uI3Oxjysf4fQQKhASDhaSVjVzM/xqRlvLnWnobdvpABYGXjeWVJVOVR+5kTc2ZiH1WXQeQdK5WrgQ0U1YV0L64nkkj/39XZj/+u"+
                "RKEDUcXrKT9XEnR/TsJyCQIHv59ILwPNzLuaniX3+SGVTPB5xoj2FP7BGuw4hLKVyymEIUuXVFci8EylgNkmsphGMcQsAI0SAL8oJBCh9YFDme5XrhQA/wAR+sO"+
                "tASIuUkPXz6nsGC7B8CgFSYoSwuwAZlKIVbG2BZY/iAYCS4HlgNFbKUoAogMlkFrUfwrHjsLUJ2WzuoudBNl99D7bSUHsSJTV6e3uxAFDr692rY8nl0L0AFbWwm"+
                "c5BsvlBz8ek05h6F//iVb6Nji+Ha2vd+xZp+tbNG2UNbl/1pXONRWPvYWYKrMHKkFAIrNuMunyNlQ+js5nJibttiS/D+wAAUx3tXeWnmwaqz5+NFxmFVgpjTG6n"+
                "DKx+miCTSNxvm5x99tsq70Sy/UqXU3b4SSTmuDYMhdGaiDUl2vcX9ObmY7R51zoxI/8K+Nf4BXQOgfVn4PniAAAAJXRFWHRjcmVhdGUtZGF0ZQAyMDA5LTExLTE"+
                "2VDIyOjE4OjE3LTA3OjAwWwD+wQAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAxMC0wMS0xMVQwNjo1MzoxMC0wNzowMDn0luMAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMT"+
                "AtMDEtMTFUMDY6NTM6MTAtMDc6MDBIqS5fAAAAYnRFWHRMaWNlbnNlAGh0dHA6Ly9jcmVhdGl2ZWNvbW1vbnMub3JnL2xpY2Vuc2VzL2J5LzMuMC8gb3IgaHR0c"+
                "DovL2NyZWF0aXZlY29tbW9ucy5vcmcvbGljZW5zZXMvYnkvMi41L4uGPGUAAAAldEVYdG1vZGlmeS1kYXRlADIwMDYtMDMtMTJUMjE6NTc6NDYtMDc6MDAPRqCU"+
                "AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAABt0RVh0U291cmNlAEZBTUZBTUZBTSBTaWxrIEljb25zgnpk+wAAADN0RVh0U291cmNlX1V"+
                "STABodHRwOi8vd3d3LmZhbWZhbWZhbS5jb20vbGFiL2ljb25zL3NpbGsvwsQNDQAAAABJRU5ErkJggg==";
        this.CreateByBase64(DefaultSymbol);
    }


    //符号样式
    private Bitmap _Icon = null;
    public Bitmap getIcon()
    {
        return _Icon;
    }
    public void setIcon(Bitmap value)
    {
        _Icon = value;
    }

    /**
     * Base64字符串转换点符号
     * @param base64
     */
    public void CreateByBase64(String base64)
    {
        //将字符串转换成Bitmap类型
        byte[] bitmapArray = Base64.decode(base64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,bitmapArray.length);
        this._Icon = bitmap;
    }

    /**
     * 将点符号转换成Base64字符串
     * @return
     */
    public String ToBase64()
    {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        this._Icon.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * 生成指定大小的符号指示图
     * @param Width
     * @param Height
     * @return
     */
    public Bitmap ToFigureBitmap(int Width,int Height)
    {
        Bitmap bp = Bitmap.createBitmap(Width,Height, Bitmap.Config.ARGB_8888);
        Canvas g = new Canvas(bp);
        //if (this.getIcon().getWidth()<=bp.getWidth() && this.getIcon().getHeight()<=bp.getHeight())
        {
            g.drawBitmap(this.getIcon(), (bp.getWidth()-this.getIcon().getWidth())/2, (bp.getHeight()-this.getIcon().getHeight())/2, null);
        }
        return bp;
    }


    //绘制符号
    @Override
    public void Draw(Map map, Geometry pGeometry)
    {
        this.Draw(map, map.getDisplayGraphic(), pGeometry, 0, 0, lkDrawType.enNormal);
    }

    @Override
    public void Draw(Map map, Canvas g, Geometry pGeometry, int OffsetX,int OffsetY, lkDrawType DrawType)
    {
        //图标的中心点偏移量
        int IconOffSet = this._Icon.getWidth() / 2;

        //图形中心点屏幕坐标
        int PointX = 0, PointY = 0;
        Point Pt = (Point)pGeometry;
        android.graphics.Point agp = map.getViewConvert().MapToScreen(Pt.getCoordinate().getX(),Pt.getCoordinate().getY());
        PointX = agp.x;PointY = agp.y;

        //图形屏幕坐标
        int PosX = PointX - IconOffSet + OffsetX;
        int PosY = PointY - IconOffSet + OffsetY;
        g.drawBitmap(this._Icon, PosX, PosY, null);
//        int RW = this._Icon.getWidth();
//        int RH = this._Icon.getHeight();
//        g.drawBitmap(this._Icon, new Rect(0,0,RW,RH),
//        					 	 new Rect(PointX, PointY,PointX+RW,PointY+RH),null);
        //Rectangle RT = new Rectangle(PosX, PosY, map.SetDPI(this._Icon.getWidth()), map.SetDPI(this._Icon.getHeight()));
        //g.DrawImage(this.Icon, RT, 0, 0, this.Icon.Width, this.Icon.Height, GraphicsUnit.Pixel, TransAttr);
        switch (DrawType)
        {
            case enSelected_Editing:
                //计算四周的小黑点坐标
                Paint pBrush = new Paint();
                pBrush.setColor(Color.BLACK);
                int W = 10, H = 10;
                int RX1 = PointX+OffsetX - 2*W, RY1 = PointY+OffsetY - 2*H;
                int RX2 = PointX+OffsetX + 2*W, RY2 = PointY+OffsetY + 2*H;
                g.drawRect(RX1-W/2, RY1-H/2, RX1+W/2, RY1+H/2, pBrush);
                g.drawRect(RX1-W/2, RY2-W/2, RX1+W/2, RY2+W/2, pBrush);
                g.drawRect(RX2-W/2, RY2-H/2, RX2+W/2, RY2+H/2, pBrush);
                g.drawRect(RX2-W/2, RY1-W/2, RX2+W/2, RY1+W/2, pBrush);

                pBrush.setColor(Color.BLUE);
                g.drawCircle(PointX, PointY, 10, pBrush);
                //g.drawRect(RX1 + IconOffSet * 2, RY1 + IconOffSet * 2, RX2 + IconOffSet * 2, RY2 + IconOffSet * 2, pBrush);
                //g.drawRect(RX1, RY1 + IconOffSet * 2, RX2, RY2 + IconOffSet * 2, pBrush);
                break;

            case enSelected_NoEditing:
                Paint pBrushUn = new Paint();
                pBrushUn.setColor(Color.rgb(0, 255, 255));
                g.drawCircle(PointX, PointY, 12, pBrushUn);
                break;

        }
    }
    @Override
    public void DrawLabel(Map map, Canvas g, Geometry pGeometry, int OffsetX,
                          int OffsetY, lkSelectionType pSelectionType)
    {

        //FontMetrics可用此计算文本高度
        //图标的中心点偏移量
        int IconOffSet = this._Icon.getWidth() / 2;

        //图形中心点屏幕坐标
        int PointX = 0, PointY = 0;
        Point Pt = (Point)pGeometry;
        android.graphics.Point agp = map.getViewConvert().MapToScreen(Pt.getCoordinate().getX(),Pt.getCoordinate().getY());
        PointX = agp.x;PointY = agp.y;
        String Text = pGeometry.getTag();
        // Text = Text.split(",")[Text.split(",").length-1];
        float TextX = PointX + IconOffSet - 2;
        float TextY = PointY - IconOffSet + 2;

        float OffsetXY = this.getTextSymbol().getTextFont().getTextSize()/2;
        TextX+=OffsetXY;TextY+=OffsetXY;

        this.getTextSymbol().Draw(g, TextX, TextY, Text, lkTextPosition.enLeft, pSelectionType);
    }
}
