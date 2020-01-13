package com.DingTu.Project;

import android.widget.Toast;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkOverMapType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class BKGridLayerExplorer {

    //工程对应的栅格底图文件，格式详见v1_BKLayerExplorer.JSONObjectToList()函数内部
    private List<HashMap<String,Object>> m_BKFileLlist = new ArrayList<HashMap<String,Object>>();
    public List<HashMap<String,Object>> GetBKFileList(){return this.m_BKFileLlist;}
    public String GetBKFileListStr()
    {
        List<String> bkFileList = new ArrayList<String>();
        for(HashMap<String,Object> hmObj:m_BKFileLlist)
        {
            bkFileList.add(hmObj.get("MapFileName")+"");
        }
        return "【"+bkFileList.size()+"】"+ Tools.JoinT(",", bkFileList);
    }
    public void SetBKFileList(List<HashMap<String,Object>> bkFileList){this.m_BKFileLlist=bkFileList;}



    /**
     * 保存栅格图层设置
     * @return
     */
    public boolean SaveBKLayer()
    {
        //保存底图文件信息
        String[] FieldList = {"Type","BKMapFile","MinX","MinY","MaxX","MaxY","CoorSystem","Transparent","Sort","Visible","F1"};
        String SQL_DEL = "delete from T_BKLayer where Type = '%1$s'";

        //保存并更新
        SQL_DEL = String.format(SQL_DEL,"栅格");
        if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_DEL))
        {
            for(HashMap<String,Object> ho:this.m_BKFileLlist)
            {
                String SQL_INS = "insert into T_BKLayer (%1$s) values ('%2$s')";
                List<String> ValueList = new ArrayList<String>();
                for(String field:FieldList)
                    if(ho.get(field)==null)
                    {
                        ValueList.add("");
                    }
                    else
                    {
                        ValueList.add(ho.get(field)+"");
                    }

                SQL_INS = String.format(SQL_INS,Tools.Joins(",", FieldList),Tools.JoinT("','", ValueList));
                boolean OK = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL_INS);
                if (!OK) return false;
            }
        } else return false;
        return true;
    }

    /**
     * 打开栅格底图数据源
     * 栅格背景，PubVar.m_Map.GetGridLayers().SetMapFileList(m_BKFileLlist);
     */
    public void OpenGridDataSource()
    {
        //判断是否为Web地图
        if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName().equals("WGS-84坐标"))
        {
            Toast.makeText(PubVar.m_DoEvent.m_Context, "WGS-84坐标", Toast.LENGTH_SHORT).show();
            //PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Satellite);
            PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Satellite);
            return;
//			 HashMap<String, Object> aHashMap = new HashMap<String, Object>();
//			 aHashMap.put("MapFileName", "Google卫星影像图");
//			 aHashMap.put("MapFileName", "Google地形图");
//			 m_BKFileLlist.add(aHashMap);
            //if (this.m_BKFileLlist.size()==0) return;
//			String MapFileName = this.m_BKFileLlist.get(0).get("MapFileName")+"";
//			if (MapFileName.indexOf("Google卫星影像图")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Satellite);return;}
//			if (MapFileName.indexOf("Google地形图")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Terrain);return;}
//			if (MapFileName.indexOf("Google街区图")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Street);return;}
//			if (MapFileName.indexOf("天地图卫星影像图")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enTianditu_Satellite);return;}
//			if (MapFileName.indexOf("天地图街区图")>=0){PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enTianditu_Street);return;}
        }
        else
        {
//            Toast.makeText(PubVar.m_DoEvent.m_Context, "呵呵", Toast.LENGTH_SHORT).show();
            //打开栅格数据底图
            PubVar.m_Map.GetGridLayers().SetMapFileList(this.m_BKFileLlist);
        }

    }



    private boolean m_GridVisible = true;
    public boolean GetBKVisible(){return this.m_GridVisible;}


    /**
     * 设置栅格底图可见性
     * @param visible
     */
    public void SetBKVisible(boolean visible)
    {
        this.m_GridVisible = visible;

        //栅格底图显示与隐藏
        PubVar.m_Map.getOverMapLayer().SetShowGrid(false);
        PubVar.m_Map.GetGridLayers().SetShowGrid(false);

        if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName().equals("WGS-84坐标"))
        {
            PubVar.m_Map.getOverMapLayer().SetShowGrid(visible);
        }
        else
        {
            PubVar.m_Map.GetGridLayers().SetShowGrid(visible);
        }
    }
}
