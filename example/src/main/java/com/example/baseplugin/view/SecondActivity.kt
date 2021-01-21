package com.example.baseplugin.view

import androidx.appcompat.app.AppCompatActivity
import com.example.baseplugin.R
import com.harrison.plugin.mvvm.base.impl.BaseActivityView
import com.harrison.plugin.mvvm.base.impl.BaseViewModel

class SecondActivity: BaseActivityView<BaseViewModel>() {
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun initView() {

    }

    override fun initViewObservable() {

    }

    override fun bindViewModel() {

    }

    override fun unBindViewModel() {

    }

    override fun getViewLayout(): Any {
        return R.layout.activity_second
    }

}