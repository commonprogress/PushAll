package com.dongxl.pushdeme.utils;

import android.util.Log;

import com.dongxl.pushdeme.BuildConfig;


public class LogUtils {

    public static String dufaultTag = "push";

    public static void v(String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        v(dufaultTag, msg);
    }

    public static void v(Class classs, String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        v(classs.getSimpleName(), classs.getSimpleName() + "==" + msg);
    }

    public static void v(String tag, String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (!isOutputLog(tag)) {
            return;
        }
        Log.v(tag, msg);
    }

    public static void d(String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        d(dufaultTag, msg);
    }

    public static void d(Class classs, String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        d(classs.getSimpleName(), classs.getSimpleName() + "==" + msg);
    }

    public static void d(String tag, String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (!isOutputLog(tag)) {
            return;
        }
        Log.d(tag, msg);
    }

    public static void i(String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        i(dufaultTag, msg);
    }

    public static void i(Class classs, String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        i(classs.getSimpleName(), classs.getSimpleName() + "==" + msg);
    }

    public static void i(String tag, String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (!isOutputLog(tag)) {
            return;
        }
        Log.i(tag, msg);
    }

    public static void e(String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        e(dufaultTag, msg);
    }

    public static void e(Class classs, String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        e(classs.getSimpleName(), classs.getSimpleName() + "==" + msg);
    }

    public static void e(String tag, String msg) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (!isOutputLog(tag)) {
            return;
        }
        Log.e(tag, msg);
    }

    /**
     *
     * @param tag
     * @return ture 输出log
     */
    public static boolean isOutputLog(String tag) {
        return !dufaultTag.equals(tag);
//        return true;
    }
}
