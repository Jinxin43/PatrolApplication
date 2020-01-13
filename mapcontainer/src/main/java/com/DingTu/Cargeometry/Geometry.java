package com.DingTu.Cargeometry;

import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkGeometryStatus;
import com.DingTu.Symbol.ISymbol;

import java.util.ArrayList;
import java.util.List;

import com.DingTu.Index.MapCellIndex;
import com.DingTu.Index.T4Index;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

public abstract class Geometry {

    //多部分的坐标序列
    protected List<Part> _PartList = new ArrayList<Part>();

    /**
     * 多部分数
     * @return
     */
    public int getPartCount()
    {
        return this._PartList.size();
    }

    /**
     * 获取实体的坐标总数
     */
    public int getVertexCount()
    {
        int CoorCount = 0;
        for(Part part : this._PartList) CoorCount += part.getVertexList().size();
        return CoorCount;
    }

    /**
     * 增加新部分
     * @param part
     */
    public void AddPart(Part part)
    {
        this._PartList.add(part);
        //if (this._PartList.size() > 1) this.IsSimple = false;
    }
    public void RemovePart(Part part)
    {
        this._PartList.remove(part);
    }

    /**
     * 得到指定索引的部分
     * @param PartIndex
     * @return
     */
    public Part GetPartAt(int PartIndex)
    {
        return this._PartList.get(PartIndex);
    }

    /**
     * 获取多部分在长坐标串的起点索引
     */
    public List<Integer> GetPartIndexList()
    {
        List<Integer> partIndex = new ArrayList<Integer>();
        partIndex.add(0);
        for(int i=0;i<this.getPartCount()-1;i++)
        {
            int VertexCount = this._PartList.get(i).getVertexList().size();
            partIndex.add(VertexCount + partIndex.get(i));  //节点数据累加
        }
        return partIndex;
    }


    /// <summary>
    /// 数据库记录的索引值，也就是与数据关联的唯一值SYS_ID
    /// </summary>
    private int _SysId = -1;
    public int getSysId()
    {
        return this._SysId;
    }
    public void setSysId(int value)
    {
        this._SysId = value;
    }

    //Geometry的实体ID号，备用值
    private String _ID;
    public String getID()
    {
        return _ID;
    }
    public void setID(String id)
    {
        _ID = id;
    }



    //实体是否为空
    public boolean IsNull()
    {
        if (this.getPartCount() == 0) return true;
        else return false;
    }

    //设置实体为NULL
    public void SetNull()
    {
        this._PartList.clear();
    }

    //Geometry的外接矩形
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
        for (Part part : this._PartList)
        {
            part.UpdateEnvelope();
            if (pEnv.IsZero()) pEnv = part.getEnvelope();
            else pEnv = pEnv.Merge(part.getEnvelope());
        }
        this._Envelope = pEnv;
    }



    //实体的状态模式，用于实体编辑处理
    private lkGeometryStatus _Status = lkGeometryStatus.enNormal;
    public lkGeometryStatus getStatus()
    {
        return _Status;
    }
    public void setStatus(lkGeometryStatus GS)
    {
        _Status = GS;
    }

    //是否被编辑过，用于保存数据
    private boolean _Edited = false;
    public void SetEdited(boolean edited){this._Edited=edited;}
    public boolean GetEdited(){return this._Edited;}


    //实体符号样式
    private ISymbol _Symbol = null;
    public ISymbol getSymbol()
    {
        return _Symbol;
    }
    public void setSymbol(ISymbol value)
    {
        _Symbol = value;
    }

    //额外数据项
    private String _Tag = "";
    public String getTag(){return this._Tag; }
    public void setTag(String tag) {this._Tag = tag;}

    private String _Tag_UniqueSymbol = "";
    public String getTagForUniqueSymbol(){return this._Tag_UniqueSymbol; }
    public void setTagForUniqueSymbol(String tag) {this._Tag_UniqueSymbol = tag;}


//    //更新最大外接矩形
//    public void UpdateEnvelope()
//    {
//        this._Envelope = this.CalEnvelope();
//    }
//
//    //计算实体的外接矩形
//    public Envelope CalEnvelope()
//    {
//        Coordinate Pt;
//        double MinX = 0, MinY = 0, MaxX = 0, MaxY = 0;
//        for (int i = 0; i < this.getItems().size(); i++)
//        {
//            Pt = this.getItems().get(i);
//            if (i == 0)
//            {
//                MinX = Pt.getX(); MinY = Pt.getY(); MaxX = MinX; MaxY = MinY;
//            }
//            if (MinX > Pt.getX()) MinX = Pt.getX();
//            if (MaxX < Pt.getX()) MaxX = Pt.getX();
//            if (MinY > Pt.getY()) MinY = Pt.getY();
//            if (MaxY < Pt.getY()) MaxY = Pt.getY();
//        }
//        //更新最大外接矩形
//        return new Envelope(MinX, MaxY, MaxX, MinY);
//
//    }

    //根据偏移量更新实体的坐标信息
    public void UpdateCoordinate(double deltX, double deltY)
    {
        for(int p=0;p<this.getPartCount();p++)
        {
            Part part = this.GetPartAt(p);
            for (Coordinate newPt:part.getVertexList())
            {
                newPt.setX(newPt.getX()+deltX);
                newPt.setY(newPt.getY()+deltY);
            }
            part.UpdateEnvelope();
        }
        this._Envelope = null;
    }

    public abstract Coordinate getCenterPoint();

    //计算实体所在树节点索引
    public T4Index CalCellIndex(MapCellIndex mapCellIndex)
    {
        return mapCellIndex.CalOneCellIndex(this.getEnvelope());
    }

    //选择实体
    //abstract public bool Select(Coordinate SelectPoint, double Tolerance);

    //克隆实体
    abstract public Geometry Clone();

    //点击测试
    abstract public boolean HitTest(Coordinate HitPoint, double Tolerance);

    //偏移
    abstract public boolean Offset(double OffsetX, double OffsetY);

    //得到实体的类型
    abstract public lkGeoLayerType GetType();
}
