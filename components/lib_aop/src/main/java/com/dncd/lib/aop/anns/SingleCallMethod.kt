package com.dncd.lib.aop.anns

/**
 * @author lcl
 * Date on 2021/9/18
 * Description:
 * 注解作用于方法上。
 *  表示此方法不允许高频重复调用。需要间隔指定后事件才可重复调用
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(value = AnnotationRetention.RUNTIME)
annotation class SingleCallMethod(
    /**
     * 间隔时长，间隔指定的时长内不允许重复调用
     */
    val timeMs:Int = 500
)
