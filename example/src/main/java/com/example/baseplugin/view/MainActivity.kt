package com.example.baseplugin.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import coil.load
import com.bumptech.glide.Glide
import com.example.baseplugin.R
import com.example.baseplugin.viewmodel.MainViewModel
import com.harrison.plugin.mvvm.base.impl.ABaseActivityView
import com.harrison.plugin.util.developer.LogUtils
import com.harrison.plugin.util.developer.Performance
import com.harrison.plugin.util.hardware.Memory
import com.harrison.plugin.util.io.CoroutineUtils
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.operators.observable.ObservableObserveOn
import io.reactivex.rxjava3.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : ABaseActivityView<MainViewModel>() {


    override fun initView() {

        Performance.startCountTime("1")
        setContentView(R.layout.activity_main)
        LogUtils.e("result", "count time:" + Performance.endCountTime("1"))

        button_jump_to_mvvm.setOnClickListener(onClickListener)
        button_jump_to_second.setOnClickListener(onClickListener)
        button_start_request.setOnClickListener(onClickListener)


//
//        Thread{
//            while (true){
//                Performance.startCountTime("21")
//                Performance.startCountTime("23")
//                Observable.create<String>(object : ObservableOnSubscribe<String> {
//                    override fun subscribe(emitter: ObservableEmitter<String>?) {
//                        LogUtils.e("RxJava main-child 线程切换  ：  "+Performance.endCountTime("21"))
//                        Performance.startCountTime("22")
//                        emitter!!.onNext("")
//                    }
//                }).subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.io())
//                    .subscribe(object : Observer<String> {
//                        override fun onSubscribe(d: Disposable?) {
//                        }
//
//                        override fun onNext(t: String?) {
//                            LogUtils.e("RxJava child-main 线程切换   ：  "+Performance.endCountTime("22"))
//                        }
//
//                        override fun onError(e: Throwable?) {
//                        }
//
//                        override fun onComplete() {
//                        }
//                    })
//                LogUtils.e("RxJava 创建线程  ：  "+Performance.endCountTime("23"))
//
//
//                Performance.startCountTime("01")
//                Performance.startCountTime("03")
//                CoroutineUtils.launchIO {
//                    LogUtils.e("协程 main-child 线程切换  ：  "+Performance.endCountTime("01"))
//
//                    Performance.startCountTime("02")
//                    CoroutineUtils.launchMain {
//                        LogUtils.e("协程 child-main 线程切换   ：  "+Performance.endCountTime("02"))
//                    }
//                }
//                LogUtils.e("协程 创建线程  ：  "+Performance.endCountTime("03"))
//                Thread.sleep(2000)
//            }
//        }.start()
//        Thread{
//            Looper.prepare()
//
//
//            Performance.startCountTime("11")
//            Performance.startCountTime("13")
//            Thread {
//                LogUtils.e("Thread main-child 线程切换    ：  "+Performance.endCountTime("11"))
//                var handle = object: Handler(Looper.getMainLooper(),object :Callback{
//                    override fun handleMessage(msg: Message): Boolean {
//                        LogUtils.e("Thread child-main 线程切换  ：  "+Performance.endCountTime("12"))
//                        return false
//                    }
//                }){
//                }
//                Performance.startCountTime("12")
//                handle.sendEmptyMessage(0)
//            }
//                .start()
//            LogUtils.e("Thread 创建线程  ：  "+Performance.endCountTime("13"))
//
//            Looper.loop()
//        }.start()


    }

    override fun initViewObservable() {
        viewModel.httpLiveEvent.observe(this) { state, value ->
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
            R.id.button_jump_to_second -> {
                var intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            }
            R.id.button_start_request -> {
//                viewModel.exeTest()
                var glide = Glide.with(this)
                Performance.startCountTime("123")
//              ivCoverImg.setBackgroundResource(R.drawable.customactivityoncrash_error_image)
                glide.load(R.drawable.customactivityoncrash_error_image).into(ivCoverImg)
//                ivCoverImg.load(R.drawable.customactivityoncrash_error_image){}
                Log.e("time_test","加载图片耗时："+Performance.endCountTime("123"))
            }
        }
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

}