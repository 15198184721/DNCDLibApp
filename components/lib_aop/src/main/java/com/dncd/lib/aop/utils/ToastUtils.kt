package com.dncd.lib.aop.utils

import android.util.Log
import android.widget.Toast

/**
 * @author lcl
 * Date on 2021/9/18
 * Description:
 */
internal object ToastUtils {
    private val TAG = "AopDNCD"

    /**
     * 输出日志
     * @param msg String
     */
    fun logV(msg: String) {
        Log.v(TAG, msg)
    }

    /**
     * 输出日志
     * @param msg String
     */
    fun logE(msg: String) {
        Log.e(TAG, msg)
    }

    /**
     * 显示提示消息
     * @param msg String
     */
    fun toastMsg(msg: String) {
        AppUtils.getApplication()?.apply {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        }
    }
}