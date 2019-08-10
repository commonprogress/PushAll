package com.dongxl.pushdeme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

public class ServiceManager {
    private final static String TAG = ServiceManager.class.getSimpleName();

    /**
     * 发送消息到接收服务
     *
     * @param context
     * @param pushData
     */
    public static void sendPushDataToService(final Context context, final PushDataBean pushData, final String platform) {
        if (null != context && null != pushData) {
            pushData.setPlatform(platform);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(PushConstants.KEY_PUSH_DATA, pushData);
//            intent.setAction(PushConstants.ACTION_PUSHRECEIVESERVICE);
//            intent.setPackage(context.getPackageName());
            ComponentName componentName = new ComponentName(context.getPackageName(), "com.dongxl.pushdeme.PushReceiveService");
            intent.setComponent(componentName);
            context.startService(intent);
            LogUtils.i(TAG, "sendPushDataToService is called. 222 resultType=" + pushData.getResultType());
        } else {
            LogUtils.e(TAG, "sendPushDataToService is other. 222  null == pushData");
        }
    }
}
