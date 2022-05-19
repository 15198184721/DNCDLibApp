package com.dncdlibapp

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.ApplicationInfo.FLAG_EXTERNAL_STORAGE
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DebugUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import cn.cd.dn.sdk.models.prints.DNPrint
import cn.cd.dn.sdk.models.utils.DNServiceTimeManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.chat_hook.HookMethodCall
import com.chat_hook.HookMethodCallParams
import com.chat_hook.HookMethodHelper
import com.chat_hook.HookMethodParams
import org.w3c.dom.Document
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.lang.Exception
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

//import com.dn.apt.HelloWorld
//import com.dn.apt.annotations.TestDNApt

//import com.dncd.lib.aop.anns.InterceptAllAnnMethod
//import com.dncd.lib.aop.anns.SingleCallMethod

//@InterceptAllAnnMethod
//@TestDNApt
class MainActivity : AppCompatActivity() {

    var msg: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        msg = findViewById(R.id.msg)
        testServiceTime()
        DNPrint.logE("appInfo=" + AppUtils.getAppInfo().toString())
        DNPrint.logE("isDebug=${AppUtils.isAppDebug()}")
//        findViewById<TextView>(R.id.msg).text = "${AppUtils.isApkInDebug()}"
        findViewById<View>(R.id.aop_singlt_click)
            .setOnClickListener {
                testClick()
            }
        DNServiceTimeManager.getIns().addTimeChanageListener(this, {
            DNPrint.logE("服务器监听更新回调已成功：$it")
        })
        HookMethodHelper.addHookMethod(
            HookMethodParams(
                MainActivity::class.java,
                "testClick",
                null,
                object : HookMethodCall {
                    override fun afterHookedMethod(param: HookMethodCallParams?) {
                        ToastUtils.showShort("Hook 执行了~~~~~~ testClick")
                    }
                })
        )
        findViewById<View>(R.id.test_click).setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
//                HelloWorld.main("s")
//                HelloWorld.testMethod()
                testClick();
            }
        })

        //添加一个hook
//        HookMethodHelper.addHookConstructorMethod(
//            HookMethodParams(
//                MainActivity::class.java,
//                "testClick",null,object : HookMethodCall {
//                    override fun afterHookedMethod(param: HookMethodCallParams?) {
//                        ToastUtils.showShort("Hook 执行了~~~~~~")
//                    }
//                })
//        )
    }

    //    @SingleCallMethod
    private fun testClick() {
//        throw NullPointerException("------------------")
    }

    val mHandler = Handler()
    private fun testServiceTime() {
        mHandler.postDelayed({
            val time = DNServiceTimeManager.getIns().getServiceTime()
            DNPrint.logE("time=====>" + time)
        }, 4000)
    }

}