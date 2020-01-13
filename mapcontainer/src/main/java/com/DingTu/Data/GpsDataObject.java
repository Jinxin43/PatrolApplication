package com.DingTu.Data;

import android.util.Log;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Dataset.SQLiteDataReader;
import com.DingTu.Enum.lkRenderType;
import com.DingTu.Render.UniqueValueRender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Index.T4Index;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class GpsDataObject {

    public GpsDataObject()
    {
        this.SYS_OID= UUID.randomUUID().toString();  	//自动生成UUID号
        this.SYS_DATE= PubVar.SaveDataDate;     			//自动生成采集日期
    }


    //关联操作数据集
    protected Dataset m_Dataset = null;
    /**
     * 设置BaseObject对应的数据表
     * @param
     */
    public void SetDataset(Dataset pDataset)
    {
        this.m_Dataset = pDataset;
    }

    protected String SYS_STATUS = "0";    //0-正常状态，1-删除状态，2-新增状态
    /**
     * 设置BaseObject的SYS_STATUS
     * @param
     */
    public void SetSYS_STATUS(String status)
    {
        this.SYS_STATUS = status;
    }

    protected String SYS_TYPE = "";
    /**
     * 设置BaseObject的SYS_TYPE
     * @param type
     */
    public void SetSYS_TYPE(String type)
    {
        this.SYS_TYPE = type;
    }

    //实体ID
    protected int SYS_ID = -1;
    /**
     * 设置BaseObject的SYS_ID
     * @param sysid
     */
    public void SetSYS_ID(int sysid)
    {
        this.SYS_ID = sysid;
    }
    public int GetSYS_ID(){return this.SYS_ID;}


    protected String SYS_OID = "";
    /**
     * UUID号
     * @return
     */
    public String getSYS_OID()
    {
        return this.SYS_OID;
    }
    public void setSYS_OID(String oid)
    {
        this.SYS_OID = oid;
    }
    public String GetSYS_LABEL()
    {
        Geometry pGeometry = this.m_Dataset.GetGeometry(this.SYS_ID);
        if (pGeometry != null)return pGeometry.getTag()+"";
        return "";
    }

    protected String SYS_PHOTO="";
    /**
     * 设置图片列表
     * @param photoList
     */
    public void SetSYS_PHOTO(String photoList)
    {
        this.SYS_PHOTO = photoList;
    }
    /**
     * 获取照片列表
     * @return
     */
    public String GetSYS_PHOTO()
    {
        return this.SYS_PHOTO;
    }
    //返回照片数量
    public int GetPhotoCount()
    {
        if (this.SYS_PHOTO==null)this.SYS_PHOTO="";
        if (this.SYS_PHOTO.equals("")) return 0;
        else return this.SYS_PHOTO.split(",").length;
    }


    protected String SYS_DATE = "";
    /**
     * 设置采集日期
     * @param date
     */
    public void SetSYS_DATE(String date)
    {
        this.SYS_DATE = date;
    }


    /**
     * 保存属性数据
     * @return
     */
    public boolean SaveFeatureToDb()
    {
        if (this.SYS_ID==-1)
        {
            return this.SaveNewAdd(this.GetFeatureList());
        }
        else
        {
            return this.UpdateFeatrue(this.GetFeatureList());
        }
    }


    /**
     * 保存图形到数据库，注意此处需要更新(_I)索引表
     * @param pGeometry,SYS_Length=-1,SYS_Area=-1表示不更新
     * @return
     */
    public int SaveGeoToDb(Geometry pGeometry, double SYS_Length, double SYS_Area)
    {
        try
        {
            //转换图形实体为Byte[]
            Object[] value = new Object[]{""};
            if (pGeometry!=null)
            {
                byte[] GeoByte = Tools.GeometryToByte(pGeometry);
                value = new Object[]{GeoByte};
            }

            //实体的树型索引
            T4Index TIndex = pGeometry.CalCellIndex(this.m_Dataset.GetMapCellIndex());

            //更新状态
            if (this.SYS_ID!=-1)
            {
                //更新图形实体
                String UpdateLenArea = ",SYS_Length=%1$s,SYS_Area=%2$s";
                if (SYS_Length==-1 && SYS_Area==-1)UpdateLenArea="";
                else UpdateLenArea = String.format(UpdateLenArea, SYS_Length,SYS_Area);

                String SQL_D = "update %1$s set SYS_GEO=?,SYS_STATUS=%2$s"+UpdateLenArea+" where SYS_ID = "+this.SYS_ID;
                SQL_D = String.format(SQL_D,this.m_Dataset.getDataTableName(),this.SYS_STATUS);

                //更新索引信息，包括RIndex,CIndex,最大外接矩形
                String SQL_I = "Update %1$s set RIndex=%2$s,CIndex=%3$s,MinX=%4$s,MinY=%5$s,MaxX=%6$s,MaxY=%7$s where SYS_ID="+this.SYS_ID;
                SQL_I = String.format(SQL_I, this.m_Dataset.getIndexTableName(),TIndex.GetRow(),TIndex.GetCol(),
                        pGeometry.getEnvelope().getMinX(),pGeometry.getEnvelope().getMinY(),
                        pGeometry.getEnvelope().getMaxX(),pGeometry.getEnvelope().getMaxY());

                Log.d("","正在更新图形数据["+SQL_D+"]");
                if (this.m_Dataset.getDataSource().ExcuteSQL(SQL_D, value) &&
                        this.m_Dataset.getDataSource().ExcuteSQL(SQL_I))
                {
                    Log.d("","更新图形数据成功！");
                    return this.SYS_ID;
                }
            }
            else  //新增状态
            {
                //图形实体
                String SQL_D = "insert into "+ this.m_Dataset.getDataTableName()+" "+
                        "(SYS_GEO,SYS_STATUS,SYS_TYPE,SYS_OID,SYS_LABEL,SYS_DATE,SYS_PHOTO,SYS_Length,SYS_Area) values " +
                        "(?,0,'%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s')";
                SQL_D = String.format(SQL_D,this.SYS_TYPE,this.SYS_OID,"",PubVar.SaveDataDate,this.SYS_PHOTO,SYS_Length,SYS_Area);

                Log.d("","正在新增图形数据["+SQL_D+"]");


                if (this.m_Dataset.getDataSource().ExcuteSQL(SQL_D, value))
                {
                    //读取新插入实体的SYS_ID号
                    String SQL = "select max(SYS_ID) as objectid from "+this.m_Dataset.getDataTableName();
                    SQLiteDataReader DR = this.m_Dataset.getDataSource().Query(SQL);
                    if (DR.Read())
                    {
                        this.SYS_ID = Integer.valueOf(DR.GetString(0));
                    }DR.Close();

                    //保存索引信息
                    //索引实体
                    String SQL_I = "insert into "+ this.m_Dataset.getIndexTableName()+" "+
                            "(SYS_ID,RIndex,CIndex,MinX,MinY,MaxX,MaxY) values "+
                            "('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s')";
                    SQL_I = String.format(SQL_I,this.SYS_ID, TIndex.GetRow(),TIndex.GetCol(),
                            pGeometry.getEnvelope().getMinX(),pGeometry.getEnvelope().getMinY(),
                            pGeometry.getEnvelope().getMaxX(),pGeometry.getEnvelope().getMaxY());
                    Log.d("","正在新增索引数据["+SQL_I+"]");
                    if (this.m_Dataset.getDataSource().ExcuteSQL(SQL_I))
                    {
                        //更新属性信息
                        if (this.SYS_ID!=-1)
                        {
                            pGeometry.setSysId(this.SYS_ID);
//        	                Dataset pDataset = pDataSource.GetDatasetByName(this.TableName);
                            //List<Integer> newObjIdx = new ArrayList<Integer>();
                            // newObjIdx.add(this.SYS_ID);
                            //if (pDataset.QueryGeometryFromDB(newObjIdx, true))
//        	                pDataset.AddGeometry(pGeometry, true);
//        	                {
//        	                	pDataset.CalEnvelope();    //更新Dataset的Envelope使之包含新增实体
//        	                	//Geometry pGeometryNew = pDataset.GetGeometryByDIndex(this.SYS_ID);
//        	                	PubVar.m_Map.ClearSelection();
//        	                	PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).GetLayerByName(pDataset.getName()).getSelSelection().Add(pGeometry);
//        	                	PubVar.m_Map.Refresh();

                            return this.SYS_ID;
//        	                }
                        }
                    }



                }
            }


            return -1;
        }
        catch(Error e)
        {
            //Tools.ShowMessageBox(PubVar.m_DoEvent.m_GPSPoint.SubWindow,"["+this.SYS_TYPE + "] 保存失败！\r\n原因："+e.getMessage());
            return -1;
        }
    }

    /**
     * 从数据库删除实体
     * @return
     */
    public boolean DeleteFormDb()
    {
        String SQL = "delete from "+this.m_Dataset.getDataTableName()+" where SYS_ID = "+this.SYS_ID;
        boolean deleteOK = this.m_Dataset.getDataSource().ExcuteSQL(SQL);
        return deleteOK;
    }

    /**
     * 读取数据并更新界面显示
     * @param
     */
    public void ReadDataAndBindToView(String Where)
    {
        ReadDataAndBindToView(Where,"","");
    }
    public void ReadDataAndBindToView(String Where, String layerID,String layerType)
    {
        List<String> FeatureList = new ArrayList<String>();
        SQLiteDataReader DR = this.m_Dataset.getDataSource().Query("select * from "+this.m_Dataset.getDataTableName()+" where " + Where);
        if (DR==null) return;
        if (DR.Read())
        {
            String[] FieldNameList = DR.GetFieldNameList();
            for(String FieldName : FieldNameList)
            {
                if (FieldName.equals("SYS_ID")) this.SYS_ID = Integer.parseInt(DR.GetString(FieldName));
                if (FieldName.equals("SYS_OID")) this.SYS_OID = DR.GetString(FieldName);
                //if (FieldName.equals("SYS_LABEL")) this.SYS_LABEL = DR.GetString(FieldName);
                if (FieldName.equals("SYS_DATE")) this.SYS_DATE = DR.GetString(FieldName);
                if (FieldName.equals("SYS_PHOTO"))
                {
                    this.SYS_PHOTO = DR.GetString(FieldName);
                }

                if (FieldName.equals("SYS_GEO"))continue;  //读取图形

                String FValue = DR.GetString(FieldName);

                if (FValue==null)FValue="";
                FeatureList.add(FieldName+","+FValue);
            }
            this.SetFeatureList(FeatureList,layerID,layerType);
        }DR.Close();
        this.RefreshDataToView();    //将数据刷新到控件上
    }

    public HashMap<String,Object> ReadDataAllFieldsValue(int sysID)
    {
        List<HashMap<String,Object>> FeatureList = new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> fieldValue = new HashMap<String,Object>();
        try
        {
            SQLiteDataReader DR = this.m_Dataset.getDataSource().Query("select * from "+this.m_Dataset.getDataTableName()+" where SYS_ID =" + sysID);
            if (DR==null)
                return fieldValue;
            if (DR.Read())
            {
                String[] FieldNameList = DR.GetFieldNameList();
                for(String FieldName : FieldNameList)
                {
//	        		if (FieldName.equals("SYS_ID"))
//	        		{
//	        			//this.SYS_ID = Integer.parseInt(DR.GetString(FieldName));
//	        			continue;
//	        		}
                    if (FieldName.equals("SYS_GEO"))
                    {
                        continue;
                    }

                    if (FieldName.equals("SYS_STATUS"))
                    {
                        continue;
                    }

                    String FValue = DR.GetString(FieldName);
                    if (FValue==null)
                    {
                        FValue="";
                    }
                    fieldValue.put(FieldName,FValue);
                }

            }
            DR.Close();
        }
        catch(Exception ex)
        {

        }

        return fieldValue;
    }

    /**
     * 保存采集实体,FeatureList格式形式：字段名称='XXX'
     * @param FeatureList
     * @return
     */
    private boolean SaveNewAdd(List<String> FeatureList)
    {
        try
        {
            List<String> FieldNameList = new ArrayList<String>();
            List<String> FieldValueList = new ArrayList<String>();
            for(String FV:FeatureList)
            {
                String fv[] = FV.split("=");
                String FieldName = fv[0];
                String FieldValue = (fv.length!=2?"":fv[1]);
                FieldNameList.add(FieldName);FieldValueList.add(FieldValue);
            }

            String SQL = "insert into %1$s (%2$s) values (%3$s)";
            SQL = String.format(SQL,this.m_Dataset.getDataTableName(), Tools.JoinT(",", FieldNameList),Tools.JoinT(",", FieldValueList));
            Log.d("","正在保存数据["+SQL+"]");
            if (this.m_Dataset.getDataSource().ExcuteSQL(SQL)) return true;

        }
        catch(Error e)
        {
            // Tools.ShowMessageBox(PubVar.m_DoEvent.m_GPSPoint.SubWindow,"["+this.SYS_TYPE + "] 保存失败！\r\n原因："+e.getMessage());
            return false;
        }
        return false;
    }

    //更新采集实体,FeatureList格式形式：F1='XXX'
    private boolean UpdateFeatrue(List<String> FeatureList)
    {
        try
        {
            //更新实体的属性信息
            String SQL = "update %1$s set SYS_LABEL='%2$s',SYS_PHOTO='%3$s',SYS_TYPE='%6$s',%4$s where SYS_ID=%5$s";
            SQL = String.format(SQL, this.m_Dataset.getDataTableName(),"",this.SYS_PHOTO,Tools.JoinT(",", FeatureList),this.SYS_ID,this.SYS_TYPE);

            if (this.m_Dataset.getDataSource().ExcuteSQL(SQL))
            {
                Geometry pGeometry = this.m_Dataset.GetGeometry(this.SYS_ID);
                if (pGeometry != null)
                {
                    //更新标注信息
                    if (this.m_Dataset.getBindGeoLayer().getRender().getIfLabel())
                    {
                        String[] labelFieldList = this.m_Dataset.getBindGeoLayer().getRender().getLabelField().split(",");
                        if (labelFieldList.length!=0)
                        {
                            String ValueStr = "";
                            for(String feature:FeatureList)
                            {
                                String[] fInfo = feature.split("=");
                                for(String UVF:labelFieldList)
                                {
                                    if (fInfo[0].equals(UVF))ValueStr+=fInfo[1].replace("'","")+",";
                                }
                            }
                            if (ValueStr.length()>0)pGeometry.setTag(ValueStr.substring(0,ValueStr.length()-1));
                        }
                    }

                    //更新唯一渲染的UniqueValue
                    if (this.m_Dataset.getBindGeoLayer().getRender().getType()== lkRenderType.enUniqueValue)
                    {
                        List<String> UVFList = ((UniqueValueRender)this.m_Dataset.getBindGeoLayer().getRender()).GetUniqueValueFieldList();
                        String ValueStr = "";
                        for(String feature:FeatureList)
                        {
                            String[] fInfo = feature.split("=");
                            for(String UVF:UVFList)
                            {
                                if (fInfo[0].equals(UVF))ValueStr+=fInfo[1].replace("'","")+",";
                            }
                        }
                        if (ValueStr.length()>0)pGeometry.setTagForUniqueSymbol(ValueStr.substring(0,ValueStr.length()-1));
                    }
                    this.m_Dataset.getBindGeoLayer().getRender().UpdateSymbol(pGeometry);
                    PubVar.m_Map.Refresh();
                }

                return true;
            }
            return false;
        }
        catch (Error e)
        {
            Tools.ShowMessageBox("[" + this.SYS_TYPE + "] 更新失败！\r\n原因："+e.getMessage());
            return false;
        }
    }

    //数据与控件集合类
    protected List<DataBindOfKeyValue> DataBindList = new ArrayList<DataBindOfKeyValue>();

    /**
     * 将列表数据绑定到List<DataBindOfKeyValue>上
     * @param FeatureList
     */
    public void SetFeatureList(List<String> FeatureList,String layerID,String layerType)
    {
        for(int i=0;i<FeatureList.size();i++)
        {
            String[] fv = FeatureList.get(i).split(",");
            String FieldName = fv[0];
            String FieldValue = (fv.length!=2?"":fv[1]);

            this.SetDataBindItemValue(FieldName,FieldValue);

            //this.DataBindList.get(i).Value=FeatureList.get(i);
        }
    }

    /**
     * 将List<DataBindOfKeyValue>转化成FeatureList列表，格式：F1='XXX'
     * @return
     */
    public List<String> GetFeatureList()
    {
        List<String> FeatureList = new ArrayList<String>();
        for(int i=0;i<this.DataBindList.size();i++)
        {
            DataBindOfKeyValue dv = this.DataBindList.get(i);
            FeatureList.add(dv.DataKey+"='"+dv.Value+"'");
        }
        return FeatureList;
    }

    public String getFeatureValue(String key)
    {
        String value = "";
        for(DataBindOfKeyValue dv:this.DataBindList)
        {
            if(dv.Key.equals(key))
            {
                value = dv.Value;
                break;
            }
        }
        return value;
    }

    /**
     * 增加绑定项目
     * @param dbov
     */
    public void AddDataBindItem(DataBindOfKeyValue dbov)
    {
        this.DataBindList.add(dbov);
    }


    /**
     * 将数据刷新到控件上
     */
    public void RefreshDataToView()
    {
        for(DataBindOfKeyValue KV:DataBindList)
        {
            if (KV.ViewControl!=null)
            {
                Tools.SetValueToView(KV.Value, KV.ViewControl);
            }
        }
    }

    /**
     * 将控件中的值刷新到数据中
     */
    public void RefreshViewValueToData()
    {
        for(DataBindOfKeyValue KV:DataBindList)
        {
            if (KV.ViewControl!=null)
            {
                KV.Value = Tools.GetViewValue(KV.ViewControl);

            }
        }
    }

    /**
     * 设置指定的KEY的绑定项值
     * @param key
     * @param value
     */



    public void SetDataBindItemValue(String key,String value)
    {
        for(DataBindOfKeyValue DKV:this.DataBindList)
        {
            if (DKV.DataKey.equals(key))
            {
                DKV.Value=value;
            }
        }
    }
}
