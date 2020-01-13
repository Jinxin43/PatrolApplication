package com.example.dingtu2.myapplication.utils;

/**
 * Created by Dingtu2 on 2018/4/27.
 */

public interface ICallback {

    public void OnSuccess(String Str, Object ExtraStr);

    public void OnFail(String Str, Object ExtraStr);
}
