package com.DingTu.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.DingTu.Base.Tools;
import com.DingTu.CoordinateSystem.ViewConvert;
import com.DingTu.Dataset.DataSource;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Dataset.SQLiteDataReader;
import com.DingTu.Enum.lkGeoLayersType;
import com.DingTu.Base.PubVar;
import com.DingTu.Layer.GeoLayer;
import com.DingTu.Layer.GeoLayers;
import com.DingTu.Layer.GridLayers;
import com.DingTu.Layer.OverLayer;
import com.DingTu.OverMap.OverMap;
import com.DingTu.mapcontainer.MapControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Envelope;
import com.DingTu.Cargeometry.Size;
import com.DingTu.mapcontainer.ScaleBar;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class Map {

    //系统执行文件的路径
    private String _SystemPath = "";
    public String getSystemPath(){return _SystemPath; }
    public void setSystemPath(String value) {_SystemPath=value;}

//    //Map的动态跟踪层
//    private TrackLayer _TrackLayer = null;
//    public TrackLayer TrackLayer
//    {
//        get { if (_TrackLayer == null) _TrackLayer = new TrackLayer(this); return this._TrackLayer; }
//    }

    //数据采集图层集合
    private GeoLayers _DAGeoLayers = new GeoLayers();

    //背景数据源图层集合
    private GeoLayers _BKGeoLayers = new GeoLayers();

    public GeoLayers getGeoLayers(lkGeoLayersType geoLayersType)
    {
        if (geoLayersType==lkGeoLayersType.enVectorEditingData) return _DAGeoLayers;
        if (geoLayersType==lkGeoLayersType.enVectorBackground) return _BKGeoLayers;
        if (geoLayersType== lkGeoLayersType.enAll)
        {
            GeoLayers pGeoLayers = new GeoLayers();
            for(GeoLayer pGeoLayer :this._DAGeoLayers.getList())pGeoLayers.AddLayer(pGeoLayer);
            for(GeoLayer pGeoLayer :this._BKGeoLayers.getList())pGeoLayers.AddLayer(pGeoLayer);
            return pGeoLayers;
        }
        return null;
    }


    //OverLayer的图层集合
    private OverLayer _OverLayer = new OverLayer();
    public OverLayer getOverLayer()
    {
        return _OverLayer;
    }

    //清空选择集合
    public void ClearSelection()
    {
        ((MapControl)this._DrawPicture)._Select.ClearAllSelection();
    }

    //坐标转换类（屏幕坐标与实际坐标转换）
    private ViewConvert _ViewConvert = new ViewConvert();
    public ViewConvert getViewConvert()
    {
        return _ViewConvert;
    }

    //以像素为单位的Map尺寸，也就是MapControl控件的尺寸
    public Size getSize()
    {
        return this.getViewConvert().getSize();
    }

    public Bitmap MaskBitmap = null;
    public Bitmap bp = null;
    public void setSize(Size value)
    {
//        if(PubVar.m_DoEvent.m_AuthorizeTools.m_AuthorizePass)
//        {
//            bp= null;
//        }

        this.Dispose();
        this.getViewConvert().setSize(value);
        //当Map尺寸发生变化时要重新创建Image对象，以适应MapControl控件的大小
        bp = Bitmap.createBitmap(this._ViewConvert.getSize().getWidth(),
                this._ViewConvert.getSize().getHeight(),
                Bitmap.Config.ARGB_8888);
        this.MaskBitmap = Bitmap.createBitmap(bp,0,0,bp.getWidth(),bp.getHeight());
        this._DrawPicture.setImageBitmap(bp);
        this._Graphics = new Canvas(bp);
    }

    public void setEmpty()
    {
        this.Dispose();

        int max = 20000000;
        int[]  colors= new int[max];
        for(int i=0;i<max;i++)
        {
            colors[i]= Color.GRAY;
        }

        Bitmap newbp = Bitmap.createBitmap(colors,this._ViewConvert.getSize().getWidth(),
                this._ViewConvert.getSize().getHeight(),
                Bitmap.Config.ARGB_8888);
        this.bp = newbp.copy(Bitmap.Config.ARGB_8888, true);
        this.MaskBitmap = Bitmap.createBitmap(bp,0,0,bp.getWidth(),bp.getHeight());
        this._DrawPicture.setImageBitmap(bp);
        this._Graphics = new Canvas(bp);
    }

    //Map的当前视图外接矩形，单位（米），即Map.Extend
    public Envelope getExtend()
    {
        return this._ViewConvert.getExtend();
    }
    public void setExtend(Envelope value)
    {
        this._ViewConvert.setExtend(value);
    }

    //Map.Extend的实际单位（米）中心坐标，即Map.Center
    public Coordinate getCenter()
    {
        return this._ViewConvert.getCenter();
    }
    public void setCenter(Coordinate value)
    {
        this._ViewConvert.setCenter(value);
    }

    //计算指定像素换算实际距离
    public double ToMapDistance(double PixDistance)
    {
        return this.getViewConvert().getZoomScale() * PixDistance;
    }

    //Map的最大视图外接矩形，也就是全屏范围，单位（米），即Map.FullExtend;
    public Envelope getFullExtend()
    {
        return this.getViewConvert().getFullExtend();
    }
    public void setFullExtend(Envelope value)
    {
        this.getViewConvert().setFullExtend(value);
    }

    /**
     * 获取比较合理的视图最大外接矩形
     * @return
     */
    public Envelope getFullExtendForView()
    {
        Envelope _ExtendForView = new Envelope(0,0,0,0);

        //提取当前正在采集的数据最大外接矩形
        _ExtendForView = PubVar.m_Workspace.GetDataSourceByEditing().GetEnvelope();

        //提取当前栅格图的最大外接矩形
        if (_ExtendForView.IsZero())
        {
            if (this.GetGridLayers().GetExtend()!=null){
                _ExtendForView = this.GetGridLayers().GetExtend();
            }
        }

        //提取当前矢量底图的最大外接矩形
        if (_ExtendForView.IsZero())
        {
            for (DataSource pDataSource:PubVar.m_Workspace.GetDataSourceList())
            {
                if (!pDataSource.getEditing())
                {
                    if (_ExtendForView.IsZero())_ExtendForView = pDataSource.GetEnvelope();
                    else _ExtendForView = _ExtendForView.Merge(pDataSource.GetEnvelope());
                }
            }
        }

        //提取系统默认最大外接矩形，在new Map()内定义
        if (_ExtendForView.IsZero())_ExtendForView = this.getFullExtend();

        //进行视图矩形的修正，使其符合显示需要，主要是高宽修正，避免显示不了
        return this.AdjustEnvelopeFitScreen(_ExtendForView);
//    	Coordinate CenterPT = _ExtendForView.getCenter();
//    	double W = _ExtendForView.getWidth()*2;  //此处放大目的是为了显示全图，避免上下工具条的遮挡
//    	double H = _ExtendForView.getHeight()*2;
//    	if (W>=H)H = W*this.getViewConvert().getSize().getHeight() / this.getViewConvert().getSize().getWidth(); //缩放高度
//    	else W = H*this.getViewConvert().getSize().getWidth() / this.getViewConvert().getSize().getHeight();  //缩放宽度
//    	return new Envelope(CenterPT.getX()-W/2,CenterPT.getY()+H/2,CenterPT.getX()+W/2,CenterPT.getY()-H/2);
    }

    /**
     * 调整矩形来适合屏幕显示
     * @param pEnv
     * @return
     */
    public Envelope AdjustEnvelopeFitScreen(Envelope pEnv)
    {
        Coordinate CenterPT = pEnv.getCenter();
        double W = pEnv.getWidth()*1.2;  //此处放大目的是为了显示全图，避免上下工具条的遮挡
        double H = pEnv.getHeight()*1.2;
        if (W>=H)H = W*this.getViewConvert().getSize().getHeight() / this.getViewConvert().getSize().getWidth(); //缩放高度
        else W = H*this.getViewConvert().getSize().getWidth() / this.getViewConvert().getSize().getHeight();  //缩放宽度
        return new Envelope(CenterPT.getX()-W/2,CenterPT.getY()+H/2,CenterPT.getX()+W/2,CenterPT.getY()-H/2);
    }

    //Map状态
    private boolean _InvalidMap = false;
    public boolean getInvalidMap()
    {
        return _InvalidMap;
    }
    public void setInvalidMap(boolean value)
    {
        _InvalidMap = value;
    }

    //画布的Graphics
    private Canvas _Graphics;
    public Canvas getDisplayGraphic()
    {
        return _Graphics;
    }

    //绘画平面，所有的图形均绘在上面
    private ImageView _DrawPicture;
    public ImageView getDrawPicture()
    {
        return _DrawPicture;
    }

    //(new Map) 初始化Map对象实例
    public Map(MapControl mapControl)
    {
        this._DrawPicture = mapControl;
        int w = 240;
        int h = 240;
        this.setSize(new Size(w,h));

        Coordinate LT = StaticObject.soProjectSystem.WGS84ToXY(73,53, 0);
        Coordinate RB = StaticObject.soProjectSystem.WGS84ToXY(135,3, 0);
        this.setFullExtend(new Envelope(LT,RB));
        mapControl.setMap(this);
    }

    //比例尺
    private ScaleBar _ScaleBar = null;
    /**
     * 设置比例尺操作类
     * @param _scaleBar
     */
    public void SetScaleBar(ScaleBar _scaleBar)
    {
        this._ScaleBar = _scaleBar;
    }

    //底图栅格图层
    private GridLayers _GridLayers = new GridLayers(this);
    public GridLayers GetGridLayers(){return this._GridLayers;}

    //叠加影像图层，也就是wgs84坐标系下的底图栅格层
    private OverMap _OverMapLayer = new OverMap(this);
    public OverMap getOverMapLayer(){return this._OverMapLayer;}

    //图层刷新 (FastRefresh、Refresh)
    //刷新全部图层
    public void Refresh()
    {

        //刷新比例尺
        if (this._ScaleBar!=null){
            this._ScaleBar.RefreshScaleBar(this);
        }

        //刷新栅格图层
        this._OverMapLayer.Refresh();
        this._GridLayers.Refresh();

        //读取实体
//        StaticObject.StartTime();

        //针对多数据源背景需要处理
        if (this._BKGeoLayers.getList().size()>0)
        {
            List<DataSource> MBKDataSourceList = new ArrayList<DataSource>();
            for(GeoLayer pGeoLayer :this._BKGeoLayers.getList())
            {
                DataSource pDataSource = pGeoLayer.getDataset().getDataSource();
                if (!MBKDataSourceList.contains(pDataSource))MBKDataSourceList.add(pDataSource);
            }

            for(DataSource pDataSource :MBKDataSourceList)
            {
                List<GeoLayer> pGeoLayerList = new ArrayList<GeoLayer>();
                for(Dataset pDataset:pDataSource.getDatasets())pGeoLayerList.add(pDataset.getBindGeoLayer());
                this.CalRefresh(pGeoLayerList);
            }
        }

        //采集数据
        this.CalRefresh(this._DAGeoLayers.getList());
//    	 String T1 = "读取图形："+StaticObject.EndTime();

        //快速显示
//    	StaticObject.StartTime();
        this.FastRefresh();
//        String T2 = "显示图形："+StaticObject.EndTime();

//        Paint _Font = new Paint();
//    	_Font.setAntiAlias(true);
//    	_Font.setTextSize(20);
//    	Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
//    	_Font.setTypeface(TF);

        //this._Graphics.drawText(T1, 0, 200, _Font);
        //this._Graphics.drawText(T2, 0, 250, _Font);

    }

    /**
     * 根据当前显示范围计算图层需要显示的实体
     * @param geoLayerList
     */
    private void CalRefresh(List<GeoLayer> geoLayerList)
    {
        if (geoLayerList.size()==0) return;


//        for (GeoLayer layer : geoLayerList)
//        {
//        	layer.getShowSelection().RemoveAll();
//            if (layer.getVisibleScaleMax() >= layer.getMap().getViewConvert().getZoom() &&
//                layer.getVisibleScaleMin() <= layer.getMap().getViewConvert().getZoom() &&
//                layer.getVisible() == true)
//            {
//                //计算每个图层中需要显示的实体列表，赋入图层的ShowSelection中
//                layer.Refresh();  //在其中有清理索引的部分
//            }
//        }

        //测试联合查询数据，四叉树索引
        StaticObject.StartTime();
        DataSource pDataSource = null;
        List<String> SQLList = new ArrayList<String>();
        for (GeoLayer layer : geoLayerList)
        {

            DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
            double D = dm.densityDpi * PubVar.m_Map.getViewConvert().getZoomScale();

            layer.getShowSelection().RemoveAll();
            if (layer.getVisibleScaleMax() >= D/0.0254&&
                    layer.getVisibleScaleMin() <= D/0.0254 &&
                    layer.getVisible() == true)
            {
                pDataSource = layer.getDataset().getDataSource();
                //选择矩形跨的网格节点
                String WhereFilter = layer.getDataset().GetMapCellIndex().CalCellIndexFilter(this.getExtend());
                //矩形范围过滤
                String EnvelopeFilter = "not (max(minx,%1$s)>min(maxx,%3$s) or max(miny,%2$s)>min(maxy,%4$s))";
                EnvelopeFilter = String.format(EnvelopeFilter, this.getExtend().getMinX(), this.getExtend().getMinY(), this.getExtend().getMaxX(), this.getExtend().getMaxY());
                //EnvelopeFilter = "1=1";
                //构造查询数据语句
                String SQL = "select SYS_ID,'%4$s' as TName  from %1$s where (%2$s) and (%3$s)";
                SQL = String.format(SQL, layer.getDataset().getIndexTableName(), WhereFilter, EnvelopeFilter, layer.getDataset().getId());
                SQLList.add(SQL);
            }

        }

        if (SQLList.size()==0) return;

        String QuerySQL = Tools.JoinT("\r\nunion all\r\n",SQLList);
        // Log.v("查询SQL", QuerySQL);
        HashMap<String,List<String>> idList = new HashMap<String,List<String>>();
        SQLiteDataReader DR = pDataSource.Query(QuerySQL);
        {
            if (DR != null) while (DR.Read())
            {
                String SYSID = DR.GetString("SYS_ID");

                String TName = DR.GetString("TName");
                if (!idList.containsKey(TName))
                {
                    idList.put(TName,new ArrayList<String>());
                }
                idList.get(TName).add(SYSID);


//                Log.d("SYS_ID",TName+":"+SYSID);
            } DR.Close();

        }

        for(String TName : idList.keySet())
        {
//            Log.d("idList",TName);
            for(GeoLayer pGeoLayer:geoLayerList)
            {
//                Log.d("pGeoLayer",pGeoLayer.getId());
                if (pGeoLayer.getId().equals(TName)) {
                    pGeoLayer.getDataset().QueryGeometryFromDB(idList.get(TName));
//                    Log.d("TName",pGeoLayer.getId());
                }

            }
        }



//
//        //对比已经存在的实体列表，排除已经存在的实体，再次查询数据库查询出真实的实体数据
//        SQLList.clear();
//        for(String TName : idList.keySet())
//        {
//            GeoLayer pGeoLayer = this._BKGeoLayers.GetLayerByName(TName);
//            Dataset pDataset = pGeoLayer.getDataset();
//
//            List<String> AllIDList = new ArrayList<String>();
//            AllIDList = idList.get(TName);
//
//            List<String> queryIDList = new ArrayList<String>();
//            for(String SYSID : AllIDList )
//            {
//            	Geometry pGeometry = pDataset.GetGeometry(Integer.parseInt(SYSID));
//	            if (pGeometry==null)
//	            {
//	            	queryIDList.add(SYSID);
//	            } else
//	            {
//	            	 pGeoLayer.getShowSelection().Add(pGeometry);
//	            }
//            }
//            String SQL = "select SYS_GEO,SYS_ID,'%3$s' as TName from %1$s where SYS_ID in (%2$s)";
//            SQL = String.format(SQL, TName + "_D", Tools.JoinT(",", queryIDList),TName);
//
//            SQLList.add(SQL);
//        }
//
//        int ReadObjectCount = 0;
//        if (SQLList.size() > 0)
//        {
//
//            QuerySQL = Tools.JoinT("\r\nunion all\r\n", SQLList);
//
//            //构造查询数据语句
//            SQLiteDataReader DR1 = pDataSource.Query(QuerySQL);
//            {
//                if (DR1 != null) while (DR1.Read())
//                    {
//                        byte[] bytex = (byte[])DR1.GetBlob("SYS_GEO");
//                        GeoLayer pGeoLayer = this._BKGeoLayers.GetLayerByName(DR1.GetString("TName"));
//                        Dataset pDataset = pGeoLayer.getDataset();
//                        Geometry pGeometry = Tools.ByteToGeometry(bytex, pDataset.getType(),false);
//                        pGeometry.setID(DR1.GetString("SYS_ID") + "");
//                        pDataset.AddGeometry(pGeometry);
//                        pGeoLayer.getShowSelection().Add(pGeometry);
//                        ReadObjectCount++;
//                    } DR.Close();
//            }
//        }
//
//
//
//        String T2 = "读取图形："+StaticObject.EndTime()+","+ReadObjectCount;
//
//
//
//        StaticObject.StartTime();
//
//        //快速显示
//        this.FastRefresh();
//
//        String T3 = "显示图形："+StaticObject.EndTime();
//
//        Paint _Font = new Paint();
//    	_Font.setAntiAlias(true);
//    	_Font.setTextSize(20);
//    	Typeface TF = Typeface.create("宋体", Typeface.NORMAL);
//    	_Font.setTypeface(TF);
//
//        this._Graphics.drawText(T1, 0, 200, _Font);
//        this._Graphics.drawText(T2, 0, 250, _Font);
//        this._Graphics.drawText(T3, 0, 300, _Font);
    }


    boolean LoadGoogleMap = false;    //表示是否已经加载了Tile图片，也就是否选中的Tile图片

    //快速刷新图层，没有选择过滤的过程
    public void FastRefresh()
    {
//        if(!PubVar.m_DoEvent.m_AuthorizeTools.m_AuthorizePass)
//        {
//            return;
//        }

        _Graphics.drawColor(Color.LTGRAY);

        this._OverMapLayer.FastRefresh();
        this._GridLayers.FastRefresh();
        this.FastRefreshForGeoLayers(this.getGeoLayers(lkGeoLayersType.enVectorBackground).getList());
        this.FastRefreshForGeoLayers(this.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList());

        //画OverLayer层
        this.getOverLayer().Refresh();

        new Canvas(this.MaskBitmap).drawBitmap(this.bp, 0,0, null);
        this._DrawPicture.invalidate();

//        Tools.UpdateShowSelectCount();
//        Tools.UpdateScaleBar();
    }



    private void FastRefreshForGeoLayers(List<GeoLayer> geoLayerList)
    {
        //重新画图层
        for (GeoLayer layer : geoLayerList)
        {
            //if (layer.getType() == lkGeoLayerType.enPolygon) continue;
            layer.FastRefresh();
        }

        //重新画图层中被选中的实体
        for (GeoLayer layer : geoLayerList)
        {
            layer.DrawSelection(layer.getSelSelection());
        }

        //重新画图层的标注信息
        for (GeoLayer layer : geoLayerList)
        {
            if (layer.getRender().getIfLabel())
            {
                layer.DrawSelectionLabel(layer.getShowSelection(), _Graphics,0,0);
            }
        }
        //重新画图层的标注信息（被选中部分）
        for (GeoLayer layer : geoLayerList)
        {
            if (layer.getRender().getIfLabel())
            {
                layer.DrawSelectionLabel(layer.getSelSelection(), _Graphics, 0,0);
            }
        }
    }


    //Map销毁
    public void Dispose()
    {
        if (this.bp!=null)bp.recycle();
        bp = null;
        if (this.MaskBitmap!=null)this.MaskBitmap.recycle();
        this.MaskBitmap=null;
        this._Graphics = null;
        System.gc();
    }
}
