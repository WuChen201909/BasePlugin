package com.harrison.plugin.mvvm.core

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.harrison.plugin.mvvm.base.IView
import com.harrison.plugin.mvvm.core.provider.IViewProvider
import kotlin.collections.ArrayList

/**
 * Android 中 View 提供者
 */
object AViewProvider : IViewProvider {

    var activityStack:MutableList<Activity> = ArrayList()

    /**
     * Android中加入栈
     */
    fun <T :Activity> pushBack(view: Class<T>) {
        var intent:Intent = Intent(activityStack.last(),view)
        activityStack.last().startActivity(intent)
    }

    fun <T:Activity> pushBack(view:Class<T>,bundle: Bundle){
        var intent:Intent = Intent(activityStack.last(),view)
        intent.putExtras(bundle)
        activityStack.last().startActivity(intent)
    }

    /**
     * Android 中退出栈顶
     */
    fun popView(view: Activity){
        view.finish()
    }

    /**
     * 在安卓中不使用，只作为接口设计
     */
    override fun pushBack(view: IView) {
    }

    /**
     * 在安卓中不使用，只作为接口设计
     */
    override fun popView(view: IView) {
    }

    fun pushActivity(activity: Activity){
        activityStack.add(activity)
    }

    fun popActivity(activity: Activity){
        activityStack.remove(activity)
    }

}