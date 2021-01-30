package com.harrison.plugin.util.io

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.harrison.plugin.util.developer.LogUtils
import kotlinx.coroutines.*
import org.json.JSONException
import java.io.IOException
import java.lang.Exception


/**
 * 携程封装工具类
 */
object CoroutineUtils {
    var exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogUtils.printException(throwable)
    }
    var IOScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)
    var mainScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main + exceptionHandler)

    /**
     * 切换到 IO 线程操作内容
     */
    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        IOScope.launch {
            block()
        }
    }

    /**
     * 切换到主线程操作内容
     */
    fun launchMain(block: suspend CoroutineScope.() -> Unit) {
        mainScope.launch {
            block()
        }
    }

    /**
     * 封装异步加载视图
     *  @param context 上下文
     *  @param layoutId 布局ID
     *  @param callResult 界面加载成功后的回掉
     */
    fun launchLayout(context: Context, layoutId:Int, callResult: (result: View) -> Unit) {
        CoroutineUtils.launchIO {
            var resultView = LayoutInflater.from(context).inflate(layoutId,null)
            CoroutineUtils.launchMain {
                callResult(resultView)
            }
        }
    }

    /**
     * 封装网络请求的线程切换
     * @param async 异步执行任务
     * @param callResult 执行成功回掉到开始线程
     * @param callResult 执行失败回掉到开始线程
     *
     */
    fun <T> launchNetwork(
        async: suspend CoroutineScope.() -> T,
        callResult: (result: T) -> Unit,
        callError: (code: Int, error: String) -> Unit
    ) {
        CoroutineUtils.launchIO {
            try {
                var re = async()
                CoroutineUtils.launchMain {
                    callResult(re)
                }
            } catch (e: Exception) {
                LogUtils.printException(e)
                var describe = ""
                when (e) {
                    is IOException -> {
                        describe = "网络异常"
                    }
                    is JSONException -> {
                        describe = "解析异常"
                    }
                }
                CoroutineUtils.launchMain {
                    callError(-200, "$describe\n:$e")
                }
            }
        }
    }

}