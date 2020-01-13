package com.example.dingtu2.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.dingtu2.myapplication.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class JieShaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jie_shao);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("保护区介绍");
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_zhoubao, R.id.tv_anjiaqi, R.id.tv_banfangzi, R.id.tv_houzhenzi, R.id.tv_xiaowangjian, R.id.tv_shuangmiaozi,
            R.id.tv_banfangzipaichusuo, R.id.tv_shuangmiaozipaichusuo, R.id.tv_hubaohe, R.id.tv_huangcaopo})
    public void clickBtn(View view) {
        Intent intent = new Intent(JieShaoActivity.this, ZhouBaoJieShaoActivity.class);

        switch (view.getId()) {
            case R.id.tv_zhoubao:
                intent.putExtra("PhotoName", R.mipmap.zhoubao_internet);
                intent.putExtra("FileName", "zhoubao_jieshao.html");
                intent.putExtra("Title", "陕西周至国家级自然保护区");
                startActivity(intent);
                break;
            case R.id.tv_anjiaqi:
                intent.putExtra("PhotoName", R.mipmap.zhoubao_internet);
                intent.putExtra("FileName", "anjiaqi_bhz.html");
                intent.putExtra("Title", "安家岐保护站");
                startActivity(intent);
                break;
            case R.id.tv_banfangzi:
                intent.putExtra("PhotoName", R.mipmap.bhz_banfangzi);
                intent.putExtra("FileName", "bhz_bafangzi.html");
                intent.putExtra("Title", "板房子保护站");
                startActivity(intent);
                break;
            case R.id.tv_houzhenzi:
                intent.putExtra("PhotoName", R.mipmap.bhz_shouzhenzi);
                intent.putExtra("FileName", "bhz_houzhenzi.html");
                intent.putExtra("Title", "厚畛子保护站");
                startActivity(intent);
                break;
            case R.id.tv_xiaowangjian:
                intent.putExtra("PhotoName", R.mipmap.bhz_xiaowangjian);
                intent.putExtra("FileName", "bhz_xiaowangjian.html");
                intent.putExtra("Title", "小王涧保护站");
                startActivity(intent);
                break;
            case R.id.tv_shuangmiaozi:
                intent.putExtra("PhotoName", R.mipmap.shuangmiaozi_bhz);
                intent.putExtra("FileName", "bhz_shuangmiaozi.html");
                intent.putExtra("Title", "双庙子保护站");
                startActivity(intent);
                break;
            case R.id.tv_banfangzipaichusuo:
                intent.putExtra("PhotoName", R.mipmap.pcs_banfangzi);
                intent.putExtra("FileName", "pcs_banfangzi.html");
                intent.putExtra("Title", "板房子森林派出所");
                startActivity(intent);
                break;
            case R.id.tv_shuangmiaozipaichusuo:
                intent.putExtra("PhotoName", R.mipmap.pcs_shuangmiaozi);
                intent.putExtra("FileName", "pcs_shuangmiaozi.html");
                intent.putExtra("Title", "双庙子森林派出所");
                startActivity(intent);
                break;
            case R.id.tv_hubaohe:
                intent.putExtra("PhotoName", R.mipmap.jsz_hubaohe);
                intent.putExtra("FileName", "jcz_hubaohe.html");
                intent.putExtra("Title", "虎豹河护林检查站");
                startActivity(intent);
                break;
            case R.id.tv_huangcaopo:
                intent.putExtra("PhotoName", R.mipmap.jcz_huangcaopo);
                intent.putExtra("FileName", "jcz_huangcaopo.html");
                intent.putExtra("Title", "黄草坡护林检查站");
                startActivity(intent);
                break;
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
}
