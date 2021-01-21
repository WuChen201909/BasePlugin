package com.example.baseplugin.model

import com.example.baseplugin.R
import com.harrison.plugin.mvvm.base.impl.BaseActivityView
import com.harrison.plugin.mvvm.base.impl.BaseViewModel

class SecondActivity : BaseActivityView<BaseViewModel>() {
    override fun initView() {

    }

    override fun initViewObservable() {

    }

    override fun bindViewModel() {

    }

    override fun unBindViewModel() {

    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun getViewLayout(): Any {
        return R.layout.activity_second
    }


}