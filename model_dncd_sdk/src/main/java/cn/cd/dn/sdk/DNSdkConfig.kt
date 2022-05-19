package cn.cd.dn.sdk

import android.content.Context

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
}