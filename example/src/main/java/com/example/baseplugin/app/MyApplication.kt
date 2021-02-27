package com.example.baseplugin.app

import android.app.Application
import com.harrison.plugin.util.developer.LogUtils
import com.kok.kuailong.utils.Performance

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        LogUtils.init(true,"test_demo")
        LogUtils.i("打印日志")

        Performance.init(this, showFps = true, analyze = true, showFPSLog = false)

    }

}