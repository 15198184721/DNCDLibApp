package cn.cd.dn.sdk.models.utils.handler

import android.os.Handler
import android.os.Looper
import cn.cd.dn.sdk.models.utils.DNServiceTimeManager

/**
 * @author lcl
 * Date on 2022/6/1
 * Description:
 * 通用的全局Handler
 */
class BackgroundsTask {

    companion object {
        private val backTask = BackgroundsTask()

        /**
         * 获取后台任务执行对象
         * @return BackgroundsTask
         */
        fun getIns(): BackgroundsTask {
            return backTask;
        }
    }

    //处理的Handler
    private val mHandler = Handler(Looper.getMainLooper())

    /**
     * 获取Handler对象
     * @return Handler
     */
    fun getHandler(): Handler {
        return mHandler
    }

    /**
     * 执行任务
     * @param task Runnable
     */
    fun post(task: Runnable) {
        mHandler.post(task)
    }

    /**
     * 延迟执行任务
     * @param task Runnable
     * @param delayedTime Long
     */
    fun postDelayed(task: Runnable, delayedTime: Long) {
        mHandler.postDelayed(task, delayedTime)
    }
}