package com.example.baseplugin.model

import com.harrison.plugin.mvvm.base.impl.ABaseActivityView
import com.harrison.plugin.mvvm.base.impl.ABaseViewModel

class SecondActivity : ABaseActivityView<ABaseViewModel>() {
    override fun initView() {

    }

    override fun initViewObservable() {

    }

    override fun bindViewModel() {

    }

    override fun unBindViewModel() {

    }

    override fun getViewModelClass(): Class<ABaseViewModel> {
        return ABaseViewModel::class.java
    }


}