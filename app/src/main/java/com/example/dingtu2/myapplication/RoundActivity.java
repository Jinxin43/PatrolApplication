package com.example.dingtu2.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Enum.lkGpsFixMode;
import com.DingTu.Map.StaticObject;
import com.example.dingtu2.myapplication.db.xEntity.PatrolEventEntity;
import com.example.dingtu2.myapplication.db.xEntity.PatrolPointEntity;
import com.example.dingtu2.myapplication.db.xEntity.PhotoEntity;
import com.example.dingtu2.myapplication.db.xEntity.TraceEntity;
import com.example.dingtu2.myapplication.http.RetrofitHttp;
import com.example.dingtu2.myapplication.manager.PatrolManager;
import com.example.dingtu2.myapplication.manager.PhotoManager;
import com.example.dingtu2.myapplication.manager.UploadMananger;
import com.example.dingtu2.myapplication.utils.PhotoCamera;
import com.example.dingtu2.myapplication.utils.PhotoShow;
import com.example.dingtu2.myapplication.utils.SharedPreferencesUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoundActivity extends AppCompatActivity {

    public static ICallback photoCallBack;
    public static ICallback mFinishCallbak;
    @BindView(R.id.gvPhotoList)
    GridView gridView;
    @BindView(R.id.finish_round_name)
    EditText roundLineName;
    @BindView(R.id.et_roundTime)
    EditText roundTime;
    @BindView(R.id.round_description)
    EditText roundDescription;
    @BindView(R.id.tv_RoundLineName)
    TextView roundLineText;

    @BindView(R.id.etXunHuYuan)
    EditText xunHuYuan;

    @BindView(R.id.spEndRoundType)
    Spinner spRoundType;

    @BindView(R.id.etEventNum)
    EditText etEventNum;

    @BindView(R.id.et_PointNum)
    EditText et_PointNum;

    @BindView(R.id.spAreaDuty)
    Spinner spAreaDuty;

    @BindView(R.id.et_finishAreaDuty)
    EditText etAreaDuty;

    @BindView(R.id.tv_patrolContent)
    TextView tvPatrolContentName;

    @BindView(R.id.spWeather)
    Spinner spWeather;

    @BindView(R.id.view_PatrlContent)
    LinearLayout viewPatrolContent;
    @BindView(R.id.view_EventNum)
    LinearLayout viewEventNum;

    @BindView(R.id.view_AreaDuty)
    LinearLayout view_AreaDuty;
    @BindView(R.id.etPatrolContent)
    EditText etPatrolContent;
    @BindView(R.id.tv_Result)
    TextView tv_Result;
    @BindView(R.id.tv_Result1)
    TextView tv_Result1;

    private Date endTime;
    private PhotoShow photoShowTool = new PhotoShow();
    private String tempPhotoName = AppSetting.photoPath + "/TempPhoto.jpg";
    private List<String> mPhotoNameList = new ArrayList<String>();
    private Dialog mSavingingDlg; // 显示正在保存的Dialog
    private TraceEntity lastTrace;

    private String[] arrRoundType = new String[3];

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        View customView = LayoutInflater.from(this).inflate(R.layout.actionbar_back, new RelativeLayout(this), false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(customView);
        ((TextView) customView.findViewById(R.id.tv_back)).setText("巡护详情");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((ImageView) customView.findViewById(R.id.iv_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            ButterKnife.bind(this);
            bingToView();
            initSavingDlg();
//            setupActionBar();
        } catch (Exception ex) {

        }

        photoCallBack = new ICallback() {
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                photoCallbackResult(Str, ExtraStr);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PubVar.m_GPSLocate == null || PubVar.m_GPSLocate.m_LocationEx == null || PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude() < 0.000001 || PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude() < 0.000001) {
                Tools.ShowYesNoMessage(this, "当前GPS信号弱，是否将巡护轨迹的最后一个点作为结束点保存？", new ICallback() {
                    @Override
                    public void OnClick(String Str, Object ExtraStr) {
                        if (Str.equals("YES")) {
                            lastTrace = AppSetting.curRound.getLastTrace();
                        } else {

                        }

                    }
                });
        } else {
            Coordinate coord = StaticObject.soProjectSystem.WGS84ToXY(PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude(), PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude(), PubVar.m_GPSLocate.m_LocationEx.GetGpsAltitude());

            lastTrace = new TraceEntity();
            lastTrace.setUserID(AppSetting.curUserKey);
            lastTrace.setHeight(PubVar.m_GPSLocate.m_LocationEx.GetGpsAltitude());
           if(PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude()>0&&PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude()>0) {
            lastTrace.setLatitude(PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude());
            lastTrace.setLongitude(PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude());
           }
            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            lastTrace.setX(nf.format(coord.getX()));
            lastTrace.setY(nf.format(coord.getY()));
            String name=StaticObject.soProjectSystem.GetCoorSystem().GetName();
            if(name.equals("西安80坐标")){
                lastTrace.setSrid("2381");
            }else if(name.equals("北京54坐标")){
                lastTrace.setSrid("2433");
            }else if(name.equals("2000国家大地坐标系")){
                lastTrace.setSrid("4545");
            }else if(name.equals("WGS-84坐标")){
                lastTrace.setSrid("4326");
            }
            lastTrace.setUploadStatus(0);
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (PubVar.m_GPSLocate.m_LocationEx.GetGpsDate() == null || PubVar.m_GPSLocate.m_LocationEx.GetGpsDate().isEmpty() || PubVar.m_GPSLocate.m_LocationEx.GetGpsDate() == null || PubVar.m_GPSLocate.m_LocationEx.GetGpsTime().isEmpty()) {
                try {
                    lastTrace.setGpsTime(sd.parse(PubVar.m_GPSLocate.m_LocationEx.GetGpsDate() + " " + PubVar.m_GPSLocate.m_LocationEx.GetGpsTime()));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                    Log.d("SaveTrace", "Time is Null");
                    lastTrace.setGpsTime(new Date());
                }
            } else {
                Log.d("SaveTrace", "Time Null");
                lastTrace.setGpsTime(new Date());
            }

            lastTrace.setSaveTime(new Date());
            lastTrace.setRoundID(AppSetting.curRound.getId());
        }

    }

//    private void setupActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            SharedPreferencesUtils.putBoolean(this, "mIsRounding", true);
            AppSetting.mIsRounding = true;
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bingToView() {

        try {
            arrRoundType[0] = "常规巡护";
            arrRoundType[1] = "稽查巡护";
            arrRoundType[2] = "责任区巡护";
            ArrayAdapter<String> roundTypeAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
                    android.R.layout.simple_spinner_item,
                    arrRoundType);
            roundTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRoundType.setAdapter(roundTypeAdapter);

            spRoundType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 1) {
                        tvPatrolContentName.setText("巡护内容");
                        viewPatrolContent.setVisibility(View.VISIBLE);
                        viewEventNum.setVisibility(View.GONE);
                        tv_Result1.setText("结果");
                        tv_Result.setVisibility(View.VISIBLE);
                        roundLineText.setText("巡护线路");
                    } else if (position == 2) {
                        tvPatrolContentName.setText("巡护内容");
                        roundLineText.setText("巡查区域");
                    } else {
                        tvPatrolContentName.setText("巡护发现");
                        viewPatrolContent.setVisibility(View.GONE);
                        viewEventNum.setVisibility(View.VISIBLE);
                        tv_Result1.setText("备注");
                        tv_Result.setVisibility(View.INVISIBLE);
                        roundLineText.setText("巡护线路");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            etPatrolContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(RoundActivity.this);
                        //复选样式Item：需要设置显示的数据，需要监听选择的Item状态的初始化（方便得到指定的被选择的信息文本并处理），
                        //监听Item的选择事件（方便不同类型信息的获取）；
                        final String[] hobits = "狩猎、下套、盗伐、盗运、割漆、挖药、开荒、放牧、烧炭、野外用火".split("、");
                        final boolean[] checkedItems = new boolean[hobits.length];
                        alert.setMultiChoiceItems(hobits, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // TODO Auto-generated method stub
                                checkedItems[which] = isChecked;
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                etPatrolContent.setText("");
                                for (int i = 0; i < checkedItems.length; i++) {
                                    if (checkedItems[i]) {
                                        etPatrolContent.append(hobits[i] + " ");
                                    }
                                }
                            }
                        }).create();
                        alert.show();
                    }
                }
            });

            String[] arrWeather = ("晴、多云、 阴、雾、阵雨、雷阵雨、小雨、中雨、大雨、雨夹雪、暴雨、阵雪、小雪、中雪、大雪、暴雪、冻雨、浮尘、霾").split("、");
            ArrayAdapter<String> weatherAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
                    android.R.layout.simple_spinner_item,
                    arrWeather);
            spWeather.setAdapter(weatherAdapter);

            Tools.SetSpinnerValueOnID(RoundActivity.this, R.id.spWeather, AppSetting.curRound.getWeather());


            try {
//                    Tools.SetSpinnerValueOnID(RoundActivity.this, R.id.spAreaDuty, AppSetting.myDutyArea.get(AppSetting.curRound.getDutyId()));
                etAreaDuty.setText(AppSetting.curRound.getDutyId());
            } catch (Exception ex) {
                Toast.makeText(RoundActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
//            }


            if (AppSetting.curRound != null) {
                if (AppSetting.curRound.getRoundName() != null) {
                    roundLineName.setText(AppSetting.curRound.getRoundName());
                }
                xunHuYuan.setText(AppSetting.curRound.getUserNames());

                roundDescription.setText(AppSetting.curRound.getSummary());
//            roundType.getEditText().setText(AppSetting.curRound.getRoundType());
                Date startDate = AppSetting.curRound.getStartTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
                String startTime = "";
                if (startDate != null) {
                    startTime = simpleDateFormat.format(startDate);
                }
                endTime = new Date();
                roundTime.setText(startTime + " 至 " + simpleDateFormat.format(endTime));
                if (AppSetting.curRound.getRoundType() == 21) {
                    spRoundType.setSelection(0);
                    view_AreaDuty.setVisibility(View.GONE);

                } else if (AppSetting.curRound.getRoundType() == 22) {
                    spRoundType.setSelection(1);
                } else if (AppSetting.curRound.getRoundType() == 23) {
                    spRoundType.setSelection(1);
                }


                List<PatrolPointEntity> points = PatrolManager.getInstance().getPatrolPoints(AppSetting.curRound.getId());
                if (points != null) {
                    et_PointNum.setText(points.size() + "");
                } else {
                    et_PointNum.setText("0");
                }

                List<PatrolEventEntity> events = PatrolManager.getInstance().getPatrolEvents(AppSetting.curRound.getId());
                if (events != null) {
                    etEventNum.setText(events.size() + "");
                } else {
                    etEventNum.setText("0");
                }

                if (PubVar.m_GPSLocate != null && PubVar.m_GPSLocate.m_LocationEx != null && PubVar.m_GPSLocate.m_LocationEx.GetGpsFixMode() == lkGpsFixMode.en3DFix) {
                    try {
                        ((TextView) findViewById(R.id.tvLon)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude() + "", 7));
                        ((TextView) findViewById(R.id.tvLat)).setText(Tools.ConvertToDigi(PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude() + "", 7));
                        ((TextView) findViewById(R.id.tvHigh)).setText(PubVar.m_GPSLocate.m_LocationEx.GetGpsAltitude() + "");
                    } catch (Exception ex) {

                    }
                }
                String photoNames = AppSetting.curRound.getPhotos();
                if (photoNames != null && photoNames.length() > 0) {
                    mPhotoNameList = Tools.StrArrayToList(photoNames.split(","));
                    photoShowTool.showPhotos(this, mPhotoNameList, gridView);
                }
            } else {
                Toast.makeText(this, "没有正在巡护线路", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.btn_roundphoto, R.id.text_save, R.id.text_finish, R.id.btn_deleteroundphoto, R.id.etEventNum, R.id.btnEventDetail, R.id.btn_PointDetail, R.id.et_PointNum})
    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.btn_roundphoto:
                takePhoto();
                break;
            case R.id.text_save:
                saveRound();
                break;
            case R.id.text_finish:
                finishRound();
                break;
            case R.id.btn_deleteroundphoto:
                deletePhoto();
                break;
            case R.id.btn_PointDetail:
                showPatrolPoint();
                break;
            case R.id.et_PointNum:
                showPatrolPoint();
                break;
        }
    }

    private void showPatrolPoint() {
        Intent intent = new Intent(RoundActivity.this, PointListActivity.class);
        intent.putExtra("patrolId", AppSetting.curRound.getId());
        startActivity(intent);
    }

    private void saveRound() {

        if (AppSetting.curRound == null) {
            Toast.makeText(this, "没有正在巡护线路", Toast.LENGTH_SHORT).show();
            return;
        } else {
            getRoundValue();
            try {
                PatrolManager.getInstance().savePatrol(AppSetting.curRound);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "保存失败" , Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void finishRound() {
        if (AppSetting.curRound == null) {
            Toast.makeText(this, "没有正在巡护线路", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lastTrace != null) {
            saveEndPoint();
            saveAndUpload();
        } else {
            Tools.ShowYesNoMessage(this, "当前巡护没有结束点，是否结束巡护？", new ICallback() {
                @Override
                public void OnClick(String Str, Object ExtraStr) {
                    if (Str.equals("YES")) {

                        saveAndUpload();

                    } else {
                        Toast.makeText(RoundActivity.this, "请GPS信号正常时再结束巡护！", Toast.LENGTH_LONG).show();
                        finish();

                    }
                }
            });
        }
    }

    private void saveAndUpload() {
        getRoundValue();
        if (AppSetting.curRound.getRoundType() == 23) {
            if (roundLineName.getText() == null || roundLineName.getText().length() == 0) {
                Tools.ShowMessageBox(RoundActivity.this, "责任区巡护必须填写责任区！", new ICallback() {
                    @Override
                    public void OnClick(String Str, Object ExtraStr) {

                    }
                });
                return;
            }
        }
        AppSetting.curRound.setEndTime(new Date());
        AppSetting.curRound.setRoundStatus(1);
        AppSetting.curRound.setUploadStatus(0);

        try {
            PatrolManager.getInstance().savePatrol(AppSetting.curRound);
            Toast.makeText(RoundActivity.this, "巡护信息已保存", Toast.LENGTH_SHORT).show();
            closeActivity();
        } catch (Exception ex) {
            Toast.makeText(RoundActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            try {
                if (mSavingingDlg.isShowing()) {
                    mSavingingDlg.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("mSavingingDlg", e.getMessage());
            }
            return;
        }

        try {
            UploadMananger.getInstance().uploadRound(AppSetting.curRound, new ICallback() {
                @Override
                public void OnClick(String Str, Object ExtraStr) {
                    if (Str.equals("success")) {
                        if (mPhotoNameList != null && mPhotoNameList.size() > 0) {
                            uploadPhotos();
                        }
                        Toast.makeText(AppSetting.applicaton.getApplicationContext(), "巡护上传成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AppSetting.applicaton.getApplicationContext(), "巡护上传失败", Toast.LENGTH_SHORT).show();
                    }


                    mFinishCallbak.OnClick("Finish", AppSetting.curRound.getId());

                }
            });

        } catch (Exception ex) {
        }
    }

    private boolean saveEndPoint() {
        try {

            PatrolPointEntity patrolPointEntity = new PatrolPointEntity();
            patrolPointEntity.setUserID(AppSetting.curUserKey);
            patrolPointEntity.setRoundID(AppSetting.curRound.getId());
            patrolPointEntity.setLatitude(lastTrace.getLatitude());
            patrolPointEntity.setLongitude(lastTrace.getLongitude());
            patrolPointEntity.setGpsTime(lastTrace.getGpsTime());
            patrolPointEntity.setHeight(lastTrace.getHeight());
            patrolPointEntity.setX(lastTrace.getX());
            patrolPointEntity.setY(lastTrace.getY());
            patrolPointEntity.setPointName("结束点");
            String name=StaticObject.soProjectSystem.GetCoorSystem().GetName();
            if(name.equals("西安80坐标")){
                patrolPointEntity.setSrid("2381");
            }else if(name.equals("北京54坐标")){
                patrolPointEntity.setSrid("2433");
            }else if(name.equals("2000国家大地坐标系")){
                patrolPointEntity.setSrid("4545");
            }else if(name.equals("WGS-84坐标")){
                patrolPointEntity.setSrid("4326");
            }
            patrolPointEntity.setPointType("2");
            PatrolManager.getInstance().savePatrolPoint(patrolPointEntity);

            if (AppSetting.curRound.getServerId() != null && !AppSetting.curRound.getServerId().isEmpty()) {
                UploadMananger.getInstance().uploadPatrolPoint(patrolPointEntity, AppSetting.curRound.getServerId(), new ICallback() {
                    @Override
                    public void OnClick(String Str, Object ExtraStr) {
                        if (Str.equals("success")) {
                            Toast.makeText(RoundActivity.this, "结束点已上传", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    private void getRoundValue() {
        if (AppSetting.curRound == null) {
            return;
        }

        AppSetting.curRound.setRoundName(roundLineName.getText().toString().trim());
        AppSetting.curRound.setPhotos(Tools.StrListToStr(mPhotoNameList));
        AppSetting.curRound.setSummary(roundDescription.getText().toString().trim());
        AppSetting.curRound.setWeather(spWeather.getSelectedItem().toString());
        AppSetting.curRound.setUserNames(xunHuYuan.getText() + "");
        AppSetting.curRound.setContent(etPatrolContent.getText() + "");

        String strRoundType = spRoundType.getSelectedItem().toString();
        if (strRoundType.equals("常规巡护")) {
            AppSetting.curRound.setRoundType(21);
        } else if (strRoundType.equals("稽查巡护")) {
            AppSetting.curRound.setRoundType(22);
            AppSetting.curRound.setDutyId(etAreaDuty.getText() + "");
        } else if (strRoundType.equals("责任区巡护")) {
            AppSetting.curRound.setRoundType(23);
            AppSetting.curRound.setDutyId(etAreaDuty.getText()  + "");
        } else {
            AppSetting.curRound.setRoundType(1);
        }
        if (mPhotoNameList.size() > 0) {
            for (String photoName : mPhotoNameList) {
                PhotoEntity photoEntity = new PhotoEntity();
                photoEntity.setBelongTo(AppSetting.curRound.getId());
                photoEntity.setPhotoName(photoName);
                photoEntity.setPhotoType("巡护");
                photoEntity.setSaveTime(new Date());
                photoEntity.setUserID(AppSetting.curUserKey);
                try {
                    PhotoManager.getInstance().savePhoto(photoEntity);
                } catch (Exception ex) {
                    Toast.makeText(this, "保存照片信息失败！" , Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    private void takePhoto() {

        if (!Tools.ExistFile(AppSetting.photoPath)) {
            boolean createPath = (new File(AppSetting.photoPath)).mkdirs();
            Log.i("createPath", createPath + "");
        }

        if (!Tools.ExistFile(AppSetting.smallPhotoPath)) {
            (new File(AppSetting.smallPhotoPath)).mkdirs();
        }

        Intent photoCamera = new Intent(this, PhotoCamera.class);
        Log.i("拍照", "打开相机");
        photoCamera.putExtra("PhotoPath", AppSetting.photoPath);
        photoCamera.putExtra("TempPhoto", "TempPhoto.jpg");
        photoCamera.putExtra("from", "roundActivity");
        this.startActivity(photoCamera);
    }

    //拍照完成后回调
    public void photoCallbackResult(String requestCode, Object ExtraStr) {

        if (!requestCode.equals("1")) {
            Log.i("事件拍照回调", "回调校验码：" + requestCode);
            return;
        }

        if (!Tools.ExistFile(tempPhotoName)) {
            Log.i("事件拍照回调", "没有临时文件写入：" + requestCode);
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String PhotoFileName = simpleDateFormat.format(new java.util.Date()) + ".jpg";

        File f1 = new File(tempPhotoName.toString());
        boolean isFailed = true;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inMutable = true;
            options.inInputShareable = true;
            FileInputStream iSteam = new FileInputStream(tempPhotoName);
            Bitmap bitmap = BitmapFactory.decodeStream(iSteam, null, options);
            iSteam.close();
            Log.v("tag", "读取照片。。。");

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            String dt = Tools.GetSystemDate();
            String strCamerTime = "拍摄时间：" + dt;
            Canvas canvasTemp = new Canvas(bitmap);

            Paint p = new Paint();
            String familyName = "宋体";
            Typeface font = Typeface.create(familyName, Typeface.BOLD);
            p.setColor(Color.RED);
            p.setTypeface(font);
            p.setTextSize(30);
            canvasTemp.drawText(strCamerTime, 8, h - 100, p);

            if (AppSetting.curUser != null && AppSetting.curUser.getLoginName() != null) {
                canvasTemp.drawText("拍摄者：" + AppSetting.curUser.getLoginName(), 8, h - 50, p);
            }


            String pswz = "拍摄位置：未定位";
            if (PubVar.m_GPSLocate != null && PubVar.m_GPSLocate.m_LocationEx != null && PubVar.m_GPSLocate.m_LocationEx.GetGpsFixMode() == lkGpsFixMode.en3DFix &&
                    PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude() > 0.0001 && PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude() > 0.0001) {
                try {
                    String[] Coor = PubVar.m_GPSLocate.getJWGPSCoordinate().split(",");
                    String jd = Tools.GetDDMMSS(Tools.ConvertToDouble(Coor[0]));
                    String wd = Tools.GetDDMMSS(Tools.ConvertToDouble(Coor[1]));
                    pswz = "拍摄位置：" + wd + "," + jd + "," + PubVar.m_GPSLocate.getGPSCoordinate().getZ();

                    Log.v("tag", "添加拍摄位置。。。");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            canvasTemp.drawText(pswz, 8, h - 150, p);

            canvasTemp.save();
            canvasTemp.restore();

            FileOutputStream fos = new FileOutputStream(f1);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            fos = null;

            FileInputStream iSteam2 = new FileInputStream(tempPhotoName);
            Bitmap bitmap2 = BitmapFactory.decodeStream(iSteam2);
            File smallF = new File(AppSetting.smallPhotoPath + "/" + PhotoFileName);
            FileOutputStream f = new FileOutputStream(smallF);
            Bitmap b = Bitmap.createScaledBitmap(bitmap2, bitmap2.getWidth() / 6, bitmap2.getHeight() / 6, false);
            b.compress(Bitmap.CompressFormat.JPEG, 100, f);

            isFailed = false;


        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        f1.renameTo(new File(AppSetting.photoPath + "/" + PhotoFileName));
        mPhotoNameList.add(PhotoFileName);
        photoShowTool.showPhotos(this, mPhotoNameList, gridView);

        if (isFailed) {
            return;
        }

        final String fileName = AppSetting.photoPath + "/" + PhotoFileName;
        final JSONObject exif = (JSONObject) ExtraStr;
        //存储exif信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (PubVar.m_GPSLocate != null && PubVar.m_GPSLocate.m_LocationEx != null) {
                        String[] Coor = PubVar.m_GPSLocate.getJWGPSCoordinate().split(",");

                        ExifInterface exifInfo = new ExifInterface(fileName);
                        exifInfo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
                        exifInfo.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
                        exifInfo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, Tools.ConvertToSexagesimal(Coor[0]));
                        exifInfo.setAttribute(ExifInterface.TAG_GPS_LATITUDE, Tools.ConvertToSexagesimal(Coor[1]));
                        String[] GPSDateTime = PubVar.m_GPSLocate.getGPSDateForPhotoFormat();
                        exifInfo.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, GPSDateTime[1]);
                        exifInfo.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, GPSDateTime[0]);

                        exif.put("lat:", PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude());
                        exif.put("lon:", PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude());
                        exif.put("gpsTime", PubVar.m_GPSLocate.m_LocationEx.GetGpsDate() + " " + PubVar.m_GPSLocate.m_LocationEx.GetGpsTime());


                        Log.d("exif save", exif.toString());
                        exifInfo.setAttribute(ExifInterface.TAG_USER_COMMENT, exif.toString());
                        exifInfo.saveAttributes();
                    }
                } catch (Exception io) {

                }


            }
        }).start();
    }

    private void uploadPhotos() {
        for (final String fileName : mPhotoNameList) {
            File photo = new File(AppSetting.photoPath + "/" + fileName);
            if (photo == null) {
                continue;
            }
            RequestBody userUid = RequestBody.create(MediaType.parse("text/plain"), AppSetting.curRound.getServerId());
            Map<String, RequestBody> map = new HashMap<>();
            map.put("roundId", userUid);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), photo);
            map.put("uploadedFiles\"; filename=\"" + photo.getName(), fileBody);

            String imageInfo = "";
            try {

                ExifInterface exifInfo = new ExifInterface(AppSetting.photoPath + "/" + fileName);
                imageInfo = exifInfo.getAttribute(ExifInterface.TAG_USER_COMMENT);
                Log.d("exif read", imageInfo);
            } catch (Exception ex) {
                Toast.makeText(RoundActivity.this, "照片信息读取失败", Toast.LENGTH_SHORT).show();
            }
            if (imageInfo == null) {
                imageInfo = "";
            }
            RequestBody imageExif = RequestBody.create(MediaType.parse("text/plain"), imageInfo);
            map.put("imageInfo", imageExif);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            Call<ResponseBody> newPhoto = RetrofitHttp.getRetrofit(builder.build()).uploadPicture("UploadRoundPhoto", map);
            newPhoto.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response == null || response.body() == null) {
                            return;
                        }
                        JSONObject result = new JSONObject(response.body().string());
                        if (result.get("success").equals(Boolean.TRUE)) {
                            try {
                                PhotoEntity photoEntity = PhotoManager.getInstance().getPhotoEntity(fileName);
                                photoEntity.setUploadStatus(1);
                                photoEntity.setUploadTime(new Date());
                                PhotoManager.getInstance().savePhoto(photoEntity);
                            } catch (Exception ex) {
                                Toast.makeText(AppSetting.applicaton.getApplicationContext(), "保存照片信息失败！" , Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.e("上传照片", response.body().string());
                        }
                    } catch (Exception ex) {

                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(RoundActivity.this, "上传照片失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void deletePhoto() {
        ListAdapter adapter = gridView.getAdapter();

        List<String> delPhotos = new ArrayList<String>();

        if (adapter != null && adapter.getCount() > 0) {

            for (int i = 0; i < adapter.getCount(); i++) {
                HashMap<String, Object> map = (HashMap<String, Object>) adapter.getItem(i);
                View view = gridView.getChildAt(i);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_select);
                if (checkBox.isChecked()) {
                    String fileName = map.get("image") + "";
                    File file = new File(fileName);
                    if (file.exists()) {
                        file.delete();
                    }

                    String bigFileName = fileName.replace("/samllPhoto", "");
                    File bigfile = new File(bigFileName);
                    if (bigfile.exists()) {
                        bigfile.delete();
                    }
                    bigfile.delete();


                    for (String f : mPhotoNameList) {
                        if (f.equals(map.get("text") + "")) {
                            delPhotos.add(f);
                        }
                    }
                }
            }

            for (String f : delPhotos) {
                mPhotoNameList.remove(f);
            }

            photoShowTool.showPhotos(this, mPhotoNameList, gridView);
        }
    }

    private void prompt(Call<ResponseBody> newRound) {
        newRound.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> reg, Response<ResponseBody> response) {

                try {
                    if (response.body() == null) {
                        Log.e("upload Round fail", " response.body() is null ");
                        Toast.makeText(RoundActivity.this, "网络无法连通，待网络恢复后进行数据补传", Toast.LENGTH_SHORT).show();
                        closeActivity();
                        return;
                    }

                    JSONObject result = new JSONObject(response.body().string());
                    if (result.get("success").equals(Boolean.TRUE)) {
                        Log.e("finish Round upload", " result: " + result);
                        AppSetting.curRound.setUploadStatus(1);//上传状态改为已上传
                        AppSetting.curRound.setRoundStatus(1);
                        AppSetting.curRound.setServerId(result.get("data").toString());
                        try {
                            PatrolManager.getInstance().savePatrol(AppSetting.curRound);
                        } catch (Exception ex) {

                        }
//                        if (DatabaseCreator.getInstance(AppSetting.applicaton).getDatabase() != null) {
////                            DatabaseCreator.getInstance(AppSetting.applicaton).getDatabase().roundDao().insertRound(AppSetting.curRound);
//                        }
                        Toast.makeText(RoundActivity.this, "巡护状态上传成功", Toast.LENGTH_SHORT);
                    } else {

                        Toast.makeText(RoundActivity.this, "巡护状态更新未完成，待网络恢复后进行数据补传", Toast.LENGTH_SHORT).show();
                        Log.e("finish Round upload", " result: " + result);
                        closeActivity();
                        return;

                    }

                    if (mPhotoNameList.size() > 0) {
                        uploadPhotos();
                    }
                    closeActivity();

                } catch (Exception io) {
                    Toast.makeText(RoundActivity.this, "数据上传失败，待网络恢复后进行数据补传", Toast.LENGTH_SHORT).show();
                    Log.e("finish Round upload", " exception: " + io.getMessage());
                    closeActivity();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> reg, Throwable t) {
                Log.e("finish Round upload", " exception: " + t.getMessage());
                Toast.makeText(RoundActivity.this, "巡护完成状态未上传到服务器，待网络恢复后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void closeActivity() {
        try {
            if (mSavingingDlg != null) {
                if (mSavingingDlg.isShowing()) {
                    mSavingingDlg.dismiss();
                }
            }
        } catch (Exception ex) {

        }


        finish();
    }

    /* 初始化正在保存对话框 */
    private void initSavingDlg() {

        mSavingingDlg = new Dialog(this, R.style.loginingDlg);
        mSavingingDlg.setContentView(R.layout.logindlg);

        Window window = mSavingingDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // 获取和mLoginingDlg关联的当前窗口的属性，从而设置它在屏幕中显示的位置

        // 获取屏幕的高宽
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int cxScreen = dm.widthPixels;
        int cyScreen = dm.heightPixels;

        int height = (int) getResources().getDimension(
                R.dimen.loginingdlg_height);// 高42dp
        int lrMargin = (int) getResources().getDimension(
                R.dimen.loginingdlg_lr_margin); // 左右边沿10dp
        int topMargin = (int) getResources().getDimension(
                R.dimen.loginingdlg_top_margin); // 上沿20dp

        params.y = (-(cyScreen - height) / 2) + topMargin; // -199
        /* 对话框默认位置在屏幕中心,所以x,y表示此控件到"屏幕中心"的偏移量 */
        ((TextView) mSavingingDlg.findViewById(R.id.tv_loading_show)).setText("正在上传数据");
        params.width = cxScreen;
        params.height = height;
        // width,height表示mLoginingDlg的实际大小
        mSavingingDlg.setCanceledOnTouchOutside(false);
//        mSavingingDlg.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog
    }

}
