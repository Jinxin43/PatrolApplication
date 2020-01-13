package com.DingTu.Layer;

import android.graphics.Canvas;

import com.DingTu.Base.Tools;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Dataset.Selection;
import com.DingTu.Enum.lkDrawType;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkGeometryStatus;
import com.DingTu.Enum.lkSelectionType;
import com.DingTu.Map.Map;
import com.DingTu.Render.IRender;

import com.DingTu.Cargeometry.Geometry;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class GeoLayer {

    //Map对象
    private Map _Map = null;
    public Map getMap()
    {
        return _Map;
    }

    // 返回\设置图层的类型，lkGeoLayerType常量
    private lkGeoLayerType _Type = lkGeoLayerType.enUnknow;
    public lkGeoLayerType getType()
    {
        return _Type;
    }
    public void setType(lkGeoLayerType value)
    {
        _Type = value;
    }

    //图层别名，一般为汉字
    private String _AliasName = "";
    public String GetAliasName()
    {
        return this._AliasName;
    }
    public void SetAliasName(String _AliasName)
    {
        this._AliasName = _AliasName;
    }

    // 返回图层名称
    private String _Id = "";
    public String getId() {return _Id; }
    public void setId(String id) {_Id = id;}

    //图层最小显示比例
    private double _VisibleScaleMin = 0;
    public double getVisibleScaleMin()
    {
        return _VisibleScaleMin;
    }
    public void setVisibleScaleMin(double value)
    {
        _VisibleScaleMin = value;
    }

    //图层最大显示比例
    private double _VisibleScaleMax = Double.MAX_VALUE;
    public double getVisibleScaleMax()
    {
        return _VisibleScaleMax;
    }
    public void setVisibleScaleMax(double value)
    {
        _VisibleScaleMax = value;
    }

    //图层可选择性
    private boolean _Selectable = true;
    public boolean getSelectable()
    {
        return _Selectable;
    }
    public void setSelectable(boolean value)
    {
        _Selectable = value;
    }

    //图层可编辑性
    private boolean _Editable = true;
    public boolean getEditable()
    {
        return _Editable;
    }
    public void setEditable(boolean value)
    {
        _Editable=value;
    }

    //图层可捕捉性
    private boolean _Snapable = true;
    public boolean getSnapable()
    {
        return _Snapable;
    }
    public void setSnapable(boolean value)
    {
        _Snapable=value;
    }

    //图层可见性
    private boolean _Visible = true;
    public boolean getVisible()
    {
        return _Visible;
    }
    public void setVisible(boolean value)
    {
        _Visible = value;
    }

    //得到图层的符号化信息
    private IRender _Render = null;
    public IRender getRender()
    {
        return _Render;
    }
    public void setRender(IRender value)
    {
        _Render = value;
    }


    //选择(数据)集成员变量
    //显示过滤条件，通过此条件可以屏蔽不需要显示的实体
    private String _DisplayFilter = "";
    public String getDispplayFilter()
    {
        return _DisplayFilter;
    }

    //当前图层中正在被显示的选择集
    private Selection _ShowSelection = new Selection();
    public Selection getShowSelection()
    {
        return _ShowSelection;
    }

    //当前图层中正在被选择的选择集
    private Selection _SelSelection = new Selection();
    public Selection getSelSelection()
    {
        return _SelSelection;
    }

    //图层实体数据集
    private Dataset _Dataset = null;
    public Dataset getDataset()
    {
        return _Dataset;
    }
    public void setDataset(Dataset value)
    {
        _Dataset = value;
        _SelSelection.setDataset(_Dataset);
        _ShowSelection.setDataset(_Dataset);
        _Dataset.setBindGeoLayer(this);
    }


    public GeoLayer(Map map)
    {
        this._Map = map;
        this._ShowSelection.setType(lkSelectionType.enShow);
        this._SelSelection.setType(lkSelectionType.enSelect);
    }

    //图层刷新
    public void Refresh()
    {
        //开始查询
        //this._Dataset.QueryByExtend(this._Map.getExtend(),this._ShowSelection);
    }

    public void FastRefresh()
    {
        //绘制选择集
        this.DrawSelection(this._ShowSelection);
    }

    //绘制选择集
    public void DrawSelection(Selection pSelection)
    {
        this.DrawSelection(pSelection,this._Map.getDisplayGraphic(),0,0);
    }

    /**
     * 绘制选择集合
     * @param pSelection  集合
     * @param g 画布
     * @param OffsetX 偏移量（象素）
     * @param OffsetY 偏移量（象素）
     */
    public void DrawSelection(Selection pSelection, Canvas g, int OffsetX, int OffsetY)
    {
        Geometry pGeometry = null;
        for (int Index : pSelection.getGeometryIndexList())
        {
            pGeometry = this._Dataset.GetGeometry(Index);
            if (pGeometry == null) continue;
            if (pGeometry.getStatus() == lkGeometryStatus.enDelete) continue;   //打上删除标识的不显示

            //对于显示集（ShowSelection)，如果此实体又在enSelect选择集中，则不显示，放在enSelect中显示
            if (pSelection.getType() == lkSelectionType.enShow)
            {
                if (this._SelSelection.getGeometryIndexList().indexOf(Index) >= 0) continue;
                if (pGeometry.getSymbol()==null)
                {
                    Tools.ShowMessageBox(this.GetAliasName());
                    return;
                } else
                {
                    pGeometry.getSymbol().Draw(this._Map, g, pGeometry, OffsetX, OffsetY, lkDrawType.enNormal);
                }
            }

            //画当前选中的集合
            if (pSelection.getType() == lkSelectionType.enSelect)
            {
                //过滤选择集的显示部分，在屏幕视图内显示
                if (!this.getMap().getExtend().Intersect(pGeometry.getEnvelope())) continue;

                if (this.getDataset().getDataSource().getEditing())
                {
                    pGeometry.getSymbol().Draw(this._Map, g, pGeometry, OffsetX, OffsetY, lkDrawType.enSelected_Editing);
                }
                else
                {
                    pGeometry.getSymbol().Draw(this._Map, g, pGeometry, OffsetX, OffsetY, lkDrawType.enSelected_NoEditing);
                }
            }

        }
    }

    //画标标注，分开单写的目的是防止标注信息被其它层覆盖
    public void DrawSelectionLabel(Selection pSelection, Canvas g, int OffsetX, int OffsetY)
    {
        Geometry pGeometry = null;
        for (int Index : pSelection.getGeometryIndexList())
        {
            pGeometry = this._Dataset.GetGeometry(Index);
            if (pGeometry == null) continue;
            if (pGeometry.getStatus() == lkGeometryStatus.enDelete) continue;
            //过滤选择集的显示部分，也就是在ShowSelection中的显示，不在ShowSelection中就是不需要在屏幕上显示的部分
            //if (this.ShowSelection.GeometryIndexList.IndexOf(Index) < 0) continue;
            //if (this.SelSelection.Type!= lkSelectionType.enSelect)
            //{
            //   if (this.SelSelection.GeometryIndexList.IndexOf(Index) >= 0) continue;
            //}
            if (pGeometry.getSymbol()==null){
                Tools.ShowMessageBox(pGeometry.getTag());return;}
            pGeometry.getSymbol().DrawLabel(this._Map, g, pGeometry, OffsetX, OffsetY, pSelection.getType());
        }
    }

    //#region 图层渲染

    ///// <summary>画实体，也就是图层渲染。分两种情况画目的是被选中的实体在最上面
    ///// </summary>
    ///// <param name="RenderObjectType">(1-可显示但未选中实体，2-选中实体)</param>
    //public void Render(int RenderObjectType)
    //{
    //    if (RenderObjectType == 1)  //1-可显示但未选中实体
    //    {
    //        foreach (int index in this.ShowObjectList)
    //        {
    //            if (this.SelectObjectList.IndexOf(index) >= 0) continue;    //目的：选中的实体不显示
    //            if (this.LayerType == 0) this.DrawPoint(index, 1);          //画点
    //            if (this.LayerType == 1) this.DrawPolyline(index, 1);       //画线
    //            if (this.LayerType == 2) this.DrawPolygon(index, 1);        //画面
    //        }
    //    }

    //    if (RenderObjectType == 2)  //2-选中实体
    //    {
    //        foreach (int index in this.SelectObjectList)
    //        {
    //            if (this.LayerType == 0) { this.DrawPoint(index, 1); this.DrawPoint(index, 2); }         //画点
    //            if (this.LayerType == 1) { this.DrawPolyline(index, 1); this.DrawPolyline(index, 2); }   //画线
    //            if (this.LayerType == 2) { this.DrawPolygon(index, 1); this.DrawPolygon(index, 2); }       //画面
    //        }
    //    }
    //}


    //#region 渲染点层
    ///// <summary>画点实体
    ///// </summary>
    ///// <param name="ObjectIndex">实体索引</param>
    ///// <param name="type">类型，1-正常，2-选中</param>
    //private void DrawPoint(int ObjectIndex, int type)
    //{
    //    this.DrawPoint(Map.DisplayGraphic,ObjectIndex, type, 0f, 0f);
    //}
    //public void DrawPoint(Graphics g,int ObjectIndex, int type /*表示类型: 1-正常 2-选中*/, float OffsetX, float OffsetY)
    //{
    //    //需要重画的实体
    //    CartoGeometry.IGeometry pGeometry = this.GetObject(ObjectIndex);
    //    if (pGeometry.LKStatusMode == lkGeometryStatus.Delete) return;
    //    //将平面坐标转换成象素坐标
    //    float PointX = 0; float PointY = 0;
    //    this.Map.MapToScreen(pGeometry.Items[0], out PointX, out PointY);

    //    //得到图层的渲染器
    //    PointRender PR = (PointRender)this.LayerRender;
    //    PR.Draw(this, pGeometry, g, PointX, PointY,OffsetX,OffsetY, type);
    //}
    //#endregion

    //#region 渲染线层

    ///// <summary>画线实体
    ///// </summary>
    ///// <param name="ObjectIndex">实体索引</param>
    ///// <param name="type">类型，1-正常，2-选中</param>
    //private void DrawPolyline(int ObjectIndex, int type)
    //{
    //    this.DrawPolyline(Map.DisplayGraphic,ObjectIndex, type, 0f, 0f);
    //}
    //public void DrawPolyline(Graphics g,int ObjectIndex, int type, float OffsetX, float OffsetY)
    //{
    //    //得到线实体
    //    CartoGeometry.IGeometry pGeometry = this.GetObject(ObjectIndex);
    //    if (pGeometry.LKStatusMode == lkGeometryStatus.Delete) return;

    //    //得取渲染器
    //    LineRender LR = (LineRender)this.LayerRender;

    //    //平面坐标转换成象素坐标
    //    System.Drawing.PointF[] OPF = this.Map.MapPointsToScreePoints(pGeometry.Items, OffsetX, OffsetY);

    //    //利用图层渲染器进行画图
    //    LR.Draw(this,pGeometry,g,OPF,type);
    //}

    //#endregion

    //#region 渲染面层
    ///// <summary>画面实体
    ///// </summary>
    ///// <param name="ObjectIndex">实体索引</param>
    ///// <param name="type">类型，1-正常，2-选中</param>
    //private void DrawPolygon(int ObjectIndex, int type)
    //{
    //    this.DrawPolygon(Map.DisplayGraphic,ObjectIndex, type, 0f, 0f);
    //}
    //public void DrawPolygon(Graphics g,int ObjectIndex, int type, float OffsetX, float OffsetY)
    //{
    //    //得到实体
    //    CartoGeometry.IGeometry pGeometry = this.GetObject(ObjectIndex);

    //    //得取渲染器
    //    PolyRender PRender = (PolyRender)this.LayerRender;
    //    //System.Drawing.PointF[] OPF = new System.Drawing.PointF();// this.Map.ClipPolygon(pGeometry.CoorList, OffsetX, OffsetY);
    //    PRender.Draw(this, pGeometry, g, type);
    //}
    //#endregion

    //#endregion

    //#region 实体选择
    ///// <summary>选择实体（点选）
    ///// </summary>
    //public bool SelectAtPoint(Coordinate SelPoint, double SelectDistance)
    //{
    //    if (!this.Queryed) return false;
    //    //转换选择距离
    //    double Tolerance = Map.ToMapDistance(SelectDistance);

    //    #region  点层，计算两点之间的距离，在选择距离内，则属于选中
    //    if (this.LayerType == 0)
    //    {
    //        LKMap.CartoGeometry.Point StPoint;
    //        for (int i = 0; i <= _ShowObjectList.Count - 1; i++)
    //        {
    //            int ObjectIndex = (int)_ShowObjectList[i];
    //            StPoint = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Point;
    //            if (StPoint.LKStatusMode == lkGeometryStatus.Delete) continue;
    //            double ResDistance = StPoint.DistanceToPoint(SelPoint);
    //            if (ResDistance <= Tolerance)
    //            {
    //                _SelectObjectList.Add(ObjectIndex);
    //                return true;
    //            }
    //        }
    //    }
    //    #endregion

    //    #region 线层，计算选取点到直线的距离，在选择距离内，则属于选中
    //    if (this.LayerType == 1)
    //    {

    //        LKMap.CartoGeometry.Polyline StPolyline;
    //        foreach (int ObjIndex in ShowObjectList)
    //        {
    //            StPolyline = (Polyline)this.GetObject(ObjIndex);
    //            if (StPolyline.LKStatusMode == lkGeometryStatus.Delete) continue;
    //            if (StPolyline.Select(SelPoint, Tolerance))
    //            {
    //                this.SelectObjectList.Add(ObjIndex);
    //                //选中一个实体就可以返回
    //                return true;
    //            }
    //        }
    //    }
    //    #endregion

    //    #region 面层，计算选取点是否在面内，如果在，则属于选中
    //    if (this.LayerType == 2)
    //    {
    //        LKMap.CartoGeometry.Polygon StPolygon;
    //        for (int i = 0; i <= _ShowObjectList.Count - 1; i++)
    //        {
    //            int ObjectIndex = (int)_ShowObjectList[i];
    //            StPolygon = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Polygon;
    //            if (StPolygon.LKStatusMode == lkGeometryStatus.Delete) continue;
    //            bool PtIn = StPolygon.PointIn(SelPoint);
    //            if (PtIn)
    //            {
    //                _SelectObjectList.Add(ObjectIndex);
    //                //选中一个实体就可以返回
    //                return true;
    //            }
    //        }
    //    }
    //    #endregion
    //    return false;
    //}

    ///// <summary>选择实体（框选）
    ///// </summary>
    //public void SelectAtRect(Envelope SelRect)
    //{
    //    LKMap.CartoGeometry.Polygon StPolygon;
    //    LKMap.CartoGeometry.Polyline StPolyline;
    //    LKMap.CartoGeometry.Point StPoint;
    //    if (!this.Queryed) return;
    //    for (int i = 0; i <= _ShowObjectList.Count - 1; i++)
    //    {
    //        int ObjectIndex = (int)_ShowObjectList[i];

    //        #region 点层，在指定矩形范围内则为选中
    //        if (this.LayerType == 0)
    //        {
    //            StPoint = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Point;
    //            if (StPoint.LKStatusMode == lkGeometryStatus.Delete) continue;
    //            if (StPoint.InRect(SelRect))
    //            {
    //                //if (StPoint.Draw((Layer)this, _Map)) _SelectObjectList.Add(ObjectIndex);
    //                _SelectObjectList.Add(ObjectIndex);
    //            }
    //        }
    //        #endregion

    //        #region 线层，有两种情况 1）正向矩形：在指定矩形范围内则为选中 2）反向矩形：搭边上就算

    //        if (this.LayerType == 1)
    //        {
    //            StPolyline = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Polyline;
    //            if (StPolyline.LKStatusMode == lkGeometryStatus.Delete) continue;
    //            if (SelRect.EnvelopeType)    //正向矩形
    //            {
    //                if (StPolyline.InRect(SelRect))
    //                {
    //                    //if (StPolyline.Draw((Layer)this, _Map)) _SelectObjectList.Add(ObjectIndex);
    //                    _SelectObjectList.Add(ObjectIndex);
    //                }
    //            }
    //            else    //反向矩形 搭边就选中
    //            {

    //                if (StPolyline.IntersectRect(SelRect))
    //                {
    //                    //if (StPolyline.Draw((Layer)this, _Map)) _SelectObjectList.Add(ObjectIndex);
    //                    _SelectObjectList.Add(ObjectIndex);
    //                }
    //            }
    //        }
    //        #endregion

    //        #region 面层，有两种情况 1）正向矩形：在指定矩形范围内则为选中 2）反向矩形：搭边上就算

    //        if (this.LayerType == 2)
    //        {
    //            StPolygon = _ObjectList[ObjectIndex] as LKMap.CartoGeometry.Polygon;
    //            if (StPolygon.LKStatusMode == lkGeometryStatus.Delete) continue;
    //            if (SelRect.EnvelopeType)    //正向矩形
    //            {
    //                if (SelRect.Contain(StPolygon.Envelope))
    //                {
    //                    //if (StPolygon.Draw((Layer)this, _Map)) _SelectObjectList.Add(ObjectIndex);
    //                    _SelectObjectList.Add(ObjectIndex);
    //                }
    //            }
    //            else    //反向矩形 搭边就选中
    //            {

    //                if (StPolygon.IntersectRect(SelRect))
    //                {
    //                    //if (StPolygon.Draw((Layer)this, _Map)) _SelectObjectList.Add(ObjectIndex);
    //                    _SelectObjectList.Add(ObjectIndex);
    //                }
    //            }
    //        }


    //        #endregion
    //    }
    //}
    //#endregion

    //#region 选择图层中需要显示的实体

    ///// <summary>选择图层中需要显示的实体,主要是用于视图操作
    ///// </summary>
    ///// <param name="pEnvelope"></param>
    //public  void SelectShowObject()
    //{
    //    //根据索引寻找实体
    //    foreach (int i in Map.MapCellIndex.CurrentCellIndex)
    //    {
    //        foreach (int ObjectIdx in this.CellIndex[i])
    //        {
    //            this.SelectShowObject(ObjectIdx);
    //            //if (Map.Extend.Intersect(pGeo.Envelope))
    //            //{
    //            //    //判断的目的是消除重复
    //            //    if (this.ShowObjectList.IndexOf(ObjectIdx) < 0) this.ShowObjectList.Add(ObjectIdx);
    //            //}
    //        }
    //    }
    //}

    //public void SelectShowObject(CartoGeometry.IGeometry pGeometry)
    //{
    //    //标识为删除的不选择
    //    if (pGeometry.LKStatusMode == lkGeometryStatus.Delete) return;

    //    //是否在视口内
    //    if (Map.Extend.Intersect(pGeometry.Envelope))
    //    {
    //        int ObjectIdx = pGeometry.ObjectIndex;
    //        //判断的目的是消除重复
    //        if (this.ShowObjectList.IndexOf(ObjectIdx) < 0) this.ShowObjectList.Add(ObjectIdx);
    //    }
    //}

    //public void SelectShowObject(int ObjIndex)
    //{
    //    this.SelectShowObject(this.GetObject(ObjIndex));
    //}

    ///// <summary>清除图层中需要显示的实体
    ///// </summary>
    //public void ClearShowObject()
    //{
    //    this.ShowObjectList.Clear();
    //}

    ///// <summary>清除图层中被选择的实体，也就是清空选择集
    ///// </summary>
    //public bool ClearSelection()
    //{
    //    bool HaveObject = false;
    //    foreach (int ObjectIdx in SelectObjectList)
    //    {
    //        //this.GetObject(ObjectIdx).LKStatusMode = LKStatusMode.Normal;
    //        HaveObject = true;
    //    }
    //    SelectObjectList.Clear();
    //    return HaveObject;
    //}

    //#endregion

    //实体索引维护

    /// <summary> 删除指定实体的图层级索引，也就是在图层索引中删除该实体的索引
    /// </summary>
    /// <param name="pGeometry"></param>
    private void RemoveIndex(Geometry pGeometry)
    {
        //int ObjIndex = pGeometry.ObjectIndex;
        //foreach (List<int> idxList in this.CellIndex)
        //{
        //    idxList.Remove(ObjIndex);
        //}
    }

    /// <summary>重新计算实体索引，并加入到图层索引中
    /// </summary>
    /// <param name="pGeometry"></param>
    private void AddIndex(Geometry pGeometry)
    {
        //List<int> cellList = pGeometry.CalCellIndex(this.Map);
        // foreach (int idx in cellList)
        //{
        //    this.CellIndex[idx].Add(pGeometry.ObjectIndex);
        //}
        //cellList.Clear(); cellList = null;
    }

    /// <summary>当新增一个带索引的实体后，在本层的网格索引列表中加入该实体所跨越的网格索引
    /// </summary>
    /// <param name="ObjectIndex"></param>
    /// <param name="CellIndex"></param>
    public void AddIndex(Geometry pGeometry,int CellIndex)
    {
        //this.CellIndex[CellIndex].Add(pGeometry.ObjectIndex);
    }


    //实体维护（增加，删除，更新）

    /// <summary>更新实体，最大外接矩形、索引值、GUID号
    /// </summary>
    /// <param name="pGeometry"></param>
    public void UpdateGeometry(Geometry pGeometry)
    {
        ////1、更新最大外接矩形
        //pGeometry.CalEnvelope();

        ////2、更新图层的索引
        //this.RemoveIndex(pGeometry);this.AddIndex(pGeometry);

        ////3、更新属性项，也就是将所有的属性项初始化为空值
        //if (pGeometry.FeatureTable.Count != this.FeatureTable.Count)
        //{
        //    int FeatureItemCount = this.FeatureTable.Count;
        //    for (int i = 0; i < FeatureItemCount; i++)
        //    {
        //        pGeometry.FeatureTable.Add("");
        //    }
        //}
        ////4、更新GUID号
        //if (pGeometry.ObjectID == null)
        //{
        //    Guid newGUID = Guid.NewGuid();//实体索引号
        //    pGeometry.ObjectID = newGUID.ToString();
        //}
    }

    /// <summary>增加实体，适全情况：索引及外接矩形没有计算，需要重新计算情况
    /// </summary>
    /// <param name="pGeometry"></param>
    /// <param name="AllUdpate">true 表示更新外接矩形、索引，false-不更新</param>
    public int AddGeometry(Geometry pGeometry,boolean AllUdpate)
    {
        ////实体在列表中的位置索引，也就是唯一标识号
        //pGeometry.ObjectIndex = this.ObjectList.Count;

        ////将实体加入实体列表
        //this.ObjectList.Add(pGeometry);

        ////更新实体
        //if (AllUdpate)
        //{
        //    this.UpdateGeometry(pGeometry);

        //    //是否需要显示
        //    this.SelectShowObject(pGeometry);
        //}


        return -1;
        // return pGeometry.getIndex();
    }

    /// <summary>增加实体，适合情况：索引及外接矩形都已经计算完成
    /// </summary>
    /// <param name="pGeometry"></param>
    public int AddGeometry(Geometry pGeometry)
    {
        return this.AddGeometry(pGeometry, false);
    }

    /// <summary>返回图层中指定索引处的实体
    /// </summary>
    /// <param name="ObjectIndex"></param>
    /// <returns></returns>
    public Geometry GetObject(int ObjectIndex)
    {
        //此处极为影响速度，解决办法就是只可在列表末尾增加记录，不可在中间插入或删除，
        //这样保证了图层索引中的值就是ObjectList的索引值（也就是实体的索引值），从而可大大加快查询速度
        //return ObjectList[ObjectIndex];
        return null;
    }

    //图层字段
    public int GetFieldIndex(String FieldName)
    {
        //int FieldCount = this.FeatureTable.Count;
        //for (int i = 0; i < FieldCount; i++)
        //{
        //    if (this.FeatureTable[i] == FieldName) return i;
        //}
        return -1;
    }

    public void Dispose()
    {
        this._ShowSelection.RemoveAll();
        this._SelSelection.RemoveAll();
        this._Dataset.Dispose();
    }
}
