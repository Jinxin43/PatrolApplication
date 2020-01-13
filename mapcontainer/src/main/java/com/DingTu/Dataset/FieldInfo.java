package com.DingTu.Dataset;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class FieldInfo {

    //字段名称
    private String _Name = "";
    public String getName()
    {
        return _Name;
    }
    public void setName(String value)
    {
        _Name = value;
        _Name = _Name.toUpperCase();
        if (_Name.indexOf("SYS_")>=0) this._Type=true;
//        if (_Name.equals("SYS_ID") || _Name.equals("SYS_STATUS") || _Name.equals("SYS_OID") || _Name.equals("SYS_AUTOID") ||
//            _Name.equals("SYS_BZ1") || _Name.equals("SYS_BZ2") || _Name == "SYS_BZ3" || _Name == "SYS_BZ4" || _Name == "SYS_BZ5")
//        {
//            this._Type = true;
//        }
    }

    //字段标题，也就是字段的中文别名
    private String _Caption = "";
    public String getCaption()
    {
        return _Caption;
    }
    public void setCaption(String value)
    {
        _Caption = value;
    }

    //字段的类型 true=系统字段，false=数据字段
    private boolean _Type = false;
    public boolean getType()
    {
        return _Type;
    }

}
