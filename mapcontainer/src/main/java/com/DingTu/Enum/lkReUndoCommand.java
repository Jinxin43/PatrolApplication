package com.DingTu.Enum;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public enum lkReUndoCommand
{
    enVertexMove,              //移动线（面）节点
    enVertexAddDel,             //增删线（面）节点
    enVertexComplex,            //线节点的复合操作，先删除再增加
    enAddDeleteObject,          //增删除实体
    enSplitMerge,               //打断合并
    enMoveObject,                //移动实体
    enFlip                   //线路翻转
}
