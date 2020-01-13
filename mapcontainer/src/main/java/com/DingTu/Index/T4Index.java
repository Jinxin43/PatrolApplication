package com.DingTu.Index;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public class T4Index {
    public T4Index(int row,int col)
    {
        this._Row=row;this._Col=col;
    }
    private int _Row=0;
    private int _Col=0;
    public void SetRow(int row){this._Row=row;}
    public void SetCol(int col){this._Col=col;}
    public int GetRow(){return this._Row;}
    public int GetCol(){return this._Col;}
}
