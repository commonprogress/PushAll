package com.dongxl.push.broadcast;

import com.dongxl.pushdeme.PushMessageReceiver;
import com.dongxl.pushdeme.bean.MessageDataBean;

public class DemoPushMessageReceiver extends PushMessageReceiver {
    @Override
    protected void onPushNewToken(String regId, String platform) {

    }

    @Override
    protected void onReceiveNotifiMessage(MessageDataBean throughMessage, String platform) {

    }

    @Override
    protected void onReceiveThroughMessage(MessageDataBean throughMessage, String platform) {

    }
}
