package com.example.dingtu2.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.dingtu2.myapplication.utils.Utils;

import java.util.HashMap;
import java.util.List;

public class PatrolListActivity extends AppCompatActivity implements View.OnClickListener {


    private CardView mCardView, mManagerView,mAllCardView;
    private List<HashMap<String, Object>> m_GridMapFileList = null;
    private List<HashMap<String, Object>> m_VetorMapFileList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_list);
        setupActionBar();
        initView();
    }

    private void initView() {
        mCardView=(CardView)findViewById(R.id.card_my_view);
        mManagerView=(CardView)findViewById(R.id.card_manager_view);
        mAllCardView=(CardView)findViewById(R.id.card_all_view);
        mCardView.setOnClickListener(this);
        mManagerView.setOnClickListener(this);
        mAllCardView.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.card_my_view:
                Intent intent=new Intent(PatrolListActivity.this, PatrolMyActivity.class);
                intent.putExtra("Type","my");
                startActivity(intent);
                break;
            case R.id.card_manager_view:
                if(Utils.isNetworkAvailable(this)){
                    startActivity(new Intent(PatrolListActivity.this, PatrolManagerActivity.class));
                }else{
                  Toast.makeText(getApplicationContext(),"网络没有连接，请开启后查看！",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.card_all_view:
                Intent intents=new Intent(PatrolListActivity.this, PatrolMyActivity.class);
                intents.putExtra("Type","all");
                startActivity(intents);
                break;
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("巡护记录");
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
