package com.grace.app.util;

import android.widget.Toast;

import com.grace.app.BuildConfig;
import com.grace.app.GraceApplication;

/**
 * Created by varsovski on 25-Dec-16.
 */

public class ToastUtil {

    public static void showToast(String message, int duration) {
        if (BuildConfig.DEBUG)
            Toast.makeText(GraceApplication.getAppComponent().getApp(), message, duration).show();
    }
}
