package com.harrison.plugin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.marginTop
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

        }

        var childView :View? = null

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            if(floatLayout == null
                && adapter is CompatRecyclerAdapter
                && layoutManager is LinearLayoutManager
            ){
                throw Exception("配置条件不符合悬浮控件")
                return
            }
            var mAdapter :CompatRecyclerAdapter = adapter as CompatRecyclerAdapter
            var position = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
//            (layoutManager as LinearLayoutManager).getDecoratedMeasuredHeight()
            var type = mAdapter.getItemViewType(position)
            if(mAdapter.isFloat(type)  && childView == null){
//              mAdapter.mData.get(position)
                var viewHolder =  mAdapter.createViewHolder(floatLayout!!,type)
                mAdapter.onBindViewHolder(viewHolder,position)

                childView = viewHolder.itemView
                floatLayout!!.addView(childView)

            }
        }
    }


}