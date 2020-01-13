package com.example.dingtu2.myapplication.utils;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.example.dingtu2.myapplication.EventActivityFragment;
import com.example.dingtu2.myapplication.RoundActivity;

import org.json.JSONObject;

import java.io.File;


/**
 * Created by Dingtu2 on 2017/6/22.
 */

public class PhotoCamera extends Activity implements SensorEventListener {

    private final JSONObject jsonExit = new JSONObject();
    private String fromActivity = "";
    private SensorManager sManager;
    private Sensor mSensorOrientation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String PhotoPath = this.getIntent().getStringExtra("PhotoPath");
        String TempPhoto = this.getIntent().getStringExtra("TempPhoto");
        fromActivity = this.getIntent().getStringExtra("from");


        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorOrientation = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sManager.registerListener(this, mSensorOrientation, SensorManager.SENSOR_DELAY_UI);

        Intent tt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File out = new File(PhotoPath, TempPhoto);
        Log.i("目录", out.getAbsolutePath());
        Uri fileName = FileProvider.getUriForFile(this, "com.example.dingtu2.myapplication.fileprovider", out);
        tt.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        tt.putExtra(MediaStore.EXTRA_OUTPUT, fileName);
        startActivityForResult(tt, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("tag", "相机完成拍照，在PhotoCamera正在回调。。。");

        sManager.unregisterListener(this);

        //TODO：回调调用类的方法
        if (fromActivity.equals("eventActivity")) {
            if (EventActivityFragment.photoCallBack != null) {
                EventActivityFragment.photoCallBack.OnClick(requestCode + "", jsonExit);
                Log.i("拍照", "photoCallBack回掉");
            } else {
                Log.e("拍照", "photoCallBack为空");
            }
        } else {
            if (RoundActivity.photoCallBack != null) {
                RoundActivity.photoCallBack.OnClick(requestCode + "", jsonExit);
            } else {
                Log.e("拍照", "photoCallBack为空");
            }
        }


        //此处可能会出现window leak问题 ，解决方法在androidMainfest.xml中加入android:configChanges="orientation|keyboardHidden|navigation"
        this.finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        try {
            jsonExit.put("Yaw", (int) (Math.round(event.values[0] * 100)) / 100);
            jsonExit.put("Pitch", (int) (Math.round(event.values[1] * 100)) / 100);
            jsonExit.put("Roll", (int) (Math.round(event.values[2] * 100)) / 100);

        } catch (Exception ex) {

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
