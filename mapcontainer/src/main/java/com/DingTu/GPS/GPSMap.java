package com.DingTu.GPS;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Data.GpsLine;
import com.DingTu.Data.GpsPoly;
import com.DingTu.Data.RoundGPSLine;
import com.DingTu.Map.StaticObject;
import com.DingTu.mapcontainer.IOnPaint;
import com.DingTu.mapcontainer.MapControl;
import com.DingTu.mapcontainer.R;

import com.DingTu.Cargeometry.Coordinate;


/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class GPSMap implements IOnPaint {

    private MapControl m_MapControl = null;
    public GPSMap(MapControl mapControl)
    {
        this.m_MapControl = mapControl;
        this.m_MapControl._GPSMapPaint = this;
    }

    //采集GPS线的实例
    private RoundGPSLine m_CRoundGpsLine = null;
    public void SetRoundGpsLine(RoundGPSLine gpsLine)
    {
        this.m_CRoundGpsLine = gpsLine;
    }

    private GpsLine m_CGpsLine = null;
    public void SetGpsLine(GpsLine gpsLine)
    {
        this.m_CGpsLine = gpsLine;
    }


    //采集GPS面的实例
    private GpsPoly m_CGpsPoly = null;
    public void SetGpsPoly(GpsPoly gpsPoly)
    {
        this.m_CGpsPoly = gpsPoly;
    }

//    //GPS计算面积工具
//    private WeiPianZhiFa_GPSMeasure  m_GpsMeasure = null;
//    public void SetGpsMeasure(WeiPianZhiFa_GPSMeasure gpsMeasure)
//    {
//        this.m_GpsMeasure = gpsMeasure;
//    }

    //GPS信息管理器
    private GpsInfoManage m_CGpsInfoManage = null;
    public void SetGpsInfoManage(GpsInfoManage gpsinfo)
    {
        this.m_CGpsInfoManage = gpsinfo;
    }
    public GpsInfoManage GetGpsInfoManage(){return this.m_CGpsInfoManage;}


    /**
     * GPS位置定时更新
     */
    private LocationEx m_LocationEx=null;
    public void UpdateGPSStatus(LocationEx locationEx)
    {
        if (locationEx!=null)
        {
            this.m_LocationEx = locationEx;
            //分采集类更新GPS位置信息

            try
            {
                String Date = locationEx.GetGpsDate();

                Coordinate newCoor = StaticObject.soProjectSystem.WGS84ToXY(locationEx.GetGpsLongitude(), locationEx.GetGpsLatitude(),locationEx.GetGpsAltitude());


                if (newCoor != null && PubVar.m_GPSLocate.AlwaysFix())
                {
                    if (this.m_CRoundGpsLine !=null)
                        this.m_CRoundGpsLine.UpdateGpsPosition(locationEx,true);
                    if(this.m_CGpsLine != null)
                    {
                        this.m_CGpsLine.UpdateGpsPosition(newCoor);
                    }
                    if (this.m_CGpsPoly!=null)this.m_CGpsPoly.getGPSLine().UpdateGpsPosition(newCoor);
//                if (this.m_CGpsInfoManage!=null)this.m_CGpsInfoManage.UpdateGpsPosition(new Coordinate(locationEx.GetGpsLongitude(),locationEx.GetGpsLatitude(),locationEx.GetGpsAltitude()));


                }
                else
                {
                    Log.d("m_LocationEx","GPS not fix");
                }

                //试用过期时间
//                SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
//                java.util.Date StopDate = SDF.parse("2018-06-15");
//                Date GPSDate = SDF.parse(Date);
//                if(GPSDate.getTime()>StopDate.getTime())
//                {
//                    Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "您的软件已过期，请联系软件提供商更新软件!", new ICallback() {
//                        @Override
//                        public void OnClick(String Str, Object ExtraStr) {
//                            ((Activity)PubVar.m_DoEvent.m_Context).finish();
//                        }
//                    });
//                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }


        }

        if (this.m_CGpsInfoManage!=null)
        {
            if (!PubVar.m_GPSLocate.GPS_OpenClose) this.m_CGpsInfoManage.UpdateGPSStatus("关闭");
            else
            {
                if (PubVar.m_GPSLocate.AlwaysFix())this.m_CGpsInfoManage.UpdateGPSStatus("已定位");
                else this.m_CGpsInfoManage.UpdateGPSStatus("定位中");
            }
        }

        this.m_MapControl.invalidate();
    }




    //主要刷新主界面上的GPS状态信息
    private Paint _TextFont = null;      //文字的字体
    private Paint GetTextFont()
    {
        if (this._TextFont==null)
        {
            this._TextFont = new Paint();
            this._TextFont.setAntiAlias(true);
            this._TextFont.setTextSize(Tools.SPToPix(20));
            this._TextFont.setColor(Color.BLUE);
            Typeface TF = Typeface.create("宋体", Typeface.BOLD);
            _TextFont.setTypeface(TF);
            _TextFont.setShadowLayer(20, 0, 0, Color.WHITE);
        }
        return this._TextFont;
    }
    private Bitmap _GpsPointICON = null;  //GPS当前位置的显示图标

    @Override
    public void OnPaint(Canvas canvas)
    {
        //1-此处为移屏时不显示更新状态
        if (PubVar.m_Map==null)return;
        if (PubVar.m_Map.getInvalidMap()) return;

        //为采集类刷新显示
        if (this.m_CRoundGpsLine !=null) this.m_CRoundGpsLine.OnPaint(canvas);
        if (this.m_CGpsPoly!=null) this.m_CGpsPoly.OnPaint(canvas);

        //如果GPS已经关闭退出
        if (!PubVar.m_GPSLocate.GPS_OpenClose) return;

        boolean GPSAlwaysFix =  PubVar.m_GPSLocate.AlwaysFix();   //GPS是否已经定位
        if (GPSAlwaysFix)
        {
//    		HashValueObject hvObject = PubVar.m_HashMap.GetValueObject("GPS_速度",true);
//    		float _SValue=m_Location.getSpeed()*3.6f; //meters/s->km/h
//    		DecimalFormat df = new DecimalFormat("#.0");
//    		String _SpeedValue = df.format(_SValue);
//    		hvObject.ShowOnMap=true;
//    		hvObject.LabelText = "GPS时间："+PubVar.m_GPSLocate.getGPSDate();
            PubVar.SaveDataDate = PubVar.m_GPSLocate.getGPSDate();

            //在此判断试用用户的时间是否超出，如果超出则退出
//            if (!PubVar.m_DoEvent.m_AuthorizeTools.isExpired(PubVar.SaveDataDate, true))
//            {
////    			//在屏幕中间给出过期文字提示
////    			String[] infoTextList = new String[]{"【系统提示】","软件试用期已过，无法继续使用！","请联系软件开发者获取正式授权码","详见【关于系统】"};
////    			for(int i=0;i<infoTextList.length;i++)
////    			{
////    				String infoText = infoTextList[i];
////    				float tw = this.GetTextFont().measureText(infoText);
////    				canvas.drawText(infoText,canvas.getWidth()/2-tw/2,canvas.getHeight()/2+i*this.GetTextFont().getTextSize(), this.GetTextFont());
////    			}
//                return;
//            }
        }


        //4-画当前的定位状态，也就是当前定位点
        Coordinate CurrentGPSCoor = null;
        if (GPSAlwaysFix )
        {
            CurrentGPSCoor = StaticObject.soProjectSystem.WGS84ToXY(this.m_LocationEx.GetGpsLongitude(), this.m_LocationEx.GetGpsLatitude(),this.m_LocationEx.GetGpsAltitude());
            if(CurrentGPSCoor == null)
            {
                return;
            }
            Point PT = this.m_MapControl.getMap().getViewConvert().MapToScreen(CurrentGPSCoor);

            //GPS状态图片资源
            if (this._GpsPointICON==null)this._GpsPointICON = ((BitmapDrawable)(PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.gpspointer))).getBitmap();

            float PointX = PT.x-this._GpsPointICON.getWidth()/2;
            float PointY = PT.y-this._GpsPointICON.getHeight()/2;
            canvas.drawBitmap(this._GpsPointICON, PointX, PointY, null);
        }

//		//5-画跟踪线
//		if (this.ShowTrackline)   //可以说明正在采集路线
//		{
//			//收集GPS轨迹点信息并计算长度值，并决定是否加入到列表中，根据系统的采样间隔设定
//			if (GPSAlwaysFix && CurrentGPSCoor!=null)this.CalMLenght(CurrentGPSCoor);
//
//			//绘制轨迹点信息，形成轨迹线
//			Point[] PList = PubVar.m_MapControl.getMap().getViewConvert().ClipPolyline(this._GPSTrackPointList, 0, 0);
//         	if (PList.length >=2)
//         	{
//				Path p = new Path();
//	        	for(int i=0;i<PList.length;i++)
//	        	{
//	        		if (i==0)p.moveTo(PList[i].x, PList[i].y);
//	        		else p.lineTo(PList[i].x, PList[i].y);
//	        	}
//
//	        	Paint pPen = new Paint();
//	        	pPen.setStyle(Style.STROKE);
//	        	pPen.setStrokeWidth(4);
//	        	pPen.setColor(Color.RED);
//	        	canvas.drawPath(p, pPen);
//         	}
//
//         	//绘制采集路线长度信息
//         	String LValue = lkmap.Tools.Tools.ReSetDistance(this._MLength);
//         	String LStarting = "";
//         	if (PubVar.m_GPSMap.GpsReceiveDataStatus == lkmap.Enum.lkGpsReceiveDataStatus.enStarting)LStarting="【正在采集】"+PubVar.m_SXX;
//         	if (PubVar.m_GPSMap.GpsReceiveDataStatus == lkmap.Enum.lkGpsReceiveDataStatus.enPause)LStarting="【暂停采集】"+PubVar.m_SXX;
//
//         	LStarting+="  长度="+LValue;
//         	if (PubVar.m_ShowStartZH)
//         	{
//         		double za = PubVar.m_StartZH+this._MLength;
//         		if (za>=0) LStarting+="  相对桩号="+lkmap.Tools.Tools.ReSetDistance(za);
//         	}
//         	HashValueObject hvObjectLen = PubVar.m_HashMap.GetValueObject("GPS_路段信息",true);
//         	hvObjectLen.ShowOnMap=true;hvObjectLen.LabelText = LStarting;
//
//         	//如果正在采集路线，启动自动存盘功能
//         	if (this.GpsReceiveDataStatus!=lkmap.Enum.lkGpsReceiveDataStatus.enStop)
//         	{
//         		PubVar.m_DoEvent.m_GPSLine.AutoSaveLine();
//         	}
//		}

//		List<String> infoList = PubVar.m_HashMap.GetInMapShowMessageList();
//		int HOffsetY = 70;
//		for (String info:infoList)
//		{
//			canvas.drawText(info,0, HOffsetY, _TextFont);
//			HOffsetY+=40;
//		}

        //6-自动移屏

        if (PubVar.AutoPan)
        {
            //判断是否已经超出了当前显示范围
            if (GPSAlwaysFix)
            {
                if (!PubVar.m_Map.getExtend().ContainsPoint(CurrentGPSCoor))
                {
                    this.m_MapControl._Pan.SetNewCenter(CurrentGPSCoor);
                }
            }
        }
    }
}
