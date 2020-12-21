package com.harrison.plugin.mvvm.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

open abstract class ABaseActivityView<T : ABaseViewModel> :IView, AppCompatActivity() {

    private lateinit var viewModel:T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(initViewModel())
        initViewObservable()
    }

    override fun onResume() {
        super.onResume()
        bindViewModel()
    }

    override fun onStop() {
        super.onStop()
        unBindViewModel()
    }

    override fun bindViewModel() {
        viewModel.initModel()
    }

    override fun unBindViewModel() {

    }

    /**
     *  在Android中使用xml布局使用
     */
    abstract fun getLayoutId():Int

    /**
     * 在Android 创建ViewModel使用
     */
    abstract fun initViewModel():Class<T>


}