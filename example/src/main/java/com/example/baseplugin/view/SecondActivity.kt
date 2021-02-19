package com.example.baseplugin.view

import androidx.lifecycle.AndroidViewModel
import com.example.baseplugin.R
import com.harrison.plugin.mvvm.BaseActivityView

class SecondActivity: BaseActivityView() {

    override fun getViewLayout(): Any {
        return R.layout.activity_second
    }

    override fun viewCreated() {

    }

}