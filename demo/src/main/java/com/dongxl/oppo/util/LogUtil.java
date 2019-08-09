/************************************************************
 * Copyright (c) 2016 www.oppo.com Inc. All rights reserved.
 * All rights reserved.
 *
 * Description     : The LogUtil for the MCS application
 * History        :( ID, Date, Author, Description)
 * v1.0, 2016-4-22,  kemaoming, create
 ************************************************************/
package com.dongxl.oppo.util;

import android.util.Log;

public class LogUtil {
    public static final String TAG = "com.coloros.opush---";

    private static String special = "Opush";
    private static boolean V = false;
    private static boolean I = false;
    private static boolean D = true;
    private static boolean W = true;
    private static boolean E = true;
    private static String seprateor = "-->";
    private static boolean isDebug = true;

    /**
     * When get catch the exception, print the exception info.
     *
     * @param e
     */
    public static void e(String tag, Throwable e) {
        if (E) {
            Log.e(tag, e.toString());
        }
    }

    public static void e(Exception e) {
        if (E) {
            e.printStackTrace();
        }
    }

    public static void v(String tag, String debugInfo) {
        if (V && isDebug) {
            Log.v(tag, special + seprateor + debugInfo);
        }
    }

    public static void d(String tag, String debugInfo) {
        if (D && isDebug) {
            Log.d(tag, special + seprateor + debugInfo);
        }
    }

    public static void i(String tag, String debugInfo) {
        if (I && isDebug) {
            Log.i(tag, special + seprateor + debugInfo);
        }
    }

    public static void w(String tag, String debugInfo) {
        if (W && isDebug) {
            Log.w(tag, special + seprateor + debugInfo);
        }
    }

    public static void e(String tag, String debugInfo) {
        if (E && isDebug) {
            Log.e(tag, special + seprateor + debugInfo);
        }
    }

    public static void v(String debugInfo) {
        if (V && isDebug) {
            Log.v(TAG, special + seprateor + debugInfo);
        }
    }

    public static void d(String debugInfo) {
        if (D && isDebug) {
            Log.d(TAG, special + seprateor + debugInfo);
        }
    }

    public static void i(String debugInfo) {
        if (I && isDebug) {
            Log.i(TAG, special + seprateor + debugInfo);
        }
    }

    public static void w(String debugInfo) {
        if (W && isDebug) {
            Log.w(TAG, special + seprateor + debugInfo);
        }
    }

    public static void e(String debugInfo) {
        if (E && isDebug) {
            Log.e(TAG, special + seprateor + debugInfo);
        }
    }

    public static String getSpecial() {
        return special;
    }

    public static void setSpecial(String special) {
        LogUtil.special = special;
    }

    public static boolean isV() {
        return V;
    }

    public static void setV(boolean v) {
        V = v;
    }

    public static boolean isD() {
        return D;
    }

    public static void setD(boolean d) {
        D = d;
    }

    public static boolean isI() {
        return I;
    }

    public static void setI(boolean i) {
        I = i;
    }

    public static boolean isW() {
        return W;
    }

    public static void setW(boolean w) {
        W = w;
    }

    public static boolean isE() {
        return E;
    }

    public static void setE(boolean e) {
        E = e;
    }

    public static void setDebugs(boolean b) {
        isDebug = b;
        if (isDebug) {
            V = true;
            D = true;
            I = true;
            W = true;
            E = true;
        } else {
            V = false;
            D = false;
            I = false;
            W = false;
            E = false;
        }
    }

    public static boolean isDebugs() {
        return isDebug;
    }

    public static String getSeprateor() {
        return seprateor;
    }

    public static void setSeprateor(String seprateor) {
        LogUtil.seprateor = seprateor;
    }

}
