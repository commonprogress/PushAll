package com.dongxl.pushdeme.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.Locale;

public class PhoneUtils {

    /**
     * 获取当前手机系统语言地区。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getLanguageCountry() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();

        String language = locale.getLanguage() + "-" + locale.getCountry();
        return language;
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文”，则返回“zh”
     */
    public static String getSystemLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();
        return locale.getLanguage();
    }

    /**
     * 获取当前手机地区。
     *
     * @return 返回当前地区。例如：当前设置的是“中国”，则返回“CN”
     */
    public static String getCurrentCountry() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();

        return locale.getCountry().toUpperCase(Locale.US);
    }

    /**
     * 判断当前是否中国大陆地区
     *
     * @return
     */
    public static Boolean isCNCountry() {
        if ("CN".equals(getCurrentCountry())) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public static boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
            context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        return result;
    }

    /**
     * 方法一：
     *       判断国家是否是国内用户
     *        比如一些联想国行的手机会出现没有插入sim卡，也能够读取到国家代码为cn
     *       @return
     */
    public static boolean isCN(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = tm.getSimCountryIso();
        boolean isCN = false;//判断是不是大陆
        if (!TextUtils.isEmpty(countryIso)) {
            countryIso = countryIso.toUpperCase(Locale.US);
            if (countryIso.contains("CN")) {
                isCN = true;
            }
        }
        return isCN;
    }


    /**
     * 方法二：
     * <p>
     *      查询手机的 MCC+MNC
     */
    private static String getSimOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            return tm.getSimOperator();
        } catch (Exception e) {

        }
        return null;
    }


    /**
     * 因为发现像华为Y300，联想双卡的手机，会返回 "null" "null,null" 的字符串
     */
    private static boolean isOperatorEmpty(String operator) {
        if (operator == null) {
            return true;
        }
        if (operator.equals("") || operator.toLowerCase(Locale.US).contains("null")) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否是国内的 SIM 卡，优先判断注册时的mcc
     */
    public static boolean isChinaSimCard(Context context) {
        String mcc = getSimOperator(context);
        if (isOperatorEmpty(mcc)) {
            return false;
        } else {
            return mcc.startsWith("460");
        }
    }

    /**
     * 判断当前手机的地区
     *
     * @param context
     * @return
     */
    public static boolean isChinaCountry(Context context) {
        if (hasSimCard(context)) {
            return isChinaSimCard(context) && isCN(context);
        } else {
            return isCNCountry();
        }
    }

    /**
     * 官方用法：
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad1(Context context) {
        return (context.getResources().getConfiguration().screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK)
            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 是否具备电话功能判断方法（现在部分平板也可以打电话）：
     *
     * @param activity
     * @return
     */
    public static boolean isPad2(Activity activity) {
        TelephonyManager telephony = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  
     *  * 判断是否为平板 
     *  *  
     *  * @return 
     *  
     */
    private boolean isPad3(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
// 屏幕宽度 
        float screenWidth = display.getWidth();
// 屏幕高度  
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
// 屏幕尺寸  
        double screenInches = Math.sqrt(x + y);
// 大于6尺寸则为Pad  
        if (screenInches >= 6.0) {
            return true;
        }
        return false;
    }
}
