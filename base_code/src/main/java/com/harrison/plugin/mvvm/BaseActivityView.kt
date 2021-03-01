package com.harrison.plugin.mvvm

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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.util.developer.LogUtils
import com.harrison.plugin.util.io.CoroutineUtils


/**
 * 尽量保持 Android 原生结构，不用范型的方式封装ViewMode的初始化
 * 封装功能
 *      1、浸入式状态栏
 *      2、点击指定空白处自动关闭软键盘
 *
 * 性能优化
 *      1、异步加载视图
 *      2、单Activity 多 Fragment 堆栈管理
 *      3、除了生命周期其他回掉都换为事件机制
 * */
open abstract class BaseActivityView: AppCompatActivity() {

    abstract fun getViewLayout(): Any

    /**
     * 异步加载视图结束回掉
     */
    abstract fun viewCreated();

    /**
     * 使用LiveData避免异步异步加载视图在界面关闭时填充到界面
     */
    var viewCallBack = MutableLiveData<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //配置浸入式状态栏
        setTranslucentStatus()

        //加载显示视图
        var view = getViewLayout()
        //当视图已经加载完成直接显示
        if (view is View) {
            setContentView(view)
            viewCreated()
        //XML 视图使用异步加载视图
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

    // 视图堆栈中心储存
    var fragmentViewStack: MutableList<Fragment> = arrayListOf();

    /**
     * 当前视图返回到栈顶的回掉事件
     */
    open fun onBackToTaskTop(){

    }

    /**
     * 将所有栈移除再加入栈
     * @param fragment 加入堆栈的页面
     * @param isAnimation 是否使用动画
     */
    fun newNavigator(fragment: Fragment, isAnimation: Boolean = true) {
        this.newNavigator(fragment, null, isAnimation)
    }

    fun newNavigator(fragment: Fragment, bundle: Bundle?, isAnimation: Boolean = true) {
        var transaction = supportFragmentManager.beginTransaction()
        for (fItem in fragmentViewStack) {
            transaction.remove(fItem)
        }
        transaction.commit()
        this.pushNavigator(fragment, bundle, isAnimation)
    }

    /**
     * 添加到栈
     */
    fun pushNavigator(fragment: Fragment, isAnimation: Boolean = true) {
        this.pushNavigator(fragment, null, isAnimation)
    }

    fun pushNavigator(fragment: Fragment, bundle: Bundle?, isAnimation: Boolean = true) {
        if (fragmentViewStack.size > 0) {
            var currentFragment = fragmentViewStack.last()
            var transaction = supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction.hide(currentFragment)
            transaction.commit()
        }

        bundle?.let { fragment.arguments = it }
        if (fragment is BaseFragmentView) {
            fragment.outofAnimation = isAnimation
            fragment.intoAnimation = isAnimation
        }

        var transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentViewStack.add(fragment)
        transaction.add(android.R.id.content, fragment)
        transaction.commit()

        var intent = Intent()
        intent.putExtra(ACTION_WHAT, ACTION_TASK_CHANGE)
        activityResultAction.value = intent
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
        transaction.commit()

        if (fragmentViewStack.size > 0) {
            var currentFragment = fragmentViewStack.last()
            transaction = supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)  // 表示使用关闭的动画  并不表示关闭页面
            transaction.show(currentFragment)
            transaction.commit()

            if(currentFragment is BaseFragmentView)
                currentFragment.onBackToTaskTop()
        }
//        else{
//            onBackToTaskTop()
//        }
        var intent = Intent()
        intent.putExtra(ACTION_WHAT, ACTION_TASK_CHANGE)
        activityResultAction.value = intent
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
            window.statusBarColor = Color.TRANSPARENT //设置状态栏颜色为透明

            var decorView = window.decorView
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or  //全屏模式
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or // 设置为浅色模式
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // 状态栏覆盖在内容上方
        }
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

    /**
     * 是否点击屏幕其他位置自动隐藏键盘
     */
    protected open fun isAutoHideKeyBoard(): Boolean {
        return true
    }

    /**
     * 当开启自动隐藏键盘时，用于过滤点击指定控件不自动隐藏软键盘
     */
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
    //只有在栈顶的界面才能收到响应事件
    var activityResultAction = FragmentTaskEvent<Intent>()

    val ACTION_WHAT = "what" //设置到响应Intent中的类型 ，使用 intent.putExtra(ACTION_WHAT,XXXX)
    val ACTION_TASK_CHANGE = "task_change"  //堆栈发生变化
    val ACTION_ACTIVITY_RESULT = "onActivityResult"
    val ACTION_PERMISSION_RESULT = "onRequestPermissionsResult"

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        responseRequestAction(requestCode, resultCode, data,ACTION_ACTIVITY_RESULT)
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
        responseRequestAction(requestCode, 0, data,ACTION_PERMISSION_RESULT)
    }

    private fun responseRequestAction(
        requestCode: Int, resultCode: Int,
        data: Intent?,what:String
    ) {
        var result: Intent =
            if (data == null) {
                Intent()
            } else {
                data
            }
        var bundle: Bundle = if (data?.extras == null) {
            Bundle()
        } else {
            data.extras!!
        }

        bundle.putInt("requestCode", requestCode)
        bundle.putInt("resultCode", resultCode)

        result.putExtras(bundle)
        result.putExtra(ACTION_WHAT,what)

        activityResultAction.value = result
    }

}