package com.DingTu.Dataset;

import android.util.Log;

import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkDatasetSourceType;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkGeometryStatus;
import com.DingTu.Enum.lkRenderType;
import com.DingTu.Layer.GeoLayer;
import com.DingTu.Render.UniqueValueRender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Envelope;
import com.DingTu.Cargeometry.Geometry;
import com.DingTu.Cargeometry.Part;
import com.DingTu.Cargeometry.Point;
import com.DingTu.Cargeometry.Polygon;
import com.DingTu.Cargeometry.Polyline;
import com.DingTu.Index.MapCellIndex;
import com.DingTu.Index.T4Index;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class Dataset {

    public Dataset(DataSource DS)
    {
        this._DataSource = DS;

        //图层的Map网格类
        this.m_MapCellIndex = new MapCellIndex();
    }

    //属于本数据集的控制网格
    private MapCellIndex m_MapCellIndex = null;
    public MapCellIndex GetMapCellIndex(){return this.m_MapCellIndex;}
    //public void SetMapCellIndex(MapCellIndex mCellIndex){this.m_MapCellIndex=mCellIndex;}


    //偏移量， 主要用于底图
    private double _OffsetX = 0,_OffsetY = 0;
    public void SetOffset(double _offsetX,double _offsetY)
    {
        //对数据进行偏移处理
        for(Geometry pGeometry : this._GeometryList.values())
        {
            if (pGeometry!=null)
            {
                this.OffsetTo(pGeometry, _offsetX, _offsetY,true);
            }
        }
        _OffsetX = _offsetX;_OffsetY = _offsetY;
    }
    private void OffsetTo(Geometry pGeometry,double offsetX,double offsetY,boolean AlwaysOffset)
    {
        if (!AlwaysOffset && offsetX==0 && offsetY==0) return;
        for(int p = 0;p<pGeometry.getPartCount();p++)
        {
            Part part = pGeometry.GetPartAt(p);
            for(Coordinate Coor :part.getVertexList())
            {
                Coor.setX(Coor.getX()-(AlwaysOffset?this._OffsetX:0)+offsetX);
                Coor.setY(Coor.getY()-(AlwaysOffset?this._OffsetY:0)+offsetY);
            }
        }
    }


    private Envelope _Envelope;
    public Envelope getEnvelope()
    {
        if (this._Envelope == null) this.CalEnvelope();
        return _Envelope;
    }
    public void setEnvelope(Envelope env)
    {
        this._Envelope = env;
    }

    /**
     * 计算实体的外接矩形
     */
    public void CalEnvelope()
    {
        Envelope pEnv = new Envelope(0, 0, 0, 0);
        for (Geometry pGeometry : this.GetGeometryList())
        {
            if (pGeometry.getStatus()==lkGeometryStatus.enDelete)continue;
            if (pEnv.IsZero()) pEnv = pGeometry.getEnvelope();
            else pEnv = pEnv.Merge(pGeometry.getEnvelope());
        }
        this.setEnvelope(pEnv);
    }

    /**
     * 增加字段
     * @param FieldName
     * @param FieldSize
     * @return
     */
    public boolean AddField(String FieldName,int FieldSize)
    {
        String SQL = "ALTER TABLE %1$s ADD %2$s varchar(%2$s)";
        return this.getDataSource().ExcuteSQL(SQL);
    }


    //属性表结构
    private List<FieldInfo> _TableStruct = null;
    public List<FieldInfo> getTableStruct()
    {
        //如果属性表结构为NUll，则加载新的属性结构
        if (_TableStruct == null) _TableStruct = this._DataSource.GetTableStruct(this.getId());
        return _TableStruct;
    }


    //数据集Id，为GUID值，不重复
    private String _Id = "";
    public String getId()
    {
        return _Id;
    }
    public void setId(String value)
    {
        this._Id = value;
    }

    private String proType = "";
    public String getProjectType()
    {
        return proType;
    }
    public void setPorjectType(String projectType)
    {
        proType= projectType;
    }

    //数据集对应的数据表名称，默认与数据集Id相同
    public String getDataTableName()
    {
        return this._Id+"_D";
    }
    public String getIndexTableName()
    {
        return this._Id+"_I";
    }

    //当前图层是否被编辑过
    private boolean _Edited = false;
    public boolean getEdited() {return _Edited;}
    public void setEdited(boolean value){_Edited = value;}

    //数据集类型（点，线，面）
    private lkGeoLayerType _Type = lkGeoLayerType.enUnknow;
    public lkGeoLayerType getType() {return this._Type; }
    public void setType(lkGeoLayerType value) {this._Type = value; }

    //被绑定的图层
    private GeoLayer _BindGeoLayer = null;
    public GeoLayer getBindGeoLayer() {return _BindGeoLayer;}
    public void setBindGeoLayer(GeoLayer value){_BindGeoLayer=value;}

    //数据集的数据来源类型，主要是区分采集数，还是背景数据，为图形转换做准备
    private lkDatasetSourceType _SourceType = lkDatasetSourceType.enUnknow;
    public lkDatasetSourceType getSourceType() {return _SourceType;}
    public void setSourceType(lkDatasetSourceType st){_SourceType=st;}

    //图形实体列表
    private HashMap<Integer,Geometry> _GeometryList = new HashMap<Integer,Geometry>();

    /**
     * 返回数据集当前集合中的实体，注意此集合不是全部实体
     * @return
     */
    public Collection<Geometry> GetGeometryList()
    {
        return this._GeometryList.values();
    }

    public void Clear()
    {
        this._GeometryList.clear();
    }

    /**
     * 得到指定Id的图形实体
     * @param SysId
     * @return
     */
    public Geometry GetGeometry(int SysId)
    {
        return _GeometryList.get(SysId);
    }

    /**
     * 创建实体
     * @param pGeometry  新实体
     * @return
     */
    public boolean AddGeometry(Geometry pGeometry)
    {
        this._GeometryList.put(pGeometry.getSysId(), pGeometry);
        return true;
    }

    /**
     * 数据库内实体数量，并不是当前列表内的实体数量
     * @return
     */
    public int GetAllObjectCount()
    {
        int AllCount = 0;
        String SQL = "select count(SYS_ID) as AllCount from "+this.getDataTableName()+" where SYS_STATUS='0'";
        SQLiteDataReader DR = this.getDataSource().Query(SQL);
        if (DR==null) return 0;
        if (DR.Read())
        {
            AllCount = DR.GetInt32(0);
        }DR.Close();
        return AllCount;
    }

    //数据所对应的数据源
    private DataSource _DataSource = null;
    public DataSource getDataSource() {return _DataSource;}


    //在数据集合中删除此数据集
    public boolean Remove()
    {
        return false;
        //this._DataSource.getDatasets().remove(this);
        //return this._DataSource.ExcuteSQL("Drop Table " + this.getTableName());
    }



    /**
     * 清理数据集，1-清理打上删除标识的实体(SYS_STATUS=1)，
     * 		   2-处理新增且意外停止的实体(SYS_STATUS=2)，
     * @return
     */
    public boolean Purge()
    {
        //清理打上删除标识的实体(SYS_STATUS=1)
        String DelSQL_I = "delete from "+this.getIndexTableName()+" where SYS_ID in (select SYS_ID from "+this.getDataTableName()+" where SYS_STATUS='1')";
        String DelSQL_D = "delete from "+this.getDataTableName()+" where SYS_STATUS='1'";
        boolean pOK = this.getDataSource().ExcuteSQL(DelSQL_I) && this.getDataSource().ExcuteSQL(DelSQL_D);
        if (!pOK) return false;

        //处理新增且意外停止的实体(SYS_STATUS=2)
        String SQL = "select SYS_ID,SYS_GEO from " + this.getDataTableName() + " where SYS_STATUS='2'";
        SQLiteDataReader DR = this.getDataSource().Query(SQL);
        if (DR == null) return true;
        List<HashMap<String,Object>> updateItemList = new ArrayList<HashMap<String,Object>>();
        while (DR.Read())
        {
            int SYSID = DR.GetInt32(0);     			//SYS_ID
            byte[] bytes = (byte[])DR.GetBlob(1);  	//图形

            //根据不同的图层调用不同的ByteToGeometry，作用是可能存在多部分实体的情况
            Geometry m_Geometry = Tools.ByteToGeometry(bytes, this.getType());
            m_Geometry.CalEnvelope();
            T4Index TIndex = m_Geometry.CalCellIndex(this.GetMapCellIndex());
            double Length=0,Area=0;
            if (this.getType()==lkGeoLayerType.enPolyline) {Length = ((Polyline)m_Geometry).getLength(true);}
            if (this.getType()==lkGeoLayerType.enPolygon) {Length = ((Polygon)m_Geometry).getLength(true);Area = ((Polygon)m_Geometry).getArea(true);}
            HashMap<String,Object> obj = new HashMap<String,Object>();
            obj.put("SYS_ID", SYSID);
            obj.put("Envelope", m_Geometry.getEnvelope());
            obj.put("T4Index", TIndex);
            obj.put("SYS_Length", Length);
            obj.put("SYS_Area", Area);
            updateItemList.add(obj);
        }DR.Close();

        //更新不完整信息
        if (updateItemList.size()==0) return true;
        for(HashMap<String,Object> hm:updateItemList)
        {
            Envelope pEnv = (Envelope)hm.get("Envelope");
            T4Index TIndex = (T4Index)hm.get("T4Index");

            //更新索引
            String UpdateSQL_I = "update "+this.getIndexTableName()+" set RIndex=%1$s,CIndex=%2$s,MinX=%3$s,MinY=%4$s,MaxX=%5$s,MaxY=%6$s where SYS_ID="+hm.get("SYS_ID");
            UpdateSQL_I = String.format(UpdateSQL_I, TIndex.GetRow(),TIndex.GetCol(),pEnv.getMinX(),pEnv.getMinY(),pEnv.getMaxX(),pEnv.getMaxY());

            //更新长度与面积
            String UpdateSQL_D = "update "+this.getDataTableName()+" set SYS_STATUS='0',SYS_Length=%1$s,SYS_Area=%2$s where SYS_ID="+hm.get("SYS_ID");
            UpdateSQL_D = String.format(UpdateSQL_D, hm.get("SYS_Length"),hm.get("SYS_Area"));

            pOK = this.getDataSource().ExcuteSQL(UpdateSQL_I) && this.getDataSource().ExcuteSQL(UpdateSQL_D);
            if (!pOK) return false;
        }
        return true;
    }

    public boolean UpdateXiaoBanHao(int id,String filedName, String xiaobanhao)
    {
        String updateSQL = "update "+this.getDataTableName()+" set "+filedName+"='"+xiaobanhao+"' where SYS_ID="+id;
        return this.getDataSource().ExcuteSQL(updateSQL);

    }

    //重新构建图层的索引
    public boolean BuildSpatialIndex()
    {
//         //MapCellIndex的最大范围，更新之前
//        // Envelope pEnv = this.m_MapCellIndex.getExtend();
//
//         Envelope newEnv = new Envelope(0,0,0,0);
//         for (int objIdx = 0; objIdx < this.getRecordCount(); objIdx++)
//         {
//             Geometry pGeometry = this.GetGeometry(objIdx);
//             if (pGeometry.getStatus()==lkGeometryStatus.enDelete)continue;
//             if (newEnv.IsZero())newEnv = pGeometry.getEnvelope();
//             else newEnv = newEnv.Merge(pGeometry.getEnvelope());
//         }
//
////         //判断最大范围是否需要更新
////         if (Math.abs(pEnv.getLeftTop().getX()-newEnv.getLeftTop().getX())<10 &&
////             Math.abs(pEnv.getLeftTop().getY()-newEnv.getLeftTop().getY())<10 &&
////        	 Math.abs(pEnv.getWidth()-newEnv.getWidth())<10&&
////        	 Math.abs(pEnv.getHeight()-newEnv.getHeight())<10)
////         {
////        	 return true;
////         }
//         this._CellIndex.ClearAllIndex();
//         this.m_MapCellIndex.setEnvelope(newEnv);
//
//         //更新图层实体索引表
//         for (int objIdx = 0; objIdx < this.getRecordCount(); objIdx++)
//         {
//             Geometry pGeometry = this.GetGeometry(objIdx);
//             if (pGeometry.getStatus()==lkGeometryStatus.enDelete)continue;
//             pGeometry.setIndex(objIdx);
//             this._CellIndex.SetIndex(pGeometry.CalCellIndex(this.m_MapCellIndex), pGeometry.getIndex());
//         }
//
        return true;


    }

//	/**
//	* 打开数据集，主要是加载数据集的最大外接矩形
//	* @return
//	*/
//     public	boolean	Open()
//     {
//	     //加入此句目的是列新SYS_STATUS的状态，也就是可能存在上次删除没保存的情况
//	    String	SQL="update "+this.getTableName()+" set SYS_STATUS=0";
//	     if (this._DataSource.getEditing())this._DataSource.ExcuteSQL(SQL);
//
//	     try
//	     {
//		     //读取最大矩形范围
//		     String	SQLExtend="select MinX,MinY,MaxX,MaxY from T_LayerExtend where LayerID='"+this.getId()+"'";
//		     SQLiteDataReader DR=this._DataSource.Query(SQLExtend);
//		     if(DR==null)return true;
//		     while(DR.Read())
//		     {
//			     double MinX=Double.valueOf(DR.GetString("MinX"));
//			     double MinY=Double.valueOf(DR.GetString("MinY"));
//			     double MaxX=Double.valueOf(DR.GetString("MaxX"));
//			     double MaxY=Double.valueOf(DR.GetString("MaxY"));
//			     this.GetMapCellIndex().setEnvelope(new Envelope(new Coordinate(MinX,MaxY),new Coordinate(MaxX,MinY)));
//			     this.setEnvelope(new Envelope(new Coordinate(MinX,MaxY),new Coordinate(MaxX,MinY)));
//		     }DR.Close();
//	     }
//	     catch(Exception e)
//	     {
//	    	 return false;
//	     }
//
//	     return true;
//     }


    /**
     * 根据在屏幕上选择矩形选择实体
     */
    public boolean QueryWithSelEnvelope(Envelope SelEnvelope,Selection SelSelection)
    {
        for(Geometry StGeometry:this.GetGeometryList())
        {
            //删除状态的不选择
            if (StGeometry.getStatus() == lkGeometryStatus.enDelete) continue;

            //点层，在指定矩形范围内则为选中
            if (this.getType() == lkGeoLayerType.enPoint)
            {
                Point StPoint = (Point)StGeometry;
                if (SelEnvelope.ContainsPoint(StPoint.getCoordinate()))
                {
                    //SelSelection.Add(StGeometry);
                    this.AddGeometryToSelection(SelSelection, StGeometry);
                }
            }

            //线层，有两种情况 1）正向矩形：在指定矩形范围内则为选中 2）反向矩形：搭边上就算
            if (this.getType() == lkGeoLayerType.enPolyline)
            {
                Polyline StPolyline = (Polyline)StGeometry;

                if (SelEnvelope.getType())    //正向矩形
                {
                    if (SelEnvelope.Contains(StPolyline.getEnvelope()))
                    {
                        //SelSelection.Add(StGeometry);
                        this.AddGeometryToSelection(SelSelection, StGeometry);
                    }
                }
                else    //反向矩形 搭边就选中
                {
                    if (SelEnvelope.Contains(StPolyline.getEnvelope()))
                    {
                        //SelSelection.Add(StGeometry);
                        this.AddGeometryToSelection(SelSelection, StGeometry);
                    }
                    else
                    {
                        if (SelEnvelope.Intersect(StPolyline.getEnvelope()))  //两个外接矩形相交
                        {
                            if (StPolyline.getSpatialRelation().Intersect(SelEnvelope.ConvertToPolyline()))
                            {
                                //SelSelection.Add(StGeometry);
                                this.AddGeometryToSelection(SelSelection, StGeometry);
                            }
                        }
                    }
                }
            }

            //面层，有两种情况 1）正向矩形：在指定矩形范围内则为选中 2）反向矩形：搭边上就算
            if (this.getType() == lkGeoLayerType.enPolygon)
            {
                Polygon StPolygon = (Polygon)StGeometry;
                if (SelEnvelope.getType())    //正向矩形
                {
                    if (SelEnvelope.Contains(StPolygon.getEnvelope()))
                    {
                        //SelSelection.Add(StGeometry);
                        this.AddGeometryToSelection(SelSelection, StGeometry);
                    }
                }
                else    //反向矩形 搭边就选中
                {
                    if (SelEnvelope.Intersect(StPolygon.getEnvelope()))  //两个外接矩形相交
                    {
                        if (SelEnvelope.Contains(StPolygon.getEnvelope()))
                        {
                            //SelSelection.Add(StGeometry);
                            this.AddGeometryToSelection(SelSelection, StGeometry);
                        }
                        else
                        {
                            boolean intersectPoly = false;
                            for (int j = 0; j < StPolygon.getPartCount(); j++)
                            {
                                Polyline intPL = new Polyline(); intPL.AddPart(StPolygon.GetPartAt(j));
                                intersectPoly = intPL.getSpatialRelation().Intersect(SelEnvelope.ConvertToPolyline());
                                if (intersectPoly) break;
                            }
                            if (intersectPoly) this.AddGeometryToSelection(SelSelection, StGeometry);//SelSelection.Add(StGeometry);
                        }
                    }
                }
            }
        }
        return false;
    }


//     public void QueryByExtend(Envelope extend,Selection showSelection)
//     {
//
//         //选择矩形跨的网格节点
//         String WhereFilter = this.GetMapCellIndex().CalCellIndexFilter(extend);
//
//         //矩形范围过滤
//         String EnvelopeFilter = "not (max(minx,%1$s)>min(maxx,%3$s) or max(miny,%2$s)>min(maxy,%4$s))";
//         EnvelopeFilter = String.format(EnvelopeFilter, extend.getMinX(), extend.getMinY(), extend.getMaxX(), extend.getMaxY());
//
//         //构造查询数据语句
//         String SQL = "select SYS_ID from %1$s where (%2$s) and (%3$s)";
//         SQL = String.format(SQL, this.getTableName() + "_I", WhereFilter, EnvelopeFilter);
//
//
//         Log.v("查询SQL", SQL);
//	     List<String> idList = new ArrayList<String>();
//	     SQLiteDataReader DR = this.getDataSource().Query(SQL);
//	     {
//	         if (DR != null) while (DR.Read())
//	             {
//	         		String SYSID = DR.GetString("SYS_ID");
//	             } DR.Close();
//	     }
//
//	     //对比已经存在的实体列表，排除已经存在的实体，再次查询数据库查询出真实的实体数据
//	     this.QueryGeometryFromDB(idList);
//     }

    /**
     * 从数据库查询实体 QueryIndexList
     * @param QueryIndexList
     * @return
     */

    int SYSID = 0;
    public boolean QueryGeometryFromDB(List<String> idList)
    {
        try
        {
            //清理缓存
            List<Integer> PurgeIdList = new ArrayList<Integer>();
            Set<Integer> allIdList = this._GeometryList.keySet();
            for(int id : allIdList)
            {
                if (idList.contains(id+""))
                {
                    idList.remove(id+"");
                    Geometry saveGeometry = this.GetGeometry(id);
                    this._BindGeoLayer.getShowSelection().Add(saveGeometry);
                } else PurgeIdList.add(id);
            }
            for(int id:PurgeIdList)this._GeometryList.remove(id);

//            Log.d("idList size",idList.size()+"");
            if (idList.size()==0) return true;
            //查询实体的SYS_ID列表
            String Where = "SYS_ID in ("+Tools.JoinT(",",idList) + ") and SYS_STATUS='0'";
//            Log.d("Where",Where);
            //需要查询的字段
            String SelectField = "SYS_GEO,SYS_ID";

            //同时检索唯一值字段及标注字段
            if (this._BindGeoLayer.getRender().getType() == lkRenderType.enUniqueValue)
            {
                List<String> UVFList = ((UniqueValueRender)this._BindGeoLayer.getRender()).GetUniqueValueFieldList();
                SelectField += ",(" + Tools.JoinT("||','||", UVFList) + ") as UniqueValueField";
            }
            if (this._BindGeoLayer.getRender().getIfLabel())
            {
                SelectField += ",(" + this._BindGeoLayer.getRender().getLabelField().replace(",","||','||") + ") as LabelField";
            }

            String SQL = "select " + SelectField + " from " + this.getDataTableName() + " where " + Where;
            SQLiteDataReader DR = this.getDataSource().Query(SQL);

            //读取实体
            if (DR == null) return false;
//            Log.d("DR","read is true");
            while (DR.Read())
            {
                SYSID = DR.GetInt32(1);     			//SYS_ID
                byte[] bytes = (byte[])DR.GetBlob(0);  	//图形

                //根据不同的图层调用不同的ByteToGeometry，作用是可能存在多部分实体的情况
                Geometry m_Geometry = Tools.ByteToGeometry(bytes, this.getType());
                m_Geometry.setSysId(SYSID);

                //偏移处理，主要用于底图
                this.OffsetTo(m_Geometry, this._OffsetX, this._OffsetY,false);

                //将实体加入到列表中
                this.AddGeometry(m_Geometry);

                //加入到选择集合中
                this._BindGeoLayer.getShowSelection().Add(m_Geometry);

                if (this._BindGeoLayer.getRender().getType() == lkRenderType.enUniqueValue) m_Geometry.setTagForUniqueSymbol(DR.GetString("UniqueValueField"));
                if (this._BindGeoLayer.getRender().getIfLabel()) m_Geometry.setTag(DR.GetString("LabelField"));

                this._BindGeoLayer.getRender().UpdateSymbol(m_Geometry);
            }DR.Close();

        }
        catch (Exception e)
        {
            Tools.ShowMessageBox("图层："+this.getDataTableName()+"，查询数据失败！"+SYSID+e.getMessage());
        }
        return true;

    }

    public boolean AddGeometryOutEnvelope(List<Integer> idList)
    {
        Set<Integer> allIdList = this._GeometryList.keySet();
        for(int id : allIdList)
        {
            if (idList.contains(id+""))
            {
                idList.remove(id+"");

            }

        }

        if (idList.size()==0) return false;
        //查询实体的SYS_ID列表
        String Where = "SYS_ID in ("+Tools.Join(",",idList) + ") and SYS_STATUS='0'";

        //需要查询的字段
        String SelectField = "SYS_GEO,SYS_ID";

        try
        {

            //同时检索唯一值字段及标注字段
            if (this._BindGeoLayer.getRender().getType() == lkRenderType.enUniqueValue)
            {
                List<String> UVFList = ((UniqueValueRender)this._BindGeoLayer.getRender()).GetUniqueValueFieldList();
                SelectField += ",(" + Tools.JoinT("||','||", UVFList) + ") as UniqueValueField";
            }
            if (this._BindGeoLayer.getRender().getIfLabel())
            {
                SelectField += ",(" + this._BindGeoLayer.getRender().getLabelField().replace(",","||','||") + ") as LabelField";
            }

            String SQL = "select " + SelectField + " from " + this.getDataTableName() + " where " + Where;
            SQLiteDataReader DR = this.getDataSource().Query(SQL);

            //读取实体
            if (DR == null) return false;


            while (DR.Read())
            {
                SYSID = DR.GetInt32(1);     			//SYS_ID
                byte[] bytes = (byte[])DR.GetBlob(0);  	//图形

                //根据不同的图层调用不同的ByteToGeometry，作用是可能存在多部分实体的情况
                Geometry m_Geometry = Tools.ByteToGeometry(bytes, this.getType());
                m_Geometry.setSysId(SYSID);

                //偏移处理，主要用于底图
                this.OffsetTo(m_Geometry, this._OffsetX, this._OffsetY,false);

                //将实体加入到列表中
                this.AddGeometry(m_Geometry);

                //加入到选择集合中
                this._BindGeoLayer.getShowSelection().Add(m_Geometry);

                if (this._BindGeoLayer.getRender().getType() == lkRenderType.enUniqueValue) m_Geometry.setTagForUniqueSymbol(DR.GetString("UniqueValueField"));
                if (this._BindGeoLayer.getRender().getIfLabel()) m_Geometry.setTag(DR.GetString("LabelField"));

                this._BindGeoLayer.getRender().UpdateSymbol(m_Geometry);
            }
            DR.Close();

        }
        catch(Exception ex)
        {
            Tools.ShowMessageBox("图层："+this.getDataTableName()+"，查询数据失败！"+SYSID+ex.getMessage());
        }

        return true;
    }

    /**
     * 从数据库内查询实体
     * @param SYS_IDList  实体SYS_ID列表，如果为Null，则查询全部实体
     * @return
     */
    public ArrayList<Geometry> QueryGeometryFromDB1(List<String> SYS_IDList)
    {
        ArrayList<Geometry> pGeometryList = new ArrayList<Geometry>();
        String SQL = "select SYS_GEO,SYS_ID from %1$s where SYS_ID in (%2$s) and SYS_STATUS='0' order by SYS_ID";
        if (SYS_IDList!=null)SQL = String.format(SQL, this.getDataTableName(),Tools.JoinT(",",SYS_IDList));
        else SQL = "select SYS_GEO,SYS_ID from "+this.getDataTableName()+" where SYS_STATUS='0' order by SYS_ID";

        SQLiteDataReader DR = this.getDataSource().Query(SQL);

        //读取实体
        if (DR == null) return pGeometryList;
        while (DR.Read())
        {
            int SYSID = DR.GetInt32(1);     			//SYS_ID
            byte[] bytes = (byte[])DR.GetBlob(0);  	//图形

            //根据不同的图层调用不同的ByteToGeometry，作用是可能存在多部分实体的情况
            Geometry m_Geometry = Tools.ByteToGeometry(bytes, this.getType());
            m_Geometry.setSysId(SYSID);
            pGeometryList.add(m_Geometry);
        }DR.Close();
        return pGeometryList;
    }

    /**
     * 根据当前视口范围选择实体，并将选择的实体放入选择集当中
     * @param Extend
     * @param ShowSelection
     * @return
     */
    public boolean QueryWithExtend(Envelope Extend,Selection ShowSelection)
    {
//     	//根据网络索引类确定当前显示范围所跨距的网格索引
//     	List<Integer> newExtendCellIndex = this.GetMapCellIndex().CalCellIndex(Extend);
//
//         //需要清理掉的实体列表
//         List<Integer> _PurgeObjectIndexList = new ArrayList<Integer>();
//
//         if (!this._DataSource.getEditing())
//         {
//          	//数据集中需要清除的网格索引
//             List<Integer> PurgeCellIndex = new ArrayList<Integer>();;
//             for (int cellIndex : this.getCellIndex().getCurrentCellIndex())
//             {
//                 if (newExtendCellIndex.indexOf(cellIndex)<0)PurgeCellIndex.add(cellIndex);
//             }
//             //需要清除掉的实体
//             for (int cellIndex : PurgeCellIndex)
//             {
//            	 for (int ObjIndex : this._CellIndex.getCellIndex().get(cellIndex))
//                 {
//            		 if (_PurgeObjectIndexList.indexOf(ObjIndex)<0) _PurgeObjectIndexList.add(ObjIndex);
//                 }
//             }
//         }
//         this._CellIndex.setCurrentCellIndex(newExtendCellIndex);
//
//
//         //根据从数据库重新读取的实体列表
//         List<Integer> QueryIndexListByDB = new ArrayList<Integer>();
//
//         //已经查询过的实体索引列表，主要目的是排除多次判读情况，也就是存在一个实体跨网格过引现象
//         List<Integer> AlwayQueryedIndexList = new ArrayList<Integer>();
//
//         for (int cellIndex : this.getCellIndex().getCurrentCellIndex())
//         {
//        	 List<Integer> ObjIndexList = this._CellIndex.getCellIndex().get(cellIndex);
//             for(int ObjectIdx : ObjIndexList)
//             {
//            	 //在当前选择集中是否存在
//            	 if (ShowSelection.InList(ObjectIdx))continue;
//
//            	 //是否已经处理过
//            	 if (AlwayQueryedIndexList.indexOf(ObjectIdx)>=0)continue;else AlwayQueryedIndexList.add(ObjectIdx);
//
//                 //取得实体
//            	 GeometryEx pGeometryEx = this.GetGeometryEx(ObjectIdx);
//
//                 //判断Geometry的外接矩形是否在当前视图窗口内部
//                 boolean InView = Extend.Intersect(pGeometryEx.getEnvelope());
//                 if (InView)
//                 {
//                     if (pGeometryEx.getGeometry()==null)
//                     {
//                         if (QueryIndexListByDB.indexOf(ObjectIdx) < 0) QueryIndexListByDB.add(ObjectIdx);
//                     }
//                     else
//                     {
//                         ShowSelection.Add(pGeometryEx.getGeometry());
//                     }
//                 }
//             }
//         }
//
//         //从数据库中查询实体图形信息,QueryIndexListByDB内存储Index，不是Dindex
//         if (QueryIndexListByDB.size() > 0)
//         {
//             if (this.QueryGeometryFromDB(QueryIndexListByDB,false))
//             {
//                 for (int idx : QueryIndexListByDB)
//                 {
//                     Geometry mGeometry  = this.GetGeometry(idx);
//                     if (mGeometry==null)
//                     {
//                    	 lkmap.Tools.Tools.ShowMessageBox("为啥退了？");
//                    	 continue;
//                     }
//                     //标识为删除的不选择
//                     if (mGeometry.getStatus() == lkGeometryStatus.enDelete) continue;
//                     ShowSelection.Add(mGeometry);
//                 }
//             }
//         }
//
//         //从内存中移除无用实体，有重复现象，也就是可能在清除列表中有，同时在ShowSelection也有
//         this.ClearGeometryFromMemory(_PurgeObjectIndexList, ShowSelection,true);
        return true;

    }

    //清理无用实体，释放内存
    public void ClearGeometryFromMemory(List<Integer> PurgeObjIndexList, Selection ShowSelection, boolean IfCheckShowSelection)
    {
//         if (!this.getDataSource().getEditing())
//         {
//             for (int OIdx : PurgeObjIndexList)
//             {
//            	 boolean InShowSelection = false;
//            	 //判断在ShowSelection中是否存在
//                 if (IfCheckShowSelection && ShowSelection.getGeometryIndexList().indexOf(OIdx) >= 0)InShowSelection=true;
//
//                 //清除
//                 if (!InShowSelection) this._GeometryList.get(OIdx).setGeometry(null);
//             }
//         }
    }



    public boolean HitTest(Coordinate SelPoint, double SelTolerance, Selection SelSelection)
    {
        return this.HitTest(SelPoint, SelTolerance, false, SelSelection);
    }

    /**
     * 点选
     * @param SelPoint 选择点位
     * @param SelTolerance 选择距离
     * @param MultiSelct 是否多选
     * @param SelSelection 选择结果集合
     * @return
     */
    public boolean HitTest(Coordinate SelPoint, double SelTolerance,boolean MultiSelct,Selection SelSelection)
    {
        boolean SelectOK = false;
        Polyline SelectBox = null;

        //重新计算选择点所在的网格索引
        for(Geometry StGeometry :this.GetGeometryList())
        {
            //删除状态的不选择
            if (StGeometry.getStatus() == lkGeometryStatus.enDelete) continue;

            //点层，计算两点之间的距离，在选择距离内，则属于选中
            if (this.getType() == lkGeoLayerType.enPoint)
            {
                Point StPoint = (Point)StGeometry;
                if (StPoint.HitTest(SelPoint, SelTolerance))
                {
                    SelectOK = true;
                    // SelSelection.Add(StGeometry);
                    this.AddGeometryToSelection(SelSelection, StGeometry);
                    if (!MultiSelct)return true;
                }
            }

            //线层，计算选择矩形框与线的相交
            if (this.getType() == lkGeoLayerType.enPolyline)
            {
                Polyline StPolyline = (Polyline)StGeometry;
                if (SelectBox == null)
                {
                    double R  = SelTolerance / 2;

                    List<Coordinate> vertextList = new ArrayList<Coordinate>();
                    SelectBox = new Polyline();
                    vertextList.add(new Coordinate(SelPoint.getX() - R, SelPoint.getY() - R));
                    vertextList.add(new Coordinate(SelPoint.getX() + R, SelPoint.getY() - R));
                    vertextList.add(new Coordinate(SelPoint.getX() + R, SelPoint.getY() + R));
                    vertextList.add(new Coordinate(SelPoint.getX() - R, SelPoint.getY() + R));
                    SelectBox.AddPart(new Part(vertextList));
                }
                if (StPolyline.getSpatialRelation().Intersect(SelectBox))
                {
                    SelectOK = true;
                    // SelSelection.Add(StGeometry);
                    this.AddGeometryToSelection(SelSelection, StGeometry);
                    if (!MultiSelct) return true;
                }
            }

            //面层，计算选取点是否在面内，如果在，则属于选中
            if (this.getType() == lkGeoLayerType.enPolygon)
            {
                Polygon StPolygon = (Polygon)StGeometry;
                if (StPolygon.HitTest(SelPoint, SelTolerance))
                {
                    SelectOK = true;
                    // SelSelection.Add(StGeometry);
                    this.AddGeometryToSelection(SelSelection, StGeometry);
                    if (!MultiSelct) return true;
                }
            }
        }
        return SelectOK;
    }

    /**
     * 加入实体到选择集合，如果有则去除
     */
    private void AddGeometryToSelection(Selection SelSelection, Geometry pGeomety)
    {
        if (!SelSelection.Remove(pGeomety))SelSelection.Add(pGeomety);
    }


    public void Dispose()
    {
        this._GeometryList.clear();
    }
}
