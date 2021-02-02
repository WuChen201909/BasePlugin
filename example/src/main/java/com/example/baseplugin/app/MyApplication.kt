package com.example.baseplugin.app

import com.harrison.plugin.mvvm.core.MVVMApplication
import com.harrison.plugin.util.developer.LogUtils
import com.kok.kuailong.utils.Performance

class MyApplication : MVVMApplication() {

    override fun onCreate() {
        super.onCreate()


        LogUtils.init(true,"test_demo")
        LogUtils.i("打印日志")

        Performance.init(this, showFps = true, analyze = false, showFPSLog = false)

    }

}