package com.dn.apt.annotations.notify

/**
 * @author lcl
 * Date on 2021/9/23
 * Description:
 *  多牛向桌面通知模块同步用户信息的方法。使用方式如下：
 *  1、再更新用户信息的方法上加上此注解即可
 *  2、或者新建一个方法,然后给其加上此注解。然后调用此方法即可
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DesktopNotifyUserInfoSync
