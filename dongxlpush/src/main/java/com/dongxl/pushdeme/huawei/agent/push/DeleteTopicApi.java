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
import com.dongxl.pushdeme.huawei.agent.push.handler.DeleteTopicHandler;

import java.util.List;

/**
 * 删除TopicApi
 *  服务已经不存在
 */
@Deprecated
public class DeleteTopicApi extends BaseApiAgent {
    /**
     * 结果回调
     */
    private DeleteTopicHandler handler;

    private List<String> keyList;

    /**
     * HuaweiApiClient 连接结果回调
     *
     * @param rst    结果码
     * @param client HuaweiApiClient 实例
     */
    @Override
    public void onConnect(final int rst, final HuaweiApiClient client) {
        //调用deleteTopic需要传入设置的keyList集合，并且需要对keyList进行非空判断
        if (null == keyList || keyList.isEmpty()) {
            HMSAgentLog.e("deleteTopic topics.isEmpty");
            deleteTopicResult(HMSAgent.AgentResultCode.EMPTY_PARAM);
        } else {
            if (client == null || !ApiClientMgr.INST.isConnect(client)) {
                HMSAgentLog.e("client not connted");
                deleteTopicResult(rst);
            } else {
                try {
                    PendingResult<HandleTagsResult> tagResult = HuaweiPush.HuaweiPushApi.deleteTags(client, keyList);
                    tagResult.setResultCallback(new ResultCallback<HandleTagsResult>() {
                        @Override
                        public void onResult(HandleTagsResult result) {
                            if (result == null) {
                                HMSAgentLog.e("result is null");
                                deleteTopicResult(HMSAgent.AgentResultCode.RESULT_IS_NULL);
                                return;
                            }
                            Status status = result.getStatus();
                            if (status == null) {
                                HMSAgentLog.e("status is null");
                                deleteTopicResult(HMSAgent.AgentResultCode.STATUS_IS_NULL);
                                return;
                            }
                            HMSAgentLog.d("status=" + status);
                            if (status == Status.SUCCESS) {
                                deleteTopicResult(HMSAgent.AgentResultCode.HMSAGENT_SUCCESS);
                            } else {
                                deleteTopicResult(status.getStatusCode());
                            }
                        }
                    });
                } catch (PushException e) {
                    HMSAgentLog.e("deleteTopic 失败:" + e.getMessage());
                    deleteTopicResult(HMSAgent.AgentResultCode.CALL_EXCEPTION);
                }
            }
        }
    }

    private void deleteTopicResult(int rstCode) {
        HMSAgentLog.i("DeleteTopicApi:callback=" + StrUtils.objDesc(handler) + " retCode=" + rstCode);
        if (handler != null) {
            new Handler(Looper.getMainLooper()).post(new CallbackCodeRunnable(handler, rstCode));
            handler = null;
        }
    }

    /**
     * 删除Topic
     *
     * @param tagKeys
     * @param handler
     */
    public void deleteTopic(List<String> tagKeys, DeleteTopicHandler handler) {
        HMSAgentLog.i("DeleteTopicApi:deleteTopic tagKeys=" + tagKeys + " handler=" + StrUtils.objDesc(handler));
        this.handler = handler;
        this.keyList = tagKeys;
        connect();
    }
}
