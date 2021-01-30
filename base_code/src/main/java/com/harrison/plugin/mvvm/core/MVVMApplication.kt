package com.harrison.plugin.mvvm.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.harrison.plugin.util.developer.LogUtils

/**
 * 用于视图的堆栈管理
 */
open abstract class MVVMApplication : Application() {


    companion object {
        lateinit var mvvmApplication: Application
        var stackTopActivity: Activity? = null

    }

    override fun onCreate() {
        super.onCreate()
        mvvmApplication = this
        initActivity()
    }

    private fun initActivity() {
        registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(p0: Activity, p1: Bundle?) {

                }

                override fun onActivityStarted(p0: Activity) {

                }

                override fun onActivityResumed(p0: Activity) {
                    stackTopActivity = p0
                }

                override fun onActivityPaused(p0: Activity) {

                }

                override fun onActivityStopped(p0: Activity) {

                }

                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                }

                override fun onActivityDestroyed(p0: Activity) {
                }
            }
        )
    }




}