package com.dongxl.huawei.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.jpushdemo.R;

import java.text.DateFormat;
import java.util.Date;

public abstract class AgentBaseActivity extends Activity implements View.OnClickListener{

    //华为移动服务Client
//    protected HuaweiApiClient client;
//    protected boolean isResolve = false;
//
//    protected static final int REQUEST_HMS_RESOLVE_ERROR = 1000;
//
//    long startTime_connect;
//
//    long endTime_connect;
//
//    long startTime;
//
//    long endTime;
    /**
     * 设置页面切换按钮点击事件
     * Set page toggle button click event
     */
    protected void setTabBtnClickListener() {
        findViewById(R.id.btn_game).setOnClickListener(this);
        findViewById(R.id.btn_iap).setOnClickListener(this);
        findViewById(R.id.btn_id).setOnClickListener(this);
        findViewById(R.id.btn_sns).setOnClickListener(this);
        findViewById(R.id.btn_push).setOnClickListener(this);
        findViewById(R.id.btn_opendevice).setOnClickListener(this);
    }

//    @Override
//    public void onConnected() {
//        endTime_connect = System.currentTimeMillis();
//        System.out.println("connect success执行时间："+(endTime_connect-startTime_connect)+"ms");
//        //华为移动服务client连接成功，在这边处理业务自己的事件
//        //Huawei Mobile Service Client connection successful, handle business own event here
//        HMSAgentLog.i("HuaweiApiClient Connect Successfully!");
//    }

//    @Override
//    public void onConnectionSuspended(int arg0) {
//        //HuaweiApiClient由于异常原因导致断开，如果业务需要继续使用HMS的功能，需要重新连接华为移动服务
//        //Huaweiapiclient is disconnected for exceptional reasons and needs to reconnect Huawei mobile service if business needs to continue using HMS Functionality
//
//        endTime = System.currentTimeMillis();
//        System.out.println("disconnect执行时间："+(endTime-startTime)+"ms");
//        client.connect(this);
//    }

//    @Override
//    public void onConnectionFailed(ConnectionResult arg0) {
//        endTime_connect = System.currentTimeMillis();
//        System.out.println("connect failed执行时间："+(endTime_connect-startTime_connect)+"ms");
//
//        HMSAgentLog.e("HuaweiApiClient Connect Failed!  Error code：" + arg0.getErrorCode());
//
//        if(isResolve) {
//            //如果解决错误的接口已经被调用，并且没有处理完毕，不要重复调用。
//            //Do not repeat the call if the interface that resolved the error has been invoked and has not been processed.
//            return;
//        }
//        if(HuaweiApiAvailability.getInstance().isUserResolvableError(arg0.getErrorCode())) {
//            HMSAgentLog.e("resolveError");
//            HuaweiApiAvailability.getInstance().resolveError(this, arg0.getErrorCode(), REQUEST_HMS_RESOLVE_ERROR);
//            isResolve = true;
//        } else {
//            //其他错误码以及处理方法请参见开发指南-HMS 通用错误码及处理方法。
//            //Other error codes and processing methods see Development Guide-HMS Common error codes and processing methods.
//        }
//    }
    /**
     * 处理页面切换按钮点击事件
     * Process page Toggle Button click event
     * @param btnId 被点击的按钮的id | The ID of the clicked button
     * @return 是否已经处理 | has been processed
     */
    protected boolean onTabBtnClickListener(int btnId) {
//        switch (btnId) {
////            case R.id.btn_game:
////                startActivity(new Intent(this, GameActivity.class));
////                overridePendingTransition(0, 0);
////                finish();
////                return true;
//            case R.id.btn_iap:
//                startActivity(new Intent(this, PayActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//                return true;
//            case R.id.btn_id:
//                startActivity(new Intent(this, HwIDActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//                return true;
//            case R.id.btn_sns:
//                startActivity(new Intent(this, SnsActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//                return true;
//            case R.id.btn_push:
//                startActivity(new Intent(this, HuaweiPushActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//                return true;
//            case R.id.btn_opendevice:
//                startActivity(new Intent(this, OpendeviceActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//                return true;
//            default:
//        }

        return false;
    }

    StringBuffer sbLog = new StringBuffer();
    protected void showLog(String logLine) {
        DateFormat format = new java.text.SimpleDateFormat("MMddhhmmssSSS");
        String time = format.format(new Date());

        sbLog.append(time+":"+logLine);
        sbLog.append('\n');
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                View vText = findViewById(R.id.tv_log);

                if (vText != null && vText instanceof TextView) {
                    TextView tvLog = (TextView)vText;
                    tvLog.setText(sbLog.toString());
                }

                View vScrool = findViewById(R.id.sv_log);
                if (vScrool != null && vScrool instanceof ScrollView) {
                    ScrollView svLog = (ScrollView)vScrool;
                    svLog.fullScroll(View.FOCUS_DOWN);
                }
            }
        });
    }
}
