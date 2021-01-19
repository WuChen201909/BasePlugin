package com.harrison.plugin.mvvm.base.impl

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.mvvm.base.IView
import com.harrison.plugin.mvvm.core.MVVMApplication
import java.lang.Exception


open abstract class BaseActivityView<T : BaseViewModel> : IView, AppCompatActivity() {

    protected lateinit var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = getViewLayout()
        if(view is View){
            setContentView(view)
        }else if(view is Int){
            setContentView(view)
        }else{
            throw Exception("please set view layout on this page")
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(MVVMApplication.application)
        )
            .get(getViewModelClass())

        initViewObservable()

        initView()

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