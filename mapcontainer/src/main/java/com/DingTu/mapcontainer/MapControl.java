package com.DingTu.mapcontainer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.DingTu.Base.PubVar;
import com.DingTu.Cargeometry.Size;
import com.DingTu.Map.Map;

import com.DingTu.Cargeometry.Envelope;

import java.util.HashMap;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class MapControl extends AppCompatImageView {

    public MapControl(Context context) {
        super(context);
        //初始化MapControl
        this.InitMapControl();
    }

    public Envelope TrackingRectangle;

    private IOnPaint _IOnPaint;                 //动态刷新接口
    public IOnPaint _GPSMapPaint;				//GPS状态刷新接口
    private ICommand _ICommand;                 //交互操作接口
    private IOnTouchCommand _IOnTouchCommand;   //手势操作接口
    private ZoomIn _ZoomIn;                     //放大操作类
    private ZoomOut _ZoomOut;                   //缩小操作类
    private ZoomInOutPan _ZoomInOutPan;			//手势放大缩小类
    public Pan _Pan;                           //移屏操作类
    public Select _Select;                     //实体选择类
    private Shutter _Shutter = null;			//卷帘操作


    //private MeasureArea _MeasureArea;           //测量面积类

    //前一动作的鼠标样式及动作
    public Tools m_BeforeTool;
    //public Cursor m_BeforeCursor;
    public ICommand m_BeforeCommand;
    public IOnPaint m_BeforeIOnPaint;


    //构造函数

    public void InitMapControl()
    {

//        base.MouseMove += new System.Windows.Forms.MouseEventHandler(this.MapControl_MouseMove);
//        base.MouseUp += new System.Windows.Forms.MouseEventHandler(this.MapControl_MouseUp);
//        base.MouseDown += new System.Windows.Forms.MouseEventHandler(this.MapControl_MouseDown);
//#if PC
//        base.MouseDoubleClick += new MouseEventHandler(MapControl_MouseDoubleClick);
//
//        base.MouseWheel += new System.Windows.Forms.MouseEventHandler(this.MapControl_MouseWheel);
//#endif

        _ZoomIn = new ZoomIn(this);
        _ZoomOut = new ZoomOut(this);
        _ZoomInOutPan = new ZoomInOutPan(this);
        _Pan = new Pan(this);
        _Select = new Select(this, true);
        _Shutter = new Shutter(this);
        //_MeasureArea = new MeasureArea(this);

        this.setActiveTool(Tools.None);

    }



    //属性

    //取得Map对象
    private Map _Map;
    public Map getMap()
    {
        return _Map;
    }
    public void setMap(Map value)
    {
        _Map = value;
    }


    //成员函数

    //设置操作命令
    public void SetCommand(ICommand _Command)
    {
        _ICommand = _Command;
    }

    //设置刷新的命令
    public void SetOnPaint(IOnPaint _OnPaint)
    {
        _IOnPaint = _OnPaint;
    }

    private void SetZoomInOut(float scale)
    {
        int gle_ZoomInAndOut = -1;
        _Map.setExtend(_Map.getExtend().Scale(scale));

        _Map.Refresh();
    }

    public void SetZoomIn()
    {
        this.SetZoomInOut(0.5f);
        _Map.Refresh();
    }

    public void SetZoomOut()
    {
        this.SetZoomInOut(2f);
        _Map.Refresh();
    }

    //工具枚举
    private Tools _Activetool;
    public Tools getActiveTool()
    {
        return _Activetool;
    }

    public void setActiveTools(Tools tools,IOnPaint _OnPaint,ICommand _Command)
    {
        this.setActiveTool(tools);
        _ICommand = _Command;
        _IOnPaint = _OnPaint;
        _Map.Refresh();
    }
    public void setActiveTools(Tools tools,IOnPaint _OnPaint,IOnTouchCommand _Command)
    {
        this.setActiveTool(tools);
        _IOnTouchCommand = _Command;
        _IOnPaint = _OnPaint;
        _Map.Refresh();
    }


    public void setActiveTool(Tools value)
    {
        //默认可
        _IOnTouchCommand = _ZoomInOutPan;
        if (value!=Tools.FullScreen)if (value!=Tools.FullScreenSize)_Activetool = value;
        switch (value)
        {
            case None:
                _ICommand = null;
                break;
            case Shutter:
                _IOnTouchCommand = _Shutter;
                _IOnPaint = _Shutter;
                _Shutter.StartShutter();
                break;
            case ZoomInOutPan:
                _IOnTouchCommand = _ZoomInOutPan;
                _IOnPaint = _ZoomInOutPan;
                if(_Map!=null) {
                    _Map.Refresh();
                }
                break;
            //放大
            case ZoomIn:
                _ICommand = _ZoomIn;
                _IOnPaint = _ZoomIn;
                break;
            //缩小
            case ZoomOut:
                _ICommand = _ZoomOut;
                _IOnPaint = _ZoomIn;
                break;
            //移屏
            case Pan:
                _IOnPaint = _Pan;
                break;
            //全屏
            case FullScreenSize:
                if (this._Map==null) return;
                int w = this.getWidth();
                int h = this.getHeight();
                DisplayMetrics dm = new DisplayMetrics();

                ((Activity)PubVar.m_DoEvent.m_Context).getWindowManager().getDefaultDisplay().getMetrics(dm);
                if(w==0)
                {
                    w = dm.widthPixels;
                }
                if(h==0)
                {
                    h = dm.heightPixels-com.DingTu.Base.Tools.DPToPix(83);
                }
                if (!(w==this._Map.getSize().getWidth() && h==this._Map.getSize().getHeight()))
                {
                    this._Map.setSize(new Size(w,h));
                }

//                if (this._Map==null) return;
//                int w = this.getWidth();
//                int h = this.getHeight();
//                if (!(w==this._Map.getSize().getWidth() && h==this._Map.getSize().getHeight()))
//                {
//                    this._Map.setSize(new Size(w,h));
//                }

                break;
            case FullScreen:
                setActiveTool(Tools.FullScreenSize);
                if (this.getMap()==null)return;
                this._Map.setExtend(this._Map.getFullExtendForView());
                this._Map.Refresh();
                break;
            //选择
            case Select:
                _IOnTouchCommand = _Select;
                this._Map.Refresh();
                break;
//            case MoveObject:
//                _ICommand = _MoveObject;
//                _IOnPaint = _MoveObject;
//                break;


            //查询
            case Query:
                break;
            //测量长度
            case MeasureLength:
                break;
            //测量面积
            case MeasureArea:
                //_ICommand = _MeasureArea;
                break;
            case CallMile:
                _ICommand = null;
                break;
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
//        if(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value == null ||
//                PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value.equals("true"))
//        {
////            PubVar.m_DoEvent.m_GlassView.SetGlassPoint(event.getX(),event.getY());
//        }

        if (this._IOnTouchCommand!=null)
        {
            this._IOnTouchCommand.SetOnTouchEvent(event);


            //处理放大镜中心点
//    		final int x = (int) event.getX();
//    		final int y = (int) event.getY();
//    		// 这个位置表示的是，画shader的起始位置
//    		drawable = new ShapeDrawable(new OvalShape());
//    		matrix = new Matrix();
//    		//matrix.setTranslate(RADIUS - x * FACTOR, RADIUS - y * FACTOR);
//    		matrix.setTranslate(this.getWidth()-x-30, this.getWidth()-y-30);

        }

//		  int action = event.getActionMasked();
//          int p = event.getPointerCount();//接触点数
//
//		switch(event.getAction() & MotionEvent.ACTION_MASK)
//		{
//			case MotionEvent.ACTION_DOWN:
//				this.MapControl_MouseDown(event);
//				break;
//			case MotionEvent.ACTION_UP:
//				this.MapControl_MouseUp(event);
//				break;
//			case MotionEvent.ACTION_MOVE:
//				this.MapControl_MouseMove(event);
//				break;
//			case MotionEvent.ACTION_POINTER_DOWN:
//		        //if (p>1){this.SetZoomOut();return true;}  //缩小
//				if (this._Activetool==Tools.ZoomInOutPan)
//					this.MapControl_MouseDown(event);
//				break;
//			case MotionEvent.ACTION_POINTER_UP:
//				if (this._Activetool==Tools.ZoomInOutPan)
//					this.MapControl_MouseUp(event);
//				break;
//		}
//		int i = event.getPointerCount();
//		i+=1;
//
//

        return true;
    }


    private void MapControl_MouseDown(MotionEvent e)
    {
        //m_MouseButtons = e.Button;
        if (_Map != null)
        {
            //单击中键，移屏
//            if (e.Button == MouseButtons.Middle)
//            {
//                //m_BeforeTool = _Activetool;
//                //m_BeforeCursor = this.Cursor;
//                m_BeforeIOnPaint = _IOnPaint;
//                m_BeforeCommand = _ICommand;
//                ActiveTool = Tools.Pan;
//                _IOnPaint = null;
//
//            }
            if (_Activetool == Tools.None) return;
            if (_ICommand == null) return;
            _ICommand.MouseDown(e);
        }
    }

    private void MapControl_MouseMove(MotionEvent e)
    {
        if (_Map != null)
        {
            if (_Activetool == Tools.None) return;
            if (_ICommand == null) return;
            _ICommand.MouseMove(e);
        }
    }

    private void MapControl_MouseUp(MotionEvent e)
    {
        //m_MouseButtons = MouseButtons.None;
        //if (e.Button == MouseButtons.Right) return;
        if (_Map != null)
        {
            if (_Activetool == Tools.None) return;
            if (_ICommand == null) return;
            _ICommand.MouseUp(e);
        }
    }

    private HashMap<String,IOnPaint> m_OnPaintList = new HashMap<String,IOnPaint>();
    public void AddOnPaint(String Id,IOnPaint pOnPaint)
    {
        if(!this.m_OnPaintList.containsKey(Id))this.m_OnPaintList.put(Id,pOnPaint);
    }
    public void ClearOnPaint(String Id)
    {
        this.m_OnPaintList.remove(Id);
    }

    //通过接口动态刷新控件
    boolean FirstLoad = true;
    @Override
    protected void onDraw(Canvas canvas)
    {
        if (FirstLoad)FirstLoad=false;

        super.onDraw(canvas);
        if (_IOnPaint != null)  _IOnPaint.OnPaint(canvas);


        if (this._Map==null)return;
        if (this._Map.getInvalidMap()) return;
        if (_GPSMapPaint!=null)_GPSMapPaint.OnPaint(canvas);
        for(IOnPaint pOnPaint:this.m_OnPaintList.values())pOnPaint.OnPaint(canvas);



//        if(PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value == null ||
//                PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value.equals("true"))
//        {
//            this.buildDrawingCache();
//        }
    }
}
