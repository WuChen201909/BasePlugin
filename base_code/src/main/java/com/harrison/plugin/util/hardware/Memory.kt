package com.harrison.plugin.util.hardware

import android.app.ActivityManager
import android.content.Context
import com.harrison.plugin.util.developer.LogUtils


/**
 * 内存工具
 *
 * <application
 *   android:largeHeap="true">   添加后可以提高最大使用内存
 */
object Memory {

    /**
     * 显示当前设别的内存信息
     */
     fun showMemoryInfoOnLog(context: Context) {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val info = ActivityManager.MemoryInfo()
        manager!!.getMemoryInfo(info)
        LogUtils.e("==========系统内存信息===========")
        LogUtils.e("系统总内存:" + info.totalMem / (1024 * 1024) + "M")
        LogUtils.e("系统剩余内存:" + info.availMem / (1024 * 1024) + "M")
        LogUtils.e("系统是否处于低内存运行：" + info.lowMemory)
        LogUtils.e("系统剩余内存低于" + info.threshold / (1024 * 1024) + "M时为低内存运行")
        LogUtils.e("=============================")
        LogUtils.e("")
        LogUtils.e("==========应用内存信息===========")
        val rt = Runtime.getRuntime()
        val maxMemory = rt.maxMemory()/ (1024 * 1024)
        val totalMemory = (rt.totalMemory() * 1.0 / (1024 * 1024)).toFloat()
        val freeMemory = (rt.freeMemory() * 1.0 / (1024 * 1024)).toFloat()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        LogUtils.e("Runnable Max Memory:$maxMemory")
        LogUtils.e("Runnable Current Memory:$totalMemory")
        LogUtils.e("Runnable Free Memory:$freeMemory")
        LogUtils.e("Memory Class:" + activityManager!!.memoryClass.toLong().toString())
        LogUtils.e("Large Memory Class:" + activityManager.largeMemoryClass.toLong().toString())
        LogUtils.e("=============================")
    }


}