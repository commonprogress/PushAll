package com.dongxl.pushdeme.huawei.agent.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 华为数据保存
 */
public class HMSSharedUtils {
    private final static String SHARED_NAME = "huawei_hms";
    private final static String KEY_PUSHTOKEN = "push_token";

    private static SharedPreferences sharedPreferences;

    private static void initSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            if (context == null) {
                throw new RuntimeException("SharedPreferences is not init", new Throwable());
            }
            sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * 保存token
     *
     * @param context
     * @param token
     */
    public static void saveHuaweiToken(Context context, String token) {
        initSharedPreferences(context);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor;
            (editor = sharedPreferences.edit()).putString(KEY_PUSHTOKEN, token);
            editor.apply();
        }
    }

    /**
     * 获取token
     *
     * @param context
     */
    public static String getHuaweiToken(Context context) {
        initSharedPreferences(context);
        if (sharedPreferences != null) {
            return sharedPreferences.getString(KEY_PUSHTOKEN, "");
        }
        return "";
    }
}
