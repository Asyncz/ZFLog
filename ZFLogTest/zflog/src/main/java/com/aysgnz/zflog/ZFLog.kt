package com.aysgnz.zflog

import android.util.Log
import com.alibaba.fastjson.JSON
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.Reference

enum class ZFLogLevel(val value : Int){
    VERBOSE(2),
    DEBUG(3),
    INFO(4),
    WARN(5),
    ERROR(6),
    ASSERT(7)
}
/**
 * zf 2019/3/2 11:20
 * Log管理
 * 依赖：implementation 'com.alibaba:fastjson:1.1.70.android'
 * 原理：使用fastjson将内容转化为json，再对jeson换行处理
 * 注：代码极大程度上参考了SAF-Kotlin-log（https://github.com/fengzhizi715/SAF-Kotlin-log）
 */
object ZFLog{
    private val TOP_LEFT_CORNER = '╔'
    private val BOTTOM_LEFT_CORNER = '╚'
    private val MIDDLE_CORNER = '╟'
    private val DOUBLE_DIVIDER = "═════════════════════════════════════════════════"
    private val SINGLE_DIVIDER = "─────────────────────────────────────────────────"
    private val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER
    private val BR = System.getProperty("line.separator")     // 换行符

    /** 默认的打印TAG */
    var TAG = "ZF_LOG"
    /** 打印等级 */
    var level = ZFLogLevel.DEBUG
    /** 写入等级  注：不自带写入本地，写入需在writeCallBack自己处理 */
    var writeLevel = ZFLogLevel.VERBOSE
    /** 写入回调，在回调里自定义写入内存的方法   注：不自带写入本地，写入需在writeCallBack自己处理 */
    var writeCallBack: ((level:ZFLogLevel,tag:String,msg:String)->Unit)? = null
    /** 是否显示线程信息 */
    var showThead = true
    /** 是否显示打印调用的位置 */
    var showCodeLine = true
    /** 自定义打印内容，显示在最上面 */
    var custom = ""
    /** 自定义打印内容的标签 */
    var customSign = "Custom: "

    /** 打印 */
    fun v(content: Any?){
        ZFLog.v(TAG,content)
    }
    /** 打印 */
    fun d(content: Any?){
        ZFLog.d(TAG,content)
    }
    /** 打印 */
    fun i(content: Any?){
        ZFLog.i(TAG,content)
    }
    /** 打印 */
    fun w(content: Any?){
        ZFLog.w(TAG,content)
    }
    /** 打印 */
    fun e(content: Any?){
        ZFLog.e(TAG,content)
    }
    /** 打印 */
    fun wtf(content: Any?){
        ZFLog.wtf(TAG,content)
    }

    /** 打印，自定义TAG，本次有效 */
    fun v(tag: String,content: Any?){
        log(ZFLogLevel.VERBOSE, tag, content)
    }
    /** 打印，自定义TAG，本次有效 */
    fun d(tag: String,content: Any?){
        log(ZFLogLevel.DEBUG, tag, content)
    }
    /** 打印，自定义TAG，本次有效 */
    fun i(tag: String,content: Any?){
        log(ZFLogLevel.INFO, tag, content)
    }
    /** 打印，自定义TAG，本次有效 */
    fun w(tag: String,content: Any?){
        log(ZFLogLevel.WARN, tag, content)
    }
    /** 打印，自定义TAG，本次有效 */
    fun e(tag: String ,content: Any?){
        log(ZFLogLevel.ERROR, tag, content)
    }
    /** 打印，自定义TAG，本次有效 */
    fun wtf(tag: String ,content: Any?){
        log(ZFLogLevel.ASSERT, tag, content)
    }
    
    /** 集中打印处理 */
    fun log(le: ZFLogLevel, tag: String, content: Any?){
        if (le.value < level.value) return
        var msg = toString(content)
        val s = formatMessage()
        if (msg.contains("\n")) {
            msg = String.format(s,msg.replace("\n".toRegex(), "\n║ "))
        } else {
            msg = String.format(s, msg)
        }
        Log.println(le.value, tag, msg)
        if (le.value < writeLevel.value){
            if (writeCallBack != null){
                writeCallBack!!.invoke(le,tag,msg)
            }
        }
    }
    /** 把内容转化为文字 */
    fun toString(any: Any?):String{
        if (any == null){
            return "null"
        }
        var num = primitiveTypeString(any)
        if (num.isNotEmpty()){
            return num
        }
        if (any is Throwable){
            return formatThrowable(any)
        }
        var header = ""
        if (any is Reference<*>){
            header = any.javaClass.canonicalName + "<" + any.get()?.javaClass?.simpleName + ">"+ BR
        }else{
            header = any.javaClass.toString() + BR
        }
        var str = JSON.toJSONString(any)
        str = formatString(str)
        str = header + str
        return str
    }

    /** json字符换行处理 */
    fun formatString(s: String): String {
        var message = ""
        var str = s
        str = str.trim{ it <= ' '}
        try {
            if (str.startsWith("{")) {
                val jsonObject = JSONObject(str)
                message = jsonObject.toString(2)
            } else if (str.startsWith("[")) {
                val jsonArray = JSONArray(str)
                message = jsonArray.toString(2)
            }else{
                message = s
            }
        } catch (e: JSONException) {
            message = s
        }
        return message
    }
    /** 基本类型转化为文字，字符串内容格式化 */
    fun primitiveTypeString(value: Any):String = when(value){
        is Boolean -> "Boolean: " + value
        is String -> formatString(value)
        is Int -> "Int: " + value
        is Float -> "Float: " + value
        is Double ->  "Double: " + value
        else -> ""
    }
    /** 错误类型转文字 */
    fun formatThrowable(throwable: Throwable): String {
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        throwable.printStackTrace(pw)
        pw.flush()
        val message = sw.toString()
        return message
    }
    /** 用来查找打印代码位置 */
    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        //第一行是：System.out: dalvik.system.VMStack
        //第二行是：java.lang.Thread
        //前两行是固定的，需要跳过
        var i =  2
        val size = trace.size
        while (i < size) {
            val e = trace[i]
            val name = e.className
//            println(name)
            if ( name != ZFLog::class.java.name) {
                return i
            }
            i++
        }
        return 0
    }

    /** 输出文字边框格式 */
    fun formatMessage(): String {
        val sElements = Thread.currentThread().stackTrace
        val stackOffset = getStackOffset(sElements)
        val builder = StringBuilder()
        builder.append("  ").append(BR).append(TOP_BORDER).append(BR)
        if (custom.isNotEmpty()) {
            builder.append("║ " + customSign + custom).append(BR)
                .append(MIDDLE_BORDER).append(BR)
        }
        // 添加当前线程名
        if (showThead){
            builder.append("║ " + "Thread: " + Thread.currentThread().name).append(BR)
                .append(MIDDLE_BORDER).append(BR)
        }
        //添加类名和行数
        if (showCodeLine){
            builder.append("║ ")
                .append(sElements[stackOffset].className)
                .append(".")
                .append(sElements[stackOffset].methodName)
                .append(" ")
                .append(" (")
                .append(sElements[stackOffset].fileName)
                .append(":")
                .append(sElements[stackOffset].lineNumber)
                .append(")")
                .append(BR)
                .append(MIDDLE_BORDER)
                .append(BR)
        }
        // 内容
        builder.append("║ ").append("%s").append(BR)
            .append(BOTTOM_BORDER).append(BR)
        return builder.toString()
    }
}