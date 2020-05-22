package com.example.dingtu2.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.DingTu.Base.ICallback;
import com.example.dingtu2.myapplication.db.xEntity.PatrolPointEntity;
import com.example.dingtu2.myapplication.manager.PatrolManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PointListActivity extends AppCompatActivity {

    private String mPatrolId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);
        setupActionBar();
        mPatrolId = this.getIntent().getStringExtra("patrolId");
        ListView listView = (ListView) findViewById(R.id.listViewPoints);
        BindListToView(listView);
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("基础巡点列表");
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

    private void BindListToView(ListView listViewGridMap) {
        List<PatrolPointEntity> allPoints = new ArrayList<PatrolPointEntity>();
        try {
//            DbManager db = x.getDb(AppSetting.getDbConfig());
            allPoints = PatrolManager.getInstance().getPatrolPoints(mPatrolId);
        } catch (Exception ex) {
            Toast.makeText(this, "查询基础巡点失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (allPoints == null) {
            allPoints = new ArrayList<PatrolPointEntity>();
        }

     PointListAdapter adapter = new PointListAdapter(this.getApplicationContext(), allPoints,
                R.layout.fragment_pointlist_item,
                new String[]{"ID", "pointType", "gpsTime", "uploadStatus", "longitude", "latitude", "height"},
                new int[]{R.id.tvPointType, R.id.tvPointTime, R.id.tvUploadStatus, R.id.tvLongitude, R.id.tvLatitude, R.id.tvHeight});
        listViewGridMap.setAdapter(adapter);
    }

    public class PointListAdapter extends BaseAdapter {
        //当前选中的项目索引
        private int m_SelectItemIndex = -1;
        private List<PatrolPointEntity> m_DataList = null;
        private int m_LayoutId = 0;
        private String[] m_ObjField;
        private int[] m_ViewId;
        //回调
        private ICallback m_Callback = null;
        private LayoutInflater mInflater = null;


        public PointListAdapter(Context context, List<PatrolPointEntity> list, int layoutid, String[] objField, int[] viewid) {
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
            return this.m_DataList.size();
        }

        @Override
        public PatrolPointEntity getItem(int position) {

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

            final PatrolPointEntity obj = this.getItem(position);
            for (int i = 0; i < this.m_ViewId.length; i++) {
                //显示可用列
                View v = convertView.findViewById(this.m_ViewId[i]);
                //分情况赋值
                String VType = v.getClass().getName();

                if (VType.equals("android.widget.TextView")) {
                    TextView tv = (TextView) v;

                    if (m_ViewId[i] == R.id.tvUploadStatus) {
                        if (obj.getUploadStatus() == 0) {
                            tv.setText("未上传");
                        } else {
                            tv.setText("已上传");
                        }

                    } else if (m_ViewId[i] == R.id.tvPointType) {
                        if (obj.getPointType().equals("0")) {
                            tv.setText("起始点");
                        } else if (obj.equals("2")) {
                            tv.setText("结束点");
                        } else {
                            tv.setText("中间点");
                        }
                    } else if (m_ViewId[i] == R.id.tvPointTime) {
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss"); //HH表示24进制
                        String time = sDateFormat.format(obj.getGpsTime());
                        tv.setText(time);
                    } else if (m_ViewId[i] == R.id.tvLongitude) {
                        String value = obj.getLongitude() + "";
                        if (value.length() > 10) {
                            value = value.substring(0, 10);
                        }
                        tv.setText(value);
                    } else if (m_ViewId[i] == R.id.tvLatitude) {
                        String value = obj.getLatitude() + "";
                        if (value.length() > 10) {
                            value = value.substring(0, 10);
                        }
                        tv.setText(value);
                    } else if (m_ViewId[i] == R.id.tvHeight) {
                        tv.setText(obj.getHeight() + "");
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

    }
}
