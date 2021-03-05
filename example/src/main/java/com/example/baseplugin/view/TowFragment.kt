package com.example.baseplugin.view

import com.example.baseplugin.R
import com.harrison.plugin.mvvm.BaseFragmentView
import kotlinx.android.synthetic.main.frgment_tow.*

class TowFragment : BaseFragmentView() {
    override fun getViewLayout(): Any {
        return  R.layout.frgment_tow
    }

    override fun viewCreated() {
        btn_02.setOnClickListener {
//            newNavigator()
        }
    }
}