package com.dn.apt.processors

import com.dn.apt.annotations.notify.DesktopNotifyUserInfoSync
import com.dn.apt.annotations.notify.DesktopNotifyUI5NumberSync
import com.dn.apt.utils.PrintAptUtil
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

/**
 * @author lcl
 * Date on 2021/9/23
 * Description:
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8) //java版本支持
@SupportedAnnotationTypes(
    "com.dn.apt.annotations.notify.DesktopNotifyInnerSync",
    "com.dn.apt.annotations.notify.DesktopNotifyOutSync"
) //标注注解处理器支持的注解类型，就是我们刚才定义的接口Test，可以写入多个注解类型。
class DesktopNotifySyncProcessor : AbstractProcessor() {

    lateinit var mMessager: Messager
    lateinit var mElements: Elements
    lateinit var mFiler: Filer

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mFiler = processingEnv.filer //文件相关的辅助类
        mElements = processingEnv.elementUtils //元素相关的辅助类
        mMessager = processingEnv.messager //日志相关的辅助类
    }

    override fun process(
        p0: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {

        val elements: Set<Element> = roundEnvironment.getElementsAnnotatedWith(
            DesktopNotifyUserInfoSync::class.java
        )
        if (elements.isNotEmpty()) {
            generatedDesktopNotifyInnerSync(elements)
        }
        val elements1: Set<Element> = roundEnvironment.getElementsAnnotatedWith(
            DesktopNotifyUI5NumberSync::class.java
        )
        if (elements1.isNotEmpty()) {
            generatedDesktopNotifyOutSync(elements)
        }
        return true
    }

    //生成[DesktopNotifyInnerSync]的代码
    private fun generatedDesktopNotifyInnerSync(elements: Set<Element>) {
        val methodMain = MethodSpec.methodBuilder("testMethod") //创建main方法
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //定义修饰符为 public static
            .addJavadoc("@  此类由apt自动生成") //在生成的代码前添加注释
            .returns(Void.TYPE) //定义返回类型
//            .addParameter(Array<String>::class.java, "args") //定义方法参数
            .addStatement("\$T.out.println(\$S)", System::class.java, "helloWorld") //定义方法体
            .build()
        val helloWorld = TypeSpec.classBuilder("HelloWorld") //创建HelloWorld类
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL) //定义修饰符为 public final
            .addMethod(methodMain) //添加方法
            .addJavadoc("@  此方法由apt自动生成") //定义方法参数
            .build()
        val javaFile = JavaFile.builder("com.dn.apt", helloWorld).build() // 生成源   代码

        try {
            PrintAptUtil.printV(mMessager, "开始准备生成$javaFile")
            javaFile.writeTo(mFiler) // 在 app module/build/generated/source/apt 生成一份源代码
            PrintAptUtil.printV(mMessager, "生成完成了")
        } catch (e: IOException) {
            PrintAptUtil.printV(mMessager, "生成出现错误了$e")
            e.printStackTrace()
        }
    }

    //生成[DesktopNotifyOutSync]的代码
    private fun generatedDesktopNotifyOutSync(elements: Set<Element>) {
        val methodMain = MethodSpec.methodBuilder("testMethod") //创建main方法
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC) //定义修饰符为 public static
            .addJavadoc("@  此类由apt自动生成") //在生成的代码前添加注释
            .returns(Void.TYPE) //定义返回类型
//            .addParameter(Array<String>::class.java, "args") //定义方法参数
            .addStatement("\$T.out.println(\$S)", System::class.java, "helloWorld") //定义方法体
            .build()
        val helloWorld = TypeSpec.classBuilder("HelloWorld") //创建HelloWorld类
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL) //定义修饰符为 public final
            .addMethod(methodMain) //添加方法
            .addJavadoc("@  此方法由apt自动生成") //定义方法参数
            .build()
        val javaFile = JavaFile.builder("com.dn.apt", helloWorld).build() // 生成源   代码

        try {
            PrintAptUtil.printV(mMessager, "开始准备生成$javaFile")
            javaFile.writeTo(mFiler) // 在 app module/build/generated/source/apt 生成一份源代码
            PrintAptUtil.printV(mMessager, "生成完成了")
        } catch (e: IOException) {
            PrintAptUtil.printV(mMessager, "生成出现错误了$e")
            e.printStackTrace()
        }
    }

}