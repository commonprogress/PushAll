package com.dongxl.pushdeme.huawei.agent.push;

import android.os.Handler;
import android.os.Looper;

import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.push.HandleTagsResult;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.PushException;
import com.dongxl.pushdeme.huawei.HMSAgent;
import com.dongxl.pushdeme.huawei.agent.common.*;
import com.dongxl.pushdeme.huawei.agent.push.handler.SetTopicHandler;

import java.util.Map;

/**
 * 设置SetTopicApi
 * 服务已经不存在
 */
@Deprecated
public class SetTopicApi extends BaseApiAgent {
    /**
     * 结果回调
     */
    private SetTopicHandler handler;
    private Map<String, String> topics;

    /**
     * HuaweiApiClient 连接结果回调
     *
     * @param rst    结果码
     * @param client HuaweiApiClient 实例
     */
    @Override
    public void onConnect(final int rst, final HuaweiApiClient client) {
        //调用setTopic需要传入设置的topics集合，并且需要对topics进行非空判断
        if (null == topics || topics.isEmpty()) {
            HMSAgentLog.e("setTopic topics.isEmpty");
            setTopicResult(HMSAgent.AgentResultCode.EMPTY_PARAM);
        } else {
            if (client == null || !ApiClientMgr.INST.isConnect(client)) {
                HMSAgentLog.e("client not connted");
                setTopicResult(rst);
            } else {
                try {
                    PendingResult<HandleTagsResult> tagResult = HuaweiPush.HuaweiPushApi.setTags(client, topics);
                    tagResult.setResultCallback(new ResultCallback<HandleTagsResult>() {
                        @Override
                        public void onResult(HandleTagsResult result) {
                            if (result == null) {
                                HMSAgentLog.e("result is null");
                                setTopicResult(HMSAgent.AgentResultCode.RESULT_IS_NULL);
                                return;
                            }

                            Status status = result.getStatus();
                            if (status == null) {
                                HMSAgentLog.e("status is null");
                                setTopicResult(HMSAgent.AgentResultCode.STATUS_IS_NULL);
                                return;
                            }
                            HMSAgentLog.d("status=" + status);
                            if (status == Status.SUCCESS) {
                                setTopicResult(HMSAgent.AgentResultCode.HMSAGENT_SUCCESS);
                            } else {
                                setTopicResult(status.getStatusCode());
                            }
                        }
                    });
                    setTopicResult(HMSAgent.AgentResultCode.HMSAGENT_SUCCESS);
                } catch (PushException e) {
                    HMSAgentLog.e("setTopic 失败:" + e.getMessage());
                    setTopicResult(HMSAgent.AgentResultCode.CALL_EXCEPTION);
                }
            }


        }
    }

    private void setTopicResult(int rstCode) {
        HMSAgentLog.i("SetTopicApi:callback=" + StrUtils.objDesc(handler) + " retCode=" + rstCode);
        if (handler != null) {
            new Handler(Looper.getMainLooper()).post(new CallbackCodeRunnable(handler, rstCode));
            handler = null;
        }
    }

    /**
     * 设置Topic
     *
     * @param tags
     * @param handler
     */
    public void setTopic(Map<String, String> tags, SetTopicHandler handler) {
        HMSAgentLog.i("SetTopicApi:setTopic tags=" + tags + " handler=" + StrUtils.objDesc(handler));
        this.handler = handler;
        this.topics = tags;
        connect();
    }
}
