package com.example.dingtu2.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.CoordinateSystem.CoorSystem;
import com.DingTu.Enum.lkMapFileType;
import com.example.dingtu2.myapplication.controls.SelectDictionary;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapSettingActivity extends AppCompatActivity {

    @BindView(R.id.spCoordinateType)
    Spinner spCoordinateType;

    @BindView(R.id.spCentralMeridian)
    Spinner spCentralMeridian;
    Dialog selectVertorDialog;
    Dialog startRoundDialog;
    private List<HashMap<String, Object>> m_GridMapFileList = null;
    private List<HashMap<String, Object>> m_VetorMapFileList = null;
    private String mSelectCoor;
    private String mSelectCenter;
    private EditText gridFileNames, vertorFileNames;
    private ICallback m_Callback;
    //上部按钮事件
    private ICallback pCallback = new ICallback() {
        @Override
        public void OnClick(String Str, Object ExtraStr) {

            if (Str.equals("确定")) {
                if (m_Callback == null) startRoundDialog.dismiss();

                //选中的背景底图项
                if (m_GridMapFileList != null) {
                    List<HashMap<String, Object>> SelectMapFileList = new ArrayList<HashMap<String, Object>>();
                    for (HashMap<String, Object> mapFile : m_GridMapFileList) {
                        if (Boolean.parseBoolean(mapFile.get("Select") + "")) {
                            SelectMapFileList.add(mapFile);
                        }
                    }
                    if (!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().SaveBKLayer("栅格", SelectMapFileList)) {
                        Tools.ShowMessageBox("栅格底图保存失败！");
                    }
                }
                if (m_Callback != null) m_Callback.OnClick("", "");
                startRoundDialog.dismiss();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_setting);

        ButterKnife.bind(this);

        String[] arrCoordinaType = "WGS-84坐标、西安80坐标、北京54坐标、2000国家大地坐标系".split("、");
        ArrayAdapter<String> roundTypeAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
                android.R.layout.simple_spinner_item,
                arrCoordinaType);
        roundTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCoordinateType.setAdapter(roundTypeAdapter);
        int index = -1;
        for (String coordType : arrCoordinaType) {
            index++;
            if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem() != null) {
                if (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName().equals(coordType)) {
                    break;
                }
            }
        }

        spCoordinateType.setSelection(index);
        spCoordinateType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initBKFileLists(lkMapFileType.enGrid);
                initBKFileLists(lkMapFileType.enVector);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String[] arrCentralType = "105、108、111".split("、");
        ArrayAdapter<String> centralMedialAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
                android.R.layout.simple_spinner_item,
                arrCentralType);
        centralMedialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCentralMeridian.setAdapter(centralMedialAdapter);
        int centralIndex = -1;
        for (String centralMeida : arrCentralType) {
            centralIndex++;
            float currentCentral = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetCenterMeridian();
            if (Float.parseFloat(centralMeida) == currentCentral) {
                break;
            }
        }
        spCentralMeridian.setSelection(centralIndex);
        spCentralMeridian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initBKFileLists(lkMapFileType.enGrid);
                initBKFileLists(lkMapFileType.enVector);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initBKFileLists(lkMapFileType.enGrid);
        initBKFileLists(lkMapFileType.enVector);
        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("地图管理");
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initBKFileLists(lkMapFileType kind) {
        if (kind == lkMapFileType.enGrid) {
            mSelectCoor = spCoordinateType.getSelectedItem().toString();
            mSelectCenter = spCentralMeridian.getSelectedItem().toString();
            this.m_GridMapFileList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList();
            if (this.m_GridMapFileList != null && this.m_GridMapFileList.size() > 0) {
                String gridFileNames = "";
                for (HashMap<String, Object> hashMap : m_GridMapFileList) {
                    String coor = hashMap.get("CoorSystem").toString();
                    if (coor.contains(mSelectCoor) && coor.contains(mSelectCoor)) {
                        String name = hashMap.get("BKMapFile") + ";";
                        gridFileNames += name;
                        if (gridFileNames.endsWith(";")) {
                            gridFileNames = gridFileNames.substring(0, gridFileNames.length() - 1);
                        }

                        ((EditText) this.findViewById(R.id.etGridMap)).setText(gridFileNames);

                    }else{
                        ((EditText) this.findViewById(R.id.etGridMap)).setText("");
                    }
                }

            }
        }
        if (kind == lkMapFileType.enVector) {
            mSelectCoor = spCoordinateType.getSelectedItem().toString();
            mSelectCenter = spCentralMeridian.getSelectedItem().toString();
            this.m_VetorMapFileList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileList();
            if (this.m_VetorMapFileList != null && this.m_VetorMapFileList.size() > 0) {
                String gridFileNames = "";
                for (HashMap<String, Object> hashMap : m_VetorMapFileList) {
                    String coor = hashMap.get("CoorSystem").toString();
                    if (coor.contains(mSelectCoor) && coor.contains(mSelectCenter)) {
                        String name = hashMap.get("BKMapFile") + ";";
                        gridFileNames += name;
                    }
                }
                if (gridFileNames.endsWith(";")) {
                    gridFileNames = gridFileNames.substring(0, gridFileNames.length() - 1);
                }

                ((EditText) this.findViewById(R.id.etVertorMap)).setText(gridFileNames);
            }


        }
    }

    @OnClick({R.id.txSelectVertorMap, R.id.txSelectGridMap, R.id.txCancelMap, R.id.txSaveMap})
    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.txSelectVertorMap:
                String mSelectCoor = spCoordinateType.getSelectedItem().toString();
                float mSelectCenter= Float.parseFloat(spCentralMeridian.getSelectedItem().toString());
                CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
                float center=PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetCenterMeridian();
                if(CS.GetName().equals(mSelectCoor)&& mSelectCenter==center){
                    selectVertorMap();
                }else {
                    Toast.makeText(getApplicationContext(),"所选坐标和当前工程坐标不一致，请先点击保存！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txSelectGridMap:
                String mSelect= spCoordinateType.getSelectedItem().toString();
                float mCenter=Float.parseFloat(spCentralMeridian.getSelectedItem().toString());
                CoorSystem cs = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
                float centers=PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetCenterMeridian();
                if(cs.GetName().equals(mSelect)&&mCenter==centers){
                    selectGridMap();
                }else{
                    Toast.makeText(getApplicationContext(),"所选坐标和当前工程坐标不一致，请先点击保存！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txCancelMap:
                finish();
                break;
            case R.id.txSaveMap:
                SaveGrid();
                SaveVetor();
                SaveMapSetting();
                break;

        }
    }

    private void SaveVetor() {
        if (m_VetorMapFileList != null) {
            mSelectCoor = spCoordinateType.getSelectedItem().toString();
            mSelectCenter = spCentralMeridian.getSelectedItem().toString();
            List<HashMap<String, Object>> SelectMapFileList = new ArrayList<HashMap<String, Object>>();
            String filePath = "";
            for (HashMap<String, Object> mapFile : m_VetorMapFileList) {
                String coor = mapFile.get("CoorSystem").toString();
                if (coor.contains(mSelectCoor) && coor.contains(mSelectCenter)) {
                    if (Boolean.parseBoolean(mapFile.get("Select") + "")) {
                        filePath += mapFile.get("BKMapFile") + ";";
                        SelectMapFileList.add(mapFile);
                    }
                }

            }
            if (!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().SaveBKLayer("矢量", SelectMapFileList)) {
                Tools.ShowMessageBox("矢量底图保存失败！");
            }
        }
    }

    private void SaveGrid() {
        if (m_GridMapFileList != null) {
            mSelectCoor = spCoordinateType.getSelectedItem().toString();
            mSelectCenter = spCentralMeridian.getSelectedItem().toString();
            List<HashMap<String, Object>> SelectMapFileList = new ArrayList<HashMap<String, Object>>();
            String filePath = "";
            for (HashMap<String, Object> mapFile : m_GridMapFileList) {
                String coor = mapFile.get("CoorSystem").toString();
                if (coor.contains(mSelectCoor) && coor.contains(mSelectCenter)) {
                    if (Boolean.parseBoolean(mapFile.get("Select") + "")) {
                        filePath += mapFile.get("BKMapFile") + ";";
                        SelectMapFileList.add(mapFile);
                    }
                }
            }
            if (!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().SaveBKLayer("栅格", SelectMapFileList)) {
                Tools.ShowMessageBox("栅格底图保存失败！");
            }
        }

    }

    public void SaveMapSetting() {
        if (PubVar.m_DoEvent.m_ProjectDB.saveProjectInfo(spCoordinateType.getSelectedItem() + "", spCentralMeridian.getSelectedItem() + "")) {
            MainActivity.mMapSettingCallbak.OnClick("", null);
            finish();
        }
    }

    private void selectVertorMap() {
        selectVertorDialog = new Dialog(MapSettingActivity.this);
        selectVertorDialog.setContentView(R.layout.dialog_selectgridmap);
        selectVertorDialog.setTitle("选择矢量底图");

        vertorFileNames = (EditText) this.findViewById(R.id.etVertorMap);
        selectVertorDialog.findViewById(R.id.text_gridcancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVertorDialog.dismiss();
            }
        });
        selectVertorDialog.findViewById(R.id.text_gridsave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSaveVetor();
                selectVertorDialog.dismiss();
            }
        });

        final ListView listViewGridMap = (ListView) selectVertorDialog.findViewById(R.id.listViewGridMap);

        CheckBox cbSelectGridAll = (CheckBox) selectVertorDialog.findViewById(R.id.cbselectgridall);
        cbSelectGridAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (m_VetorMapFileList == null) return;
                for (HashMap<String, Object> hmObj : m_VetorMapFileList) {
                    hmObj.put("Select", isChecked);
                }

                ((BKMap_MapFileAdapter) listViewGridMap.getAdapter()).notifyDataSetChanged();
            }
        });

        selectVertorDialog.findViewById(R.id.txSelectGridPath).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDictionary sd = new SelectDictionary(selectVertorDialog.getContext());
                sd.isSelectFolder = true;
                sd.SetCallback(new ICallback() {
                    @Override
                    public void OnClick(String Str, final Object ExtraStrT) {
                        List<String> folderList = (List<String>) ExtraStrT;
                        for (String importFile : folderList) {
                            m_VetorMapFileList = Tools.GetBKMapListFromFolder(importFile, lkMapFileType.enVector);
                            BindMapFileListToView(m_VetorMapFileList, listViewGridMap);
                        }


                    }
                });
                Log.d("SelectDictionary", PubVar.m_SysAbsolutePath);
                sd.ShowDialog(PubVar.m_SysAbsolutePath);
            }
        });

        //坐标系统
        CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
        String CoorSystemInfo = CS.GetName() + "【" + CS.GetCenterMeridian() + "】";
        if (CS.GetName().equals("WGS-84坐标")) {
            CoorSystemInfo = CS.GetName();
        }
        Tools.SetTextViewValueOnID(selectVertorDialog, R.id.et_prjInfo, CoorSystemInfo);


        this.m_VetorMapFileList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetBKFileList();

        if (m_VetorMapFileList != null && m_VetorMapFileList.size() > 0) {
            mSelectCoor = spCoordinateType.getSelectedItem().toString();
            mSelectCenter = spCentralMeridian.getSelectedItem().toString();
            for (HashMap<String, Object> hashMap : m_VetorMapFileList) {
                String coor = hashMap.get("CoorSystem").toString();
                if (coor.contains(mSelectCenter) && coor.contains(mSelectCoor)) {
                    BindMapFileListToView(this.m_VetorMapFileList, listViewGridMap);
                }
            }
        }

        selectVertorDialog.show();

    }

    private void selectSaveVetor() {
        if (m_VetorMapFileList != null) {
            mSelectCoor = spCoordinateType.getSelectedItem().toString();
            mSelectCenter = spCentralMeridian.getSelectedItem().toString();
            List<HashMap<String, Object>> SelectMapFileList = new ArrayList<HashMap<String, Object>>();
            String filePath = "";
            for (HashMap<String, Object> mapFile : m_VetorMapFileList) {
                String coor = mapFile.get("CoorSystem").toString();
                if (coor.contains(mSelectCoor) && coor.contains(mSelectCenter)) {
                    if (Boolean.parseBoolean(mapFile.get("Select") + "")) {
                        filePath += mapFile.get("BKMapFile") + ";";
                        SelectMapFileList.add(mapFile);
                    }
                }

            }
            if (!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().SaveBKLayer("矢量", SelectMapFileList)) {
                Tools.ShowMessageBox("矢量底图保存失败！");
            } else {
                if(filePath!=null&& !TextUtils.isEmpty(filePath)) {
                    if (filePath.endsWith(";")) {
                        filePath = filePath.substring(0, filePath.length() - 1);
                    }
                    vertorFileNames.setText(filePath);
                }
            }
        }

    }

    private void selectGridMap() {
        startRoundDialog = new Dialog(MapSettingActivity.this);
        startRoundDialog.setContentView(R.layout.dialog_selectgridmap);
        startRoundDialog.setTitle("选择栅格底图");

        gridFileNames = (EditText) this.findViewById(R.id.etGridMap);
        startRoundDialog.findViewById(R.id.text_gridcancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRoundDialog.dismiss();
            }
        });
        startRoundDialog.findViewById(R.id.text_gridsave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSaveGrid();
                startRoundDialog.dismiss();
            }
        });

        final ListView listViewGridMap = (ListView) startRoundDialog.findViewById(R.id.listViewGridMap);

        CheckBox cbSelectGridAll = (CheckBox) startRoundDialog.findViewById(R.id.cbselectgridall);
        cbSelectGridAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (m_GridMapFileList == null) return;
                for (HashMap<String, Object> hmObj : m_GridMapFileList) {
                    hmObj.put("Select", isChecked);
                }

                ((BKMap_MapFileAdapter) listViewGridMap.getAdapter()).notifyDataSetChanged();
            }
        });

        startRoundDialog.findViewById(R.id.txSelectGridPath).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectDictionary sd = new SelectDictionary(startRoundDialog.getContext());
                sd.isSelectFolder = true;
                sd.SetCallback(new ICallback() {
                    @Override
                    public void OnClick(String Str, final Object ExtraStrT) {
                        List<String> folderList = (List<String>) ExtraStrT;
                        for (String importFile : folderList) {
                            m_GridMapFileList = Tools.GetBKMapListFromFolder(importFile, lkMapFileType.enGrid);
                            BindMapFileListToView(m_GridMapFileList, listViewGridMap);
                        }


                    }
                });
                Log.d("SelectDictionary", PubVar.m_SysAbsolutePath);
                sd.ShowDialog(PubVar.m_SysAbsolutePath);
            }
        });

        //坐标系统
        CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
        String CoorSystemInfo = CS.GetName() + "【" + CS.GetCenterMeridian() + "】";
        if (CS.GetName().equals("WGS-84坐标")) {
            CoorSystemInfo = CS.GetName();
        }
        Tools.SetTextViewValueOnID(startRoundDialog, R.id.et_prjInfo, CoorSystemInfo);

        this.m_GridMapFileList = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetGridLayerExplorer().GetBKFileList();

        if (m_GridMapFileList != null && m_GridMapFileList.size() > 0) {
            mSelectCoor = spCoordinateType.getSelectedItem().toString();
            mSelectCenter = spCentralMeridian.getSelectedItem().toString();
            for (HashMap<String, Object> hashMap : m_GridMapFileList) {
                String coor = hashMap.get("CoorSystem").toString();
                if (coor.contains(mSelectCenter) && coor.contains(mSelectCoor)) {
                    BindMapFileListToView(this.m_GridMapFileList, listViewGridMap);
                }
            }
        }
        startRoundDialog.show();
    }

    private void selectSaveGrid() {
        if (m_GridMapFileList != null) {
            mSelectCoor = spCoordinateType.getSelectedItem().toString();
            mSelectCenter = spCentralMeridian.getSelectedItem().toString();
            List<HashMap<String, Object>> SelectMapFileList = new ArrayList<HashMap<String, Object>>();
            String filePath = "";
            for (HashMap<String, Object> mapFile : m_GridMapFileList) {
                String coor = mapFile.get("CoorSystem").toString();
                if (coor.contains(mSelectCoor) && coor.contains(mSelectCenter)) {
                    if (Boolean.parseBoolean(mapFile.get("Select") + "")) {
                        filePath += mapFile.get("BKMapFile") + ";";
                        SelectMapFileList.add(mapFile);
                    }
                }
            }
            if (!PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().SaveBKLayer("栅格", SelectMapFileList)) {
                Tools.ShowMessageBox("栅格底图保存失败！");
            } else {
                if(filePath!=null&&!TextUtils.isEmpty(filePath)) {
                    if (filePath.endsWith(";")) {
                        filePath = filePath.substring(0, filePath.length() - 1);
                    }
                    gridFileNames.setText(filePath);
                }
            }
        }
    }

    private void BindMapFileListToView(List<HashMap<String, Object>> mapFileList, ListView listViewGridMap) {
        for (HashMap<String, Object> ho : mapFileList) {
            if (!ho.containsKey("Select")) ho.put("Select", true);
        }
        //刷新列表
        if (mapFileList == null) mapFileList = new ArrayList<HashMap<String, Object>>();
        BKMap_MapFileAdapter adapter = new BKMap_MapFileAdapter(this.getApplicationContext(), mapFileList,
                R.layout.select_bkmap_listitem,
                new String[]{"Select", "BKMapFile", "F1"},
                new int[]{R.id.cbselect, R.id.tvname, R.id.tvcoorsystem});
        listViewGridMap.setAdapter(adapter);
    }


    public class BKMap_MapFileAdapter extends BaseAdapter {
        //当前选中的项目索引
        private int m_SelectItemIndex = -1;
        private List<HashMap<String, Object>> m_DataList = null;
        private int m_LayoutId = 0;
        private String[] m_ObjField;
        private int[] m_ViewId;
        //回调
        private ICallback m_Callback = null;
        private LayoutInflater mInflater = null;


        public BKMap_MapFileAdapter(Context context, List<HashMap<String, Object>> list, int layoutid, String[] objField, int[] viewid) {
            if (this.mInflater == null) this.mInflater = LayoutInflater.from(context);
            this.m_DataList = list;
            this.m_LayoutId = layoutid;
            this.m_ObjField = objField;
            this.m_ViewId = viewid;
        }

        public void SetSelectItemIndex(int idx) {
            this.m_SelectItemIndex = idx;
        }

        public void SetCallback(ICallback cb) {
            this.m_Callback = cb;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return this.m_DataList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub

            return this.m_DataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(this.m_LayoutId, null);
            }

            HashMap<String, Object> obj = (HashMap<String, Object>) this.getItem(position);
            for (int i = 0; i < this.m_ViewId.length; i++) {
                //显示可用列
                View v = convertView.findViewById(this.m_ViewId[i]);
                v.setTag(obj);
                //分情况赋值
                String VType = v.getClass().getName();
                if (VType.equals("android.widget.TextView")) {
                    TextView tv = (TextView) v;
                    if (obj.get(this.m_ObjField[i]) != null) {
                        tv.setText(obj.get(this.m_ObjField[i]).toString());
                    }

                }
                if (VType.equals("android.widget.CheckBox")) {

                    CheckBox cb = (CheckBox) v;
                    cb.setTag(position + "," + i);
                    cb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View vv) {
                            CheckBox ccbb = (CheckBox) vv;
                            UpdateCheckBoxDataListValue(ccbb, ccbb.getTag().toString());
                        }
                    });
                    cb.setChecked(Boolean.parseBoolean(obj.get(this.m_ObjField[i]).toString()));
                }
                if (VType.equals("android.widget.ImageButton")) {
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (m_Callback != null) m_Callback.OnClick("ButtonClick", v.getTag());
                        }
                    });
                }
            }

            //选中项目的突出显示
            if (position == this.m_SelectItemIndex) {
                convertView.setSelected(true);
                convertView.setPressed(true);
                convertView.setBackgroundColor(Color.BLUE);
            } else convertView.setBackgroundColor(Color.TRANSPARENT);
            return convertView;
        }

        //设置CheckBox单击事件值
        private boolean UpdateCheckBoxDataListValue(CheckBox cb, String posInfo) {
            String[] psoInfo = posInfo.split(",");
            int posIdx = Integer.parseInt(psoInfo[0]);
            int objId = Integer.parseInt(psoInfo[1]);
            HashMap<String, Object> obj = (HashMap<String, Object>) this.getItem(posIdx);
            boolean value = Boolean.parseBoolean(obj.get(this.m_ObjField[objId]).toString());
            obj.put(this.m_ObjField[objId], !value);
            return !value;
        }
    }
}
