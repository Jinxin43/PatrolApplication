package com.example.dingtu2.myapplication.utils;

import com.example.dingtu2.myapplication.manager.PhotoManager;

/**
 * Created by Dingtu2 on 2019/3/25.
 */

public class LogHelper {

    private  static  LogHelper instance;
    private LogHelper() {

    }

    public LogHelper getInstance()
    {
        synchronized (PhotoManager.class){
            if(instance == null)
            {
                instance = new LogHelper();
            }

            return instance;
        }
    }


}
