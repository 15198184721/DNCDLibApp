package cn.cd.dn.sdk.models.utils

import android.app.Application
import android.content.Context
import cn.cd.dn.sdk.DNSdkConfig
import cn.cd.dn.sdk.models.prints.DNPrint
import com.chat_hook.HookMethodCall
import com.chat_hook.HookMethodCallParams
import com.chat_hook.HookMethodHelper
import com.chat_hook.HookMethodParams
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import dalvik.system.DexFile
import dalvik.system.PathClassLoader
import java.lang.Exception
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.Throws

/**
 * @author lcl
 * Date on 2022/5/30
 * Description:
 * 对ClassLoader的一些管理
 */
class ClassLoaderManager {

    companion object {
        private val s_ClassLoaderManager = ClassLoaderManager()

        /**
         * 获取ClassMananger对象
         * @return 单利的自身对象
         */
        fun getClassLoaderManager(): ClassLoaderManager {
            return s_ClassLoaderManager
        }
    }

    // 是否初始化
    private var isInit = false

    // 自己内使用的ClassLoader对象
    private lateinit var mDnClassLoader: ClassLoader

    /**
     * 对ClassLoader的一些拦截处理
     * @param appContext app的上下文
     */
    fun initInterceptClassLoader(appContext: Context) {
        if (isInit) {
            return
        }
        interceptAppClassloader(appContext)
    }

    /**
     * 获取多牛内部的ClassLoader加载对象
     * @return ClassLoader
     */
    fun getDNClassLoader(): ClassLoader {
        return mDnClassLoader
    }

    /**
     * 扫描某个包路径下的所有Class
     * 注：暂时不确定分包情况下是否有效
     * @param pageackName 指定包路径
     * @return List<Class
     */
    fun scanPackageClass(pageackName: String): List<Class<*>> {
        val time = System.currentTimeMillis()
        val listClass = mutableListOf<Class<*>>()
        if (pageackName.isEmpty()) {
            return listClass
        }
        try {
            val dex = DexFile(DNSdkConfig.getIns().appContext.packageResourcePath)
            val entries: Enumeration<String> = dex.entries()
            var entryName: String?
            var curClazz: Class<*>?
            while (entries.hasMoreElements()) {
                entryName = entries.nextElement()
                if (entryName != null && entryName.startsWith(pageackName)) {
                    try {
                        curClazz = if (getDNClassLoader() is DNClassLoader) {
                            (getDNClassLoader() as DNClassLoader).dnFindClass(entryName)
                        } else {
                            getDNClassLoader().loadClass(entryName)
                        }
                        listClass.add(curClazz!!)
                    } catch (not: ClassNotFoundException) {
                        DNPrint.logI("未找到Class：$entryName")
                    }
                }
            }
        } catch (e: Throwable) {
            DNPrint.logI("扫描class出错了:$e")
        }
        DNPrint.logI("scan finish ,sum time =${System.currentTimeMillis() - time}/MS,load class count = ${listClass.size}")
        return listClass
    }


    //拦截App的ClassLoader
    private fun interceptAppClassloader(appContext: Context) {
        var oldClassLoader: PathClassLoader? = null
        try {
            if (isInit || appContext !is Application) {
                return
            }
            isInit = true
            //获取mLoaderApk信息
            val mLoaderApk = appContext.javaClass.getField("mLoadedApk").get(appContext)
            val mClassloadField = mLoaderApk.javaClass.getDeclaredField("mClassLoader")
            mClassloadField.isAccessible = true
            //原始的Classloader
            oldClassLoader = mClassloadField.get(mLoaderApk) as PathClassLoader
            //原始的apk路径
            val apkDirField = mLoaderApk.javaClass.getDeclaredField("mAppDir")
            apkDirField.isAccessible = true
            val apkDir: String = apkDirField.get(mLoaderApk) as String
            //创建一个新的ClassLoader
            mDnClassLoader = DNClassLoader(apkDir, oldClassLoader)
            //替换原ClassLoader
            mClassloadField.set(mLoaderApk, mDnClassLoader)
        } catch (e: Throwable) {
            mDnClassLoader = oldClassLoader ?: javaClass.classLoader
            DNPrint.logI("加载ClassLoader失败 放弃操作,自动相关惭怍将无法实现...请注意!!!!")
        }
    }

    /**
     * 自己内部使用的ClassLoader，将系统的ClassLoader 替换为自定义的内部ClassLoader
     */
    class DNClassLoader : PathClassLoader {
        constructor(dexPath: String?, parent: ClassLoader?) : super(dexPath, parent)
        constructor(dexPath: String?, librarySearchPath: String?, parent: ClassLoader?) : super(
            dexPath,
            librarySearchPath,
            parent
        )

        override fun loadClass(name: String?, resolve: Boolean): Class<*> {
            DNPrint.logI("dnClassLoader run ... loadClass=$name")
            return super.loadClass(name, resolve)
        }

        override fun findLibrary(name: String?): String {
            return super.findLibrary(name)
        }

        override fun findResource(name: String?): URL {
            return super.findResource(name)
        }

        override fun findResources(name: String?): Enumeration<URL> {
            return super.findResources(name)
        }

        override fun findClass(name: String?): Class<*> {
            return super.findClass(name)
        }

        @Throws(ClassNotFoundException::class)
        fun dnFindClass(name: String): Class<*> {
            return super.findClass(name)
        }
    }

}