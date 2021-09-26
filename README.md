# DNCDLibApp
多牛成都项目基础库的Lib，常用的一些Lib等封装使用

> ####[apt接入方式参考]()
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
----