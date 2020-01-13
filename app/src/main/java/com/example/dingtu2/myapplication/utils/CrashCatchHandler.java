package com.example.dingtu2.myapplication.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.DingTu.Base.PubVar;
import com.example.dingtu2.myapplication.http.RetrofitHttp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dingtu2 on 2018/9/11.
 */

public class CrashCatchHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    private static final CrashCatchHandler INSTANCE = new CrashCatchHandler();// 单例模式
    private Context context;
    private UncaughtExceptionHandler defaultHandler;// 系统默认的UncaughtException处理类
    private Map<String, String> infosMap = new HashMap<String, String>(); // 用来存储设备信息和异常信息

    private CrashCatchHandler() {

    }

    public static CrashCatchHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        this.context = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置当前CrashHandler为程序的默认处理器
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {


        try {
            Log.e("UnHandledException", ex.getMessage());
            ex.printStackTrace();

            if (!handleException(ex) && defaultHandler != null) {
                // 如果用户没有处理则让系统默认的异常处理器来处理
                defaultHandler.uncaughtException(thread, ex);
            } else {
                Thread.sleep(5000);
                // 杀死进程
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        } catch (Exception e) {
            Log.e(TAG, "exception : ", e);
            e.printStackTrace();
        }

    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 使用Toast显示异常信息
        new Thread() {
            public void run() {
                try {
                    Looper.prepare();
                    Toast.makeText(context, "程序出现未捕获的异常，即将退出！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } catch (Exception e) {
                    Log.e("LooperException", e.getMessage());
                    e.printStackTrace();

                }

            }
        }.start();

        try {
            collectDeviceInfo(context);// 收集设备参数信息
            String fileName = saveCrashInfoToFile(ex);// 保存日志文件
            if (fileName != null) {
                uploadExceptionToServer(fileName);
            }
            return true;
        } catch (Exception exx) {
            Log.e("collectDeviceInfo", exx.getMessage());
            exx.printStackTrace();
            return false;
        }
    }

    public void collectDeviceInfo(Context context) {
        // 使用包管理器获取信息
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                // TODO 在这里得到包的信息
                String versionName = pi.versionName == null ? "" : pi.versionName;// 版本名;若versionName==null，则="null"；否则=versionName
                String versionCode = pi.versionCode + "";// 版本号
                infosMap.put("versionName", versionName);
                infosMap.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an NameNotFoundException occured when collect package info");
            e.printStackTrace();
        }

        // 使用反射获取获取系统的硬件信息
        Field[] fields = Build.class.getDeclaredFields();// 获得某个类的所有申明的字段，即包括public、private和proteced，
        for (Field field : fields) {
            field.setAccessible(true);// 暴力反射 ,获取私有的信息;类中的成员变量为private,故必须进行此操作
            try {
                infosMap.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "an IllegalArgumentException occured when collect reflect field info", e);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                Log.e(TAG, "an IllegalAccessException occured when collect reflect field info", e);
                e.printStackTrace();
            }
        }
    }

    private String saveCrashInfoToFile(Throwable ex) {
        // 字符串流

        StringBuffer stringBuffer = new StringBuffer();

        // 获得设备信息
        for (Map.Entry<String, String> entry : infosMap.entrySet()) {// 遍历map中的值
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuffer.append(key + "=" + value + "\n");
        }

        // 获得错误信息
        Writer writer = new StringWriter();// 这个writer下面还会用到，所以需要它的实例
        PrintWriter printWriter = new PrintWriter(writer);// 输出错误栈信息需要用到PrintWriter
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {// 循环，把所有的cause都输出到printWriter中
            cause.printStackTrace(printWriter);
            cause = ex.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        stringBuffer.append(result);

        // 写入文件
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String crashFileName = PubVar.m_SysAbsolutePath + "/Log/crash_" + simpleDateFormat.format(new Date()) + ".log";

        //因为是sd卡根目录，所以就需要创建父文件夹了
        File file = new File(PubVar.m_SysAbsolutePath + "/Log");
        if (!file.exists()) {
            file.mkdirs();// 如果不存在，则创建所有的父文件夹
        }

        try {
            FileOutputStream fos = new FileOutputStream(crashFileName);
            fos.write(stringBuffer.toString().getBytes());
            fos.close();

            //TODO 在这里可以将文件名写入sharedPreferences中，方便下一次打开程序时对错误日志进行操作
            /*SharedPreferences.Editor editor = mContext
                    .getSharedPreferences("waka").edit();
            editor.putString("lastCrashFileName", crashFileName);
            editor.commit();*/


            return crashFileName;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "an FileNotFoundException occured when write crashfile to sdcard", e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "an IOException occured when write crashfile to sdcard", e);
            e.printStackTrace();
        }
        return null;
    }

    private void uploadExceptionToServer(String fileName) {

        File logFile = new File(fileName);
        RequestBody eventId = RequestBody.create(MediaType.parse("text/plain"), fileName);
        Map<String, RequestBody> map = new HashMap<>();
        map.put("fileName", eventId);
        RequestBody fileBody = RequestBody.create(MediaType.parse("text/plain"), logFile);
        map.put("uploadedFiles\"; filename=\"" + logFile.getName(), fileBody);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Call<ResponseBody> newPhoto = RetrofitHttp.getRetrofit(builder.build()).UploadErrorLogFile("UploadErrorLogFile", map);
        newPhoto.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

//                if(response.body() == null)
//                {
//                    Log.e("uploadExceptionSuccess","response.body() is null");
//                    return;
//                }

                try {
                    Log.e("uploadExceptionSuccess", response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                t.printStackTrace();
                Log.e("uploadExceptionFail", t.getMessage());
            }
        });
    }
}
