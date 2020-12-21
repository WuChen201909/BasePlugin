package com.example.baseplugin

import com.harrison.plugin.mvvm.base.impl.ABaseActivityView
import com.harrison.plugin.mvvm.base.impl.ABaseViewModel

class SecondActivity : ABaseActivityView<ABaseViewModel>() {

    override fun initViewObservable() {

    }

    override fun bindViewModel() {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_second
    }

    override fun initViewModel(): Class<ABaseViewModel> {
        return ABaseViewModel::class.java
    }


}