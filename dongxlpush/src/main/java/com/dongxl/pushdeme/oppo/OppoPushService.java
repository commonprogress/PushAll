package com.dongxl.pushdeme.oppo;

import android.content.Context;

import com.coloros.mcssdk.PushService;
import com.coloros.mcssdk.mode.AppMessage;
import com.coloros.mcssdk.mode.CommandMessage;
import com.coloros.mcssdk.mode.SptDataMessage;
import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

/**
 * <p>Title:${Title} </p>
 * <p>Description: PushMessageService</p>
 * <p>Copyright (c) 2016 www.oppo.com Inc. All rights reserved.</p>
 * <p>Company: OPPO</p>
 *
 * @author QuWanxin
 * @version 1.0
 * @date 2017/7/28
 */

/**
 * 如果应用需要解析和处理Push消息（如透传消息），则继承PushService来处理，并在Manifest文件中申明Service
 * 如果不需要处理Push消息，则不需要继承PushService，直接在Manifest文件申明PushService即可
 */
public class OppoPushService extends PushService {
    private final static String TAG = OppoPushService.class.getSimpleName();

    /**
     * 命令消息，主要是服务端对客户端调用的反馈，一般应用不需要重写此方法
     *
     * @param context
     * @param commandMessage
     */
    @Override
    public void processMessage(Context context, CommandMessage commandMessage) {
        super.processMessage(context, commandMessage);
        LogUtils.i(TAG, "OPPO processMessage CommandMessage is called. 111:" + commandMessage.toString());
    }

    /**
     * 普通应用消息，视情况看是否需要重写
     *
     * @param context
     * @param appMessage
     */
    @Override
    public void processMessage(Context context, AppMessage appMessage) {
        super.processMessage(context, appMessage);
        LogUtils.i(TAG, "OPPO processMessage AppMessage is called. 111:" + appMessage.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(appMessage.getContent());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_ARRIVE);
        pushData.setThroughMessage(messageData);
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_OPPO);
    }


    /**
     * 透传消息处理，应用可以打开页面或者执行命令,如果应用不需要处理透传消息，则不需要重写此方法
     *
     * @param context
     * @param sptDataMessage
     */
    @Override
    public void processMessage(Context context, SptDataMessage sptDataMessage) {
        super.processMessage(context.getApplicationContext(), sptDataMessage);
        LogUtils.i(TAG, "OPPO processMessage is called. 111:" + sptDataMessage.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(sptDataMessage.getContent());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE);
        pushData.setThroughMessage(messageData);
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_OPPO);
    }
}
