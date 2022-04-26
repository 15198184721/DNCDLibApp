package com.dn.apt.annotations.notify

/**
 * @author lcl
 * Date on 2021/9/23
 * Description:
 *  桌面通知模块。向外部同步数据的方法,再内部保存数据时候向外同步给此注解的方法
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DesktopNotifyUI5NumberSync(
    /** 参数类型 */
    val paramsTypeClass:String,
)
