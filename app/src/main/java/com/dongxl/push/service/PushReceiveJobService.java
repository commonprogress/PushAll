package com.dongxl.push.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongxl.pushdeme.PushMessageService;
import com.dongxl.pushdeme.bean.MessageDataBean;
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
     *
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
     * 集成JobIntentService 必须步骤四，重写onStartCommand方法时要返回super.onStartCommand() 或者不重写
     *
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

    /**
     * 保存regid 相关操作
     *
     * @param platform
     * @param regId
     */
    @Override
    protected void onPushNewToken(String regId, String platform) {

    }

    /**
     * 接收到通知消息 暂时不支持
     *
     * @param messageData
     * @param platform
     */
    @Override
    protected void onReceiveNotifiMessage(MessageDataBean messageData, String platform) {
        onReceiveThroughMessage(messageData, platform);
        clearNotifiOfArrived(messageData, platform);
    }

    /**
     * 透传消息的处理
     *
     * @param messageData
     * @param platform
     */
    @Override
    protected void onReceiveThroughMessage(MessageDataBean messageData, String platform) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "====onDestroy===");
    }

}
