package com.example.dingtu2.myapplication;

import android.app.Application;

import com.example.dingtu2.myapplication.utils.CrashCatchHandler;

/**
 * Created by Dingtu2 on 2018/9/11.
 */

public class PatrolApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashCatchHandler crashCatchHandler = CrashCatchHandler.getInstance();//获得单例
        crashCatchHandler.init(getApplicationContext());//初始化,传入context
    }
}
