package cn.cd.dn.sdk.models.utils

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import cn.cd.dn.sdk.kexts.ioToMainScheduler
import cn.cd.dn.sdk.kexts.subscribeHttp
import cn.cd.dn.sdk.models.netowrks.DNOkHttpInit
import cn.cd.dn.sdk.models.prints.DNPrint
import com.blankj.utilcode.util.SPUtils
import io.reactivex.Observable
import okhttp3.Request
import org.json.JSONObject
import java.util.*

/**
 * @author lcl
 * Date on 2022/5/18
 * Description:
 * 服务器时间工具。获取服务器时间的工具类
 * 1、采用服务器时间+本地计算模式减少服务器压力
 */
class DNServiceTimeManager private constructor() {
    companion object {

        /** 服务器获取时间的接口地址(暂不考虑外部传入) */
        private const val serviceTimeGetURL = "https://answer.xg.tagtic.cn/answer/v1/get-now-time"

        //系统时间保存文件
        private const val TIME_SERVICE_SAVE_FILE = "TIME_SERVICE_FILE"

        //系统时间
        private const val TIME_SERVICE = "TIME_SERVICE"

        //获取服务器时间时,系统开机的时长,这样只需要计算过了多长时间，就可以更新服务器时间，不用多次刷新获取服务器时间接口
        private const val TIME_SERVICE_ELAPSED_REALTIME = "TIME_SERVICE_ELAPSED_REALTIME"

        //初始化失败以后刷新时间
        private const val INIT_FAIL_REFRESH_TIME = 30 * 1000L

        //是否网络加载成功的标志
        private var mInitSuccess = false

        //处理的Handler
        private val mHandler = Handler(Looper.getMainLooper()) {
            if (it.what == 1) {
                getIns().updateLocalTime()
            }
            return@Handler false
        }

        // 对当服务器时间矫正进行通知的集合(服务器时间变更通知集合，当服务器时间矫正之后会通知此集合的回调)
        private val serviceTimeChanageNotify: MutableMap<LifecycleOwner, (Long) -> Unit> =
            Collections.synchronizedMap(mutableMapOf())

        //单利化对象
        private val S___TIME_MANAGER_DN: DNServiceTimeManager by lazy {
            DNServiceTimeManager()
        }

        /**
         * 获取单利华的工具对象
         * @return ServiceTimeUtil
         */
        fun getIns(): DNServiceTimeManager {
            return S___TIME_MANAGER_DN
        }
    }

    /**
     * 添加对服务器时间变更的监听
     * @param owner 生命周期对象
     * @param updateListener 更新的回调通知,Long=更新之后的时间
     */
    fun addTimeChanageListener(owner: LifecycleOwner, updateListener: (Long) -> Unit) {
        if (serviceTimeChanageNotify[owner] == null) {
            owner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    //结束生命周期的时候。移除监听。防止内存泄露
                    serviceTimeChanageNotify.remove(owner)
                }
            })
        }
        serviceTimeChanageNotify[owner] = updateListener
    }

    /**
     * 获取服务器时间(单位：毫秒)
     * @return
     * <=0 :处理出现异常
     * >0 : 获取成功
     */
    fun getServiceTime(): Long {
        val currentElapsedRealTime = SystemClock.elapsedRealtime()
        val lastTime = SPUtils.getInstance(TIME_SERVICE_SAVE_FILE)
            .getLong(TIME_SERVICE_ELAPSED_REALTIME, currentElapsedRealTime)
        val duration = currentElapsedRealTime - lastTime
        val serviceTime = SPUtils.getInstance(TIME_SERVICE_SAVE_FILE)
            .getLong(TIME_SERVICE, System.currentTimeMillis())
        return serviceTime + duration
    }

    /**
     * 矫正本地时间。通过网络请求服务器时间重新进行校正
     */
    fun updateLocalTime() {
        getHttpServiceTime()
    }

    //获取网络时间
    private fun getHttpServiceTime() {
        DNPrint.logE(">> 服务器时开始更新")
        Observable.just("")
            .map {
                val request =
                    Request.Builder().url(serviceTimeGetURL + DNOkHttpInit.getDnCommonJson())
                        .build()
                val response = DNOkHttpInit.getDNOkHttp().newCall(request).execute()
                if (response.isSuccessful) {
                    val joon = response.body?.string() ?: ""
                    val jsonObj = JSONObject(joon)
                    try {
                        if (jsonObj.getInt("code") != 0) {
                            return@map 0L
                        }
                        val time = jsonObj.getJSONObject("data").getLong("now")
                        //保存服务器时间
                        SPUtils.getInstance(TIME_SERVICE_SAVE_FILE).put(TIME_SERVICE, time * 1000L)
                        //保存系统开机时间
                        SPUtils.getInstance(TIME_SERVICE_SAVE_FILE).put(
                            TIME_SERVICE_ELAPSED_REALTIME, SystemClock.elapsedRealtime()
                        )
                        mInitSuccess = true
                        return@map time
                    } catch (e: Exception) {
                        return@map 0L
                    }
                }
                return@map 0L
            }
            .ioToMainScheduler()
            .map { time ->
                if (time > 0) {
                    //执行成功止呕。执行上层回调
                    serviceTimeChanageNotify.forEach {
                        it.value.invoke(time)
                    }
                }
                return@map mInitSuccess
            }
            .subscribeHttp(succ = {
                if (!it) {
                    DNPrint.logE(">> 服务器时间更新处理失败")
                    handleInitFail()
                } else {
                    DNPrint.logE(">> 服务器时间更新处理成功。已更新完成!!")
                    mHandler.removeMessages(1)
                }
            }, err = {
                DNPrint.logE(">> 服务器时间更新异常!e=$it")
            })
    }

    //加载失败或者处理失败。延迟再试
    private fun handleInitFail() {
        mInitSuccess = false
        val lastServiceTime = SPUtils.getInstance(TIME_SERVICE_SAVE_FILE)
            .getLong(TIME_SERVICE, 0L)
        //如果上一次保存过时间，则使用此时间,可能有错，用户在此期间可能重启手机
        //如果没有保存过时间，则暂时使用当前手机时间
        if (lastServiceTime == 0L) {
            SPUtils.getInstance(TIME_SERVICE_SAVE_FILE)
                .put(TIME_SERVICE, System.currentTimeMillis())
            SPUtils.getInstance(TIME_SERVICE_SAVE_FILE)
                .put(TIME_SERVICE_ELAPSED_REALTIME, SystemClock.elapsedRealtime())
        }
        //发送消息刷新时间
        mHandler.removeMessages(1)
        mHandler.sendEmptyMessageDelayed(
            1, INIT_FAIL_REFRESH_TIME
        )
    }
}