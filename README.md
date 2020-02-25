# ZFLog

[![](https://jitpack.io/v/Asyncz/ZFLog.svg)](https://jitpack.io/#Asyncz/ZFLog)

基于Kotlin的Android日志框架。

主要功能是通过[fastjson](https://github.com/alibaba/fastjson)将一些无法正常打印的数据转化为JSON，然后对JSON内容换行处理，优化显示效果。

整理过程中主要参考了[SAF-Kotlin-log](https://github.com/fengzhizi715/SAF-Kotlin-log)。

## 下载

### 1.Project build.gradle

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### 2. dependencies

```
	dependencies {
	        implementation 'com.github.Asyncz:ZFLog:1.0.2'
	}
```

## 示例

```kotlin
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
	
	// 打印null
	ZFLog.e(null)
```

![img](http://thyrsi.com/t6/676/1551514153x2728278668.png)
