package com.harrison.plugin.util.developer

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Looper
import android.view.Choreographer
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import java.util.*

/**
 * 性能分析工具
 */
object Performance {

    private var isInit = false

    private var countStartSecond: Long = 1
    private var fps = 1
    private var fpsCallBack: MutableList<(Int) -> Unit> = mutableListOf()

    private var analyze = false

    private var countTimeMap: MutableMap<String, Long> = mutableMapOf()
    private var countTimeType: MutableMap<String, Boolean> = mutableMapOf()


    /**
     * @param analyze 是否打开卡顿检测
     */
    fun init(analyze: Boolean) {
        if (isInit) return
        doFrame()
        if (analyze) {
            this.analyze = analyze
            showFPSLog()
            startSnapShootThread()
            startAnalyzeMainThread()
        }
        isInit = true
    }


    /**
     * ================================================================================
     * FPS 工具
     * ================================================================================
     */


    /**
     * 添加和删除fps回掉函数
     */
    fun addFPSCallBack(callBack: (Int) -> Unit) {
        fpsCallBack.add(callBack)
    }

    fun removeFPSCallBack(callBack: (Int) -> Unit) {
        fpsCallBack.remove(callBack)
    }

    /**
     * 打印FPS日志
     */
    private fun showFPSLog() {
        addFPSCallBack {
            LogUtils.i("fps: [$it]")
        }
    }

    /**
     * 将FPS显示在屏幕上
     */
    fun showFPSView(application: Application) {
        application.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {
                var activityStack = hashSetOf<Activity>()

                override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
                override fun onActivityStarted(activity: Activity) {
                    if(!activityStack.contains(activity)){
                        addFPSView(activity)
                    }
                }
                override fun onActivityResumed(p0: Activity) {}
                override fun onActivityPaused(p0: Activity) {}
                override fun onActivityStopped(activity: Activity) {
                    activityStack.remove(activity)
                }
                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
                override fun onActivityDestroyed(p0: Activity) {}
            }
        )
    }

    private fun addFPSView(activity: Activity){
        var decorView = activity.window.decorView.findViewById<FrameLayout>(android.R.id.content)
        var showView = TextView(activity)
        showView.text = ""
        var layoutParameter = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        showView.layoutParams = layoutParameter
        layoutParameter.gravity = Gravity.TOP
        decorView.addView(showView)
        addFPSCallBack { showView.text = "fps: [$it]" }
    }

    /**
     * 开始响应屏幕刷新事件
     */
    private fun doFrame() {
        Choreographer.getInstance().postFrameCallback {
            val t = System.currentTimeMillis()
            if (countStartSecond == 1L) {
                countStartSecond = t
            }
            if (countStartSecond / 1000 == t / 1000) {
                fps++
            } else if (t / 1000 - countStartSecond / 1000 >= 1) {
                for (item in fpsCallBack) {
                    item(fps)
                }
                fps = 1
                countStartSecond = t
            }

            if (analyze) {
                resetSelf()
            }

            doFrame()
        }
    }


    /**
     * ================================================================================
     * 计时工具
     * ================================================================================
     */

    /**
     *
     * 开始计时
     *  默认使用毫秒为单位
     */
    fun startCountTime(tag: String, isNano: Boolean = false) {
        countTimeMap[tag] = if (isNano) System.nanoTime() else System.currentTimeMillis()
        countTimeType[tag] = isNano
    }

    /**
     * 结束计时
     */
    fun endCountTime(tag: String): Long {
        countTimeMap[tag]?.let { startTime ->
            var currentTime = if (countTimeType[tag] == null || !countTimeType[tag]!!) {
                System.currentTimeMillis()
            } else {
                System.nanoTime()
            }
            return currentTime - startTime
        }
        return -1
    }

    /**
     * ================================================================================
     * 卡顿检测工具
     * ================================================================================
     */

    private const val TIME_BLOCK: Int = 100 //阈值，执行超过指定时间，阀值要大于16否则无效

    private const val FREQUENCY = 10 //采样频率，表示超时部分采样多少次

    var time: Int = FREQUENCY

    // 当前快照任务堆栈队列
    var list: MutableList<Array<StackTraceElement>> = mutableListOf()

    // 分析任务堆栈队列
    var stackList: MutableList<MutableList<Array<StackTraceElement>>> = mutableListOf() // 任务堆栈
    private const val maxSize = 10  //分析任务队列最大值
    var covertStackTrace: MutableList<List<String>> = arrayListOf() //
    var resultTask: MutableList<String>? = null


    /**
     * 重置快照计数
     */
    private fun resetSelf() {
        synchronized(list) {
            time = FREQUENCY
            list.clear()
        }
    }

    /**
     * 启动快照线程
     */
    private fun startSnapShootThread() {
        Thread {
            while (true) {
                // 开始快照
                val stackTrace = Looper.getMainLooper().thread.stackTrace
                synchronized(list) {
                    list.add(stackTrace) // 添加到堆栈中
                }

                //检测阀值
                time--
                if (time == 0) { // 表示超过阀值
                    time = FREQUENCY
                    synchronized(list) {
                        addSnapShoot(list)
                        list = mutableListOf()
                    }
                }

                //间隔指定时间处理事件
                try {
                    Thread.sleep((TIME_BLOCK / FREQUENCY).toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }


    /**
     * 检测线程
     */
    private fun startAnalyzeMainThread() {
        Thread {
            while (true) {
                var list: List<Array<StackTraceElement>>? = null
                synchronized(stackList) {
                    if (stackList.size > 0) {
                        list = stackList[0]
                        stackList.removeAt(0)
                    }
                }

                if (list == null) {
                    try {
                        Thread.sleep(5)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    continue
                }

                // 先转化堆栈内容为文本，将堆栈顺序反转
                covertStackTrace.clear()
                for (i in list!!.indices) {
                    val task: MutableList<String> = ArrayList()
                    for (j in list!![i].size - 1 downTo 0) { //将堆栈反过来
                        task.add(list!![i][j].toString())
                    }
                    covertStackTrace.add(task)
                }

                resultTask = analyzeStackTrace(0, 0, covertStackTrace)

                val resultBuffer = StringBuffer()
                resultBuffer.append("=========================================\n")
                // 打印结果
                for (i in resultTask!!.indices) {
                    resultBuffer.append("${resultTask!![i]} \n")
                }
                resultBuffer.append("=========================================\n")
                LogUtils.e("BlockDetectUtil", "\n应用出现了卡顿以下是造成本次卡顿的堆栈信息：\n$resultBuffer")
            }
        }.start()
    }

    /**
     * 添加分析任务到任务队列
     */
    private fun addSnapShoot(list: MutableList<Array<StackTraceElement>>) {
//        LogUtils.e("BlockDetectUtil", "分析超时快照")
        while (true) {
            synchronized(stackList) {
                if (stackList.size < maxSize) {
                    stackList.add(list)
                    return
                }
            }
            try {
                Thread.sleep(5)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            break
        }
    }

    /**
     * 分析收集的堆栈列表 ， 以缩进方式表示快照中执行的不同任务
     *
     * @param level     缩近等级
     * @param snapShoot 分析堆栈快照
     * @return 　返回分析列表
     */
    private fun analyzeStackTrace(
        lineNumber: Int, level: Int,
        snapShoot: MutableList<List<String>>,
    ): MutableList<String>? {
        // 生成缩进占位符
        val space = StringBuffer()
        for (i in 0 until level) {
            space.append("    ")
        }
        val result: MutableList<String> = ArrayList()

        //排查到不同的堆栈时进行分组， key
        val newSnapShoot: MutableMap<String, MutableList<List<String>>> = HashMap()
        val lineNumberMap: MutableMap<String, Int> = HashMap()
        for (i in lineNumber until snapShoot[0].size) {  //遍历行
            val lastLine = snapShoot[0][i]
            var j = 0
            while (j < snapShoot.size) {
                //遍历堆栈
                //将当前行不同的数据递归下去处理
                if (snapShoot[j].size <= i) {
                    j++
                    continue
                } else if (snapShoot[j][i].length != lastLine.length || snapShoot[j][i] != lastLine) {
                    var temp = newSnapShoot[lastLine]
                    if (temp == null) {
                        temp = ArrayList()
                        newSnapShoot[lastLine] = temp
                    }
                    temp.add(snapShoot[j])
                    snapShoot.removeAt(j)
                    lineNumberMap[lastLine] = i
                    j--
                }
                j++
            }
            result.add(space.toString() + lastLine)
        }

        // 获取所有与当前任务不同的节点
        for (key in newSnapShoot.keys) {
            result.addAll(analyzeStackTrace(lineNumberMap[key]!!, level + 1, newSnapShoot[key]!!)!!)
        }
        return result
    }


}