package com.example.dingtu2.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkGpsFixMode;
import com.DingTu.GPS.LocationEx;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OneWarningActivity extends AppCompatActivity {

    @BindView(R.id.gvList)
    GridView gridView;
    @BindView(R.id.etWarningTitle)
    EditText etWarningTitle;
    @BindView(R.id.etWarningTime)
    EditText etWarningTime;
    @BindView(R.id.etWarningDescription)
    EditText etWarningDescription;
    private LocationEx mLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_warning);

        try {
            ButterKnife.bind(this);
            setupActionBar();
        } catch (Exception ex) {

        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("一键报警");
        }
    }

    private void refreshLocation() {
        try {
            if (PubVar.m_GPSLocate.m_LocationEx != null && PubVar.m_GPSLocate.m_LocationEx.GetGpsFixMode() == lkGpsFixMode.en3DFix) {
                mLocation = PubVar.m_GPSLocate.m_LocationEx;
            } else {
                Toast.makeText(OneWarningActivity.this, "请开启GPS或在开阔地带精确定位,然后刷新位置!", Toast.LENGTH_SHORT).show();
            }
            ((TextView) findViewById(R.id.tvLon)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude() + "", 7));
            ((TextView) findViewById(R.id.tvLat)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude() + "", 7));
            ((TextView) findViewById(R.id.tvHigh)).setText(PubVar.m_GPSLocate.m_LocationEx.GetGpsAltitude() + "");
        } catch (Exception ex) {
            Toast.makeText(OneWarningActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
