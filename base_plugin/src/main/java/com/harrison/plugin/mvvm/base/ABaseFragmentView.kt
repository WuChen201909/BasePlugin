package com.harrison.plugin.mvvm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


open abstract class ABaseFragmentView <T : ABaseViewModel> :IView, Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initViewObservable() {
    }

    override fun bindViewModel() {
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