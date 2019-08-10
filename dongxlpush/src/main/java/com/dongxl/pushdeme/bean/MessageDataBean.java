package com.dongxl.pushdeme.bean;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessageDataBean implements Serializable {

    public final static String DataKey = "data";
    public final static String UserKey = "user";
    public final static String TypeKey = "type";
    public final static String ConvKey = "conv";

    private String from;
    private String content;
    private Map<String, String> messageMap = new HashMap<>();
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
        parserPushContent(content);
    }

    /**
     * push 消息的解析
     * {
     * "conv":"054ee1e7-ca8a-4210-b4d8-8ff8b08bb2ac"
     * "user":"b61110f5-98db-4076-8853-54d4b64c2eb7"
     * "data":"{"id":"054ee1e7-ca8a-4210-b4d8-8ff8b08bb2ac"}"
     * "type":"notice"
     * }
     *
     * @param json
     */
    private void parserPushContent(String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
            if (null == jsonObject) {
                return;
            }
            if (null == messageMap) {
                messageMap = new HashMap<>();
            }
            if (jsonObject.has(ConvKey)) {
                JsonElement jsonElement = jsonObject.get(ConvKey);
                if (!jsonElement.isJsonNull()) {
                    messageMap.put(ConvKey, jsonObject.get(ConvKey).getAsString());
                }
            }
            if (jsonObject.has(UserKey)) {
                JsonElement jsonElement = jsonObject.get(UserKey);
                if (!jsonElement.isJsonNull()) {
                    messageMap.put(UserKey, jsonObject.get(UserKey).getAsString());
                }
            }
            if (jsonObject.has(DataKey)) {
                JsonElement jsonElement = jsonObject.get(DataKey);
                if (!jsonElement.isJsonNull()) {
                    if (jsonElement.isJsonObject()) {
                        messageMap.put(DataKey, jsonElement.getAsJsonObject().toString());
                    } else {
                        messageMap.put(DataKey, jsonElement.getAsJsonObject().getAsString());
                    }
                }
            }
            if (jsonObject.has(TypeKey)) {
                JsonElement jsonElement = jsonObject.get(TypeKey);
                if (!jsonElement.isJsonNull()) {
                    messageMap.put(TypeKey, jsonObject.get(TypeKey).getAsString());
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getMessageMap() {
//        parserPushContent(content);
        return messageMap;
    }

    public void setMessageMap(Map<String, String> messageMap) {
        this.messageMap = messageMap;
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
            ", messageMap=" + messageMap +
            ", sentTime=" + sentTime +
            ", notifyId=" + notifyId +
            '}';
    }
}
