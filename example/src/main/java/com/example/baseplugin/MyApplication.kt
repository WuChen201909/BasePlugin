package com.example.baseplugin

import com.harrison.plugin.mvvm.core.MVVMApplication
import com.harrison.plugin.util.KLog

class MyApplication : MVVMApplication() {

    override fun onCreate() {
        super.onCreate()
        KLog.init(true)
        KLog.i("打印日志")
    }


}