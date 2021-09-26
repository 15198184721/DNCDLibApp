package com.dncd.lib.aop.aspect

import com.dncd.lib.aop.anns.InterceptAllAnnMethod
import com.dncd.lib.aop.anns.SingleCallMethod
import com.dncd.lib.aop.utils.ToastUtils
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

import org.aspectj.lang.ProceedingJoinPoint

import org.aspectj.lang.annotation.Around
import java.lang.Exception
import java.lang.reflect.Method
import com.dncd.lib.aop.utils.AppUtils


/**
 * @author lcl
 * Date on 2021/9/18
 * Description: 侵入式切面：
 *
 *  [InterceptAllAnnMethod]的注解切面处理
 *  左右就是对指定方法或者所有方法进行统计
 */
@Aspect
internal class InterceptAllAnnMethodAspect {

    /**
     * 注解class表示被注解的所有方法都会通知到此方法
     * 作用于方法的话则是当前方法调用通知到此方法
     */
    @Pointcut("within(@com.dncd.lib.aop.InterceptAllAnnMethod *)")
    fun withinAnnotatedClass() {
    }

    /**
     * 表示所有非内部类的方法并且被注解的方法。
     */
    // synthetic 是内部类编译后添加的修饰语，所以 !synthetic 表示非内部类的
    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    fun methodInsideAnnotatedType() {
    }

    //    @DeclareMixin
    @Around("methodInsideAnnotatedType()")
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint) {
        try {
            //查找方法
            val signature = joinPoint.signature as MethodSignature
            val method: Method? = signature.method

            //检查是否有注解
            var hasAnnotation = method != null && method.isAnnotationPresent(
                InterceptAllAnnMethod::class.java
            )
            if (hasAnnotation) {
                val exceptionCapture: InterceptAllAnnMethod =
                    method!!.getAnnotation(InterceptAllAnnMethod::class.java)
            } else {
                //如果方法上没有注解。则去所属的Class上检查
                hasAnnotation = method != null && method.declaringClass.isAnnotationPresent(
                    InterceptAllAnnMethod::class.java
                )
                if (hasAnnotation) {
                    val exceptionCapture: InterceptAllAnnMethod =
                        method!!.declaringClass.getAnnotation(InterceptAllAnnMethod::class.java)
                }
            }
            if (hasAnnotation) {
                if (AppUtils.isApkInDebug()) {
                    ToastUtils.logV(
                        "${InterceptAllAnnMethod::class.simpleName}方法已经执行：[${method!!.name}")
                }
            }
        } catch (e: Exception) {
            //出现异常 不拦截点击事件
            ToastUtils.logV("aop出现异常,继续执行:$e")
        }
        //没有找到注解或者执行错误。那么执行原方法
        joinPoint.proceed()
    }
}