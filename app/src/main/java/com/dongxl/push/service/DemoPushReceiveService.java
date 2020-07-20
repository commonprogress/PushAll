package com.dongxl.push.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dongxl.pushdeme.PushReceiveService;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

public class DemoPushReceiveService extends PushReceiveService {
    private final static String TAG = DemoPushReceiveService.class.getSimpleName();
    private final static long delayMillis = 20 * 1000; //秒
    private Handler mHandler = new Handler();

    public DemoPushReceiveService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "====onCreate:333===");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "====onDestroy:333===");
    }

    /**
     * 获取新的token new token
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
     * @param throughMessage
     * @param platform
     */
    @Override
    protected void onReceiveNotifiMessage(MessageDataBean throughMessage, String platform) {

    }

    /**
     * 接收到透传消息的 小米 华为 支持
     *
     * @param throughMessage
     * @param platform
     */
    @Override
    protected void onReceiveThroughMessage(MessageDataBean throughMessage, String platform) {

    }


}
