package com.example.baseplugin.view

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.example.baseplugin.R
import com.example.baseplugin.viewmodel.MainViewModel
import com.harrison.plugin.mvvm.base.BaseActivityView
import com.harrison.plugin.util.developer.LogUtils
import com.harrison.plugin.util.hardware.Memory
import com.kok.kuailong.utils.Performance
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivityView<MainViewModel>() {

    override fun viewCreated() {
        Performance.startCountTime("1")
        LogUtils.e("result", "count time:" + Performance.endCountTime("1"))

        button_jump_to_mvvm.setOnClickListener(onClickListener)
        button_jump_to_second.setOnClickListener(onClickListener)
        button_start_request.setOnClickListener(onClickListener)
        button_main_add_window.setOnClickListener(onClickListener)

        viewModel.httpLiveEvent.observe(this) { state, value ->
            LogUtils.i("网络请求状态 $state  $value")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
//                    WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//                    params.type = WindowManager.LayoutParams.TYPE_PHONE;
//                    params.format = PixelFormat.RGBA_8888;
//                    windowManager.addView(view,params);

                } else {
                    Toast.makeText(
                        this,
                        "ACTION_MANAGE_OVERLAY_PERMISSION权限已被拒绝",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
            R.id.button_main_add_window -> {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(applicationContext)) {
                        //启动Activity让用户授权
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data = Uri.parse("package:$packageName")
                        startActivityForResult(intent, 100)
                    } else {
                        val wmManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                        val wmParams = WindowManager.LayoutParams()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                        } else {
                            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                        }
//                      wmParams.type = WindowManager.LayoutParams.TYPE_PHONE // 设置window type
                        wmParams.format = PixelFormat.RGBA_8888 // 设置图片格式，效果为背景透明
                        wmParams.flags =
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        wmParams.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL // 调整悬浮窗口至右侧中间
                        wmParams.x = 0 //以屏幕左上角为原点，设置x、y初始值
                        wmParams.y = 0
                        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT // 设置悬浮窗口长宽数据

                        var view = TextView(this)
                        var layoutParameter = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        view.layoutParams = layoutParameter
                        view.text = "悬浮窗测试"

                        wmManager.addView(view, wmParams);
                    }
                }


//                //检查是否已经授予权限
//                if (Settings.canDrawOverlays(this)) {
//                    LogUtils.i("拥有权限")
//
//                } else {
//                    LogUtils.i("没有权限")
////                    getOverlayPermission()
//                }

                var rxPermissions: RxPermissions = RxPermissions(this);
                rxPermissions
                    .request(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .subscribe { granted ->
                        if (granted) { // Always true pre-M

                        } else {
                            Toast.makeText(this, "没有权限", Toast.LENGTH_LONG).show()
//                              ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 20)
                        }
                    }
            }
        }
    }

    //请求悬浮窗权限
    @TargetApi(Build.VERSION_CODES.M)
    private fun getOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, 0)
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun getViewLayout(): Any {

        return R.layout.activity_main
    }

}