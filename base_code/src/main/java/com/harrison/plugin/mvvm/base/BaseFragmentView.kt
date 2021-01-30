package com.harrison.plugin.mvvm.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.mvvm.core.MVVMApplication
import com.harrison.plugin.util.io.CoroutineUtils


open abstract class BaseFragmentView<T : BaseViewModel> : Fragment() {

    lateinit var viewModel: T

    lateinit var fragmentContent: FrameLayout

    var viewCallBack = MutableLiveData<View>()

    abstract fun getViewModelClass(): Class<T>
    abstract fun getViewLayout(): Any
    abstract fun viewCreated()  // 视图创建成功

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContent = FrameLayout(requireContext())
        return fragmentContent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(MVVMApplication.mvvmApplication)
        ).get(getViewModelClass())
        var view = getViewLayout()
        if (view is View) {
            addToContentView(view)
            viewCreated()
        } else if (view is Int) {
            viewCallBack.observe(this,{
                addToContentView(it)
                viewCreated()
            })
            CoroutineUtils.launchLayout(requireContext(), view, {
                viewCallBack.value = it
            })
            layoutInflater.inflate(view, null)
        } else {
            throw Exception("please set view layout on this page")
        }
    }


    fun addToContentView(view: View) {
        var layoutParameter = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        view.layoutParams = layoutParameter
        fragmentContent.addView(view)
    }


}