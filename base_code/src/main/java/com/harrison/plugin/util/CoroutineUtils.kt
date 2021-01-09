package com.harrison.plugin.util

import kotlinx.coroutines.*


/**
 * 携程封装工具类
 */
object CoroutineUtils {
    var exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        KLog.printException(throwable)
    }
    var IOScope:CoroutineScope = CoroutineScope(SupervisorJob()+Dispatchers.IO+exceptionHandler)
    var mainScope:CoroutineScope = CoroutineScope(SupervisorJob()+Dispatchers.Main+exceptionHandler)

    fun launchIO(block: suspend CoroutineScope.() -> Unit){
        IOScope.launch {
            block()
        }
    }

    fun launchMain(block: suspend CoroutineScope.() -> Unit){
        mainScope.launch {
            block()
        }
    }

}