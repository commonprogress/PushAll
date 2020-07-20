package com.dongxl.push.broadcast;

import com.dongxl.pushdeme.PushMessageReceiver;
import com.dongxl.pushdeme.bean.MessageDataBean;

public class DemoPushMessageReceiver extends PushMessageReceiver {
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
}
