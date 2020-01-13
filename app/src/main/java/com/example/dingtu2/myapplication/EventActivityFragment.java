package com.example.dingtu2.myapplication;

import android.app.Dialog;
import android.arch.lifecycle.LifecycleFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.media.ExifInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Enum.lkGpsFixMode;
import com.DingTu.GPS.LocationEx;
import com.example.dingtu2.myapplication.db.xEntity.PatrolEventEntity;
import com.example.dingtu2.myapplication.db.xEntity.PhotoEntity;
import com.example.dingtu2.myapplication.http.Httpmodel.HttpEventModel;
import com.example.dingtu2.myapplication.http.RetrofitHttp;
import com.example.dingtu2.myapplication.manager.PatrolManager;
import com.example.dingtu2.myapplication.manager.PhotoManager;
import com.example.dingtu2.myapplication.utils.PhotoCamera;

import net.anumbrella.customedittext.FloatLabelView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventActivityFragment extends LifecycleFragment {
    
    public static ICallback photoCallBack = null;
    final PatrolEventEntity roundEventEntity = new PatrolEventEntity();
    @BindView(R.id.gvList)
    GridView gridView;
    @BindView(R.id.etEventPOI)
    EditText eventPOI;
    @BindView(R.id.etEventTime)
    EditText eventTime;
    @BindView(R.id.event_name)
    FloatLabelView eventName;
    @BindView(R.id.etEventDescription)
    EditText eventDescription;
    @BindView(R.id.spRoundEventType)
    Spinner spRoundEventType;
    FragmentActivity mOwner;
    private View mView;
    private Date eventTimeDate;
    private Unbinder unbinder;
    private ArrayList<String> mPhotoNameList = new ArrayList<String>();
    private String photoPath = PubVar.m_SysAbsolutePath + "/Photo";
    private String smallPhotoPath = photoPath + "/samllPhoto";
    private String tempPhotoName = photoPath + "/TempPhoto.jpg";
    private LocationEx mLocation = null;
    private Dialog mSavingingDlg; // 显示正在保存的Dialog
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean mIsSaving = false;
    public EventActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_event, container, false);
        unbinder = ButterKnife.bind(this, mView);
        mOwner = this.getActivity();
        photoCallBack = new ICallback() {
            @Override
            public void OnClick(String Str, Object ExtraStr) {
                photoCallbackResult(Str, ExtraStr);
            }
        };

        bindingToView();
        initSavingDlg();
        eventTimeDate = new Date();
        eventTime.setText(simpleDateFormat.format(eventTimeDate));
        eventTime.setEnabled(false);

        refreshLocation();
        return mView;
    }

    private void refreshLocation() {
        try {
            if (PubVar.m_GPSLocate.m_LocationEx != null && PubVar.m_GPSLocate.m_LocationEx.GetGpsFixMode() == lkGpsFixMode.en3DFix&&
                    PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude()>0.0001&&PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude()>0.0001  ) {
                mLocation = PubVar.m_GPSLocate.m_LocationEx;//TODO:还是一个引用
                Toast.makeText(mOwner, "位置已刷新!", Toast.LENGTH_SHORT).show();
                ((TextView) mView.findViewById(R.id.tvLon)).setText(Tools.ConvertToDigi(mLocation.GetGpsLongitude() + "", 6));
                ((TextView) mView.findViewById(R.id.tvLat)).setText(Tools.ConvertToDigi(mLocation.GetGpsLatitude() + "", 6));
                ((TextView) mView.findViewById(R.id.tvHigh)).setText(mLocation.GetGpsAltitude() + "");
            } else {
                Toast.makeText(mOwner, "GPS信号弱,请到开阔地带刷新位置!", Toast.LENGTH_LONG).show();
                //TODO：save to log
            }

        } catch (Exception ex) {
            Toast.makeText(mOwner, ex.getMessage(), Toast.LENGTH_SHORT).show();
            //TODO：save to log
        }
    }

    private void bindingToView() {
        String[] arrRoundEventType = "资源安全巡查、保护设施检查、森林防火、野生动物活动情况、社区宣传、其他".split("、");
        ArrayAdapter<String> roundEventTypeAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
                android.R.layout.simple_spinner_item,
                arrRoundEventType);
        roundEventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoundEventType.setAdapter(roundEventTypeAdapter);
    }

    @OnClick({R.id.btn_photo, R.id.text_back, R.id.text_submit, R.id.btn_deletephoto, R.id.text_Refresh})
    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.btn_photo:
                takePhoto();
                break;
            case R.id.text_back:
                this.getActivity().finish();
                break;
            case R.id.text_submit:
                saveEvent();
                break;
            case R.id.btn_deletephoto:
                deletePhoto();
                break;
            case R.id.text_Refresh:
                refreshLocation();
                break;
        }
    }

    private void deletePhoto() {
        GridView gridView = (GridView) mView.findViewById(R.id.gvList);
        ListAdapter adapter = gridView.getAdapter();

        List<String> delPhotos = new ArrayList<String>();

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

        showPhotos();

    }


    private int getEventType() {
        String eventType = spRoundEventType.getSelectedItem() + "";
        if (eventType.equals("资源安全巡查")) {
            return 21;
        } else if (eventType.equals("保护设施检查")) {
            return 22;
        } else if (eventType.equals("森林防火")) {
            return 23;
        } else if (eventType.equals("野生动物活动情况")) {
            return 24;
        } else if (eventType.equals("社区宣传")) {
            return 25;
        } else {
            return 26;
        }
    }

    private void saveEvent() {
        boolean okay = true;
        if(mIsSaving)
        {
            Toast.makeText(mOwner, "点击过于频繁，请稍后点击", Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            mIsSaving = true;
        }
        String strEventName = eventName.getEditText().getText().toString().trim();

        String strEventPOI = eventPOI.getText().toString().trim();
        String strEventDescrption = eventDescription.getText().toString().trim();

        if (mLocation == null) {
            refreshLocation();
        }
        if (mLocation == null) {
            mIsSaving = false;
            Toast.makeText(mOwner, "GPS信号弱，无法取到位置", Toast.LENGTH_LONG).show();
//            okay = false;
            return;
        }

//        if (mPhotoNameList.size() == 0) {
//            Toast.makeText(mOwner, "巡护记录要拍摄照片", Toast.LENGTH_SHORT).show();
//            okay = false;
//        }

        if (okay) {

            try {
                roundEventEntity.setId(simpleDateFormat.format(eventTimeDate) + "_1");//为什么要加后缀
                roundEventEntity.setEventName(strEventName);
                roundEventEntity.setEventPOI(strEventPOI);
                roundEventEntity.setEventDescription(strEventDescrption);
                roundEventEntity.setEventType(getEventType());
                roundEventEntity.setRoundId(AppSetting.curRound.getId());

                roundEventEntity.setEventTime(eventTimeDate);
                if (mLocation != null) {
                    roundEventEntity.setEventLat(mLocation.GetGpsLatitude());
                    roundEventEntity.setEventLon(mLocation.GetGpsLongitude());
                    roundEventEntity.setAltitude(mLocation.GetGpsAltitude());
                }

                String strPhotoList = "";
                for (String photo : mPhotoNameList) {
                    if (strPhotoList.length() > 0) {
                        strPhotoList += ";" + photo;
                    } else {
                        strPhotoList += photo;
                    }
                }
                roundEventEntity.setEventPhotos(strPhotoList);
                PatrolManager.getInstance().savePatrolEvent(roundEventEntity);
                if (mPhotoNameList.size() > 0) {
                    for (String photoName : mPhotoNameList) {
                        PhotoEntity photoEntity = new PhotoEntity();
                        photoEntity.setBelongTo(roundEventEntity.getId());
                        photoEntity.setPhotoName(photoName);
                        photoEntity.setPhotoType("发现");
                        photoEntity.setSaveTime(new Date());
                        photoEntity.setUserID(AppSetting.curUserKey);
                        try {
                            PhotoManager.getInstance().savePhoto(photoEntity);
                        } catch (Exception ex) {
                            Toast.makeText(mOwner, "保存照片信息：" + ex.getMessage(), Toast.LENGTH_LONG).show();
                            //TODO:save error message to logfile
                        }

                    }
                }
                if (AppSetting.curRound.getServerId() == null || AppSetting.curRound.getServerId().isEmpty()) {
                    //没有ServerId不上传
                    mIsSaving = false;
                    getActivity().finish();
                    return;
                } else {
                    roundEventEntity.setServerId(AppSetting.curRound.getServerId());
                }
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HttpEventModel eventModel = new HttpEventModel();
                        eventModel.setUserId(AppSetting.curUserKey);
                        eventModel.setDescription(roundEventEntity.getEventDescription());
                        if (AppSetting.curRound == null) {
                            return;
                        } else {
                            eventModel.setRoundId(AppSetting.curRound.getServerId());
                        }
                        eventModel.setEventTime(roundEventEntity.getEventTime().getTime());
                        eventModel.setLatitude(roundEventEntity.getEventLat() + "");
                        eventModel.setLongitude(roundEventEntity.getEventLon() + "");
                        eventModel.setEventPOI(roundEventEntity.getEventPOI());
                        eventModel.setHeight(roundEventEntity.getAltitude() + "");
                        eventModel.setType(roundEventEntity.getEventType() + "");
                        eventModel.setGpsTime(roundEventEntity.getEventTime().getTime());

                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        prompt(RetrofitHttp.getRetrofit(builder.build()).CreateEvent("CreateEvent", eventModel));
                    }
                });

                mIsSaving = false;
                getActivity().finish();
            }
            catch (Exception ex)
            {
                mIsSaving = false;
                Toast.makeText(mOwner,"保存发现失败："+ex.getMessage(),Toast.LENGTH_LONG).show();
                //TODO:save file
            }
        }
    }

    private void takePhoto() {

        if (!Tools.ExistFile(photoPath)) {
            boolean createPath = (new File(photoPath)).mkdirs();
            Log.i("createPath", createPath + "");
        }

        if (!Tools.ExistFile(smallPhotoPath)) {
            (new File(smallPhotoPath)).mkdirs();
        }

        Intent photoCamera = new Intent(mOwner, PhotoCamera.class);
        Log.i("拍照", "打开相机");
        photoCamera.putExtra("PhotoPath", photoPath);
        photoCamera.putExtra("TempPhoto", "TempPhoto.jpg");
        photoCamera.putExtra("from", "eventActivity");
        this.startActivity(photoCamera);
    }


    //拍照完成后回调
    public void photoCallbackResult(String resultCode, final Object ExtraStr) {
        if (!resultCode.equals("1")) {
            Log.i("事件拍照回调", "回调校验码：" + resultCode);
            return;
        }

        if (!Tools.ExistFile(tempPhotoName)) {
            Log.i("事件拍照回调", "没有写入文件：" + resultCode);
            return;
        }

        try {
            if (mSavingingDlg != null) {
                mSavingingDlg.show();
            }
        } catch (Exception ex) {
            Toast.makeText(mOwner,"打开SavingDlg"+ex.getMessage(),Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    if (PubVar.m_GPSLocate.m_LocationEx != null && PubVar.m_GPSLocate.m_LocationEx.GetGpsFixMode() == lkGpsFixMode.en3DFix&&
                            PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude()>0.0001&&PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude()>0.0001  ) {
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

                    canvasTemp.save(Canvas.ALL_SAVE_FLAG);
                    canvasTemp.restore();

                    FileOutputStream fos = new FileOutputStream(f1);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    fos = null;
                    if(!bitmap.isRecycled()){
                        bitmap.recycle();
                    }
                    FileInputStream iSteam2 = new FileInputStream(tempPhotoName);
                    Bitmap bitmap2 = BitmapFactory.decodeStream(iSteam2);
                    File smallF = new File(AppSetting.smallPhotoPath + "/" + PhotoFileName);
                    FileOutputStream f = new FileOutputStream(smallF);
                    Bitmap b = Bitmap.createScaledBitmap(bitmap2, bitmap2.getWidth() / 8, bitmap2.getHeight() / 8, false);
                    b.compress(Bitmap.CompressFormat.JPEG, 100, f);

                    isFailed = false;
                    f.flush();
                    f.close();
                    f= null;

                    if(!b.isRecycled()){
                        b.recycle();
                    }

                f1.renameTo(new File(photoPath + "/" + PhotoFileName));
                mPhotoNameList.add(PhotoFileName);
                gridView.post(new Runnable() {
                    @Override
                    public void run() {
                        showPhotos();
                    }
                });


                String fileName = AppSetting.photoPath + "/" + PhotoFileName;
                JSONObject exif = (JSONObject) ExtraStr;
                //存储exif信息

                try {
                    String[] Coor = PubVar.m_GPSLocate.getJWGPSCoordinate().split(",");

                    ExifInterface exifInfo = new ExifInterface(fileName);
                    exifInfo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
                    exifInfo.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
                    exifInfo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, Tools.ConvertToSexagesimal(Coor[0]));
                    exifInfo.setAttribute(ExifInterface.TAG_GPS_LATITUDE, Tools.ConvertToSexagesimal(Coor[1]));
                    String[] GPSDateTime = PubVar.m_GPSLocate.getGPSDateForPhotoFormat();
                    exifInfo.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, GPSDateTime[1]);
                    exifInfo.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, GPSDateTime[0]);

                    if (PubVar.m_GPSLocate.m_LocationEx != null) {
                        exif.put("lat:", PubVar.m_GPSLocate.m_LocationEx.GetGpsLatitude());
                        exif.put("lon:", PubVar.m_GPSLocate.m_LocationEx.GetGpsLongitude());
                        exif.put("gpsTime", PubVar.m_GPSLocate.m_LocationEx.GetGpsDate() + " " + PubVar.m_GPSLocate.m_LocationEx.GetGpsTime());
                    }

                    Log.d("exif save", exif.toString());
                    exifInfo.setAttribute(ExifInterface.TAG_USER_COMMENT, exif.toString());
                    exifInfo.saveAttributes();
                } catch (Exception io) {
                    //TODO:save to log
                    io.printStackTrace();
                }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (mSavingingDlg != null && mSavingingDlg.isShowing()) {
                        mSavingingDlg.dismiss();
                    }
                } catch (Exception ex) {
                    Toast.makeText(mOwner,"关闭SavingDlg"+ex.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    private void showPhotos() {
        String[] from = {"image", "text", "check"};
        int[] to = {R.id.iv_image, R.id.tv_info, R.id.cb_select};
        ArrayList<HashMap<String, Object>> data_list = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < mPhotoNameList.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            //map.put("image", mSmallPhotoPath+"/"+mPhotoNameList.get(i));
            map.put("image", smallPhotoPath + "/" + mPhotoNameList.get(i));
            map.put("text", mPhotoNameList.get(i));
            map.put("check", false);
            data_list.add(map);
        }
        //sim_adapter = new ImageListAdapter(mOwnActivity, data_list, R.layout.photolistitem, from, to);
        SimpleAdapter sim_adapter = new SimpleAdapter(mOwner, data_list, R.layout.photolistitem, from, to);

        Log.i("gridView", "gridView item:" + data_list.size());
        gridView.setAdapter(sim_adapter);
        gridView.setOnItemClickListener(new photoItemClickListener());
        gridView.invalidate();
    }

    private void prompt(Call<ResponseBody> newRound) {

        newRound.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> reg, Response<ResponseBody> response) {

                try {
                    JSONObject result = new JSONObject(response.body().string());
                    if (result.get("success").equals(Boolean.TRUE)) {
                        Log.e("finish Event upload", " result: " + result);
                        roundEventEntity.setUploadStatus(1);//已上传
                        roundEventEntity.setServerId(result.get("data").toString());
                        PatrolManager.getInstance().savePatrolEvent(roundEventEntity);
                        Toast.makeText(mOwner, "巡护事件上传成功，继续上传照片", Toast.LENGTH_SHORT).show();
                        if(mPhotoNameList.size()>0)
                        {
                            uploadPhotoOneByOne(0);
                        }
                    } else {
                        Toast.makeText(mOwner, "巡护事件失败：" + result.get("msg"), Toast.LENGTH_SHORT).show();
                        Log.e("Event upload failed", " result: " + result);
                    }
                } catch (Exception io) {
                    Toast.makeText(mOwner, io.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Event upload failed", " exception: " + io.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> reg, Throwable t) {
                Log.e("finish Round upload", " exception: " + t.getMessage());
                Toast.makeText(mOwner, "巡护完成状态未上传到服务器，待网络恢复后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadPhotoOneByOne(final int photoIndex)
    {
        final boolean hasNext = photoIndex < (mPhotoNameList.size() - 2);
        final String fileName = mPhotoNameList.get(photoIndex);
        try {
            File photo = new File(AppSetting.photoPath + "/" + fileName);
            RequestBody eventId = RequestBody.create(MediaType.parse("text/plain"), roundEventEntity.getServerId());
            Map<String, RequestBody> map = new HashMap<>();
            map.put("eventId", eventId);
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), photo);
            map.put("uploadedFiles\"; filename=\"" + photo.getName(), fileBody);

            String imageInfo = "";
            try {
                ExifInterface exifInfo = new ExifInterface(AppSetting.photoPath + "/" + fileName);
                imageInfo = exifInfo.getAttribute(ExifInterface.TAG_USER_COMMENT);
                Log.d("exif read", imageInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (imageInfo == null) {
                imageInfo = "";
            }
            RequestBody imageExif = RequestBody.create(MediaType.parse("text/plain"), imageInfo);
            map.put("imageInfo", imageExif);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            Call<ResponseBody> newPhoto = RetrofitHttp.getRetrofit(builder.build()).uploadEventPicture("UploadEventFile", map);
            newPhoto.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        if(hasNext)
                        {
                            uploadPhotoOneByOne(photoIndex+1);
                        }
                        if (response==null||response.body() == null||response.body().string()==null) {
                            Log.e("uploadPhotos faild", "response.body() is null");
                            return;
                        }

                        JSONObject result = new JSONObject(response.body().string());
                        if (result.get("success").equals(Boolean.TRUE)) {

                            try{
                                PhotoEntity photoEntity = PhotoManager.getInstance().getPhotoEntity(fileName);
                                photoEntity.setUploadStatus(1);
                                photoEntity.setUploadTime(new Date());
                                PhotoManager.getInstance().savePhoto(photoEntity);
                            }
                            catch (Exception ex){
                                Toast.makeText(AppSetting.applicaton.getApplicationContext(), "更新照片失败："+ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(mOwner, "上传照片成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mOwner, "上传照片失败1：" + result.get("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(mOwner, "照片上传失败2"+ex.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(mOwner, "上传照片失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(mOwner, "上传照片失败3：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            //TODO:log to file
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initSavingDlg() {

        mSavingingDlg = new Dialog(this.getActivity(), R.style.loginingDlg);
        mSavingingDlg.setContentView(R.layout.logindlg);

        Window window = mSavingingDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // 获取和mLoginingDlg关联的当前窗口的属性，从而设置它在屏幕中显示的位置

        // 获取屏幕的高宽
        DisplayMetrics dm = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
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
        ((TextView) mSavingingDlg.findViewById(R.id.tv_loading_show)).setText("正在处理照片");
        params.width = cxScreen;
        params.height = height;
        // width,height表示mLoginingDlg的实际大小
        mSavingingDlg.setCanceledOnTouchOutside(false);
//        mSavingingDlg.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog
    }

    //照片点击展示
    class photoItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened
                                View arg1,//The view within the AdapterView that was clicked
                                int arg2,//The position of the view in the adapter
                                long arg3//The row id of the item that was clicked
        ) {
            //在本例中arg2=arg3
            HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            //显示所选Item的ItemText

            Intent it = new Intent(Intent.ACTION_VIEW);
            String photoName = item.get("image") + "";
            photoName = photoName.replace("/samllPhoto", "");
            File file = new File(photoName);
            Uri fileName = FileProvider.getUriForFile(mOwner, "com.example.dingtu2.myapplication.fileprovider", file);
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            it.setDataAndType(fileName, "image/*");

            mOwner.startActivity(it);
        }
    }

}
