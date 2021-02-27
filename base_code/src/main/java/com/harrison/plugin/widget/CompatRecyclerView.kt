package com.harrison.plugin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 1、配合 CompatRecyclerAdapter 实现悬浮置顶
 */
class CompatRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {


    //浮动窗口显示区域
    var floatLayout:LinearLayout? = null
    var levelStack:MutableList<Int> = arrayListOf()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addOnScrollListener(onScrollListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeOnScrollListener(onScrollListener)
    }

    //实现悬浮置顶功能
    private val onScrollListener = object: OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        }
    }


}