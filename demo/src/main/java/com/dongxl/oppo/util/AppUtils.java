package com.dongxl.oppo.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * AppUtils
 * <ul>
 * <li>{@link AppUtils#isNamedProcess(Context, String)}</li>
 * </ul>
 *
 * @author <invoke href="http://www.trinea.cn" target="_blank">Trinea</invoke> 2014-5-07
 */
public class AppUtils {

    private AppUtils() {
        throw new AssertionError();
    }

    /**
     * whether this process is named with processName
     *
     * @param context
     * @param processName
     * @return <ul>
     * return whether this process is named with processName
     * <li>if context is null, return false</li>
     * <li>if {@link ActivityManager#getRunningAppProcesses()} is null, return false</li>
     * <li>if one process of {@link ActivityManager#getRunningAppProcesses()} is equal to processName, return
     * true, otherwise return false</li>
     * </ul>
     */
    public static boolean isNamedProcess(Context context, String processName) {
        if (context == null) {
            return false;
        }

        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
        if (processInfoList == null || processInfoList.isEmpty()) {
            return false;
        }

        for (RunningAppProcessInfo processInfo : processInfoList) {
            if (processInfo != null && processInfo.pid == pid
                    && processName.equalsIgnoreCase(processInfo.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * whether application is in background
     * <ul>
     * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
     * </ul>
     *
     * @param context
     * @return if application is in background return true, otherwise return false
     */
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * manifest中声明的版本号versionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * manifest中声明的版本号versionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "V1.0";
        }
    }

    /**
     * 获取指定报名的应用的版本号
     *
     * @param context 上下文
     * @param pkgName 包名
     * @return 应用的版本号, 注意是versionCode而不是versionName
     */
    public static int getVersionCode(Context context, String pkgName) {
        int version;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            version = info.versionCode;
        } catch (Exception e) {
            return 1;
        }
        return version;
    }

    /**
     * 获取指定报名的应用的版本名称
     *
     * @param context 上下文
     * @param pkgName 包名
     * @return 应用的版本号, 注意是versionName
     */
    public static String getVersionName(Context context, String pkgName) {
        String version;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            version = info.versionName;
        } catch (Exception e) {
            return "";
        }
        return version;
    }

    /**
     * 获取指定路径的apk的版本号
     *
     * @param context 上下文
     * @param apkPath apk文件在手机的存放地址
     * @return 该应用的版本号, 注意是versionCode而不是versionName
     */
    public static int getApkVersionCode(Context context, String apkPath) {
        int version = 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            version = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }


}
