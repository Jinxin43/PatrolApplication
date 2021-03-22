package com.example.dingtu2.myapplication;

import android.content.Context;
import android.util.Log;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dingtu2 on 2017/6/7.
 */

public class SystemSetup {


    /**
     * @param C
     * @return HashMap:Result=OK 表示通过检查，否则为错误信息,Path=代表最终的系统路径
     */
    public static HashMap<String, String> CheckSystemFile(Context C) {
        HashMap<String, String> resultHM = new HashMap<String, String>();
        resultHM.put("Result", "OK");
        resultHM.put("Path", "");

        //得到系统主目录列表，SDPath=完整路径,AllSize=全部容量,FreeSize=可用容量
        List<HashMap<String, Object>> SysDirList = Tools.GetAllSDCardInfoList(C);

        //判断主目录
        String SysDir = "";
        for (HashMap<String, Object> syObj : SysDirList) {
            String SysDirTemp = syObj.get("SDPath") + "/" + PubVar.m_SysDictionaryName;
            Log.v("SysDirTemp", SysDirTemp);
            if (Tools.ExistFile(SysDirTemp)) {
                SysDir = SysDirTemp;
                break;
            }
        }

        //检查各级目整的完整性
        List<String> dirList = new ArrayList<String>();
        List<String> fileList = new ArrayList<String>();
        dirList.add(SysDir + "/Map");  //底图存储目录
        dirList.add(SysDir + "/Data");  //采集数据存储目录
        dirList.add(SysDir + "/SysFile");  //系统文件目录
        fileList.add(SysDir + "/SysFile/Config.dbx" + "," + R.raw.config);        //系统配置文件
        fileList.add(SysDir + "/SysFile/Project.dbx" + "," + R.raw.project);    //工程配置文件
        fileList.add(SysDir + "/SysFile/Template.dbx" + "," + R.raw.tadata);
        fileList.add(SysDir + "/SysFile/Tadata.dbx" + "," + R.raw.tadata);//采集数据模板文件
        fileList.add(SysDir + "/SysFile/UserConfig.dbx" + "," + R.raw.userconfig);  //用户自定配置模板文件
        if (SysDir.equals("")) {
            resultHM.put("Result", "系统主目录缺失");
            return resultHM;
        }
        for (String dir : dirList) {
            if (!Tools.ExistFile(dir)) {
                if (!new File(dir).mkdirs()) {
                    resultHM.put("Result", "无法创建目录【" + dir + "】，程序无法正常运行！");
                    return resultHM;
                }
            }
        }

        //判断系统配置文件
        for (String sysFile : fileList) {
            String fileName = sysFile.split(",")[0];
            int rowID = Integer.parseInt(sysFile.split(",")[1]);
            if (!Tools.ExistFile(fileName)) {
                if (!Tools.CopyToFileFromRawID(C, fileName, rowID)) {
                    resultHM.put("Result", "无法创建配置文件【" + sysFile + "】，程序无法正常运行！");
                    return resultHM;
                }
            }
        }

//        PubVar.m_DoEvent.m_ProjectDB.CreateProject("天保巡护");
//        Log.d("CreateProject", "project name :天保巡护");

        resultHM.put("Result", "OK");
        resultHM.put("Path", SysDir);
        return resultHM;
    }
}
