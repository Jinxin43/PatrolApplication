package com.DingTu.Data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.Tools;
import com.DingTu.GPS.LocationEx;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by Dingtu2 on 2017/6/29.
 */

public class GPSDataStatus {

    private ImageView m_StatusView = null;
    private View mView = null;
    private TextView tvStatus;
    private TextView tvLocation;
    private TextView tvArea;

    public GPSDataStatus(){}

    /**
     * 设置采集状态显示控件
     * @param tv
     */
    public void SetStatusView(ImageView tv)
    {
        this.m_StatusView = tv;
    }

    public void setStatusView(TextView tvView)
    {
        this.tvStatus = tvView;
    }
    public void setLocationView(TextView tvView)
    {
        this.tvLocation = tvView;
    }
    public void setAreaView(TextView tvView)
    {
        this.tvArea = tvView;
    }

    private String m_LineStatus = "";
    private String m_PolyStatus = "";

    private String mStatusString="";
    private String mLocationString="";
    private String mAreaString="";

    private ICallback m_GPSPositionCallback = null;
    public void SetGpsSetCallback(ICallback callback)
    {
        this.m_GPSPositionCallback = callback;
    }

    /**
     * 更新采集状态
     * @param Type
     * @param len
     */
    public void UpdateLineStatus(String Type,double len)
    {
        if (Type.equals("停止") || Type.equals(""))
        { this.m_LineStatus="";}
        else if (Type.equals("巡护"))
        {
            Log.d("UpdateLineStatus",len+"");
            m_LineStatus =  "【巡护距离】"+Tools.ReSetDistance(len, true);
            m_PolyStatus = "【巡护时间】1小时22分钟";
        }
        else {
            this.m_LineStatus = "【"+Tools.ToLocale(Type)+"】"+Tools.ToLocale("线")+"="+Tools.ReSetDistance(len, true);
        }

        this.UpdateShow();
    }

    private void UpdateInfo()
    {
        if(tvArea != null)
        {
//            tvArea.setText(mAreaString);
        }
        if(tvLocation != null)
        {
//            tvLocation.setText(mLocationString);
        }
        if(tvStatus != null)
        {
            tvStatus.setText(mStatusString);
        }
    }


    public void  UpdateRoundStatus(double len,Date startTime)
    {
        if(startTime == null)
        {
            return;
        }
        m_LineStatus =  "巡护距离:"+Tools.ReSetDistance(len, true);
        m_PolyStatus = "巡护时间:"+Tools.CalcDuration(startTime);
        this.UpdateShow();
    }

    public void  UpdateRoundStatus(double len, Date startTime, LocationEx location)
    {
        if(startTime == null)
        {
            mStatusString="未开始巡护";
        }
        else
        {
            mStatusString =  "巡护中,"+Tools.ReSetDistance(len, true);

        }

       if(m_GPSPositionCallback != null)
       {
           m_GPSPositionCallback.OnClick("",mStatusString);
       }

        if(location != null)
        {
            mLocationString=Tools.ConvertToDigi(location.GetGpsLatitude(),7)+","+Tools.ConvertToDigi(location.GetGpsLongitude(),7)+","+Tools.ConvertToDigi(location.GetGpsAltitude(),1);
            mAreaString="安家岐保护站(实验区)";
        }
        else
        {
            mLocationString="未定位";
            mAreaString="未定位";
        }


        this.UpdateInfo();
    }

    /**
     * 更新采集状态
     * @param Type
     * @param area
     */
    public void UpdatePolyStatus(String Type,double area)
    {
        if (Type.equals("停止") || Type.equals("")){ this.m_PolyStatus="";}
        else if(Type.equals("巡护"))
        {
            this.m_PolyStatus = "【"+Tools.ToLocale(Type)+"】"+Tools.ToLocale("面")+"="+Tools.ReSetArea(area, true);
        }
        else{
            this.m_PolyStatus = "【"+Tools.ToLocale(Type)+"】"+Tools.ToLocale("面")+"="+Tools.ReSetArea(area, true);
        }
        this.UpdateShow();
    }

    private Canvas m_g = null;
    private Bitmap m_bp = null;

    /**
     * 更新状态显示
     */
    private void UpdateShow()
    {
        if (this.m_g==null)
        {
            this.m_bp = Bitmap.createBitmap(this.m_StatusView.getMeasuredWidth(), this.m_StatusView.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
            this.m_g = new Canvas(this.m_bp);
            this.m_StatusView.setImageBitmap(this.m_bp);
        }

        //计算文字位置以及文字换行的控制

        this.m_bp.eraseColor(Color.TRANSPARENT);
        float TextX = 0,TextY = this.m_g.getHeight()-4,OffsetY = 0;
        if (!this.m_PolyStatus.equals(""))
        {
            OffsetY = this.getTextPen().getTextSize();
            TextX = (float) (this.m_g.getWidth() * 0.5 - this.getTextPen().measureText(this.m_PolyStatus)* 0.5);
            this.DrawTextStroke(this.m_PolyStatus, TextX,TextY);
            TextY -= (OffsetY);
        }

        if (!this.m_LineStatus.equals(""))
        {
            TextX = (float) (this.m_g.getWidth() * 0.5 - this.getTextPen().measureText(this.m_LineStatus)* 0.5);
            this.DrawTextStroke(this.m_LineStatus, TextX,TextY);
        }
    }

    /**
     * 画描边文字
     * @param Text
     * @param TextX
     * @param TextY
     */
    private void DrawTextStroke(String Text,float TextX,float TextY)
    {
        //画描边文字
        this.m_g.drawText(Text, TextX-2, TextY, this.getBKFont());
        this.m_g.drawText(Text, TextX+2, TextY, this.getBKFont());
        this.m_g.drawText(Text, TextX, TextY-2, this.getBKFont());
        this.m_g.drawText(Text, TextX, TextY+2, this.getBKFont());
        this.m_g.drawText(Text, TextX+2, TextY+2, this.getBKFont());
        this.m_g.drawText(Text, TextX-2, TextY-2, this.getBKFont());
        this.m_g.drawText(Text, TextX+2, TextY-2, this.getBKFont());
        this.m_g.drawText(Text, TextX-2, TextY+2, this.getBKFont());

        this.m_g.drawText(Text, TextX,TextY , this.getTextPen());
    }

    //标注字体
    private TextPaint m_TextPen = null;
    private TextPaint getTextPen()
    {
        if (this.m_TextPen==null)
        {
            this.m_TextPen = new TextPaint();
            this.m_TextPen.setColor(Color.BLUE);
            this.m_TextPen.setAntiAlias(true);
            Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
            this.m_TextPen.setTypeface(TF);
            this.m_TextPen.setTextSize(Tools.SPToPix(18));
        }
        return this.m_TextPen;
    }

    //标注的描边
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
}
