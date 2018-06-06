package com.rh.materialdemo.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannedString;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.ActivityCollector;
import com.rh.materialdemo.Util.MyToast;
import com.rh.materialdemo.adapter.MessageAdapter;
import com.rh.materialdemo.bean.ChatMessage;
import com.rh.materialdemo.service.WebSocketService;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author RH
 * @date 2018/1/4
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ChatActivity";
    private ListView mListView;
    private EditText mEditText;
    private static MessageAdapter messageAdapter;
    private static List<ChatMessage> chatChatMessage;
    private Intent webSocketService;
    public static MyHandler mHandler;
    private Button mbtSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*String message = getIntent().getStringExtra("data");
        Log.e(TAG, "onCreate: "+message);*/

        initView();
        init();
        initConnection();
    }

    @Override
    protected void onDestroy() {
        //stopService(webSocketService);
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
            /*一定要置为空，释放掉对Handler的引用，否则ChatActivity的Handler无法释放掉，下次就无法再次进行绑定（布局中无法再加载消息）*/
        WebSocketService.mMessenger = null;
    }

    private void initConnection() {
        webSocketService = new Intent(ChatActivity.this, WebSocketService.class);
        webSocketService.putExtra("messenger", new Messenger(mHandler));
        startService(webSocketService);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.content_chat_listView);
        mEditText = (EditText) findViewById(R.id.content_chat_edit);
        mbtSend = (Button) findViewById(R.id.content_chat_send);
        Button mbtReceive = (Button) findViewById(R.id.content_chat_receive);
        mbtSend.setOnClickListener(this);
        mbtReceive.setOnClickListener(this);
        mEditText.setOnKeyListener(onKey);
    }

    private void init() {
        chatChatMessage = new ArrayList<>();
        messageAdapter = new MessageAdapter(chatChatMessage);
        mListView.setAdapter(messageAdapter);

        mHandler = new MyHandler(ChatActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_chat_send:
                if (!"".equals(mEditText.getText().toString())) {
                    com.rh.materialdemo.bean.ChatMessage chatMessageSend = new ChatMessage();
                    chatMessageSend.setTime(grtFormatTime());
                    chatMessageSend.setType(MessageAdapter.TYPE_SEND);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        chatMessageSend.setMessageContent(filterHtml(Html.toHtml(mEditText.getText(), Html.FROM_HTML_MODE_COMPACT)));
                    } else {
                        chatMessageSend.setMessageContent(filterHtml(Html.toHtml(mEditText.getText())));
                    }
                    chatChatMessage.add(chatMessageSend);
                    messageAdapter.notifyDataSetChanged();
                    Log.e(TAG, "onClick: " + mEditText.getText().toString());
                    //发送给服务器
                    // WebSocketService.sendMsg(filterHtml(Html.toHtml(mEditText.getText())));
                    //这里文字转换后，网页上显示会乱码
                    WebSocketService.sendMsg(mEditText.getText().toString());
                    mEditText.setText("");
                }
                break;
            case R.id.content_chat_receive:
                MyToast.show("功能开发中，请关注后续更新！");
                break;
            default:
                break;
        }

    }

    /**
     * @return 获取当前时间
     */
    private  String grtFormatTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM—dd HH:mm", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 用正则表达式过滤掉无关的文本
     */
    public static String filterHtml(String str) {
        Log.e(TAG, "filterHtml: " + str);
        return str.replaceAll("<(?!br|img)[^>]+>", "").trim();
    }

    private void setTag() {

    }

    /**
     * 在 Java 语言中，非静态匿名内部类将持有一个对外部类的隐式引用，而静态内部类则不会。
     * <p>
     * //弱引用，避免Handler持有外部类的引用。即MainActivity的引用，
     * // 这样会导致MainActivity的上下文及资源无法被回收，引发内存泄露的情况发生
     *
     * 想要在别的类中直接使用ChatActivity.mHandler.sendEmptyMessage()需要将类的修饰符改为public
     */

    public static class MyHandler extends Handler {
        /**
         * Activity的弱引用
         */
        private WeakReference<ChatActivity> mActivity;

        private MyHandler(ChatActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
             ChatActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0x0001:
                        if (ActivityCollector.isActivityExist(ChatActivity.class)) {
                            SpannedString receiveMsg = SpannedString.valueOf(msg.obj.toString());
                            if (!TextUtils.isEmpty(receiveMsg)) {
                                ChatMessage chatMessageReceiver = new ChatMessage();
                                chatMessageReceiver.setTime(activity.grtFormatTime());
                                chatMessageReceiver.setType(MessageAdapter.TYPE_RECEIVE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    chatMessageReceiver.setMessageContent(filterHtml(Html.toHtml(receiveMsg, Html.FROM_HTML_MODE_COMPACT)));
                                } else {
                                    chatMessageReceiver.setMessageContent(filterHtml(Html.toHtml(receiveMsg)));
                                }
                                chatChatMessage.add(chatMessageReceiver);
                                messageAdapter.notifyDataSetChanged();
                                Log.e(TAG, "0x0001: 执行完");
                            }
                        } else {
                            Log.e(TAG, "activity已被释放: ");
                            mActivity.get().setTag();
                        }
                        break;
                    default:
                }
            } else {
                Log.e(TAG, "ChatActivity已被GC回收: ");
            }
        }
    }

    /**
     * 按下Enter键后发送消息
     */

    View.OnKeyListener onKey = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                mbtSend.performClick();
            }
            return false;
        }
    };

}
