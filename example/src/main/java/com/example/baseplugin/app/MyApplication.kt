package com.example.baseplugin.app

import com.harrison.plugin.mvvm.core.MVVMApplication
import com.harrison.plugin.util.developer.LogUtils
import com.harrison.plugin.util.developer.Performance

class MyApplication : MVVMApplication() {

    override fun onCreate() {
        super.onCreate()

        Performance.showFPSView(this)

        LogUtils.init(true,"result")
        LogUtils.i("打印日志")
        Performance.init(true)
    }

}