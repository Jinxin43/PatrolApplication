package com.DingTu.Layer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Dataset.SQLiteDataReader;
import com.DingTu.Map.Map;
import com.DingTu.OverMap.OverMapSQLiteDataBase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Envelope;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class GridLayer {

    //动态筛选标识
    public String DynamicFilterStr = "";

    private Map Map = null;

    private String m_LayerId = "";
    public String GetLayerID(){return this.m_LayerId;};

    /**
     * 初始化GridLayer对象
     * @param _map
     */
    public GridLayer(Map _map)
    {
        this.m_LayerId = UUID.randomUUID().toString();
        this.Map = _map;
    }

    /**
     * 栅格数据源数据库类
     */
    private OverMapSQLiteDataBase _SQLiteDB = null;
    private OverMapSQLiteDataBase getSQLiteDB()
    {
        if (_SQLiteDB == null) _SQLiteDB = new OverMapSQLiteDataBase();
        return _SQLiteDB;
    }


    //是否加载栅格地图
    private boolean _IfLoadGrid = false;
    /**
     * 卸载栅格图
     */
    public void UnloadGird()
    {
        this._IfLoadGrid =  false;
        this._CacheList.clear();
        this._GridDataFileName = "";
    }
    private boolean _ShowGird = true;  //显示
    public boolean GetShowGird(){return this._ShowGird;}
    public void SetShowGrid(boolean visible)
    {
        this._ShowGird = visible;
        if (!this._ShowGird)this._CacheList.clear();
    }

    /**
     * 获取此栅格的最大范围
     * @return
     */
    public Envelope GetExtend()
    {
        if (this._IfLoadGrid)return this._GridPad.Extend;else return null;
    }


    private String _GridDataFileName = "";
    public String GetGridDataFile()
    {
        return this._GridDataFileName;
    }
    /**
     * 设置栅格数据源文件名称
     * @param gridFileName
     */
    public void SetGridDataFile(String gridFileName,String path)
    {
        String GridFileMainPath = PubVar.m_SysAbsolutePath+"/Map/";

        if(path != null && path.length()>0 && (!path.equals("null")))
        {
            GridFileMainPath = path;
            if(!GridFileMainPath.endsWith("/"))
            {
                GridFileMainPath+="/";
            }
        }

        if (!Tools.ExistFile(GridFileMainPath+gridFileName))
        {
            if(Tools.ExistFile(gridFileName))
            {
                int lastIndex = gridFileName.lastIndexOf("/");
                if(lastIndex>-1)
                {
                    this._GridDataFileName = gridFileName.substring(lastIndex,gridFileName.length()-1);
                }
                else
                {
                    this._GridDataFileName = gridFileName;
                }

                this.getSQLiteDB().setDatabaseName(gridFileName);
                this._IfLoadGrid = true;
            }
            else
            {
                //清空缓存
                this._IfLoadGrid = false;
                this._CacheList.clear();return;
            }
        }
        else
        {
            this._GridDataFileName = gridFileName;
            this.getSQLiteDB().setDatabaseName(GridFileMainPath+this._GridDataFileName);
            this._IfLoadGrid = true;
        }





        //清空缓存
        this._CacheList.clear();

        //栅格图的分级信息
        this._LevelScale.clear();
        SQLiteDataReader DR = this.getSQLiteDB().Query("select * from MapInfo");
        if (DR == null) return;
        while (DR.Read())
        {
            String Level = DR.GetString("MaxLevel");
            double Scale = DR.GetDouble("Scale");
            for(int i=1;i<=Integer.parseInt(Level);i++)
            {
                this._LevelScale.add(0,Scale*(Math.pow(2, i-1)));
            }

            this._GridPad.TileSize = Integer.parseInt(DR.GetString("TileSize"));
            this._GridPad.SetExtend(DR.GetDouble("Min_X"), DR.GetDouble("Min_Y"),
                    DR.GetDouble("Max_X"), DR.GetDouble("Max_Y"));

        }DR.Close();
    }


    //缩放级别比例尺，也就是每个像素代表的实际距离
    private List<Double> _LevelScale = new ArrayList<Double>();

    //栅格数据平面，主要用于计算需要显示的栅格切片
    private GridPad _GridPad = new GridPad();

    public void Refresh()
    {
        if (!this._ShowGird)return;  //不显示栅格图像
        if (!this._IfLoadGrid)return;  //没有加载栅格图像

        //获取当前视图下每个像素代表的高度值，也就是确定栅格图的级数
        double PerPixDistance = this.Map.ToMapDistance(1);
        int CurrentLevel = this.GetCurrentLevel(PerPixDistance);

        //获取当前视图下的最大外接矩形范围
        Envelope evp = this.Map.getExtend();

        //判断是否在当前视图范围内，如果不在则清空缓存
        if (!this._GridPad.InCurrentView(evp)){this._CacheList.clear();return;}

        //根据最大外接矩形范围及当前显示级数计算需要显示的小切片
        Log.d("Refresh Envelope","evp");
        String LT = this._GridPad.CalGridPosition(this._LevelScale.get(CurrentLevel),evp.getLeftTop());
        String RB = this._GridPad.CalGridPosition(this._LevelScale.get(CurrentLevel),evp.getRightBottom());
        Log.d("Refresh Envelope",LT+","+RB);
        //刷新ID
        String RefreshID = UUID.randomUUID().toString();

        //构造调用地图名称列表
        int StartX = Integer.parseInt(LT.split(",")[0]);
        int StartY = Integer.parseInt(LT.split(",")[1]);
        int EndX = Integer.parseInt(RB.split(",")[0]);
        int EndY = Integer.parseInt(RB.split(",")[1]);
        List<String> NameList = new ArrayList<String>();
        for (int i = StartX; i <= EndX; i++)
        {
            for (int j = StartY; j <= EndY; j++)
            {
                //判断是否在缓存中，如果在则不读取
                boolean InCache = false;
                for(Tile TL:this._CacheList)
                {
                    if (TL.TileName.equals(CurrentLevel+"-"+j + "-" + i)){TL.TileUniqueID=RefreshID;InCache=true;}
                }

                if (!InCache)NameList.add(j + "-" + i);

            }
        }

        //清理无用的缓存
        int CacheCount = this._CacheList.size()-1;
        for(int ci = CacheCount;ci>=0;ci--)
        {
            Tile TL = this._CacheList.get(ci);
            if (!TL.TileUniqueID.equals(RefreshID))this._CacheList.remove(TL);
        }

        //查询数据库已经存在哪些地图
        if (NameList.size()==0) return;
        String TableName = "L"+(CurrentLevel+1);
        Log.d("NameList",Tools.JoinT("','", NameList));
        String SQL = "select * from " + TableName + " where SYS_RC in ('" + Tools.JoinT("','", NameList) + "')";

        //读取数据库中的相应图片
        SQLiteDataReader DR = this.getSQLiteDB().Query(SQL);
        if (DR == null)
        {
            Log.d("SQLiteDataReader is null",SQL);
            return;
        }
        while (DR.Read())
        {
            String Name = DR.GetString("SYS_RC");
            byte[] ImageByte = DR.GetBlob("SYS_GEO");

            double LT_X = DR.GetDouble("LT_X");
            double LT_Y = DR.GetDouble("LT_Y");
            double RB_X = DR.GetDouble("RB_X");
            double RB_Y = DR.GetDouble("RB_Y");
            int isMask = 0;
            try
            {
//                isMask = DR.GetInt32("Mask");
            }
            catch(Exception ex)
            {
                isMask = 0;
            }
            //加入切片缓存内
            Tile TL = new Tile();
            TL.LT_X = LT_X;
            TL.LT_Y = LT_Y;
            TL.RB_X = RB_X;
            TL.RB_Y = RB_Y;
            TL.TileName = CurrentLevel+"-"+Name;
            Log.d("TileName",TL.TileName);


//	    	if(isMask == 1)
//	    	{
//	    		TL.TileBitmap = getTransparentBitmap(BitmapFactory.decodeByteArray(ImageByte, 0, ImageByte.length));
//	    	}
//	    	else
//	    	{
//	    		TL.TileBitmap = BitmapFactory.decodeByteArray(ImageByte, 0, ImageByte.length);
//	    	}

            try
            {
                TL.TileBitmap = BitmapFactory.decodeByteArray(ImageByte, 0, ImageByte.length);
                this._CacheList.add(TL);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }


        }DR.Close();

    }

    public  Bitmap getTransparentBitmap(Bitmap sourceImg)
    {

        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        Bitmap b = sourceImg.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);
//
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

//
//        int number =0;

        for (int i = 0; i < argb.length; i++)
        {
//        	//argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
////        	if(Color.red(argb[i])<=20)
////        	{
////        		if(Color.green(argb[i]) <= 20)
////        		{
////        			if(Color.blue(argb[i]) <= 20)
////        			{
////        				argb[i] = 0;
////        			}
////        		}
////        	}
            if(Color.WHITE == argb[i])
            {
                argb[i] = 0;
            }

        }

        b.setPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());

        return b;
    }

    //切片缓存
    private List<Tile> _CacheList = new ArrayList<Tile>();
    public void ClearAllCache()
    {
        this._CacheList.clear();
    }

    //透明度
    private int m_Transparent = 0;
    public int GetTransparet(){return this.m_Transparent;}
    /**
     * 设置透明度
     * @param transparent
     */
    public void SetTransparent(int transparent)
    {
        this.m_Transparent = transparent;
    }
    /**
     * 快速刷新，没有选择的过程 ，直接利用缓存
     */
    public void FastRefresh()
    {
        if (!this._ShowGird) return;
        Paint paint = new Paint();
        paint.setAlpha(255-this.m_Transparent); //设置透明程度
        paint.setAntiAlias(true);
        for(Tile TL:this._CacheList)
        {
            PointF LTP = this.Map.getViewConvert().MapToScreenF(TL.LT_X,TL.LT_Y);
            PointF RBP = this.Map.getViewConvert().MapToScreenF(TL.RB_X,TL.RB_Y);
            this.Map.getDisplayGraphic().drawBitmap(TL.TileBitmap,new Rect(0,0,TL.TileBitmap.getWidth(),TL.TileBitmap.getHeight()),
                    new RectF(LTP.x,LTP.y,RBP.x,RBP.y),paint);
        }
    }

    /**
     * 根据当前的视图比例，计算适应的栅格级数
     * @param PerPixDistance
     * @return
     */
    private int GetCurrentLevel(double PerPixDistance)
    {
        double MinD = Double.MAX_VALUE;
        int level = -1;
        for(int Level = 0;Level<_LevelScale.size();Level++)
        {
            double D = Math.abs(_LevelScale.get(Level) - PerPixDistance);
            if (D < MinD){level = Level;MinD = D;}
        }
        return level;
    }


    //用于计算当前视图范围所包含的切片
    private class GridPad
    {
        //最大范围
        private double MinX,MinY,MaxX,MaxY;
        public Envelope Extend;
        public void SetExtend(double MinX,double MinY,double MaxX,double MaxY)
        {
            this.MinX = MinX;this.MinY = MinY;this.MaxX = MaxX;this.MaxY = MaxY;
            this.Extend = new Envelope(MinX,MaxY,MaxX,MinY);
        }
        //小切片的大小
        public int TileSize = 256;

        /**
         * 根据比例计算坐标所处的位置，也是行与列值
         * @param Scale 每个像素代表的实际距离值
         * @param ViewPT
         * @return
         */
        public String CalGridPosition(double Scale, Coordinate ViewPT)
        {
            double TileSizeT = this.TileSize * Scale;

            int MaxRow = (int)((MaxX - MinX) / TileSizeT)+1;
            int MaxCol = (int)((MaxY - MinY) / TileSizeT)+1;

            int Row = (int)((ViewPT.getX() - MinX) / TileSizeT);
            int Col = (int)((MaxY - ViewPT.getY()) / TileSizeT);

            if (Row<0)Row=0;if (Col<0)Col=0;
            if (Row>MaxRow)Row=MaxRow;if (Col>MaxCol)Col = MaxCol;

            return Row+","+Col;
        }

        //是否与当前视口有交点
        public boolean InCurrentView(Envelope evp)
        {
            return evp.Intersect(this.Extend);
        }
    }

    //用于缓冲用的切片类
    private class Tile
    {
        //切片的真实坐标
        public double LT_X = 0;
        public double LT_Y = 0;
        public double RB_X = 0;
        public double RB_Y = 0;

        public String TileName = "";  //格式1-Row-Col，也就是级数-行-列
        public String TileUniqueID = "";   //通过此ID清除无用的切片缓存
        public Bitmap TileBitmap = null;
    }
}
