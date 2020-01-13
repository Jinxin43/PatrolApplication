package com.DingTu.Map;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Param {

    String _value;
    public void setValue(boolean value)
    {
        _value = String.valueOf(value);
    }
    public void setValue(String value)
    {
        _value=value;
    }
    public void setValue(int value)
    {
        _value = String.valueOf(value);
    }

    public void setValue(double value)
    {
        _value = String.valueOf(value);
    }
    public String getStringValue()
    {
        return _value;
    }
    public int getInt()
    {
        return Integer.parseInt(_value);
    }
    public double getDouble()
    {
        return Double.parseDouble(_value);
    }
    public boolean getBoolean()
    {
        return Boolean.parseBoolean(_value);
    }
}
