package com.example.dingtu2.myapplication.Login;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.DingTu.Base.ICallback;
import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;
import com.example.dingtu2.myapplication.AppSetting;
import com.example.dingtu2.myapplication.MainActivity;
import com.example.dingtu2.myapplication.R;
import com.example.dingtu2.myapplication.SelectSystemPath;
import com.example.dingtu2.myapplication.SystemSetup;
import com.example.dingtu2.myapplication.db.xEntity.UserEntity;
import com.example.dingtu2.myapplication.http.Httpmodel.HttpUserModel;
import com.example.dingtu2.myapplication.http.RetrofitHttp;
import com.example.dingtu2.myapplication.manager.UserManager;

import org.json.JSONObject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener, PopupWindow.OnDismissListener {


    public static final String Login = "login";//存储登录信息键值
    public static final String LoginKey = "loginID";//存储登录ID
    protected static final String TAG = "LoginActivity";
    private static final MutableLiveData ABSENT = new MutableLiveData();
    public static SharedPreferences preferences;
    private LoginActivity mContext;
    private LinearLayout mLoginLinearLayout; // 登录内容的容器
    private LinearLayout mUserIdLinearLayout; // 将下拉弹出窗口在此容器下方显示
    private Animation mTranslate; // 位移动画
    private Dialog mLoginingDlg; // 显示正在登录的Dialog
    private EditText mIdEditText; // 登录ID编辑框
    private EditText mPwdEditText; // 登录密码编辑框
    private ImageView mMoreUser; // 下拉图标
    private Button mLoginButton; // 登录按钮
    private ImageView mLoginMoreUserView; // 弹出下拉弹出窗的按钮
    private String mIdString;
    private String mPwdString;
    private List<UserEntity> mUsers; // 用户列表
    private ListView mUserIdListView; // 下拉弹出窗显示的ListView对象
    private MyAapter mAdapter; // ListView的监听器
    private PopupWindow mPop; // 下拉弹出窗

    public static void clearShareRefrenece() {
        if (preferences != null) {
            preferences.edit().clear().commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        preferences = getSharedPreferences(Login,
                MODE_PRIVATE);
        final String loginKey = preferences.getString(LoginKey,
                null);

        mContext = this;
        AppSetting.applicaton = this.getApplication();
//        initWorkDirectionary();
        PubVar.m_SysDictionaryName = this.getResources().getString(R.string.app_name);

        x.Ext.init(this.getApplication());
//        x.Ext.setDebug(BuildConfig.DEBUG);

        setContentView(R.layout.activity_login);
        initView();

        try {
//            SimpleDateFormat last = new SimpleDateFormat("yyyy-MM-dd");
//            Date expireDate = last.parse("2019-06-15");
//            Date now = new Date();
//            if(now.getTime()>expireDate.getTime())
//            {
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
//                alertDialog.setMessage("您的软件已经过期，请联系开发商更新软件！");
//                alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                });
//                alertDialog.show();
//                return;
//            }
        } catch (Exception ex) {
            Log.e("expireDate", ex.getMessage());
        }

        if (loginKey == null) {
            setListener();
            mLoginLinearLayout.startAnimation(mTranslate); // Y轴水平移动
            initUserList();
        } else {
            initLoginingDlg();
            showLoginingDlg();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    AppSetting.curUserKey = loginKey;
                    AppSetting.curUser = UserManager.getInstance().getLoginUser(AppSetting.curUserKey);
                    if (AppSetting.curUser != null) {
                        mIdString = AppSetting.curUser.getLoginName();
                        mPwdString = AppSetting.curUser.getPassword();
                        mIdEditText.setText(mIdString);
                        mPwdEditText.setText(mPwdString);

                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        HttpUserModel userModel = new HttpUserModel();
                        userModel.setAccount(AppSetting.curUser.getLoginName());
                        userModel.setPassword(AppSetting.curUser.getPassword());
                        userModel.setDevice("0");
                        rePrompt(RetrofitHttp.getRetrofit(builder.build()).login("login", userModel));
                    } else {
                        trunToMainActivity();
                    }

                }
            });

        }


    }

    private void initWorkDirectionary() {
        HashMap<String, String> resultHM = SystemSetup.CheckSystemFile(mContext);
        if (resultHM.get("Result").equals("系统主目录缺失")) {
            SelectSystemPath sst = new SelectSystemPath(mContext);
            sst.SetCallback(new ICallback() {
                @Override
                public void OnClick(String Str, Object ExtraStr) {
                    if (Str.equals("退出")) {
                        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                        String PackName = mContext.getPackageName();
                        am.killBackgroundProcesses(PackName);
                        System.exit(0);
                    }
                    if (Str.equals("工作目录")) {
                        SystemSetup.CheckSystemFile(mContext);
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
//                openProject();

            } else {

                Tools.ShowMessageBox(mContext, resultHM.get("Result"), new ICallback() {
                    @Override
                    public void OnClick(String Str, Object ExtraStr) {
                        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                        String PackName = mContext.getPackageName();
                        am.killBackgroundProcesses(PackName);   // API Level至少为8才能使用
                        System.exit(0);
                    }
                });
            }
        }
    }

    private void initUserList() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                mUsers = UserManager.getInstance().getAllLoginedUsers();
                if (mUsers != null && mUsers.size() > 0) {
                    mIdEditText.setText(mUsers.get(0).getLoginName());
                    mPwdEditText.setText(mUsers.get(0).getPassword());
                    mAdapter = new MyAapter(mUsers);
                    mUserIdListView.setAdapter(mAdapter);
                }
            }
        });

    }

    private void trunToMainActivity() {
        Intent inet = new Intent(this, MainActivity.class);
        this.startActivity(inet);
        finish();
    }

    private void setListener() {
        mIdEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mIdString = s.toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mPwdEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mPwdString = s.toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mLoginButton.setOnClickListener(this);
        mLoginMoreUserView.setOnClickListener(this);
    }

    private void initView() {
        mIdEditText = (EditText) findViewById(R.id.login_edtId);
        mPwdEditText = (EditText) findViewById(R.id.login_edtPwd);
        mMoreUser = (ImageView) findViewById(R.id.login_more_user);
        mLoginButton = (Button) findViewById(R.id.login_btnLogin);
        mLoginMoreUserView = (ImageView) findViewById(R.id.login_more_user);
        mLoginLinearLayout = (LinearLayout) findViewById(R.id.login_linearLayout);
        mUserIdLinearLayout = (LinearLayout) findViewById(R.id.userId_LinearLayout);
        mTranslate = AnimationUtils.loadAnimation(this, R.anim.logintranslate); // 初始化动画对象
        initLoginingDlg();

        LinearLayout parent = (LinearLayout) getLayoutInflater().inflate(
                R.layout.userinfo_listview, null);
        mUserIdListView = (ListView) parent.findViewById(android.R.id.list);
        parent.removeView(mUserIdListView); // 必须脱离父子关系,不然会报错
        mUserIdListView.setOnItemClickListener(this); // 设置点击事
    }

    public void initPop() {
        int width = mUserIdLinearLayout.getWidth() - 4;
        int height = LayoutParams.WRAP_CONTENT;
        mPop = new PopupWindow(mUserIdListView, width, height, true);
        mPop.setOnDismissListener(this);// 设置弹出窗口消失时监听器

        // 注意要加这句代码，点击弹出窗口其它区域才会让窗口消失
        mPop.setBackgroundDrawable(new ColorDrawable(0xffffffff));

    }

    /* 初始化正在登录对话框 */
    private void initLoginingDlg() {

        mLoginingDlg = new Dialog(this, R.style.loginingDlg);
        mLoginingDlg.setContentView(R.layout.logindlg);

        Window window = mLoginingDlg.getWindow();
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

        params.width = cxScreen;
        params.height = height;
        // width,height表示mLoginingDlg的实际大小

        mLoginingDlg.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog
    }

    /* 显示正在登录对话框 */
    private void showLoginingDlg() {
        if (mLoginingDlg != null)
            mLoginingDlg.show();
    }

    /* 关闭正在登录对话框 */
    private void closeLoginingDlg() {
        if (mLoginingDlg != null && mLoginingDlg.isShowing())
            mLoginingDlg.dismiss();
    }

    @Override
    public void onClick(View v) {

        boolean authPass = false;
        switch (v.getId()) {
            case R.id.login_btnLogin:
                // 启动登录
                showLoginingDlg();
                Log.i(TAG, mIdString + "  " + mPwdString);
                if (mIdString == null || mIdString.equals("")) { // 账号为空时
                    Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT)
                            .show();
                } else if (mPwdString == null || mPwdString.equals("")) {// 密码为空时
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT)
                            .show();
                } else {// 账号和密码都不为空时
                    boolean mIsSave = true;
                    try {
                        for (UserEntity user : mUsers) { // 判断本地文档是否有此ID用户
                            if (user.getLoginName().equals(mIdString)) {
                                mIsSave = false;
                                if (user.getPassword().equals(mPwdString)) {
                                    authPass = true;
                                    AppSetting.curUser = user;
                                    AppSetting.curUserKey = mIdString;
                                } else {
                                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_LONG)
                                            .show();
                                }
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (authPass) {
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        preferences = getSharedPreferences(
                                LoginActivity.Login, MODE_PRIVATE);
                        preferences.edit().putString(LoginActivity.LoginKey,
                                mIdString).apply();
                        closeLoginingDlg();
                        trunToMainActivity();
                    } else {
                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        HttpUserModel userModel = new HttpUserModel();
                        userModel.setAccount(mIdString);
                        userModel.setPassword(mPwdString);
                        userModel.setDevice("0");
                        prompt(RetrofitHttp.getRetrofit(builder.build()).login("login", userModel));

                    }
                }
                break;
            case R.id.login_more_user: // 当点击下拉栏
                if (mPop == null) {
                    initPop();
                }
                if (!mPop.isShowing() && mUsers != null && mUsers.size() > 0) {
                    // Log.i(TAG, "切换为角向上图标");
                    mMoreUser.setImageResource(R.drawable.login_more_down); // 切换图标
                    mPop.showAsDropDown(mUserIdLinearLayout, 2, 1); // 显示弹出窗口
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        mIdEditText.setText(mUsers.get(position).getLoginName());
        mPwdEditText.setText(mUsers.get(position).getPassword());
        mPop.dismiss();
    }

    /* PopupWindow对象dismiss时的事件 */
    @Override
    public void onDismiss() {
        // Log.i(TAG, "切换为角向下图标");
        mMoreUser.setImageResource(R.drawable.login_more_up);
    }

    /* 退出此Activity时保存users */
    @Override
    public void onPause() {
        super.onPause();
    }

    /* 通过验证登录获取用户信息*/
    private void rePrompt(Call<ResponseBody> register) {
        register.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() == null) {
                    Toast.makeText(mContext, "本地验证登录", Toast.LENGTH_SHORT).show();
//                    closeLoginingDlg();
                    trunToMainActivity();
                    return;
                }
                try {
                    JSONObject result = new JSONObject(response.body().string());
                    if (result.get("success").equals(Boolean.TRUE)) {
                        JSONObject data = new JSONObject(result.get("data").toString());
                        UserEntity user = new UserEntity(mIdString, mPwdString);

                        user.setUserName(data.get("Username") + "");
                        user.setLastLoginTime(new Date());
                        user.setMobilePhone(data.get("Phonenumber") + "");
                        if (data.get("Postdiscription") != null) {
                            user.setPostDiscription(data.get("Postdiscription") + "");
                        }

                        if (data.get("Groupid") != null) {
                            user.setDepartmentId(data.get("Groupid") + "");
                        }

                        if (!UserManager.getInstance().SaveLoginUser(user)) {
                            Log.e("save logined user", "can't save logined user");
                        }

                        AppSetting.curUserKey = mIdString;
                        AppSetting.curUser = user;
                        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                    }
//                    Toast.makeText(mContext, "离线成功", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {

                }

//                closeLoginingDlg();
                trunToMainActivity();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mContext, "离线成功", Toast.LENGTH_SHORT).show();
//                closeLoginingDlg();
                trunToMainActivity();
            }
        });
    }

    private void prompt(Call<ResponseBody> register) {
        register.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> reg, Response<ResponseBody> response) {
                try {
                    if (response.body() == null) {
                        return;
                    }
                    JSONObject result = new JSONObject(response.body().string());
                    if (result.get("success").equals(Boolean.TRUE)) {
                        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                        JSONObject data = new JSONObject(result.get("data").toString());
                        UserEntity user = new UserEntity(mIdString, mPwdString);

                        user.setUserName(data.get("Username") + "");
                        user.setLastLoginTime(new Date());
                        user.setMobilePhone(data.get("Phonenumber") + "");
                        if (data.get("Postdiscription") != null) {
                            user.setPostDiscription(data.get("Postdiscription") + "");
                        }

                        if (data.get("Groupid") != null) {
                            user.setDepartmentId(data.get("Groupid") + "");
                        }

                        if (!UserManager.getInstance().SaveLoginUser(user)) {
                            Log.e("save logined user", "can't save logined user");
                        }
//                        saveLoginUser(user);
                        AppSetting.curUserKey = mIdString;
                        AppSetting.curUser = user;
                        if (mUsers == null) {
                            mUsers = new ArrayList<UserEntity>();
                        }
                        mUsers.add(user);
                        preferences = getSharedPreferences(
                                LoginActivity.Login, MODE_PRIVATE);
                        preferences.edit().putString(LoginActivity.LoginKey,
                                mIdString).apply();

                        closeLoginingDlg();
                        trunToMainActivity();
                    } else {
                        closeLoginingDlg();// 关闭对话框
                        Toast.makeText(mContext, result.get("msg") + "", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception io) {
                    Toast.makeText(mContext, io.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> reg, Throwable t) {

                Log.d("internet failed", t.getMessage());
                closeLoginingDlg();// 关闭对话框
                Toast.makeText(mContext, "网络不给力", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /* ListView的适配器 */
    class MyAapter extends ArrayAdapter<UserEntity> {

        public MyAapter(List<UserEntity> users) {
            super(LoginActivity.this, 0, users);
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.loginlistviewitem, null);
            }

            TextView userIdText = (TextView) convertView
                    .findViewById(R.id.listview_username);
            userIdText.setText(getItem(position).getLoginName());

            ImageView deleteUser = (ImageView) convertView
                    .findViewById(R.id.login_delete_user);
            deleteUser.setOnClickListener(new OnClickListener() {
                // 点击删除deleteUser时,在mUsers中删除选中的元素
                @Override
                public void onClick(View v) {

                    if (getItem(position).getLoginName().equals(mIdString)) {
                        // 如果要删除的用户Id和Id编辑框当前值相等，则清空
                        mIdString = "";
                        mPwdString = "";
                        mIdEditText.setText(mIdString);
                        mPwdEditText.setText(mPwdString);
                    }
                    mUsers.remove(getItem(position));
                    mAdapter.notifyDataSetChanged(); // 更新ListView
                }
            });
            return convertView;
        }
    }
}
