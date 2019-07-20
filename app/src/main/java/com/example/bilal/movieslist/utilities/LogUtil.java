package com.example.bilal.movieslist.utilities;

import android.util.Log;

/**
 * Created by akeel on 24/11/2017.
 */

public class LogUtil {

    private static final String DEBUG_TAG = "LOS";
    private static boolean logsEnabled =true;

    public static void e(String msg) {
        if (logsEnabled) {
            Log.e(DEBUG_TAG, msg);
        }
    }

    public static void v(String msg) {
        if (logsEnabled) {
            Log.v(DEBUG_TAG, msg);
        }
    }

    public static void i(String msg) {
        if (logsEnabled) {
            Log.i(DEBUG_TAG, msg);
        }
    }

    public static void w(String msg) {
        if (logsEnabled) {
            Log.w(DEBUG_TAG, msg);
        }
    }

    public static void d(String msg) {
        if (logsEnabled) {
            Log.d(DEBUG_TAG, msg);
        }
    }
}
