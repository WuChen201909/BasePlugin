package com.harrison.plugin.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harrison.plugin.util.io.CoroutineUtils
import java.lang.Exception

/**
 *  1、Item的异步加载
 *  2、多列显示的数据管理（使用复杂布局时统一使用LinearLayoutManager）
 *  3、吸顶效果的封装
 *
 */
class CompatRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    /**
     * ============================================================
     * 吸顶效果
     * ============================================================
     */
    var floatLayout: LinearLayout? = null

    /**
     * 吸顶部分的高度
     */
    var covertHeight: Int = 0

    //悬浮窗列表
    var floatDescribeList: MutableList<FloatDescribeItem> = arrayListOf()

    inner class FloatDescribeItem(var position: Int, var level: Int, var view: View? = null)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addOnScrollListener(onScrollListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeOnScrollListener(onScrollListener)
    }

    //实现悬浮置顶功能
    private val onScrollListener = object : OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

        }


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (floatLayout == null
                && adapter is CompatAdapter
                && layoutManager is LinearLayoutManager
            ) {
                return
            }

            var startPosition = findHightLevelPosition()
            var newFloatDescribeList: MutableList<FloatDescribeItem> =
                getNewFloatDescribeList(startPosition)
            clearOldDefferentDescribe(floatDescribeList, newFloatDescribeList)
            addNewFloatLayout(newFloatDescribeList)
        }

        /**
         * 找到当前悬浮窗顶级所在的position
         *  @return 返回找到的位置
         */
        fun findHightLevelPosition(): Int {
            var position = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            var hightLevel = 1000
            var mAdapter = adapter as CompatAdapter
            for (index in position downTo 0) {
                var type = mAdapter.getItemViewType(index)
                if (mAdapter.isFloat(type)) {  // 浮动控件
                    var currentLevel = mAdapter.floatLevel(type)
                    if (hightLevel < currentLevel) {  // 找到更高等级浮动控件
                        hightLevel = currentLevel
                    } else if (hightLevel < currentLevel // 找到低于当前等级的浮动控件，表示找到当前分组最高等级
                        || currentLevel == 0
                    ) {
                        return index
                    }
                }
            }
            return 0
        }


        /**
         * 获取当前最新的浮动窗口列表描述
         */
        fun getNewFloatDescribeList(startPosition: Int): MutableList<FloatDescribeItem> {

            var mAdapter = adapter as CompatAdapter
            var newFloatDescribeList: MutableList<FloatDescribeItem> = arrayListOf() //新列表
            var tempCovertHeight = 0

            //先将 firstShowPosition 以外的悬浮窗添加到列表
            var firstShowPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            for (currentPosition in startPosition..firstShowPosition) {
                var type = mAdapter.getItemViewType(currentPosition)
                if (mAdapter.isFloat(type)) {
                    tempCovertHeight = addFloatDescribe(
                        newFloatDescribeList,
                        FloatDescribeItem(currentPosition, mAdapter.floatLevel(type)))
                }
            }

            //再将 firstShowPosition 以后的的悬浮窗添加到列表
            var lastShowPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            do {
                var showPosition = getCoveredVisibilitedPosition(tempCovertHeight)
                var type = mAdapter.getItemViewType(showPosition)
                if(mAdapter.isFloat(type)){
                    tempCovertHeight = addFloatDescribe(
                        newFloatDescribeList,
                        FloatDescribeItem(showPosition, mAdapter.floatLevel(type)),)
                }
            } while (mAdapter.isFloat(type)&&lastShowPosition>showPosition)  // 只要是浮动窗口就要继续添加,且只检测窗口显示中的内容

            Log.i("result", "=======  当前浮窗显示内容顺序  ======= ")
            for (item in newFloatDescribeList) {
                Log.i("result", "==== " + item.position + "   " + item.level)
            }
            Log.i("result", "========================== ")
            return newFloatDescribeList
        }

        /*
        for (currentPosition in startPosition..firstPosition) {
                var type = mAdapter.getItemViewType(currentPosition)
                if (mAdapter.isFloat(type)) {                   // 是悬浮控件
                    var currentLevel = mAdapter.floatLevel(type)
                    Log.i("result", "悬浮等级：" + currentLevel)
                    while (newFloatDescribeList.size > 0) {         //回退到当前等级应该显示的数据
                        var lastFloatDescribe = newFloatDescribeList.last()
                        if (lastFloatDescribe == null || currentLevel > lastFloatDescribe.level || lastFloatDescribe.level == 0) {
                            break
                        }
                        newFloatDescribeList.removeLast()
                    }
                    newFloatDescribeList.add(FloatDescribeItem(currentPosition, currentLevel))
                }
            }
         */

        /**
         * 添加一个浮动描述
         *  如果添加成功则返回添加浮动窗口的高度
         *  @param showView 如果添加的悬浮窗已经显示到窗口中则将窗口传递到当前函数用于判断
         */
        fun addFloatDescribe(
            flostDesList: MutableList<FloatDescribeItem>,
            describeItem: FloatDescribeItem,showView:View? = null
        ): Int {

            if(flostDesList.size != 0 && showView == null){  //如果是添加显示区以外的等级直接删除低于等于当前等级的悬浮窗然后追加
                var lastDes = flostDesList.last()
                while(describeItem.level <= lastDes.level){
                    flostDesList.removeLast()
                    if(flostDesList.size == 0 )break
                    lastDes = flostDesList.last()
                }
            }

            var mAdapter = adapter as CompatAdapter
            var viewHolder = mAdapter.createViewHolder(floatLayout!!,mAdapter.getItemViewType(describeItem.position))
            mAdapter.onBindViewHolder(viewHolder,describeItem.position)
            describeItem.view = viewHolder.itemView
            flostDesList.add(describeItem)

            var covertHeight = 0
            for (item in flostDesList){
                covertHeight+= item.view!!.layoutParams!!.height
            }

            if(showView != null){
                covertHeight = if(showView.top<covertHeight) showView.top else covertHeight
            }

            return covertHeight
        }

        /**
         * 获取悬浮窗覆盖后的可见位置
         */
        fun getCoveredVisibilitedPosition(covertHeight: Int): Int {
            var plushCount = 0
            var visibilityView: View? = null
            while (plushCount < childCount) {
                visibilityView = getChildAt(plushCount) // 表示可以被看到的视图
                if (visibilityView.top + visibilityView.height > covertHeight) { // 表示视图没有被悬浮窗盖住
                    break
                }
                plushCount++
            }
            var showPosition = getChildAdapterPosition(visibilityView!!)
            return showPosition
        }

        /**
         * 将原列表和当前列表不相同的部分删除
         */
        fun clearOldDefferentDescribe(
            oldList: MutableList<FloatDescribeItem>,
            newList: MutableList<FloatDescribeItem>
        ) {

            //老列表数量多余新列表表时移除老列表多出部分
            while (oldList.size > newList.size) {
                var lastDes = oldList.last()
                floatLayout?.removeView(lastDes.view)
                oldList.removeLast()
            }

            // 从老列表与新列表不同处开始删除
            var startIndex = 0 // 浮动窗口不同的开始位置
            for (index in 0 until oldList.size) {
                var lastDes = oldList[index]
                if (lastDes.position != newList[index].position) {
                    startIndex = index + 1
                }
            }

            //一直删除到不同的位置
            while (oldList.size != 0 && startIndex >= oldList.size) {
                var lastDes = oldList.last()
                floatLayout?.removeView(lastDes.view)
                oldList.removeLast()
            }

//            Log.i("result", "=======  删除不同后的老列表  ======= ")
//            for (item in oldList){
//                Log.i("result", "==== "+item.position +"   "+item.level)
//            }
//            Log.i("result", "========================== ")
        }

        /**
         *  追加新浮动布局
         */
        fun addNewFloatLayout(newList: MutableList<FloatDescribeItem>) {
            var mAdapter = adapter as CompatAdapter
            while (floatDescribeList.size != newList.size) {
                var currentDescribe = newList[floatDescribeList.size]
                floatLayout!!.addView(currentDescribe.view)
                floatDescribeList.add(currentDescribe)
            }
        }
    }

    /**
     * ============================================================
     * 适配器
     * ============================================================
     */

    /**
     * 异步加载xml视图提高加载速度
     */
    abstract class CompatAdapter<T : CompatViewHolder> : RecyclerView.Adapter<T>() {

        /**
         * ============================================================
         * 对异步加载的封装
         * ============================================================
         */

        //在界面中显示的数据与对应的视图
        private var onWindowDataViewMap: HashMap<Any, CompatViewHolder> = hashMapOf()

        /**
         *  获取指定位置数据
         */
        abstract fun getItemData(position: Int): Any

        override fun onBindViewHolder(holder: T, position: Int) {
            var data = getItemData(position)
            onWindowDataViewMap.remove(holder.cacheData)
            holder.onBaind(data)
            onWindowDataViewMap[data] = holder
        }

        /**
         * =============================
         *   配合实现吸顶相关功能
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

    /**
     * ============================================================
     * ViewHolder 对异步加载的封装
     * ============================================================
     */
    abstract class CompatViewHolder(
        context: Context,
        var type: Int
    ) :
        RecyclerView.ViewHolder(creatItemView(context)) {
        var cacheData: Any? = null
        var initView = false

        companion object {
            fun creatItemView(context: Context): View {
                var view = LinearLayout(context)
                var layoutParameter =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                view.layoutParams = layoutParameter
                return view
            }
        }

        init {
            itemView as LinearLayout
            //设置Item的高度
            itemView.layoutParams.height =
                (context.resources.displayMetrics.density * getItemHeight(type)).toInt()

            var contentView = getItemLayout(type)
            if (contentView is View) {
                itemView.addView(contentView)
                initView = true
                viewCreated()
            } else if (contentView is Int) {
                CoroutineUtils.launchLayout(itemView.context, contentView, {
                    it.layoutParams = itemView.layoutParams
                    itemView.addView(it)
                    initView = true
                    viewCreated()
                    refreshView()
                })
            } else {
                throw Exception("Please Set Content View")
            }
        }

        /**
         * 重写指定 Item高度,首次加载高度不影响尺寸变换动画
         */
        abstract fun getItemHeight(type: Int): Int

        /**
         * 确定加载布局
         */
        abstract fun getItemLayout(type: Int): Any

        /**
         * 异步视图加载完成
         */
        open fun viewCreated() {

        }

        /**
         * 绑定视图到界面
         */
        abstract fun onBindViewData(type: Int, itemView: View, data: Any)

        fun onBaind(data: Any) {
            this.cacheData = data
            refreshView()
        }

        /**
         * 刷新视图
         */
        fun refreshView() {
            if (!initView || cacheData == null) {
                return
            }
            onBindViewData(type, itemView, cacheData!!)
        }
    }


    /**
     * ============================================================
     * 数据管理器
     *      所有列表都使用LinearLayoutManage ,遇到网格布局时将将数据格式化为多列数据
     * ============================================================
     */
    companion object {
        /**
         * 将数据转换为多列数据格式
         */
        fun makeColumnData(
            datas: MutableList<Any>,
            column: Int,
            type: Int = 0
        ): MutableList<RowData> {
            var tempData: MutableList<RowData> = arrayListOf()
            var rowCount = datas.size / column + if (datas.size % column != 0) 1 else 0

            for (rowIndex in 0 until rowCount) {
                var temp: MutableList<Any> = arrayListOf()
                for (columnIndex in 0 until column) {
                    var position = rowIndex * column + columnIndex
                    if (position >= datas.size) break
                    temp.add(datas[position])
                }
                var rowData = RowData(rowIndex, column, type, temp)
                tempData.add(rowData)
            }

            return tempData
        }

        /**
         * 找到指定数据所在行的数据
         */
        fun findRowData(
            datas: MutableList<RowData>,
            data: Any,
            equal: (Any, Any) -> Boolean
        ): RowData? {
            for (item in datas) {
                for (columnData in item.data) {
                    if (equal(columnData, data)) {
                        return item
                    }
                }
            }
            return null
        }
    }


    // 多列数据
    class RowData constructor(
        var rowPosition: Int,            // 当前数据所在行位置
        var column: Int,                 // 当前行的列数
        var type: Int,                   // 当前行显示的数据类型
        var data: MutableList<Any>,      // 当前行列数据
        var actionColumnIndex: Int = 0   // 表示点击位置
    )

}