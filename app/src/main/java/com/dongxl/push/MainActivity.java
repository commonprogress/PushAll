package com.dongxl.push;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dongxl.push.service.MyJobService;
import com.dongxl.push.uitls.UWhiteListSetting;
import com.dongxl.pushdeme.PushRegisterSet;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bottom1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushRegisterSet.registerInitPush(MainActivity.this);
            }
        });
        findViewById(R.id.bottom2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UWhiteListSetting.enterWhiteListSetting(MainActivity.this);
//                ZiqiManager.jumpStartInterface(MainActivity.this);
            }
        });
        checkRequestPermissions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //限制应用时，确保 ActivityManager.isBackgroundRestricted()返回 true。
//             ActivityManager.isBackgroundRestricted();
        }
    }

    /**
     * 检测和获取权限
     * 在非MIUI平台下，如果targetSdkVersion>=23，需要动态申请电话和存储权限，请在申请权限后再调用注册接口，否则会注册失败。
     */
    private void checkRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permissions[] = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_PHONE_STATE};
            for (int i = 0; i < permissions.length; ++i) {
                if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, PERMISSION_REQUEST);
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            boolean granted = false;
            for (int i = 0; i < grantResults.length; ++i) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                }
            }
            if (granted) {
                Log.w("PermissionActivity", "Permissions granted:");
//                PushRegisterSet.registerInitPush(MainActivity.this);
            }
//            finish();
        }
    }
    private int mJobId = 0;
    private void sss(){
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(MainActivity.this, MyJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(++mJobId, componentName);

    }
}
