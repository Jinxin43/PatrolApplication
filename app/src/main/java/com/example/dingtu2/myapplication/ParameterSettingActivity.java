package com.example.dingtu2.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.CoordinateSystem.CoorSystem;
import com.DingTu.Dataset.SQLiteDataReader;

public class ParameterSettingActivity extends AppCompatActivity {

    private Double oldDelX = 0d;
    private Double oldDelY = 0d;
    private Double oldRoute = 0d;
    private Double oldScale = 1.0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter_setting);
        getParameter();
//        findViewById(R.id.txSavePara).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveParameter();
//            }
//        });
        findViewById(R.id.txCancelPara).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getParameter() {
        String SQL = "Select * from T_Project where Id=2";
        SQLiteDataReader DR = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().Query(SQL);
        if (DR.Read()) {
            if (DR.GetString("P41") != null && !DR.GetString("P41").isEmpty()) {
                oldDelX = Double.parseDouble(DR.GetString("P41"));
            } else {
                oldDelX = 0d;
            }

            if (DR.GetString("P42") != null && !DR.GetString("P42").isEmpty()) {
                oldDelY = Double.parseDouble(DR.GetString("P42"));
            }

            if (DR.GetString("P43") != null && !DR.GetString("P43").isEmpty()) {
                oldRoute = Double.parseDouble(DR.GetString("P43"));
            }

            if (DR.GetString("P44") != null && !DR.GetString("P44").isEmpty()) {
                oldScale = Double.parseDouble(DR.GetString("P44"));
            } else {
                oldScale = 1.0d;
            }
        }

        Tools.SetTextViewValueOnID(this, R.id.et_DelX, oldDelX + "");
        Tools.SetTextViewValueOnID(this, R.id.et_DelY, oldDelY + "");
        Tools.SetTextViewValueOnID(this, R.id.et_Rotate, oldRoute + "");
        Tools.SetTextViewValueOnID(this, R.id.et_Scale, 1 / oldScale + "");
    }

    private void saveParameter() {

        String DelX = Tools.GetTextValueOnID(this, R.id.et_DelX); // X平移
        String DelY = Tools.GetTextValueOnID(this, R.id.et_DelY); // Y平移
        String Rotate = Tools.GetTextValueOnID(this, R.id.et_Rotate); // 旋转
        String Scale = Tools.GetTextValueOnID(this, R.id.et_Scale); // 尺度

        try {
            oldDelX = Double.parseDouble(DelX);
            if (oldDelX > 500 || oldDelX < -500) {
                Tools.ShowMessageBox(this, "X平移不能大于500且不能小于-500！");
                return;
            }
        } catch (NumberFormatException ex) {
            Tools.ShowMessageBox(this, "X平移格式不对！");
            return;
        }

        try {
            oldDelY = Double.parseDouble(DelY);
            if (oldDelY > 500 || oldDelY < -500) {
                Tools.ShowMessageBox(this, "Y平移不能大于500且不能小于-500！");
                return;
            }
        } catch (NumberFormatException ex) {
            Tools.ShowMessageBox(this, "Y平移格式不对！");
            return;
        }

        try {
            oldRoute = Double.parseDouble(Rotate);
        } catch (NumberFormatException ex) {
            Tools.ShowMessageBox(this, "旋转格式不对！");
            return;
        }

        try {
            oldScale = Double.parseDouble(Scale);
        } catch (NumberFormatException ex) {
            Tools.ShowMessageBox(this, "尺度格式不对！");
            return;
        }

        String SQL1 = "update T_Project set %1$s where Id=2";
        SQL1 = String.format(SQL1,
                "P41='" + oldDelX + "'," + "P42='" + oldDelY + "',"
                        + "P43='" + oldRoute + "'," + "P44='"
                        + (1 / oldScale) + "'," + "F1=''," + "PMTransMethod='四参转换'");

        if (PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(SQL1)) {

            CoorSystem CS = (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem());
            CS.SetPMTransMethodName("四参转换");
            CS.SetTransToP41(Double.parseDouble(DelX) + "");
            CS.SetTransToP42(Double.parseDouble(DelY) + "");
            CS.SetTransToP43(Double.parseDouble(Rotate) + "");
            double s = 1 / Double.parseDouble(Scale);
            CS.SetTransToP44(s + "");

            finish();
        } else {
            Tools.ShowMessageBox(this, "保存转换参数失败！");
            return;
        }
    }
}
