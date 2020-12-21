package com.example.baseplugin

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.harrison.plugin.mvvm.base.impl.ABaseActivityView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.Exception

class MainActivity : ABaseActivityView<MainViewModel>(){


    override fun initView() {
        setContentView(R.layout.activity_main)
        button_jump_to_mvvm.setOnClickListener(onClickListener)
        button_jump_to_second.setOnClickListener(onClickListener)
        button_start_request.setOnClickListener(onClickListener)
    }

    override fun initViewObservable() {
        viewModel.launch(

        );
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

            }
            R.id.button_start_request ->{


            }
        }
    }

}