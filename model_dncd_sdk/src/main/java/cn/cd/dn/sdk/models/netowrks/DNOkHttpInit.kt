package cn.cd.dn.sdk.models.netowrks

import okhttp3.OkHttpClient

/**
 * @author lcl
 * Date on 2022/5/18
 * Description:
 * 多牛模块OkHttp相关的初始化
 */
object DNOkHttpInit {
    /**
     * 全局唯一通用的OkHttp网络请求对象
     */
    private val dnOkHttp: OkHttpClient by lazy {
        OkHttpClient()
    }

    /**
     * 获取多牛OkHttp网络通讯对象
     * @return OkHttpClient
     */
    fun getDNOkHttp(): OkHttpClient {
        return dnOkHttp
    }
}