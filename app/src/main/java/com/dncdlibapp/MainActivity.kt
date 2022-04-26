package com.dncdlibapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.chat_hook.HookMethodCall
import com.chat_hook.HookMethodCallParams
import com.chat_hook.HookMethodHelper
import com.chat_hook.HookMethodParams

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


//        findViewById<TextView>(R.id.msg).text = "${AppUtils.isApkInDebug()}"
        findViewById<View>(R.id.aop_singlt_click)
            .setOnClickListener {
                testClick()
            }
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
}