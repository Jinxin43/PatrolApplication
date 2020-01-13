package com.DingTu.Symbol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.DingTu.Enum.lkSelectionType;
import com.DingTu.Enum.lkTextPosition;
import com.DingTu.Base.PubVar;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class TextSymbol
{
    public TextSymbol() {}

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

    //标注字体
    private Paint _BKFont = null;
    private Paint getBKFont()
    {
        if (_BKFont == null)
        {
            _BKFont = new Paint();
            _BKFont.setColor(Color.WHITE);
            _BKFont.setAntiAlias(true);

            Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
            _BKFont.setTypeface(TF);
            _BKFont.setAntiAlias(true);
        }

        _BKFont.setTextSize(this.getTextFont().getTextSize());
        return _BKFont;
    }


    private Paint _Font = null;
    public Paint getTextFont()
    {
        if (_Font == null)
        {
            _Font = new Paint();
            _Font.setAntiAlias(true);
            _Font.setTextSize(20);
            Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
            _Font.setTypeface(TF);
        }
        _Font.setAntiAlias(true);
        return _Font;
    }

    public void setTextFont(Paint value)
    {
        float s = (float) PubVar.m_DisplayMetrics.densityDpi / 96f;
        _Font = value;
        _Font.setTextSize(_Font.getTextSize()*s);
    }

    //标注颜色
    public int getColor()
    {
        return this.getTextFont().getColor();
    }
    public void setColor(int value)
    {
        this.getTextFont().setColor(value);
    }

    //绘制图形
    public void Draw(Canvas g, float TextX, float TextY, String Text, lkTextPosition lkTP, lkSelectionType pSelectionType)
    {
        if (Text==null) return;
        if (lkTP == lkTextPosition.enCenter)
        {
            float LableW = this.getTextFont().measureText(Text);
            TextX -= LableW / 2;
        }

        //if (pSelectionType == lkSelectionType.enSelect) g.DrawString(Text, this.TextFont,System.Drawing.Brushes.Blue, TextX, TextY);
        //g.DrawString(Text, this.TextFont, this._Brush, TextX, TextY);

        //叠加Google地图后文字加边框
        //if (PubVar.m_Map.getGMap().getIfLoadGoogleMap())
        {
            g.drawText(Text, TextX-1, TextY, this.getBKFont());
            g.drawText(Text, TextX+1, TextY, this.getBKFont());
            g.drawText(Text, TextX, TextY-1, this.getBKFont());
            g.drawText(Text, TextX, TextY+1, this.getBKFont());
            g.drawText(Text, TextX+1, TextY+1, this.getBKFont());
            g.drawText(Text, TextX-1, TextY-1, this.getBKFont());
            g.drawText(Text, TextX+1, TextY-1, this.getBKFont());
            g.drawText(Text, TextX-1, TextY+1, this.getBKFont());
        }
        g.drawText(Text,TextX, TextY, this.getTextFont());

        //  g.drawTextOnPath(text, path, hOffset, vOffset, paint)

    }
}