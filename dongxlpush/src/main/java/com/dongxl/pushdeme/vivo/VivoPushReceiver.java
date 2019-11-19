package com.dongxl.pushdeme.vivo;

import android.content.Context;
import android.text.TextUtils;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class VivoPushReceiver extends OpenClientPushMessageReceiver {
    /**
     * TAG to Log
     */
    public static final String TAG = VivoPushReceiver.class.getSimpleName();

    /**
     * 通知栏消息点击
     * @param context
     * @param msg
     */
    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
        LogUtils.i(TAG, "vivo onNotificationMessageClicked is called. 111:" + msg.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(msg.getSkipContent());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_CLICK);
        pushData.setThroughMessage(messageData);
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_VIVO);
    }

    /**
     * regid
     * @param context
     * @param regId
     */
    @Override
    public void onReceiveRegId(Context context, String regId) {
        LogUtils.i(TAG, "vivo onReceiveRegId is called. 111: regId=" + regId);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER);
        String reason;
        if (!TextUtils.isEmpty(regId)) {
            pushData.setRegId(regId);
            reason = "vivo RegisterResult SUCCESS mRegId=" + regId;
        } else {
            reason = "vivo RegisterResult Failed ";
        }
        pushData.setReason(reason);
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_VIVO);
    }

}