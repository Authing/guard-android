package cn.authing.guard.util;

import android.util.Log;

public class ALog {
    public static void d(String TAG, String message) {
        Log.d(TAG, message);
    }

    public static void i(String TAG, String message) {
        Log.i(TAG, message);
    }

    public static void w(String TAG, String message) {
        Log.w(TAG, message);
    }

    public static void e(String TAG, String message) {
        Log.e(TAG, message);
    }

    public static void d(String TAG, String message, Throwable tr) {
        ALog.d(TAG, message);
    }

    public static void i(String TAG, String message, Throwable tr) {
        ALog.i(TAG, message);
    }

    public static void w(String TAG, String message, Throwable tr) {
        ALog.w(TAG, message);
    }

    public static void e(String TAG, String message, Throwable tr) {
        ALog.e(TAG, message);
    }
}
