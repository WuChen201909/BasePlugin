package com.example.baseplugin.view

import com.example.baseplugin.R
import com.harrison.plugin.mvvm.base.BaseActivityView
import com.harrison.plugin.mvvm.base.BaseViewModel

class SecondActivity: BaseActivityView<BaseViewModel>() {
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }


    override fun getViewLayout(): Any {
        return R.layout.activity_second
    }

    override fun viewCreated() {

    }

}