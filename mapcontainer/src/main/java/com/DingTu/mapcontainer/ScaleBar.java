package com.DingTu.mapcontainer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.ImageView;
import com.DingTu.Base.Tools;
import com.DingTu.Map.Map;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class ScaleBar {

    private Canvas m_g = null;
    private Paint m_TextPen = null;
    private Paint m_LinePen = null;
    private Bitmap m_bp = null;
    public void SetImageView(ImageView iv)
    {
        this.m_bp = Bitmap.createBitmap(Tools.DPToPix(80), Tools.DPToPix(30), Bitmap.Config.ARGB_4444);
        this.m_g = new Canvas(this.m_bp);
        iv.setImageBitmap(this.m_bp);

        this.m_TextPen = new Paint();
        this.m_TextPen.setColor(Color.BLUE);
        this.m_TextPen.setAntiAlias(true);

        Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
        this.m_TextPen.setTypeface(TF);

        this.m_TextPen.setTextSize(Tools.SPToPix(13));

        this.m_LinePen = new Paint();
        this.m_LinePen.setColor(Color.RED);
        this.m_LinePen.setStyle(Paint.Style.STROKE);
        this.m_LinePen.setAntiAlias(true);
        this.m_LinePen.setStrokeWidth(Tools.DPToPix(3));


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
        _BKFont.setTextSize(this.m_TextPen.getTextSize());
        return _BKFont;
    }


    /**
     * 刷新比例条
     */
    public void RefreshScaleBar(Map map)
    {
        this.m_bp.eraseColor(Color.TRANSPARENT);
        //this.m_g.drawColor(Color.GRAY);
        String Uint = Tools.ToLocale("米");
        double BLC = 1;
        if (map!=null)BLC = map.getViewConvert().getZoomScale();

        //根据BLC值计算比例条应有宽度，也就是取整后的BLC值对应的canvas宽度
        int IBLC = (int)(BLC*this.m_g.getWidth());
        if (IBLC==0)
        {
            float N = (float)Tools.Save3Point(BLC*this.m_g.getWidth());
            this.DrawBar(N,N, Uint);return;
        }
        if (IBLC>=1000){IBLC = IBLC / 1000;Uint=Tools.ToLocale("公里");}

        //将IBLC趋近化
        String StrBLC = IBLC+"";
        if (StrBLC.length()==1) {this.DrawBar(IBLC, IBLC, Uint);return;}
        if (StrBLC.length()==2)
        {
            int EndNumber = Integer.parseInt(StrBLC.substring(1,2));
            String Str2 = StrBLC.substring(0,1);
            if (EndNumber>=5)Str2+="5";else Str2+="0";
            this.DrawBar(IBLC, Integer.parseInt(Str2), Uint);return;
        }
        if (StrBLC.length()==3)
        {
            int EndNumber = Integer.parseInt(StrBLC.substring(StrBLC.length()-2,StrBLC.length()));
            String Str2 = StrBLC.substring(0,StrBLC.length()-2);
            if (EndNumber>=50)Str2+="50";else Str2+="00";
            this.DrawBar(IBLC, Integer.parseInt(Str2), Uint);return;
        }
        if (StrBLC.length()>=4)
        {
            this.DrawBar(IBLC, Integer.parseInt(StrBLC), Uint);return;
        }

//		if (StrBLC.length()==4)
//		{
//			int EndNumber = Integer.parseInt(StrBLC.substring(StrBLC.length()-2,2));
//			String Str2 = StrBLC.substring(0,StrBLC.length()-2);
//			if (EndNumber>=50)Str2+="50";else Str2+="00";
//			this.DrawBar(IBLC, Integer.parseInt(Str2), Uint);return;
//		}

    }

    private void DrawBar(float OldValue,float NewValue,String Unit)
    {
        String T = NewValue+"";
        if (T.substring(T.length()-2,T.length()).equals(".0"))T=T.substring(0,T.length()-2);

        //绘制比例尺条样式
        float newW = (float)NewValue*this.m_g.getWidth()/(float)OldValue;
        float newH = this.m_g.getHeight();

        float OffsetX=0;
        for(int i=1;i<=2;i++)
        {
            if (i==1)
            {
                this.m_LinePen.setColor(Color.WHITE);
                this.m_LinePen.setStrokeWidth(6);
                OffsetX = 2;
            }
            if (i==2)
            {
                this.m_LinePen.setColor(Color.BLUE);
                this.m_LinePen.setStrokeWidth(4);
            }
            this.m_g.drawLine(0,newH-5,newW ,newH-5, this.m_LinePen);
            this.m_g.drawLine(0+2,newH/2,0+2 ,newH, this.m_LinePen);
            this.m_g.drawLine(newW-OffsetX,newH/2,newW-OffsetX,newH, this.m_LinePen);
        }

        //文字位置
        String Text = T+Unit;
        float TextX = (newW - this.m_TextPen.measureText(T+Unit))/2;
        float TextY = this.m_g.getHeight()-12;

        this.m_g.drawText(Text, TextX-2, TextY, this.getBKFont());
        this.m_g.drawText(Text, TextX+2, TextY, this.getBKFont());
        this.m_g.drawText(Text, TextX, TextY-2, this.getBKFont());
        this.m_g.drawText(Text, TextX, TextY+2, this.getBKFont());
        this.m_g.drawText(Text, TextX+2, TextY+2, this.getBKFont());
        this.m_g.drawText(Text, TextX-2, TextY-2, this.getBKFont());
        this.m_g.drawText(Text, TextX+2, TextY-2, this.getBKFont());
        this.m_g.drawText(Text, TextX-2, TextY+2, this.getBKFont());

        this.m_g.drawText(Text,TextX,TextY , this.m_TextPen);

    }
}
