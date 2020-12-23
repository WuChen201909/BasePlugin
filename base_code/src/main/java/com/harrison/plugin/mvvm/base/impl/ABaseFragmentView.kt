package com.harrison.plugin.mvvm.base.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.mvvm.base.IView
import com.harrison.plugin.mvvm.core.MVVMApplication


open abstract class ABaseFragmentView<T : ABaseViewModel> : IView, Fragment() {

    private lateinit var viewModel: T
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(MVVMApplication.application)
        )
            .get(viewModel.javaClass)
        initView()
        initViewObservable()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        bindViewModel();
    }

    override fun onStop() {
        super.onStop()
        unBindViewModel()
    }
    
}