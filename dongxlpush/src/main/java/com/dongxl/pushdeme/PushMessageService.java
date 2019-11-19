package com.dongxl.pushdeme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class PushMessageService extends JobIntentService {
    /**
     * 这个Service 唯一的id
     */
    static final int JOB_ID = 10111;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, ComponentName componentName, Intent intent) {
        enqueueWork(context, componentName, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }
}
