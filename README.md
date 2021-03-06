# DNCDLibApp
多牛成都项目基础库的Lib，常用的一些Lib等封装使用

# 引入方式
```
    // 多牛sdk引入，主要是一些常用组件集合。来进行组件的回调
    api 'com.donews.lk:model_dncd_sdk:2.0.11'
```

> ####[apt接入方式参考](./components/lib_java_apt/使用方法.txt)
> ####[aop接入方式参考](./components/lib_aop/plugins/接入说明.txt)

###注意：如果开启了混淆记得再"app"模块加入以下混淆配置
=======
```gradle
> -keep class cn.cd.dn.sdk.** { *; }

> -dontwarn cn.cd.dn.sdk.**

> -keep class com.dncd.lib.aop.** { *; }

> -dontwarn com.dncd.lib.aop.**

> -keep class com.dncd.apt.** { *; }

> -dontwarn com.dncd.apt.**

> -keep class com.chat_hook.** { *; }

> -dontwarn com.chat_hook.**

> -keep class com.swift.sandhook.** { *; }

> -dontwarn com.swift.sandhook.**
```

###注意事项

### 包结构说明 -> cn.cd.dn.sdk.models
> events       : 事件上报相关的模块
> netowrks     : 和业务处网络服务器同步的相关模块
> prints       : 日志输出相关工具 
> utils        : 工具模块 
----