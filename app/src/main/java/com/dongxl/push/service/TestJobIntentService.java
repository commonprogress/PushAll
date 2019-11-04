/**
 * Wire
 * Copyright (C) 2019 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dongxl.push.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import android.text.TextUtils;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

public class TestJobIntentService extends JobIntentService {
    private static final String TAG = TestJobIntentService.class.getSimpleName();

    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, TestJobIntentService.class, JOB_ID, intent);
    }

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, ComponentName component, Intent intent) {
        enqueueWork(context, TestJobIntentService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "==onCreate==");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        LogUtils.i(TAG, "==onHandleWork==");
        PushDataBean pushData = null == intent ? null : (PushDataBean) intent.getSerializableExtra(PushConstants.KEY_PUSH_DATA);
        if (null != pushData) {
            pushResultOperation(pushData);
        } else {
            LogUtils.i(TAG, "==onHandleWork==pushData==null");
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
//                    stopSelf();
                } else {
                    thirdPushRegister(platform, pushData.getRegId());
//                    sendHandlerStopSelf(delayMillis / 3);
                }
                break;
            default:
                LogUtils.i(TAG, "pushResultOperation WHAT_PUSH_OTHER 555 platform: " + platform + " Command:  " + pushData.getCommand() + " ResultCode:  " + pushData.getResultCode() + " Reason:  " + pushData.getReason());
                stopSelf();
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
//            stopSelf();
        } else {
            LogUtils.i(TAG, "throughMessageReceived 555 messageData:" + messageData.toString());
//            sendHandlerStopSelf(delayMillis);
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
//        PushRegisterSet.clearPushNotification(this, platform, notifyId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "==onDestroy==");
    }
}
