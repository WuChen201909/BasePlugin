package com.example.baseplugin

import com.harrison.plugin.mvvm.core.BaseApplication
import com.harrison.plugin.util.KLog

class MyApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        KLog.init(true)
        KLog.i("打印日志")
    }


}