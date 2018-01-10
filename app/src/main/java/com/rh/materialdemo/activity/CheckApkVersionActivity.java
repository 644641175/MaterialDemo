package com.rh.materialdemo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rh.materialdemo.MainActivity;
import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.DownloadListener;
import com.rh.materialdemo.Util.DownloadTask;
import com.rh.materialdemo.Util.HttpUtils;
import com.rh.materialdemo.dialog.CustomDialog;

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
public class CheckApkVersionActivity extends BaseActivity implements View.OnClickListener {
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

    private PackageInfo packageInfo;

    private String versionName;
    private int versionCode;
    private static String content;
    private String url;
    private TextView tvPro;

    /**
     * 升级提示框
     */
    private static CustomDialog updateDialog;
    private UpdateApkHandler mUpdateHandler = new UpdateApkHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkapkversion);
        initView();
        getJSON();
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
        HttpUtils.sendOkHttpRequestWithGET("http://10.203.147.113:8080/MyServlet/update/update.json", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mUpdateHandler.sendEmptyMessage(IO_ERROR);
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
                        mUpdateHandler.sendEmptyMessage(UPDATE_YES);
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
    private static void showUpdateDialog(CheckApkVersionActivity activity) {
        updateDialog = new CustomDialog(activity, 0, 0, R.layout.dialog_update,
                R.style.Theme_dialog, Gravity.CENTER, 0);
        //如果他点击其他地方，不安装，我们就直接去
        updateDialog.setOnCancelListener(dialog -> goHome(activity));
        // 更新内容
        TextView dialogUpdateContent = updateDialog.findViewById(R.id.dialog_update_content);
        dialogUpdateContent.setText(content);
        // 确定更新
        TextView dialogConfirm = updateDialog.findViewById(R.id.dialog_confrim);
        dialogConfirm.setOnClickListener(activity);
        // 取消更新
        TextView dialogCancel = updateDialog.findViewById(R.id.dialog_cancel);
        dialogCancel.setOnClickListener(activity);
        updateDialog.show();
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_confrim:
                updateDialog.dismiss();
                downloadAPK(url);

                break;
            case R.id.dialog_cancel:
                // 跳主页面
                updateDialog.dismiss();
                goHome(this);
                break;
            default:
        }
    }

    /**
     * 跳转主页面
     */
    private static void goHome(CheckApkVersionActivity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish();
    }

    /**
     * 请求写入SD卡的权限
     */
    private boolean mayRequestSDCard() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(tvPro, R.string.doenload_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE));
        } else {
            Log.e(TAG, "请求权限 ");
             requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult:系统权限已获得，开始下载 ");
                downloadAPK(url);
            }
        }
    }

    /**
     * 下载更新
     */
    private void downloadAPK(String downloadUrl) {
        tvPro.setVisibility(View.VISIBLE);
        if (!mayRequestSDCard()) {
            Log.e(TAG, "权限请求失败 ");
            tvPro.setText( "获取写入外部存储器权限失败\n    请先赋予App该系统权限");
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
                        showUpdateDialog(activity);
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
                        Toast.makeText(activity, "请检查网络", Toast.LENGTH_LONG).show();
                        goHome(activity);
                        break;
                    default:
                }
            }
        }
    }

}
