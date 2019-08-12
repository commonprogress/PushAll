package com.dongxl.push;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dongxl.huawei.demo.HuaweiPushActivity;
import com.dongxl.oppo.component.OppoDemoActivity;
import com.dongxl.xiaomipush.XiaomiMainActivity;
import com.example.jpushdemo.JPushMainActivity;
import com.example.jpushdemo.R;

public class JumpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button xiaomiBtn, huaweiBtn, meizuBtn, oppoBtn, vivoBtn, jpushBtn, otherBtn, ziqiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);
        xiaomiBtn = findViewById(R.id.button_xiaomi);
        xiaomiBtn.setOnClickListener(this);
        huaweiBtn = findViewById(R.id.button_huawei);
        huaweiBtn.setOnClickListener(this);
        meizuBtn = findViewById(R.id.button_meizu);
        meizuBtn.setOnClickListener(this);
        oppoBtn = findViewById(R.id.button_oppo);
        oppoBtn.setOnClickListener(this);
        vivoBtn = findViewById(R.id.button_vivo);
        vivoBtn.setOnClickListener(this);
        jpushBtn = findViewById(R.id.button_jpush);
        jpushBtn.setOnClickListener(this);
        otherBtn = findViewById(R.id.button_other);
        otherBtn.setOnClickListener(this);
        ziqiBtn = findViewById(R.id.button_ziqi);
        ziqiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UWhiteListSetting.enterWhiteListSetting(JumpActivity.this);
                ZiqidongUtils.jumpStartInterface(JumpActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent();
//        Class<?> cls = null;
//        switch (v.getId()) {
//            case R.id.button_xiaomi:
//                cls = XiaomiMainActivity.class;
//                break;
//            case R.id.button_huawei:
//                cls = HuaweiPushActivity.class;
//                break;
//            case R.id.button_meizu:
//                break;
//            case R.id.button_oppo:
//                cls = OppoDemoActivity.class;
//                break;
//            case R.id.button_vivo:
//                break;
//            case R.id.button_jpush:
//                cls = JPushMainActivity.class;
//                break;
//            case R.id.button_other:
//                break;
//            default:
//                break;
//        }
//        if (null != cls) {
//            intent.setClass(this, cls);
//            startActivity(intent);
//        }
    }
}
