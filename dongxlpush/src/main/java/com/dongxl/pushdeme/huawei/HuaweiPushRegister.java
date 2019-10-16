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
package com.dongxl.pushdeme.huawei;

import android.content.Context;
import android.text.TextUtils;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.aaid.entity.AAIDResult;
import com.huawei.hms.push.HmsMessaging;

/**
 * 关于主题，请注意以下事项：
 * ● 主题消息传递最适合传递天气或其他可通过公开途径获得的信息。
 * ● 如果您需要向每个用户的多台设备发送消息，请考虑为这些用例使用设备组消息传递。
 * ● 主题消息传递不限制每个主题的订阅数。但是，HUAWEI Push在这些方面有如下约束限制：
 * 1、一个应用实例不可订阅超过2000个主题。
 * 2、订阅频率的速率受限。如果您在短时间内发送过多订阅请求，push订阅服务器将返回错误码提示请求过于频繁。
 * 3、该功能仅在EMUI版本不低于10.0的华为设备上支持。
 * 4、华为移动服务（APK）的版本不低于3.0.0。
 */
public class HuaweiPushRegister {
    public static final String TAG = HuaweiPushRegister.class.getSimpleName();

    /**
     * 发送消息到接收服务
     *
     * @param pushData
     */
    private static void sendPushDataToService(final Context context, final PushDataBean pushData) {
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_HUAWEI);
    }

    /**
     * @param context
     * @param isGet   get AAID / delete AAID
     */
    public void huaweiGetAndDeleteAAID(final Context context, boolean isGet) {
        LogUtils.i(TAG, "huaweiGetAAID isGet:" + isGet);
        if (isGet) {
            Task<AAIDResult> idResult = HmsInstanceId.getInstance(context).getAAID();
            idResult.addOnSuccessListener(new OnSuccessListener<AAIDResult>() {
                @Override
                public void onSuccess(AAIDResult aaidResult) {
                    String aaId = aaidResult.getId();
                    LogUtils.i(TAG, "huaweiGetAAID getAAID aaId " + aaId);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    LogUtils.i(TAG, "huaweiGetAAID getAAID failed " + e.getMessage());
                }
            });
        } else {
            new Thread() {
                @Override
                public void run() {
                    try {
                        HmsInstanceId.getInstance(context).deleteAAID();
                        LogUtils.i(TAG, "huaweiGetAAID deleteAAID and token success");
                    } catch (Exception e) {
                        LogUtils.i(TAG, "huaweiGetAAID deleteAAID failed");
                    }

                }
            }.start();
        }
    }

    /**
     * 获取token
     * <p>
     * Token发生变化时或者EMUI版本低于10.0以onNewToken方法返回。
     * EMUI10.0及以上版本的华为设备上，getToken接口直接返回token。如果当次调用失败PUSH会自动重试申请，成功后则以onNewToken接口返回。
     * 3、低于EMUI10.0的设备上，getToken接口返回为空，结果以onNewToken接口返回。
     * 4、缓存重试的申请token以及服务端识别token过期后刷新token，以onNewToken接口返回。
     * 5、华为移动服务（APK）的版本不低于3.0.0。
     *
     * @param context
     */
    public static void getHuaweiPushToken(final Context context) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String token = HmsInstanceId.getInstance(context).getToken(PushConstants.HUAWEI_APP_ID, "HCM");
                    LogUtils.d(TAG, "getToken:" + token);
                    if (!TextUtils.isEmpty(token)) {
                        sendRegistrationToServer(context, token);
                    }
                } catch (Exception e) {
                    LogUtils.i(TAG, "getToken faile." + e);
                }
            }
        }.start();
    }

    private static void sendRegistrationToServer(Context context, String token) {
        if (TextUtils.isEmpty(token)) {
            return;
        }
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER);
        pushData.setRegId(token);
        String reason = "sendRegistrationToServer Token is:" + token;
        pushData.setReason(reason);
        sendPushDataToService(context, pushData);
        HMSSharedUtils.saveHuaweiToken(context, token);
        LogUtils.i(TAG, "sendRegistrationToServer is called. 111: log==" + reason);
    }

    /**
     * 调用注销Token接口。
     *
     * @param context
     */
    public static void deleteHuaweiPushToken(final Context context) {
        new Thread() {
            @Override
            public void run() {
                try {
                    HmsInstanceId.getInstance(context).deleteToken(PushConstants.HUAWEI_APP_ID, "HCM");
                    HMSSharedUtils.saveHuaweiToken(context, "");
                } catch (Exception e) {
                    LogUtils.e(TAG, "deleteToken failed." + e);
                }
            }
        }.start();
    }

    /**
     * 订阅topic
     *
     * @param context
     * @param topic
     */
    public static void subscribe(final Context context, final String topic) {
        try {
            HmsMessaging.getInstance(context).subscribe(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    String reason;
                    int code;
                    if (task.isSuccessful()) {
                        code = HuaweiResultCode.HUAWEI_SUCCESS;
                        reason = "Huawei subscribe Result SUCCESS msg=" + topic;
                    } else {
                        code = HuaweiResultCode.HUAWEI_FAIL;
                        reason = "Huawei subscribe Result Failed msg=" + task.getException().getMessage();
                    }

                    final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_TOPIC, code);
                    if (code == HuaweiResultCode.HUAWEI_SUCCESS) {
                        pushData.setTopic(topic);
                    } else {
                    }
                    pushData.setReason(reason);
                    sendPushDataToService(context, pushData);
                    LogUtils.e(TAG, "subscribe reason=" + reason);
                }
            });
        } catch (Exception e) {
            LogUtils.e(TAG, "subscribe failed: exception=" + e.getMessage());
        }
    }

    /**
     * 取消订阅topic
     *
     * @param context
     * @param topic
     */
    public static void unsubscribe(final Context context, final String topic) {
        try {
            HmsMessaging.getInstance(context).unsubscribe(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    String reason;
                    int code;
                    if (task.isSuccessful()) {
                        code = HuaweiResultCode.HUAWEI_SUCCESS;
                        reason = "Huawei unsubscribe Result SUCCESS msg=" + topic;
                    } else {
                        code = HuaweiResultCode.HUAWEI_FAIL;
                        reason = "Huawei unsubscribe Result Failed msg=" + task.getException().getMessage();
                    }

                    final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_UNTOPIC, code);
                    if (code == HuaweiResultCode.HUAWEI_SUCCESS) {
                        pushData.setTopic(topic);
                    } else {
                    }
                    pushData.setReason(reason);
                    sendPushDataToService(context, pushData);
                    LogUtils.e(TAG, "unsubscribe reason=" + reason);
                }
            });
        } catch (Exception e) {
            LogUtils.e(TAG, "unsubscribe failed: exception=" + e.getMessage());
        }
    }


    /**
     * 控制是否允许显示通知栏消息
     * 注意：
     * 1、该功能仅在EMUI版本不低于EMUI5.1的设备上支持。
     * 2、华为移动服务（APK）的版本不低于3.0.0。
     *
     * @param context
     * @param isOn    true 打开
     */
    public static void turnOnOrOffHuaweiPush(final Context context, boolean isOn) {
        if (isOn) {
            HmsMessaging.getInstance(context).turnOnPush().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        LogUtils.i(TAG, "turnOnPush Complete");
                    } else {
                        LogUtils.e(TAG, "turnOnPush failed: ret=" + task.getException().getMessage());
                    }
                }
            });
        } else {
            HmsMessaging.getInstance(context).turnOffPush().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        LogUtils.i(TAG, "turnOffPush Complete");
                    } else {
                        LogUtils.e(TAG, "turnOffPush failed: ret=" + task.getException().getMessage());
                    }
                }
            });
        }
    }

    public static final class HuaweiResultCode {
        /**
         * huawei 成功 | success
         */
        public static final int HUAWEI_SUCCESS = 0;

        /**
         * huawei 失败 | fail
         */
        public static final int HUAWEI_FAIL = -1;
    }

}
