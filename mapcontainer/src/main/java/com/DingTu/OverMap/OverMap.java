package com.DingTu.OverMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.Toast;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.CoordinateSystem.Project_Web;
import com.DingTu.Dataset.SQLiteDataReader;
import com.DingTu.Enum.lkOverMapType;
import com.DingTu.Map.Map;
import com.DingTu.Map.Param;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class OverMap {

    //Map对象
    private Map Map = null;

    /**
     * 初始化OverMap对象
     * @param _map
     */
    public OverMap(Map _map)
    {
        this.Map = _map;
        this.SetOverMapType(lkOverMapType.enGoogle_Satellite);
    }

    //叠加地图的类型（谷歌，天地图）
    private lkOverMapType _OverMapType = lkOverMapType.enGoogle_Satellite;
    /**
     * 设置叠加地图的类型
     * @param overmapType
     */
    public void SetOverMapType(lkOverMapType overmapType)
    {
        this._OverMapType = overmapType;
        if (this._OverMapType==lkOverMapType.enGoogle_Satellite ||
                this._OverMapType==lkOverMapType.enGoogle_Terrain ||
                this._OverMapType==lkOverMapType.enGoogle_Street)
        {
            //初始化Google地图的缩放级别比例尺  ,每个象像代表的距离
            for(int i=1;i<=20;i++)
            {
                _LevelScale[i] = ((2 * Math.PI * 6378137) / (Math.pow(2, i)))/256;
            }

            if (this._OverMapType==lkOverMapType.enGoogle_Satellite)this._TableName = "g_Sat";
            if (this._OverMapType==lkOverMapType.enGoogle_Terrain)this._TableName = "g_Ter";
            if (this._OverMapType==lkOverMapType.enGoogle_Street)this._TableName = "g_Str";
        }
    }

    /*
     * 根据地图类型的不同，对应的数据表名，
     * 格式：卫星影像=Sat，如谷歌影像表为g_Sat
     *       地图=Ter，如谷歌地形表为g_Ter
     */
    private String _TableName = "";

    //地图的缩放级别比例尺
    private double[] _LevelScale = new double[21];
    private double[] getLevelScale(){return _LevelScale;}

    //缓存地图数据库类
    private OverMapSQLiteDataBase _SQLiteDB = null;
    private OverMapSQLiteDataBase getSQLiteDB()
    {
        if (_SQLiteDB == null) _SQLiteDB = new OverMapSQLiteDataBase();
        return _SQLiteDB;
    }

    //地图下载器
    private OverMapDownloader _OverMapDownloader = null;
    private OverMapDownloader GetOverMapDownloader()
    {
        if (_OverMapDownloader == null) _OverMapDownloader = new OverMapDownloader();
        _OverMapDownloader.setBindGoogleMap(this);
        return _OverMapDownloader;
    }

    public boolean _PanRefresh = false;     //此次刷新操作是否由移屏引起的

    //是否加载栅格地图
    private boolean _ShowGird = false;  //显示
    public void SetShowGrid(boolean visible)
    {
        this._ShowGird = visible;
    }

    private int OffsetX = 0;
    private int OffsetY = 0;

    /**
     * 本地叠加地图的位置，也是在线浏览时的地图缓存位置
     * @return
     */
    public String GetOverMapPath()
    {
        String CachePath = PubVar.m_SysAbsolutePath+"/Map/OverMap";
        File file = new File(CachePath);
        if (!file.exists())file.mkdir();
        return CachePath;
    }


    /// <summary>
    /// 根据当前Map显示比例确定Google的缩放级数
    /// </summary>
    /// <param name="Zoom"></param>
    /// <returns></returns>
//    private int GetGoogleLevel(double Zoom)
//    {
//        return this.GetGoogleLevel(Zoom, !this.getZoomOnScale());
//    }

    /**
     * 根据当前的视图比例，计算适应的栅格级数
     * @param PerPixDistance
     * @return
     */
    private int GetCurrentLevel(double PerPixDistance)
    {
        double MinD = Double.MAX_VALUE;
        int level = -1;
        for(int Level = 1;Level<this._LevelScale.length;Level++)
        {
            double D = Math.abs(this._LevelScale[Level] - PerPixDistance);
            if (D < MinD){level = Level;MinD = D;}
        }
        return level;
    }


////    /// <summary>
////    /// 根据当前视图的显示范围，自动调整显示级别
////    /// </summary>
//    private int _CurrentLevel = -1;
////    public void SetCurrentLevel()
////    {
////        int Level = this.GetGoogleLevel(this.Map.getViewConvert().getZoom(), true);
////        if (Level != -1) this.SetLevel(Level);
////    }
//
//    /// <summary>
//    /// 设置当前视图的显示级别
//    /// </summary>
//    /// <param name="Level"></param>
//    public void SetLevel(int Level)
//    {
//    	if (Level>=_LevelScale.length-1)Level = _LevelScale.length-1;
//    	if (Level<=1) Level=1;
//    	_CurrentLevel = Level;
//        this.Map.setExtend(this.GetEnveForLevel(Level));
//        this.Map.Refresh();
//        //System.Windows.Forms.MessageBox.Show(Level.ToString() + "," + this.Map.ViewConvert.ZoomScale.ToString());
//    }
//
//    //根据指定级数，返回Map的外接矩形
//    private Envelope GetEnveForLevel(int Level)
//    {
//        double W = this.getLevelScale()[Level] * this.Map.getSize().getHeight();
//        return new Envelope(
//            this.Map.getExtend().getCenter().getX() - W / 2,
//            this.Map.getExtend().getCenter().getY() + W / 2,
//            this.Map.getExtend().getCenter().getX() + W / 2,
//            this.Map.getExtend().getCenter().getY() - W / 2);
//    }

    /**
     * 刷新地图
     * @return
     */
    public boolean Refresh()
    {

        if (this._OverMapType==lkOverMapType.enUnknow) return false;
        boolean PanResult = this._PanRefresh;this._PanRefresh=false;

        if (!this._ShowGird) return false;

        boolean HaveLoadMap = false;

        //加载google地图
        try
        {
            if (_TileScaleCanvas!=null) _TileScaleCanvas.drawColor(Color.WHITE);

            //获取当前视图下每个像素代表的高度值，也就是确定栅格图的级数
            int Level = this.GetCurrentLevel(this.Map.ToMapDistance(1));

            //Envelope _NextEnve = null;

            //当前屏幕的左上角，右下角坐标
            Coordinate LeftTop,RightBottom;
            LeftTop = this.Map.getExtend().getLeftTop();
            RightBottom = this.Map.getExtend().getRightBottom();
//            if (PanResult)
//            {
//            	if (this._CurrentLevel==-1){lkmap.Tools.Tools.ShowMessageBox("移屏-1"); return false;}
//            	Level = this._CurrentLevel;
//            	//_NextEnve = this.GetEnveForLevel(Level);
//                LeftTop = this.Map.getExtend().getLeftTop();
//                RightBottom = this.Map.getExtend().getRightBottom();
//            }
//            else
//            {
//            	if (Level==-1) {lkmap.Tools.Tools.ShowMessageBox("缩放-1"); return false;}
//                LeftTop = this.Map.getExtend().getLeftTop();
//                RightBottom = this.Map.getExtend().getRightBottom();
//            }



            double OffsetX = 0, OffsetY = 0;
            // if (this.getGoogleMapType() == 1) { OffsetX = this.OffsetX; OffsetY = this.OffsetY; }

            //计算左上角，右下角坐标所在的小切片位置，也就是行列值
            double LTX = LeftTop.getX() + OffsetX;
            double LTY = LeftTop.getY() + OffsetY;
            double RBX = RightBottom.getX() + OffsetX;
            double RBY = RightBottom.getY() + OffsetY;

            Param StartX=new Param(), StartY=new Param();
            Coordinate LT = Project_Web.Web_XYToBL(LTX, LTY);
            GoogleMapAPI.GetTileXY(LT.getX(), LT.getY(), Level, StartX, StartY);

            Param EndX=new Param(), EndY=new Param();
            Coordinate RB = Project_Web.Web_XYToBL(RBX, RBY);
            GoogleMapAPI.GetTileXY(RB.getX(), RB.getY(), Level, EndX, EndY);

            //构造调用小切片名称列表
            List<String> TileNameList = new ArrayList<String>();
            List<OverMapTile> TileList = new ArrayList<OverMapTile>();
            for (int i = StartX.getInt(); i <= EndX.getInt(); i++)
            {
                for (int j = StartY.getInt(); j <= EndY.getInt(); j++)
                {
                    String TileName = String.valueOf(i) + "@" + String.valueOf(j) + "@" + String.valueOf(Level);
                    OverMapTile omt = new OverMapTile();
                    omt.SetTileName(TileName, this._TableName);
                    TileList.add(omt);TileNameList.add(TileName);
                }
            }

            //查询数据库已经存在哪些地图

            String SQL = "select * from %1$s where Name in (%2$s)";
            SQL = String.format(SQL, this._TableName,"'"+Tools.JoinT("','", TileNameList)+"'");

            //判断需要打开哪个图片数据库
            String DatabaseName = this.GetOverMapPath() + "/MapBase" + String.valueOf(Level) + ".dbx";
            if (this.getSQLiteDB().getDatabaseName() != DatabaseName) this.getSQLiteDB().setDatabaseName(DatabaseName);

            //读取数据库中的相应图片
            SQLiteDataReader DR = this.getSQLiteDB().Query(SQL);
            if (DR != null){while (DR.Read())
            {
                String Name = DR.GetString(0);
                byte[] ImageByte = (byte[])(DR.GetBlob(1));
                for(OverMapTile omt:TileList)
                {
                    if (omt.GetTileName().equals(Name))
                    {
                        if (this.ShowImage(omt, ImageByte)) HaveLoadMap = true;
                        TileList.remove(omt);break;
                    }
                }
            } DR.Close();}


            //哪些没有命中
            for(OverMapTile tile:TileList)
            {
                //在二级缓存中查找是否存在，二级缓存也就是PNG文件的形式
                String cheFileName = this.GetOverMapPath()+"/"+tile.GetTileName()+".png";
                if (Tools.ExistFile(cheFileName))
                {
                    this.ShowImage(tile, Tools.readStream(cheFileName));
                    TileList.remove(tile);continue;
                }

                //下载缓存中没有的
                //"x=" + Row + "&y=" + Col + "&z=" + Level;
//            	String s = "Galileo".substring(0, ((3 * col + row) % 8));
//            	String url = "http://mt"+(col%4)+".google.cn/vt/lyrs=s&"+"x=" + col + "&y=" + row + "&z=" + level+"&s=" + s;
                if (this._OverMapType==lkOverMapType.enGoogle_Satellite ||
                        this._OverMapType==lkOverMapType.enGoogle_Terrain ||
                        this._OverMapType==lkOverMapType.enGoogle_Street)
                    tile.Url = GoogleMapAPI.CreateTileUrl(this._OverMapType, tile.Row, tile.Col, tile.Level);
                tile.CachePath = this.GetOverMapPath();
            }

            //开始启动地图下载器
            this.GetOverMapDownloader().setUpLoadFileList(TileList);
            this.GetOverMapDownloader().StartUpLoad();

        }
        finally
        {
            //this.Map.DrawPicture.BackColor = OriColor;

        }
        return HaveLoadMap;
    }

    private Bitmap _TileScaleBitmap = null;
    private Canvas _TileScaleCanvas = null;
    public void FastRefresh()
    {
        if (this._OverMapType==lkOverMapType.enUnknow) return;
        if (!this._ShowGird) return;
        if (_TileScaleBitmap==null) return;
        //this.Map.getDisplayGraphic().drawBitmap(_TileScaleBitmap, 0, 0, null);
        float MoveX = _TileScaleBitmap.getWidth()/2f;
        float MoveY = _TileScaleBitmap.getHeight()/2f;
// 	   	this.Map.getDisplayGraphic().drawBitmap(_TileScaleBitmap,new Rect(0,0,_TileScaleBitmap.getWidth(),_TileScaleBitmap.getHeight()),
// 			   													new RectF(-MoveX,-MoveY,_TileScaleBitmap.getWidth()*2-MoveX,_TileScaleBitmap.getHeight()*2-MoveY),null);
//
        this.Map.getDisplayGraphic().drawBitmap(_TileScaleBitmap,new Rect(0,0,_TileScaleBitmap.getWidth(),_TileScaleBitmap.getHeight()),
                new RectF(0,0,_TileScaleBitmap.getWidth(),_TileScaleBitmap.getHeight()),null);

    }
    //显示指定的图片
    public boolean ShowImage(OverMapTile tile,byte[] ImageByte)
    {
        if (_TileScaleBitmap==null)
        {
            //当Map尺寸发生变化时要重新创建Image对象，以适应MapControl控件的大小
            _TileScaleBitmap = Bitmap.createBitmap(this.Map.getSize().getWidth(),
                    this.Map.getSize().getHeight(),
                    Bitmap.Config.ARGB_8888);
            _TileScaleCanvas = new Canvas(_TileScaleBitmap);
        }
        //提取正在加载的图片信息
        int OffsetX = 0, OffsetY = 0;
        // if (this.getGoogleMapType() == 1) { OffsetX = this.OffsetX; OffsetY = this.OffsetY; }

        Param JD1=new Param(), WD1=new Param();
        Param JD2=new Param(), WD2=new Param();;
        GoogleMapAPI.GetTileLL(tile.Col, tile.Row, tile.Level, JD1, WD1);
        GoogleMapAPI.GetTileLL(tile.Col + 1, tile.Row + 1, tile.Level, JD2, WD2);


        Coordinate CoorLT = Project_Web.Web_BLToXY(JD1.getDouble(), WD1.getDouble());
        Coordinate CoorRB = Project_Web.Web_BLToXY(JD2.getDouble(), WD2.getDouble());


        //X1 -= OffsetX; X4 -= OffsetX; Y1 -= OffsetY; Y4 -= OffsetY;

        //Coor1.setX(Coor1.getX()-OffsetX);Coor1.setY(Coor1.getY()-OffsetY);
        Point ptLT = this.Map.getViewConvert().MapToScreen(CoorLT);
        Point ptRB = this.Map.getViewConvert().MapToScreen(CoorRB);
        //this.Map.ViewConvert.MapToScreen(X4, Y4, out RBX, out RBY);

        try
        {
            Bitmap bp = BitmapFactory.decodeByteArray(ImageByte, 0, ImageByte.length);
            //this.Map.getDisplayGraphic().drawBitmap(bp, PT.x, PT.y, null);
            // _TileScaleCanvas.drawBitmap(bp, PT.x, PT.y, null);

            _TileScaleCanvas.drawBitmap(bp, new Rect(0,0,bp.getWidth(),bp.getHeight()),new Rect(ptLT.x,ptLT.y,ptRB.x,ptRB.y), null);


            //this.Map.getDisplayGraphic().drawBitmap(bp,new Rect(0,0,256,256),new Rect(PT.x,PT.y,PT.x+512,PT.y+512),null);
//           using (MemoryStream ms = new MemoryStream(ImageByte))
//           {
//               using (Image pImage = new Bitmap(ms) as Image)
//               {
//                   Rectangle destRect = new Rectangle(LTX, LTY, RBX - LTX, RBY - LTY);
//                   //Rectangle soucRect = new Rectangle(0,0,256,256);
//                   ImageAttributes IA = new ImageAttributes();
//                   Color pColor = Color.FromArgb(255, 255, 255);
//                   IA.SetColorKey(pColor, pColor);
//                   this.Map.DisplayGraphic.DrawImage(pImage, destRect, 0, 0, 256, 256, GraphicsUnit.Pixel, IA);
//               }
//           }
        }
        catch(NullPointerException e)
        {
            if (_TileScaleCanvas==null) _TileScaleBitmap = null;

            return false;
        }
        return true;

    }

    //销毁
    public void Dispose()
    {
        if (_TileScaleBitmap!=null)
            _TileScaleBitmap.recycle();_TileScaleBitmap=null;
        _TileScaleCanvas = null;System.gc();

    }
}
