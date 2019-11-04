package com.dongxl.push.service;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.dongxl.pushdeme.utils.LogUtils;

/**
 * https://www.jianshu.com/p/a23df3eeb245
 * https://www.jianshu.com/p/c3290ff1520a
 */
public class MyJobIntentService extends JobIntentService {

    private static final String TAG = MyJobIntentService.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG, "==onCreate==");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        LogUtils.i(TAG, "==onHandleWork==");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "==onDestroy==");
    }
}
