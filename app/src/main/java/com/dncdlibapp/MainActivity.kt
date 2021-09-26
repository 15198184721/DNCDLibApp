package com.dncdlibapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dncd.lib.aop.anns.InterceptAllAnnMethod
import com.dncd.lib.aop.anns.SingleCallMethod

@InterceptAllAnnMethod
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        findViewById<TextView>(R.id.msg).text = "${AppUtils.isApkInDebug()}"
        findViewById<View>(R.id.aop_singlt_click)
            .setOnClickListener {
                testClick()
            }
//        findViewById<View>(R.id.aop_singlt_click).setOnClickListener(object :View.OnClickListener{
//            override fun onClick(p0: View?) {
//                testClick()
//            }
//        })
    }

    @SingleCallMethod
    private fun testClick(){
//        throw NullPointerException("------------------")
    }
}