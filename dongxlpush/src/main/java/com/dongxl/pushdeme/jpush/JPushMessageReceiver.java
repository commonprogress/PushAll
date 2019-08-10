package com.dongxl.pushdeme.jpush;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

import java.util.Arrays;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;

/**
 * 3.3.0开始是通过继承 JPushMessageReceiver并配置来接收所有事件回调。>
 * 如果仍然需要在这个Receiver里接收，需要在JPushMessageReceiver 的子类里不重写对应的回调方法，或者重写方法且调用super
 * //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
 */
public class JPushMessageReceiver extends cn.jpush.android.service.JPushMessageReceiver {
    private static final String TAG = JPushMessageReceiver.class.getSimpleName();

    /**
     * 发送消息到接收服务
     *
     * @param context
     * @param pushData
     */
    private void sendPushDataToService(final Context context, final PushDataBean pushData) {
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_JPSUH);
    }

    /**
     * 收到自定义消息回调
     * 如果需要在旧版本的Receiver接收cn.jpush.android.intent.MESSAGE_RECEIVED广播
     * 可以不重写此方法，或者重写此方法且调用super.onMessage
     * 如果重写此方法，没有调用super，则不会发送广播到旧版本Receiver
     *
     * @param context       Application Context。
     * @param customMessage 接收自定义消息内容
     */
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        LogUtils.i(TAG, "jpush onMessage CustomMessage is called. 111:" + customMessage.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(customMessage.message);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }

    /**
     * 通知的点击
     * 如果需要在旧版本的Receiver接收cn.jpush.android.intent.NOTIFICATION_OPENED广播
     * 可以不重写此方法，或者重写此方法且调用super.onNotifyMessageOpened
     * 如果重写此方法，没有调用super，则不会发送广播到旧版本Receiver
     *
     * @param context Application Context。
     * @param message 接收到的通知内容
     */
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        LogUtils.i(TAG, "jpush onNotifyMessageOpened is called. 111:" + message.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setNotifyId(message.notificationId);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_CLICK);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }

    /**
     * 通知栏自定义action点击 通知的MultiAction回调
     * 如果需要在旧版本的Receiver接收cn.jpush.android.intent.NOTIFICATION_CLICK_ACTION广播
     * 可以不重写此方法，或者重写此方法且调用super.onMultiActionClicked
     * 如果重写此方法，没有调用super，则不会发送广播到旧版本Receiver
     *
     * @param context Application Context。
     * @param intent  点击后触发的Intent
     */
    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        LogUtils.i(TAG, "jpush onMultiActionClicked is called. 111:");
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);
        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            Log.d(TAG, "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
            return;
        }
        if (nActionExtra.equals("my_extra1")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一");
        } else if (nActionExtra.equals("my_extra2")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二");
        } else if (nActionExtra.equals("my_extra3")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三");
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义");
        }

        final MessageDataBean messageData = new MessageDataBean();
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_CLICK);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }

    /**
     * 收到通知回调
     * 如果需要在旧版本的Receiver接收cn.jpush.android.intent.NOTIFICATION_RECEIVED广播
     * 可以不重写此方法，或者重写此方法且调用super.onNotifyMessageArrived
     * 如果重写此方法，没有调用super，则不会发送广播到旧版本Receiver
     *
     * @param context Application Context。
     * @param message 接收到的通知内容
     */
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        LogUtils.i(TAG, "jpush onNotifyMessageArrived is called. 111:" + message.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setNotifyId(message.notificationId);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_ARRIVE);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }

    /**
     * 清除通知回调
     * 1.同时删除多条通知，可能不会多次触发清除通知的回调
     * 2.只有用户手动清除才有回调，调接口清除不会有回调
     *
     * @param context Application Context。
     * @param message 清除的通知内容
     */
    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        LogUtils.i(TAG, "jpush onNotifyMessageDismiss is called. 111:" + message.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setNotifyId(message.notificationId);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER);
        String reason = "Jpush onNotifyMessageDismiss Result " + message.toString();
        pushData.setReason(reason);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }

    /**
     * 注册成功回调
     *
     * @param context        Application Context。
     * @param registrationId 注册id
     */
    @Override
    public void onRegister(Context context, String registrationId) {
        LogUtils.i(TAG, "jpush onRegister is called. 111:registrationId=" + registrationId);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER);
        String reason;
        if (!TextUtils.isEmpty(registrationId)) {
            pushData.setRegId(registrationId);
            reason = "Jpush onRegister Result SUCCESS registrationId=" + registrationId;
        } else {
            reason = "Jpush onRegister Result Failed";
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * 长连接状态回调
     *
     * @param context     Application Context。
     * @param isConnected 长连接状态
     */
    @Override
    public void onConnected(Context context, boolean isConnected) {
        LogUtils.i(TAG, "jpush onConnected is called. 111isConnected:" + isConnected);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER);
        String reason = "Jpush GetPushStatus Result SUCCESS onConnected=" + isConnected;
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * 注册失败回调
     *
     * @param context    Application Context。
     * @param cmdMessage 错误信息
     */
    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        LogUtils.i(TAG, "jpush onCommandResult is called. 111:" + cmdMessage.toString());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER, cmdMessage.errorCode);
        String reason = "Jpush onCommandResult Result Failed msg " + cmdMessage.toString();
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * tag 增删查改的操作会在此方法中回调结果。
     *
     * @param context
     * @param jPushMessage
     */
    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
        LogUtils.i(TAG, "jpush onTagOperatorResult is called. 111:" + jPushMessage.toString());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_TOPIC, jPushMessage.getErrorCode());
        String reason;
        if (jPushMessage.getErrorCode() == JPushMessageReceiver.JPushResultCode.JPUSH_SUCCESS) {
            pushData.setTopic(Arrays.toString(jPushMessage.getTags().toArray()));
            reason = "Jpush TagOperatorResult Result SUCCESS msg=" + Arrays.toString(jPushMessage.getTags().toArray());
        } else {
            if (jPushMessage.getErrorCode() == JPushMessageReceiver.JPushResultCode.JPUSH_MAX_LIMIT) {
                reason = " tags is exceed limit need to clean";
            } else {
                reason = "Jpush TagOperatorResult Result Failed code=" + jPushMessage.getErrorCode();
            }
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);

    }

    /**
     * 查询某个 tag 与当前用户的绑定状态的操作会在此方法中回调结果。
     *
     * @param context
     * @param jPushMessage
     */
    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
        LogUtils.i(TAG, "jpush onCheckTagOperatorResult is called. 111:" + jPushMessage.toString());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER, jPushMessage.getErrorCode());
        String reason;
        if (jPushMessage.getErrorCode() == JPushMessageReceiver.JPushResultCode.JPUSH_SUCCESS) {
            pushData.setTopic(Arrays.toString(jPushMessage.getTags().toArray()));
            reason = "Jpush onCheckTagOperatorResult Result SUCCESS msg=" + Arrays.toString(jPushMessage.getTags().toArray());
        } else {
            reason = "Jpush onCheckTagOperatorResult Result Failed code=" + jPushMessage.getErrorCode();
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * alias 相关的操作会在此方法中回调结果。
     *
     * @param context
     * @param jPushMessage
     */
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        LogUtils.i(TAG, "jpush onAliasOperatorResult is called. 111:" + jPushMessage.toString());
//        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context, jPushMessage);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_ALIAS, jPushMessage.getErrorCode());
        String reason;
        if (jPushMessage.getErrorCode() == JPushMessageReceiver.JPushResultCode.JPUSH_SUCCESS) {
            pushData.setAlias(jPushMessage.getAlias());
            reason = "Jpush onAliasOperatorResult Result SUCCESS msg=" + jPushMessage.getAlias();
        } else {
            reason = "Jpush onAliasOperatorResult Result Failed code=" + jPushMessage.getErrorCode();
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * 设置手机号码会在此方法中回调结果。
     *
     * @param context
     * @param jPushMessage
     */
    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
        LogUtils.i(TAG, "jpush onMobileNumberOperatorResult is called. 111:" + jPushMessage.toString());
//        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context, jPushMessage);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER, jPushMessage.getErrorCode());
        String reason;
        if (jPushMessage.getErrorCode() == JPushMessageReceiver.JPushResultCode.JPUSH_SUCCESS) {
            reason = "Jpush onMobileNumberOperatorResult Result SUCCESS msg=" + jPushMessage.getMobileNumber();
        } else {
            reason = "Jpush onMobileNumberOperatorResult Result Failed code=" + jPushMessage.getErrorCode();
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    public class JPushResultCode {

        public static final int JPUSH_SUCCESS = 0;
        public static final int JPUSH_MAX_LIMIT = 6018;
    }
}
