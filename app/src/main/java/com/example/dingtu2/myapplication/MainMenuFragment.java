package com.example.dingtu2.myapplication;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkGpsFixMode;
import com.DingTu.GPS.LocationEx;
import com.example.dingtu2.myapplication.View.GuanHuZhiDuActivity;
import com.example.dingtu2.myapplication.View.JieShaoActivity;
import com.example.dingtu2.myapplication.controls.GridViewAdapter;
import com.example.dingtu2.myapplication.controls.Picture;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment implements AdapterView.OnItemClickListener {


    public TextView mAreaView;
    private View myView;
    private GridView mGridView;
    private String IMAGE_ITEM = "imgage_item";
    private String TEXT_ITEM = "text_item";

    public MainMenuFragment() {
        // Required empty public constructor
    }

    /**
     * 将度转换为度分秒
     *
     * @param DDD
     * @return
     */
    private static String GetDDMMSS(double DDD) {
        //DD°MM'SS.SSSS″
        int dd = (int) Math.floor(DDD);
        double MM = (DDD - dd) * 60;
        int mm = (int) Math.floor(MM);

        double SS = (MM - mm) * 60;
        String ss = Tools.ConvertToDigi(SS + "", 4);
        return dd + "°" + mm + "'" + ss + "″";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        myView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        mGridView = (GridView) myView.findViewById(R.id.mainmenu_gridview);

        mAreaView = (TextView) myView.findViewById(R.id.mainmenu_area);
        GridViewAdapter saImageItems = new GridViewAdapter(this.getActivity(), mGridView);

        // 设置GridView的adapter。GridView继承于AbsListView。
        mGridView.setAdapter(saImageItems);
        mGridView.setOnItemClickListener(this);

        if (PubVar.m_GPSLocate != null) {
            PubVar.m_GPSLocate.setGPSCallback(new ICallback() {
                @Override
                public void OnClick(String Str, Object ExtraStr) {

//                    "108.9215856,34.4681726,512.0"
//                    "巡护中,2小时21分钟,11.4公里"

                    if (ExtraStr == null || ((LocationEx) ExtraStr).GetGpsFixMode() != lkGpsFixMode.en3DFix) {
                        ((TextView) myView.findViewById(R.id.mainmenu_coord)).setText("GPS信号弱");
                    } else {
                        if (((LocationEx) ExtraStr).GetGpsLongitude() > 0 && (((LocationEx) ExtraStr).GetGpsLatitude()) > 0) {
                            String coor = GetDDMMSS(((LocationEx) ExtraStr).GetGpsLongitude()) + ", " + GetDDMMSS((((LocationEx) ExtraStr).GetGpsLatitude())) + ", " + Tools.ConvertToDigi(((LocationEx) ExtraStr).GetGpsAltitude() + "", 1);
                            ((TextView) myView.findViewById(R.id.mainmenu_coord)).setText(coor);
                        } else {
                            ((TextView) myView.findViewById(R.id.mainmenu_coord)).setText("GPS信号弱");
                        }

                    }
                }


            });
        }

        PubVar.m_DoEvent.mGPSSatus.SetGpsSetCallback(new ICallback() {
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                if (ExtraStr != null) {
                    ((TextView) myView.findViewById(R.id.mainmenu_patrolStatus)).setText(ExtraStr.toString());
                }

            }
        });

        return myView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long rowid) {

        // 根据元素位置获取对应的值
        Picture textView = (Picture) adapter.getItemAtPosition(position);
        String itemText = textView.getTitle();

        if ("保护区介绍".equals(itemText)) {
            try {
//                Intent intent = new Intent(this.getActivity(),ZhouBaoJieShaoActivity.class);
                Intent intent = new Intent(this.getActivity(), JieShaoActivity.class);
                startActivity(intent);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if ("政策法规".equals(itemText)) {

            try {
                Intent intent = new Intent(this.getActivity(), GuanHuZhiDuActivity.class);
                startActivity(intent);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if ("公告通知".equals(itemText)) {
            try {
                Intent intent = new Intent(this.getActivity(), NoticesActivity.class);
                startActivity(intent);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if ("地图".equals(itemText)) {
            ((MainActivity) getActivity()).switchBottomMenu(R.id.navigation_dashboard);
        }

        if ("巡护".equals(itemText)) {
            ((MainActivity) getActivity()).switchBottomMenu(R.id.navigation_dashboard);
        }

        if ("巡护记录".equals(itemText)) {
                Intent intent = new Intent(this.getActivity(),PatrolListActivity.class);
                startActivity(intent);
                getActivity().finish();

        }
        if ("设置".equals(itemText)) {
//            Intent intent = new Intent(this.getActivity(),SettingsActivity.class);
//            startActivity(intent);

            ((MainActivity) getActivity()).switchBottomMenu(R.id.navigation_notifications);

        }


    }

}
