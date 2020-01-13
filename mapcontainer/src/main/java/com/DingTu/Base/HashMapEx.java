package com.DingTu.Base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class HashMapEx {
    private List<HashValueObject> m_HashMap = new ArrayList<HashValueObject>();

    //得到需要在Map中动态显示的信息
    public List<String> GetInMapShowMessageList()
    {
        List<String> InfoList = new ArrayList<String>();
        for(HashValueObject hvObj:this.m_HashMap)
        {
            if (hvObj.ShowOnMap)InfoList.add(hvObj.LabelText);
        }
        return InfoList;
    }

    //取得指定Key的HashValueObject实体
    public HashValueObject GetValueObject(String Key)
    {
        return this.GetValueObject(Key, false);
    }
    public HashValueObject GetValueObject(String Key,boolean CreateNew)
    {

        for(HashValueObject hvObj1:this.m_HashMap)
        {
            if (hvObj1.Key.equals(Key)) return hvObj1;
        }

        if (CreateNew)
        {
            HashValueObject hvObj = new HashValueObject();
            hvObj.Key = Key;
            this.m_HashMap.add(hvObj);
            return hvObj;
        }
        else return null;
    }

    //加入HashValueObject实体
    public void Add(String Key,HashValueObject hvObject)
    {
        for(HashValueObject hvObj1:this.m_HashMap)
        {
            if (hvObj1.Key.equals(Key)) {this.m_HashMap.remove(hvObj1);break;}
        }
        hvObject.Key = Key;
        this.m_HashMap.add(hvObject);
    }

    public void Delete(String Key)
    {
        for(HashValueObject hvObj1:this.m_HashMap)
        {
            if (hvObj1.Key.equals(Key)){ this.m_HashMap.remove(hvObj1);return;}
        }
    }

}
