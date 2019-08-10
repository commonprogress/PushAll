package com.dongxl.pushdeme.jpush;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;

/**
 * 3.3.0开始是通过继承 JPushMessageReceiver并配置来接收所有事件回调。>
 * 如果仍然需要在这个Receiver里接收，需要在JPushMessageReceiver 的子类里不重写对应的回调方法，或者重写方法且调用super
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

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        LogUtils.i(TAG, "jpush onMessage CustomMessage is called. 111:" + customMessage.toString());
        processCustomMessage(context, customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        LogUtils.i(TAG, "jpush onNotifyMessageOpened is called. 111:" + message.toString());
//        try {
//            //打开自定义的Activity
//            Intent i = new Intent(context, TestActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle);
//            bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent);
//            i.putExtras(bundle);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(i);
//        } catch (Throwable throwable) {
//
//        }
    }

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
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        LogUtils.i(TAG, "jpush onNotifyMessageArrived is called. 111:" + message.toString());
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        LogUtils.i(TAG, "jpush onNotifyMessageDismiss is called. 111:" + message.toString());
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        LogUtils.i(TAG, "jpush onRegister is called. 111:registrationId=" + registrationId);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        LogUtils.i(TAG, "jpush onConnected is called. 111isConnected:" + isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        LogUtils.i(TAG, "jpush onCommandResult is called. 111:" + cmdMessage.toString());
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        LogUtils.i(TAG, "jpush onTagOperatorResult is called. 111:" + jPushMessage.toString());
//        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context, jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        LogUtils.i(TAG, "jpush onCheckTagOperatorResult is called. 111:" + jPushMessage.toString());
//        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context, jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        LogUtils.i(TAG, "jpush onAliasOperatorResult is called. 111:" + jPushMessage.toString());
//        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context, jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        LogUtils.i(TAG, "jpush onMobileNumberOperatorResult is called. 111:" + jPushMessage.toString());
//        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context, jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    //send msg to JPushMainActivity
    private void processCustomMessage(Context context, CustomMessage customMessage) {
        LogUtils.i(TAG, "jpush processCustomMessage is called. 111:" + customMessage.toString());
//        if (JPushMainActivity.isForeground) {
//            String message = customMessage.message;
//            String extras = customMessage.extra;
//            Intent msgIntent = new Intent(JPushMainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(JPushMainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (extraJson.length() > 0) {
//                        msgIntent.putExtra(JPushMainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//        }
    }
}
