package com.harrison.plugin.util

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.util.Log

object ServiceUtils {


    /**
     * 检查系统设置：是否开启辅助服务
     * @param service 辅助服务
     */
    private fun isSettingOpen(service: Class<*>, cxt: Context): Boolean {
        try {
            val enable: Int = Settings.Secure.getInt(cxt.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0)
            if (enable != 1) return false
            val services: String = Settings.Secure.getString(cxt.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (!TextUtils.isEmpty(services)) {
                val split = TextUtils.SimpleStringSplitter(':')
                split.setString(services)
                while (split.hasNext()) { // 遍历所有已开启的辅助服务名
                    if (split.next().equals(cxt.getPackageName().toString() + "/" + service.name, ignoreCase = true)) return true
                }
            }
        } catch (e: Throwable) { //若出现异常，则说明该手机设置被厂商篡改了,需要适配
            Log.e("result", "isSettingOpen: " + e.message)
        }
        return false
    }


}