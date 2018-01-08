package com.rh.materialdemo.bean;

/**
 * @author RH
 * @date 2018/1/4
 */

public class ChatMessage {
    private int type = 0;
    private String messageContent;
    private String time;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
