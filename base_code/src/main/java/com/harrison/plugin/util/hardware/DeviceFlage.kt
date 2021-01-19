package com.harrison.plugin.util.hardware

import android.content.Context
import java.util.*

/**
 * 用于设备唯一编号
 */
object DeviceFlage {

    private const val  SP_FILE = "UUID_FILE"
    private const val UUID_KEY = "UUID_KEY"
    lateinit var M_UUID:String

    fun initUUID(context: Context){
        var uuid:String = ""
        var sp =   context.getSharedPreferences(SP_FILE,Context.MODE_PRIVATE)
        uuid = sp.getString(UUID_KEY,"").toString()
        if (uuid.isEmpty()){
            var edit = sp.edit()
            uuid = UUID.randomUUID().toString()
            edit.putString(UUID_KEY,uuid)
            edit.commit()
        }
        M_UUID =  uuid
    }

}