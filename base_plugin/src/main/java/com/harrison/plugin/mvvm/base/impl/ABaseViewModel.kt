package com.harrison.plugin.mvvm.base.impl

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.harrison.plugin.mvvm.base.IViewModel
import com.harrison.plugin.util.KLog
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.Exception
import kotlin.coroutines.CoroutineContext


open class ABaseViewModel(application: Application) : IViewModel,
    AndroidViewModel(application) ,
    CoroutineScope by MainScope() {



    fun launch(block: suspend CoroutineScope.() -> Unit,error: (error:String) -> Unit){

        var exceptionHandler = CoroutineExceptionHandler {
                _ : CoroutineContext, throwable: Throwable ->
            error(throwable.message.toString())
            KLog.printException(throwable)
        }
        launch(exceptionHandler){

        }
    }



}

