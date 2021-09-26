package com.dncdlibapp.app

import android.app.Application
import com.blankj.utilcode.util.ToastUtils
import com.chat_hook.HookMethodCall
import com.chat_hook.HookMethodCallParams
import com.chat_hook.HookMethodHelper
import com.chat_hook.HookMethodParams
import com.dncd.apt.anns.TestApt
import com.dncdlibapp.MainActivity

/**
 * @author lcl
 * Date on 2021/9/14
 * Description:
 */
@TestApt
class MApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //添加一个hook
        HookMethodHelper.addHookConstructorMethod(
            HookMethodParams(
                MainActivity::class.java,
                null,null,object : HookMethodCall {
                    override fun afterHookedMethod(param: HookMethodCallParams?) {
                        ToastUtils.showShort("Hook 执行了~~~~~~")
                    }
                })
        )
    }

}