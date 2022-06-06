package cn.cd.dn.sdk.models.events

import cn.cd.dn.sdk.DNSdkConfig
import cn.cd.dn.sdk.kexts.ioToMainScheduler
import cn.cd.dn.sdk.kexts.subscribeHttp
import cn.cd.dn.sdk.models.events.beans.DynamicEventResp
import cn.cd.dn.sdk.models.netowrks.DNOkHttpInit
import cn.cd.dn.sdk.models.prints.DNPrint
import cn.cd.dn.sdk.models.utils.handler.BackgroundsTask
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.chat_hook.HookMethodCall
import com.chat_hook.HookMethodCallParams
import com.chat_hook.HookMethodHelper
import com.chat_hook.HookMethodParams
import io.reactivex.Observable
import okhttp3.Request
import java.lang.Exception
import java.lang.reflect.Method
import java.nio.charset.Charset
import java.util.*

/**
 * @author lcl
 * Date on 2022/5/30
 * Description:
 *  动态注册事件相关的管理类
 */
class DynamicEventsManager {

    companion object {
        //配置缓存的存储文件
        private val DYNAMIC_EVENT_CONFIG_CACHE_FILE = "DYNAMICEVENTCONFIGCACHEFILE"

        //配置缓存的存储key
        private val DYNAMIC_EVENT_CONFIG_CACHE_KEY = "DYNAMICEVENTCONFIGCACHEKey"

        //是否初始化
        private var isInit = false

        //单利对象
        var s_DynamicEventsManager = DynamicEventsManager()

        /**
         * 获取单利对象
         * @return DynamicEventsManager
         */
        fun getIns(): DynamicEventsManager {
            return s_DynamicEventsManager
        }
    }

    //延迟加载任务
    private val postDayTask = Runnable {
        updateConfig()
    }

    // 当前正在执行中的动态事件任务
    private val currentInvokDynamicEventTask: MutableMap<String, DynamicEventResp> = mutableMapOf()

    /**
     * 获取最近一次成功的配置
     * @return 最近一次成功的缓存配置
     */
    fun getSaveConfig(): String? {
        return SPUtils.getInstance(DYNAMIC_EVENT_CONFIG_CACHE_FILE)
            .getString(DYNAMIC_EVENT_CONFIG_CACHE_KEY, null)
    }

    /**
     * 获取指定方法的唯一iID
     * @param clazz class对象
     * @param method 方法对象
     * @param isOverloading 是否重载。T:需要添加参数。F:不区分。不包括参数信息
     * @return 此方法的唯一标记。会通过这个去进行查找相关数据
     */
    fun getQueId(clazz: Class<*>, method: Method, isOverloading: Boolean): String {
        val sb = StringBuffer("${clazz.name}#${method.name}_")
        if (isOverloading) {
            method.parameterTypes.forEach {
                sb.append(it.name)
            }
        }
        return UUID.nameUUIDFromBytes(
            sb.toString().toByteArray(Charset.forName("UTF-8"))
        ).toString().replace("-", "")
    }

    /**
     * 更新配置,重新获取配置并处理(如果删除了某个配置。那么需要下次重启生效)
     */
    fun updateConfig() {
        DNSdkConfig.getIns().getEventNotifyReportListener() ?: return
        DNPrint.logE(">> Dynamic Events config load start...")
        //测试代码
//        var eventList1: MutableList<DynamicEventResp>? = mutableListOf()
//        var resp = DynamicEventResp()
//        resp.eventId = "2222"
//        resp.methodName = "com.dncdlibapp.MainActivity#BBB"
//        resp.paramsTypes = mutableListOf("int")
//        eventList1?.add(resp)
//        buildConfigParams(eventList1)
//        return
        //移除其他延迟任务
        BackgroundsTask.getIns().getHandler().removeCallbacks(postDayTask)
        Observable.just(
            DNSdkConfig.getIns().getEventNotifyReportListener()!!.getEventDynamicReportConfigUrl() +
                    DNOkHttpInit.getDnCommonJson()
        )
            .map { url ->
                val request =
                    Request.Builder()
                        .url(url)
                        .build()
                val response = DNOkHttpInit.getDNOkHttp().newCall(request).execute()
                var eventList: MutableList<DynamicEventResp>? = mutableListOf()
                return@map try {
                    eventList = if (response.isSuccessful) {
                        val jsonResp: String = response.body?.string() ?: return@map eventList
                        saveConfig(jsonResp)
                        GsonUtils.fromJson(jsonResp, eventList!!::class.java)
                    } else {
                        val jsonResp: String = getSaveConfig() ?: return@map eventList
                        GsonUtils.fromJson(jsonResp, eventList!!::class.java)
                    }
                    eventList
                } catch (e: Throwable) {
                    eventList
                }
            }
            .ioToMainScheduler()
            .subscribeHttp(succ = {
                DNPrint.logE(">> Dynamic Events config download success")
                buildConfigParams(it)
            }, err = {
                //如果出错。那么一分钟后再试
                BackgroundsTask.getIns().postDelayed(postDayTask, 60 * 1000L)
                var eventList: MutableList<DynamicEventResp>? = mutableListOf()
                val jsonResp: String? = getSaveConfig()
                if (jsonResp != null) {
                    try {
                        GsonUtils.fromJson(jsonResp, eventList!!::class.java)
                        buildConfigParams(eventList)
                        DNPrint.logE(">> Dynamic Events config download error ,get cache success")
                    } catch (e: Throwable) {
                        DNPrint.logE(">> Dynamic Events config download error ,get cache data errMsg = $e")
                    }
                } else {
                    DNPrint.logE(">> Dynamic Events config download error msg = $it")
                }
            })
    }

    /**
     * 初始化操作，如果已经初始化操作了那么只是会进行刷新数据
     * 注意：此方法为内部关联初始化
     */
    internal fun init() {
        if (isInit) {
            return
        }
        isInit = true
        updateConfig()
    }

    /**
     * 构建参数
     * @param eventListConfig MutableList<DynamicEventResp>?
     */
    private fun buildConfigParams(eventListConfig: MutableList<DynamicEventResp>?) {
        DNPrint.logE(">> Dynamic Events config build start")
        //以最新的数据为准
        currentInvokDynamicEventTask.clear()
        eventListConfig?.forEach { item ->
            if (currentInvokDynamicEventTask[item.getQueId()] == null) {
                currentInvokDynamicEventTask[item.getQueId()] = item
                addDynamicEvent(item)
            }
        }
        DNPrint.logE(">> Dynamic Events config build finish")
    }

    //添加动态事件
    private fun addDynamicEvent(item: DynamicEventResp) {
        try {
            val methodpara: List<String>? = item.methodName?.split("#")
            val clazz = Class.forName(methodpara!![0])
            val methods: MutableList<Method> = mutableListOf()
            val methodsTem: MutableList<Method> = mutableListOf()
            // 只根据方法名称查询(私有加公有的)
            methodsTem.addAll(clazz.declaredMethods)
            methodsTem.forEach {
                if (it.name.equals(methodpara[1])) {
                    //筛选出所有的方法
                    methods.add(it)
                }
            }
            //重置数据为指定方法的所有。满足条件的方法
            methodsTem.clear()
            methodsTem.addAll(methods)
            methods.clear()
            // 是否区分重载
            var isOverloading = false
            //进一步删选
            if (item.paramsTypes != null && item.paramsTypes!!.isNotEmpty()) {
                isOverloading = true
                //根据参数类型进行再次筛选
                for (method in methodsTem) {
                    if (item.paramsTypes!!.size != method.parameterTypes.size) {
                        continue
                    }
                    var isAdd = true
                    for (i in 0 until item.paramsTypes!!.size) {
                        try {
                            if (method.parameterTypes[i].name != item.paramsTypes!![i]) {
                                // 只要有一个参数不相等。那么移除指定方法
                                isAdd = false
                                break
                            }
                        } catch (e1: Exception) {
                            isAdd = false
                            break
                        }
                    }
                    if (isAdd) {
                        // 完全符合的的方法
                        methods.add(method)
                    }
                }
            } else {
                methods.addAll(methodsTem)
                methodsTem.clear()
            }
            //开始处理业务逻辑
            for (method in methods) {
                val fMethod = method
                val isQfOvers = isOverloading
                //开始处理逻辑
                HookMethodHelper.addHookMethod(
                    HookMethodParams(
                        clazz,
                        fMethod.name,
                        if (fMethod.parameterTypes.isEmpty())
                            null
                        else
                            fMethod.parameterTypes.toList().toTypedArray(),
                        object : HookMethodCall {
                            override fun afterHookedMethod(param: HookMethodCallParams?) {
                                currentInvokDynamicEventTask[getQueId(
                                    clazz,
                                    fMethod,
                                    isQfOvers
                                )]?.apply {
                                    this.eventId ?: return@apply
                                    DNSdkConfig.getIns().getEventNotifyReportListener()
                                        ?.eventReport(
                                            DNSdkConfig.getIns().appContext,
                                            this.eventId!!,
                                            mutableMapOf()
                                        )
                                }
                            }
                        })
                )
            }
        } catch (e: Throwable) {
            DNPrint.logE(">> Dynamic Events add error e=$e")
        }
    }

    /**
     * 报错最近一次成功的配置
     * @param json String
     */
    private fun saveConfig(json: String) {
        SPUtils.getInstance(DYNAMIC_EVENT_CONFIG_CACHE_FILE)
            .put(DYNAMIC_EVENT_CONFIG_CACHE_KEY, json)
    }
}