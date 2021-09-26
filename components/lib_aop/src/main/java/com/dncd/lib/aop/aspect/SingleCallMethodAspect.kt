package com.dncd.lib.aop.aspect

import android.os.Build
import androidx.annotation.RequiresApi
import com.dncd.lib.aop.anns.SingleCallMethod
import com.dncd.lib.aop.utils.ToastUtils
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

import org.aspectj.lang.ProceedingJoinPoint

import org.aspectj.lang.annotation.Around
import java.lang.Exception
import java.lang.reflect.Method


/**
 * @author lcl
 * Date on 2021/9/18
 * Description: 侵入式切面：
 *
 *  [SingleCallMethod]的注解切面处理
 *  防止被注解的方法被高频调用,每次调用后必须间隔指定时间后才能再次调用,默认500ms（常用于点击防抖）
 */
@Aspect
internal class SingleCallMethodAspect {
    companion object {
        //切入点
        private const val POINTCUT_METHOD =
            "execution(@com.dncd.lib.aop.anns.SingleCallMethod * *(..))"
    }

    //
    private val mRecodMap: MutableMap<String, Long> = mutableMapOf()

    @Pointcut(POINTCUT_METHOD)
    private fun methodPointcut() {
    }

    @Around("methodPointcut()")
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint) {
        try {
            if (joinPointCheck(joinPoint)) {
                ToastUtils.logV(
                    "@SingleCallMethod aop Verification not through, recordMap.size = ${mRecodMap.size}"
                )
                return //高频调用了。直接返回
            }
            ToastUtils.logV("@SingleCallMethod aop Verification through, recordMap.size = ${mRecodMap.size}")
            //old method
            joinPoint.proceed()
        } catch (e: Exception) {
            //出现异常 不拦截点击事件
            ToastUtils.logV("aop出现异常,继续执行:$e")
            //old method
            joinPoint.proceed()
        }
    }

    /**
     * 检查是否高频调用
     * @return
     *  T:高频调用了。直接返回
     *  F:没有高频调用。正常执行原方法
     */
    private fun joinPointCheck(joinPoint: ProceedingJoinPoint): Boolean {
        //查找方法
        val signature = joinPoint.signature as MethodSignature
        val method: Method? = signature.method

        //检查是否有注解
        val hasAnnotation = method != null && method.isAnnotationPresent(
            SingleCallMethod::class.java
        )
        if (hasAnnotation) {
            val singleCallMethodAnn: SingleCallMethod =
                method!!.getAnnotation(SingleCallMethod::class.java)
            //函数签名作为key
            val key = method.toString()
            val curTime = System.currentTimeMillis()
            if (mRecodMap[key] == null) {
                //更新或者刷新当前的操作数据
                mRecodMap[key] = curTime
                return false //没有高频调用
            } else {
                if (curTime - mRecodMap[key]!! < singleCallMethodAnn.timeMs) {
                    return true //高频调用了。直接返回
                }
            }
            cleanMap(curTime,singleCallMethodAnn.timeMs)
            //更新或者刷新当前的操作数据
            mRecodMap[key] = curTime
        }
        return false
    }

    /**
     * 清理记录数据信息的Map
     * @param curTime
     * @param stepTime Int
     */
    private fun cleanMap(curTime:Long,stepTime:Int) {
        if(mRecodMap.size < 200){
            return //小于200就不清理,避免高频调用
        }
        //清理无用的过期数据。防止集合过于庞大
        val tMap = mRecodMap.filter {
            curTime - it.value < stepTime
        }
        mRecodMap.clear()
        mRecodMap.putAll(tMap)
    }
}