package com.example.baseplugin.viewmodel

import android.app.Application
import android.util.Log
import com.example.baseplugin.http.RetrofitManager
import com.harrison.plugin.constant.HttpResponseCode
import com.harrison.plugin.mvvm.base.impl.ABaseViewModel
import com.harrison.plugin.mvvm.event.HttpLiveEvent

class MainViewModel(application: Application) : ABaseViewModel(application) {

    var httpLiveEvent = HttpLiveEvent<String>()

    fun exeTest(){
        launch({
            var result =  RetrofitManager.instance().getInformationType()
            Log.i("result","执行结果$result")
            httpLiveEvent.setValue(HttpResponseCode.SUCCESS, result.toString())
        },{
            httpLiveEvent.setValue(HttpResponseCode.HTTP_LOCAL_DEFAULT_ERROR, null)
            Log.i("result","执行异常 $it")
        })
    }

}