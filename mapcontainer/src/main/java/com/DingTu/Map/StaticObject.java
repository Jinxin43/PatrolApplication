package com.DingTu.Map;

import com.DingTu.CoordinateSystem.ProjectSystem;

import java.util.Date;
/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class StaticObject {

    //public static MapCellIndex soMapCellIndex = null;
    public static ProjectSystem soProjectSystem = new ProjectSystem();

    private static Date dtStartTime = null;
    public static void StartTime()
    {
        dtStartTime = new Date(System.currentTimeMillis());
    }


    public static int EndTime()
    {
        Date endDate = new Date(System.currentTimeMillis());
        long diff = endDate.getTime() - dtStartTime.getTime();
        return (int)diff;
    }
}
