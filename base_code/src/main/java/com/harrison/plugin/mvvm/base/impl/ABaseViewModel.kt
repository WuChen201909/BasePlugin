package com.harrison.plugin.mvvm.base.impl

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.harrison.plugin.mvvm.base.IViewModel
import com.harrison.plugin.util.io.CoroutineUtils
import kotlinx.coroutines.CoroutineScope
import org.json.JSONException
import java.io.IOException
import java.lang.Exception


open class ABaseViewModel(application: Application) : IViewModel,
    AndroidViewModel(application) {


    fun <T> launch(async: suspend CoroutineScope.() -> T, callResult: (error:T) -> Unit, callError: (code:Int,error:String) -> Unit){
        CoroutineUtils.launchIO {
            try {
                var re =  async()
                CoroutineUtils.launchMain {
                    callResult(re)
                }
            } catch (e:Exception){
                var describe = ""
                when(e){
                    is IOException -> {
                        describe = "网络异常"
                    }
                    is JSONException ->{
                        describe = "解析异常"
                    }
                }
                CoroutineUtils.launchMain {
                    callError(-200,"$describe\n:$e")
                }
            }
        }
    }



}

