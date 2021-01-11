package com.harrison.plugin.util.developer

import android.view.Choreographer
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

object Performance {

    private var  isInit =false

    private  var countStartSecond: Long = 1
    private  var fps = 1
    private var fpsCallBack :MutableList<(Int)->Unit> = mutableListOf()

    var countTimeMap: MutableMap<String, Long> = mutableMapOf()
    var countTimeType: MutableMap<String, Boolean> = mutableMapOf()

    fun init(showFPS:Boolean){
        if(isInit)return
        doFrame()
        if(showFPS)showFPSLog()
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
    fun addFPSCallBack(callBack:(Int)->Unit){
        fpsCallBack.add(callBack)
    }
    fun removeFPSCallBack(callBack:(Int)->Unit){
        fpsCallBack.remove(callBack)
    }

    /**
     * 打印FPS日志
     */
    private fun showFPSLog(){
        addFPSCallBack {
            LogUtils.i("fps: [$it]")
        }
    }

    /**
     * 将FPS显示在屏幕上
     */
    fun showFPSView(activity: AppCompatActivity){
        var showView = TextView(activity)
        showView.text = "显示测试内通"
        var layoutParameter = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        showView.layoutParams = layoutParameter

        activity.window.decorView.findViewById<FrameLayout>(android.R.id.content).addView(showView)

        addFPSCallBack {
            showView.text = "fps: [$it]"
        }

    }
    /**
     * 开始响应屏幕刷新事件
     */
    private fun doFrame(){
        Choreographer.getInstance().postFrameCallback {
            val t = System.currentTimeMillis()
            if (countStartSecond == 1L) {
                countStartSecond = t
            }
            if (countStartSecond / 1000 == t / 1000) {
                fps++
            } else if (t / 1000 - countStartSecond / 1000 >= 1) {
                for (item in fpsCallBack){
                    item(fps)
                }
                fps = 1
                countStartSecond = t
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
            var currentTime = if(countTimeType[tag] == null || !countTimeType[tag]!!){
                System.currentTimeMillis()
            }else{
                System.nanoTime()
            }
            return  currentTime  - startTime
        }
        return -1
    }

    /**
     * ================================================================================
     * 卡顿检测工具
     * ================================================================================
     */




}