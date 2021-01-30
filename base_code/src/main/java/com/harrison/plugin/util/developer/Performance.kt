package com.kok.kuailong.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import com.harrison.plugin.util.developer.LogUtils
import java.text.DecimalFormat
import java.util.*


/**
 * 性能分析工具
 */
object Performance {

    private var isInit = false

    private const val TIME_BLOCK: Int = 100 //阈值，doFrame超过当前阀值没有被调用则做为卡顿分析,超过30-40毫秒的事件需要优化

    private const val FREQUENCY = 10 //采样频率，表示超时部分采样多少次

    /**
     * @param analyze 是否打开性能分析
     * @param showFps 是否在屏幕显示 FPS
     * @param showFPSLog 是否打印FPS Log
     *
     */
    fun init(application: Application?, showFps: Boolean, analyze: Boolean, showFPSLog: Boolean) {
        if (isInit) return
        doFrame()
        if (showFps && application != null) {
            showFPSView(application)
        }
        if (analyze) {
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

    private var countStartSecond: Long = 1
    private var fps = 1
    private var fpsCallBack: MutableList<(Int) -> Unit> = mutableListOf()

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
                var firstRequest = true
                var startActivityCount = 0

                override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
                override fun onActivityStarted(activity: Activity) {}
                override fun onActivityResumed(activity: Activity) {
                    if (Settings.canDrawOverlays(activity) && startActivityCount == 0) {
                        showFPSWindow(application)
                    } else if (firstRequest) {
                        activity.startActivityForResult(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                                    "package:" + activity.getPackageName()
                                )
                            ), 0
                        );
                    }
                    firstRequest = false
                    startActivityCount++
                }

                override fun onActivityPaused(p0: Activity) {}
                override fun onActivityStopped(activity: Activity) {
                    startActivityCount--
                    if (startActivityCount == 0) hideFPSWindow()
                }

                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
                override fun onActivityDestroyed(activity: Activity) {}
            }
        )
    }


    /**
     * 每隔16毫秒刷新一次。
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

            // 每次刷新重置快照堆栈以及快照计时
            resetSelf()

            doFrame()
        }
    }


    /**
     * ================================================================================
     * 计时工具
     * ================================================================================
     */

    private var countTimeMap: MutableMap<String, Long> = mutableMapOf()
    private var countTimeType: MutableMap<String, Boolean> = mutableMapOf()

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



    var time: Int = FREQUENCY

    // 当前快照任务堆栈队列
    var list: MutableList<Array<StackTraceElement>> = mutableListOf()

    // 分析任务堆栈队列
    var stackList: MutableList<MutableList<Array<StackTraceElement>>> = mutableListOf() // 任务堆栈
    private const val maxSize = 10  //分析任务队列最大值
    var covertStackTrace: MutableList<List<String>> = arrayListOf() //
    var resultTask: MutableList<String>? = null


    /**
     *
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
                if (time == 0) { //如果阀值时间为0表示本次快照超过了阀值时间，并且界面还没有调用doFrame刷新界面
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

    var windowManager: WindowManager? = null
    var layoutParams: WindowManager.LayoutParams? = null
    var flotLayout: FrameLayout? = null
    var showFPSView: TextView? = null

    const val  FPS_DEFAULT_TOP = 30
    const val  FPS_LAYOUT_WIDTH = 65
    const val  FPS_LAYOUT_HEIGHT = 40

    private fun showFPSWindow(application: Application) {
        if (windowManager == null) {
            windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager?

            layoutParams = WindowManager.LayoutParams().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    this.type = WindowManager.LayoutParams.TYPE_PHONE
                }
                var density = application.resources.displayMetrics.density
                this.format = PixelFormat.RGBA_8888
                this.gravity = Gravity.LEFT or Gravity.TOP
                this.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                this.width = (FPS_LAYOUT_WIDTH*density).toInt()
                this.height = (FPS_LAYOUT_HEIGHT*density).toInt()
                this.x = 0
                this.y = (FPS_DEFAULT_TOP*density).toInt()
            }

            showFPSView = TextView(application).apply {
                var frameParameter = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                frameParameter.gravity = Gravity.CENTER
                this.layoutParams = frameParameter
//                setLineSpacing(0f,0.8f)
            }

            flotLayout = FrameLayout(application).apply {
                var frameParameter = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                this.layoutParams = frameParameter
                background = ColorDrawable(Color.parseColor("#3B3B3B"))
                addView(showFPSView)
                setOnTouchListener(FloatingOnTouchListener())
            }
        }
        windowManager?.addView(flotLayout, layoutParams)

        addFPSCallBack(Performance::fpsWindowCallBack)
    }

    fun fpsWindowCallBack(fps: Int) {
        if (flotLayout == null) return

        var showBuilder = SpannableStringBuilder()

        var fpsColorStauts =
            when {
                fps > 40 -> {
                    "#B5DF3A"
                }
                fps <= 40 && fps > 25 -> {
                    "#E97F02"
                }
                else -> {
                    "#EF5285"
                }
            }
        val fpsValueSpan = SpannableString("$fps")
        val fpsValueSpanColor = ForegroundColorSpan(Color.parseColor(fpsColorStauts))
        fpsValueSpan.setSpan(
            fpsValueSpanColor,
            0,
            "$fps".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        showBuilder.append(fpsValueSpan)
        showBuilder.append("   ")

        val fpsKeySpan = SpannableString("FPS")
        val fpsKeySpanColor = ForegroundColorSpan(Color.WHITE)
        fpsKeySpan.setSpan(fpsKeySpanColor, 0, "FPS".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        showBuilder.append(fpsKeySpan)

        showBuilder.append("\n")

        val totalMemory = DecimalFormat("#.00").format(
            (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024)).toFloat()
        ).toString()
        val manerayValueSpan = SpannableString(totalMemory)
        val manerayValueSpanColor = ForegroundColorSpan(Color.parseColor("#B5DF3A"))
        manerayValueSpan.setSpan(
            manerayValueSpanColor,
            0,
            totalMemory.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        showBuilder.append(manerayValueSpan)
        showBuilder.append(" ")

        val manerayKeySpan = SpannableString("M")
        val manerayKeySpanColor = ForegroundColorSpan(Color.WHITE)
        manerayKeySpan.setSpan(
            manerayKeySpanColor,
            0,
            "M".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        showBuilder.append(manerayKeySpan)

        showFPSView?.text = showBuilder

    }

    private fun hideFPSWindow() {
        windowManager?.removeView(flotLayout)
        removeFPSCallBack(Performance::fpsWindowCallBack)
    }

    private class FloatingOnTouchListener : View.OnTouchListener {
        private var x = 0
        private var y = 0
        override fun onTouch(view: View?, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    if(layoutParams == null)return false
                    layoutParams!!.x = layoutParams!!.x  + movedX
                    layoutParams!!.y = layoutParams!!.y  + movedY
                    windowManager?.updateViewLayout(view, layoutParams)
                }
                MotionEvent.ACTION_UP -> {
                    view?.context?.resources?.displayMetrics?.widthPixels?.let {mWidth ->
                        if(event.rawX.toInt() > mWidth/2){
                            layoutParams!!.x = (mWidth-FPS_LAYOUT_WIDTH*view.context.resources.displayMetrics.density).toInt()
                        }else{
                            layoutParams!!.x = 0
                        }
                    }
                    windowManager!!.updateViewLayout(view, layoutParams)
                }
                else -> {

                }
            }
            return false
        }
    }

}