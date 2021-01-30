package com.example.baseplugin.viewmodel

import android.app.Application
import android.util.Log
import com.example.baseplugin.http.RetrofitManager
import com.harrison.plugin.util.constant.HttpResponseCode
import com.harrison.plugin.mvvm.base.BaseViewModel
import com.harrison.plugin.mvvm.event.HttpLiveEvent
import com.harrison.plugin.util.io.CoroutineUtils

class MainViewModel(application: Application) : BaseViewModel(application) {

    var httpLiveEvent = HttpLiveEvent<String>()

    fun exeTest(){
        CoroutineUtils.launchNetwork<String>({
            var result =  RetrofitManager.instance().getInformationType()
            Log.i("result","执行结果$result")
            result.toString()
        },{ result ->
            httpLiveEvent.setValue(HttpResponseCode.SUCCESS, result)
        },{ code: Int, error: String ->
            httpLiveEvent.setValue(HttpResponseCode.HTTP_LOCAL_DEFAULT_ERROR, null)
            Log.i("result","执行异常  $code $error")
        })
    }

}