package com.harrison.plugin.util.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import java.lang.Exception

/**
 * 全屏工具
 *   对应Activity配置
 *       android:configChanges="orientation|screenSize"   避免横竖屏切换重新执行生命周期
 *       android:screenOrientation="portrait"             固定屏幕方向
 *   用法
 *      1、通过构造函数指定全屏的Activity和需要控制全屏的控件
 *      2、调用chanOrientation函数控制显示是否全屏
 *
 * @param activity
 * @param view 需要全屏的控件
 */
class FullScreenUtils(var activity: Activity, var view: View) {

    private var parent: ViewGroup? = null  // 需要全屏控件的父控件
    private var viewIndex: Int = -1        //需要全屏控件在父控件中的位置
    private var smallLayoutParameter: ViewGroup.LayoutParams? = null
    private var fullLayoutParameter: ViewGroup.LayoutParams? = null
    var isFullScreen: Boolean = false      //当前全屏状态
        private set

    /**
     * ==========================================================================
     *  外部调用函数
     * ==========================================================================
     */

    /**
     * @param isFullScreen 表示操作全屏还是非全屏
     * @param orientation 全屏时屏幕方向，默认为空时全屏为横屏
     */
    fun chanOrientation(isFullScreen: Boolean, orientation: Orientation? = null) {
        if (this.isFullScreen == isFullScreen) return
        this.isFullScreen = isFullScreen
        var to = orientation ?: if (isFullScreen) Orientation.HORIZONTAL else Orientation.VERTICAL

        changeStatusBar()
        changeScreenOrientation(to)
        changeViewPlace()
    }

    /**
     * 获取当前屏幕方向
     *  作为外部工具函数使用
     */
    fun getOrientation(): Orientation {
        return if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            Orientation.VERTICAL
        else
            Orientation.HORIZONTAL

    }

    enum class Orientation {
        HORIZONTAL,
        VERTICAL
    }

    /**
     * ==========================================================================
     *  内部业业务逻辑
     * ==========================================================================
     */
    init {
        parent = view.parent as ViewGroup
        parent?.let {
            viewIndex = it.indexOfChild(view)
            smallLayoutParameter = view.layoutParams
            fullLayoutParameter = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
    /**
     * 修改状态栏显示
     */
    private fun changeStatusBar() {
        if (isFullScreen) {
            val decorView: View = activity.window.decorView
            if (Build.VERSION.SDK_INT in 12..18) { // lower api
                decorView.systemUiVisibility = View.GONE
            } else if (Build.VERSION.SDK_INT >= 19) {
                val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
                decorView.systemUiVisibility = uiOptions
            }
        } else {
            val decorView = activity.window.decorView ?: return
            if (Build.VERSION.SDK_INT in 12..18) { // lower api
                decorView.systemUiVisibility = View.VISIBLE
            } else if (Build.VERSION.SDK_INT >= 19) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    /**
     * 修改屏幕方向
     */
    private fun changeScreenOrientation(orientation: Orientation) {
        if (getOrientation() == orientation) return
        if (orientation == Orientation.HORIZONTAL) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * 修改视图显示位置
     */
    private fun changeViewPlace() {
        if (parent == null || viewIndex < 0) throw Exception("未找到父控件")
        val decorView: FrameLayout = activity.window.decorView as FrameLayout
        if (isFullScreen) {
            parent!!.removeView(view)
            view.layoutParams = fullLayoutParameter
            decorView.addView(view)
        } else {
            decorView.removeView(view)
            view.layoutParams = smallLayoutParameter
            parent!!.addView(view, viewIndex)
        }
    }






}