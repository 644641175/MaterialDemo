package com.rh.materialdemo.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rh.materialdemo.R;
import com.rh.materialdemo.activity.ChatActivity;
import com.rh.materialdemo.bean.ChatMessage;

import java.util.List;

/**
 * @author RH
 * @date 2018/1/4
 */

public class MessageAdapter extends BaseAdapter {
    public final static int TYPE_SEND = 0;
    public final static int TYPE_RECEIVE = 1;
    /**
     * (TYPE_SEND<TYPE_SUM)&&(TYPE_RECEIVE<TYPE_SUM)
     */
    private static final int TYPE_SUM = 2;
    private List<ChatMessage> chatMessageList;

    public MessageAdapter(List<ChatMessage> chatChatMessage) {
        chatMessageList = chatChatMessage;
    }

    class SendViewHolder {
        TextView mTextViewSend;
        TextView mTextViewTime;
    }

    class ReceiveViewHolder {
        TextView mTextViewReceive;
        TextView mTextViewTime;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendViewHolder sendViewHolder;
        ReceiveViewHolder receiveViewHolder;
        ChatMessage chatMessage = chatMessageList.get(position);
        switch (getItemViewType(position)) {
            case TYPE_SEND:
                if (convertView == null) {
                    sendViewHolder = new SendViewHolder();
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_listview_item_send, null);
                    sendViewHolder.mTextViewSend = convertView.findViewById(R.id.chat_item_send);
                    sendViewHolder.mTextViewTime = convertView.findViewById(R.id.chat_item__send_time);
                    convertView.setTag(sendViewHolder);
                } else {
                    sendViewHolder = (SendViewHolder) convertView.getTag();
                }
                //设置布局内容
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //html块元素之间使用一个换行符分隔
                    sendViewHolder.mTextViewSend.setText(Html.fromHtml(chatMessage.getMessageContent(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    sendViewHolder.mTextViewSend.setText(Html.fromHtml(chatMessage.getMessageContent()));
                }
                if (position > 0 && chatMessage.getTime().equals(chatMessageList.get(position - 1).getTime())) {
                    sendViewHolder.mTextViewTime.setVisibility(View.GONE);
                } else {
                    sendViewHolder.mTextViewTime.setText(chatMessage.getTime());
                    sendViewHolder.mTextViewTime.setVisibility(View.VISIBLE);
                }
                break;
            case TYPE_RECEIVE:
                if (convertView == null) {
                    receiveViewHolder = new ReceiveViewHolder();
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_listview_item_receive, null);
                    receiveViewHolder.mTextViewReceive = convertView.findViewById(R.id.chat_item_receive);
                    receiveViewHolder.mTextViewTime = convertView.findViewById(R.id.chat_item__receive_time);
                    convertView.setTag(receiveViewHolder);
                } else {
                    receiveViewHolder = (ReceiveViewHolder) convertView.getTag();
                }
                //开始设置布局
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    receiveViewHolder.mTextViewReceive.setText(Html.fromHtml(chatMessage.getMessageContent(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    receiveViewHolder.mTextViewReceive.setText(Html.fromHtml(chatMessage.getMessageContent()));
                }
                if (position > 0 && chatMessage.getTime().equals(chatMessageList.get(position - 1).getTime())) {
                    receiveViewHolder.mTextViewTime.setVisibility(View.GONE);
                } else {
                    receiveViewHolder.mTextViewTime.setText(chatMessage.getTime());
                    receiveViewHolder.mTextViewTime.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
        return convertView;
    }


    /**
     * @param position listview中的位置
     * @return 该位置message的type
     */
    @Override
    public int getItemViewType(int position) {
        return chatMessageList.get(position).getType();
    }

    /**
     * 获取类型的总数,默认是1
     *
     * @return 类型的种类总数，此处使用的是2种,TYPE_SEND和TYPE_RECEIVE必须为0和1，否则会ArrayIndexOutOfBoundsException
     * getItemViewType < getViewTypeCount
     */
    @Override
    public int getViewTypeCount() {
        return TYPE_SUM;
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
