package com.dongxl.huawei.hms.agent.opendevice;

import android.os.Handler;
import android.os.Looper;

import com.dongxl.huawei.hms.agent.HMSAgent;
import com.dongxl.huawei.hms.agent.common.ApiClientMgr;
import com.dongxl.huawei.hms.agent.common.BaseApiAgent;
import com.dongxl.huawei.hms.agent.common.CallbackResultRunnable;
import com.dongxl.huawei.hms.agent.common.HMSAgentLog;
import com.dongxl.huawei.hms.agent.common.StrUtils;
import com.dongxl.huawei.hms.agent.opendevice.handler.GetOdidHandler;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.core.CommonCode;
import com.huawei.hms.support.api.opendevice.HuaweiOpendevice;
import com.huawei.hms.support.api.opendevice.OdidResult;

/**
 * 获取odid请求
 */
public class GetOdidApi extends BaseApiAgent {

    /**
     * client 无效最大尝试次数
     */
    private static final int MAX_RETRY_TIMES = 1;

    /**
     * 获取odid结果回调
     */
    private GetOdidHandler handler;

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
            onGetOdidResult(rst, null);
            return;
        }

        PendingResult<OdidResult> getOdidResult = HuaweiOpendevice.HuaweiOpendeviceApi.getOdid(client);
        getOdidResult.setResultCallback(new ResultCallback<OdidResult>() {
            @Override
            public void onResult(OdidResult result) {
                if (result == null) {
                    HMSAgentLog.e("result is null");
                    onGetOdidResult(HMSAgent.AgentResultCode.RESULT_IS_NULL, null);
                    return;
                }

                Status status = result.getStatus();
                if (status == null) {
                    HMSAgentLog.e("status is null");
                    onGetOdidResult(HMSAgent.AgentResultCode.STATUS_IS_NULL, null);
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
                    onGetOdidResult(rstCode, result);
                }
            }
        });
    }

    /**
     * getodid结果
     * @param rstCode 结果码
     * @param result odid结果
     */
    void onGetOdidResult(int rstCode, OdidResult result) {
        HMSAgentLog.i("getOdid:callback=" + StrUtils.objDesc(handler) +" retCode=" + rstCode);
        if (handler != null) {
            new Handler(Looper.getMainLooper()).post(new CallbackResultRunnable<OdidResult>(handler, rstCode, result));
            handler = null;
        }
        retryTimes = MAX_RETRY_TIMES;
    }

    /**
     * getoaid请求
     */
    public void getOdid(GetOdidHandler handler){
        HMSAgentLog.i("getOdid:handler=" + StrUtils.objDesc(handler));
        this.handler = handler;
        retryTimes = MAX_RETRY_TIMES;
        connect();
    }
}
