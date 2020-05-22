package com.example.dingtu2.myapplication;

import android.app.ActivityManager;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.DingTu.Base.DoEvent;
import com.DingTu.Base.HashValueObject;
import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Envelope;
import com.DingTu.Cargeometry.Part;
import com.DingTu.Cargeometry.Polygon;
import com.DingTu.Data.DeleteAddObject;
import com.DingTu.Data.GpsLine;
import com.DingTu.Data.GpsPoint;
import com.DingTu.Data.GpsPoly;
import com.DingTu.Data.IUnRedo;
import com.DingTu.Data.RoundGPSLine;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Dataset.Workspace;
import com.DingTu.Enum.lkDataCollectType;
import com.DingTu.Enum.lkGeoLayerType;
import com.DingTu.Enum.lkGpsFixMode;
import com.DingTu.GPS.GPSLocate;
import com.DingTu.GPS.GPSMap;
import com.DingTu.GPS.LocationEx;
import com.DingTu.Map.Map;
import com.DingTu.Map.StaticObject;
import com.DingTu.Project.BKLayerExplorer;
import com.DingTu.Project.LayerExplorer;
import com.DingTu.Project.ProjectDB;
import com.DingTu.Project.ProjectExplorer;
import com.DingTu.mapcontainer.MapControl;
import com.example.dingtu2.myapplication.JPush.JPushUtil;
import com.example.dingtu2.myapplication.Login.LoginActivity;
import com.example.dingtu2.myapplication.db.DatabaseCreator;
import com.example.dingtu2.myapplication.db.GenDataBase;
import com.example.dingtu2.myapplication.db.entity.RoundEntity;
import com.example.dingtu2.myapplication.db.xEntity.PatrolEntity;
import com.example.dingtu2.myapplication.db.xEntity.PatrolPointEntity;
import com.example.dingtu2.myapplication.db.xEntity.TraceEntity;
import com.example.dingtu2.myapplication.http.Httpmodel.HttpTraceModel;
import com.example.dingtu2.myapplication.http.Httpmodel.RequestUserId;
import com.example.dingtu2.myapplication.http.RetrofitHttp;
import com.example.dingtu2.myapplication.manager.PatrolManager;
import com.example.dingtu2.myapplication.manager.TraceManager;
import com.example.dingtu2.myapplication.manager.UploadMananger;
import com.example.dingtu2.myapplication.manager.UserManager;
import com.example.dingtu2.myapplication.viewmodel.RoundViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.DingTu.mapcontainer.Tools.AddPolygon;
import static com.DingTu.mapcontainer.Tools.AddPolyline;
import static com.DingTu.mapcontainer.Tools.ZoomInOutPan;

public class MainActivityBackup extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LifecycleRegistryOwner {

    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static boolean isForeground = false;
    public static ICallback mMapSettingCallbak;
    private static boolean unloadTraces = true;
    public int sequence = 1;
    LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    int[] allDrawButtons = new int[]{R.id.bt_delete_op, R.id.bt_undo_op, R.id.bt_poly_drawline, R.id.bt_poly_gps,
            R.id.bt_poly_create, R.id.bt_line_drawline, R.id.bt_line_gps, R.id.bt_line_create,
            R.id.bt_point_draw, R.id.bt_point_coor, R.id.bt_point_gps};
    int[] polyButtons = new int[]{R.id.bt_poly_drawline, R.id.bt_poly_gps, R.id.bt_poly_create};
    int[] lineButtons = new int[]{R.id.bt_line_drawline, R.id.bt_line_gps, R.id.bt_line_create};
    int[] pointButtons = new int[]{R.id.bt_point_draw, R.id.bt_point_coor, R.id.bt_point_gps};
    Boolean isDrawing = false;
    private MapControl mp;
    //返回键点击次数
    private long exitTime = 0;
    private boolean mIsRounding = false;
    private MainActivityBackup mContext;
    private RoundViewModel roundViewModel;
    private LiveData<RoundEntity> mObvervableOnGoingRound;
    private RoundEntity mOnGoingRound;
    private View drawnButtons;
    private int[] m_SelectToolsBarItemIdList = {R.id.bt_poly_drawline, R.id.bt_poly_gps, R.id.bt_line_drawline, R.id.bt_line_gps,
            R.id.bt_point_draw, R.id.bt_point_gps, R.id.btnSelectDraw, R.id.btnStartRound};
    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        DatabaseCreator.getInstance(mContext).createDB(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        addMapViewOnUI();
        initPubVar();
        initWorkDirectionary();

        if (!checkSystemFiles()) {
            exitApp();
        }
//TODO:需要异步实现
//        UpdateManager um = new UpdateManager(this);
//        um.checkUpdate();

        AppSetting.applicaton = this.getApplication();
        mContext = this;


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE) {
                    if (AppSetting.curUser != null) {
                        TextView txUserName = (TextView) findViewById(R.id.nh_userName);
                        TextView txUserPost = (TextView) findViewById(R.id.nh_userPost);
                        txUserName.setText(AppSetting.curUser.getUserName());
                        String post = AppSetting.curUser.getPost() == null ? "" : AppSetting.curUser.getPost();
                        String orginazation = AppSetting.curUser.getOrganization() == null ? "" : AppSetting.curUser.getOrganization();
                        txUserPost.setText(orginazation + " " + post);
                    }

                }
            }
        });

//        if (AppSetting.curUser == null) {
//            Log.i("onCreated", "curUser is  null");
//            LiveData<UserEntity> mOUser = new UserViewModel(this.getApplication(), AppSetting.curUserKey).getObservableUser();
//            mOUser.observe(this, new Observer<UserEntity>() {
//                @Override
//                public void onChanged(@Nullable UserEntity userEntity) {
//                    if (userEntity != null) {
//                        AppSetting.curUser = userEntity;
////                        txUserName.setText(AppSetting.curUser.getUserName());
////                        String postValue = AppSetting.curUser.getOrganization()+" "+AppSetting.curUser.getPost();
////                        txUserPost.setText(postValue);
//                        Log.i("getUser", " UserID " + userEntity.getLoginName());
//                    }
//                }
//            });
//        }
//        else{
////            txUserName.setText(AppSetting.curUser.getUserName());
////            String postValue = AppSetting.curUser.getOrganization()+" "+AppSetting.curUser.getPost();
////            txUserPost.setText(postValue);
//        }


//        if (AppSetting.curUserKey != null && AppSetting.curRound == null) {
//            if (DatabaseCreator.getInstance(mContext).getDatabase() != null) {
//
//                Log.i("onCreated", "get on going round");
//                LiveData<RoundEntity> mOnGoingRound = DatabaseCreator.getInstance(mContext).getDatabase().roundDao().getGoingRound(AppSetting.curUserKey);
//
//                mOnGoingRound.observe(mContext, new Observer<RoundEntity>() {
//                    @Override
//                    public void onChanged(@Nullable RoundEntity roundEntity) {
//                        if (roundEntity != null) {
//                            settingOnGoingPatrol(roundEntity);
//                        }
//                        else
//                        {
////                            startRound();
//                        }
//
//                    }
//                });
//            }
//            else
//            {
//                Log.i("onCreated", "DB is  null");
//            }
//        }


        mMapSettingCallbak = new ICallback() {
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                openProject();
            }
        };
        drawnButtons = findViewById(R.id.llDrwaButtons);
        ButterKnife.bind(this);

//        checkPatrolCommand();
        registerMessageReceiver();
        doChangeByExtra();

        initBussiness();
        PubVar.m_GPSLocate.OpenGPS();

    }

    private void initBussiness() {
        Handler handler = new Handler();
        if (AppSetting.curUser == null) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    AppSetting.curUser = UserManager.getInstance().getLoginUser(AppSetting.curUserKey);
                    settingOnGoingPatrol();
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    settingOnGoingPatrol();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        String from = data.getStringExtra("from");
        if (resultCode == 2)//newPatrol
        {

            if (from == null) {
                Log.d("from", "from is null");
                return;
            }

            Log.d("from", from);

            String commandId = data.getStringExtra("patrolID");
            double[] newCoord = data.getDoubleArrayExtra("patrolCenter");
            newSpecialPatrol(commandId, newCoord);
        } else if (resultCode == 1)//show Patrol Center
        {
            double[] newCoord = data.getDoubleArrayExtra("patrolCenter");
            setNewCenter(newCoord);
        } else {
            Log.d("resultCode", resultCode + "");
        }

    }

    private void doChangeByExtra() {
        String from = this.getIntent().getStringExtra("from");
        if (from == null) {
            Log.d("doChangeByExtra from", "from is null");
            return;
        }
        Log.d("doChangeByExtra from", from);
        if (from.equals("newPatrol")) {
            String commandId = this.getIntent().getStringExtra("patrolID");
            double[] newCoord = this.getIntent().getDoubleArrayExtra("patrolCenter");
            newSpecialPatrol(commandId, newCoord);
        } else if (from.equals("patrolCenter")) {
            double[] newCoord = this.getIntent().getDoubleArrayExtra("patrolCenter");
            setNewCenter(newCoord);
        }
    }

    private void newSpecialPatrol(String commandID, double[] newCenterCoord) {
        PatrolEntity roundEntity = new PatrolEntity();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sDateFormat.format(new Date());
        String userId = AppSetting.curUserKey;
        roundEntity.setUserID(userId);
        String id = userId + date.toString();
        roundEntity.setId(id);
        roundEntity.setStartTime(new Date());
        //TODO: add Command ID to Round;


        roundEntity.setRoundType(10);
        roundEntity.setRoundStatus(0);

        mIsRounding = true;
        AppSetting.curRound = roundEntity;
        GenDataBase db = DatabaseCreator.getInstance(AppSetting.applicaton).getDatabase();
        if (db != null) {
//            db.roundDao().insertRound(roundEntity);
            Log.d("save Special Round", " RoundID: " + roundEntity.getId());
//            uploadRound(AppSetting.curRound);
            PubVar.m_DoEvent.mRoundLinePresenter.Start(lkDataCollectType.enGps_T, AppSetting.curRound.getStartTime());
            initRounding();
        }


        setNewCenter(newCenterCoord);
    }

    private void setNewCenter(double[] coord) {
        if (coord != null && coord.length == 2) {
            Coordinate centerCoord = new Coordinate(coord[0], coord[1]);
            PubVar.m_Map.setCenter(centerCoord);
            PubVar.m_MapControl._Pan.SetNewCenter(centerCoord);
            PubVar.m_Map.Refresh();
        }
    }

    private void checkPatrolCommand() {
        Log.d("Patrol", "PatrolCommand start");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        RequestUserId requestUserId = new RequestUserId();
        requestUserId.setUserId(AppSetting.curUserKey);
        Log.d("curUserKey", AppSetting.curUserKey);
        Call<ResponseBody> newPatrol = RetrofitHttp.getRetrofit(builder.build()).GetSpecialPatrol("GetSpecialPatrol", requestUserId);
        newPatrol.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() == null) {
                    Log.e("Patrol", "checkPatrol faild,response.body() is null");
                    return;
                }

                try {
                    JSONObject result = new JSONObject(response.body().string());
                    if (result.get("success").equals(Boolean.TRUE)) {

                        JSONArray dataArray = new JSONArray(result.get("data").toString());
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject jsonObject = dataArray.getJSONObject(i);
                            String geom = jsonObject.get("geom") + "";
                            String geomtype = jsonObject.get("geomtype") + "";
                            Log.d("Patrol", "geom:" + geom);
                            if (geomtype.equals("Polygon")) {
                                JSONArray polyArray = new JSONArray(geom);
                                Polygon pGeometry = new Polygon();
                                for (int j = 0; j < polyArray.length(); j++) {
                                    JSONArray pointArray = polyArray.getJSONArray(j);
                                    Part part = new Part();
                                    pGeometry.AddPart(part);
                                    List<Coordinate> pointList = new ArrayList<Coordinate>();
                                    for (int k = 0; k < pointArray.length(); k++) {
                                        JSONObject point = pointArray.getJSONObject(j);
                                        double x = point.getDouble("X");
                                        double y = point.getDouble("Y");
                                        Coordinate coor = StaticObject.soProjectSystem.WGS84ToXY(x, y, 0);
                                        if (coor != null) {
                                            pointList.add(coor);
                                        }
                                    }
                                    pointList.add(pointList.get(0).Clone());
                                    part.setVertext(pointList);
                                }

                                //TODO:add pologon to DB;
                            }
                        }


                    } else {
                        Toast.makeText(mContext, "获取指令巡护失败：" + result.get("msg"), Toast.LENGTH_SHORT).show();
                        Log.e("checkPatrolCommand", result.get("msg") + "");
                    }
                } catch (Exception ex) {
                    Log.e("checkPatrolCommand ex", ex.getMessage());
                    Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void settingOnGoingPatrol() {

        AppSetting.curRound = PatrolManager.getInstance().getOnGoingPatrol(AppSetting.curUserKey);
        if (AppSetting.curRound == null) {
            return;
        }

        Tools.ShowYesOrOtherMessage(mContext, "是否继续您上次未完成的巡护？", new ICallback() {
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                if (Str == "YES") {
                    Log.d("巡护", "继续上次未完成的巡护");
                    Log.i("curRound", "round id：" + AppSetting.curRound.getId());
                    Log.i("curRound", "round server id：" + AppSetting.curRound.getServerId());
                    if (AppSetting.curRound.getServerId() == null || AppSetting.curRound.getServerId().isEmpty()) {
                        UploadMananger.getInstance().uploadRound(AppSetting.curRound, new ICallback() {
                            @Override
                            public void OnClick(String Str, Object ExtraStr) {

                            }
                        });
                    }

                    PubVar.m_DoEvent.mRoundLinePresenter.Start(lkDataCollectType.enGps_T, AppSetting.curRound.getStartTime());

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<TraceEntity> traces = TraceManager.getInstance().getTracesByPatrolId(AppSetting.curRound.getId());
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                if (traces == null) {
                                    return;
                                }
                                for (TraceEntity trace : traces) {

                                    LocationEx location = new LocationEx();
                                    Log.d("trace", "lon:" + trace.getLongitude() + " lat:" + trace.getLatitude());
                                    location.SetGpsLongitude(trace.getLongitude());
                                    location.SetGpsLatitude(trace.getLatitude());
                                    location.SetGpsAltitude(trace.getHeight());
                                    location.SetGpsDate(dateFormat.format(trace.getGpsTime()));
                                    location.SetGpsTime(timeFormat.format(trace.getGpsTime()));

                                    PubVar.m_DoEvent.mRoundLinePresenter.UpdateGpsPosition(location, false);

                                    if (trace.getUploadStatus() == 0) {
                                        //TODO:补传轨迹
                                    }
                                }
                            } catch (Exception ex) {
                                Toast.makeText(MainActivityBackup.this.getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT);
                            }

                        }
                    });

                    mIsRounding = true;
                    initRounding();
                } else {
                    mIsRounding = false;
                    stopRound();
                }

            }
        });
    }

    private void initPubVar() {
        PubVar.m_GPSMap = new GPSMap(PubVar.m_MapControl);
        PubVar.m_DoEvent = new DoEvent(this);
        PubVar.m_DoEvent.mGPSSatus.SetStatusView((ImageView) this.findViewById(R.id.iv_status));
        PubVar.m_GPSLocate = new GPSLocate(PubVar.m_MapControl);
        PubVar.m_DoEvent.mRoundLinePresenter = new RoundGPSLine();
        PubVar.m_GPSMap.SetRoundGpsLine(PubVar.m_DoEvent.mRoundLinePresenter);
        PubVar.m_SysDictionaryName = this.getResources().getString(R.string.app_name);
    }

    private void initWorkDirectionary() {
        HashMap<String, String> resultHM = SystemSetup.CheckSystemFile(this);
        if (resultHM.get("Result").equals("系统主目录缺失")) {

            SelectSystemPath sst = new SelectSystemPath(this);
            sst.SetCallback(new ICallback() {
                @Override
                public void OnClick(String Str, Object ExtraStr) {
                    if (Str.equals("退出")) {
                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        String PackName = getPackageName();
                        am.killBackgroundProcesses(PackName);
                        System.exit(0);
                    }
                    if (Str.equals("工作目录")) {
                        SystemSetup.CheckSystemFile(PubVar.m_DoEvent.m_Context);
                    }
                }
            });
            sst.ShowDialog();
            return;
        } else {
            if ((resultHM.get("Result").equals("OK"))) {
                PubVar.m_SysAbsolutePath = resultHM.get("Path");
                AppSetting.photoPath = PubVar.m_SysAbsolutePath + "/Photo";
                AppSetting.smallPhotoPath = AppSetting.photoPath + "/samllPhoto";
                initProjectInfo();

            } else {

                Tools.ShowMessageBox(this, resultHM.get("Result"), new ICallback() {
                    @Override
                    public void OnClick(String Str, Object ExtraStr) {
                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        String PackName = getPackageName();
                        am.killBackgroundProcesses(PackName);   // API Level至少为8才能使用
                        System.exit(0);
                    }
                });
            }
        }
    }

    private void initRounding() {
        if (mIsRounding) {
            PubVar.m_GPSLocate.OpenGPS();
            PubVar.m_DoEvent.mRoundLinePresenter.setTraceCallback(new ICallback() {
                @Override
                public void OnClick(String Str, final Object ExtraStr) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

//                            if (DatabaseCreator.getInstance(mContext).getDatabase() != null) {
                            LocationEx location = (LocationEx) ExtraStr;
                            TraceEntity traceEntity = new TraceEntity();
                            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            if (location.GetGpsPDOP() > 15) {
                                Log.d("PDOP", location.GetGpsPDOP() + "");
//                                    Toast.makeText(MainActivityBackup.this,location.GetGpsPDOP()+"",Toast.LENGTH_SHORT);
                                return;
                            }

                            if (location.GetGpsDate() == null || location.GetGpsDate().isEmpty() || location.GetGpsDate() == null || location.GetGpsTime().isEmpty()) {
                                try {
                                    traceEntity.setGpsTime(sd.parse(location.GetGpsDate() + " " + location.GetGpsTime()));
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                    traceEntity.setGpsTime(new Date());
                                }
                            } else {
                                traceEntity.setGpsTime(new Date());
                            }


                            if (AppSetting.curRound != null) {
                                traceEntity.setRoundID(AppSetting.curRound.getId());
                            } else {
                                Toast.makeText(MainActivityBackup.this, "Current round is null", Toast.LENGTH_SHORT).show();
                            }

                            Coordinate coord = StaticObject.soProjectSystem.WGS84ToXY(location.GetGpsLongitude(), location.GetGpsLatitude(), location.GetGpsAltitude());

                            traceEntity.setSrid("2381");
                            traceEntity.setGpsTime(traceEntity.getGpsTime());
                            traceEntity.setUserID(AppSetting.curUserKey);
                            traceEntity.setHeight(location.GetGpsAltitude());
                            traceEntity.setLatitude(location.GetGpsLatitude());
                            traceEntity.setLongitude(location.GetGpsLongitude());
                            traceEntity.setX(coord.getX());
                            traceEntity.setY(coord.getY());
                            traceEntity.setSrid("2381");
                            traceEntity.setUploadStatus(0);
                            traceEntity.setSaveTime(new Date());

                            traceEntity.setId(traceEntity.getGpsTime().getTime());
//                                DatabaseCreator.getInstance(mContext).getDatabase().traceDao().insertTraces(traceEntity);
//                            TraceManager.getInstance().SaveTrace(traceEntity);

                            if (AppSetting.curRound.getServerId() == null || AppSetting.curRound.getServerId().isEmpty()) {
                                UploadMananger.getInstance().uploadRound(AppSetting.curRound, new ICallback() {
                                    @Override
                                    public void OnClick(String Str, Object ExtraStr) {

                                    }
                                });
                            } else {
                                HttpTraceModel httpTraceModel = new HttpTraceModel();
                                httpTraceModel.setUserId(traceEntity.getUserID());
                                httpTraceModel.setRoundId(AppSetting.curRound.getServerId());
                                httpTraceModel.setLatitude(traceEntity.getLatitude() + "");
                                httpTraceModel.setLongitude(traceEntity.getLongitude() + "");
                                httpTraceModel.setGpsTime(traceEntity.getGpsTime().getTime());
                                httpTraceModel.setHeight(traceEntity.getHeight() + "");
                                httpTraceModel.setX(traceEntity.getX());
                                httpTraceModel.setY(traceEntity.getY());
                                httpTraceModel.setSrid(traceEntity.getSrid());

                                Log.d("轨迹存储", "ID:" + traceEntity.getId() + " lat: " + traceEntity.getLatitude() + " lon:" + traceEntity.getLongitude() + " time:" + traceEntity.getGpsTime().toString());
                                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                                Call<ResponseBody> newTraceCall = RetrofitHttp.getRetrofit(builder.build()).uploadTrace("InsertTrackData", httpTraceModel);
                                final TraceEntity trace = traceEntity;
                                newTraceCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.body() == null) {
                                            Log.d("upload trace", "response.body() is null");
                                            return;
                                        }

                                        try {
                                            if (response.body().string().contains("true")) {
                                                Log.e("上传轨迹", trace.getGpsTime().toString());
                                                trace.setUploadStatus(1);
//                                                    DatabaseCreator.getInstance(mContext).getDatabase().traceDao().insertTraces(trace);
                                                TraceManager.getInstance().SaveTrace(trace);
                                            } else {
                                                Log.e("上传轨迹", response.body().string());

                                            }
                                        } catch (IOException io) {
                                            io.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.e("上传轨迹失败：", t.getMessage());
                                    }
                                });
//                                }
                            }
                        }
                    }).start();
                }
            });


        } else {
            PubVar.m_GPSLocate.CloseGPS();
            PubVar.m_DoEvent.mRoundLinePresenter.Stop();
            PubVar.m_DoEvent.mRoundLinePresenter.setTraceCallback(null);
        }
    }

    private void initProjectInfo() {
        openProject();
    }

    @OnClick({R.id.btnStartRound, R.id.btnSelectDraw, R.id.btnReportAlarm, R.id.btnReportEvent, R.id.btnDraw, R.id.btnFinishRound})
    public void clickBtn(View view) {

        switch (view.getId()) {
            case R.id.btnStartRound:
//                startRound();
                startNomalPatrol();
                break;
            case R.id.btnReportAlarm:
                break;
            case R.id.btnReportEvent:
                reportEvent();
                break;
            case R.id.btnDraw:
//                isDrawing = !isDrawing;
//                if(isDrawing)
//                {
//                    selectDrawType();
//                }
//                else
//                {
//                    exitDraw();
//                }
                addPatrolPoint();
                break;
            case R.id.btnFinishRound:
                stopRound();
                break;
            case R.id.btnSelectDraw:
                PubVar.m_MapControl.setActiveTool(com.DingTu.mapcontainer.Tools.Select);
                break;
        }
        setSelectStatus(view);
    }

    private void setSelectStatus(View view) {
        boolean isNone = false;
        for (int id : m_SelectToolsBarItemIdList) {
            if (view.getId() == id) {
                view.setSelected(true);
                isNone = true;
            } else {
                View buttonView = findViewById(id);
                buttonView.setSelected(false);
            }
        }

        if (!isNone) {
            PubVar.m_MapControl.setActiveTool(com.DingTu.mapcontainer.Tools.ZoomInOutPan);
        }
    }

    @OnClick({R.id.bt_delete_op, R.id.bt_undo_op, R.id.bt_poly_drawline, R.id.bt_poly_gps, R.id.bt_poly_create,
            R.id.bt_line_drawline, R.id.bt_line_gps, R.id.bt_line_create, R.id.bt_point_draw, R.id.bt_point_coor,
            R.id.bt_point_gps})
    public void clickDrawBtn(View view) {
        switch (view.getId()) {
            case R.id.bt_poly_drawline:
                if (!PubVar.m_DoEvent.m_GPSPoly.getGPSLine().CheckLayerValid()) return;
                PubVar.m_MapControl.setActiveTools(AddPolygon, PubVar.m_DoEvent.m_GPSPoly, PubVar.m_DoEvent.m_GPSPoly);
                PubVar.m_DoEvent.m_GPSPoly.getGPSLine().Start(lkDataCollectType.enManual);
                break;
            case R.id.bt_poly_gps:
                if (!PubVar.m_DoEvent.m_GPSPoly.getGPSLine().CheckLayerValid()) return;
                PubVar.m_MapControl.setActiveTools(AddPolygon, PubVar.m_DoEvent.m_GPSPoly, PubVar.m_DoEvent.m_GPSPoly);
                PubVar.m_DoEvent.m_GPSPoly.getGPSLine().Start(lkDataCollectType.enGps_T);
                PubVar.m_GPSMap.SetGpsPoly(PubVar.m_DoEvent.m_GPSPoly);
                break;
            case R.id.bt_poly_create:
                PubVar.m_DoEvent.m_GPSPoly.getGPSLine().Stop(lkGeoLayerType.enPolygon);
                PubVar.m_MapControl.setActiveTool(ZoomInOutPan);
                break;
            case R.id.bt_delete_op:
                if (PubVar.m_DoEvent.m_GPSPoly != null && PubVar.m_DoEvent.m_GPSPoly.getGPSLine().CheckIfStarting()) {
//                    Tools.ShowYesNoMessage(getBaseContext(), "是否删除当前正在采集面？", new ICallback(){
//
//                        @Override
//                        public void OnClick(String Str, Object ExtraStr) {
//                            PubVar.m_DoEvent.m_GPSPoly.getGPSLine().Cancel();
//                        }});
                    PubVar.m_DoEvent.m_GPSPoly.getGPSLine().Cancel();
                    return;
                }
                if (PubVar.m_DoEvent.m_GPSLine != null && PubVar.m_DoEvent.m_GPSLine.CheckIfStarting()) {
//                    Tools.ShowYesNoMessage(mContext, "是否删除当前正在采集线？", new ICallback(){
//
//                        @Override
//                        public void OnClick(String Str, Object ExtraStr) {
//                            PubVar.m_DoEvent.m_GPSLine.Cancel();
//                        }});
                    PubVar.m_DoEvent.m_GPSLine.Cancel();
                    return;
                }

                if (PubVar.m_DoEvent.m_Delete == null)
                    PubVar.m_DoEvent.m_Delete = new DeleteAddObject();
                PubVar.m_DoEvent.m_Delete.Delete();
                break;
            case R.id.bt_undo_op:
                //先判断是否有当前正在采集的数据
                if (PubVar.m_DoEvent.m_GPSPoly.getGPSLine().CheckIfStarting()) {
                    if (PubVar.m_DoEvent.m_GPSPoly.getGPSLine().Undo()) return;
                }
                if (PubVar.m_DoEvent.m_GPSLine.CheckIfStarting()) {
                    if (PubVar.m_DoEvent.m_GPSLine.Undo()) return;

                }

                Tools.OpenDialog("正在进行【回退】操作...", new ICallback() {


                    public void OnClick(String Str, Object ExtraStr) {
                        if (!IUnRedo.Undo()) {
                            Tools.ShowToast(PubVar.m_DoEvent.m_Context, Tools.ToLocale("无可【回退】操作记录！"));
                        }
                    }
                });

                break;
            case R.id.bt_line_drawline:
                if (!PubVar.m_DoEvent.m_GPSLine.CheckLayerValid()) return;
                PubVar.m_MapControl.setActiveTools(AddPolyline, PubVar.m_DoEvent.m_GPSLine, PubVar.m_DoEvent.m_GPSLine);
                PubVar.m_DoEvent.m_GPSLine.Start(lkDataCollectType.enManual);
                break;
            case R.id.bt_line_gps:
                if (!PubVar.m_DoEvent.m_GPSLine.CheckLayerValid()) return;
                PubVar.m_MapControl.setActiveTools(AddPolyline, PubVar.m_DoEvent.m_GPSLine, PubVar.m_DoEvent.m_GPSLine);
                PubVar.m_DoEvent.m_GPSLine.Start(lkDataCollectType.enManual);
                PubVar.m_GPSMap.SetGpsLine(PubVar.m_DoEvent.m_GPSLine);
                break;
            case R.id.bt_line_create:
                PubVar.m_DoEvent.m_GPSLine.Stop(lkGeoLayerType.enPolyline);
                PubVar.m_MapControl.setActiveTool(ZoomInOutPan);
                break;

        }
        setSelectStatus(view);
    }

    private void selectDrawType() {
        try {
            if (PubVar.m_DoEvent.m_GPSLine == null) {
                PubVar.m_DoEvent.m_GPSLine = new GpsLine();
                Dataset pDataset = PubVar.m_Workspace.GetDatasetById("TDA7A51805A2F4F60859380F06A08D742");
                PubVar.m_DoEvent.m_GPSLine.SetDataset(pDataset);

            }
            if (PubVar.m_DoEvent.m_GPSPoly == null) {
                PubVar.m_DoEvent.m_GPSPoly = new GpsPoly();
                Dataset pDataset = PubVar.m_Workspace.GetDatasetById("T37E673C4466441448C9417373F77D442");
                PubVar.m_DoEvent.m_GPSPoly.SetDataset(pDataset);
            }
            if (PubVar.m_DoEvent.m_GPSPoint == null) {
                PubVar.m_DoEvent.m_GPSPoint = new GpsPoint();
                Dataset pDataset = PubVar.m_Workspace.GetDatasetById("TF5F3B004D7C34DCAAF01685476828F25");
                PubVar.m_DoEvent.m_GPSPoint.SetDataset(pDataset);
            }


            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivityBackup.this);
            dialog.setTitle("选择采集类型");
            dialog.setSingleChoiceItems(new String[]{"面", "线", "点"}, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            startDraw(lkGeoLayerType.enPolygon);
                            break;
                        case 1:
                            startDraw(lkGeoLayerType.enPolyline);
                            break;
                        case 2:
                            startDraw(lkGeoLayerType.enPoint);
                            break;
                    }
                }
            });
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    drawnButtons.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            });

            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setCancelable(true);
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exitDraw() {
        for (int id : allDrawButtons) {
            this.findViewById(id).setVisibility(View.GONE);
        }
    }

    private void startDraw(lkGeoLayerType drawType) {

        for (int id : allDrawButtons) {
            this.findViewById(id).setVisibility(View.GONE);
        }

        this.findViewById(R.id.bt_delete_op).setVisibility(View.VISIBLE);
        this.findViewById(R.id.bt_undo_op).setVisibility(View.VISIBLE);

        if (drawType == lkGeoLayerType.enPolygon) {

            for (int id : polyButtons) {
                this.findViewById(id).setVisibility(View.VISIBLE);
            }

        } else if (drawType == lkGeoLayerType.enPolyline) {
            for (int id : lineButtons) {
                this.findViewById(id).setVisibility(View.VISIBLE);
            }

        } else if (drawType == lkGeoLayerType.enPoint) {
            for (int id : pointButtons) {
                this.findViewById(id).setVisibility(View.VISIBLE);
            }


        } else//lkGeoLayerType.enUnknow
        {
            this.findViewById(R.id.bt_delete_op).setVisibility(View.GONE);
            this.findViewById(R.id.bt_undo_op).setVisibility(View.GONE);
        }
    }

    private boolean checkSystemFiles() {
        //得到系统主目录列表，SDPath=完整路径,AllSize=全部容量,FreeSize=可用容量
        List<HashMap<String, Object>> SysDirList = Tools.GetAllSDCardInfoList(MainActivityBackup.this);

        //判断主目录
        String SysDir = "";
        for (HashMap<String, Object> syObj : SysDirList) {
            String SysDirTemp = syObj.get("SDPath") + "/" + PubVar.m_SysDictionaryName;
            if (Tools.ExistFile(SysDirTemp)) {
                SysDir = SysDirTemp;
                break;
            }
        }

        //检查各级目整的完整性
        List<String> dirList = new ArrayList<String>();
        List<String> fileList = new ArrayList<String>();
        Log.d("SysDir", SysDir);
        dirList.add(SysDir + "/Map");  //底图存储目录
        dirList.add(SysDir + "/Data");  //采集数据存储目录
        dirList.add(SysDir + "/SysFile");  //系统文件目录
//        dirList.add(SysDir+"/Data/天保巡护");  //采集数据存储目录
        fileList.add(SysDir + "/SysFile/Config.dbx" + "," + R.raw.config);        //系统配置文件
        fileList.add(SysDir + "/SysFile/Project.dbx" + "," + R.raw.project);    //工程配置文件
        fileList.add(SysDir + "/SysFile/Template.dbx" + "," + R.raw.tadata);    //采集数据模板文件
        fileList.add(SysDir + "/SysFile/UserConfig.dbx" + "," + R.raw.userconfig);  //用户自定配置模板文件
//        fileList.add(SysDir+"/Data/天保巡护/Project.dbx"+","+R.raw.project);  //采集数据存储目录
//        fileList.add(SysDir+"/Data/天保巡护/TAData.dbx"+","+R.raw.tadata);  //采集数据存储目录

        if (SysDir.equals("")) {
            Log.d("create path", "系统主目录缺失");
            Tools.ShowMessageBox(MainActivityBackup.this, "系统主目录缺失");
            return false;
        }
        for (String dir : dirList) {
            if (!Tools.ExistFile(dir)) {
                if (!new File(dir).mkdirs()) {
                    Tools.ShowMessageBox(MainActivityBackup.this, "无法创建目录【" + dir + "】，程序无法正常运行！");
                    Log.d("create path", dir);
                    return false;
                }
            }
        }

        //判断系统配置文件
        for (String sysFile : fileList) {
            String fileName = sysFile.split(",")[0];
            int rowID = Integer.parseInt(sysFile.split(",")[1]);
            if (!Tools.ExistFile(fileName)) {
                if (!Tools.CopyToFileFromRawID(MainActivityBackup.this, fileName, rowID)) {
                    Log.d("Copy file", fileName);
                    Tools.ShowMessageBox(MainActivityBackup.this, "无法创建配置文件【" + sysFile + "】，程序无法正常运行！");
                    return false;
                }
            }
        }

        return true;
    }

    private void openProject() {
        HashValueObject hvo = new HashValueObject();
        hvo.Value = "天保巡护";
        PubVar.m_HashMap.Add("Project", hvo);


        PubVar.m_DoEvent.m_ProjectDB = new ProjectDB();

        PubVar.m_DoEvent.m_ProjectDB.CreateProject(hvo.Value);

        PubVar.m_DoEvent.m_ProjectDB.OpenProject(hvo.Value, false);
        StaticObject.soProjectSystem.SetCoorSystem(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem());
        //在PorjectDB.v1_BKLayerExplorer,底图数据图层
        BKLayerExplorer _BKLayerExplorer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer();

        //在PorjectDB.PorjectDB.v1_LayerExplorer,采集数据图层
        LayerExplorer _LayerExplorer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer();

//			//图层渲染器
//			v1_LayerRenderExplorer _LayerRenderExplorer = PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer();

        //工程浏览器
        ProjectExplorer _ProjectExplorer = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer();

        //创建工程空间实例
        if (PubVar.m_Workspace != null) {
            PubVar.m_Workspace.FreeWorkSpace();
        }

        Workspace pWorkspace = new Workspace();
        PubVar.m_Workspace = pWorkspace;

        //设置工程空间坐标系统，工程坐标系统存储于StaticObject.soProjectSystem.
        pWorkspace.SetCoorSystemInfo(StaticObject.soProjectSystem.GetCoorSystem());

        //创建MAP对象，在此处赋最大范围this.setFullExtend(StaticObject.soMapCellIndex.GetCellExtend());
        Map map = new Map(PubVar.m_MapControl);
        map.SetScaleBar(PubVar.m_DoEvent.mScaleBar);
        map.setEmpty();
        PubVar.m_Map = map;
        ImageView iv = (ImageView) this.findViewById(R.id.iv_scalebar);
        PubVar.m_DoEvent.mScaleBar.SetImageView(iv);
//        PubVar.m_Map.getOverMapLayer().SetOverMapType(lkOverMapType.enGoogle_Satellite);

        //不需要比例尺
        //map.SetScaleBar(PubVar.m_DoEvent.m_ScaleBar);

        //打开采集数据源，其中有渲染底图图层 ，也就是创建GeoLayer
        _LayerExplorer.OpenDataSource(_ProjectExplorer.GetProjectDataFileName());

        //打开底图数据源，其中有渲染底图图层 ，也就是创建GeoLayer
        _BKLayerExplorer.OpenBKDataSource();

        //读取工程的上次视图范围，如果没有则全图显示
        Envelope pEnv = _ProjectExplorer.ReadShowExtend();
        if (pEnv != null) {
            PubVar.m_MapControl.setActiveTool(com.DingTu.mapcontainer.Tools.FullScreenSize);
            PubVar.m_Map.setExtend(pEnv);
//            PubVar.m_Map.Refresh();
        } else {
            PubVar.m_MapControl.setActiveTool(com.DingTu.mapcontainer.Tools.FullScreen);
        }

        PubVar.m_Map.Refresh();
        PubVar.m_MapControl.setActiveTool(ZoomInOutPan);

        Dataset pDataset = PubVar.m_Workspace.GetDatasetById("T5A37395CA75F49F2B0A017DEE983D4EF");
        PubVar.m_DoEvent.mRoundLinePresenter.SetDataset(pDataset);
    }

    private void addMapViewOnUI() {

        mp = new MapControl(this);
        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.mMainContainer);
        linearLayout.addView(mp, 0);

        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(0, 0);
        para.height = RelativeLayout.LayoutParams.FILL_PARENT;
        para.width = RelativeLayout.LayoutParams.FILL_PARENT;
        para.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        para.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        para.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        para.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        mp.setLayoutParams(para);
        PubVar.m_MapControl = mp;

        mp.invalidate();


    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_mapsettings) {
            Intent mapSettingIntent = new Intent(this.getApplicationContext(), MapSettingActivity.class);
            this.startActivity(mapSettingIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            takePhoto();
        } else if (id == R.id.nav_endround) {
            stopRound();

        } else if (id == R.id.nav_command) {
//            try {
//                startActivityForResult(new Intent(mContext, PatrolCommandActivity.class),2);
//            }
//            catch (Exception ex)
//            {
//                Log.e("nav_command",ex.getMessage());
//            }
            addPatrolPoint();


        } else if (id == R.id.nav_startRound) {
//            startRound();
            startNomalPatrol();
        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_event) {
            reportEvent();

        } else if (id == R.id.nav_unload) {
//            reuploadTraces();
//            UpdateManager um = new UpdateManager(this);
//            um.checkUpdate();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        Tools.ShowYesNoMessage(this, "确定要退出当前用户的登录？", new ICallback() {
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                if (Str == "YES") {
                    AppSetting.curUser = null;
                    AppSetting.curRound = null;
                    AppSetting.curUserKey = null;
                    LoginActivity.clearShareRefrenece();
                    trunToLogin();
                    finish();
                }
            }
        });

    }

    //返回登录页面重新登录
    private void trunToLogin() {
        Intent inet = new Intent(this, LoginActivity.class);
        this.startActivity(inet);
    }

    private void reportEvent() {
        if (AppSetting.curRound != null) {
            startActivity(new Intent(mContext, EventActivity.class));
        } else {
            Toast.makeText(this, "请先开始巡护！", Toast.LENGTH_LONG).show();
        }

    }

    private void takePhoto() {
        if (AppSetting.curRound == null) {
            Toast.makeText(MainActivityBackup.this, "没有正在巡护的路线", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(mContext, RoundActivity.class));
    }

    private void stopRound() {
        if (AppSetting.curRound == null) {
            Toast.makeText(MainActivityBackup.this, "没有正在巡护的路线", Toast.LENGTH_SHORT).show();
            return;
        }

        RoundActivity.mFinishCallbak = new ICallback() {
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                if (Str.equals("Finish")) {
                    AppSetting.curRound = null;
                    mIsRounding = false;
                    unloadTraces = true;
//                    PubVar.m_DoEvent.mRoundLinePresenter = new RoundGPSLine();

                    initRounding();
                }
            }
        };
        startActivity(new Intent(mContext, RoundActivity.class));
    }

    private void startNomalPatrol() {
        if (mIsRounding) {
            Tools.ShowMessageBox("正在进行巡护，请完成正在进行的巡护后再开始新的巡护！");
            return;
        }

        AlertDialog.Builder startRoundDialog = new AlertDialog.Builder(MainActivityBackup.this);
        startRoundDialog.setCancelable(true);
        startRoundDialog.setTitle("开始巡护");

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_startround, null);
        final Spinner spRoundType = (Spinner) layout.findViewById(R.id.spRoundType);

        String[] arrRoundType = "常规巡护、稽查巡护、责任区巡护".split("、");
        ArrayAdapter<String> roundTypeAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
                android.R.layout.simple_spinner_item,
                arrRoundType);
        roundTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoundType.setAdapter(roundTypeAdapter);

        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currenetDate = format.format(now);
        ((TextView) layout.findViewById(R.id.etRoundDate)).setText(currenetDate);

        final Spinner spRoundWeather = (Spinner) layout.findViewById(R.id.spWeather);
        String[] arrRoundWeather = "晴、多云、 阴、雾、阵雨、雷阵雨、小雨、中雨、大雨、雷阵雨伴有冰雹、雨夹雪、暴雨、阵雪、小雪、中雪、大雪、暴雪、冻雨、小雨-中雨、中雨-大雨、大雨-暴雨、暴雨-大暴雨、小雪-中雪、中雪-大雪、大雪-暴雪、浮尘、霾".split("、");
        ArrayAdapter<String> roundWeatherAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
                android.R.layout.simple_spinner_item,
                arrRoundWeather);
        roundWeatherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoundWeather.setAdapter(roundWeatherAdapter);

            if (PubVar.m_GPSLocate!=null&&PubVar.m_GPSLocate.m_LocationEx != null) {
            try {
                ((TextView) layout.findViewById(R.id.tvLon)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude() + "", 7));
                ((TextView) layout.findViewById(R.id.tvLat)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude() + "", 7));
                ((TextView) layout.findViewById(R.id.tvHigh)).setText(PubVar.m_GPSLocate.m_LocationEx.GetGpsAltitude() + "");
            } catch (Exception ex) {

            }
        }

//        startRoundDialog.setMessage("您要现在开始新的巡护？");
        startRoundDialog.setPositiveButton("开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                PatrolEntity roundEntity = new PatrolEntity();
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String date = sDateFormat.format(new Date());
                String userId = AppSetting.curUserKey;
                roundEntity.setUserID(userId);
                String id = userId + date.toString();
                roundEntity.setId(id);
                roundEntity.setStartTime(new Date());
                String strRoundType = spRoundType.getSelectedItem().toString();
                roundEntity.setRoundName(((TextView) ((AlertDialog) dialog).findViewById(R.id.etRoundName)).getText() + "");
                roundEntity.setUserNames(((TextView) ((AlertDialog) dialog).findViewById(R.id.etXunHuYuan)).getText() + "");

                if (strRoundType.equals("常规巡护")) {
                    roundEntity.setRoundType(21);
                } else if (strRoundType.equals("稽查巡护")) {
                    roundEntity.setRoundType(22);
                } else if (strRoundType.equals("责任区巡护")) {
                    roundEntity.setRoundType(23);
                } else {
                    roundEntity.setRoundType(1);
                }

                roundEntity.setRoundStatus(0);
                roundEntity.setWeather(spRoundWeather.getSelectedItem().toString());

                try {
                    PatrolManager.getInstance().savePatrol(roundEntity);
                    mIsRounding = true;
                    AppSetting.curRound = roundEntity;
                    PubVar.m_DoEvent.mRoundLinePresenter.Start(lkDataCollectType.enGps_T, AppSetting.curRound.getStartTime());
                    initRounding();


                    UploadMananger.getInstance().uploadRound(roundEntity, new ICallback() {
                        @Override
                        public void OnClick(String Str, Object ExtraStr) {

                            if (Str.equals("success")) {
                                if (PubVar.m_GPSLocate!=null&&PubVar.m_GPSLocate.m_LocationEx != null) {
                                    saveStartPoint(PubVar.m_GPSLocate.m_LocationEx, true);
                                    Toast.makeText(MainActivityBackup.this.getBaseContext(), "上传成功", Toast.LENGTH_SHORT);
                                }

//                                else if (PubVar.m_BaiduLocate!=null&&PubVar.m_BaiduLocate.m_LocationEx != null) {
//                                    saveStartPoint(PubVar.m_BaiduLocate.m_LocationEx, true);
//                                    Toast.makeText(MainActivityBackup.this.getBaseContext(), "上传成功", Toast.LENGTH_SHORT);
//                                }
                            } else {
                                if(PubVar.m_GPSLocate!=null&&PubVar.m_GPSLocate.m_LocationEx!=null){
                                    saveStartPoint(PubVar.m_GPSLocate.m_LocationEx, false);
                                    Toast.makeText(MainActivityBackup.this.getBaseContext(), "上传失败", Toast.LENGTH_SHORT);
                                }

//                                else if(PubVar.m_BaiduLocate!=null&&PubVar.m_BaiduLocate.m_LocationEx != null){
//                                    saveStartPoint(PubVar.m_BaiduLocate.m_LocationEx, false);
//                                    Toast.makeText(MainActivityBackup.this.getBaseContext(), "上传失败", Toast.LENGTH_SHORT);
//                                }

                            }
                        }
                    });
                    dialog.dismiss();
                } catch (Exception ex) {
                    Toast.makeText(MainActivityBackup.this.getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT);
                }
            }
        });

        startRoundDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        startRoundDialog.show();
    }

    private void saveStartPoint(LocationEx locationEx, boolean patrolUploaded) {
        if (locationEx == null || locationEx.GetGpsLatitude() < 0.000001 || locationEx.GetGpsLongitude() < 0.000001) {
            return;
        }

        PatrolPointEntity patrolPointEntity = new PatrolPointEntity();
        patrolPointEntity.setUserID(AppSetting.curUser.getUserID());
        patrolPointEntity.setRoundID(AppSetting.curRound.getId());
        patrolPointEntity.setLatitude(locationEx.GetGpsLatitude());
        patrolPointEntity.setLongitude(locationEx.GetGpsLongitude());
        //TODO:
        patrolPointEntity.setGpsTime(new Date());
        patrolPointEntity.setHeight(locationEx.GetGpsAltitude());
        Coordinate coordinate = StaticObject.soProjectSystem.WGS84ToXY(locationEx.GetGpsLongitude(),
                locationEx.GetGpsLatitude(),
                locationEx.GetGpsAltitude());
        patrolPointEntity.setX(coordinate.getX());
        patrolPointEntity.setY(coordinate.getY());
//            TODO:auto get srid;
        patrolPointEntity.setSrid("2381");
        patrolPointEntity.setPointType("0");

        try {
            PatrolManager.getInstance().savePatrolPoint(patrolPointEntity);
        } catch (Exception ex) {
            return;
        }

        if (patrolUploaded) {
//            UploadMananger.getInstance().uploadPatrolPoint(patrolPointEntity, new ICallback() {
//                @Override
//                public void OnClick(String Str, Object ExtraStr) {
//
//                }
//            });
        }


    }

    private void addPatrolPoint() {
        AlertDialog.Builder addPointDialog = new AlertDialog.Builder(MainActivityBackup.this);
        addPointDialog.setCancelable(true);
        addPointDialog.setTitle("记录巡点");

        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_addpatrolpoint, null);
        addPointDialog.setView(layout);

        if (PubVar.m_GPSLocate!=null&&PubVar.m_GPSLocate.m_LocationEx != null && PubVar.m_GPSLocate.m_LocationEx.GetGpsFixMode() == lkGpsFixMode.en3DFix) {
            try {
                ((TextView) layout.findViewById(R.id.etPointLon)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude() + "", 7));
                ((TextView) layout.findViewById(R.id.etPointLat)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude() + "", 7));
                ((TextView) layout.findViewById(R.id.etPointAlt)).setText(PubVar.m_GPSLocate.m_LocationEx.GetGpsAltitude() + "");
            } catch (Exception ex) {

            }
        }
        ((TextView) layout.findViewById(R.id.etPointLon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PubVar.m_GPSLocate!=null&&PubVar.m_GPSLocate.m_LocationEx != null && PubVar.m_GPSLocate.m_LocationEx.GetGpsFixMode() == lkGpsFixMode.en3DFix) {
                    try {
                        ((TextView) layout.findViewById(R.id.etPointLon)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude() + "", 7));
                        ((TextView) layout.findViewById(R.id.etPointLat)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude() + "", 7));
                        ((TextView) layout.findViewById(R.id.etPointAlt)).setText(PubVar.m_GPSLocate.m_LocationEx.GetGpsAltitude() + "");
                    } catch (Exception ex) {

                    }
                } else {
                    Toast.makeText(v.getContext(), "请开启GPS并在开阔地带精确定位,然后刷新位置!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addPointDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    if (PubVar.m_GPSLocate!=null&&PubVar.m_GPSLocate.m_LocationEx != null
                            && PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude() > 0
                            && PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude() > 0) {
                        PatrolPointEntity pointEntity = new PatrolPointEntity();
                        pointEntity.setHeight(PubVar.m_GPSLocate.m_LocationEx.GetGpsAltitude());
                        pointEntity.setLatitude(PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude());
                        pointEntity.setLongitude(PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude());
//                       pointEntity.setWarningtime(PubVar.m_GPSLocate.m_LocationEx.GetGpsTime());
                        pointEntity.setGpsTime(new Date());
                        Coordinate coord = StaticObject.soProjectSystem.WGS84ToXY(PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude(),
                                PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude(),
                                PubVar.m_GPSLocate.m_LocationEx.GetGpsAltitude());

                        pointEntity.setPointType("1");
                        pointEntity.setSrid("2381");
                        pointEntity.setX(coord.getX());
                        pointEntity.setY(coord.getY());
                        pointEntity.setRoundID(AppSetting.curRound.getId());
                        //TODO:
//                       pointEntity.setUserID(AppSetting.curUser.getUserID());
                        pointEntity.setUserID(AppSetting.curUserKey);
                        try {
                            PatrolManager.getInstance().savePatrolPoint(pointEntity);
                        } catch (Exception ex) {
                            Toast.makeText(MainActivityBackup.this.getBaseContext(), "保存失败", Toast.LENGTH_SHORT);
                            return;
                        }

//                        UploadMananger.getInstance().uploadPatrolPoint(pointEntity, new ICallback() {
//                            @Override
//                            public void OnClick(String Str, Object ExtraStr) {
//
//                            }
//                        });
                        dialog.dismiss();

                    } else {
                        Toast.makeText(MainActivityBackup.this, "请开启GPS并在开阔地带精确定位!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                } catch (Exception ex) {
                    Toast.makeText(MainActivityBackup.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        addPointDialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        addPointDialog.show();
    }

    private void startRound() {

        if (mIsRounding) {
            Tools.ShowMessageBox("正在进行巡护，请完成正在进行的巡护后再开始新的巡护！");
            return;
        }
        AlertDialog.Builder startRoundDialog = new AlertDialog.Builder(MainActivityBackup.this);
        startRoundDialog.setCancelable(true);
        startRoundDialog.setTitle("开始巡护");
        LayoutInflater inflater = getLayoutInflater();
        startRoundDialog.setMessage("您要现在开始新的巡护？");
        startRoundDialog.setPositiveButton("开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                PatrolEntity roundEntity = new PatrolEntity();
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String date = sDateFormat.format(new Date());
                String userId = AppSetting.curUserKey;
                roundEntity.setUserID(userId);
                String id = userId + date.toString();
                roundEntity.setId(id);
                roundEntity.setStartTime(new Date());
                roundEntity.setRoundType(1);
                roundEntity.setRoundStatus(0);

                mIsRounding = true;
                AppSetting.curRound = roundEntity;
//                GenDataBase db = DatabaseCreator.getInstance(AppSetting.applicaton).getDatabase();
//                if(db != null)
//                {
//                    db.roundDao().insertRound(roundEntity);
//                    Log.d("save Round", " RoundID: " + roundEntity.getId());
//                    UploadMananger.getInstance().uploadRound(roundEntity);
//                    PubVar.m_DoEvent.mRoundLinePresenter.Start(lkDataCollectType.enGps_T,AppSetting.curRound.getStartTime());
//                    initRounding();
//                    dialog.dismiss();
//                }
            }
        });

        startRoundDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        startRoundDialog.show();

    }

    private void uploadTrace(final TraceEntity traceEntity, final String roundServerId) {
        if (roundServerId == null || roundServerId.isEmpty()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpTraceModel httpTraceModel = new HttpTraceModel();
                httpTraceModel.setUserId(traceEntity.getUserID());
                httpTraceModel.setRoundId(roundServerId);
                httpTraceModel.setLatitude(traceEntity.getLatitude() + "");
                httpTraceModel.setLongitude(traceEntity.getLongitude() + "");
                httpTraceModel.setGpsTime(traceEntity.getGpsTime().getTime());
                httpTraceModel.setHeight(traceEntity.getHeight() + "");
                httpTraceModel.setX(traceEntity.getX());
                httpTraceModel.setY(traceEntity.getY());
                httpTraceModel.setSrid(traceEntity.getSrid());

                Log.d("轨迹存储", "ID:" + traceEntity.getId() + " lat: " + traceEntity.getLatitude() + " lon:" + traceEntity.getLongitude() + " time:" + traceEntity.getGpsTime().toString());
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                Call<ResponseBody> newTraceCall = RetrofitHttp.getRetrofit(builder.build()).uploadTrace("InsertTrackData", httpTraceModel);
                newTraceCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.body() == null) {
                            Log.d("upload trace", "response.body() is null");
                            return;
                        }

                        try {
                            if (response.body().string().contains("true")) {
                                Log.d("upload trace", traceEntity.getGpsTime().toString());
                                traceEntity.setUploadStatus(1);
//                                DatabaseCreator.getInstance(mContext).getDatabase().traceDao().insertTraces(traceEntity);
                            } else {
                                Log.e("上传轨迹", response.body().string());
                            }
                        } catch (IOException io) {
                            io.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("上传轨迹失败：", t.getMessage());
                    }
                });
            }

        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exitApp();
            return false;
        }
        return true;
    }

    private void reuploadTraces() {


        if (DatabaseCreator.getInstance(AppSetting.applicaton).getDatabase() == null) {
            Log.d("reuploadTraces", "database is null ");
            return;
        } else {
            DatabaseCreator.getInstance(AppSetting.applicaton).createDB(this);
        }

        if (AppSetting.curRound == null) {
            return;
        }

        GenDataBase db = DatabaseCreator.getInstance(AppSetting.applicaton).getDatabase();


//        LiveData<List<TraceEntity>> unloadTrances = DatabaseCreator.getInstance(AppSetting.applicaton).getDatabase().traceDao().getUnloadTraces(AppSetting.curRound.getId());
//        unloadTrances.observe(MainActivityBackup.this, new Observer<List<TraceEntity>>() {
//            @Override
//            public void onChanged(@Nullable List<TraceEntity> traceEntities) {
//                if(traceEntities.size()>0) {
//                    Toast.makeText(MainActivityBackup.this, "未上传轨迹点数：" + traceEntities.size(), Toast.LENGTH_SHORT).show();
//                    for(TraceEntity trace:traceEntities)
//                    {
//                            if(AppSetting.curRound.getServerId() == null || AppSetting.curRound.getServerId().isEmpty())
//                            {
//
//                            }
//                            else
//                            {
//                                uploadTrace(trace,AppSetting.curRound.getServerId());
//                            }
//                    }
//                }
//            }
//        });


    }

    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再次按键退出天保巡护系统！", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {

            //创建AlertDialog
            AlertDialog.Builder menuDialog = new AlertDialog.Builder(this);
            menuDialog.setTitle(("系统提示"));
            menuDialog.setCancelable(true);
            menuDialog.setMessage("您确定要退出天保巡护系统？");
            menuDialog.setPositiveButton(("确定"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().SaveShowExtend(PubVar.m_Map.getExtend());
                    System.exit(0);

                }
            });
            menuDialog.setNegativeButton(("取消"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            menuDialog.show();
        }
    }

    public void registerMessageReceiver() {
        JPushInterface.init(getApplicationContext());

        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        com.example.dingtu2.myapplication.JPush.LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);

        sequence++;
        if (AppSetting.curUserKey != null) {
            JPushInterface.setAlias(getApplicationContext(), sequence, AppSetting.curUserKey);
            Log.d("JPushInterfaceSetAlias", sequence + ":" + AppSetting.curUserKey);
        }

    }

    private void setCostomMsg(String msg) {
//        if (null != msgText) {
//            msgText.setText(msg);
//            msgText.setVisibility(android.view.View.VISIBLE);
//        }
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!JPushUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e) {
            }
        }
    }

}
