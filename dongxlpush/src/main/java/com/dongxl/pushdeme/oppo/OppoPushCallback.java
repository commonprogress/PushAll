package com.dongxl.pushdeme.oppo;

import android.content.Context;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;
import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;

/**
 * oppo 操作相关的回调
 */
public class OppoPushCallback implements ICallBackResultService {
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
        if (null != pushData) {
            LogUtils.i("OppoPushCallback", "OppoPushCallback Result is called. 111: ==pushData" + pushData.toString());
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
            HeytapPushManager.setRegisterID(msg);
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
     * Push状态正常
     *
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
     *
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
     *
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
