package com.DingTu.Base;

import android.content.Context;

import com.DingTu.Data.DeleteAddObject;
import com.DingTu.Data.GPSDataStatus;
import com.DingTu.Data.GpsLine;
import com.DingTu.Data.GpsPoint;
import com.DingTu.Data.GpsPoly;
import com.DingTu.Data.RoundGPSLine;
import com.DingTu.GPS.GPSLocate;
import com.DingTu.GPS.GpsInfoManage;
import com.DingTu.Project.ConfigDB;
import com.DingTu.Project.ProjectDB;
import com.DingTu.Project.UserConfigDB;
import com.DingTu.ToolBar.Agent_Measure;
import com.DingTu.mapcontainer.MapControl;
import com.DingTu.mapcontainer.ScaleBar;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class DoEvent {

    public ProjectDB m_ProjectDB = null;
    private MapControl m_MapControl = null;
    public Context m_Context = null;					//主界面的Context

    public DeleteAddObject m_Delete = null;				//删除实体
    public GPSLocate m_GPSLocate = null;				//GPS通信类
    public GpsLine m_GPSLine = null;					//采集路线
    public RoundGPSLine mRoundLinePresenter = null;
    public GpsPoint m_GPSPoint = null;					//采集点位
    public GpsPoly m_GPSPoly = null;				//采集面
    public UserConfigDB m_UserConfigDB = null;			//用户自定义配置操作类
    public ConfigDB m_ConfigDB = null;
    public GpsInfoManage m_GpsInfoManage = null;
    public GPSDataStatus mGPSSatus = null;
    public ScaleBar mScaleBar = null;
    public Agent_Measure m_Agent_Measure = null; // 测量类

//    public Agent_Measure m_Agent_Measure = null;		//测量类
    public DoEvent(Context _context) {
        m_ConfigDB = new ConfigDB();
        m_Context = _context;

        this.m_GpsInfoManage = new GpsInfoManage();
        this.m_ProjectDB = new ProjectDB();
        mGPSSatus = new GPSDataStatus();
        mScaleBar = new ScaleBar();
    }
}
