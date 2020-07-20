package com.dongxl.pushdeme.meizu;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.R;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.oppo.OppoPushCallback;
import com.dongxl.pushdeme.utils.LogUtils;
import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.handler.MzPushMessage;
import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;

import java.util.Arrays;
import java.util.Map;

public class MeizuPushReceiver extends MzPushMessageReceiver {
    private static final String TAG = MeizuPushReceiver.class.getSimpleName();

    /**
     * 发送消息到接收服务
     *
     * @param context
     * @param pushData
     */
    private void sendPushDataToService(final Context context, final PushDataBean pushData) {
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_FLYME);
    }

    /**
     * // 调用旧版的订阅 PushManager.register(context) 方法后，
     * // 会在此回调订阅状态(已废弃)
     *
     * @param context
     * @param s
     */
    @Override
    @Deprecated
    public void onRegister(Context context, String s) {
        LogUtils.i(TAG, "meizu onRegister is called. 111:" + s);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER);
        String reason;
        if (!TextUtils.isEmpty(s)) {
            pushData.setRegId(s);
            reason = "meizu Register Result SUCCESS mRegId=" + s;
        } else {
            reason = "meizu Register Result Failed";
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);

    }

    /**
     * // 调用旧版的反订阅 PushManager.unRegister(context) 方法后，
     * // 会在此回调反订阅状态(已废弃)
     *
     * @param context
     * @param b
     */
    @Override
    @Deprecated
    public void onUnRegister(Context context, boolean b) {
        LogUtils.i(TAG, "meizu onUnRegister is called. 111:" + b);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER);
        String reason;
        if (b) {
            reason = "meizu UnRegister Result SUCCESS ";
        } else {
            reason = "meizu UnRegister Result Failed";
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * // 调用新版的订阅 PushManager.register(context,appId,appKey) 方法后，
     * // 会在此回调订阅状态
     *
     * @param context
     * @param registerStatus
     */
    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        LogUtils.i(TAG, "meizu onRegisterStatus is called. 111:" + registerStatus.toString());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER);
        String pushId = null == registerStatus ? "" : registerStatus.getPushId();
        String reason;
        if (!TextUtils.isEmpty(pushId)) {
            pushData.setRegId(pushId);
            reason = "meizu RegisterResult SUCCESS mRegId=" + pushId + "=expireTime=" + registerStatus.getExpireTime();
        } else {
            reason = "meizu RegisterResult Failed ";
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * // 调用新版的反订阅 PushManager.unRegister(context,appId,appKey) 方法后，
     * // 会在此回调订阅状态
     *
     * @param context
     * @param unRegisterStatus
     */
    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {
        LogUtils.i(TAG, "meizu onUnRegisterStatus is called. 111:" + unRegisterStatus.toString());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER);
        boolean isUnRegisterSuccess = null == unRegisterStatus ? false : unRegisterStatus.isUnRegisterSuccess();
        String reason;
        if (isUnRegisterSuccess) {
            reason = "meizu UnRegisterResult SUCCESS";
        } else {
            reason = "meizu UnRegisterResult Failed ";
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * // 调用 PushManager.switchPush/checkPush 方法后，
     * // 会在此回调通知栏和透传消息的开关状态
     *
     * @param context
     * @param pushSwitchStatus
     */
    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {
        LogUtils.i(TAG, "meizu onPushStatus is called. 111:" + pushSwitchStatus.toString());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER);
        String reason = "meizu onPushStatus Result SUCCESS status=" + pushSwitchStatus.toString();
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * 调用 PushManager.subScribeTags/unSubScribeTags/unSubScribeAllTags
     * checkSubScribeTags 方法后，会在此回调标签相关信息
     *
     * @param context
     * @param subTagsStatus
     */
    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {
        LogUtils.i(TAG, "meizu onSubTagsStatus is called. 111:" + subTagsStatus.toString());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_TOPIC);//无法区分 设置 取消设置 还是check
        String reason;
        if (null != subTagsStatus) {
            pushData.setTopic(Arrays.toString(subTagsStatus.getTagList().toArray()));
            pushData.setRegId(subTagsStatus.getPushId());
            reason = "meizu SubTagsStatus Result SUCCESS msg=" + Arrays.toString(subTagsStatus.getTagList().toArray());
        } else {
            reason = "meizu SubTagsStatus Result Failed=";
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
    }

    /**
     * // 调用 PushManager.subScribeAlias/unSubScribeAlias/checkSubScribeAlias
     * // /checkSubScribeTags 方法后，会在此回调别名相关信息
     *
     * @param context
     * @param subAliasStatus
     */
    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {
        LogUtils.i(TAG, "meizu onSubAliasStatus is called. 111:" + subAliasStatus.toString());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_ALIAS);//无法区分 设置 取消设置 还是check
        String reason;
        if (null != subAliasStatus) {
            pushData.setAlias(subAliasStatus.getAlias());
            pushData.setRegId(subAliasStatus.getPushId());
            reason = "meizu SubAliasStatus Result SUCCESS msg=" + subAliasStatus.getAlias();
        } else {
            reason = "meizu SubAliasStatus Result Failed ";
        }
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);


    }

    /**
     * 在目前较新的 Flyme 系统已经不再需要专门进行状态栏图标的设置，此方法是作用于兼容旧版本 Flyme 系统中设置
     * 消息弹出后状态栏中的小图标
     *
     * @param pushNotificationBuilder
     */
    @Override
    public void onUpdateNotificationBuilder(PushNotificationBuilder pushNotificationBuilder) {
        LogUtils.i(TAG, "meizu onUpdateNotificationBuilder is called. 111:" + pushNotificationBuilder);
//        pushNotificationBuilder.setmStatusbarIcon(R.drawable.mz_push_notification_small_icon);
        //注意:请在相应的 drawable 不同分辨率文件夹下放置一张名称务必为 mz_push_notification_small_icon 的图片
    }

    /**
     * 三个透传功能的回调方法 已经弃用
     *
     * @param context
     * @param intent  Intent 参数的方法，是在 Flyme3 系统中处理透传消息使用
     */
    @Override
    @Deprecated
    public void onMessage(Context context, Intent intent) {
        String content = intent.getExtras().toString();
        LogUtils.i(TAG, "meizu onMessage is called. 111 content:" + content);
    }

    /**
     * 带一个 String 方法 和 第三个带两个 String 方法会同时回调透传消息，带三个参数的方法中额外增加一个平
     * 台参数，格式如:{"task_id":"1232"}
     *
     * @param context
     * @param message
     */
    @Override
    @Deprecated
    public void onMessage(Context context, String message) {
        LogUtils.i(TAG, "meizu onMessage is called. 111:message:" + message);
    }

    /**
     * 目前如果要处理透传消息的回调，只使用 onMessage(Context context,String message,String platformExtra)
     * 方法即可
     *
     * @param context
     * @param message
     * @param platformExtra
     */
    @Override
    @Deprecated
    public void onMessage(Context context, String message, String platformExtra) {
        LogUtils.i(TAG, "meizu onMessage is called. 111 message:" + message + "=platformExtra==" + platformExtra);
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(message);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }

    /**
     * 通知栏消息点击回调方法
     *
     * @param context
     * @param mzPushMessage
     */
    @Override
    public void onNotificationClicked(Context context, MzPushMessage mzPushMessage) {
        LogUtils.i(TAG, "meizu onNotificationClicked is called. 111:" + mzPushMessage.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(mzPushMessage.getSelfDefineContentString());
        messageData.setNotifyId(mzPushMessage.getNotifyId());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_CLICK);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }

    @Override
    public void onNotificationDeleted(Context context, MzPushMessage mzPushMessage) {
        LogUtils.i(TAG, "meizu onNotificationDeleted is called. 111:" + mzPushMessage.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(mzPushMessage.getSelfDefineContentString());
        messageData.setNotifyId(mzPushMessage.getNotifyId());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }


    @Override
    public void onNotifyMessageArrived(Context context, String message) {
        LogUtils.i(TAG, "meizu onNotifyMessageArrived is called. 111 message:" + message);
    }

    /**
     * 通知栏消息展示回调方法 新版本
     *
     * @param context
     * @param mzPushMessage
     */
    @Override
    public void onNotificationArrived(Context context, MzPushMessage mzPushMessage) {
        LogUtils.i(TAG, "meizu onNotificationArrived is called. 111:" + mzPushMessage.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(mzPushMessage.getSelfDefineContentString());
        messageData.setNotifyId(mzPushMessage.getNotifyId());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_ARRIVE);
        pushData.setThroughMessage(messageData);
        sendPushDataToService(context, pushData);
    }
}
