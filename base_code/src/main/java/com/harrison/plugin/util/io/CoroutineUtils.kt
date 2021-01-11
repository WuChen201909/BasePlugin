package com.harrison.plugin.util.io

import com.harrison.plugin.util.developer.LogUtils
import kotlinx.coroutines.*


/**
 * 携程封装工具类
 */
object CoroutineUtils {
    var exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogUtils.printException(throwable)
    }
    var IOScope:CoroutineScope = CoroutineScope(SupervisorJob()+Dispatchers.IO+ exceptionHandler)
    var mainScope:CoroutineScope = CoroutineScope(SupervisorJob()+Dispatchers.Main+ exceptionHandler)

    /**
     * 切换到 IO 线程操作内容
     */
    fun launchIO(block: suspend CoroutineScope.() -> Unit){
        IOScope.launch {
            block()
        }
    }

    /**
     * 切换到主线程操作内容
     */
    fun launchMain(block: suspend CoroutineScope.() -> Unit){
        mainScope.launch {
            block()
        }
    }

}