package com.kok.kuailong.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Choreographer
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.harrison.plugin.util.developer.LogUtils
import java.text.DecimalFormat
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
     * @param analyze 是否打开性能分析
     * @param showFps 是否在屏幕显示 FPS
     * @param showFPSLog 是否打印FPS Log
     *
     */
    fun init(application: Application?, analyze: Boolean, showFps: Boolean, showFPSLog: Boolean) {
        if (isInit) return
        doFrame()
        if (showFps && application != null) {
            showFPSView(application)
        }
        if (analyze) {
            this.analyze = analyze
            startSnapShootThread()
            startAnalyzeMainThread()
        }
        if (showFPSLog) {
            showFPSLog()
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
            LogUtils.e("fps: [$it]")
        }
    }

    /**
     * 将FPS显示在屏幕上
     */
    fun showFPSView(application: Application) {
        application.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {
                var callBackStack = hashMapOf<Activity, (Int) -> Unit>()

                init {
                    addFPSCallBack {
                        for ((key, value) in callBackStack) {
                            value(it)
                        }
                    }
                }

                override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
                override fun onActivityStarted(activity: Activity) {
                    if (!callBackStack.containsKey(activity)) {
                        callBackStack[activity] = addFPSView(activity)
                    }
                }

                override fun onActivityResumed(p0: Activity) {}
                override fun onActivityPaused(p0: Activity) {}
                override fun onActivityStopped(activity: Activity) {}
                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
                override fun onActivityDestroyed(activity: Activity) {
                    callBackStack.remove(activity)
                }
            }
        )
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

    private const val TIME_BLOCK: Int = 100 //阈值，doFrame超过当前阀值没有被调用则做为卡顿分析

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
        var snapShootThread = Thread {
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
        }
        snapShootThread.name = "snapShoot"
        snapShootThread.start()
    }

    /**
     * 检测线程
     */
    private fun startAnalyzeMainThread() {
        var analyzeThread = Thread {
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
                if (resultTask == null) continue
                val resultBuffer = StringBuffer()
                resultBuffer.append("=========================================\n")
                resultBuffer.append("应用出现了卡顿以下是造成本次卡顿的堆栈信息：\n")
                // 打印结果
                for (i in resultTask!!.indices) {
                    resultBuffer.append("${resultTask!![i]} \n")
                }
                resultBuffer.append("=========================================\n")
                LogUtils.e(resultBuffer.toString())
            }
        }
        analyzeThread.name = "analyze"
        analyzeThread.start()
    }

    /**
     * 添加分析任务到任务队列
     */
    private fun addSnapShoot(list: MutableList<Array<StackTraceElement>>) {
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
        if (snapShoot.size == 0) return null
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
            val lastLine = snapShoot[0][i]  // 第0个作为对照
            var j = 1                       // 从第一个开始与第0个对照
            var count = 1
            while (j < snapShoot.size) {
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
                } else {
                    count++
                    j++
                }
            }
            result.add("$space$lastLine[$count]")
        }

        // 获取所有与当前任务不同的节点
        for (key in newSnapShoot.keys) {
            result.addAll(analyzeStackTrace(lineNumberMap[key]!!, level + 1, newSnapShoot[key]!!)!!)
        }
        return result
    }


    /**
     * ============================================================================
     * 性能显示的视图结构
     * ============================================================================
     */

    private fun addFPSView(activity: Activity): (Int) -> Unit {
        var decorView = activity.window.decorView.findViewById<FrameLayout>(android.R.id.content)

        var showRootLayout = TouchMoveLayout(activity)
        var layoutParamet = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        showRootLayout.layoutParams = layoutParamet;

        var showLayout = LinearLayout(activity)
        layoutParamet = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        showLayout.layoutParams = layoutParamet

        showLayout.setBackgroundColor(Color.parseColor("#99808A87"))

        var showView = TextView(activity)
        showView.setTextColor(Color.RED)
        var layoutParameter = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        showView.layoutParams = layoutParameter

        showLayout.addView(showView)
        showRootLayout.addView(showLayout)
        decorView.addView(showRootLayout)
        return {
            val totalMemory = (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024)).toFloat()
            showView.text = "FPS:$it \nM:${DecimalFormat("#.00").format(Math.abs(totalMemory))}"
        }
    }

    class TouchMoveLayout(context: Context) : FrameLayout(context) {

        var startX: Float = 0f
        var startY: Float = 0f
        var startLeft: Float = 0f
        var startTop: Float = 0f

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            super.onLayout(changed, left, top, right, bottom)
        }

        override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
            return onTouchEvent(ev)
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            var moveView = getChildAt(0)
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y

                    startLeft = moveView.left.toFloat()
                    startTop = moveView.top.toFloat()
                }
                MotionEvent.ACTION_MOVE -> {
                    var layoutParames = moveView.layoutParams as FrameLayout.LayoutParams
                    layoutParames.leftMargin = ((event.x-startX)+startLeft).toInt()
                    layoutParames.topMargin = ((event.y-startY)+startTop).toInt()
                    requestLayout()
                }
                MotionEvent.ACTION_UP -> {
                    // 贴边操作
                    var layoutParames = moveView.layoutParams as FrameLayout.LayoutParams
                    if((event.x-startX)+startLeft > resources.displayMetrics.widthPixels/2){
                        layoutParames.leftMargin = resources.displayMetrics.widthPixels - moveView.measuredWidth
                    }else{
                        layoutParames.leftMargin = 0
                    }
                    layoutParames.topMargin = ((event.y-startY)+startTop).toInt()
                    requestLayout()

                    startX = 0f
                    startY = 0f
                }
            }

            if (event?.x!! > moveView.left
                && event?.y!! > moveView.top
                && event?.x!! <= moveView.left + moveView.measuredWidth
                && event?.y!! <= moveView.top + moveView.measuredHeight
            ) {
                return true
            } else {
                return false
            }
        }

    }

}