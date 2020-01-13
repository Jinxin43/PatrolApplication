package com.example.dingtu2.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.DingTu.Base.ICallback;
import com.example.dingtu2.myapplication.db.XDbManager;
import com.example.dingtu2.myapplication.db.entity.PatrolCommandEntity;

import java.util.List;

public class PatrolCommandActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_command);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Log.d("PatrolCommandActivity", "start");
        ListView listView = (ListView) findViewById(R.id.listViewCommand);
        BindListToView(listView);
    }

    private void BindListToView(ListView listViewGridMap) {
        List<PatrolCommandEntity> allCommand;
        try {
//            DbManager db = x.getDb(AppSetting.getDbConfig());
            allCommand = XDbManager.getDb().findAll(PatrolCommandEntity.class);
            Log.d("巡护指令", allCommand.size() + "");
        } catch (Exception ex) {
            Log.e("查询巡护指令", ex.getMessage());
            Toast.makeText(this, "查询巡护指令失败", Toast.LENGTH_SHORT).show();
            return;
        }


        PatrolCommandAdapter adapter = new PatrolCommandAdapter(this.getApplicationContext(), allCommand,
                R.layout.patrol_command_listitem,
                new String[]{"ID", "title", "Description"},
                new int[]{R.id.txViewCommand, R.id.txStartPatr, R.id.tvCoomandId, R.id.tvTitle, R.id.tvCommandDescript});
        listViewGridMap.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        Intent intent = new Intent(PatrolCommandActivity.this, MainActivity.class);
        intent.putExtras(data.getExtras());

        if (resultCode == 2)//newPatrol
        {
            intent.putExtra("from", "newPatrol");
            intent.putExtra("patrolID", data.getStringExtra("patrolID"));

            setResult(2, intent);
            finish();
        }
        if (resultCode == 1) {
            intent.putExtra("from", "patrolCenter");
            intent.putExtra("patrolCenter", data.getStringExtra("patrolCenter"));
            setResult(1, intent);
            finish();
        }
    }

    public class PatrolCommandAdapter extends BaseAdapter {
        //当前选中的项目索引
        private int m_SelectItemIndex = -1;
        private List<PatrolCommandEntity> m_DataList = null;
        private int m_LayoutId = 0;
        private String[] m_ObjField;
        private int[] m_ViewId;
        //回调
        private ICallback m_Callback = null;
        private LayoutInflater mInflater = null;


        public PatrolCommandAdapter(Context context, List<PatrolCommandEntity> list, int layoutid, String[] objField, int[] viewid) {
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
        public PatrolCommandEntity getItem(int position) {
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

            final PatrolCommandEntity obj = this.getItem(position);
            for (int i = 0; i < this.m_ViewId.length; i++) {
                //显示可用列
                View v = convertView.findViewById(this.m_ViewId[i]);
//                v.setTag(obj);
                //分情况赋值
                String VType = v.getClass().getName();

                if (VType.equals("android.widget.TextView")) {
                    TextView tv = (TextView) v;

                    if (m_ViewId[i] == R.id.txViewCommand) {
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle patrolBuddle = new Bundle();
                                String id = obj.getGeomId();
                                patrolBuddle.putString("from", "DB");
                                patrolBuddle.putString("Id", id);
                                Intent i = new Intent(PatrolCommandActivity.this, com.example.dingtu2.myapplication.SpcialPatrolActivity.class);
                                i.putExtras(patrolBuddle);
//                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                PatrolCommandActivity.this.startActivityForResult(i, 2);
                            }
                        });
                    } else if (m_ViewId[i] == R.id.txStartPatr) {
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (AppSetting.curRound != null) {
                                    Toast.makeText(PatrolCommandActivity.this, "正在巡护中，请先结束当前巡护再开始指令巡护！", Toast.LENGTH_LONG).show();
                                } else {
                                    Intent mainIntent = new Intent(PatrolCommandActivity.this, com.example.dingtu2.myapplication.SpcialPatrolActivity.class);
                                    mainIntent.putExtra("from", "newPatrol");
                                    mainIntent.putExtra("patrolID", obj.getGeomId());

                                    //指定巡护区域中心点
//                                    if(mGeoCenter != null)
//                                    {
//                                        mainIntent.putExtra("patrolCenter",new double[]{mGeoCenter.getX(),mGeoCenter.getY()});
//                                    }
                                    //resultCode 2:新的巡护，1：查看巡护位置
                                    setResult(2, mainIntent);
                                    finish();
                                }

                            }
                        });

                    } else {
                        if (m_ViewId[i] == R.id.tvTitle) {
                            if (obj.getTitle() != null) {
                                tv.setText(obj.getTitle());
                            } else {
                                tv.setText("指令巡护");
                            }

                        }
                        if (m_ViewId[i] == R.id.tvCommandDescript) {
                            if (obj.getPatrolDescription() != null) {
                                tv.setText(obj.getPatrolDescription());
                            }
                        }
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
