package com.dongxl.huawei.hms.agent.opendevice;

import android.os.Handler;
import android.os.Looper;

import com.dongxl.huawei.hms.agent.HMSAgent;
import com.dongxl.huawei.hms.agent.common.ApiClientMgr;
import com.dongxl.huawei.hms.agent.common.BaseApiAgent;
import com.dongxl.huawei.hms.agent.common.CallbackResultRunnable;
import com.dongxl.huawei.hms.agent.common.HMSAgentLog;
import com.dongxl.huawei.hms.agent.common.StrUtils;
import com.dongxl.huawei.hms.agent.opendevice.handler.GetOaidHandler;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.core.CommonCode;
import com.huawei.hms.support.api.opendevice.HuaweiOpendevice;
import com.huawei.hms.support.api.opendevice.OaidResult;

/**
 * 获取oaid请求
 */
public class GetOaidApi extends BaseApiAgent {

    /**
     * client 无效最大尝试次数
     */
    private static final int MAX_RETRY_TIMES = 1;

    /**
     * 获取oaid结果回调
     */
    private GetOaidHandler handler;

    /**
     * 当前剩余重试次数
     */
    private int retryTimes = MAX_RETRY_TIMES;

    /**
     * HuaweiApiClient 连接结果回调
     *
     * @param rst    结果码
     * @param client HuaweiApiClient 实例
     */
    @Override
    public void onConnect(int rst, HuaweiApiClient client) {
        if (client == null || !ApiClientMgr.INST.isConnect(client)) {
            HMSAgentLog.e("client not connted");
            onGetOaidResult(rst, null);
            return;
        }

        PendingResult<OaidResult> getOaidResult = HuaweiOpendevice.HuaweiOpendeviceApi.getOaid(client);
        getOaidResult.setResultCallback(new ResultCallback<OaidResult>() {
            @Override
            public void onResult(OaidResult result) {
                if (result == null) {
                    HMSAgentLog.e("result is null");
                    onGetOaidResult(HMSAgent.AgentResultCode.RESULT_IS_NULL, null);
                    return;
                }

                Status status = result.getStatus();
                if (status == null) {
                    HMSAgentLog.e("status is null");
                    onGetOaidResult(HMSAgent.AgentResultCode.STATUS_IS_NULL, null);
                    return;
                }

                int rstCode = status.getStatusCode();
                HMSAgentLog.d("status=" + status);

                // 需要重试的错误码，并且可以重试
                if ((rstCode == CommonCode.ErrorCode.SESSION_INVALID
                        || rstCode == CommonCode.ErrorCode.CLIENT_API_INVALID) && retryTimes > 0) {
                    retryTimes--;
                    connect();
                } else {
                    onGetOaidResult(rstCode, result);
                }
            }
        });
    }

    /**
     * getoaid结果
     * @param rstCode 结果码
     * @param result oaid结果
     */
    void onGetOaidResult(int rstCode, OaidResult result) {
        HMSAgentLog.i("getOaid:callback=" + StrUtils.objDesc(handler) +" retCode=" + rstCode);
        if (handler != null) {
            new Handler(Looper.getMainLooper()).post(new CallbackResultRunnable<OaidResult>(handler, rstCode, result));
            handler = null;
        }
        retryTimes = MAX_RETRY_TIMES;
    }

    /**
     * getoaid请求
     */
    public void getOaid(GetOaidHandler handler){
        HMSAgentLog.i("getOaid:handler=" + StrUtils.objDesc(handler));
        this.handler = handler;
        retryTimes = MAX_RETRY_TIMES;
        connect();
    }
}
