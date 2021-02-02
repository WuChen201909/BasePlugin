package com.harrison.plugin.util.component

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

object IntentUtils {

    /**
     *  通过类名跳转到指定类
     */
    fun intentToActivityByClassName(context:Context,packageName:String,activityName:String){
        var intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        var component = ComponentName(packageName, activityName)
        intent.component = component
        context.startActivity(intent)
    }
    
    /**
     * 跳转到系统设置：开启辅助服
     */
    fun intentToAccessibilitySetting(cxt: Context) {
        try {
            cxt.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        } catch (e: Throwable) { //若出现异常，则说明该手机设置被厂商篡改了,需要适配
            try {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                cxt.startActivity(intent)
            } catch (e2: Throwable) {
                Log.e("result", "jumpToSetting: " + e2.message)
            }
        }
    }


}