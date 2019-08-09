package com.dongxl.huawei.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dongxl.huawei.hms.agent.HMSAgent;
import com.dongxl.huawei.hms.agent.push.handler.DeleteTokenHandler;
import com.dongxl.huawei.hms.agent.push.handler.EnableReceiveNormalMsgHandler;
import com.dongxl.huawei.hms.agent.push.handler.EnableReceiveNotifyMsgHandler;
import com.dongxl.huawei.hms.agent.push.handler.GetPushStateHandler;
import com.dongxl.huawei.hms.agent.push.handler.GetTokenHandler;
import com.dongxl.huawei.hms.agent.push.handler.QueryAgreementHandler;
import com.example.jpushdemo.R;

import static com.dongxl.huawei.demo.HuaweiPushRevicer.ACTION_TOKEN;
import static com.dongxl.huawei.demo.HuaweiPushRevicer.ACTION_UPDATEUI;


public class HuaweiPushActivity extends AgentBaseActivity implements HuaweiPushRevicer.IPushCallback {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        setTabBtnClickListener();
        Button btn = (Button) findViewById(R.id.btn_push);
        if (btn != null) {
            btn.setTextColor(Color.RED);
            btn.setEnabled(false);
        }

        findViewById(R.id.btn_gettoken).setOnClickListener(this);
        findViewById(R.id.btn_deletetoken).setOnClickListener(this);
        findViewById(R.id.btn_getpushstatus).setOnClickListener(this);
        findViewById(R.id.btn_setnormal).setOnClickListener(this);
        findViewById(R.id.btn_setnofify).setOnClickListener(this);
        findViewById(R.id.btn_agreement).setOnClickListener(this);

        registerBroadcast();
    }

    /**
     * 获取token | get push token
     */
    private void getToken() {
        showLog("get token: begin");
        HMSAgent.Push.getToken(new GetTokenHandler() {
            @Override
            public void onResult(int rtnCode) {
                showLog("get token: end code=" + rtnCode);
            }
        });
    }

    /**
     * 删除token | delete push token
     */
    private void deleteToken() {
        showLog("deleteToken:begin");
        HMSAgent.Push.deleteToken(token, new DeleteTokenHandler() {
            @Override
            public void onResult(int rst) {
                showLog("deleteToken:end code=" + rst);
            }
        });
    }

    /**
     * 获取push状态 | Get Push State
     */
    private void getPushStatus() {
        showLog("getPushState:begin");
        HMSAgent.Push.getPushState(new GetPushStateHandler() {
            @Override
            public void onResult(int rst) {
                showLog("getPushState:end code=" + rst);
            }
        });
    }

    /**
     * 设置是否接收普通透传消息 | Set whether to receive normal pass messages
     *
     * @param enable 是否开启 | enabled or not
     */
    private void setReceiveNormalMsg(boolean enable) {
        showLog("enableReceiveNormalMsg:begin");
        HMSAgent.Push.enableReceiveNormalMsg(enable, new EnableReceiveNormalMsgHandler() {
            @Override
            public void onResult(int rst) {
                showLog("enableReceiveNormalMsg:end code=" + rst);
            }
        });
    }

    /**
     * 设置接收通知消息 | Set up receive notification messages
     *
     * @param enable 是否开启 | enabled or not
     */
    private void setReceiveNotifyMsg(boolean enable) {
        showLog("enableReceiveNotifyMsg:begin");
        HMSAgent.Push.enableReceiveNotifyMsg(enable, new EnableReceiveNotifyMsgHandler() {
            @Override
            public void onResult(int rst) {
                showLog("enableReceiveNotifyMsg:end code=" + rst);
            }
        });
    }

    /**
     * 显示push协议 | Show Push protocol
     */
    private void showAgreement() {
        showLog("queryAgreement:begin");
        HMSAgent.Push.queryAgreement(new QueryAgreementHandler() {
            @Override
            public void onResult(int rst) {
                showLog("queryAgreement:end code=" + rst);
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_push) {
            // 本页面切换到本页面的按钮事件不处理 | "This page switches to itself" button event does not need to be handled
            return;
        } else if (!onTabBtnClickListener(id)) {
            // 如果不是tab切换按钮则处理业务按钮事件 | Handle Business button events without the TAB toggle button
            switch (id) {
                case R.id.btn_gettoken:
                    getToken();
                    break;
                case R.id.btn_deletetoken:
                    deleteToken();
                    break;
                case R.id.btn_getpushstatus:
                    getPushStatus();
                    break;
                case R.id.btn_setnormal:
                    setReceiveNormalMsg(true);
                    break;
                case R.id.btn_setnofify:
                    setReceiveNotifyMsg(true);
                    break;
                case R.id.btn_agreement:
                    showAgreement();
                    break;
                default:
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HuaweiPushRevicer.unRegisterPushCallback(this);
        ;
    }

    /**
     * 以下代码为sample自身逻辑，和业务能力不相关
     * 作用仅仅为了在sample界面上显示push相关信息
     */
    private void registerBroadcast() {
        HuaweiPushRevicer.registerPushCallback(this);
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            if (b != null && ACTION_TOKEN.equals(action)) {
                token = b.getString(ACTION_TOKEN);
            } else if (b != null && ACTION_UPDATEUI.equals(action)) {
                String log = b.getString("log");
                showLog(log);
            }
        }
    }
}