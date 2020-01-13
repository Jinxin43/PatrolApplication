package com.example.dingtu2.myapplication.controls;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.example.dingtu2.myapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/7/19.
 */

public class SelectDictionary {
    public FormTemplate _Dialog = null;

    public boolean isSelectFolder = true;
    //回调
    private ICallback m_Callback = null;
    private List<View> m_PathButtonList = new ArrayList<View>();
    //文件过滤器
    private String[] m_FileFilter = null;
    private DictionaryList_Adpter m_LayerList_Adpter = null;
    private List<HashMap<String, Object>> m_PathItemList = new ArrayList<HashMap<String, Object>>();
    //按钮事件
    private ICallback pCallback = new ICallback() {
        @Override
        public void OnClick(String Str, Object ExtraStr) {
            if (Str.equals("确定")) {
                List<String> SelectFileList = new ArrayList<String>();
                for (HashMap<String, Object> ho : m_PathItemList) {
                    if (Boolean.parseBoolean(ho.get("Select") + "")) {
                        SelectFileList.add(ho.get("PathFullName") + "");
                    }
                }
                if (SelectFileList.size() == 0) {
                    Tools.ShowMessageBox(_Dialog.getContext(), "请勾选需要的文件！");
                    return;
                }

                if (m_Callback != null) m_Callback.OnClick("", SelectFileList);
                _Dialog.dismiss();

            }
        }
    };

    public SelectDictionary(Context context) {
        _Dialog = new FormTemplate(context);
        _Dialog.SetOtherView(R.layout.dialog_selectdictionary);
        _Dialog.ReSetSize(0.99f, 0.8f);

        //设置标题
        _Dialog.SetCaption(Tools.ToLocale("选择目录"));
        _Dialog.SetButtonInfo("1," + R.drawable.icon_title_comfirm + "," + Tools.ToLocale("确定") + " ,确定", pCallback);

        _Dialog.findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout view = (LinearLayout) _Dialog.findViewById(R.id.ll_pathbutton);
                if (view.getChildCount() > 1) {
                    View vv = view.getChildAt(view.getChildCount() - 2);
                    Button bt = (Button) vv.findViewById(R.id.bt_path);
                    bt.performClick();
                }
            }
        });

        View view = ((LinearLayout) _Dialog.findViewById(R.id.ll_pathbutton)).getChildAt(0);
        view.findViewById(R.id.bt_path).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Purge(v);
                LoadDictionary("");
            }
        });
    }

    /**
     * 回调
     *
     * @param cb
     */
    public void SetCallback(ICallback cb) {
        this.m_Callback = cb;
    }

    private void AddPathButton(String pathFullName) {
        LinearLayout view = (LinearLayout) _Dialog.findViewById(R.id.ll_pathbutton);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater3 = LayoutInflater.from(_Dialog.getContext());
        View newView = inflater3.inflate(R.layout.selectdictionary_header, null);
        newView.setLayoutParams(lp);
        Button bt = (Button) newView.findViewById(R.id.bt_path);
        bt.setText(GetPathName(pathFullName));
        bt.setTag(pathFullName);
        newView.setTag(pathFullName);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Purge(v);
                LoadDictionary(v.getTag() + "");
            }
        });
        view.addView(newView);
    }

    private void Purge(View v) {
        int Pos = -1;
        LinearLayout viewT = (LinearLayout) _Dialog.findViewById(R.id.ll_pathbutton);
        for (int i = 0; i < viewT.getChildCount(); i++) {
            View v1 = viewT.getChildAt(i);
            if (v1.getTag().toString().equals(v.getTag().toString())) Pos = i;
        }
        int ViewCount = viewT.getChildCount();
        for (int i = ViewCount - 1; i > Pos; i--) {
            viewT.removeViewAt(i);
        }
    }

    /**
     * 设置文件过滤器
     *
     * @param fileFilter
     */
    public void SetFileFilter(String[] fileFilter) {
        this.m_FileFilter = fileFilter;
    }

    /**
     * 加载图层列表信息
     */
    private void LoadDictionary(String parentPath) {
        this.m_PathItemList.clear();
        if (parentPath.equals("")) {
            List<HashMap<String, Object>> parentPathList = Tools.GetAllSDCardInfoList(PubVar.m_DoEvent.m_Context);
            Log.d("GetAllSDCardInfoList", parentPathList.size() + "");
            int xh = 1;
            for (HashMap<String, Object> ho : parentPathList) {
                ho.put("PathName", "存储卡" + xh);
                xh++;
                ho.put("PathFullName", ho.get("SDPath"));
                ho.put("Image", null);
                ho.put("Type", "目录");
                ho.put("Select", false);
                this.m_PathItemList.add(ho);
            }
        } else {
            File f = new File(parentPath);
            File[] files = f.listFiles();// 列出所有文件
            for (File ff : files) {
                if (ff.isDirectory()) {
                    String FileName = ff.getAbsolutePath();
                    HashMap<String, Object> ho = new HashMap<String, Object>();
                    ho.put("PathName", GetPathName(FileName));
                    Log.d("PathName", GetPathName(FileName));
                    ho.put("PathFullName", FileName);
                    ho.put("Image", null);
                    ho.put("Type", "目录");
                    ho.put("Select", false);
                    this.m_PathItemList.add(ho);
                }
            }
            for (File ff : files) {
                if (this.m_FileFilter == null) break;
                if (ff.isFile()) {
                    String FileName = ff.getAbsolutePath();

                    HashMap<String, Object> ho = new HashMap<String, Object>();
                    ho.put("PathName", GetPathName(FileName));
                    ho.put("PathFullName", FileName);
                    ho.put("Image", null);
                    ho.put("Type", "文件");
                    ho.put("Select", false);
                    if (FileName.length() < 4) continue;
                    int dotIndex = FileName.lastIndexOf(".");
                    if (dotIndex <= 0 || dotIndex == FileName.length()) {
                        continue;
                    }
                    FileName = FileName.substring(dotIndex + 1, FileName.length()).toUpperCase();
                    for (String filter : this.m_FileFilter) {
                        if (filter.toUpperCase().equals(FileName)) {
                            if (FileName.contains("VMX"))
                                ho.put("Image", Tools.GetBitmapByResources(R.drawable.v1_file_vmx));
                            if (FileName.contains("IMX"))
                                ho.put("Image", Tools.GetBitmapByResources(R.drawable.v1_file_imx));
                            if (FileName.contains("SHP"))
                                ho.put("Image", Tools.GetBitmapByResources(R.drawable.v1_file_shp));
                            if (ho.get("Image") == null)
                                ho.put("Image", Tools.GetBitmapByResources(R.drawable.v1_file_bank));
                        }

                    }

                    if (ho.get("Image") == null) {
                        continue;
                    }
                    this.m_PathItemList.add(ho);
                }
            }
        }

        if (this.m_LayerList_Adpter == null) {
            if (isSelectFolder) {
                this.m_LayerList_Adpter = new DictionaryList_Adpter(_Dialog.getContext(),
                        this.m_PathItemList,
                        R.layout.bk_dictionary_item,
                        new String[]{"PathName", "Image", "Select"},
                        new int[]{R.id.tv_sdpath, R.id.iv_sdimage, R.id.rb_sdselect});
            } else {
                this.m_LayerList_Adpter = new DictionaryList_Adpter(_Dialog.getContext(),
                        this.m_PathItemList,
                        R.layout.bk_dictionary_item,
                        new String[]{"PathName", "Image", "Select"},
                        new int[]{R.id.tv_sdpath, R.id.iv_sdimage, R.id.cb_sdselect});
            }

        }

        ListView lvList = (ListView) _Dialog.findViewById(R.id.lvList);
        lvList.setAdapter(this.m_LayerList_Adpter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ListView lvList = (ListView) arg0;
                DictionaryList_Adpter la = (DictionaryList_Adpter) lvList.getAdapter();
                HashMap<String, Object> ho = m_PathItemList.get(arg2);
                if (ho.get("Type").equals("文件")) return;
                String FullName = ho.get("PathFullName") + "";
                LoadDictionary(FullName);
                AddPathButton(FullName);
            }
        });
    }

    private String GetPathName(String fullName) {
        String[] pathInfo = fullName.split("/");
        return pathInfo[pathInfo.length - 1];
    }

    public void ShowDialog(final String currentPath) {
        //此处这样做的目的是为了计算控件的尺寸
        _Dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                      @Override
                                      public void onShow(DialogInterface dialog) {
                                          LoadDictionary(currentPath);
                                          AddPathButton(currentPath);

                                      }
                                  }
        );
        _Dialog.show();
    }

    class DictionaryList_Adpter extends BaseAdapter {
        private int index = -1;
        //当前选中的项目索引
        private int m_SelectItemIndex = -1;
        private List<HashMap<String, Object>> m_DataList = null;
        private int m_LayoutId = 0;
        private String[] m_ObjField;
        private int[] m_ViewId;
        //回调
        private ICallback m_Callback = null;
        private LayoutInflater mInflater = null;

        public DictionaryList_Adpter(Context context, List<HashMap<String, Object>> list, int layoutid, String[] objField, int[] viewid) {
            if (this.mInflater == null) this.mInflater = LayoutInflater.from(context);
            this.m_DataList = list;
            this.m_LayoutId = layoutid;
            this.m_ObjField = objField;
            this.m_ViewId = viewid;
        }

        public void SetSelectItemIndex(int idx) {
            this.m_SelectItemIndex = idx;
        }

        public List<HashMap<String, Object>> GetDataList() {
            return this.m_DataList;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(this.m_LayoutId, null);
            }

            final HashMap<String, Object> obj = (HashMap<String, Object>) this.getItem(position);
            for (int i = 0; i < this.m_ViewId.length; i++) {
                //显示可用列
                View v = convertView.findViewById(this.m_ViewId[i]);

                //分情况赋值
                String VType = v.getClass().getName();
                Log.d("ViewType", VType);
                //if (VType.equals("android.widget.TextView"))
                if (VType.equals("android.support.v7.widget.AppCompatTextView")) {
                    TextView tv = (TextView) v;
                    tv.setText(obj.get(this.m_ObjField[i]).toString());
                }
                //if (VType.equals("android.widget.CheckBox"))
                if (VType.equals("android.support.v7.widget.AppCompatCheckBox")) {
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
                    if (obj.get("Type").equals("目录")) cb.setVisibility(View.INVISIBLE);
                    if (obj.get("Type").equals("文件")) cb.setVisibility(View.VISIBLE);
                }

                //if (VType.equals("android.widget.RadioButton"))
                if (VType.equals("android.support.v7.widget.AppCompatRadioButton")) {
                    RadioButton rb = (RadioButton) v;
                    rb.setTag(position + "," + i);

                    rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                index = position;
                            }

                            RadioButton ccbb = (RadioButton) buttonView;
                            UpdateRaidoButtionDataListValue(ccbb, ccbb.getTag().toString());
                            notifyDataSetChanged();
                        }
                    });

                    if (position == index) {
                        rb.setChecked(true);
                    } else {
                        rb.setChecked(false);
                    }
                    //rb.setChecked(Boolean.parseBoolean(obj.get(this.m_ObjField[i]).toString()));
                    Log.d("PathType", obj.get("Type") + "");
                    if (obj.get("Type").equals("目录")) rb.setVisibility(View.VISIBLE);
                    if (obj.get("Type").equals("文件")) rb.setVisibility(View.INVISIBLE);
                }

//                if (VType.equals("android.widget.ImageView"))
                if (VType.equals("android.support.v7.widget.AppCompatImageView")) {
                    ImageView iv = (ImageView) v;
                    if (i < this.m_ObjField.length) {
                        if (obj.get(this.m_ObjField[i]) != null)
                            iv.setImageBitmap((Bitmap) obj.get(this.m_ObjField[i]));
                    }
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

        private boolean UpdateRaidoButtionDataListValue(RadioButton rb, String posInfo) {
            String[] psoInfo = posInfo.split(",");
            int posIdx = Integer.parseInt(psoInfo[0]);
            int objId = Integer.parseInt(psoInfo[1]);
            HashMap<String, Object> obj = (HashMap<String, Object>) this.getItem(posIdx);
//		boolean value = Boolean.parseBoolean(obj.get(this.m_ObjField[objId]).toString());
            obj.put("Select", rb.isChecked());
            return true;
        }
    }
}