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
import com.harrison.plugin.util.hardware.Memory
import com.harrison.plugin.util.io.CoroutineUtils
import com.kok.kuailong.utils.Performance
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.operators.observable.ObservableObserveOn
import io.reactivex.rxjava3.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import org.apache.lucene.util.RamUsageEstimator


class MainActivity : ABaseActivityView<MainViewModel>() {


    override fun initView() {

        Performance.startCountTime("1")
        setContentView(R.layout.activity_main)
        LogUtils.e("result", "count time:" + Performance.endCountTime("1"))

        button_jump_to_mvvm.setOnClickListener(onClickListener)
        button_jump_to_second.setOnClickListener(onClickListener)
        button_start_request.setOnClickListener(onClickListener)



//        //计算指定对象及其引用树上的所有对象的综合大小，单位字节
////        RamUsageEstimator.sizeOf(this)
//
//        //计算指定对象本身在堆空间的大小，单位字节
////
////        LogUtils.i("对象所占用内存大小  ${ RamUsageEstimator.shallowSizeOf(this)}")
//
//        //计算指定对象及其引用树上的所有对象的综合大小，返回可读的结果，如：2KB
//        RamUsageEstimator.shallowSizeOfInstance(MainActivity::class.java)
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
                viewModel.exeTest()
//                var glide = Glide.with(this)
//                Performance.startCountTime("123")
////              ivCoverImg.setBackgroundResource(R.drawable.customactivityoncrash_error_image)
//                glide.load(R.drawable.customactivityoncrash_error_image).into(ivCoverImg)
////                ivCoverImg.load(R.drawable.customactivityoncrash_error_image){}
//                Log.e("time_test","加载图片耗时："+Performance.endCountTime("123"))
            }
        }
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

}