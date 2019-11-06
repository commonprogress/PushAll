package com.dongxl.pushdeme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

public class ServiceManager {
    private final static String TAG = ServiceManager.class.getSimpleName();
    private final static String MAIN_PKG = BuildConfig.MAIN_PACKAGENAME;
    /**
     * 发送消息到接收服务
     *
     * @param context
     * @param pushData
     */
    public static void sendPushDataToService(final Context context, final PushDataBean pushData, final String platform) {
        String pkgName = context.getPackageName();
        if (TextUtils.isEmpty(pkgName) || !pkgName.startsWith(MAIN_PKG)) {
            pkgName = MAIN_PKG;
        }
        if (null != context && null != pushData) {
            pushData.setPlatform(platform);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(PushConstants.KEY_PUSH_DATA, pushData);
            ComponentName componentName = new ComponentName(pkgName, BuildConfig.PUSHRECEIVESERVICE);
            intent.setComponent(componentName);

            //方法一
            context.startService(intent);
//            ContextCompat.startForegroundService(context, intent);

            //方法二
//            PushMessageService.enqueueWork(context, componentName, intent);

            LogUtils.i(TAG, "sendPushDataToService is called. 222 resultType=" + pushData.getResultType() + " ,pkgName=" + pkgName);
        } else {
            LogUtils.e(TAG, "sendPushDataToService is other. 222  null == pushData,pkgName=" + pkgName);
        }
    }
}
