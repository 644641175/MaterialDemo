package com.rh.materialdemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.rh.materialdemo.MainActivity;
import com.rh.materialdemo.MyApplication;
import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.DownloadListener;
import com.rh.materialdemo.Util.DownloadTask;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.dialog.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * @author RH
 * @date 2018/1/9
 */
public class CheckApkVersionActivity extends BaseActivity{
    private static final String TAG = "CheckApkVersionActivity";
    /**
     * 更新
     */
    private static final int UPDATE_YES = 1;
    /**
     * 不更新
     */
    private static final int UPDATE_NO = 2;
    /**
     * 没有网络
     */
    private static final int IO_ERROR = 4;
    /**
     * 数据异常
     */
    private static final int JSON_ERROR = 5;

    /**
     * ID以标识WRITE_EXTERNAL_STORAGE权限请求。
     */
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSION_REQUEST_CODE = 1;

    private PackageInfo packageInfo;
    private String versionName;
    private int versionCode;
    private static String content;
    private static String url;
    private TextView tvPro;
    /**
     * 升级提示框
     */
    private MyDialog myDialog;
    private UpdateApkHandler mUpdateHandler = new UpdateApkHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*系统版本为Android5.0以上时 状态栏与布局融合*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_checkapkversion);
        if (isFirstInstall()) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("isFirstInstall", false);
            editor.apply();
            requestAllNeedPermissions();
        } else {
            initView();
            getJSON();
        }
    }

    public boolean isFirstInstall() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("isFirstInstall", true);
    }

    /**
     * 请求App所需的所有权限
     */
    private void requestAllNeedPermissions() {
        boolean isAllGranted = checkPermissionAllGranted(
                new String[]{
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );
        if (!isAllGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    MY_PERMISSION_REQUEST_CODE
            );
        }
    }

    /**
     * 检查是否拥有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化控件
     *
     * @author LGL
     */
    @SuppressLint("SetTextI18n")
    private void initView() {
        tvPro = (TextView) findViewById(R.id.tv_pro);

        TextView tvVersion = (TextView) findViewById(R.id.tv_version);
        // 设置版本号
        tvVersion.setText("版本号：" + getAppVersion());
    }

    /**
     * @return APP版本号
     */
    private String getAppVersion() {
        try {
            // PackageManager管理器
            PackageManager pm = getPackageManager();
            // 获取相关信息
            packageInfo = pm.getPackageInfo(getPackageName(), 0);
            // 版本名称
            String name = packageInfo.versionName;
            // 版本号
            int version = packageInfo.versionCode;

            Log.e("版本信息", "版本名称：" + name + "版本号" + version);

            return name;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 如果出现异常抛出
        return "无法获取";
    }

    /**
     * 解析JSON
     */
    private void getJSON() {
        // 子线程访问，耗时操作
        HttpUtils.sendOkHttpRequestWithGETWithConnectTimeOut("http://10.203.147.113:8080/MyServlet/update/update.json", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toast.makeText(MyApplication.getContext(), "与服务器连接超时，请与服务器管理员联系！", Toast.LENGTH_LONG).show();
                mUpdateHandler.sendEmptyMessage(IO_ERROR);
                Looper.loop();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    versionName = jsonObject.getString("versionName");
                    versionCode = jsonObject.getInt("versionCode");
                    content = jsonObject.getString("content");
                    url = jsonObject.getString("url");
                    Log.e(TAG, "versionName：" + versionName + "\n"
                            + "versionCode：" + versionCode + "\n"
                            + "content：" + content + "\n"
                            + "url：" + url);
                    // 版本判断
                    if (versionCode > getCode()) {
                        // 提示更新
                        //mUpdateHandler.sendEmptyMessage(UPDATE_YES);
                        //mUpdateHandler.post(() -> showUpdateDialog());
                        mUpdateHandler.post(() -> mySelfDialog(content));
                    } else {
                        // 不更新，跳转到主页
                        mUpdateHandler.sendEmptyMessage(UPDATE_NO);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mUpdateHandler.sendEmptyMessage(JSON_ERROR);
                }

            }
        });

    }

    /**
     * 获取versionCode
     */
    private int getCode() {
        // PackageManager管理器
        PackageManager pm = getPackageManager();
        // 获取相关信息
        try {
            packageInfo = pm.getPackageInfo(getPackageName(), 0);
            // 版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * 升级弹框
     */
    private void mySelfDialog(String content){
        MyDialog myDialog = new MyDialog(this);
        myDialog.setTitle("检测到有新版本，是否下载？");
        myDialog.setMessage(content);
        myDialog.setYesOnclickListener("立即下载", () -> {
            myDialog.dismiss();
            downloadAPK(url);
        });
        myDialog.setNoOnClickListener("暂不更新", () -> {
            myDialog.dismiss();
            startActivity(new Intent(   CheckApkVersionActivity.this, MainActivity.class));
            finish();
        });
        myDialog.show();
    }

    /**
     * 跳转主页面
     */
    private static void goHome(CheckApkVersionActivity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish();
    }

    /**
     * 如果未赋予权限的话，请求写入SD卡的权限
     */
    private boolean mayRequestSDCard() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "onRequestPermissionsResult:系统权限已获得，开始下载 ");
                    downloadAPK(url);
                } else {
                    tvPro.setText("用户权限拒绝，App更新失败！");
                    tvPro.setTextColor(Color.rgb(255, 64, 129));
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(CheckApkVersionActivity.this, MainActivity.class));
                            finish();
                    }, 2000);
                }
                break;
            case MY_PERMISSION_REQUEST_CODE:
                initView();
                getJSON();
                break;
            default:
        }
    }

    /**
     * 下载更新
     */
    @SuppressLint("SetTextI18n")
    private void downloadAPK(String downloadUrl) {
        tvPro.setVisibility(View.VISIBLE);
        if (!mayRequestSDCard()) {
            Log.e(TAG, "downloadAPK: 未获取下载权限");
            return;
        }
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        Log.e(TAG, "开始下载新APK: ");
        DownloadTask downloadTask = new DownloadTask(new DownloadListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProcess(int progress) {
                // 显示进度
                tvPro.setText(progress + "%");
            }

            @Override
            public void onSuccess() {
                // 跳转系统安装页面
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //在AndroidManifest中的android:authorities值
                    Uri apkUri = FileProvider.getUriForFile(CheckApkVersionActivity.this, "com.rh.materialdemo.fileprovider", new File(directory + fileName));
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    startActivity(install);
                    finish();
                } else {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setDataAndType(Uri.fromFile(new File(directory + fileName)), "application/vnd.android.package-archive");
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onCanceled() {

            }
        });
        downloadTask.execute(downloadUrl);

    }


    public static class UpdateApkHandler extends Handler {

        private WeakReference<CheckApkVersionActivity> mActivity;

        private UpdateApkHandler(CheckApkVersionActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CheckApkVersionActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case UPDATE_YES:
                        //showUpdateDialog(activity);
                        break;
                    case UPDATE_NO:
                        Log.e(TAG, "Apk不需要更新: ");
                        goHome(activity);
                        break;
                    case JSON_ERROR:
                        Toast.makeText(activity, "Json解析错误", Toast.LENGTH_LONG).show();
                        goHome(activity);
                        break;
                    case IO_ERROR:
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        goHome(activity);
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (myDialog != null){
            myDialog.dismiss();
            myDialog = null;
        }
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (myDialog != null){
            myDialog.dismiss();
            myDialog = null;
        }
        super.onDestroy();
    }
}
