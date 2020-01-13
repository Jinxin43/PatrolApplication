package com.DingTu.mapcontainer;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public enum Tools {
    /// <summary>//Pan 移屏
    /// </summary>
    Pan,
    Shutter,
    ZoomInOutPan,   //手势放大缩小

    /// <summary>Zoom in 放大窗口
    /// </summary>
    ZoomIn,

    /// <summary>Zoom out 缩小窗口
    /// </summary>
    ZoomOut,

    /// <summary>选择
    /// </summary>
    Select,

    /// <summary>全屏
    /// </summary>
    FullScreen,
    FullScreenSize,

    /// <summary>查询
    /// </summary>
    Query,

    /// <summary>No active tool  无动作
    /// </summary>
    None,

    /// <summary>距离测量
    /// </summary>
    MeasureLength,

    /// <summary>面积测量
    /// </summary>
    MeasureArea,

    /// <summary>测量桩号
    /// </summary>
    CallMile,

    /// <summary>插入节点鼠标样式
    /// </summary>
    InsertVertex,

    MoveVertex,
    EditLine,
    AddVertex,
    DelVertex,
    DelVertexInBox,
    MoveObject,
    Split,
    Merge1,
    Merge2,
    /// <summary>加点
    /// </summary>
    AddPoint,
    AddPolyline,
    AddPolygon,
    Smooth
}
