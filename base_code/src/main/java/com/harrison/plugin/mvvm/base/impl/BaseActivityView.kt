package com.harrison.plugin.mvvm.base.impl

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.mvvm.base.IView
import com.harrison.plugin.mvvm.core.MVVMApplication
import com.harrison.plugin.util.io.CoroutineUtils
import kotlinx.coroutines.CoroutineScope
import java.lang.Exception


open abstract class BaseActivityView<T : BaseViewModel> : IView, AppCompatActivity() {

    protected lateinit var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(MVVMApplication.mvvmApplication)
        )
            .get(getViewModelClass())

        initViewObservable()

        var view = getViewLayout()
        if(view is View){
            setContentView(view)
            initView()
        }else if(view is Int){
//            var resultView :View? = null
//            CoroutineUtils.launchIO {
//                resultView = layoutInflater.inflate(view ,null)
//                CoroutineUtils.launchMain {
//                    setContentView(resultView)
//                    initView()
//                }
//            }
            var resultView :View? = null
            Thread{
                resultView = layoutInflater.inflate(view ,null)
                var handle = Handler(Looper.getMainLooper(), Handler.Callback {

                    setContentView(resultView)
                    initView()
                     false
                })
                handle.sendEmptyMessage(0)
            }.start()

        }else{
            throw Exception("please set view layout on this page")
        }

    }

    override fun onStart() {
        super.onStart()
        bindViewModel()
    }

    override fun onStop() {
        super.onStop()
        unBindViewModel()
    }


    abstract fun getViewModelClass():Class<T>
    abstract fun getViewLayout():Any



}