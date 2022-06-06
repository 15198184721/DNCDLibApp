package cn.cd.dn.sdk

import android.content.Context

/**
 * @author lcl
 * Date on 2022/5/20
 * Description:
 * 需要同步上层的接口对象。规范了需要和上层模块同步的接口
 */
interface ISyncTheUpperTask {



    /**
     * 调用同步上层任务接口
     * @param context Context
     */
    fun sync(context: Context)

}