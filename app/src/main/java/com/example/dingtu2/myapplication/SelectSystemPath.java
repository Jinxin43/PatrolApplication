package com.example.dingtu2.myapplication;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/6.
 */

public class SelectSystemPath {

    private Context m_Context = null;
    //回调
    private ICallback m_Callback = null;
    private List<HashMap<String, Object>> m_SDCardList = null;
    private AlertDialog m_AlertDialog = null;
    private View m_View = null;

    public SelectSystemPath(Context C) {
        this.m_Context = C;
    }

    public void SetCallback(ICallback cb) {
        this.m_Callback = cb;
    }

    /**
     * 加载存储卡信息
     */
    private void LoadSDCardInfo() {
        TextView tvName = (TextView) this.m_View.findViewById(R.id.tvSystemName);
        tvName.setText("请选择【" + PubVar.m_SysDictionaryName + "】工作目录位置");
        this.m_SDCardList = Tools.GetAllSDCardInfoList(this.m_Context);

        //刷新列表
        SystemPathAdapter adapter = new SystemPathAdapter(this.m_Context, this.m_SDCardList,
                R.layout.v1_bk_selectsystempath,
                new String[]{"SDPath", "AllSize", "FreeSize", "Status"},
                new int[]{R.id.tvPath, R.id.tvAllSize, R.id.tvFreeSize, R.id.tvStatus});
        (((ListView) this.m_View.findViewById(R.id.listView1))).setAdapter(adapter);

        (((ListView) this.m_View.findViewById(R.id.listView1))).setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final String SDPath = m_SDCardList.get(arg2).get("SDPath") + "";
                //验证是否可创建目录
                if (!(new File(SDPath)).canWrite()) {
                    Tools.ShowMessageBox(m_Context, "创建工作目录失败！\n\n原因：目录为只读状态！");
                    return;
                }
                Tools.ShowYesNoMessage(m_View.getContext(), "是否确定在以下位置创建工作目录？\r\n" + SDPath, new ICallback() {
                    @Override
                    public void OnClick(String Str, Object ExtraStr) {
                        if (Str.equals("YES")) {
                            //创建主目录
                            String SystemPath = SDPath + "/" + PubVar.m_SysDictionaryName;

                            if (new File(SystemPath).mkdirs()) {
                                if (m_Callback != null) m_Callback.OnClick("工作目录", SDPath);
                                m_AlertDialog.dismiss();
                            } else {
                                if (m_Callback != null) m_Callback.OnClick("退出", "");
                            }

                            m_AlertDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    public void ShowDialog() {
        LayoutInflater inflater = (LayoutInflater) this.m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.m_View = inflater.inflate(R.layout.system_selectsystempath, null, false);

        AlertDialog.Builder ab = new AlertDialog.Builder(this.m_Context);
        ab.setTitle("工作目录设置");
        ab.setIcon(R.drawable.messageinfo);
        ab.setView(this.m_View);
        ab.setCancelable(false);
        ((RelativeLayout) this.m_View.findViewById(R.id.btquit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_Callback != null) m_Callback.OnClick("退出", "");
            }
        });
        m_AlertDialog = ab.create();
        m_AlertDialog.show();

        this.LoadSDCardInfo();

    }
}
