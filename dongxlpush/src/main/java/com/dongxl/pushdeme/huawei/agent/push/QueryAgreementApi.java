package com.dongxl.pushdeme.huawei.agent.push;

import android.os.Handler;
import android.os.Looper;

import com.huawei.hms.api.HuaweiApiClient;
//import com.huawei.hms.support.api.push.HuaweiPush;
import com.dongxl.pushdeme.huawei.HMSAgent;
import com.dongxl.pushdeme.huawei.agent.common.*;
import com.dongxl.pushdeme.huawei.agent.push.handler.QueryAgreementHandler;

/**
 * 获取push协议展示的接口。
 */
public class QueryAgreementApi extends BaseApiAgent {

    /**
     * 调用接口回调
     */
    private QueryAgreementHandler handler;

    /**
     * HuaweiApiClient 连接结果回调
     *
     * @param rst    结果码
     * @param client HuaweiApiClient 实例
     */
    @Override
    public void onConnect(final int rst, final HuaweiApiClient client) {
        //需要在子线程中执行获取push协议展示的操作
//        ThreadUtil.INST.excute(new Runnable() {
//            @Override
//            public void run() {
//                if (client == null || !ApiClientMgr.INST.isConnect(client)) {
//                    HMSAgentLog.e("client not connted");
//                    onQueryAgreementResult(rst);
//                } else {
//                    HuaweiPush.HuaweiPushApi.queryAgreement(client);
//                    onQueryAgreementResult(HMSAgent.AgentResultCode.HMSAGENT_SUCCESS);
//                }
//            }
//        });
    }

    void onQueryAgreementResult(int rstCode) {
        HMSAgentLog.i("queryAgreement:callback=" + StrUtils.objDesc(handler) +" retCode=" + rstCode);
        if (handler != null) {
            new Handler(Looper.getMainLooper()).post(new CallbackCodeRunnable(handler, rstCode));
            handler = null;
        }
    }

    /**
     * 请求push协议展示
     */
    public void queryAgreement(QueryAgreementHandler handler) {
        HMSAgentLog.i("queryAgreement:handler=" + StrUtils.objDesc(handler));
        this.handler = handler;
        connect();
    }
}
