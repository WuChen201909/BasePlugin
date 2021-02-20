package com.harrison.plugin.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.harrison.plugin.util.io.CoroutineUtils
/**
 * 1、封装数据刷新
 * 2、封装Item点击事件
 */
abstract class CompatRecyclerAdapter<T : RecyclerView.ViewHolder?, D> : RecyclerView.Adapter<T>() {

    private var listener: OnItemClickListener<D>? = null

    //数据
    var mData: MutableList<D> = arrayListOf()

    /**
     * =============================
     *  封装点击事件
     * =============================
     */

    override fun getItemCount(): Int {

        return mData.size
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        holder?.itemView?.tag = position
        holder?.itemView?.setOnClickListener(onClickListener)
    }

    fun setOnItemClickListener(listener: OnItemClickListener<D>) {
        notifyDataSetChanged()
        this.listener = listener
    }

    private var onClickListener = View.OnClickListener {
        var position: Int = it.tag as Int
        listener?.onItemClick(mData[position])
    }

    interface OnItemClickListener<D> {
        fun onItemClick(d: D)
    }


    /**
     * =============================
     *  数据刷新
     * =============================
     */

    /**
     * 刷新所有数据
     */
    fun notifyDataSetChanged(data: MutableList<D> ){
        mData.clear()
        mData.addAll(data)
    }

    /**
     * 刷新指定数据
     */
    fun notifyItemChanged(item: D){
        CoroutineUtils.launchIO {
            for ((index, adapterItem) in mData.withIndex()){
                if(itemEqual(item, adapterItem)){
                    mData[index] =  item
                    CoroutineUtils.launchMain {
                        notifyItemChanged(index)  // 刷新指定位置的数据
                    }
                }
            }
        }
    }

    //用于数据更新时判断更新指定Item
    fun itemEqual(item: D, adapterItem: D):Boolean{
        return item == adapterItem
    }

}

