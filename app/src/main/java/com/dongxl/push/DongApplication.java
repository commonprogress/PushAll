package com.dongxl.push;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.dongxl.pushdeme.PushRegisterSet;

import java.util.List;

public class DongApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!shouldInit()) {
            return;
        }
        PushRegisterSet.applicationInit(this);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
