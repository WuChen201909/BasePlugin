package com.example.baseplugin.view

import android.content.Intent
import android.view.View
import com.example.baseplugin.R
import com.example.baseplugin.viewmodel.MainViewModel
import com.harrison.plugin.mvvm.base.impl.ABaseActivityView
import com.harrison.plugin.util.developer.LogUtils
import com.harrison.plugin.util.developer.Performance

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ABaseActivityView<MainViewModel>(){


    override fun initView() {
        setContentView(R.layout.activity_main)
        button_jump_to_mvvm.setOnClickListener(onClickListener)
        button_jump_to_second.setOnClickListener(onClickListener)
        button_start_request.setOnClickListener(onClickListener)

        Performance.showFPSView(this)
    }

    override fun initViewObservable() {
        viewModel.httpLiveEvent.observe(this){ state, value ->
            LogUtils.i("网络请求状态 $state  $value")
        }
    }

    override fun bindViewModel() {

    }

    override fun unBindViewModel() {

    }

    private var onClickListener = View.OnClickListener {


        when (it.id) {
            R.id.button_jump_to_mvvm -> {
            }
            R.id.button_jump_to_second ->{
                var intent = Intent(this,SecondActivity::class.java)
                startActivity(intent)
            }
            R.id.button_start_request ->{
                viewModel.exeTest()

            }
        }
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

}