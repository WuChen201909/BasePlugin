package com.harrison.plugin.mvvm.base.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.mvvm.base.IView
import com.harrison.plugin.mvvm.core.MVVMApplication
import java.lang.Exception


open abstract class BaseFragmentView<T : BaseViewModel> : IView, Fragment() {

    lateinit var viewModel: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = getViewLayout()
        if (view is View) {
            return view
        } else if (view is Int) {
            return layoutInflater.inflate(view, null)
        } else {
            throw  Exception("please set view layout on this page")
            return null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(MVVMApplication.mvvmApplication)
        ).get(getViewModelClass())

        initViewObservable()

        initView()
    }

    override fun onStart() {
        super.onStart()
        bindViewModel();
    }

    override fun onStop() {
        super.onStop()
        unBindViewModel()
    }

    abstract fun getViewModelClass(): Class<T>
    abstract fun getViewLayout(): Any
}