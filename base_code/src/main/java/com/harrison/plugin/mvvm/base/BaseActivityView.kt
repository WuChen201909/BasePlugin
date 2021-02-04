package com.harrison.plugin.mvvm.base

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.mvvm.core.MVVMApplication
import com.harrison.plugin.util.developer.LogUtils
import com.harrison.plugin.util.io.CoroutineUtils
import java.lang.reflect.Field
import java.lang.reflect.Method


/**
 * 尽量保持 Android 原生结构
 *
 * */
open abstract class BaseActivityView<T : BaseViewModel> : AppCompatActivity() {

    lateinit var viewModel: T

    abstract fun getViewModelClass(): Class<T>
    abstract fun getViewLayout(): Any
    abstract fun viewCreated(); // 视图创建成功

    var viewCallBack = MutableLiveData<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 配置ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(MVVMApplication.mvvmApplication)
        )
            .get(getViewModelClass())

        //配置浸入式状态栏
        setTranslucentStatus()

        //加载显示视图
        var view = getViewLayout()
        if (view is View) {
            setContentView(view)
            viewCreated()
        } else if (view is Int) {
            viewCallBack.observe(this, {
                setContentView(it)
                viewCreated()
            })
            CoroutineUtils.launchLayout(this, view, {
                viewCallBack.value = it
            })
        } else {
            throw Exception("please set view layout on this page")
        }
    }


    /**
     * ====================================================
     * Fragment 堆栈管理
     * ====================================================
     */

    var fragmentViewStack: MutableList<Fragment> = arrayListOf();

    /**
     * 回退栈
     */
    fun popNavigator() {
        if (fragmentViewStack.size <= 0) {
            return
        }

        var currentFragment = fragmentViewStack.last()
        fragmentViewStack.remove(currentFragment)

        var transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)  //表示使用打开的动画 并不表示打开页面
        transaction.remove(currentFragment)
        currentFragment.onPause()  //调用对应生命周期
        transaction.commit()

        if (fragmentViewStack.size > 0) {
            var currentFragment = fragmentViewStack.last()
            transaction = supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)  // 表示使用关闭的动画  并不表示关闭页面
            transaction.show(currentFragment)
            currentFragment.onResume()  //调用对应生命周期
            transaction.commit()
        }
    }

    /**
     * 添加到栈
     */
    fun pushNavigator(fragment: Fragment) {
        this.pushNavigator(fragment, null)
    }

    /**
     * 添加到栈  带参数
     */
    fun pushNavigator(fragment: Fragment, bundle: Bundle?) {

        if (fragmentViewStack.size > 0) {
            var currentFragment = fragmentViewStack.last()
            var transaction = supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction.hide(currentFragment)
            currentFragment.onPause()  //调用对应生命周期
            transaction.commit()
        }

        bundle?.let { fragment.arguments = it }

        var transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentViewStack.add(fragment)
        transaction.add(android.R.id.content, fragment)
        fragment.onResume()  //调用对应生命周期
        transaction.commit()


    }

    /**
     * 拦截返回按钮实现栈回退
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragmentViewStack.size > 0) {
                popNavigator()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    /**
     * ====================================================
     * 状态栏操作
     * ====================================================
     */

    /**
     * 设置状态栏透明 沉浸式
     */
    open fun setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            LogUtils.i("设置SDK大于30的状态栏")

        } else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {
            LogUtils.i("设置SDK大于19的状态栏")
            //绘制状态栏背景色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT  //设置状态栏颜色为透明

            var decorView = window.decorView
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or  //全屏模式
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or // 设置为浅色模式
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // 状态栏覆盖在内容上方
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
//            val decorView = window.decorView
//            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
//            decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            decorView.systemUiVisibility =
//                decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.statusBarColor = Color.TRANSPARENT
//            //导航栏颜色也可以正常设置
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            val attributes = window.attributes
//            val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//            attributes.flags = attributes.flags or flagTranslucentStatus
//            //int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
//            //attributes.flags |= flagTranslucentNavigation;
//
//            window.decorView.systemUiVisibility =
//                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.statusBarColor = Color.TRANSPARENT
//
//            window.attributes = attributes
//        }


    }


    /**
     * ===========================================================
     * 软键盘配置操作
     * ===========================================================
     */

    /**
     * 点击软键盘之外的空白处，隐藏软件盘
     *
     * @param ev
     * @return
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (isAutoHideKeyBoard()) {
            if (ev.action == MotionEvent.ACTION_DOWN) {
                val v: View? = getCurrentFocus()
                if (isShouldHide(v, ev)) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(v?.windowToken, 0)
                }
                return super.dispatchTouchEvent(ev)
            }
            // 必不可少，否则所有的组件都不会有TouchEvent了
            if (getWindow().superDispatchTouchEvent(ev)) {
                true
            } else onTouchEvent(ev)
        } else {
            super.dispatchTouchEvent(ev)
        }
    }

    protected open fun isAutoHideKeyBoard(): Boolean {
        return true
    }

    private fun isShouldHide(v: View?, event: MotionEvent): Boolean {
        //这里是用常用的EditText作判断参照的,可根据情况替换成其它View
        if (v != null &&
            (v is EditText || v is Button)
        ) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            val b = event.x > left && event.x < right && event.y > top && event.y < bottom
            return !b
        }
        return false
    }

    /**
     * ===========================================================
     * 事件管理 ， 处理非UI生命周期事件
     * ===========================================================
     */

    var activityResultAction = MutableLiveData<Intent>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        responseRequestAction(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var data = Intent()
        var bundle = Bundle()
        bundle.putStringArray("permissions", permissions)
        bundle.putIntArray("grantResults", grantResults)
        data.putExtras(bundle)
        responseRequestAction(requestCode, 0, data)
    }


    private fun responseRequestAction(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        var result: Intent =
            if (data == null) {
                Intent()
            } else {
                data
            }

        var bundle: Bundle = if (intent.extras == null) {
            Bundle()
        } else {
            intent.extras!!
        }

        bundle.putInt("requestCode", requestCode)
        bundle.putInt("resultCode", resultCode)

        result.putExtras(bundle)

        activityResultAction.value = result
    }

}