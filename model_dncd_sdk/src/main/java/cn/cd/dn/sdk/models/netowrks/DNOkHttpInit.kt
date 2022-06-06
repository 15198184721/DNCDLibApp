package cn.cd.dn.sdk.models.netowrks

import android.content.Context
import cn.cd.dn.sdk.CInitProvider
import cn.cd.dn.sdk.ISyncTheUpperTask
import cn.cd.dn.sdk.models.prints.DNPrint
import cn.cd.dn.sdk.models.utils.ClassLoaderManager
import cn.cd.dn.sdk.models.utils.DNServiceTimeManager
import com.blankj.utilcode.util.ReflectUtils
import com.chat_hook.HookMethodCall
import com.chat_hook.HookMethodCallParams
import com.chat_hook.HookMethodHelper
import com.chat_hook.HookMethodParams
import okhttp3.OkHttpClient
import java.lang.Exception
import java.lang.reflect.Field
import kotlin.jvm.Throws

/**
 * @author lcl
 * Date on 2022/5/18
 * Description:
 * 多牛模块OkHttp相关的初始化
 */
object DNOkHttpInit : ISyncTheUpperTask {
    /**
     * 全局唯一通用的OkHttp网络请求对象
     */
    private var dnOkHttpBuilder: OkHttpClient.Builder? = null

    /**
     * 获取多牛OkHttp网络通讯对象
     * @return OkHttpClient
     */
    fun getDNOkHttp(): OkHttpClient {
        if (dnOkHttpBuilder == null) {
            dnOkHttpBuilder = OkHttpClient.Builder()
        }
        return dnOkHttpBuilder!!.build()
    }

    /**
     * 获取 GET 请求的请求拼接参数
     * @return 上层的拼接参数(拼接再Get请求后的参数)
     */
    fun getDnCommonJson(): String {
        return try {
            var jsonUtilsCzz = Class.forName("com.donews.utilslibrary.utils.JsonUtils")
            val getCommonJsonMethod = jsonUtilsCzz.getMethod("getCommonJson", Boolean::class.java)
                ?: jsonUtilsCzz.getDeclaredMethod("getCommonJson", Boolean::class.java)
            getCommonJsonMethod.isAccessible = true
            var any = getCommonJsonMethod.invoke(null, false)
            any as? String ?: ""
        } catch (e: Throwable) {
            ""
        }
    }

    /**
     * 调用同步上层任务接口 (此方法只允许[CInitProvider]进行调用)
     * @param context Context
     */
    override fun sync(context: Context) {
        val stackTrace = Thread.currentThread().stackTrace
        var isAllowCall = false
        for (stackTraceElement in stackTrace) {
            if (CInitProvider::class.java.name.equals(stackTraceElement.className)) {
                isAllowCall = true
                break
            }
        }
        if (!isAllowCall) {
            throw NullPointerException(
                "非法调用:sync() method Only allow ${CInitProvider::class.java.name} Call！\n" +
                        "current Call The Is ${stackTrace[3].className}！"
            )
        }
        try {
            DNPrint.logI("attch application okHttpClientBuilder start !")
            val esayHttpClzz: Class<*> = Class.forName("com.donews.network.EasyHttp")
            HookMethodHelper.addHookConstructorMethod(
                HookMethodParams(
                    esayHttpClzz, null, null, object : HookMethodCall {
                        var isFinishAttch = false
                        override fun afterHookedMethod(param: HookMethodCallParams?) {
                            if (!isFinishAttch && param?.getThisObject() != null) {
                                try {
                                    isFinishAttch = true
                                    val okHttpClientBuilderField: Field = try {
                                        esayHttpClzz.getField("okHttpClientBuilder");
                                    } catch (e1: Exception) {
                                        esayHttpClzz.getDeclaredField("okHttpClientBuilder");
                                    }
                                    okHttpClientBuilderField.isAccessible = true
                                    val okBuilder =
                                        okHttpClientBuilderField.get(param.getThisObject())
                                    if (okBuilder is OkHttpClient.Builder) {
                                        dnOkHttpBuilder = okBuilder
                                        DNPrint.logI("attch application okHttpClientBuilder success,Builder=$dnOkHttpBuilder !!")
                                        syncApplicationNetworkRequest()
                                        return
                                    }
                                    DNPrint.logI("attch application okHttpClientBuilder cancel,not fund Object")
                                } catch (tr: Throwable) {
                                    DNPrint.logI("attch application okHttpClientBuilder error,errMsg=$tr")
                                }
                            }
                        }
                    })
            )
        } catch (e: Throwable) {
            DNPrint.logI("attch application okHttpClientBuilder failure msg=$e")
        }
    }

    /**
     * 使用上层的网络请求框架。再次统一拉取一次数据
     */
    private fun syncApplicationNetworkRequest() {
        // 使用上层网络框架更新一次服务器时间
        DNServiceTimeManager.getIns().updateLocalTime()
    }
}