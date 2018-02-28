package com.grace.app.util;

import android.util.Log;

import com.grace.app.BuildConfig;

/**
 * Created by varsovski on 20-Nov-16.
 */

public class LogUtil {

    public static void d(String TAG, String message) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, message);
    }

    public static void e(String TAG, String message) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, message);
    }

    public static void w(String TAG, String message) {
        if (BuildConfig.DEBUG)
            Log.w(TAG, message);
    }

    public static void i(String TAG, String message) {
        if (BuildConfig.DEBUG)
            Log.i(TAG, message);
    }
}
