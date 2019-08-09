package com.dongxl.pushdeme.oppo;

import android.content.Context;

import com.coloros.mcssdk.callback.PushAdapter;
import com.coloros.mcssdk.mode.NotificatoinStatus;
import com.coloros.mcssdk.mode.PushStatus;
import com.coloros.mcssdk.mode.SubscribeResult;
import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.PushDataBean;

import java.util.Arrays;
import java.util.List;

/**
 * oppo 操作相关的回调
 */
public class OppoPushCallback extends PushAdapter {
    private Context mContext;

    public OppoPushCallback() {
        super();
    }

    public OppoPushCallback(Context context) {
        this();
        this.mContext = context;
    }

    public void setCurrentContext(Context context) {
        this.mContext = context;
    }

    /**
     * 发送消息到接收服务
     *
     * @param pushData
     */
    private void sendPushDataToService(final PushDataBean pushData) {
        if (null != mContext) {
            ServiceManager.sendPushDataToService(mContext, pushData, PushConstants.PushPlatform.PLATFORM_OPPO);
        }
    }

    /**
     * @param code
     * @param msg  成功为registerId 失败为msg
     */
    @Override
    public void onRegister(int code, String msg) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setRegId(msg);
            reason = "OPPO RegisterResult SUCCESS mRegId=" + msg;
        } else {
            reason = "OPPO RegisterResult Failed code=" + code + "=msg=" + msg;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    /**
     * 注销
     *
     * @param code
     */
    @Override
    public void onUnRegister(int code) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            reason = "OPPO UnRegisterResult SUCCESS";
        } else {
            reason = "OPPO UnRegisterResult Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    /**
     * 获取别名
     *
     * @param code
     * @param list
     */
    @Override
    public void onGetAliases(int code, List<SubscribeResult> list) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setAlias(Arrays.toString(list.toArray()));
            reason = "OPPO GetAliases Result SUCCESS msg=" + Arrays.toString(list.toArray());
        } else {
            reason = "OPPO GetAliases Result Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    @Override
    public void onSetAliases(int code, List<SubscribeResult> list) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_ALIAS, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setAlias(Arrays.toString(list.toArray()));
            reason = "OPPO SetAliases Result SUCCESS msg=" + Arrays.toString(list.toArray());
        } else {
            reason = "OPPO SetAliases Result Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    @Override
    public void onUnsetAliases(int code, List<SubscribeResult> list) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_UNALIAS, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setAlias(Arrays.toString(list.toArray()));
            reason = "OPPO UnsetAliases Result SUCCESS msg=" + Arrays.toString(list.toArray());
        } else {
            reason = "OPPO UnsetAliases Result Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    /**
     * 设置用户名
     * @param code
     * @param list
     */
    @Override
    public void onSetUserAccounts(int code, List<SubscribeResult> list) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_ACCOUNT, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setAccount(Arrays.toString(list.toArray()));
            reason = "OPPO SetUserAccounts Result SUCCESS msg=" + Arrays.toString(list.toArray());
        } else {
            reason = "OPPO SetUserAccounts Result Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    @Override
    public void onUnsetUserAccounts(int code, List<SubscribeResult> list) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_UNACCOUNT, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setAccount(Arrays.toString(list.toArray()));
            reason = "OPPO UnsetUserAccounts Result SUCCESS msg=" + Arrays.toString(list.toArray());
        } else {
            reason = "OPPO UnsetUserAccounts Result Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    @Override
    public void onGetUserAccounts(int code, List<SubscribeResult> list) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setAccount(Arrays.toString(list.toArray()));
            reason = "OPPO GetUserAccounts Result SUCCESS msg=" + Arrays.toString(list.toArray());
        } else {
            reason = "OPPO GetUserAccounts Result Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    /**
     * 设置topic
     * @param code
     * @param list
     */
    @Override
    public void onSetTags(int code, List<SubscribeResult> list) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_TOPIC, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setTopic(Arrays.toString(list.toArray()));
            reason = "OPPO SetTags Result SUCCESS msg=" + Arrays.toString(list.toArray());
        } else {
            reason = "OPPO SetTags Result Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    @Override
    public void onUnsetTags(int code, List<SubscribeResult> list) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_UNTOPIC, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setTopic(Arrays.toString(list.toArray()));
            reason = "OPPO UnsetTags Result SUCCESS msg=" + Arrays.toString(list.toArray());
        } else {
            reason = "OPPO UnsetTags Result Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    @Override
    public void onGetTags(int code, List<SubscribeResult> list) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            pushData.setTopic(Arrays.toString(list.toArray()));
            reason = "OPPO GetTags Result SUCCESS msg=" + Arrays.toString(list.toArray());
        } else {
            reason = "OPPO GetTags Result Failed code=" + code;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    /**
     * push 时间
     * @param code
     * @param status
     */
    @Override
    public void onGetPushStatus(int code, int status) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
//            PushStatus.PUSH_STATUS_PAUSE = status
            reason = "OPPO GetPushStatus Result SUCCESS status=" + status;
        } else {
            reason = "OPPO GetPushStatus Result Failed code=" + code + "==status==" + status;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    /**
     * 通知状态
     * @param code
     * @param status
     */
    @Override
    public void onGetNotificationStatus(int code, int status) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_OTHER, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
//            NotificatoinStatus.STATUS_CLOSE = status
            reason = "OPPO GetNotificationStatus Result SUCCESS status=" + status;
        } else {
            reason = "OPPO GetNotificationStatus Result Failed code=" + code + "==status==" + status;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    /**
     * push时间
     * @param code
     * @param result
     */
    @Override
    public void onSetPushTime(int code, String result) {
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_ACCEPT_TIME, code);
        String reason;
        if (code == OppoResultCode.OPPO_SUCCESS) {
            reason = "OPPO SetPushTime Result SUCCESS msg=" + result;
        } else {
            reason = "OPPO SetPushTime Result Failed code=" + code + "==result==" + result;
        }
        pushData.setReason(reason);
        sendPushDataToService(pushData);
    }

    public static final class OppoResultCode {
        /**
         * Oppo 成功 | success
         */
        public static final int OPPO_SUCCESS = 0;
    }
}
