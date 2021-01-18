package com.harrison.plugin.mvvm.base.impl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.mvvm.base.IView
import com.harrison.plugin.mvvm.core.MVVMApplication
import com.harrison.plugin.util.developer.LogUtils


open abstract class ABaseActivityView<T : ABaseViewModel> : IView, AppCompatActivity() {

    protected lateinit var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(MVVMApplication.application)
        )
            .get(getViewModelClass())
        initView()
        initViewObservable()

    }

    override fun onStart() {
        super.onStart()
        bindViewModel()
    }


    override fun onStop() {
        super.onStop()
        unBindViewModel()
    }

    abstract  fun getViewModelClass():Class<T>

    

}