package com.harrison.plugin.util.io

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.harrison.plugin.util.developer.LogUtils
import kotlinx.coroutines.*
import org.json.JSONException
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock


/**
 * 携程封装工具类
 *  1、封装IO线程
 *  2、封装主线程
 *  3、封装异步加载视图
 *  4、封装网络请求加载
 *  5、封装任务队列
 */
object CoroutineUtils {

    var exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogUtils.printException(throwable)
    }
    var IOScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)
    var mainScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main + exceptionHandler)

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
    fun launchLayout(context: Context, layoutId: Int, callResult: (result: View) -> Unit) {
        CoroutineUtils.launchIO {
            var resultView = LayoutInflater.from(context).inflate(layoutId, null)
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

        var httpExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            LogUtils.printException(throwable)
            CoroutineUtils.launchMain {
                callError(-200, "${throwable.message}")
            }
        }

        var httpScope: CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.IO + httpExceptionHandler)
        httpScope.launch {
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


    /**
     * =============================================================
     *  任务队列封装
     * =============================================================
     */
    class TaskQueueManager<T> {

        var taskQueue: MutableList<Task<T>> = arrayListOf()
        var workStatus = false;

        //任务数据结构 ，thread 执行线程类型
        data class Task<T>(var exest: () -> T, var result: (data: T) -> Unit)

        /**
         * 添加任务到队列
         */
        fun addTask(exest: () -> T, result: (data: T) -> Unit) {

            var task = Task<T>(exest, result)
            synchronized(taskQueue) {
                taskQueue.add(task)
            }

            synchronized(workStatus) {
                if (workStatus)
                    return
                workStatus = true
            }

            launchIO {
                while (workStatus) {
                    var taskItem: Task<T>? = null

                    synchronized(taskQueue) {
                        if (taskQueue.size <= 0) {
                            workStatus = false
                        }
                        taskItem = taskQueue[0]
                        taskQueue.removeAt(0)
                    }

                    if (taskItem == null) {
                        synchronized(workStatus) {
                            workStatus = false
                        }
                        break
                    }

                    var result = taskItem!!.exest()

                    launchMain {
                        taskItem!!.result(result)
                    }

                }
            }
        }

        /**
         * 移出但前任务队列
         */
        fun removeTask(task: Task<T>) {
            synchronized(taskQueue) {
                taskQueue.remove(task)
            }
        }

        fun clearAllTask() {
            synchronized(taskQueue) {
                taskQueue.clear()
            }
        }


    }


}