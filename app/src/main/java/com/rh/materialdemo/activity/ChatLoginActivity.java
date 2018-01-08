package com.rh.materialdemo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.rh.materialdemo.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ChatLoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "ChatActivity";
    /**
     * ID以标识READ_CONTACTS权限请求。
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * 包含已知用户名和密码的虚拟身份验证存储。
     * TODO: 连接到真正的身份验证系统后删除。
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "whuser@example.com:123456", "administrator@example.com:hello"
    };
    /**
     * 跟踪登录任务，如果请求的话确保我们可以取消它。
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox mCheckBox;
    private Button mbutton_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_login);
        // 设置登录表单。
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();//请求获取通讯录

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mCheckBox = (CheckBox) findViewById(R.id.checkbox_remember);
        mCheckBox.setOnCheckedChangeListener(this);

        mbutton_login = (Button) findViewById(R.id.button_login);
        mbutton_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        readaccount();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.e(TAG, "onCheckedChanged: " + isChecked);
        if (isChecked) {
            //sharedpreferance非常适合保存零散的简单的数据，这里使用它来保存用户名和密码
            //路径data/data/……
            SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);//设置属性，90%为私有
            //拿到SharedPreferences的编辑器
            SharedPreferences.Editor ed = sp.edit();
            //将数据放入
            ed.putString("name", mEmailView.getText().toString());
            Log.e(TAG, "name: " + mEmailView.getText().toString());
            ed.putString("password", mPasswordView.getText().toString());
            Log.e(TAG, "password: " + mPasswordView.getText().toString());
            //最后记得提交
            ed.commit();
        }

    }

    //读取存储的用户信息
    private void readaccount() {
        SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);//90%为私有
        String name = sp.getString("name", "");
        Log.e(TAG, "readaccount: " + name);
        String password = sp.getString("password", "");
        Log.e(TAG, "readaccount: " + password);
        mEmailView.setText(name);
        mPasswordView.setText(password);

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);//初始化一个基于LoaderManager的异步查询操作。
    }

    //使用时请求通讯录权限
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * 当权限请求已完成时收到回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * 尝试登录或注册由登录表单指定的帐户。如果存在表单错误（无效的电子邮件，缺少字段等），则会显示错误，并且不会进行实际的登录尝试。
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 如果用户输入一个密码，请检查一个有效的密码。
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // 检查一个有效的电子邮件地址。
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            //错误; 不要尝试登录并将第一个表单字段与一个错误集中。
            focusView.requestFocus();
        } else {
            //显示进度条，并启动后台任务来执行用户登录尝试。
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    //判断账号是否合乎规则
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
        //return email.length() < 20;
    }

    //判断密码是否合乎规则
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // 在Honeycomb MR2上，我们有ViewPropertyAnimator APIs，它允许非常简单的动画。 如果可用，请使用这些API淡入进度微调器。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 表示用于验证用户的异步登录/注册任务。
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            /**不能在doInBackground中更新UI*/
            try {
                // 模拟网络访问。
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return 1;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // 帐户存在，如果密码匹配则返回true。
                    if (pieces[1].equals(mPassword)) {
                        Log.e(TAG, "登录成功");
                        return 0;
                    } else {
                        return 1;
                    }
                }
            }

            try {
                // 模拟注册中。
                Thread.sleep(2000);
                return 2;
            } catch (InterruptedException e) {
                return 3;
            }
        }

        @Override
        protected void onPostExecute(final Integer code) {
            mAuthTask = null;
            showProgress(false);

            if (code == 0) {
                Intent chatActivity = new Intent(ChatLoginActivity.this, ChatActivity.class);
                startActivity(chatActivity);
                finish();
            } else if (code == 1) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            } else if (code == 2) {
                Toast.makeText(ChatLoginActivity.this, "功能完善中，暂不支持注册", Toast.LENGTH_LONG).show();
                Log.e(TAG, "注册完成");
            } else {
                Toast.makeText(ChatLoginActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                Log.e(TAG, "注册失败");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    /**
     * LoaderCallbacks<Cursor>异步的数据库操作方法
     * 其子方法如下：
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // 检索设备用户的“个人资料”联系人的数据行。
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                //只选择电子邮件地址
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},
                //首先显示主要电子邮件地址。 请注意，如果用户没有指定主电子邮件地址，则不会有主电子邮件地址。
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //创建适配器来告诉AutoCompleteTextView在下拉列表中显示什么。
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatLoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


}

