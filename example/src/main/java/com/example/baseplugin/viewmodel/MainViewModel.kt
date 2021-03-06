package com.example.baseplugin.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.baseplugin.http.RetrofitManagerImp
import com.harrison.plugin.http.event.HttpLiveEvent
import com.harrison.plugin.util.constant.HttpResponseCode
import com.harrison.plugin.util.io.CoroutineUtils
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class MainViewModel(application: Application) : AndroidViewModel(application) {

    var httpLiveEvent = HttpLiveEvent<String>()

    fun exeTest(){
        CoroutineUtils.launchNetwork<String>({

//            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "{\"pid\":0,\"v\":0}")
            val requestBody ="{\"pid\":0,\"v\":0}".toRequestBody("application/json".toMediaTypeOrNull())
            var cookie = "sbawl3-yb4=1309806346.20480.0000; ASP.NET_SessionId=zsno2yos5ubvrn4nitjuawzz; .Xauth=FF6504FB305E6C1410B450399608BFAFAE545D6F9BC8063CF311C5F5AC0630662CFC4A2F4A4D41AFD5B3CF10D5AAAFE946E9C554E5A4F21E171BE0316139865EAF274A45CAB780BE797DA1DE982F0F93BE8B6CA552974E9D9D57B1038234431F28FEECC294403F0BCE4AF0119924E8CD3AB23FC81A17BB2AB5017E36ACB439C262829C423B4B17BB8831BE345CFD802396993EB9CD2B4F60CECA221C7DD9C46F"
            var result = RetrofitManagerImp.instance().getInformationType(cookie,requestBody)
            Log.i("result", "执行结果$result")
            result.toString()
        }, { result ->
            httpLiveEvent.setValue(HttpResponseCode.SUCCESS, result)
        }, { code: Int, error: String ->
            httpLiveEvent.setValue(HttpResponseCode.HTTP_LOCAL_DEFAULT_ERROR, null)
            Log.i("result", "执行异常  $code $error")
        })

    }

}