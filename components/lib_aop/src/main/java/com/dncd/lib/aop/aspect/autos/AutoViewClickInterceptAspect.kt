package com.dncd.lib.aop.aspect.autos

import android.view.View
import com.dncd.lib.aop.anns.InterceptAllAnnMethod
import com.dncd.lib.aop.utils.ToastUtils
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

import org.aspectj.lang.ProceedingJoinPoint

import org.aspectj.lang.annotation.Around
import java.lang.Exception
import java.lang.reflect.Method
import com.dncd.lib.aop.utils.AppUtils
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Before


/**
 * @author lcl
 * Date on 2021/9/18
 * Description: 非侵入式切面：
 *
 *  View的click方法自动拦截，不需要配合注解。自动拦截方法
 */
@Aspect
internal class AutoViewClickInterceptAspect {

    @Pointcut("execution(* android.view.View.OnClickListener+.onClick(..))")
    fun onClickPointcut() {
    }

    @Around("onClickPointcut()")
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint) {
        ToastUtils.logV(" -------> click intercept from Aop")
        //执行原方法
        joinPoint.proceed()
    }
}