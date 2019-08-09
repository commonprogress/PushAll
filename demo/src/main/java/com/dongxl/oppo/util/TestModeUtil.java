/************************************************************
 * Copyright (c) 2016 www.oppo.com Inc. All rights reserved.
 * All rights reserved.
 * <p>
 * Description     : The TestModeUtil for the MCS application
 * History        :( ID, Date, Author, Description)
 * v1.0, 2016-4-22,  kemaoming, create
 ************************************************************/
package com.dongxl.oppo.util;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestModeUtil {

    public static final int TYPE_BASE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_STATUS = 2;
    private static final String LOG_FORMAT = "time ->[tag] msg";//有tag的log格式
    private static final String LOG_FORMAT_NO_TAG = "time -> msg";//没有tag的log格式
    private static final int MAX_LOG_SIZE = 3000;//最大的log条数
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);//log中的时间格式
    private static UpdateTestMode mUpdateTestMode;
    private static ConcurrentHashMap<String, String> statusMap = new ConcurrentHashMap<>();
    private static List<String> mHistoryLog = new CopyOnWriteArrayList<>();//历史log信息
    private static Queue<String> mLogQueue = new ConcurrentLinkedQueue<>();//当前在队列中的log信息
    private static String mStatus = "";

    /**
     * 所有记录的log，比较长
     */
    public static String getLog() {
        StringBuilder sb = new StringBuilder();
        for (String log : mHistoryLog) {
            sb.append(log).append("\n");
        }
        return sb.toString();
    }

    /**
     * 最后的log
     */
    public static String getLastLog() {
        return mLogQueue.poll();
    }

    public static String getStatus() {
        return mStatus.trim();
    }

    public synchronized static void setStatus(String value) {
        if (!TextUtils.isEmpty(value) && !value.equalsIgnoreCase(mStatus)) {
            mStatus = value;
            notifyLogChanged(TYPE_STATUS);
        }
    }

    public static void addLogString(@NonNull String msg) {
        addLogString("", msg);
    }

    public static void addLogString(@NonNull String tag, @NonNull String msg) {
        if (mHistoryLog.size() > MAX_LOG_SIZE) clearLog();
        LogUtil.w(tag, msg);
        String log = formatLog(tag, msg);
        mLogQueue.offer(log);
        mHistoryLog.add(0, log);
        notifyLogChanged(TYPE_LOG);
    }

    private static String formatLog(@NonNull String tag, @NonNull String msg) {
        if (TextUtils.isEmpty(tag) && TextUtils.isEmpty(msg)) return "";
        String FORMAT = TextUtils.isEmpty(tag) ? LOG_FORMAT_NO_TAG : LOG_FORMAT;
        return FORMAT.toLowerCase().
                replaceAll("time", sdf.format(new Date())).
                replaceAll("tag", tag).
                replaceAll("msg", msg);
    }

    public synchronized static void clearLog() {
        mHistoryLog.clear();
        mLogQueue.clear();
        notifyLogChanged(TYPE_LOG);
    }

    public static void setUpdateTestMode(UpdateTestMode updateTestMode) {
        mUpdateTestMode = updateTestMode;
    }

    private static void notifyLogChanged(@LogType int type) {
        if (null != mUpdateTestMode) {
            mUpdateTestMode.onLogUpdate(type);
        }
    }

    @IntDef({TYPE_BASE, TYPE_LOG, TYPE_STATUS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LogType {
    }

    public interface UpdateTestMode {
        void onLogUpdate(@LogType int type);
    }
}
