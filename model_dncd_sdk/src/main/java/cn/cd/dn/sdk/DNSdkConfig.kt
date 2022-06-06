package cn.cd.dn.sdk

import android.content.Context
import cn.cd.dn.sdk.models.events.DynamicEventsManager
import cn.cd.dn.sdk.models.events.intefaces.IEventNotifyReportListener
import cn.cd.dn.sdk.models.netowrks.DNOkHttpInit

/**
 * @author lcl
 * Date on 2022/5/19
 * Description:
 *  多牛相sdk的相关配置信息存储类
 */
class DNSdkConfig private constructor() {
    companion object {

        //配置参数对象
        private val s_DNSdkConfig: DNSdkConfig by lazy {
            DNSdkConfig()
        }

        /**
         * 获取配置数据对象对象
         * @return DNSdkConfig
         */
        fun getIns(): DNSdkConfig {
            return s_DNSdkConfig
        }
    }

    /** 上下文对象 */
    lateinit var appContext: Context

    /** app是否为debug编译模式 */
    var appIsDebug: Boolean = false

    // 私有相关的内容
    // 动态事件上报的回调。如果发生了相关的动态事件需要上报。那么会通过此回调告知业务层上报事件
    private var eventNotifyReportListener: IEventNotifyReportListener? = null

    /**
     * 设置动态事件上报的回调监听
     * @param eventNotifyReportListener IEventNotifyReportListener
     */
    fun setEventNotifyReportListener(eventNotifyReportListener: IEventNotifyReportListener) {
        this.eventNotifyReportListener = eventNotifyReportListener
        // 初始化
        DynamicEventsManager.getIns().init()
    }

    /**
     * 获取上层动态事件上报的回调监听
     * @return IEventNotifyReportListener?
     */
    fun getEventNotifyReportListener(): IEventNotifyReportListener? {
        return this.eventNotifyReportListener
    }

    //----------------- 仅支持内部调用 -------------------
    /**
     * 获取需要同步的上层应用相关的任务列表 (此方法只允许[CInitProvider]进行调用)
     * @return MutableList<ISyncTheUpperTask>
     */
    fun getSyncTheUpperTaskList(): MutableList<ISyncTheUpperTask> {
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
                "非法调用:getSyncTheUpperTaskList() method Only allow ${CInitProvider::class.java.name} Call！\n" +
                        "current Call The Is ${stackTrace[3].className}！"
            )
        }
        return mutableListOf(DNOkHttpInit)
    }
}