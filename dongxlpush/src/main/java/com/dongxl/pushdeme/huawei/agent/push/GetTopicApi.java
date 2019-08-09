package com.dongxl.pushdeme.huawei.agent.push;

import android.os.Handler;
import android.os.Looper;

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.push.GetTagResult;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.PushException;
import com.dongxl.pushdeme.huawei.HMSAgent;
import com.dongxl.pushdeme.huawei.agent.common.*;
import com.dongxl.pushdeme.huawei.agent.push.handler.GetTopicHandler;

import java.util.Map;

/**
 * 设置getTopicApi
 * 服务已经不存在
 */
@Deprecated
public class GetTopicApi extends BaseApiAgent {
    /**
     * 结果回调
     */
    private GetTopicHandler handler;

    /**
     * HuaweiApiClient 连接结果回调
     *
     * @param rst    结果码
     * @param client HuaweiApiClient 实例
     */
    @Override
    public void onConnect(final int rst, final HuaweiApiClient client) {
        if (client == null || !ApiClientMgr.INST.isConnect(client)) {
            HMSAgentLog.e("client not connted");
            getTopicResult(rst, null);
        } else {
            try {
                PendingResult<GetTagResult> getTagResult = HuaweiPush.HuaweiPushApi.getTags(client);
                getTagResult.setResultCallback(new ResultCallback<GetTagResult>() {
                    @Override
                    public void onResult(GetTagResult result) {
                        if (result == null) {
                            HMSAgentLog.e("result is null");
                            getTopicResult(HMSAgent.AgentResultCode.RESULT_IS_NULL, null);
                            return;
                        }

                        Status status = result.getStatus();
                        if (status == null) {
                            HMSAgentLog.e("status is null");
                            getTopicResult(HMSAgent.AgentResultCode.STATUS_IS_NULL, null);
                            return;
                        }
                        HMSAgentLog.d("status=" + status);
                        if (status == Status.SUCCESS) {
                            getTopicResult(HMSAgent.AgentResultCode.HMSAGENT_SUCCESS, result.getTags());
                        } else {
                            getTopicResult(status.getStatusCode(), null);
                        }
                    }
                });
                getTopicResult(HMSAgent.AgentResultCode.HMSAGENT_SUCCESS, null);
            } catch (PushException e) {
                HMSAgentLog.e("getTopic 失败:" + e.getMessage());
                getTopicResult(HMSAgent.AgentResultCode.CALL_EXCEPTION, null);
            }
        }
    }

    private void getTopicResult(int rstCode, Map<String, String> tags) {
        HMSAgentLog.i("GetTopicApi:callback=" + StrUtils.objDesc(handler) + " retCode=" + rstCode + " tags=" + tags);
        if (handler != null) {
            new Handler(Looper.getMainLooper()).post(new CallbackCodeRunnable(handler, rstCode));
            handler = null;
        }
    }

    /**
     * 获取Topic
     *
     * @param handler
     */
    public void getTopic(GetTopicHandler handler) {
        HMSAgentLog.i("GetTopicApi:getTopic handler=" + StrUtils.objDesc(handler));
        this.handler = handler;
        connect();
    }
}
