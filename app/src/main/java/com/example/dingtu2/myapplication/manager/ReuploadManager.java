package com.example.dingtu2.myapplication.manager;

import android.support.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import com.DingTu.Base.ICallback;
import com.DingTu.Map.StaticObject;
import com.example.dingtu2.myapplication.AppSetting;
import com.example.dingtu2.myapplication.db.xEntity.PatrolEntity;
import com.example.dingtu2.myapplication.db.xEntity.PatrolEventEntity;
import com.example.dingtu2.myapplication.db.xEntity.PatrolPointEntity;
import com.example.dingtu2.myapplication.db.xEntity.PhotoEntity;
import com.example.dingtu2.myapplication.db.xEntity.TraceEntity;
import com.example.dingtu2.myapplication.http.Httpmodel.HttpEventModel;
import com.example.dingtu2.myapplication.http.RetrofitHttp;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dingtu2 on 2018/5/9.
 */

public class ReuploadManager {

    private List<PatrolEntity> patrols = new ArrayList<PatrolEntity>();
    private List<PatrolPointEntity> points = new ArrayList<PatrolPointEntity>();
    private List<TraceEntity> traces = new ArrayList<TraceEntity>();
    private List<PhotoEntity> photos = new ArrayList<>();
    private List<PatrolEventEntity> events = new ArrayList<>();

    public void reuploadAllRecords(String userKey) {
        patrols = PatrolManager.getInstance().getUnloadPatrols(userKey);
        if (patrols != null && patrols.size() > 0) {
            uploadOnePatrol(0);
        }
    }

    private void uploadOnePatrol(final int index) {
        final boolean hasNext = index < (patrols.size() - 2);
        UploadMananger.getInstance().uploadRound(patrols.get(index), new ICallback() {
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                if (hasNext) {
                    uploadOnePatrol(index + 1);
                }
            }
        });
    }

    public void reUploadAllPoint(String userKey) {
        points = PatrolManager.getInstance().getUnloadPatrolPoints(userKey);
        if (points != null && points.size() > 0) {
            uploadPointOneByOne(0);
        }
    }

    public void reUploadAllEvent()
    {
        events = PatrolManager.getInstance().getUnloadPatrolEvents();
        if(events != null && events.size()>0)
        {
            uploadEventOneByOne(0);
        }
    }

    private void uploadEventOneByOne(final int eventIndex)
    {
        final boolean hasNext = eventIndex < (events.size() - 2);
        PatrolEventEntity eventEntity = events.get(eventIndex);

        try
        {

            if(PatrolManager.getInstance().getPatrolById(eventEntity.getRoundId()) == null)
            {
                if(hasNext){
                    uploadEventOneByOne(eventIndex+1);
                }
                return;
            }
            String serverPatrolID = PatrolManager.getInstance().getPatrolById(eventEntity.getRoundId()).getServerId();
            if (serverPatrolID == null || serverPatrolID.length() == 0) {

                if(hasNext){
                    uploadEventOneByOne(eventIndex+1);
                }
                return;
            }

            HttpEventModel eventModel = new HttpEventModel();
            eventModel.setUserId(AppSetting.curUserKey);
            eventModel.setDescription(eventEntity.getEventDescription());
            eventModel.setRoundId(serverPatrolID);
            eventModel.setEventTime(eventEntity.getEventTime().getTime());
            if(eventEntity.getEventLat()>0&&eventEntity.getEventLon()>0) {
                eventModel.setLatitude(eventEntity.getEventLat() + "");
                eventModel.setLongitude(eventEntity.getEventLon() + "");
            }
            eventModel.setEventPOI(eventEntity.getEventPOI());
            eventModel.setHeight(eventEntity.getAltitude() + "");
            eventModel.setType(eventEntity.getEventType() + "");
            eventModel.setGpsTime(eventEntity.getEventTime().getTime());
            String name= StaticObject.soProjectSystem.GetCoorSystem().GetName();
            if(name.equals("西安80坐标")){
                eventModel.setSrid("2381");
            }else if(name.equals("北京54坐标")){
                eventModel.setSrid("2433");
            }else if(name.equals("2000国家大地坐标系")){
                eventModel.setSrid("4545");
            }else if(name.equals("WGS-84坐标")){
                eventModel.setSrid("4326");
            }

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            RetrofitHttp.getRetrofit(builder.build()).CreateEvent("CreateEvent", eventModel).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> reg, Response<ResponseBody> response) {

                    if(hasNext){
                        uploadEventOneByOne(eventIndex+1);
                    }
                    try {
                        JSONObject result = new JSONObject(response.body().string());
                        if (result.get("success").equals(Boolean.TRUE)) {
                            Log.e("finish Event upload", " result: " + result);
                            PatrolEventEntity eventEntity = events.get(eventIndex);
                            eventEntity.setUploadStatus(1);//已上传
                            eventEntity.setServerId(result.get("data").toString());
                            PatrolManager.getInstance().savePatrolEvent(eventEntity);
                        Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传一个巡护发现", Toast.LENGTH_LONG).show();

                        } else {
                        Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传巡护发现失败", Toast.LENGTH_LONG).show();
                            Log.e("Event upload failed", " result: " + result);
                        }
                    } catch (Exception io) {
                   Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传巡护发现失败", Toast.LENGTH_LONG).show();
                        Log.e("Event upload failed", " exception: " + io.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> reg, Throwable t) {
                    Log.e("finish Round upload", " exception: " + t.getMessage());
                Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传巡护发现失败", Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传巡护失败：", Toast.LENGTH_LONG).show();
        }


    }

    public void reuploadPhoto()
    {
        photos = PhotoManager.getInstance().getUnuploadPhoto();
        if(photos != null && photos.size()>0){
            uploadPhotoOneByOne(0);
        }

    }
    private void uploadPhotoOneByOne(final int photoIndex)
    {

        final boolean hasNext = photoIndex < (photos.size() - 2);
        PhotoEntity photoEntity = photos.get(photoIndex);
        final String fileName = photoEntity.getPhotoName();

        try{
            File photo = new File(AppSetting.photoPath + "/" + fileName);

            if(photo == null){
                if(hasNext){
                    uploadPhotoOneByOne(photoIndex+1);
                }
                Log.e("找不到照片",fileName);
                return;
            }
            Map<String, RequestBody> map = new HashMap<>();
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), photo);

            String imageInfo = "";
            try {

                ExifInterface exifInfo = new ExifInterface(AppSetting.photoPath + "/" + fileName);
                imageInfo = exifInfo.getAttribute(ExifInterface.TAG_USER_COMMENT);
                Log.d("exif read", imageInfo);
            } catch (Exception ex) {
            }
            if(imageInfo == null){
                imageInfo = "";
            }
            RequestBody imageExif = RequestBody.create(MediaType.parse("text/plain"), imageInfo);
            map.put("imageInfo", imageExif);

            if(photoEntity.getPhotoType().equals("巡护")){

                PatrolEntity patrolEntity = PatrolManager.getInstance().getPatrolById(photoEntity.getBelongTo());
                if(patrolEntity == null || patrolEntity.getServerId() == null ||patrolEntity.getServerId().isEmpty())
                {
                    if(hasNext)
                    {
                        uploadPhotoOneByOne(photoIndex+1);
                    }
                    return;
                }
                RequestBody userUid = RequestBody.create(MediaType.parse("text/plain"), patrolEntity.getServerId());
                map.put("roundId", userUid);
                map.put("uploadedFiles\"; filename=\"" + photo.getName(), fileBody);

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                Call<ResponseBody> newPhoto = RetrofitHttp.getRetrofit(builder.build()).uploadPicture("UploadRoundPhoto", map);
                newPhoto.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if(hasNext){
                                uploadPhotoOneByOne(photoIndex+1);
                            }
                            JSONObject result = new JSONObject(response.body().string());
                            if (result.get("success").equals(Boolean.TRUE)) {
                                Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传了一张巡护照片", Toast.LENGTH_SHORT).show();
                                try{
                                    PhotoEntity photoEntity = PhotoManager.getInstance().getPhotoEntity(fileName);
                                    photoEntity.setUploadStatus(1);
                                    photoEntity.setUploadTime(new Date());
                                    PhotoManager.getInstance().updateUpload(photoEntity);
                                }
                                catch (Exception ex){
                                    Toast.makeText(AppSetting.applicaton.getApplicationContext(), "更新巡护照片失败："+ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("上传巡护照片", response.body().string());
                            }

                        } catch (Exception ex) {
                            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传巡护照片：", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传巡护照片失败：" , Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {

                PatrolEventEntity eventEntity = PatrolManager.getInstance().getPatrolEvent(photoEntity.getBelongTo());
                if(eventEntity== null || eventEntity.getServerId() == null||eventEntity.getServerId().isEmpty()){
                    if(hasNext){
                        uploadPhotoOneByOne(photoIndex+1);
                    }
                    return;
                }

                RequestBody eventId = RequestBody.create(MediaType.parse("text/plain"), eventEntity.getServerId());
                map.put("eventId", eventId);
                map.put("uploadedFiles\"; filename=\"" + photo.getName(), fileBody);

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                Call<ResponseBody> newPhoto = RetrofitHttp.getRetrofit(builder.build()).uploadEventPicture("UploadEventFile", map);
                newPhoto.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(hasNext){
                            uploadPhotoOneByOne(photoIndex+1);
                        }

                        try {
                            JSONObject result = new JSONObject(response.body().string());
                            if (result.get("success").equals(Boolean.TRUE)) {

                                Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传了一张发现照片", Toast.LENGTH_SHORT).show();
                                try{
                                    PhotoEntity photoEntity = PhotoManager.getInstance().getPhotoEntity(fileName);
                                    photoEntity.setUploadStatus(1);
                                    photoEntity.setUploadTime(new Date());
                                    PhotoManager.getInstance().savePhoto(photoEntity);
                                }
                                catch (Exception ex){
                                    Toast.makeText(AppSetting.applicaton.getApplicationContext(), "更新发现照片失败：", Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传了一张发现照片", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传发现照片失败：" , Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传发现照片失败", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传发现照片失败：" , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            if(hasNext){
                uploadPhotoOneByOne(photoIndex+1);
            }
            Toast.makeText(AppSetting.applicaton.getApplicationContext(), "补传照片失败：", Toast.LENGTH_LONG).show();
        }

    }


    private void uploadPointOneByOne(final int index) {
        final boolean hasNext = index < (points.size() - 2);
        PatrolPointEntity point = points.get(index);
        try {
            String serverPatrolID = PatrolManager.getInstance().getPatrolById(point.getRoundID()).getServerId();
            if (serverPatrolID != null && serverPatrolID.length() > 0) {
                UploadMananger.getInstance().uploadPatrolPoint(points.get(index), serverPatrolID, new ICallback() {

                            @Override
                            public void OnClick(String Str, Object ExtraStr) {
                                if (hasNext) {
                                    uploadPointOneByOne(index + 1);
                                }

                            }
                        }
                );
            }

        } catch (Exception ex) {

        }

    }

    public void reUploadTraces(String userKey) {
        traces = TraceManager.getInstance().getUnloadTraces();
        if (traces != null && traces.size() > 0) {
            uploadTraceOneByOne(0);
        }
    }

    public void uploadTraceOneByOne(final int index) {
        final boolean hasNext = index < (traces.size() - 2);
        TraceEntity trace = traces.get(index);

        if (trace.getServerRoundId() == null || trace.getServerRoundId().isEmpty()) {
            try {
                PatrolEntity patrol = PatrolManager.getInstance().getPatrolById(trace.getRoundID());
                if (patrol.getServerId() == null || patrol.getServerId().isEmpty()) {

                } else {
                    trace.setServerRoundId(patrol.getServerId());
                }
            } catch (Exception ex) {

            }

        }

        UploadMananger.getInstance().uploadTraces(trace, trace.getServerRoundId(), new ICallback() {
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                if (hasNext) {
                    uploadTraceOneByOne(index + 1);
                }

            }
        });

    }

    public void reloadAll() {
        try {
            reuploadAllRecords(AppSetting.curUserKey);
            reUploadAllPoint(AppSetting.curUserKey);
            reUploadAllEvent();
            reUploadTraces(AppSetting.curUserKey);
            reuploadPhoto();
        } catch (Exception ex) {
            Log.e("Reload All", ex.getMessage());
        }

    }


}
