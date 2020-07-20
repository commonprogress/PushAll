package com.dongxl.pushdeme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dongxl.pushdeme.bean.MessageDataBean;
import com.dongxl.pushdeme.bean.PushDataBean;
import com.dongxl.pushdeme.utils.LogUtils;

public abstract class PushMessageReceiver extends BroadcastReceiver {
    private final static String TAG = PushMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = null != context && null != intent ? intent.getAction() : "";
        if (!TextUtils.isEmpty(action) && action.equals(BuildConfig.PUSHRECEIVESERVICE)) {
            PushDataBean pushData = (PushDataBean) intent.getSerializableExtra(PushConstants.KEY_PUSH_DATA);
            String platform = null == pushData ? null : pushData.getPlatform();
            int resultType = null == pushData ? 0 : pushData.getResultType();
            switch (resultType) {
                //透传消息
                case PushConstants.HandlerWhat.WHAT_THROUGH_MESSAGE:
                    onReceiveThroughMessage(pushData.getThroughMessage(), platform);
                    break;
                //收到通知回调
                case PushConstants.HandlerWhat.WHAT_NOTIFI_MESSAGE_ARRIVE:
                    onReceiveNotifiMessage(pushData.getThroughMessage(), platform);
                    break;
                //获取token
                case PushConstants.HandlerWhat.WHAT_PUSH_REGISTER:
                    onPushNewToken(pushData.getRegId(), platform);
                    break;
                default:
                    LogUtils.i(TAG, "pushResultOperation WHAT_PUSH_OTHER 555 platform: " + platform + " Command:  " + pushData.getCommand() + " ResultCode:  " + pushData.getResultCode() + " Reason:  " + pushData.getReason());
                    break;
            }
        }
    }

    /**
     * 获取新的token new token
     *
     * @param platform
     * @param regId
     */
    protected abstract void onPushNewToken(String regId, String platform);

    /**
     * 接收到通知消息 暂时不支持
     *
     * @param throughMessage
     * @param platform
     */
    protected abstract void onReceiveNotifiMessage(MessageDataBean throughMessage, String platform);

    /**
     * 接收到透传消息的 小米 华为 支持
     *
     * @param throughMessage
     * @param platform
     */
    protected abstract void onReceiveThroughMessage(MessageDataBean throughMessage, String platform);

}
