/**
 * Wire
 * Copyright (C) 2019 Wire Swiss GmbH
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dongxl.pushdeme;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.huawei.HMSAgent;
import com.dongxl.pushdeme.huawei.agent.common.HMSAgentLog;
import com.dongxl.pushdeme.huawei.agent.common.handler.ConnectHandler;
import com.dongxl.pushdeme.huawei.agent.push.handler.DeleteTopicHandler;
import com.dongxl.pushdeme.huawei.agent.push.handler.GetTokenHandler;
import com.dongxl.pushdeme.huawei.agent.push.handler.SetTopicHandler;
import com.dongxl.pushdeme.oppo.OppoPushCallback;
import com.dongxl.pushdeme.utils.LogUtils;
import com.dongxl.pushdeme.utils.PhoneUtils;
import com.dongxl.pushdeme.utils.RomUtil;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推送相关注册和操作
 */
public class PushRegisterSet {
    private final static String TAG = PushRegisterSet.class.getSimpleName();
    /**
     * 是否支持oppo推送
     * true 表示手机平台支持PUSH, false表示不支持
     */
    private static boolean isSupportOppoPush;
    /**
     * oppo 设置操作相关的回调
     */
    private static OppoPushCallback oppoPushCallback;

    /**
     * 是否支持vivo 推送
     */
    private static boolean isSupportVivoPush;

    /**
     * 是否支持Meizu 推送
     */
    private static boolean isSupportMeizuPush;

    /**
     * application 初始化注册
     *
     * @param application
     */
    public static void applicationInit(Application application) {
        if (!PhoneUtils.isChinaCountry(application)) {
            return;
        }
        if (RomUtil.isMiui()) {
        } else if (RomUtil.isEmui()) {
            huaweiRegisterInit(application);
        } else if (RomUtil.isVivo()) {
            isSupportVivoPush = PushClient.getInstance(application).isSupport();
        } else if (RomUtil.isOppo()) {
            oppoRegisterInit(application);
        } else if (RomUtil.isFlyme()) {
            isSupportMeizuPush = MzSystemUtils.isMeizu(application);
        } else {
            if (RomUtil.isSmartisan()) {
//                huaweiRegisterInit(application);
            }
        }
    }

    /**
     * activity初始化注册
     *
     * @param activity
     */
    public static void registerInitPush(Activity activity) {
        if (!PhoneUtils.isChinaCountry(activity)) {
            return;
        }
        if (RomUtil.isMiui()) {
            xiaomiRegisterInit(activity);
        } else if (RomUtil.isEmui()) {
            huaweiRegisterConnect(activity);
        } else if (RomUtil.isVivo()) {
            vivoRegisterInit(activity);
        } else if (RomUtil.isOppo()) {
            oppoRegisterConnect(activity);
        } else if (RomUtil.isFlyme()) {
            meizuRegisterInit(activity);
        } else {
            if (RomUtil.isSmartisan()) {
//                huaweiRegisterConnect(activity);
//                xiaomiRegisterInit(activity);
            }
        }
    }

    /**
     * 小米注册
     *
     * @param context
     */
    public static void xiaomiRegisterInit(Context context) {
        LogUtils.i(PushConstants.XIAOMI_TAG, "==xiaomiRegisterInit==");
        MiPushClient.registerPush(context, PushConstants.XIAOMI_APP_ID, PushConstants.XIAOMI_APP_KEY);
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
//                LogUtils.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                LogUtils.d(PushConstants.XIAOMI_TAG, content);
            }
        };
        //SDCard/Android/data/app pkgname/files/MiPushLog
        Logger.setLogger(context, newLogger);
        if (!BuildConfig.DEBUG) {
            //关闭日志功能
            Logger.disablePushFileLog(context);
        }
    }

    /**
     * 华为注册
     * SDK连接HMS
     *
     * @param application
     */
    private static void huaweiRegisterInit(Application application) {
        HMSAgentLog.setHMSAgentLogCallback(new HMSAgentLog.IHMSAgentLogCallback() {
            @Override
            public void logD(String tag, String log) {
                LogUtils.d(TAG, log);
            }

            @Override
            public void logV(String tag, String log) {
                LogUtils.v(TAG, log);
            }

            @Override
            public void logI(String tag, String log) {
                LogUtils.i(TAG, log);
            }

            @Override
            public void logW(String tag, String log) {
                LogUtils.e(TAG, log);
            }

            @Override
            public void logE(String tag, String log) {
                LogUtils.e(TAG, log);
            }
        });
        HMSAgent.init(application);
    }

    /**
     * 华为注册
     * SDK连接HMS
     *
     * @param activity
     */
    private static void huaweiRegisterConnect(final Activity activity) {
        final Context context = activity.getApplicationContext();
        HMSAgent.connect(activity, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                LogUtils.i(TAG, "huawei HMS connect end:" + rst);
                getRegId(context);
            }
        });
    }

    /**
     * oppo init
     *
     * @param context context必须传入当前app的application context
     */
    private static void oppoRegisterInit(Context context) {
        //判断是否手机平台是否支持PUSH
        isSupportOppoPush = com.coloros.mcssdk.PushManager.isSupportPush(context);
        if (isSupportOppoPush) {
            oppoPushCallback = new OppoPushCallback(context);
        }
    }

    /**
     * oppo注册
     */
    private static void oppoRegisterConnect(Context context) {
        if (null == oppoPushCallback) {
            oppoRegisterInit(context.getApplicationContext());
        }
        if (null != oppoPushCallback) {
            //applicatoinContext必须传入当前app的applicationcontet
//            oppoPushCallback.setCurrentContext(context.getApplicationContext());
            com.coloros.mcssdk.PushManager.getInstance().register(context.getApplicationContext(), PushConstants.OPPO_APP_KEY, PushConstants.OPPO_APP_SECRET, oppoPushCallback);//setPushCallback接口也可设置callback
//            getRegId(context);
        }
    }

    /**
     * vivo 注册和初始化
     *
     * @param context
     */
    private static void vivoRegisterInit(Context context) {
        if (isSupportVivoPush) {
            PushClient.getInstance(context.getApplicationContext()).initialize();
        }
    }

    /**
     * 魅族注册
     *
     * @param context
     */
    private static void meizuRegisterInit(Context context) {
        if (!isSupportMeizuPush) {
            return;
        }
        com.meizu.cloud.pushsdk.PushManager.register(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY);
    }

    /**
     * 获取当前设置支持的推送类型
     *
     * @param context
     * @return push platform
     */
    private String getSupportPushPlatform(Context context) {
        Context mContext = context.getApplicationContext();
        if (MzSystemUtils.isMeizu(mContext)) {
            return PushConstants.PushPlatform.PLATFORM_FLYME;
        } else if (com.coloros.mcssdk.PushManager.isSupportPush(context)) {
            return PushConstants.PushPlatform.PLATFORM_OPPO;
        } else if (PushClient.getInstance(context).isSupport()) {
            return PushConstants.PushPlatform.PLATFORM_VIVO;
        } else if (RomUtil.isEmui()) {
            return PushConstants.PushPlatform.PLATFORM_HUAWEI;
        } else if (RomUtil.isMiui()) {
            return PushConstants.PushPlatform.PLATFORM_XIAOMI;
        } else {
            if (RomUtil.isSmartisan()) {
//                return PushConstants.PushPlatform.PLATFORM_HUAWEI;
//                return PushConstants.PushPlatform.PLATFORM_XIAOMI;
            }
            return PushConstants.PushPlatform.PLATFORM_OTHER;
        }
    }

    /**
     * 获取 regId
     *
     * @return
     */
    public static String getRegId(Context context) {
        if (!PhoneUtils.isChinaCountry(context)) {
            return "";
        }
        if (RomUtil.isMiui()) {
            return MiPushClient.getRegId(context);
        } else if (RomUtil.isEmui()) {
            HMSAgent.Push.getToken(new GetTokenHandler() {
                @Override
                public void onResult(int rst) {
                    LogUtils.i(TAG, "华为 get token: end" + rst);
                }
            });
        } else if (RomUtil.isVivo()) {
            if (!isSupportVivoPush) {
                return "";
            }
            PushClient.getInstance(context.getApplicationContext()).turnOnPush(new IPushActionListener() {

                @Override
                public void onStateChanged(int state) {
                    LogUtils.i(TAG, "vivo 打开push getRegId: end state：" + state + " isSuc:" + (state == 0));
                }
            });
        } else if (RomUtil.isOppo()) {
            if (null != oppoPushCallback) {
                com.coloros.mcssdk.PushManager.getInstance().getRegister();
            }
        } else if (RomUtil.isFlyme()) {
            if (!isSupportMeizuPush) {
                return "";
            }
        } else {
            if (RomUtil.isSmartisan()) {
//                HMSAgent.Push.getToken(new GetTokenHandler() {
//                    @Override
//                    public void onResult(int rst) {
//                        LogUtils.i(TAG, "get token: end" + rst);
//                    }
//                });
//                return MiPushClient.getRegId(context);
            }
        }
        return "";
    }

    /**
     * 设置别名
     *
     * @param context
     * @param alias
     */
    public static void setAlias(Context context, String alias) {
        if (!PhoneUtils.isChinaCountry(context)) {
            return;
        }
        if (RomUtil.isMiui()) {
            MiPushClient.setAlias(context, alias, null);
        } else if (RomUtil.isEmui()) {

        } else if (RomUtil.isVivo()) {
            if (!isSupportVivoPush) {
                return;
            }
            PushClient.getInstance(context.getApplicationContext()).bindAlias(alias, new IPushActionListener() {

                @Override
                public void onStateChanged(int state) {
                    LogUtils.i(TAG, "vivo  setAlias: end state：" + state + " isSuc:" + (state == 0));
                }
            });
        } else if (RomUtil.isOppo()) {
            if (null != oppoPushCallback) {
                List<String> list = new ArrayList<>();
                list.add(alias);
                com.coloros.mcssdk.PushManager.getInstance().setAliases(list);
            }
        } else if (RomUtil.isFlyme()) {
            if (!isSupportMeizuPush) {
                return;
            }
            com.meizu.cloud.pushsdk.PushManager.subScribeAlias(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY, com.meizu.cloud.pushsdk.PushManager.getPushId(context), alias);
        } else {
            if (RomUtil.isSmartisan()) {
            }
        }
    }

    /**
     * 撤销别名
     *
     * @param context
     * @param alias
     */
    public static void unsetAlias(Context context, String alias) {
        if (!PhoneUtils.isChinaCountry(context)) {
            return;
        }
        if (RomUtil.isMiui()) {
            MiPushClient.unsetAlias(context, alias, null);
        } else if (RomUtil.isEmui()) {

        } else if (RomUtil.isVivo()) {
            if (!isSupportVivoPush) {
                return;
            }
            PushClient.getInstance(context.getApplicationContext()).unBindAlias(alias, new IPushActionListener() {

                @Override
                public void onStateChanged(int state) {
                    LogUtils.i(TAG, "vivo unsetAlias: end state：" + state + " isSuc:" + (state == 0));
                }
            });
        } else if (RomUtil.isOppo()) {
            if (null != oppoPushCallback) {
                com.coloros.mcssdk.PushManager.getInstance().unsetAlias(alias);
            }
        } else if (RomUtil.isFlyme()) {
            if (!isSupportMeizuPush) {
                return;
            }
            com.meizu.cloud.pushsdk.PushManager.unSubScribeAlias(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY, com.meizu.cloud.pushsdk.PushManager.getPushId(context), alias);
        } else {
            if (RomUtil.isSmartisan()) {
            }
        }
    }

    /**
     * 设置账号
     *
     * @param context
     * @param account
     */
    public static void setUserAccount(Context context, String account) {
        if (!PhoneUtils.isChinaCountry(context)) {
            return;
        }
        if (RomUtil.isMiui()) {
            MiPushClient.setUserAccount(context, account, null);
        } else if (RomUtil.isEmui()) {

        } else if (RomUtil.isVivo()) {
            if (!isSupportVivoPush) {
                return;
            }
        } else if (RomUtil.isOppo()) {
            if (null != oppoPushCallback) {
                com.coloros.mcssdk.PushManager.getInstance().setUserAccount(account);
            }
        } else if (RomUtil.isFlyme()) {
            if (!isSupportMeizuPush) {
                return;
            }
        } else {
            if (RomUtil.isSmartisan()) {
            }
        }
    }

    /**
     * 撤销账号
     *
     * @param context
     * @param account
     */
    public static void unsetUserAccount(Context context, String account) {
        if (!PhoneUtils.isChinaCountry(context)) {
            return;
        }
        if (RomUtil.isMiui()) {
            MiPushClient.unsetUserAccount(context, account, null);
        } else if (RomUtil.isEmui()) {

        } else if (RomUtil.isVivo()) {
            if (!isSupportVivoPush) {
                return;
            }
        } else if (RomUtil.isOppo()) {
            if (null != oppoPushCallback) {
                List<String> list = new ArrayList<>();
                list.add(account);
                com.coloros.mcssdk.PushManager.getInstance().unsetUserAccounts(list);
            }
        } else if (RomUtil.isFlyme()) {
            if (!isSupportMeizuPush) {
                return;
            }
        } else {
            if (RomUtil.isSmartisan()) {
            }
        }
    }

    /**
     * 设置标签
     *
     * @param context
     * @param topic
     */
    public static void setTopic(final Context context, final String topic) {
        if (!PhoneUtils.isChinaCountry(context)) {
            return;
        }
        if (RomUtil.isMiui()) {
            MiPushClient.subscribe(context, topic, null);
        } else if (RomUtil.isEmui()) {
            HMSAgent.Push.setTopic(getTopicMap(topic), new SetTopicHandler() {
                @Override
                public void onResult(int rst) {
                    LogUtils.i(TAG, "huawei setTopic: end" + rst);
                    PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_TOPIC);
                    pushData.setTopic(topic);
                    pushData.setResultCode(rst);
                    ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_HUAWEI);
                }
            });
        } else if (RomUtil.isVivo()) {
            if (!isSupportVivoPush) {
                return;
            }
            PushClient.getInstance(context.getApplicationContext()).setTopic(topic, new IPushActionListener() {
                @Override
                public void onStateChanged(int state) {
                    LogUtils.i(TAG, "vivo setTopic: end state：" + state + " isSuc:" + (state == 0));
                }
            });
        } else if (RomUtil.isOppo()) {
            if (null != oppoPushCallback) {
                List<String> list = new ArrayList<>();
                list.add(topic);
                com.coloros.mcssdk.PushManager.getInstance().setTags(list);
            }
        } else if (RomUtil.isFlyme()) {
            if (!isSupportMeizuPush) {
                return;
            }
            com.meizu.cloud.pushsdk.PushManager.subScribeTags(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY, com.meizu.cloud.pushsdk.PushManager.getPushId(context), topic);//多个标签 topic1+","+topic2+","+topic3
        } else {
            if (RomUtil.isSmartisan()) {
//                MiPushClient.subscribe(context, topic, null);
            }
        }
    }

    /**
     * 获取添加的topic集合
     *
     * @param topic
     * @return
     */
    private static Map<String, String> getTopicMap(String topic) {
        Map<String, String> map = new HashMap<>();
        map.put(topic, topic);
        return map;
    }

    /**
     * 撤销标签
     *
     * @param context
     * @param topic
     */
    public static void unsetTopic(final Context context, final String topic) {
        if (!PhoneUtils.isChinaCountry(context)) {
            return;
        }
        if (RomUtil.isMiui()) {
            MiPushClient.unsubscribe(context, topic, null);
        } else if (RomUtil.isEmui()) {
            HMSAgent.Push.deleteTopic(getTopicKeys(topic), new DeleteTopicHandler() {
                @Override
                public void onResult(int rst) {
                    LogUtils.i(TAG, "华为 deleteToken: end" + rst);
                    PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_UNTOPIC);
                    pushData.setTopic(topic);
                    pushData.setResultCode(rst);
                    ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_HUAWEI);
                }
            });
        } else if (RomUtil.isVivo()) {
            if (!isSupportVivoPush) {
                return;
            }
            PushClient.getInstance(context.getApplicationContext()).delTopic(topic, new IPushActionListener() {

                @Override
                public void onStateChanged(int state) {
                    LogUtils.i(TAG, "vivo unsetTopic: end state：" + state + " isSuc:" + (state == 0));
                }
            });
        } else if (RomUtil.isOppo()) {
            if (null != oppoPushCallback) {
                List<String> list = new ArrayList<>();
                list.add(topic);
                com.coloros.mcssdk.PushManager.getInstance().unsetTags(list);
            }
        } else if (RomUtil.isFlyme()) {
            if (!isSupportMeizuPush) {
                return;
            }
            com.meizu.cloud.pushsdk.PushManager.unSubScribeTags(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY, com.meizu.cloud.pushsdk.PushManager.getPushId(context), topic);
        } else {
            if (RomUtil.isSmartisan()) {
//                MiPushClient.unsubscribe(context, topic, null);
            }
        }
    }

    /**
     * 获取删除的topic key集合
     *
     * @param topic
     * @return
     */
    private static List<String> getTopicKeys(String topic) {
        List<String> keys = new ArrayList<>();
        keys.add(topic);
        return keys;
    }

    /**
     * 设置接收时间
     *
     * @param context
     * @param startHour
     * @param startMin
     * @param endHour
     * @param endMin
     */
    public static void setAcceptTime(Context context, int startHour, int startMin, int endHour, int endMin) {
        if (!PhoneUtils.isChinaCountry(context)) {
            return;
        }
        if (RomUtil.isMiui()) {
            MiPushClient.setAcceptTime(context, startHour, startMin, endHour, endMin, null);
        } else if (RomUtil.isEmui()) {

        } else if (RomUtil.isVivo()) {
            if (!isSupportVivoPush) {
                return;
            }
        } else if (RomUtil.isOppo()) {
            if (null != oppoPushCallback) {
                List<Integer> weekDays = new ArrayList<>();
                weekDays.add(0);//周日为0,周一为1,以此类推
                weekDays.add(1);
                weekDays.add(2);
                weekDays.add(3);
                weekDays.add(4);
                weekDays.add(5);
                weekDays.add(6);
                com.coloros.mcssdk.PushManager.getInstance().setPushTime(weekDays, startHour, startMin, endHour, endMin);
            }
        } else if (RomUtil.isFlyme()) {
            if (!isSupportMeizuPush) {
                return;
            }
        } else {
            if (RomUtil.isSmartisan()) {
            }
        }
    }

    /**
     * 清空推送通知根据id
     *
     * @param context
     * @param platform
     * @param notifyId
     */
    public static void clearPushNotification(Context context, String platform, int notifyId) {
        switch (platform) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                MiPushClient.clearNotification(context, notifyId);
                break;
            default:
                MiPushClient.clearNotification(context, notifyId);
                break;
        }
    }

    /**
     * 清空所有推送通知
     *
     * @param context
     * @param platform
     */
    public static void clearAllPushNotification(Context context, String platform) {
        switch (platform) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                MiPushClient.clearNotification(context);
                break;
            default:
                MiPushClient.clearNotification(context);
                break;
        }
    }
}
