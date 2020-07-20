package com.dongxl.pushdeme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

public abstract class PushMessageService extends JobIntentService {
    private final static String TAG = PushMessageReceiver.class.getSimpleName();
    private final static long delayMillis = 20 * 1000; //秒
    /**
     * 这个Service 唯一的id
     */
    static final int JOB_ID = 10111;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, ComponentName componentName, Intent intent) {
        enqueueWork(context, componentName, JOB_ID, intent);
    }

    private Handler mHandler;

    private Handler getJobHandler() {
        if (null != mHandler) {
            return mHandler;
        }
        Looper myLooper = Looper.myLooper();
        if (null != myLooper) {
            mHandler = new Handler();
        } else {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        LogUtils.i(TAG, "==onHandleWork==");
        PushDataBean pushData = null == intent ? null : (PushDataBean) intent.getSerializableExtra(PushConstants.KEY_PUSH_DATA);
        if (null == pushData) {
            stopCurrentWork();
        } else {
            pushResultOperation(pushData);
        }
    }

    private void pushResultOperation(PushDataBean pushData) {
        String platform = pushData.getPlatform();
        int resultType = pushData.getResultType();
        LogUtils.i(TAG, "pushResultOperation 444:  resultType " + resultType + ", pushData: " + pushData.toString() + ", platform: " + platform);

        switch (resultType) {
            //透传消息
            case PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE:
                throughMessageReceived(pushData.getThroughMessage(), platform);
                break;
            //收到通知回调
            case PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_ARRIVE:
                onReceiveNotifiMessage(pushData.getThroughMessage(), platform);
                sendHandlerStopSelf(delayMillis);
                break;
            //获取token
            case PushConstants.HandlerWhat.WHAT_PUSH_REGISTER:
                onPushNewToken(pushData.getRegId(), platform);
                sendHandlerStopSelf(delayMillis / 3);
                break;
            default:
                LogUtils.i(TAG, "pushResultOperation WHAT_PUSH_OTHER 555 platform: " + platform + " Command:  " + pushData.getCommand() + " ResultCode:  " + pushData.getResultCode() + " Reason:  " + pushData.getReason());
                onStopCurrentWork();
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
            onStopCurrentWork();
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

    @Override
    public void onDestroy() {
        removeAllHandler();
        super.onDestroy();
        LogUtils.i(TAG, "====onDestroy===");
    }

    /**
     * 通知消息延长销毁
     */
    public void sendHandlerStopSelf(long millis) {
        removeAllHandler();
        getJobHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopCurrentWork();
            }
        }, millis);
    }

    /**
     * 移除所有的消息
     */
    public void removeAllHandler() {
        getJobHandler().removeCallbacksAndMessages(null);
    }

    private void stopCurrentWork() {
        if (!isStopped()) {
            onStopCurrentWork();
        }
    }
}
