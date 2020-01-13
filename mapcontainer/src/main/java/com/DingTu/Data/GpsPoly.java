package com.DingTu.Data;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.DingTu.Base.ICallback;
import com.DingTu.Dataset.Dataset;
import com.DingTu.mapcontainer.IOnPaint;
import com.DingTu.mapcontainer.IOnTouchCommand;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class GpsPoly implements IOnTouchCommand, IOnPaint {

    /**
     * 设置相关数据集Dataset
     * @param pDataset
     */
    public void SetDataset(Dataset pDataset){this.m_CGPSLine.SetDataset(pDataset);}
    public Dataset GetDataset(){return this.m_CGPSLine.GetDataset();}


    private GpsLine m_CGPSLine = null;
    public GpsLine getGPSLine()
    {
        return m_CGPSLine;
    }

    public GpsPoly()
    {
        this.m_CGPSLine = new GpsLine();
        this.m_CGPSLine.SetIfCalArea(true); //是否计算面积
    }

    /**
     * 编辑属性
     * @param LayerID 图层ID
     * @param SYS_ID 实体ID
     */
    public void Edit(String LayerID,int SYS_ID)
    {

//        if (!PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().CheckLayerValid(LayerID))return;

//        String projectType = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(LayerID).GetLayerProjecType();
//        if(projectType != null && projectType.equals(ForestryLayerType.TuigengLayer))
//        {
//            TuiGengData tuiGengData = new TuiGengData(LayerID,SYS_ID,false);
//        }
//        else if(projectType != null && projectType.equals(ForestryLayerType.LindibiangengLayer))
//        {
//            LinDiBianGengData ldbg = new LinDiBianGengData(LayerID,SYS_ID);
//            ldbg.ShowView();
//        }
//        else if(projectType != null && projectType.equals(ForestryLayerType.WeipianJianchaLayer))
//        {
//            WeiPianZhiFaData wpzf = new WeiPianZhiFaData(LayerID,SYS_ID);
//            wpzf.ShowView();
//        }
//        else
//        {
//            this.Edit(LayerID, SYS_ID,null);
//        }

        this.Edit(LayerID, SYS_ID,null);
    }


    public void Edit(String LayerID,int SYS_ID,ICallback cb)
    {
//    	TanhuiDataTemplate _DT = new TanhuiDataTemplate();
//    	_DT.SetEditInfo(LayerID, SYS_ID);
//    	_DT.SetCallback(cb);
//    	_DT.ShowDialog();
//        GeneralDateEditor dataEdit = new GeneralDateEditor(LayerID, SYS_ID);
//        dataEdit.SetCallback(cb);
    }

    @Override
    public void OnPaint(Canvas canvas)
    {
        this.m_CGPSLine.OnPaint(canvas);
    }

    @Override
    public void SetOnTouchEvent(MotionEvent e) {
        // TODO Auto-generated method stub
        this.m_CGPSLine.SetOnTouchEvent(e);
    }
}
