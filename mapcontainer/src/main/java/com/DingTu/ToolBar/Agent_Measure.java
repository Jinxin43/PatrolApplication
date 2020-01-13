package com.DingTu.ToolBar;

import android.view.View;

import com.DingTu.Base.PubVar;
import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.mapcontainer.Tools;

import java.util.UUID;

/**
 * Created by Dingtu2 on 2018/5/12.
 */

public class Agent_Measure implements IToolsBarCommand {

    private String Id = UUID.randomUUID().toString();
    @Override
    public void OnDispose() {
        PubVar.m_MapControl.ClearOnPaint(this.Id);
        this.m_Measure.Clear();
    }

    @Override
    public void OnChange() {
        // TODO Auto-generated method stub

    }

    @Override
    public void OnPrepare() {
        if (this.m_Measure==null)this.m_Measure = new Measure();
        PubVar.m_MapControl.AddOnPaint(this.Id, this.m_Measure);
    }


    private Measure m_Measure = null;

    private View m_View = null;
    public void SetView(View view)
    {
        this.m_View = view;
        if (this.m_Measure==null)this.m_Measure = new Measure();
//        this.m_View.findViewById(R.id.bt_line_measure).setOnClickListener(new ViewClick());
//        this.m_View.findViewById(R.id.bt_poly_measure).setOnClickListener(new ViewClick());
//        this.m_View.findViewById(R.id.bt_snap).setOnClickListener(new ViewClick());
//        this.m_View.findViewById(R.id.bt_clear).setOnClickListener(new ViewClick());
    }

    public class ViewClick implements View.OnClickListener
    {
        @Override
        public void onClick(View arg0)
        {
            String Tag = arg0.getTag().toString();
            if (Tag.equals("测线"))
            {
                SetMode(1);
            }
            if (Tag.equals("测面"))
            {
                SetMode(2);
            }
            if (Tag.equals("捕捉"))
            {
//                View btnSnap = m_View.findViewById(R.id.bt_snap);
//               Tools.SetToolsBarItemSelect(btnSnap,!m_Measure.m_Snap);
                m_Measure.m_Snap = !m_Measure.m_Snap;
            }
            if (Tag.equals("清空"))
            {
//                if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
                m_Measure.Clear();
            }
        }
    }

    //设置测量模式
    private void SetMode(int mode)
    {
//        View btnLine = this.m_View.findViewById(R.id.bt_line_measure);
//        View btnPoly = this.m_View.findViewById(R.id.bt_poly_measure);
//        com.DingTu.Base.Tools.SetToolsBarItemSelect(btnLine, false);
//        com.DingTu.Base.Tools.SetToolsBarItemSelect(btnPoly, false);
//        if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
        this.m_Measure.SetMode(mode);
        PubVar.m_MapControl.setActiveTools(Tools.AddPolyline, this.m_Measure, this.m_Measure);
        PubVar.m_MapControl.invalidate();
//        if (mode==1)com.DingTu.Base.Tools.SetToolsBarItemSelect(btnLine, true);
//        if (mode==2)com.DingTu.Base.Tools.SetToolsBarItemSelect(btnPoly, true);
    }

    public void SetZHMode()
    {

        this.m_Measure.SetMode(0);
        PubVar.m_MapControl.setActiveTools(Tools.AddPolyline, this.m_Measure, this.m_Measure);
        PubVar.m_MapControl.invalidate();
    }

    public Coordinate getFirstPoint()
    {
        return this.m_Measure.getFisrtCoor();
    }
}
