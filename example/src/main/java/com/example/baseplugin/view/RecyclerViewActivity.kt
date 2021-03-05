package com.example.baseplugin.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baseplugin.R
import com.harrison.plugin.mvvm.BaseActivityView
import com.harrison.plugin.util.io.CoroutineUtils
import com.harrison.plugin.widget.CompatRecyclerView
import com.kok.kuailong.utils.Performance
import kotlinx.android.synthetic.main.activity_recycler.*

class RecyclerViewActivity : BaseActivityView() {

    var data = arrayListOf<CustomData>()
//    var tData: MutableList<CompatRecyclerView.RowData> = arrayListOf<CompatRecyclerView.RowData>()

    class CustomData(var type: Int, var level: Int, var data: String)

    override fun getViewLayout(): Any {
        return R.layout.activity_recycler
    }

    var time = 0L

    private var handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

        }
    }

    override fun viewCreated() {
        for (dItem in 0..100) {
            for (item in 0..10) {
                data.add((CustomData(0, 0, "测试数据:" + item)))
            }
            data.add((CustomData(1, 1, "${dItem} 一级浮动窗口")))
            for (item in 0..5) {
                data.add((CustomData(0, 0, "测试数据:" + item)))
            }
            data.add((CustomData(2, 2, "${dItem} 二级浮动窗口")))

            for (item in 0..9) {
                data.add((CustomData(0, 0, "测试数据:" + item)))
            }
            data.add((CustomData(3, 3, "${dItem} 三级浮动窗口")))
        }

//      tData = CompatRecyclerView.makeColumnData(data as MutableList<Any>, 3)

        Performance.startCountTime("A")
        recyclerview_new.layoutManager = LinearLayoutManager(this)
        recyclerview_new.adapter = CustomAdapter()
        var a = Performance.endCountTime("A")
        Log.i("result", "界面加载时间：" + a)

        recyclerview_new.setItemAnimator(FlyAnimator())
        recyclerview_new.floatLayout = ll_float

//        var intent = Intent(this,SecondActivity::class.java)
//        startActivity(intent)
//        finish()

    }

    inner class CustomAdapter : CompatRecyclerView.CompatAdapter<CustomViewHolder>() {
        override fun getItemData(position: Int): Any {
            return data[position]
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            return CustomViewHolder(parent.context, viewType);
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemViewType(position: Int): Int {
            return data[position].type
        }

        override fun isFloat(type: Int): Boolean {
            return type == 1 || type == 2|| type == 3
        }

        override fun floatLevel(type: Int): Int {
            if (type == 1) {
                return 1
            } else if(type ==2){
                return 2
            }else{
                return 3
            }
        }

    }

    class CustomViewHolder(context: Context, type: Int) : CompatRecyclerView.CompatViewHolder(
        context,
        type
    ) {

        lateinit var item1: TextView
        lateinit var item2: TextView
        lateinit var item3: TextView

        private var onClick = object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (v?.tag != null) {
                    Log.i("result", "点击：" + v!!.tag)
                }
            }
        }

        override fun getItemHeight(type: Int): Int {
            return 19
        }

        override fun getItemLayout(type: Int): Any {
            return R.layout.item_test
        }

//        var handler= object :Handler(Looper.myLooper()!!){
//            override fun handleMessage(msg: Message) {
//                super.handleMessage(msg)
//            }
//        }

        override fun viewCreated() {
            super.viewCreated()
            item1 = itemView.findViewById<TextView>(R.id.tv_test)
            item1.setOnClickListener(onClick)
//            item2 = itemView.findViewById<TextView>(R.id.tv_test2)
//            item2.setOnClickListener(onClick)
//            item3 = itemView.findViewById<TextView>(R.id.tv_test3)
//            item3.setOnClickListener(onClick)
        }

        override fun onBindViewData(type: Int, itemView: View, data: Any) {

            var rowData: CustomData = data as CustomData
            item1.text = rowData.data

//            if (rowData.data.size > 0) {
//                item1.text = rowData.data[0] as String
//                item1.tag = rowData.data[0]
//                item1.visibility = View.VISIBLE
//            } else {
//                item1.visibility = View.INVISIBLE
//            }
//
//            if (rowData.data.size > 1) {
//                item2.text = rowData.data[1] as String
//                item2.tag = rowData.data[1]
//                item2.visibility = View.VISIBLE
//            } else {
//                item2.visibility = View.INVISIBLE
//            }
//
//            if (rowData.data.size > 2) {
//                item3.text = rowData.data[2] as String
//                item3.tag = rowData.data[2]
//                item3.visibility = View.VISIBLE
//            } else {
//                item3.visibility = View.INVISIBLE
//            }
        }

    }


    inner class SyncAdapter : RecyclerView.Adapter<SyncAdapter.SyncViewHolder>() {

        inner class SyncViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textView = itemView.findViewById<TextView>(R.id.tv_test)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyncViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
            return SyncViewHolder(view)
        }

        override fun onBindViewHolder(holder: SyncViewHolder, position: Int) {
            holder.textView.text = data[position].data
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }

    /**
     * 异步加载xml视图提高加载速度
     */
    inner class AsyncAdapter : RecyclerView.Adapter<CompatViewHolder>() {

        //在界面中显示的数据与对应的视图
        private var onWindowDataViewMap: HashMap<Any, CompatViewHolder> = hashMapOf()

        /**
         * 只刷新现有的数据，新增和删除数据刷新整个列表
         */
        fun notifyItemData(data: Any) {
            for (item in onWindowDataViewMap) {
                if (equalItem(item, data)) {
                    onWindowDataViewMap[data]!!.onBaind(data) // 刷新指定数据
                }
            }
        }

        /**
         * 重写对比规则判断数据是刷新的数据是否是同一个
         */
        fun equalItem(dataA: Any, dataB: Any): Boolean {
            return dataA == dataB
        }

        /**
         * 重写指定Item高度
         */
        fun getItemHeight(type: Int): Int {
            return 15
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompatViewHolder {
            var view = LinearLayout(parent.context)
            var layoutParameter =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (parent.context.resources.displayMetrics.density * getItemHeight(
                        viewType
                    )).toInt()
                )
            view.layoutParams = layoutParameter
            return CompatViewHolder(view, viewType) {
                onWindowDataViewMap.remove(it)
            }
        }

        override fun onBindViewHolder(holder: CompatViewHolder, position: Int) {
            holder.onBaind(data[position])
            onWindowDataViewMap[data] = holder
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }

    class CompatViewHolder(
        itemView: LinearLayout,
        var type: Int,
        var covertCallBack: (Any) -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {
        var data: Any? = null
        var initView = false

        init {
            var contentView = getItemLayout(type)
            if (contentView is View) {
                itemView.addView(contentView)
                initView = true
            } else if (contentView is Int) {
                CoroutineUtils.launchLayout(itemView.context, contentView, {
                    it.layoutParams = itemView.layoutParams
                    itemView.addView(it)
                    initView = true
                    refreshView()
                })
            } else {
                throw Exception("Please Set Content View")
            }
        }

        /**
         * 确定加载布局
         */
        fun getItemLayout(type: Int): Any {
            return R.layout.item_test
        }

        /**
         * 绑定视图到界面
         */
        fun onBindViewData(type: Int, itemView: View, data: Any) {
            itemView.findViewById<TextView>(R.id.tv_test).text = data as String
        }

        fun onBaind(data: Any) {
            if (this.data != null) {
                covertCallBack(this.data!!)
            }
            this.data = data
            refreshView()
        }

        /**
         * 刷新视图
         */
        fun refreshView() {
            if (!initView || data == null) {
                return
            }
            onBindViewData(type, itemView, data!!)
        }


    }


}