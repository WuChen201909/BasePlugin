package com.harrison.plugin.mvvm.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.harrison.plugin.mvvm.core.MVVMApplication
import com.harrison.plugin.util.developer.LogUtils
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
            if (isStackModel()) requireActivity() else this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(MVVMApplication.mvvmApplication)
        ).get(getViewModelClass())

        var view = getViewLayout()
        if (view is View) {
            addToContentView(view)
            viewCreated()
        } else if (view is Int) {
            viewCallBack.observe(this, {
                addToContentView(it)
                viewCreated()
            })
            CoroutineUtils.launchLayout(requireContext(), view, {
                viewCallBack.value = it
            })
        } else {
            throw Exception("please set view layout on this page")
        }
    }

    private fun addToContentView(view: View) {
        var layoutParameter = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        view.layoutParams = layoutParameter
        fragmentContent.addView(view)
    }


    /**
     * ====================================================
     * Fragment 堆栈管理
     * ====================================================
     */

    /**
     * 堆栈管理模式
     */
    open fun isStackModel(): Boolean {
        return false
    }

    /**
     * 显示其他内容到栈中
     */
    fun pushNavigator(fragment: Fragment){
        if(isStackModel() && requireActivity() is BaseActivityView<*>){
            (requireActivity() as BaseActivityView<*>).pushNavigator(fragment)
        }
    }

    /**
     * 将自己退出显示栈
     */
    fun popNavigator(){
        if(isStackModel() && requireActivity() is BaseActivityView<*>){
            (requireActivity() as BaseActivityView<*>).popNavigator()
        }
    }

    /**
     * ====================================================
     * Fragment 进出效果
     * ====================================================
     */
    
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        var animation: TranslateAnimation? = null
        LogUtils.i("进入状态"+enter)
        if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
            if(enter){
                animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                )
            }else{
                animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                )
            }
        } else if (FragmentTransaction.TRANSIT_FRAGMENT_CLOSE === transit) {
            if(enter){
                animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                )
            }else{
                animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
                )

            }

        }
        if (animation == null) {
            animation = TranslateAnimation(0f, 0f, 0f, 0f)
        }
        animation.setDuration(1000)
        return animation
    }


}