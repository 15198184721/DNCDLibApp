package cn.cd.dn.sdk.models.prints

import android.util.Log
import cn.cd.dn.sdk.DNSdkConfig
import cn.cd.dn.sdk.DNSdkConfig.Companion.getIns
import com.blankj.utilcode.util.AppUtils

/**
 * @author lcl
 * Date on 2022/5/19
 * Description:
 * 日志输出控制
 */
object DNPrint {

    private val TAG = "DNLog"

    /**
     * 打印Error级别的日志
     */
    fun logE(msg: String) {
        Log.e(TAG, msg)
    }

    /**
     * 打印通用的级别的日志
     */
    fun logV(msg: String) {
        Log.v(TAG, msg)
    }

    /**
     * 打印Info级别的日志
     */
    fun logI(msg: String) {

        Log.i(TAG, msg)
    }

    /**
     * 打印Error级别的日志
     */
    fun logE(tag: String, msg: String) {
        Log.e(tag, msg)
    }

    /**
     * 打印通用的级别的日志
     */
    fun logV(tag: String, msg: String) {
        Log.v(tag, msg)
    }

    /**
     * 打印Info级别的日志
     */
    fun logI(tag: String, msg: String) {

        Log.i(tag, msg)
    }
}