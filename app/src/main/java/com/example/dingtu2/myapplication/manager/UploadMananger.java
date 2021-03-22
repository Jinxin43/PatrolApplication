package com.example.dingtu2.myapplication.manager;

import android.os.Handler;
import android.support.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import com.DingTu.Base.ICallback;
import com.example.dingtu2.myapplication.AppSetting;
import com.example.dingtu2.myapplication.RoundActivity;
import com.example.dingtu2.myapplication.db.xEntity.PatrolEntity;
import com.example.dingtu2.myapplication.db.xEntity.PatrolPointEntity;
import com.example.dingtu2.myapplication.db.xEntity.PhotoEntity;
import com.example.dingtu2.myapplication.db.xEntity.TraceEntity;
import com.example.dingtu2.myapplication.http.Httpmodel.HttpPatrolPointModel;
import com.example.dingtu2.myapplication.http.Httpmodel.HttpRoundModel;
import com.example.dingtu2.myapplication.http.Httpmodel.HttpTraceModel;
import com.example.dingtu2.myapplication.http.RetrofitHttp;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dingtu2 on 2018/4/19.
 */

public class UploadMananger {

    private static UploadMananger mInstantce;

    private UploadMananger() {
    }

    public static UploadMananger getInstance() {
        synchronized (UploadMananger.class) {
            if (mInstantce == null) {
                mInstantce = new UploadMananger();
            }
        }
        return mInstantce;
    }

    public void uploadRound(final PatrolEntity roundEntity, final ICallback myCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpRoundModel httpRoundModel = new HttpRoundModel();
                httpRoundModel.setStartTime(roundEntity.getStartTime().getTime());
                httpRoundModel.setRoundName(roundEntity.getRoundName());
                httpRoundModel.setRoundType(roundEntity.getRoundType());
                httpRoundModel.setRoundStatus(roundEntity.getRoundStatus());
                httpRoundModel.setUserId(roundEntity.getUserID());
                httpRoundModel.setDescription(roundEntity.getSummary());
                httpRoundModel.setWeather(roundEntity.getWeather());
                httpRoundModel.setDutyId(roundEntity.getDutyId());
                httpRoundModel.setUserNames(roundEntity.getUserNames());
                httpRoundModel.setContent(roundEntity.getContent());
                httpRoundModel.setLineOrZone(roundEntity.getLineOrZone());
                try {
                    if (roundEntity.getRoundStatus() == 1) {
                        httpRoundModel.setEndTime(roundEntity.getEndTime().getTime());
                    }
                } catch (Exception ex) {
                        Toast.makeText(AppSetting.applicaton.getApplicationContext(),"结束时间："+ex.getMessage(),Toast.LENGTH_LONG).show();
                }


                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                promptRount(roundEntity, RetrofitHttp.getRetrofit(builder.build()).CreateRound("CreateNewRound", httpRoundModel), myCallback);
            }
        }).start();
    }


    private void promptRount(final PatrolEntity roundEntity, Call<ResponseBody> newRound, final ICallback myCallback) {
        newRound.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> reg, Response<ResponseBody> response) {

                try {

                    if (response.body() == null) {
                        myCallback.OnClick("failed", AppSetting.curRound);
                        Log.e("Start Round upload", " response.body() is null ");
                        return;
                    }

                    JSONObject result = new JSONObject(response.body().string());
                    if (result.get("success").equals(Boolean.TRUE)) {
                        Log.e("upload round", " result: " + result);

                        roundEntity.setServerId(result.get("data").toString());
                        roundEntity.setUploadStatus(1);
                        PatrolManager.getInstance().savePatrol(roundEntity);
                        myCallback.OnClick("success", roundEntity.getServerId());

                    } else {
                        myCallback.OnClick("failed", roundEntity);
                        Log.e("upload round failed", " result: " + result);
                    }
                } catch (Exception io) {
                    Log.e("upload round fail", " exception: " + io.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> reg, Throwable t) {
                myCallback.OnClick("failed", AppSetting.curRound);
                Log.e("Round upload failed", " exception: " + t.getMessage());
            }
        });
    }

    public void uploadPatrolPoint(final PatrolPointEntity pointEntity, final String patrolServerId, final ICallback callback) {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                HttpPatrolPointModel httpPointModel = new HttpPatrolPointModel();
                httpPointModel.setPatrolId(patrolServerId);
                httpPointModel.setGpsTime(pointEntity.getGpsTime().getTime());
                if(pointEntity.getLatitude()>0&&pointEntity.getLatitude()>0) {
                    httpPointModel.setHeight(pointEntity.getHeight() + "");
                    httpPointModel.setLatitude(pointEntity.getLatitude() + "");
                    httpPointModel.setLongitude(pointEntity.getLongitude() + "");
                    httpPointModel.setX(pointEntity.getX());
                    httpPointModel.setY(pointEntity.getY());
                }
                httpPointModel.setSrid(pointEntity.getSrid());
                httpPointModel.setType(pointEntity.getPointType());

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                promptAddPoint(RetrofitHttp.getRetrofit(builder.build()).AddPatrolPoint("AddPatrolPoint", httpPointModel), pointEntity, callback);

            }
        });
    }

    private void promptAddPoint(Call<ResponseBody> newPoint, final PatrolPointEntity pointEntity, final ICallback myCallback) {
        newPoint.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    JSONObject result = new JSONObject(response.body().string());
                    if (result.get("success").equals(Boolean.TRUE)) {
                        Log.e("upload Point", " result: " + result);

//                        Toast.makeText(AppSetting.applicaton.getApplicationContext(),"巡护点上传完成",Toast.LENGTH_SHORT).show();
                        pointEntity.setUploadStatus(1);
                        PatrolManager.getInstance().savePatrolPoint(pointEntity);
                        myCallback.OnClick("success", null);
                    } else {
                        myCallback.OnClick("failed", null);
//                        Toast.makeText(AppSetting.applicaton.getApplicationContext(),"巡护点上传失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    myCallback.OnClick("failed", null);
                    Toast.makeText(AppSetting.applicaton.getApplicationContext(), "巡护点上传失败" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                myCallback.OnClick("failed", null);
                Toast.makeText(AppSetting.applicaton.getApplicationContext(), "网络异常,网络恢复后补传", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadTraces(TraceEntity traceEntity, String serverPatrolId, final ICallback callback) {
        HttpTraceModel httpTraceModel = new HttpTraceModel();
        httpTraceModel.setUserId(traceEntity.getUserID());
        httpTraceModel.setRoundId(traceEntity.getServerRoundId());
        if(traceEntity.getLatitude()>0&&traceEntity.getLongitude()>0) {
            httpTraceModel.setLatitude(traceEntity.getLatitude() + "");
            httpTraceModel.setLongitude(traceEntity.getLongitude() + "");
        }
        httpTraceModel.setGpsTime(traceEntity.getGpsTime().getTime());
        httpTraceModel.setHeight(traceEntity.getHeight() + "");
        httpTraceModel.setX(traceEntity.getX());
        httpTraceModel.setY(traceEntity.getY());
        httpTraceModel.setSrid(traceEntity.getSrid());


        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Call<ResponseBody> newTraceCall = RetrofitHttp.getRetrofit(builder.build()).uploadTrace("InsertTrackData", httpTraceModel);
        final TraceEntity trace = traceEntity;
        newTraceCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() == null) {
                    Log.e("upload trace", "response.body() is null");
                    callback.OnClick("failed", callback);
                    return;
                }

                try {
                    if (response.body().string().contains("true")) {
                        Log.d("上传轨迹", trace.getGpsTime().toString());
                        trace.setUploadStatus(1);
                        TraceManager.getInstance().SaveTrace(trace);
                        callback.OnClick("success", callback);
                    } else {
                        callback.OnClick("failed", callback);
                        Log.e("上传轨迹", response.body().string());

                    }
                } catch (IOException io) {
                    callback.OnClick("failed", callback);
                    Log.e("上传轨迹失败", io.getMessage());
                    io.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.OnClick("failed", callback);
                Log.e("上传轨迹失败：", t.getMessage());
            }
        });
    }

    public void uploadPhoto(final String fileName)
    {
        File photo = new File(AppSetting.photoPath + "/" + fileName);
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
        }
        if(imageInfo == null)
        {
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
                    JSONObject result = new JSONObject(response.body().string());
                    if (result.get("success").equals(Boolean.TRUE)) {

                        try
                        {
                            PhotoEntity photoEntity = PhotoManager.getInstance().getPhotoEntity(fileName);
                            photoEntity.setUploadStatus(1);
                            photoEntity.setUploadTime(new Date());
                            PhotoManager.getInstance().savePhoto(photoEntity);
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "更新照片失败："+ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(AppSetting.applicaton.getApplicationContext(), response.body().toString(), Toast.LENGTH_LONG).show();
                        Log.e("上传照片", response.body().string());
                    }


                } catch (Exception ex) {
                    Toast.makeText(AppSetting.applicaton.getApplicationContext(), "上传照片："+ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AppSetting.applicaton.getApplicationContext(), "上传照片失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
