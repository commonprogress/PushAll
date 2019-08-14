package com.dongxl.pushdeme.vivo;

import android.content.Context;
import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;

/**
 * vivo 操作结果类
 */
public class VivoPushOperation {

    /**
     * 发送消息到接收服务
     *
     * @param pushData
     */
    private void sendPushDataToService(final Context context, final PushDataBean pushData) {
        if (null != context && null != pushData) {
            ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_VIVO);
        }
        if (null != pushData) {
            LogUtils.i("VivoPushOperation", "VivoPushOperation Result is called. 111: ==pushData" + pushData.toString());
        }
    }

    /**
     * 设置别名
     *
     * @param context
     * @param alias
     */
    public void bindAlias(final Context context, final String alias) {
        PushClient.getInstance(context.getApplicationContext()).bindAlias(alias, new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_ALIAS, state);
                String reason;
                if (state == VivoResultCode.VIVO_SUCCESS) {
                    pushData.setAlias(alias);
                    reason = "Vivo bindAlias Result SUCCESS msg=" + alias;
                } else {
                    reason = "Vivo bindAlias Result Failed code=" + state;
                }
                pushData.setReason(reason);
                sendPushDataToService(context, pushData);

            }
        });
    }

    /**
     * 取消别名
     *
     * @param context
     * @param alias
     */
    public void unBindAlias(final Context context, final String alias) {
        PushClient.getInstance(context.getApplicationContext()).unBindAlias(alias, new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_UNALIAS, state);
                String reason;
                if (state == VivoResultCode.VIVO_SUCCESS) {
                    pushData.setAlias(alias);
                    reason = "Vivo unBindAlias Result SUCCESS msg=" + alias;
                } else {
                    reason = "Vivo unBindAlias Result Failed code=" + state;
                }
                pushData.setReason(reason);
                sendPushDataToService(context, pushData);
            }
        });
    }

    /**
     * 设置topic
     *
     * @param context
     * @param topic
     */
    public void setTopic(final Context context, final String topic) {
        PushClient.getInstance(context.getApplicationContext()).setTopic(topic, new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_TOPIC, state);
                String reason;
                if (state == VivoResultCode.VIVO_SUCCESS) {
                    pushData.setTopic(topic);
                    reason = "Vivo setTopic Result SUCCESS msg=" + topic;
                } else {
                    reason = "Vivo setTopic Result Failed code=" + state;
                }
                pushData.setReason(reason);
                sendPushDataToService(context, pushData);
            }
        });
    }

    /**
     * 设置topic
     *
     * @param context
     * @param topic
     */
    public void delTopic(final Context context, final String topic) {
        PushClient.getInstance(context.getApplicationContext()).delTopic(topic, new IPushActionListener() {

            @Override
            public void onStateChanged(int state) {
                final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_UNTOPIC, state);
                String reason;
                if (state == VivoResultCode.VIVO_SUCCESS) {
                    pushData.setTopic(topic);
                    reason = "Vivo delTopic Result SUCCESS msg=" + topic;
                } else {
                    reason = "Vivo delTopic Result Failed code=" + state;
                }
                pushData.setReason(reason);
                sendPushDataToService(context, pushData);
            }
        });
    }

    public class VivoResultCode {
        public static final int VIVO_SUCCESS = 0;
    }
}
