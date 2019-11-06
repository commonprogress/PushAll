package com.dongxl.push.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.PushMessageService;
import com.dongxl.pushdeme.PushRegisterSet;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

/**
 * https://www.jianshu.com/p/a23df3eeb245
 * https://www.jianshu.com/p/c3290ff1520a
 */
public class PushReceiveJobService extends PushMessageService {
    private static final String TAG = PushReceiveJobService.class.getSimpleName();
    private final static long delayMillis = 20 * 1000; //秒
    private static final int JOB_ID = 1000;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, PushReceiveJobService.class, JOB_ID, work);
    }

    /**
     * 集成JobIntentService 必须步骤三 大坑 重写的话要返回super.onBind(),否则onHandleWork不会回调。不重写onBind方法
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        LogUtils.i(TAG, "==onBind==");
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "==onCreate==");
    }

    /**
     *  集成JobIntentService 必须步骤四，重写onStartCommand方法时要返回super.onStartCommand() 或者不重写
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "==onStartCommand==");
        return super.onStartCommand(intent, flags, startId);
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

    private void stopCurrentWork(){
        if(!isStopped()){
            onStopCurrentWork();
        }
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
            case PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE:
                throughMessageReceived(pushData.getThroughMessage(), platform);
                break;
            case PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_ARRIVE:
                throughMessageReceived(pushData.getThroughMessage(), platform);
                clearNotifiOfArrived(pushData.getThroughMessage(), platform);
                break;
            case PushConstants.HandlerWhat.WHAT_PUSH_REGISTER:
                if (TextUtils.isEmpty(pushData.getRegId())) {
                    LogUtils.i(TAG, "pushResultOperation 555: regId is empty: " + pushData.getRegId());
                    onStopCurrentWork();
                } else {
                    thirdPushRegister(platform, pushData.getRegId());
                    sendHandlerStopSelf(delayMillis / 3);
                }
                break;
            default:
                LogUtils.i(TAG, "pushResultOperation WHAT_PUSH_OTHER 555 platform: " + platform + " Command:  " + pushData.getCommand() + " ResultCode:  " + pushData.getResultCode() + " Reason:  " + pushData.getReason());
                onStopCurrentWork();
                break;
        }
    }

    /**
     * 保存regid
     *
     * @param platform
     * @param regId
     */
    private void thirdPushRegister(String platform, String regId) {

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
            sendHandlerStopSelf(delayMillis);
        }
    }

    /**
     * 清空通知消息
     *
     * @param messageData
     * @param platform
     */
    private void clearNotifiOfArrived(MessageDataBean messageData, String platform) {
        int notifyId = null == messageData ? 0 : messageData.getNotifyId();
        LogUtils.i(TAG, "clearNotifiOfArrived 555: notifyId : " + notifyId);
        PushRegisterSet.clearPushNotification(this, platform, notifyId);
    }

}
