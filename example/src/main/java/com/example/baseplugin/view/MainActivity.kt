package com.example.baseplugin.view

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import com.example.baseplugin.R
import com.example.baseplugin.viewmodel.MainViewModel
import com.harrison.plugin.mvvm.base.impl.ABaseActivityView
import com.harrison.plugin.util.developer.LogUtils
import com.harrison.plugin.util.developer.Performance
import com.harrison.plugin.util.hardware.Memory
import com.harrison.plugin.util.io.CoroutineUtils

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : ABaseActivityView<MainViewModel>(){


    override fun initView() {

        Performance.startCountTime("1")
        setContentView(R.layout.activity_main)
        LogUtils.e("result", "count time:" + Performance.endCountTime("1"))

        button_jump_to_mvvm.setOnClickListener(onClickListener)
        button_jump_to_second.setOnClickListener(onClickListener)
        button_start_request.setOnClickListener(onClickListener)

//        Thread{
//            Looper.prepare()
//            Performance.startCountTime("1",true)
//            Performance.startCountTime("3",true)
//            CoroutineUtils.launchIO {
//                LogUtils.e("计时01  ：  "+Performance.endCountTime("1"))
//
//                Performance.startCountTime("2",true)
//                CoroutineUtils.launchMain {
//                    LogUtils.e("计时02  ：  "+Performance.endCountTime("2"))
//                }
//            }
//            LogUtils.e("计时03  ：  "+Performance.endCountTime("3"))
//
//
//            Performance.startCountTime("11",true)
//            Performance.startCountTime("13",true)
//            Thread {
//                LogUtils.e("计时11  ：  "+Performance.endCountTime("11"))
//                var handle = object: Handler(Looper.getMainLooper(),object :Callback{
//                    override fun handleMessage(msg: Message): Boolean {
//                        LogUtils.e("计时12  ：  "+Performance.endCountTime("12"))
//                        return false
//                    }
//                }){
//                }
//                Performance.startCountTime("12",true)
//                handle.sendEmptyMessage(0)
//            }
//                .start()
//            LogUtils.e("计时13  ：  "+Performance.endCountTime("13"))
//
//            Looper.loop()
//        }.start()



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
                Memory.showMemoryInfoOnLog(this)
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