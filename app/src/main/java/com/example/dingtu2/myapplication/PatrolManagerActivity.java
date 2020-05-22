package com.example.dingtu2.myapplication;

import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dingtu2.myapplication.View.RecyclerViewSpacesItemDecoration;
import com.example.dingtu2.myapplication.adapter.PatroManagerAdapter;
import com.example.dingtu2.myapplication.http.RetrofitHttp;
import com.example.dingtu2.myapplication.manager.UserManager;
import com.example.dingtu2.myapplication.model.GetRoutelineBean;
import com.example.dingtu2.myapplication.model.ManagerBean;
import com.example.dingtu2.myapplication.utils.onLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dingtu2.myapplication.Login.LoginActivity.Login;
import static com.example.dingtu2.myapplication.Login.LoginActivity.LoginKey;


public class PatrolManagerActivity extends AppCompatActivity {

    private RecyclerView mRecyView;
    private PatroManagerAdapter mPatroAdapter;
    private int i = 1;
    private List<ManagerBean> mRouteBean;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_official);
        setupActionBar();
        initView();
    }
    private void initView() {
        mRouteBean = new ArrayList<>();
        mRecyView = (RecyclerView) findViewById(R.id.list);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        //设置下拉时圆圈的颜色（可以尤多种颜色拼成）
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
        //设置下拉时圆圈的背景颜色（这里设置成白色）
        mSwipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        //仅设置每个item的底部间隔为3
        mRecyView.addItemDecoration(new RecyclerViewSpacesItemDecoration(10));
        mRecyView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPatroAdapter = new PatroManagerAdapter(this,"manager");
        mRecyView.setAdapter(mPatroAdapter);


        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getdata(true, false);
            }
        });

        mRecyView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                getdata(false, true);
                // contentLoadingProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }
    //
//
//
    @Override
    protected void onStart() {
        super.onStart();
        getdata(false, false);
    }

    private void getdata(final boolean isRegresh, final boolean isLoadmore) {
        if (!isLoadmore) {
            if (mRouteBean != null && mRouteBean.size() > 0) {
                mRouteBean.clear();
            }
            i = 1;
        }else{
            mPatroAdapter.contentLoadingProgressBar.setVisibility(View.VISIBLE);
            i++;
        }
        SharedPreferences preferences = getSharedPreferences(Login, MODE_PRIVATE);
        final String loginKey = preferences.getString(LoginKey, null);
        AppSetting.curUserKey = loginKey;
        AppSetting.curUser = UserManager.getInstance().getLoginUser(AppSetting.curUserKey);
        GetRoutelineBean bean = new GetRoutelineBean();
        if (AppSetting.curUser != null) {
            bean.setAccount(AppSetting.curUser.getLoginName());
        }
        bean.setStartPage(i);
        bean.setPageSize(10);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Call<ResponseBody> newTraceCall = RetrofitHttp.getRetrofit(builder.build()).GetRouteManagerLine("GetPatrolsByDepartment", bean);
        newTraceCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() == null) {
                        return;
                    }
                    JSONObject result = new JSONObject(response.body().string());
                    String data = result.getString("data");
                    JSONArray array = new JSONArray(data);
                    if(array!=null&&array.length()>0){
                        for (int i = 0; i < array.length(); i++) {
                            ManagerBean bean = new ManagerBean();
                            JSONObject json = (JSONObject) array.get(i);
                            bean.setId(json.getString("id"));
                            bean.setUsername(json.getString("username"));
                            bean.setPatrolusername(json.getString("patrolusername"));
                            bean.setCreateuserid(json.getString("createuserid"));
                            bean.setStatus(json.getString("status"));
                            bean.setType(json.getInt("type"));
                            bean.setStarttime(json.getString("starttime"));
                            bean.setEndtime(json.getString("endtime"));
                            bean.setName(json.getString("name"));
                            bean.setDescription(json.getString("description"));
                            bean.setDistances(json.getString("distances"));
                            mRouteBean.add(bean);
                        }
                        mPatroAdapter.setData(mRouteBean, false);
                        if (isRegresh && !isLoadmore) {
                            if (mSwipeLayout.isRefreshing()) {
                                mSwipeLayout.setRefreshing(false);
                            }
                            Toast.makeText(getApplicationContext(), "刷新完毕", Toast.LENGTH_SHORT).show();
                        }
                        if( isLoadmore&&!isRegresh){
                            Toast.makeText(getApplicationContext(), "加载完毕", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "无更多数据", Toast.LENGTH_SHORT).show();
                        if( isLoadmore&&!isRegresh){
                            mPatroAdapter.contentLoadingProgressBar.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isRegresh && !isLoadmore) {
                    if (mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(false);
                    }
                }
                if( isLoadmore&&!isRegresh){
                    mPatroAdapter.contentLoadingProgressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getApplicationContext(), "网络不给力!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("巡护管理");
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
}
