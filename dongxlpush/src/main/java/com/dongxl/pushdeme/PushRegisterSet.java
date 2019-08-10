package com.dongxl.pushdeme;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.dongxl.pushdeme.huawei.HMSAgent;
import com.dongxl.pushdeme.huawei.agent.common.HMSAgentLog;
import com.dongxl.pushdeme.huawei.agent.common.handler.ConnectHandler;
import com.dongxl.pushdeme.huawei.agent.push.handler.GetTokenHandler;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * 推送相关注册和操作
 */
public class PushRegisterSet {
    private final static String TAG = PushRegisterSet.class.getSimpleName();

    /**
     * oppo 设置操作相关的回调
     */
    private static OppoPushCallback oppoPushCallback;

    private static void initOppoPushCallback(Context context) {
        if (null == oppoPushCallback) {
            oppoPushCallback = new OppoPushCallback(context.getApplicationContext());
        }
    }

    /**
     * application 初始化注册
     *
     * @param application
     */
    public static void applicationInit(Application application) {
        if (!PhoneUtils.isChinaCountry(application)) {
            return;
        }
        String platform = getSupportPushPlatform(application);
        LogUtils.e(PushConstants.XIAOMI_TAG, "==push applicationInit==platform:" + platform);
        switch (platform) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                //不需要Application初始化
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                huaweiRegisterInit(application);
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                //不需要Application初始化
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                //不需要Application初始化
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                //不需要Application初始化
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光 不需要Application初始化
                break;
            default:
                break;
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
        switch (getSupportPushPlatform(activity)) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                xiaomiRegisterInit(activity);
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                huaweiRegisterConnect(activity);
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                oppoRegisterInit(activity);
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                vivoRegisterInit(activity);
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                meizuRegisterInit(activity);
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光
                jpushRegisterInit(activity);
                break;
            default:
                break;
        }
    }

    /**
     * 小米注册
     *
     * @param context
     */
    public static void xiaomiRegisterInit(Context context) {
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
                LogUtils.i(TAG, "HMS connect end:" + rst);
                getRegId(context);
            }
        });
    }

    /**
     * vivo 注册
     *
     * @param context
     */
    private static void vivoRegisterInit(Context context) {
        PushClient.getInstance(context.getApplicationContext()).initialize();
        PushClient.getInstance(context.getApplicationContext()).turnOnPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                LogUtils.i(TAG, "vivo 打开push getRegId: end state：" + state + " isSuc:" + (state == 0));
            }
        });
    }

    /**
     * oppo 注册
     *
     * @param context
     */
    private static void oppoRegisterInit(Context context) {
        initOppoPushCallback(context);
        com.coloros.mcssdk.PushManager.getInstance().register(context.getApplicationContext(),
                PushConstants.OPPO_APP_KEY, PushConstants.OPPO_APP_SECRET, oppoPushCallback);//setPushCallback接口也可设置callback
//        getRegId(context);
    }

    /**
     * 魅族注册
     *
     * @param context
     */
    private static void meizuRegisterInit(Context context) {
        com.meizu.cloud.pushsdk.PushManager.register(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY);
    }

    /**
     * 极光推送注册
     *
     * @param context
     */
    private static void jpushRegisterInit(Context context) {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
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
        String regId = "";
        switch (getSupportPushPlatform(context)) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                regId = MiPushClient.getRegId(context);
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                HMSAgent.Push.getToken(new GetTokenHandler() {
                    @Override
                    public void onResult(int rst) {
                        LogUtils.i(TAG, "get token: end" + rst);
                    }
                });
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                com.coloros.mcssdk.PushManager.getInstance().getRegister();
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                regId = PushClient.getInstance(context.getApplicationContext()).getRegId();
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光
                regId = JPushInterface.getRegistrationID(context.getApplicationContext());
                break;
            default:
                break;
        }
        return regId;
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
        switch (getSupportPushPlatform(context)) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                MiPushClient.setAlias(context, alias, null);
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                initOppoPushCallback(context);
                List<String> list = new ArrayList<>();
                list.add(alias);
                com.coloros.mcssdk.PushManager.getInstance().setAliases(list);
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                //unBindAlias 一天内最多调用 100 次，两次调用的间隔需大于 2s
                PushClient.getInstance(context.getApplicationContext()).bindAlias(alias, new IPushActionListener() {

                    @Override
                    public void onStateChanged(int state) {
                        LogUtils.i(TAG, "vivo  setAlias: end state：" + state + " isSuc:" + (state == 0));
                    }
                });
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                com.meizu.cloud.pushsdk.PushManager.subScribeAlias(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY, com.meizu.cloud.pushsdk.PushManager.getPushId(context), alias);
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光
                JPushInterface.setAlias(context, 1, alias);//也可以同时设置对个，具体看官网
                break;
            default:
                break;
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
        switch (getSupportPushPlatform(context)) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                MiPushClient.unsetAlias(context, alias, null);
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                initOppoPushCallback(context);
                com.coloros.mcssdk.PushManager.getInstance().unsetAlias(alias);
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                //bindAlias 一天内最多调用 100 次，两次调用的间隔需大于 2s
                PushClient.getInstance(context.getApplicationContext()).unBindAlias(alias, new IPushActionListener() {

                    @Override
                    public void onStateChanged(int state) {
                        LogUtils.i(TAG, "vivo unsetAlias: end state：" + state + " isSuc:" + (state == 0));
                    }
                });
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                com.meizu.cloud.pushsdk.PushManager.unSubScribeAlias(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY, com.meizu.cloud.pushsdk.PushManager.getPushId(context), alias);
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光
                JPushInterface.deleteAlias(context, 2);//也可以同时设置对个，具体看官网
                break;
            default:
                break;
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
        switch (getSupportPushPlatform(context)) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                MiPushClient.setUserAccount(context, account, null);
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                initOppoPushCallback(context);
                com.coloros.mcssdk.PushManager.getInstance().setUserAccount(account);
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光 不支持
                break;
            default:
                break;
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
        switch (getSupportPushPlatform(context)) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                MiPushClient.unsetUserAccount(context, account, null);
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                initOppoPushCallback(context);
                List<String> list = new ArrayList<>();
                list.add(account);
                com.coloros.mcssdk.PushManager.getInstance().unsetUserAccounts(list);
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光 不支持
                break;
            default:
                break;
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
        switch (getSupportPushPlatform(context)) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                MiPushClient.subscribe(context, topic, null);
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                // 以前支持现在已经作废
//            HMSAgent.Push.setTopic(getTopicMap(topic), new SetTopicHandler() {
//                @Override
//                public void onResult(int rst) {
//                    LogUtils.i(TAG, "setTopic: end" + rst);
//                    PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_TOPIC);
//                    pushData.setTopic(topic);
//                    pushData.setResultCode(rst);
//                    ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_HUAWEI);
//                }
//            });
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                initOppoPushCallback(context);
                List<String> list = new ArrayList<>();
                list.add(topic);
                com.coloros.mcssdk.PushManager.getInstance().setTags(list);
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                //delTopic 一天内最多调用 500 次，两次调用的间隔需大于 2s
                PushClient.getInstance(context.getApplicationContext()).setTopic(topic, new IPushActionListener() {
                    @Override
                    public void onStateChanged(int state) {
                        LogUtils.i(TAG, "vivo setTopic: end state：" + state + " isSuc:" + (state == 0));
                    }
                });
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                com.meizu.cloud.pushsdk.PushManager.subScribeTags(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY, com.meizu.cloud.pushsdk.PushManager.getPushId(context), topic);
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光
                Set<String> topics = new LinkedHashSet<>();
                topics.add(topic);
                JPushInterface.setTags(context, 4, topics);
                JPushInterface.addTags(context, 3, topics); //可以同时设置多个
                break;
            default:
                break;
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
        switch (getSupportPushPlatform(context)) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                MiPushClient.unsubscribe(context, topic, null);
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                // 以前支持现在已经作废
//            HMSAgent.Push.deleteTopic(getTopicKeys(topic), new DeleteTopicHandler() {
//                @Override
//                public void onResult(int rst) {
//                    LogUtils.i(TAG, "deleteToken: end" + rst);
//                    PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_UNTOPIC);
//                    pushData.setTopic(topic);
//                    pushData.setResultCode(rst);
//                    ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_HUAWEI);
//                }
//            });
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                initOppoPushCallback(context);
                List<String> list = new ArrayList<>();
                list.add(topic);
                com.coloros.mcssdk.PushManager.getInstance().unsetTags(list);
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                //与setTopic 一天内最多调用 500 次，两次调用的间隔需大于 2s
                PushClient.getInstance(context.getApplicationContext()).delTopic(topic, new IPushActionListener() {

                    @Override
                    public void onStateChanged(int state) {
                        LogUtils.i(TAG, "vivo unsetTopic: end state：" + state + " isSuc:" + (state == 0));
                    }
                });
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                com.meizu.cloud.pushsdk.PushManager.unSubScribeTags(context, PushConstants.MEIZU_APP_ID, PushConstants.MEIZU_APP_KEY, com.meizu.cloud.pushsdk.PushManager.getPushId(context), topic);
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光
                Set<String> topics = new LinkedHashSet<>();
                topics.add(topic);
                JPushInterface.deleteTags(context, 5, topics);
                JPushInterface.cleanTags(context, 6);

                break;
            default:
                break;
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
        switch (getSupportPushPlatform(context)) {
            case PushConstants.PushPlatform.PLATFORM_XIAOMI:
                MiPushClient.setAcceptTime(context, startHour, startMin, endHour, endMin, null);
                break;
            case PushConstants.PushPlatform.PLATFORM_HUAWEI:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_OPPO:
                initOppoPushCallback(context);
                List<Integer> weekDays = new ArrayList<>();
                weekDays.add(0);//周日为0,周一为1,以此类推
                weekDays.add(1);
                weekDays.add(2);
                weekDays.add(3);
                weekDays.add(4);
                weekDays.add(5);
                weekDays.add(6);
                com.coloros.mcssdk.PushManager.getInstance().setPushTime(weekDays, startHour, startMin, endHour, endMin);
                break;
            case PushConstants.PushPlatform.PLATFORM_VIVO:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_FLYME:
                //不支持
                break;
            case PushConstants.PushPlatform.PLATFORM_JPSUH:
                //极光 不支持
                break;
            default:
                break;
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

    /**
     * 获取当前设备支持的推送类型
     *
     * @param context
     * @return push platform
     */
    private static String getSupportPushPlatform(Context context) {
        Context mContext = context.getApplicationContext();
        if (RomUtil.isMiui()) {
            return PushConstants.PushPlatform.PLATFORM_XIAOMI;
        } else if (RomUtil.isEmui()) {
            return PushConstants.PushPlatform.PLATFORM_HUAWEI;
        } else if (PushClient.getInstance(mContext).isSupport()) {
            return PushConstants.PushPlatform.PLATFORM_VIVO;
        } else if (com.coloros.mcssdk.PushManager.isSupportPush(mContext)) {
            return PushConstants.PushPlatform.PLATFORM_OPPO;
        } else if (MzSystemUtils.isMeizu(mContext)) {
            return PushConstants.PushPlatform.PLATFORM_FLYME;
        } else {
            return PushConstants.PushPlatform.PLATFORM_JPSUH;
//            return PushConstants.PushPlatform.PLATFORM_OTHER;
        }
    }
}
