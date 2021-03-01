package com.harrison.plugin.widget

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.harrison.plugin.util.io.CoroutineUtils

/**
 * 1、封装数据刷新
 * 2、封装Item点击事件
 */
abstract class CompatRecyclerAdapter : RecyclerView.Adapter<CompatRecyclerAdapter.RowViewHolder>() {

    private var listener: OnItemClickListener? = null

    //数据
    var mData: MutableList<RowData> = arrayListOf()

    /**
     * =============================
     *  多视图和Item点击事件封装
     * =============================
     */
    override fun getItemCount(): Int {
        return mData.size
    }

    /**
     * 获取指定列数量
     */
    open fun getColumnCount(type: Int): Int {
        return 1
    }

    /**
     * 获取指定类型的视图
     *   获取Item视图
     */
    abstract fun getLayout(type: Int): Int

    /**
     * 绑定视图
     */
    abstract fun onBindView(type: Int, view: View, data: Any)

    /**
     * 自定义视图结构
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        var recyclerRootLayout = LinearLayout(parent.context)
        var rootParameter = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        recyclerRootLayout.layoutParams = rootParameter
        recyclerRootLayout.orientation = LinearLayout.HORIZONTAL
        for (holderIndex in 0 until getColumnCount(viewType)) {
            var itemView = LayoutInflater.from(parent.context).inflate(getLayout(viewType), null, false)
            var parameter: LinearLayout.LayoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            parameter.weight = 1f
            itemView.layoutParams = parameter
            recyclerRootLayout.addView(itemView)
        }
        return RowViewHolder(recyclerRootLayout, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mData[position].type
    }

    /**
     *  绑定监听数据
     */
    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        var rowData = mData[position]
        for ((itemIndex, itemView) in holder.holderViews.withIndex()) {
            if (itemIndex < rowData.data.size) {
                var data = rowData.data[itemIndex]
                itemView.tag = data
                itemView.visibility = View.VISIBLE
                onBindView(getItemViewType(position), itemView, data)
            } else {
                itemView.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * 自定义视图控制
     */
    inner class RowViewHolder(itemView: LinearLayout, type: Int) :
        RecyclerView.ViewHolder(itemView) {
        var holderViews: MutableList<View> = arrayListOf()
        private var onClickListener = View.OnClickListener {
            listener?.onItemClick(type, it.tag)
        }

        init {
            for (itemCount in 0 until itemView.childCount) {
                var item = itemView.getChildAt(itemCount)
                holderViews.add(item)
                item.setOnClickListener(onClickListener)
            }
        }
    }

    //自定义数据行，一行数据
    class RowData constructor(
        var type: Int,
        var data: MutableList<Any>
    )

    fun setOnItemClickListener(listener: OnItemClickListener) {
        notifyDataSetChanged()
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(type: Int, data: Any)
    }

    /**
     * =============================
     *  数据刷新
     * =============================
     */

    fun clearDataSetChanged() {
        mData.clear()
        notifyDataSetChanged()
    }

    /**
     * 刷新所有数据
     *      datas 数据
     *      type  数据类型
     *      row   每行数据有几列
     */
    fun notifyDataSetChanged(datas: MutableList<Any>, type: Int = 0) {
        var columnCount = getColumnCount(type)
        var rowCount = datas.size / columnCount + if (datas.size % columnCount == 0) 0 else 1
        var temp:MutableList<RowData> = arrayListOf()
        for (index in 0 until rowCount) {
            var rowData: MutableList<Any> = arrayListOf()
            for (rowIndex in 0 until columnCount) {
                var position = index * columnCount + rowIndex
                if (position < datas.size) {
                    rowData.add(datas[position])
                }
            }
            temp.add(RowData(type, rowData))
        }
        mData.addAll(temp)
        notifyDataSetChanged()
    }

    /**
     * 刷新指定数据
     */
    fun notifyItemChanged(item: Any) {
        var index = -1
        for ((rowIndex, row) in mData.withIndex()) {
            for (column in row.data) {
                if (equalItem(item, column)) {  //找到对应的 Item
                    index = rowIndex
                    break
                }
            }
            if (index >= 0)
                break
        }
        if (index < 0) return
        notifyItemChanged(index)     // 刷新指定位置的数据
    }

    /**
     * 用于对比Item
     *      重写当前函数，自定义匹配规则
     */
    fun equalItem(dataA: Any, dataB: Any): Boolean {
        return dataA == dataB
    }

    /**
     * =============================
     *   关于悬浮相关功能
     * =============================
     */

    /**
     * 自定义控件重写当前函数告诉控当前Item是否悬浮
     *      获取是否浮动
     */
    open fun isFloat(type: Int): Boolean {
        return false
    }

    /**
     * 自定义控件重写当前函数告诉控当前Item是否悬浮
     *      浮动等级
     */
    open fun floatLevel(type: Int): Int {
        return 0
    }


}

