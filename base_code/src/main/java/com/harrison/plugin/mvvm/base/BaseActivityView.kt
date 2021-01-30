package com.harrison.plugin.mvvm.base

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.mvvm.core.MVVMApplication
import com.harrison.plugin.mvvm.event.SingleLiveEvent
import com.harrison.plugin.util.io.CoroutineUtils


/**
 * 尽量保持 Android 原生结构
 *
 * */
open abstract class BaseActivityView<T : BaseViewModel>   : AppCompatActivity() {

    protected lateinit var viewModel: T

    abstract fun getViewModelClass(): Class<T>
    abstract fun getViewLayout(): Any
    abstract fun viewCreated(); // 视图创建成功

    var viewCallBack = MutableLiveData<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(MVVMApplication.mvvmApplication)
        )
        .get(getViewModelClass())

        var view = getViewLayout()
        if (view is View) {
            setContentView(view)
            viewCreated()
        } else if (view is Int) {
            viewCallBack.observe(this,{
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

    private  fun isShouldHide(v: View?, event: MotionEvent): Boolean {
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




}