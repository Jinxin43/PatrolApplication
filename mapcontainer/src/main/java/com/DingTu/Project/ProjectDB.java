package com.DingTu.Project;

import android.util.Log;
import android.widget.Toast;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.DingTu.Dataset.ASQLiteDatabase;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Dingtu2 on 2017/6/5.
 */

public class ProjectDB {

    public ProjectDB() {

    }

    //工程管理器
    private ProjectExplorer m_ProjectExplorer = null;

    public ProjectExplorer GetProjectExplorer() {
        return this.m_ProjectExplorer;
    }

    //图层管理器
    private LayerExplorer m_LayerExplorer = null;

    public LayerExplorer GetLayerExplorer() {
        return this.m_LayerExplorer;
    }

    //底图管理器
    private BKLayerExplorer m_BKLayerExplorer = null;

    public BKLayerExplorer GetBKLayerExplorer() {
        return this.m_BKLayerExplorer;
    }

    //图层渲染器
    private LayerRenderExplorer m_LayerRenderExplorer = null;

    public LayerRenderExplorer GetLayerRenderExplorer() {
        if (this.m_LayerRenderExplorer == null)
            this.m_LayerRenderExplorer = new LayerRenderExplorer();
        return this.m_LayerRenderExplorer;
    }

    /**
     * 创建工程，以工程名称做为目录名，目录下有project.dbx工程配置文件
     *
     * @param prjName
     */
    public boolean CreateProject(String prjName) {
        String PrjPath = PubVar.m_SysAbsolutePath + "/Data/" + prjName;

        //检查工程目录，如果没有则创建工程目录
        if (!Tools.ExistFile(PrjPath)) {
            if ((new File(PrjPath)).mkdirs()) {
                //创建工程相关子目录及配置文件
                Tools.CopyFile(PubVar.m_SysAbsolutePath + "/sysfile/Template.dbx", PrjPath + "/TAData.dbx");
                Tools.CopyFile(PubVar.m_SysAbsolutePath + "/sysfile/Project.dbx", PrjPath + "/Project.dbx");
                this.OpenDatabase(PrjPath + "/Project.dbx");
                return true;
            } else {
                Log.d("Create Project Folder", "create project folder failed:" + PrjPath);
            }
        }
        return false;
    }

    //标识是否已经打开工程
    private boolean m_AlwaysOpenProject = false;

    public boolean AlwaysOpenProject() {
        return this.m_AlwaysOpenProject;
    }

    public boolean OpenProject(String prjName) {
        return this.OpenProject(prjName, true);
    }

    /**
     * 打开工程
     *
     * @param prjName
     * @return
     * @saveInfo 是否保存上次打开信息
     */
    public boolean OpenProject(String prjName, boolean saveInfo) {

        String PrjFileFullName = PubVar.m_SysAbsolutePath + "/Data/" + prjName + "/Project.dbx";
        Log.d("OpenProject", PrjFileFullName);

        //打开工程 配置库
        if (!Tools.ExistFile(PrjFileFullName)) {
            Toast.makeText(PubVar.m_DoEvent.m_Context, "PrjFileFullName", Toast.LENGTH_SHORT).show();
            return false;
        }
        this.OpenDatabase(PrjFileFullName);

        //工程管理器
        if (this.m_ProjectExplorer == null) this.m_ProjectExplorer = new ProjectExplorer();
        this.m_ProjectExplorer.SetBindProjectDB(this);
        this.m_ProjectExplorer.SetProjectName(prjName);
        this.m_ProjectExplorer.LoadProjectInfo();


        //打开本工程所对应的图层列表
        if (this.m_LayerExplorer == null) this.m_LayerExplorer = new LayerExplorer();
        this.m_LayerExplorer.SetBindProjectDB(this);
        this.m_LayerExplorer.LoadLayer();

        //打开本工程所对应的底图图层管理器
        if (this.m_BKLayerExplorer == null) this.m_BKLayerExplorer = new BKLayerExplorer();
        this.m_BKLayerExplorer.SetBindProjectDB(this);
        this.m_BKLayerExplorer.LoadBKLayer();
        this.m_AlwaysOpenProject = true;

        //将本次打开的工程信息存入用户配置库，方便下次以快捷方式打开
        if (saveInfo) {
            HashMap<String, String> beforeOpenProjectInfo = new HashMap<String, String>();
            beforeOpenProjectInfo.put("F2", prjName);
            beforeOpenProjectInfo.put("F3", Tools.GetSystemDate());
            PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().SaveUserPara("Tag_BeforeOpenProject", beforeOpenProjectInfo);
        }
        return true;
    }

    /**
     * 关闭工程
     *
     * @return
     */
    public boolean CloseProject() {
        if (this.m_SQLiteDatabase != null) this.m_SQLiteDatabase.Close();
        return true;
    }


    public boolean saveProjectInfo(String coorSys, String centerMeridian) {
        return this.m_ProjectExplorer.saveProjectCoorSystem(coorSys, centerMeridian);
    }

    //数据库操作类
    private ASQLiteDatabase m_SQLiteDatabase = null;

    //打开数据库
    private void OpenDatabase(String dbFileName) {
        if (Tools.ExistFile(dbFileName)) {
            if (this.m_SQLiteDatabase != null) this.m_SQLiteDatabase.Close();
            this.m_SQLiteDatabase = new ASQLiteDatabase();
            this.m_SQLiteDatabase.setDatabaseName(dbFileName);
        }
    }

    /**
     * 得到指定的数据库操作类
     *
     * @return
     */
    public ASQLiteDatabase GetSQLiteDatabase() {
        return this.m_SQLiteDatabase;
    }
}
