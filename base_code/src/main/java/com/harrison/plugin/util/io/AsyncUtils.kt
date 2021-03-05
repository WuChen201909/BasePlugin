package com.harrison.plugin.util.io

import okhttp3.internal.notifyAll
import okhttp3.internal.wait

class AsyncUtils {

    fun lock(){
        synchronized(this){
            wait()
        }
    }

    fun unlock(){
        synchronized(this){
            notifyAll()
        }
    }
}