package com.example.dingtu2.myapplication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dingtu2.myapplication.View.RecyclerViewSpacesItemDecoration;
import com.example.dingtu2.myapplication.adapter.PartroAdapter;
import com.example.dingtu2.myapplication.db.xEntity.PatrolEntity;
import com.example.dingtu2.myapplication.http.RetrofitHttp;
import com.example.dingtu2.myapplication.manager.PatrolManager;
import com.example.dingtu2.myapplication.manager.UserManager;
import com.example.dingtu2.myapplication.model.GetRoutelineBean;
import com.example.dingtu2.myapplication.model.ResultRouteBean;
import com.example.dingtu2.myapplication.utils.Utils;
import com.example.dingtu2.myapplication.utils.onLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dingtu2.myapplication.Login.LoginActivity.Login;
import static com.example.dingtu2.myapplication.Login.LoginActivity.LoginKey;

public class PatrolMyActivity extends AppCompatActivity {

    private RecyclerView mRecyView;
    private PartroAdapter mPatroAdapter;
    private int i = 1;
    private List<ResultRouteBean> mRouteBean;
    private SwipeRefreshLayout mSwipeLayout;
    private String mType;
    private GetRoutelineBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_online);
        mType = getIntent().getStringExtra("Type");
        initView();
    }

    @SuppressLint("WrongConstant")
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
        mPatroAdapter = new PartroAdapter(this, mType);
        mRecyView.setAdapter(mPatroAdapter);
        setupActionBar();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    getdata(true, false);
                } else {
                    if (mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(false);
                    }
                }
            }
        });

        mRecyView.addOnScrollListener(new onLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    getdata(false, true);
                } else {
                    if (mSwipeLayout.isRefreshing()) {
                        mSwipeLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Utils.isNetworkAvailable(this)) {
            getdata(false, false);
        } else {
            List<PatrolEntity> patrol = PatrolManager.getInstance().getPatrol();
            if (patrol != null && patrol.size() > 0) {
                for (int i = 0; i < patrol.size(); i++) {
                    if (patrol.get(i).getStartTime() != null && patrol.get(i).getEndTime() != null) {
                        ResultRouteBean bean = new ResultRouteBean();
                        bean.setId(patrol.get(i).getId());
                        bean.setName(patrol.get(i).getRoundName());
                        bean.setCreateUserId(patrol.get(i).getUserID());
                        bean.setPartrolLine(patrol.get(i).getLineOrZone());
                        bean.setDiscription(patrol.get(i).getContent());
                        bean.setStatus(patrol.get(i).getRoundStatus());
                        bean.setType(patrol.get(i).getRoundType());
                        bean.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(patrol.get(i).getStartTime()));
                        bean.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(patrol.get(i).getEndTime()));
                        bean.setPhotos(patrol.get(i).getPhotos());
                        bean.setWeather(patrol.get(i).getWeather());
                        bean.setPatrolUserName(patrol.get(i).getUserNames());
                        mRouteBean.add(bean);
                    }
                }
                mPatroAdapter.setData(mRouteBean);
            }

        }

    }


    private void getdata(final boolean isRegresh, final boolean isLoadmore) {
        if (!isLoadmore) {
            if (mRouteBean != null && mRouteBean.size() > 0) {
                mRouteBean.clear();
            }
            i = 1;
        } else {
            mPatroAdapter.contentLoadingProgressBar.setVisibility(View.VISIBLE);
            i++;
        }
        bean = new GetRoutelineBean();
        if (mType.equals("my")) {
            SharedPreferences preferences = getSharedPreferences(Login, MODE_PRIVATE);
            final String loginKey = preferences.getString(LoginKey, null);
            AppSetting.curUserKey = loginKey;
            AppSetting.curUser = UserManager.getInstance().getLoginUser(AppSetting.curUserKey);
            if (AppSetting.curUser != null) {
                bean.setAccount(AppSetting.curUser.getLoginName());
            }
        } else if (mType.equals("all")) {
            bean.setAccount("");
        }
        bean.setStartPage(i);
        bean.setPageSize(10);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Call<ResponseBody> newTraceCall = RetrofitHttp.getRetrofit(builder.build()).GetRouteLine("GetMyPatrols", bean);
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
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            ResultRouteBean bean = new ResultRouteBean();
                            JSONObject json = (JSONObject) array.get(i);
                            bean.setId(json.getString("Id"));
                            bean.setName(json.getString("Name"));
                            bean.setCreateUserId(json.getString("CreateUserId"));
                            bean.setPartrolLine(json.getString("PartrolLine"));
                            bean.setDiscription(json.getString("Discription"));
                            bean.setStatus(json.getInt("Status"));
                            bean.setType(json.getInt("Type"));
                            bean.setStartTime(json.getString("StartTime"));
                            bean.setEndTime(json.getString("EndTime"));
                            bean.setCode(json.getString("Code"));
                            bean.setPhotos(json.getString("Photos"));
                            bean.setImageInfo(json.getString("ImageInfo"));
                            bean.setSpId(json.getString("spId"));
                            bean.setWeather(json.getString("weather"));
                            bean.setPatrolUserName(json.getString("patrolUserName"));
                            bean.setPatrolContent(json.getString("PatrolContent"));
                            bean.setDistances(json.getString("Distances"));
                            bean.setUsetimes(json.getString("Usetimes"));
                            mRouteBean.add(bean);
                        }
                        mPatroAdapter.setData(mRouteBean);
                        if (isRegresh && !isLoadmore) {
                            if (mSwipeLayout.isRefreshing()) {
                                mSwipeLayout.setRefreshing(false);
                            }
                            Toast.makeText(getApplicationContext(), "刷新完毕", Toast.LENGTH_SHORT).show();
                        }
                        if (isLoadmore && !isRegresh) {
                            Toast.makeText(getApplicationContext(), "加载完毕", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "无更多数据", Toast.LENGTH_SHORT).show();
                        if (isLoadmore && !isRegresh) {
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
                if (isLoadmore && !isRegresh) {
                    mPatroAdapter.contentLoadingProgressBar.setVisibility(View.GONE);
                }
                Toast.makeText(getApplicationContext(), "网络不给力!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("巡护列表");
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
