package com.dongxl.huawei.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dongxl.huawei.hms.agent.HMSAgent;
import com.dongxl.huawei.hms.agent.sns.Handler.GetMsgSendIntentHandler;
import com.dongxl.huawei.hms.agent.sns.Handler.GetUiIntentHandler;
import com.example.jpushdemo.R;
import com.huawei.hms.support.api.entity.sns.Constants;
import com.huawei.hms.support.api.entity.sns.SNSCode;
import com.huawei.hms.support.api.entity.sns.SnsMsg;

public class SnsActivity extends AgentBaseActivity {

    private static final int REQUEST_GET_UI_INTENT = 1004;
    private static final int REQUEST_GET_SENDMSG_INTENT = 1005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sns);
        setTabBtnClickListener();
        Button btn = (Button) findViewById(R.id.btn_sns);
        if (btn != null) {
            btn.setTextColor(Color.RED);
            btn.setEnabled(false);
        }

        findViewById(R.id.btn_uimsg).setOnClickListener(this);
        findViewById(R.id.btn_uifriend).setOnClickListener(this);
        findViewById(R.id.btn_sendmsg).setOnClickListener(this);
    }


    private void goSnsUI(final int type, final long param){
        showLog("goSnsUI: begin type=" + type + "param=" + param);
        HMSAgent.Sns.getUiIntent(type, param, new GetUiIntentHandler() {
            @Override
            public void onResult(int retnCode, Intent intent) {
                if (intent != null) {
                    SnsActivity.this.startActivityForResult(intent, REQUEST_GET_UI_INTENT);
                } else {
                    if(retnCode == SNSCode.Status.HWID_UNLOGIN) {
                        showLog("goSnsUI:end Failed to start the SNS page because the account is not logged in");
                    } else {
                        showLog("goSnsUI:end error: " + retnCode + " with type=" + type+" param=" + param);
                    }
                }
            }
        });
    }

    private void sendMsg(){
        showLog("sendMsg: begin");
        HMSAgent.Sns.getMsgSendIntent(createSnsMsg(), true, new GetMsgSendIntentHandler() {
            @Override
            public void onResult(int retnCode, Intent intent) {
                if (intent != null) {
                    SnsActivity.this.startActivityForResult(intent, REQUEST_GET_SENDMSG_INTENT);
                } else {
                    if(retnCode == SNSCode.Status.HWID_UNLOGIN) {
                        showLog("sendMsg: Failed to start the SNS page because the account is not logged in");
                    } else {
                        showLog("sendMsg: error: " + retnCode);
                    }
                }
            }
        });
    }

    /**
     * 必须按照指定格式封装社交图文消息 | SNS text messages must be encapsulated in a specified format
     * 消息标题 | Message headers
     * 消息正文 | Message body
     * 消息点击跳转的url，url中可以指定 | Message Click Jump Url,url can specify
     * 消息icon（社交图文消息的icon需要传递图片的二进制数据，并且大小不能超过30K，需要方形或者圆形图片，否则会被截断） | Message icon (SNS graphic message icon needs to pass the binary data of the picture, and the size can not exceed 30K, need square or circular picture, otherwise it will be truncated)
     * 社交的图文消息可以指定打开消息的应用，如不指定，则默认使用浏览器打开，指定方式如下: | SNS messages you can specify an application to open a message, and if not, the default is to open by using a browser, as follows:
     * TargetAppPackageName:应用的包名 | Package name of app
     * TargetAppVersionCode:应用的最低版本号 | Minimum version number of app
     * TargetAppMarketId:该应用在华为应用市场的下载APPID，如果消息接收方没有安装指定APP或者APP版本号未达到最低版本号要求，则社交应用会去应用市场下载指定应用 | The application in the Huawei application market download AppID, if the message receiver does not install the specified app or app version number does not meet the minimum version number requirements, then social applications will be applied to the market download specified application
     * CheckTargetApp:是否要检测以上3个参数指定的应用信息，默认设置为false。如果该参数设置为true，则会去检测消息接收方是否安装指定APP以及APP版本号 | If you want to detect the application information specified by the above 3 parameters, the default setting is False. If this parameter is set to TRUE, the message receiver is detected to install the specified app and app version number
     * @return SnsMsg 社交图文消息 | SNS graphic Message
     * UrlType:目前消息类型不可选 | Currently, the message type is not selectable
     */
    private SnsMsg createSnsMsg()  {

        SnsMsg snsMsg = new SnsMsg();
        String appName = getApplicationName();
        snsMsg.setAppName(appName);
        snsMsg.setCheckTargetApp(false);
        //图文消息的标题 | The caption of a text message
        snsMsg.setTitle("Test text SNS msgs");
        //图文消息的正文 | The body of a text message
        String txt = ((EditText)findViewById(R.id.et_content)).getText().toString();
        snsMsg.setDescription(txt);
        //消息点击跳转URL | Message Click Jump URL
        snsMsg.setUrl("http://www.baidu.com");
        //社交图文消息的icon需要传递图片的二进制数据 | The icon of SNS graphic message needs to pass the binary data of the picture
        //图标的大小不能超过30K | The size of the icon cannot exceed 30K
        //社交图文消息界面只支持正方形的图片，如果图片非正方形图片，则会进行截取，请CP注意处理 | Social graphic Message interface only supports a square picture, if the picture is not a square picture, it will be intercepted, please pay attention to the CP processing
        snsMsg.setLinkIconData(SnsUtil.getBytesFromAssetsFile("logo.png", this));

        return snsMsg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GET_UI_INTENT) {
            if(resultCode == Activity.RESULT_OK) {
                showLog("Start SNS page successfully");
            } else {
                showLog("Failed to start SNS page");
            }
        } else if (requestCode == REQUEST_GET_SENDMSG_INTENT) {
            if(resultCode == Activity.RESULT_OK) {
                showLog("Send Message Succeeded");
            } else {
                showLog("Send Message failed");
            }
        }
    }

    private String getApplicationName() {
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
                if (applicationInfo != null) {
                    return (String) packageManager.getApplicationLabel(applicationInfo);
                }
            }
        } catch (Exception e) {
            showLog("getApplicationName failed");
        }
        return null;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_sns) {
            // 本页面切换到本页面的按钮事件不处理 | "This page switches to itself" button event does not need to be handled
            return;
        } else if (!onTabBtnClickListener(id)) {
            // 如果不是tab切换按钮则处理业务按钮事件 | Handle Business button events without the TAB toggle button
//            switch (id) {
//                case R.id.btn_uimsg:
//                    goSnsUI(Constants.UiIntentType.UI_MSG, 0);
//                    break;
//                case R.id.btn_uifriend:
//                    goSnsUI(Constants.UiIntentType.UI_FRIEND, 0);
//                    break;
//                case R.id.btn_sendmsg:
//                    sendMsg();
//                    break;
//                default:
//            }
        }
    }
}
