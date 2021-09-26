package com.dncd.apt.utils

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * @author lcl
 * Date on 2021/9/23
 * Description:
 */
internal object PrintAptUtil {
    /**
     * 打印提示日志
     * @param mMessager
     * @param msg
     */
    fun printV(mMessager: Messager, msg: String) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    /**
     * 打印警告日志
     * @param mMessager
     * @param msg
     */
    fun printW(mMessager: Messager, msg: String) {
        mMessager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    /**
     * 打印错误日志
     * @param mMessager
     * @param msg
     */
    fun printE(mMessager: Messager, msg: String) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, msg);
    }
}