package com.example.baseplugin.view

import androidx.appcompat.app.AppCompatActivity
import com.example.baseplugin.R
import com.harrison.plugin.mvvm.base.impl.ABaseActivityView
import com.harrison.plugin.mvvm.base.impl.ABaseViewModel

class SecondActivity: ABaseActivityView<ABaseViewModel>() {
    override fun getViewModelClass(): Class<ABaseViewModel> {
        return ABaseViewModel::class.java
    }

    override fun initView() {
        setContentView(R.layout.activity_second)
    }

    override fun initViewObservable() {

    }

    override fun bindViewModel() {

    }

    override fun unBindViewModel() {

    }

}