package com.example.baseplugin.viewmodel

import android.app.Application
import android.util.Log
import com.example.baseplugin.http.RetrofitManager
import com.harrison.plugin.mvvm.base.impl.ABaseViewModel
import com.harrison.plugin.mvvm.core.SingleLiveEvent
import java.lang.Exception

class MainViewModel(application: Application) : ABaseViewModel(application) {

    var singleLiveEvent = SingleLiveEvent<String>()

    fun exeTest(){
        launch({
//            throw Exception("yi")
            var result =  RetrofitManager.instance().getInformationType()
            Log.i("result","执行结果$result")
        },{
            Log.i("result","执行异常 $it")
        })

    }

}