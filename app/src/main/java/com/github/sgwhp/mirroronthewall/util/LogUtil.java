package com.github.sgwhp.mirroronthewall.util;

import android.util.Log;

import com.github.sgwhp.mirroronthewall.model.Constant;

/**
 * Created by robust on 2015/9/23.
 */
public class LogUtil {
    private static final String TAG = "MotW";

    public static void v(String msg){
        if(Constant.DEBUG){
            Log.v(TAG, msg);
        }
    }

    public static void d(String msg){
        if(Constant.DEBUG){
            Log.d(TAG, msg);
        }
    }

    public static void w(String msg){
        if(Constant.DEBUG){
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg){
        if(Constant.DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
