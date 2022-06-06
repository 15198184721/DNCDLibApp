package cn.cd.dn.sdk.models.events.beans

import java.nio.charset.Charset
import java.util.*

/**
 * @author lcl
 * Date on 2022/5/31
 * Description:
 */
class DynamicEventResp {
    /**
     * 此事件需要上报的具体位置。也就是需要在那个方法中上报。方法的全路径(不能是一个接口或者抽象方法)
     * 例如:
     *  方法名称：AAA() 需要写为 com.xx.xx.xx#AAA
     *  方法名称：AAA(String,Int) 需要写为 com.xx.xx.xx#AAA
     */
    var methodName: String? = null

    /**
     * 此方法所对应的参数。如果没有指定参数表示所有相同方法名称的方法都将上报
     * 例如：
     *  方法名称：AAA() 需要写为：null
     *  方法名称：AAA(String,Int) 需要写为 arrayOf("java.lang.String","java.lang.Integer") 指定类型的原始Class类型路径
     *      注意：如果参数是基础类型 int、long 等，直接和关键字对应字符串即可,例如：
     *      方法名称：AAA(int,long) 需要写为 arrayOf("int","long")
     */
    var paramsTypes: MutableList<String>? = null

    /**
     * 需要上报的事件id
     */
    var eventId: String? = null

    /**
     * 获取当前对象的唯一id
     */
    fun getQueId(): String {
        val sb = StringBuffer("${methodName}_")
        paramsTypes?.forEach {
            sb.append(it)
        }
        return UUID.nameUUIDFromBytes(
            sb.toString().toByteArray(Charset.forName("UTF-8"))
        ).toString().replace("-", "")
    }
}