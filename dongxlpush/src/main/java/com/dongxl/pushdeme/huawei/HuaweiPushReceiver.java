/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2016. All rights reserved.
 * See LICENSE.txt for this sample's licensing information.
 */

package com.dongxl.pushdeme.huawei;

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;
import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

import java.io.UnsupportedEncodingException;

/**
 * 应用需要创建一个子类继承com.huawei.hms.support.api.push.PushReceiver，
 * 实现onToken，onPushState ，onPushMsg，onEvent，这几个抽象方法，用来接收token返回，push连接状态，透传消息和通知栏点击事件处理。
 * onToken 调用getToken方法后，获取服务端返回的token结果，返回token以及belongId
 * onPushState 调用getPushState方法后，获取push连接状态的查询结果
 * onPushMsg 推送消息下来时会自动回调onPushMsg方法实现应用透传消息处理。本接口必须被实现。 在开发者网站上发送push消息分为通知和透传消息
 * 通知为直接在通知栏收到通知，通过点击可以打开网页，应用 或者富媒体，不会收到onPushMsg消息
 * 透传消息不会展示在通知栏，应用会收到onPushMsg
 * onEvent 该方法会在设置标签、点击打开通知栏消息、点击通知栏上的按钮之后被调用。由业务决定是否调用该函数。
 * <p>
 * Application needs to create a subclass to inherit com.huawei.hms.support.api.push.PushReceiver,
 * implement Ontoken,onpushstate, Onpushmsg,onevent,
 * these several abstract methods, Used to receive token return, push connection status,
 * pass through message and notification bar click event handling.
 * After Ontoken calls the GetToken method, gets the token result returned by the server, returns token,
 * and after Belongidonpushstate invokes the Getpushstate method,
 * gets the query result of the push connection state onpushmsg When a push message comes down,
 * it will automatically callback the Onpushmsg method to implement the application of the message processing.
 * This interface must be implemented.
 * On the developer website to send a push message is divided into notification and message notification for direct notification in the notice bar,
 * by clicking can open the Web page, application or rich media,
 * will not receive ONPUSHMSG messages will not be displayed in the notification bar,
 * application will receive onpushmsgonevent the method will It is called after you set the label,
 * click to open the Notification bar message, and click the button on the notification bar.
 * The business decides whether to call the function.
 */
public class HuaweiPushReceiver extends PushReceiver {

    public static final String TAG = HuaweiPushReceiver.class.getSimpleName();

    /**
     * 发送消息到接收服务
     *
     * @param context
     * @param pushData
     */
    private void sendPushDataToService(final Context context, final PushDataBean pushData) {
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_HUAWEI);
    }

    /**
     * 获取token 的监听
     *
     * @param context
     * @param token
     * @param extras
     */
    @Override
    public void onToken(Context context, String token, Bundle extras) {
        //开发者自行实现Token保存逻辑
        String belongId = null == extras ? "" : extras.getString("belongId");
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER);
        pushData.setRegId(token);
        String reason = "belongId is:" + belongId + " Token is:" + token;
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
        LogUtils.i(TAG, "华为onToken is called. 111: log==" + reason + "==extras==" + extras.toString());
    }

    /**
     * 开发者自行实现透传消息处理
     *
     * @param context
     * @param msg
     * @param bundle
     * @return
     */
    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        String token = null == bundle ? "" : bundle.getString(BOUND_KEY.deviceTokenKey);
        String reason;
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE);
        try {
            //CP可以自己解析消息内容，然后做相应的处理 | CP can parse message content on its own, and then do the appropriate processing
            String content = new String(msg, "UTF-8");
            final MessageDataBean messageData = new MessageDataBean();
            messageData.setContent(content);
            pushData.setThroughMessage(messageData);
            reason = "Receive a push pass message with the message:" + content;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            reason = "Receive push pass message, exception:" + e.getMessage();
        }
        pushData.setRegId(token);
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
        LogUtils.i(TAG, "华为onPushMsg is called. 111:==reason=" + reason + "==bundle==" + bundle);
        return false;
    }

    /**
     * 可选，后续版本逐渐废弃，请开发者谨慎使用：
     * Event.NOTIFICATION_OPENED 通知栏点击事件
     * Event.NOTIFICATION_CLICK_BTN 通知栏按钮点击事件
     *
     * @param context
     * @param event
     * @param extras
     */
    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        int notifyId = 0;
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            notifyId = null == extras ? 0 : extras.getInt(BOUND_KEY.pushNotifyId, 0);
//            if (0 != notifyId) {
//                NotificationManager manager = (NotificationManager) context
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.cancel(notifyId);
//            }
        }
        String msgKey = null == extras ? "" : extras.getString(BOUND_KEY.pushMsgKey);
        String reason = "Received event,notifyId:" + notifyId + " msgKey:" + msgKey;

        final MessageDataBean messageData = new MessageDataBean();
        messageData.setNotifyId(notifyId);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_CLICK);
        pushData.setReason(reason);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
        LogUtils.i(TAG, "华为onEvent is called. 111:==reason=" + reason + "==bundle==" + extras);
        super.onEvent(context, event, extras);
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE);
        String reason = "The Push connection status is:" + pushState;
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
        LogUtils.i(TAG, "华为onPushState is called. 111:==reason=" + reason);
    }

}
