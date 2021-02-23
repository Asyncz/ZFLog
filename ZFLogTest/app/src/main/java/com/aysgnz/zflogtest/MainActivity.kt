package com.aysgnz.zflogtest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aysgnz.zflog.ZFLog
import com.aysgnz.zflog.ZFLogLevel
import java.lang.RuntimeException
import java.net.URI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map = mapOf("hello" to "hhhhhhh","22222" to "33333333")
        val map2 = mapOf("first" to Dev(1,"hahahah"),"second" to Dev(2,"eeeeeeeee"))
        var int = Intent()
        int.putExtra("111","2222")
        var bundle = Bundle()
        bundle.putString("2222","444444444")

        //仅一次有效的TAG
        ZFLog.i("2222",Dev(1,"hahahah"))

        ZFLog.d(RuntimeException("test"))
        ZFLog.e(arrayListOf("xixixixixixi","hehehehehhe"))

        //自定义TAG
        ZFLog.TAG = "TEST_TAG"
        ZFLog.i(map2)
        ZFLog.d(8.2222222)
        ZFLog.e(bundle)
        ZFLog.e(URI("333333333333333333"))

        //自定义打印等级
        ZFLog.level = ZFLogLevel.INFO
        ZFLog.d(map)

        //自定义显示内容，自定义内容在最上面
        ZFLog.custom = "1.0.0"
        ZFLog.customSign = "version: "
        ZFLog.i(123)

        //设置不显示线程信息
        ZFLog.showThead = false
        ZFLog.i("**********************")

        //设置不显示代码行数
        ZFLog.showCodeLine = false
        ZFLog.e(Dev(5,"xxxx"))

        //写入本地设置， 注意：不自带写入本地，写入需在writeCallBack自己处理
        ZFLog.writeLevel = ZFLogLevel.WARN
        ZFLog.writeCallBack = {level, tag, msg ->
            //自定义写入本地
        }

        //添加null打印
        ZFLog.e(null)
    }
    data class Dev(var id:Int,var name:String)
}
