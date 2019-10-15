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
package com.dongxl.pushdeme.huawei;

import android.text.TextUtils;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.huawei.agent.common.HMSSharedUtils;
import com.dongxl.pushdeme.utils.LogUtils;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

public class HuaweiPushService extends HmsMessageService {
    public static final String TAG = HuaweiPushService.class.getSimpleName();

    /**
     * 发送消息到接收服务
     *
     * @param pushData
     */
    private void sendPushDataToService(final PushDataBean pushData) {
        ServiceManager.sendPushDataToService(this, pushData, PushConstants.PushPlatform.PLATFORM_HUAWEI);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_PUSH_REGISTER);
        pushData.setRegId(token);
        String reason = "onNewToken Token is:" + token;
        pushData.setReason(reason);
        sendPushDataToService(pushData);
        HMSSharedUtils.saveHuaweiToken(this, token);
        LogUtils.i(TAG, "onNewToken is called. 111: log==" + reason);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String data = null == remoteMessage ? "" : remoteMessage.getData();
        LogUtils.i(TAG, "onMessageReceived is called. 000:==data==" + data + ":==remoteMessage==" + remoteMessage);
        if (!TextUtils.isEmpty(data)) {
            String reason;
            final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE);
            try {
                //CP可以自己解析消息内容，然后做相应的处理 | CP can parse message content on its own, and then do the appropriate processing
//                String content = new String(msg, "UTF-8");
                String content = data;
                final MessageDataBean messageData = new MessageDataBean();
                messageData.setSentTime(remoteMessage.getSentTime());
                messageData.setContent(content);
                pushData.setThroughMessage(messageData);
                reason = "Receive a push pass message with the message:" + content + " ,collapseKey:" + remoteMessage.getCollapseKey();
            } catch (/*UnsupportedEncoding*/Exception e) {
                e.printStackTrace();
                reason = "Receive push pass message, exception:" + e.getMessage() + " ,collapseKey:" + remoteMessage.getCollapseKey();
            }
//            pushData.setRegId(token);
            pushData.setReason(reason);
            sendPushDataToService(pushData);
            LogUtils.i(TAG, "华为onMessageReceived is called. 111:==reason=" + reason + " ,getMessageId:" + remoteMessage.getMessageId());
        }
        RemoteMessage.Notification notification = null == remoteMessage ? null : remoteMessage.getNotification();
        if (notification != null) {
            LogUtils.i(TAG, "华为onMessageReceived is called. 111:==notification=" + notification);
        }


        // TODO(developer): Handle HCM messages here.
        // Check if message contains a data payload.
        if (remoteMessage.getData().length() > 0) {
            LogUtils.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Job.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            LogUtils.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received HCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * 发送上行消息 结果回掉
     * @param msgId
     */
    @Override
    public void onMessageSent(String msgId) {
        super.onMessageSent(msgId);
//handle this message sent result
    }

    /**
     * 发送上行消息 错误结果回掉
     * @param msgId
     * @param e
     */
    @Override
    public void onSendError(String msgId, Exception e) {
        super.onSendError(msgId, e);
// handle this message sent result
    }
}
