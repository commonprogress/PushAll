/**
 * Wire
 * Copyright (C) 2019 Wire Swiss GmbH
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dongxl.pushdeme.xiaomi;

import android.content.Context;
import android.text.TextUtils;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;
import java.util.Map;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 XiaoMiPushReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 * <receiver
 * android:name="com.dongxl.pushdeme.xiaomi.XiaoMiPushReceiver"
 * android:exported="true">
 * <intent-filter>
 * <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 * </intent-filter>
 * <intent-filter>
 * <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 * </intent-filter>
 * <intent-filter>
 * <action android:name="com.xiaomi.mipush.ERROR" />
 * </intent-filter>
 * </receiver>
 * }</pre>
 * 3、XiaoMiPushReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、XiaoMiPushReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、XiaoMiPushReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、XiaoMiPushReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、XiaoMiPushReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 */
public class XiaoMiPushReceiver extends PushMessageReceiver {

    private final static String TAG = XiaoMiPushReceiver.class.getSimpleName();

    /**
     * 发送消息到接收服务
     *
     * @param context
     * @param pushData
     */
    private void sendPushDataToService(final Context context, final PushDataBean pushData) {
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_XIAOMI);
    }

    /**
     * 透传消息
     *
     * @param context
     * @param message
     */
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        super.onReceivePassThroughMessage(context, message);
        LogUtils.i(TAG, "onReceivePassThroughMessage is called. 111:" + message.toString());

        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(message.getContent());
        final Map<String, String> extra = message.getExtra();
        if (null != extra) {
            if (extra.containsKey("__m_ts")) {
                messageData.setSentTime(getStringToLong(extra.get("__m_ts")));
            }
        }
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        super.onNotificationMessageClicked(context, message);
        LogUtils.i(TAG, "onNotificationMessageClicked is called. 111:" + message.toString());

        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(message.getContent());
        final Map<String, String> extra = message.getExtra();
        if (null != extra) {
            if (extra.containsKey("__m_ts")) {
                messageData.setSentTime(getStringToLong(extra.get("__m_ts")));
            }
        }
        messageData.setNotifyId(message.getNotifyId());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_CLICK);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
//        LogUtils.i(TAG, "onNotificationMessageClicked is called. 222222");
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setClassName(context,"com.waz.zclient.MainActivity");
//        context.startActivity(intent);
//        LogUtils.i(TAG, "onNotificationMessageClicked is called. 333333");
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        super.onNotificationMessageArrived(context, message);
        LogUtils.i(TAG, "onNotificationMessageArrived is called. 111:" + message.toString());
//messageId={tcm525425645657517098r}
// ,passThrough={0},alias={null}
// ,topic={**ALL**},userAccount={null}
// ,content={{"conv":"054ee1e7-ca8a-4210-b4d8-8ff8b08bb2ac","data":{"id":"054ee1e7-ca8a-4210-b4d8-8ff8b08bb2ac"},"type":"notice"}}
// ,description={d},
// title={t},
// isNotified={false}
// ,notifyId={0}
// ,notifyType={0},
// category={null},
// extra={{mt_start=1564565751748, fe_end=1564565751709, __m_ts=1564648634229, fe_start=1564565751708}}
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(message.getContent());
        final Map<String, String> extra = message.getExtra();
        if (null != extra) {
            if (extra.containsKey("__m_ts")) {
                messageData.setSentTime(getStringToLong(extra.get("__m_ts")));
            }
        }
        messageData.setNotifyId(message.getNotifyId());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_ARRIVE);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        super.onReceiveRegisterResult(context, message);
        String reason = message.getReason();
//        String command = message.getCommand();
//        List<String> arguments = message.getCommandArguments();
//        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);

//        final PushDataBean pushData = new PushDataBean(command, message.getResultCode());
//        switch (command) {
//            case MiPushClient.COMMAND_REGISTER:
//                pushData.setResultType(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER);
//                if (message.getResultCode() == ErrorCode.SUCCESS) {
//                    pushData.setRegId(cmdArg1);
//                    reason = "RegisterResult SUCCESS mRegId=" + cmdArg1;
//                } else {
//                    reason = "RegisterResult Failed " + message.getReason();
//                }
//                break;
//            default:
//                pushData.setResultType(PushConstants.HandlerWhat.WHAT_PUSH_OTHER);
//                reason = message.getReason();
//                break;
//        }
//        pushData.setReason(reason);
//        sendPushDataToService(context, pushData);
        LogUtils.i(TAG, "onReceiveRegisterResult is called.111: reason==" + reason + "==message.toString==" + message.toString());
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        super.onCommandResult(context, message);
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String reason;
        final PushDataBean pushData = new PushDataBean(command, message.getResultCode());
        int resultType;
        switch (command) {
            case MiPushClient.COMMAND_REGISTER:
                resultType = PushConstants.HandlerWhat.WHAT_PUSH_REGISTER;
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    pushData.setRegId(cmdArg1);
                    reason = "RegisterResult SUCCESS mRegId=" + cmdArg1;
                } else {
                    reason = "RegisterResult Failed " + message.getReason();
                }
                break;
            case MiPushClient.COMMAND_SET_ALIAS:
                //设置别名结果
                resultType = PushConstants.HandlerWhat.WHAT_PUSH_ALIAS;
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    pushData.setAlias(cmdArg1);
                    reason = "Set alias success. mAlias=" + cmdArg1;
                } else {
                    reason = "Set alias fail for " + message.getReason();
                }
                break;
            case MiPushClient.COMMAND_UNSET_ALIAS:
                resultType = PushConstants.HandlerWhat.WHAT_PUSH_UNALIAS;
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    pushData.setAlias(cmdArg1);
                    reason = " Unset alias success. mAlias=" + cmdArg1;
                } else {
                    reason = "Unset alias fail for " + message.getReason();
                }
                break;
            case MiPushClient.COMMAND_SET_ACCOUNT:
                resultType = PushConstants.HandlerWhat.WHAT_PUSH_ACCOUNT;
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    pushData.setAccount(cmdArg1);
                    reason = "Set account success. mAccount=" + cmdArg1;
                } else {
                    reason = "Set account fail for " + message.getReason();
                }
                break;
            case MiPushClient.COMMAND_UNSET_ACCOUNT:
                resultType = PushConstants.HandlerWhat.WHAT_PUSH_UNACCOUNT;
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    pushData.setAccount(cmdArg1);
                    reason = " Unset account success. mAccount=" + cmdArg1;
                } else {
                    reason = "Unset account fail for " + message.getReason();
                }
                break;
            case MiPushClient.COMMAND_SUBSCRIBE_TOPIC:
                resultType = PushConstants.HandlerWhat.WHAT_PUSH_TOPIC;
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    pushData.setTopic(cmdArg1);
                    reason = "Set topic success. mTopic=" + cmdArg1;
                } else {
                    reason = "Set topic fail for " + message.getReason();
                }
                break;
            case MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC:
                resultType = PushConstants.HandlerWhat.WHAT_PUSH_UNTOPIC;
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    pushData.setTopic(cmdArg1);
                    reason = "Unset topic success. mTopic=" + cmdArg1;
                } else {
                    reason = "Unset topic fail for " + message.getReason();
                }
                break;
            case MiPushClient.COMMAND_SET_ACCEPT_TIME:
                resultType = PushConstants.HandlerWhat.WHAT_PUSH_ACCEPT_TIME;
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    pushData.setStartTime(cmdArg1);
                    pushData.setEndTime(cmdArg2);
                    reason = "Unset accept time success. mStartTime=" + cmdArg1 + "==mEndTime==" + cmdArg2;
                } else {
                    reason = "Unset accept time fail for " + message.getReason();
                }
                break;
            default:
                resultType = PushConstants.HandlerWhat.WHAT_PUSH_OTHER;
                reason = message.getReason();
                break;
        }
        pushData.setResultType(resultType);
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
        LogUtils.i(TAG, "onCommandResult is called. 111: log==" + reason + "==message.toString==" + message.toString());
    }

    @Override
    public void onRequirePermissions(Context context, String[] permissions) {
        super.onRequirePermissions(context, permissions);
        LogUtils.e(TAG, "onRequirePermissions is called.111: need permission" + arrayToString(permissions));
    }

    public String arrayToString(String[] strings) {
        String result = " ";
        for (String str : strings) {
            result = result + str + " ";
        }
        return result;
    }

    private long getStringToLong(String m_ts) {
        if (TextUtils.isEmpty(m_ts)) {
            return 0;
        }
        try {
            return Long.parseLong(m_ts);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
