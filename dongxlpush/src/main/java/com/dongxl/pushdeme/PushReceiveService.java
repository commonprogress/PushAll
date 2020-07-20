package com.dongxl.pushdeme;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import androidx.lifecycle.LifecycleService;

import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

public abstract class PushReceiveService extends LifecycleService {
    private final static String TAG = PushReceiveService.class.getSimpleName();
    private final static long delayMillis = 20 * 1000; //秒
    private Handler mHandler = new Handler();

    public PushReceiveService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "====onCreate:333===");
    }

    @Override
    public void onDestroy() {
        removeAllHandler();
        super.onDestroy();
        LogUtils.i(TAG, "====onDestroy:333===");
    }

    /**
     * 通知消息延长销毁
     */
    public void sendHandlerStopSelf(long millis) {
        removeAllHandler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, millis);
    }

    /**
     * 移除所有的消息
     */
    public void removeAllHandler() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        PushDataBean pushData = null == intent ? null : (PushDataBean) intent.getSerializableExtra(PushConstants.KEY_PUSH_DATA);
        if (null == pushData) {
            stopSelf(startId);
            return Service.START_STICKY_COMPATIBILITY;
        } else {
            pushResultOperation(pushData);
            return Service.START_REDELIVER_INTENT;
        }
    }

    private void pushResultOperation(PushDataBean pushData) {
        String platform = pushData.getPlatform();
        int resultType = pushData.getResultType();
        LogUtils.i(TAG, "pushResultOperation 444:  resultType " + resultType + ", pushData: " + pushData.toString() + ", platform: " + platform);
        switch (resultType) {
            case PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE:
                throughMessageReceived(pushData.getThroughMessage(), platform);
                break;
            case PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_ARRIVE:
                onReceiveNotifiMessage(pushData.getThroughMessage(), platform);
                break;
            case PushConstants.HandlerWhat.WHAT_PUSH_REGISTER:
                if (TextUtils.isEmpty(pushData.getRegId())) {
                    LogUtils.i(TAG, "pushResultOperation 555: regId is empty: " + pushData.getRegId());
                    stopSelf();
                } else {
                    onPushNewToken(pushData.getRegId(), platform);
                    sendHandlerStopSelf(delayMillis / 3);
                }
                break;
            default:
                LogUtils.i(TAG, "pushResultOperation WHAT_PUSH_OTHER 555 platform: " + platform + " Command:  " + pushData.getCommand() + " ResultCode:  " + pushData.getResultCode() + " Reason:  " + pushData.getReason());
                stopSelf();
                break;
        }
    }

    /**
     * 透传消息的处理
     *
     * @param messageData
     * @param platform
     */
    private void throughMessageReceived(MessageDataBean messageData, String platform) {
        if (null == messageData) {
            LogUtils.i(TAG, "throughMessageReceived 555 messageData isEmpty true");
            stopSelf();
        } else {
            LogUtils.i(TAG, "throughMessageReceived 555 messageData:" + messageData.toString());
            onReceiveThroughMessage(messageData, platform);
            sendHandlerStopSelf(delayMillis);
        }
    }

    /**
     * 获取新的token new token
     *
     * @param platform
     * @param regId
     */
    protected abstract void onPushNewToken(String regId, String platform);

    /**
     * 接收到通知消息 暂时不支持
     *
     * @param throughMessage
     * @param platform
     */
    protected abstract void onReceiveNotifiMessage(MessageDataBean throughMessage, String platform);

    /**
     * 接收到透传消息的 小米 华为 支持
     *
     * @param throughMessage
     * @param platform
     */
    protected abstract void onReceiveThroughMessage(MessageDataBean throughMessage, String platform);

    /**
     * 清空通知消息
     *
     * @param messageData
     * @param platform
     */
    protected void clearNotifiOfArrived(MessageDataBean messageData, String platform) {
        int notifyId = null == messageData ? 0 : messageData.getNotifyId();
        LogUtils.i(TAG, "clearNotifiOfArrived 555: notifyId : " + notifyId);
        PushRegisterSet.clearPushNotification(this, platform, notifyId);
    }
}
