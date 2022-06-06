package cn.cd.dn.sdk.models.events.intefaces

import android.content.Context

/**
 * @author lcl
 * Date on 2022/5/30
 * Description:
 * 和上层上报事件的监听。就是发送了动态事件。通知上层进行上报
 */
interface IEventNotifyReportListener {
    /**
     * 通知上层进行数据上报
     * @param appContext app的上下文
     * @param eventId 上报的事件id
     * @param paramsMap 需要上报的参数(暂时不支持错参数类型)
     */
    fun eventReport(appContext: Context, eventId: String, paramsMap: Map<String, Any>?)

    /**
     * 获取动态事件上报的配置请求地址，配置信息获取的地址(中台GET获取)
     * 返回结果结构参考：[cn.cd.dn.sdk.models.events.beans.DynamicEventResp]
     * @return String
     */
    fun getEventDynamicReportConfigUrl(): String
}