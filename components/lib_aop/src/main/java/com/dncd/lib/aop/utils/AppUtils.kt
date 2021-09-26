package com.dncd.lib.aop.utils

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import java.lang.Exception

/**
 * @author lcl
 * Date on 2021/9/18
 * Description:
 */
internal object AppUtils {

    private var applicationContext: Application? = null;

    /**
     * 获取全局的Application
     * @return null 表示未获取到
     */
    fun getApplication(): Application? {
        if (applicationContext != null) return applicationContext
        try {
            val clzz = Class.forName("android.app.ActivityThread")
            val method = clzz.getDeclaredMethod("currentApplication") ?: return null
            applicationContext = method.invoke(null) as Application
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return applicationContext
    }

    /**
     * 判断当前应用是否是debug状态
     * @param context Context
     * @return T:是debug版本，F:不是debug版本
     */
    fun isApkInDebug(): Boolean {
        return try {
            val info: ApplicationInfo = getApplication()!!.applicationInfo
            info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: Exception) {
            false
        }
    }
}