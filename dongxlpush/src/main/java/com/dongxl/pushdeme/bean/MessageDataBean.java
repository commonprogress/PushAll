package com.dongxl.pushdeme.bean;

import java.io.Serializable;

public class MessageDataBean implements Serializable {

    private String from;
    private String content;
    private long sentTime;
    private int notifyId;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public MessageDataBean() {
    }

    @Override
    public String toString() {
        return "MessageDataBean{" +
                "from='" + from + '\'' +
                ", content='" + content + '\'' +
                ", sentTime=" + sentTime +
                ", notifyId=" + notifyId +
                '}';
    }
}
