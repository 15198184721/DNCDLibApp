
#保持多牛的sdk模块中代码不混淆，保持原样
-keep class cn.cd.dn.sdk.** { *; }
-dontwarn cn.cd.dn.sdk.**

#多牛model的aop模块代码不混淆，保持原样
-keep class com.dncd.lib.aop.** { *; }
-dontwarn com.dncd.lib.aop.**

#多牛model的apt模块代码不混淆，保持原样
-keep class com.dncd.apt.** { *; }
-dontwarn com.dncd.apt.**

#用到的第三方库排除。不混淆
-keep class com.chat_hook.** { *; }
-dontwarn com.chat_hook.**

-keep class com.swift.sandhook.** { *; }
-dontwarn com.swift.sandhook.**