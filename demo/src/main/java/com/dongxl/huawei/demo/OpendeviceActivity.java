package com.dongxl.huawei.demo;

import android.app.PendingIntent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dongxl.huawei.hms.agent.HMSAgent;
import com.dongxl.huawei.hms.agent.opendevice.handler.GetOaidHandler;
import com.dongxl.huawei.hms.agent.opendevice.handler.GetOdidHandler;
import com.example.jpushdemo.R;
import com.huawei.hms.support.api.opendevice.OaidResult;
import com.huawei.hms.support.api.opendevice.OdidResult;

public class OpendeviceActivity extends AgentBaseActivity {

    /**
     * 拉起oaid设置界面的intent
     */
    private PendingIntent pendingIntentOaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opendevice);
        setTabBtnClickListener();
        Button btn = (Button) findViewById(R.id.btn_opendevice);
        if (btn != null) {
            btn.setTextColor(Color.RED);
            btn.setEnabled(false);
        }

        findViewById(R.id.btn_Oaid).setOnClickListener(this);
        findViewById(R.id.btn_Oaid).setOnClickListener(this);
    }


    private void getOaid(){
        showLog("getOaid: begin");
        HMSAgent.OpenIdentifier.getOaid(new GetOaidHandler() {
            @Override
            public void onResult(int retnCode, OaidResult oaidResult) {
                if (oaidResult != null) {
                    showLog("getOaid:oaid=" + oaidResult.getId()
                            + " \n\tsettingIntent=" + oaidResult.getSettingIntent()
                            + " \n\tisTrackLimited=" + oaidResult.isTrackLimited()
                            + " \n\tstatus=" + oaidResult.getStatus());
                    updateOaidSettingBtn(oaidResult);
                } else {
                    showLog("getOaid:end error: " + retnCode);
                }
            }
        });
    }

    private void updateOaidSettingBtn(OaidResult oaidResult) {
        pendingIntentOaid = oaidResult.getSettingIntent();
        Button btnOaidSetting = (Button) findViewById(R.id.btn_oaid_setting);
        if (btnOaidSetting != null) {
            if (pendingIntentOaid != null) {
                btnOaidSetting.setVisibility(View.VISIBLE);
                btnOaidSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            pendingIntentOaid.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                btnOaidSetting.setVisibility(View.GONE);
            }
        }
    }

    private void getOdid(){
        showLog("getOdid: begin");
        HMSAgent.OpenIdentifier.getOdid(new GetOdidHandler() {
            @Override
            public void onResult(int retnCode, OdidResult odidResult) {
                if (odidResult != null) {
                    showLog("getOdid:odid=" + odidResult.getId()
                            + " \n\tstatus=" + odidResult.getStatus());
                } else {
                    showLog("getOdid:end error: " + retnCode);
                }
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
        if (id == R.id.btn_opendevice) {
            // 本页面切换到本页面的按钮事件不处理 | "This page switches to itself" button event does not need to be handled
            return;
        } else if (!onTabBtnClickListener(id)) {
            // 如果不是tab切换按钮则处理业务按钮事件 | Handle Business button events without the TAB toggle button
            switch (id) {
                case R.id.btn_Oaid:
                    getOaid();
                    break;
//                case R.id.btn_Odid:
//                    getOdid();
//                    break;
                default:
            }
        }
    }
}
