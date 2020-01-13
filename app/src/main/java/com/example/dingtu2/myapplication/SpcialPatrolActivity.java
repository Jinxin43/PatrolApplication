package com.example.dingtu2.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Cargeometry.Coordinate;
import com.DingTu.Cargeometry.Part;
import com.DingTu.Cargeometry.Point;
import com.DingTu.Cargeometry.Polygon;
import com.DingTu.CoordinateSystem.CoorSystem;
import com.DingTu.Data.GpsDataObject;
import com.DingTu.Dataset.Dataset;
import com.DingTu.Map.StaticObject;
import com.example.dingtu2.myapplication.db.XDbManager;
import com.example.dingtu2.myapplication.db.entity.PatrolCommandEntity;
import com.example.dingtu2.myapplication.http.Httpmodel.RequestId;
import com.example.dingtu2.myapplication.http.RetrofitHttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpcialPatrolActivity extends AppCompatActivity {

    private Coordinate mGeoCenter;
    private String from;
    private String patrolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spcial_patrol);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.txPatrolPosition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGeoCenter != null) {
                    Log.d("txPatrolPosition", mGeoCenter.toString());

                    if (from.equals("HTTP")) {
                        //是否需要判断来自于那个页面
                        Intent mainIntent = new Intent(SpcialPatrolActivity.this, MainActivity.class);
                        mainIntent.putExtra("from", "patrolCenter");
                        mainIntent.putExtra("patrolCenter", new double[]{mGeoCenter.getX(), mGeoCenter.getY()});
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Intent mainIntent = new Intent();
                        mainIntent.putExtra("from", "patrolCenter");
                        mainIntent.putExtra("patrolCenter", new double[]{mGeoCenter.getX(), mGeoCenter.getY()});
                        setResult(1, mainIntent);
                        finish();
                    }
                } else {
                    Toast.makeText(SpcialPatrolActivity.this, "巡护指令没有设置巡护区域", Toast.LENGTH_SHORT).show();
                }


            }
        });

        findViewById(R.id.txStartPatrol).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("txStartPatrol", patrolId);
                if (patrolId == null || patrolId.isEmpty()) {
                    Toast.makeText(SpcialPatrolActivity.this, "无法获得指令ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (AppSetting.curRound != null) {
                    Toast.makeText(SpcialPatrolActivity.this, "正在巡护中，请先停止正在进行的巡护！", Toast.LENGTH_LONG).show();
                    return;
                }

                if (from.equals("HTTP")) {
                    Intent mainIntent = new Intent(SpcialPatrolActivity.this, MainActivity.class);
                    mainIntent.putExtra("from", "newPatrol");
                    mainIntent.putExtra("patrolID", patrolId);
                    if (mGeoCenter != null) {
                        mainIntent.putExtra("patrolCenter", new double[]{mGeoCenter.getX(), mGeoCenter.getY()});
                    }

                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent mainIntent = new Intent();
                    mainIntent.putExtra("from", "newPatrol");
                    mainIntent.putExtra("patrolID", patrolId);
                    if (mGeoCenter != null) {
                        mainIntent.putExtra("patrolCenter", new double[]{mGeoCenter.getX(), mGeoCenter.getY()});
                    }
                    setResult(2, mainIntent);
                    finish();
                }


            }
        });

        Log.d("Patrol", "PatrolCommand start");
        patrolId = this.getIntent().getStringExtra("Id");
        from = this.getIntent().getStringExtra("from");
        Log.d("SpcialPatrolId", patrolId);
        if (from.equals("HTTP")) {
            getCommandFromHttp(patrolId);
        } else {
            getCommandFromDB(patrolId);
        }

    }

    private void getCommandFromHttp(String patrolId) {
        if (patrolId != null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            RequestId requestId = new RequestId();
            requestId.setRequestId(patrolId);
            Call<ResponseBody> newPatrol = RetrofitHttp.getRetrofit(builder.build()).GetSpecialPatrolById("GetSpecialPatrolById", requestId);
            newPatrol.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject result = new JSONObject(response.body().string());
                        if (result.get("success").equals(Boolean.TRUE)) {
                            JSONObject jsonObject = new JSONObject(result.getString("data"));
                            String geom = jsonObject.get("geom") + "";
                            String geomtype = jsonObject.get("geomtype") + "";
                            Log.d("Patrol", "geom:" + geom);

                            PatrolCommandEntity patrolCommand = new PatrolCommandEntity();
                            patrolCommand.setGeomType(geomtype);
                            patrolCommand.setPatrolDescription(jsonObject.getString("destription"));
                            patrolCommand.setCreateTime(jsonObject.getString("createTime"));
//                            patrolCommand.setCreatorUser(jsonObject.getString("creatorUser"));
                            patrolCommand.setCreatorUser("管理员");
                            patrolCommand.setAttachments(jsonObject.getString("attachments"));
                            patrolCommand.setGeomId(jsonObject.getString("Id"));
                            patrolCommand.setTitle(jsonObject.getString("title"));
                            patrolCommand.setUserId(jsonObject.getString("userId"));
                            patrolCommand.setStartTime(jsonObject.getString("startTime"));
                            patrolCommand.setEndTime(jsonObject.getString("endTime"));
                            patrolCommand.setSendType(jsonObject.getString("sendtype"));
                            patrolCommand.setReadStatus(true);

                            showPatrolCommandOnUI(patrolCommand);

                            if (geomtype.equals("Point")) {
                                JSONObject pointJson = new JSONObject(jsonObject.getString("geom"));
                                double x = pointJson.getDouble("X");
                                double y = pointJson.getDouble("Y");

                                CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
                                Coordinate coord = StaticObject.soProjectSystem.WGS84ToXY(x, y, 0);

                                String SRID = pointJson.getString("SRID");
                                Point p = new Point(coord.getX(), coord.getY());

                                Dataset pDataset = PubVar.m_Workspace.GetDatasetById(AppSetting.SpecialPatrolPointLayerId);
                                GpsDataObject dataObj = new GpsDataObject();
                                dataObj.SetDataset(pDataset);
                                dataObj.SetSYS_TYPE("指令巡护点");
                                dataObj.SetSYS_STATUS("0");
                                dataObj.setSYS_OID(patrolCommand.getGeomId());
                                int SYS_ID = dataObj.SaveGeoToDb(p, 0, 0);
                                patrolCommand.setGeomByte(Tools.GeometryToByte(p));
                                if (SYS_ID != -1) {
                                    Log.d("SavePatrolGeo", SYS_ID + "");
                                    mGeoCenter = coord;
                                    PubVar.m_Map.setCenter(coord);
                                    PubVar.m_Map.Refresh();
                                }
                                saveCommandToDB(patrolCommand);
                            } else if (patrolCommand.getGeomType().equals("Polygon")) {
                                JSONArray polyArray = new JSONArray(geom);
                                Polygon pGeometry = new Polygon();
                                for (int j = 0; j < polyArray.length(); j++) {
                                    JSONArray pointArray = polyArray.getJSONArray(j);
                                    Part part = new Part();
                                    pGeometry.AddPart(part);
                                    List<Coordinate> pointList = new ArrayList<Coordinate>();
                                    for (int k = 0; k < pointArray.length(); k++) {
                                        JSONObject point = pointArray.getJSONObject(k);
                                        double x = point.getDouble("X");
                                        double y = point.getDouble("Y");
                                        Coordinate coor = StaticObject.soProjectSystem.WGS84ToXY(x, y, 0);
                                        if (coor != null) {
                                            pointList.add(coor);
                                            Log.d("coor", coor.getX() + "   " + coor.getY());
                                        } else {
                                            Log.d("coor", "coor is null");
                                        }

                                    }
                                    pointList.add(pointList.get(0).Clone());
                                    pGeometry.GetPartAt(0).setVertext(pointList);
                                }
                                pGeometry.CalEnvelope();
                                mGeoCenter = pGeometry.getCenterPoint();
                                Dataset pDataset = PubVar.m_Workspace.GetDatasetById(AppSetting.SpecialPatrolPolyLayerId);
                                GpsDataObject dataObj = new GpsDataObject();
                                dataObj.SetDataset(pDataset);
                                dataObj.SetSYS_TYPE("指令巡护面");
                                dataObj.SetSYS_STATUS("0");
                                dataObj.setSYS_OID(patrolCommand.getGeomId());

                                int SYS_ID = dataObj.SaveGeoToDb(pGeometry, pGeometry.getLength(true), pGeometry.getArea(true));
                                patrolCommand.setGeomByte(Tools.GeometryToByte(pGeometry));
                                Coordinate coord = pGeometry.getCenterPoint();
                                if (SYS_ID != -1) {
                                    saveCommandToDB(patrolCommand);

                                    Log.d("SavePatrolGeo", patrolCommand.getGeomId());
                                    PubVar.m_Map.setCenter(coord);
                                    PubVar.m_Map.Refresh();
                                }

                            } else//线
                            {

                            }


                        } else {
                            Toast.makeText(SpcialPatrolActivity.this, "获取巡护指令失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        Log.e("Paser Geo", ex.getMessage() + ex.getStackTrace().toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } else {
            Toast.makeText(this, "没有正确的指令ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCommandFromDB(String patrolId) {
        if (patrolId != null) {
            try {
                PatrolCommandEntity patrolCommand = XDbManager.getDb().selector(PatrolCommandEntity.class).where("geomId", "in", new String[]{patrolId}).findFirst();
                if (patrolCommand != null) {
                    showPatrolCommandOnUI(patrolCommand);
                }
            } catch (Exception ex) {
                Log.d("GetCommandFromDB", ex.getMessage());
                Toast.makeText(this, "查询巡护指令错误", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void saveCommandToDB(PatrolCommandEntity patrolCommand) {
        try {
            XDbManager.getDb().save(patrolCommand);
        } catch (Exception ex) {
            Log.e("save command", ex.getMessage());
            Toast.makeText(SpcialPatrolActivity.this, "保存巡护指令失败", Toast.LENGTH_SHORT).show();
        }


    }

    private void showPatrolCommandOnUI(PatrolCommandEntity patrolCommand) {
        ((EditText) findViewById(R.id.etPatrolName)).setText(patrolCommand.getTitle());
        ((EditText) findViewById(R.id.etPatrolDescription)).setText(patrolCommand.getPatrolDescription());
        ((EditText) findViewById(R.id.etPatrolDescription)).setText(patrolCommand.getPatrolDescription());
        ((EditText) findViewById(R.id.etCreateUserName)).setText(patrolCommand.getCreatorUser());
        ((EditText) findViewById(R.id.etPatrolTime)).setText(patrolCommand.getStartTime() + "-" + patrolCommand.getEndTime());

    }

    private void ViewClose() {
        this.finish();
    }
}
