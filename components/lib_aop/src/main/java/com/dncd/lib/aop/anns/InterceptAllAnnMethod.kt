package com.dncd.lib.aop.anns

/**
 * @author lcl
 * Date on 2021/9/18
 * Description:
 * 对方法活在类的所有方法进行拦截。统计等(暂时是为了测试学习使用)
 *  1、注解再方法上只是拦截注解的方法
 *  2、如果应用于class则是对所有方法拦截
 */
@Target(AnnotationTarget.FUNCTION,AnnotationTarget.CLASS)
@Retention(value = AnnotationRetention.RUNTIME)
annotation class InterceptAllAnnMethod()
