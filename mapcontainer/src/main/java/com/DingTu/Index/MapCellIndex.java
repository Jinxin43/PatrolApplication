package com.DingTu.Index;

import com.DingTu.Base.Tools;

import java.util.ArrayList;
import java.util.List;

import com.DingTu.Cargeometry.Envelope;

/**
 * Created by Dingtu2 on 2017/5/31.
 * Map的全局格网索引
 */



public class MapCellIndex {

    //网格索引的高度与宽度
    private double _CellWidth, _CellHeight;
    public double getCellWidth()
    {
        return _CellWidth;
    }
    public double getCellHeigth()
    {
        return _CellHeight;
    }

    //全局最大矩形
    private Envelope _Extend;
    public Envelope getExtend()
    {
        return _Extend;
    }
    public void setEnvelope(Envelope env)
    {
        this._Extend=env;
    }

    //四叉树级数
    private int[] _TreeLevel = new int[] {64,32, 16, 8, 4, 2, 1 };
    public MapCellIndex()
    {
        this._Extend = new Envelope(0,0,0,0);
        //this.InitCellIndex(this.getCells());
    }

    /**
     * 计算矩形范围的查询过滤条件
     * @param pEnve
     * @return
     */
    public String CalCellIndexFilter(Envelope pEnve)
    {
        List<String> cellIndexList = new ArrayList<String>();

        for (int i = 0; i < this._TreeLevel.length; i++)
        {
            int HRow, LRow, HCol, LCol;
            int Cells = this._TreeLevel[i];
            T4Index LT1 = this.CalCellIndexByPoint(pEnve.getLeftTop().getX(), pEnve.getLeftTop().getY(), Cells);
            T4Index RB1 = this.CalCellIndexByPoint(pEnve.getRightBottom().getX(), pEnve.getRightBottom().getY(), Cells);
            HRow = LT1.GetRow();LCol = LT1.GetCol();
            LRow = RB1.GetRow();HCol = RB1.GetCol();

            LRow+=(this._TreeLevel.length - i) * 100 * 100;
            HRow+=(this._TreeLevel.length - i) * 100 * 100;
            LCol+=(this._TreeLevel.length - i) * 100 * 100;
            HCol+=(this._TreeLevel.length - i) * 100 * 100;
            String SubSQL = "(RIndex>=%1$s and RIndex<=%2$s and CIndex>=%3$s and CIndex<=%4$s)";
            SubSQL = String.format(SubSQL, LRow, HRow, LCol, HCol);
            cellIndexList.add(SubSQL);
        }
        return Tools.JoinT(" or ", cellIndexList);
    }

    /// <summary>
    /// 计算指定矩形所在唯一节点索引
    /// </summary>
    /// <param name="pEnve"></param>
    /// <returns></returns>
    public T4Index CalOneCellIndex(Envelope pEnve)
    {
        for (int i = 0; i < this._TreeLevel.length; i++)
        {
            int Cells = this._TreeLevel[i];
            int HRow, LRow, HCol, LCol;
            T4Index LT = this.CalCellIndexByPoint(pEnve.getLeftTop().getX(), pEnve.getLeftTop().getY(), Cells);
            T4Index RB = this.CalCellIndexByPoint(pEnve.getRightBottom().getX(), pEnve.getRightBottom().getY(), Cells);
            HRow = LT.GetRow();LCol = LT.GetCol();
            LRow = RB.GetRow();HCol = RB.GetCol();
            if (HRow == LRow && LCol == HCol)
            {
                HRow+=(this._TreeLevel.length - i) * 100 * 100;
                HCol+=(this._TreeLevel.length - i) * 100 * 100;
                return new T4Index(HRow,HCol);
            }
        }
        return new T4Index(0,0);
    }

    /**
     * 计算指定点所在的树型节点索引
     * @param X
     * @param Y
     * @param Cells
     * @return
     */
    private T4Index CalCellIndexByPoint(double X, double Y,int Cells)
    {
        int Row,Col;
        double _CellHeight = this._Extend.getHeight() / Cells;  //每个小网格的高度
        double _CellWidth = this._Extend.getWidth() / Cells;    //每个小网格的宽度
        Row = (int)((Y - this._Extend.getMinY()) / _CellHeight);
        Col = (int)((X - this._Extend.getMinX()) / _CellWidth);
        if (Col >= Cells) Col = Cells - 1;
        else if (Col < 0) Col = 0;
        if (Row >= Cells) Row = Cells - 1;
        else if (Row < 0) Row = 0;
        return new T4Index(Row,Col);
    }
}
