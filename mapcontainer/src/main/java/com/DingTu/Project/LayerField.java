package com.DingTu.Project;

import com.DingTu.Base.PubVar;
import com.DingTu.Enum.lkFieldType;

import java.util.List;
import java.util.UUID;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class LayerField {

    //字段唯一ID值
    private String _LayerID = "T"+(UUID.randomUUID().toString()).replace("-", "").toUpperCase();
    public String GetFieldID(){return this._LayerID;}

    //字段名称
    private String _FieldName = "";
    public String GetFieldName(){return this._FieldName;}
    public void SetFieldName(String fieldName){this._FieldName=fieldName;}

    //字段对应的数据表物理字段的名称，形如：F1,F2,F3....
    private String _DataFieldName = "";
    public String GetDataFieldName(){return this._DataFieldName;}
    public void SetDataFieldName(String dataFieldName){this._DataFieldName=dataFieldName;}

    //字段类型
    private lkFieldType _FieldType = lkFieldType.enString;
    public lkFieldType GetFieldType(){return this._FieldType;}

    private String _FieldTypeName = "";
    public String GetFieldTypeName(){return _FieldTypeName;}
    public void SetFieldTypeName(String fieldTypeName)
    {
        this._FieldTypeName = fieldTypeName;
        if (fieldTypeName.equals("字符串"))this._FieldType=lkFieldType.enString;
        if (fieldTypeName.equals("整型"))this._FieldType=lkFieldType.enInt;
        if (fieldTypeName.equals("浮点型"))this._FieldType=lkFieldType.enFloat;
        if (fieldTypeName.equals("布尔型"))this._FieldType=lkFieldType.enBoolean;
        if (fieldTypeName.equals("日期型"))this._FieldType=lkFieldType.enDateTime;
    }

    //字段大小
    private int _FieldSize = 255;
    public int GetFieldSize(){return this._FieldSize;}
    public void SetFieldSize(int fieldSize){this._FieldSize=fieldSize;}


    //字段精度
    private int _FieldDecimal = 0;
    public int GetFieldDecimal(){return this._FieldDecimal;}
    public void SetFieldDecimal(int fieldDecimal){this._FieldDecimal=fieldDecimal;}

    //关联数据字典项目
    private String _FieldEnumCode = "";
    public String GetFieldEnumCode(){return this._FieldEnumCode;}
//    public List<String> GetFieldEnumList(String yinzi)
//    {
////		return PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().GetZDValueList(this._FieldEnumCode);
//        return PubVar.m_DoEvent.m_DictDataDB.getEnumList("自定义工程", this._FieldEnumCode);
////		return new ArrayList<String>(PubVar.m_DoEvent.m_DictDataDB.getEnumList("自定义工程", this._FieldEnumCode).values());
//    }
//
//    public List<String> getFieldEnumList(String yinzi,String category)
//    {
//        return PubVar.m_DoEvent.m_DictDataDB.getEnumList(yinzi, category);
////		return new ArrayList<String>(PubVar.m_DoEvent.m_DictDataDB.getEnumList(yinzi, category).values());
//    }

    public void SetFieldEnumCode(String fieldEnumCode){this._FieldEnumCode=fieldEnumCode;}

    //是否关联项目是否允许手动输入
    private boolean _FieldEnumEdit = false;
    public boolean GetFieldEnumEdit(){return this._FieldEnumEdit;}
    public void SetFieldEnumEdit(boolean enumEdit){this._FieldEnumEdit=enumEdit;}

    private String fieldShortName="";
    public void SetFieldShortName(String dbName)
    {
        fieldShortName = dbName;
    }

    public String GetFieldShortName()
    {
        return fieldShortName;
    }



    private boolean isSelect = true;
    public void SetIsSelect(boolean s)
    {
        isSelect = s;
    }
    public boolean getIsSelect()
    {
        return isSelect;
    }
}
