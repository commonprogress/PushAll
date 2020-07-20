package com.dongxl.pushdeme.oppo;

import android.content.Context;

import com.dongxl.pushdeme.PushConstants;
import com.dongxl.pushdeme.ServiceManager;
import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;
import com.heytap.msp.push.mode.DataMessage;
import com.heytap.msp.push.service.DataMessageCallbackService;

public class OppoPushMsgService extends DataMessageCallbackService {
    private final static String TAG = OppoPushMsgService.class.getSimpleName();

    /**
     * 透传消息处理，应用可以打开页面或者执行命令,如果应用不需要处理透传消息，则不需要重写此方法
     *
     * @param context
     * @param message
     */
    @Override
    public void processMessage(Context context, DataMessage message) {
        super.processMessage(context, message);
        LogUtils.i(TAG, "OPPO processMessage CommandMessage is called. 111:" + message.toString());
        final MessageDataBean messageData = new MessageDataBean();
        messageData.setContent(message.getContent());
        final PushDataBean pushData = new PushDataBean(PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE);
        pushData.setThroughMessage(messageData);
        ServiceManager.sendPushDataToService(context, pushData, PushConstants.PushPlatform.PLATFORM_OPPO);
    }
}
