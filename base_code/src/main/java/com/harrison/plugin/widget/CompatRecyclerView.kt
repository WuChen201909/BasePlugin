package com.harrison.plugin.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
    var floatLayout: FrameLayout? = null

    //悬浮窗列表
    var floatDescribeList: MutableList<FloatDescribeItem> = arrayListOf()

    /**
     * 悬浮控件描述对象
     *  position  表示悬浮对象在数据中的位置
     *  level     悬浮控件等级
     *  height    悬浮控件的高度
     *  padding   悬浮控件的顶部边距
     *  view      悬浮控件本身
     */
    inner class FloatDescribeItem(
        var position: Int,
        var level: Int,
        var height: Int,
        var top: Int,
        var view: View? = null
    )

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
            // 找到当前层级最高级应该显示的位置
            var startPosition = findHightLevelPosition()

            //获取当前悬浮窗描述列表
            var newFloatDescribeList: MutableList<FloatDescribeItem> = getNewFloatDescribeList(startPosition)

            //删除与新描述列表不同的部分
            clearOldDefferentDescribe(floatDescribeList, newFloatDescribeList)

            //添加新的描述列表中的悬浮控件
            addNewFloatLayout(newFloatDescribeList)
        }

        /**
         * 找到当前悬浮窗顶级所在的 position
         *  @return 返回找到的位置
         */
        fun findHightLevelPosition(): Int {
            var position = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            var hightLevel = Int.MAX_VALUE  //等级值越大表示等级越低，先设置为最低等级依次往上找
            var mAdapter = adapter as CompatAdapter
            for (index in position downTo 0) {
                var type = mAdapter.getItemViewType(index)
                if (mAdapter.isFloat(type)) {  // 找到浮动窗口则开始判断
                    var currentLevel = mAdapter.floatLevel(type)
                    if (hightLevel < currentLevel) {         // 找到比当前等级高的控件
                        hightLevel = currentLevel
                    } else if (hightLevel <= currentLevel   // 找到低于当前等级的浮动控件，表示找到当前分组最高等级
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
         *      1、先添加显示区以外的描述列表
         *      2、再添加显示区中的描述列表
         */
        fun getNewFloatDescribeList(
            startPosition: Int
        ): MutableList<FloatDescribeItem> {

            var mAdapter = adapter as CompatAdapter
            var newFloatDescribeList: MutableList<FloatDescribeItem> = arrayListOf()    //新列表
            var tempCovertHeight = 0

            //先将 firstShowPosition 以外的悬浮窗添加到列表
            var firstShowPosition =
                (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            for (currentPosition in startPosition..firstShowPosition) {
                var type = mAdapter.getItemViewType(currentPosition)
                if (mAdapter.isFloat(type)) {
                    tempCovertHeight =
                        addFloatDescribe(
                            newFloatDescribeList,
                            currentPosition,
                            mAdapter.floatLevel(type)
                        )
                }
            }

            // 再将 firstShowPosition 以后的的悬浮窗添加到列表
            var currentVisibilityCount = 0
            while (true) {
                // 依次处理显示区域的视图
                var visibilityView = getChildAt(currentVisibilityCount)
                var showPosition = getChildAdapterPosition(visibilityView)
                currentVisibilityCount++

                if (newFloatDescribeList.size != 0 && showPosition == newFloatDescribeList.last().position) {
                    continue
                }

                if (visibilityView.top >= tempCovertHeight) {
                    break
                }

                var type = mAdapter.getItemViewType(showPosition)

                if (mAdapter.isFloat(type)) {
                    tempCovertHeight = addFloatDescribe(
                        newFloatDescribeList,
                        showPosition, mAdapter.floatLevel(type), visibilityView
                    )
                }
            }

            return newFloatDescribeList
        }

        /**
         * 添加一个浮动描述
         *  @param  showView 如果添加的悬浮窗已经显示到窗口中则将窗口传递到当前函数用于判断
         *  @return 返回当前描述列表应该显示的高度
         */
        fun addFloatDescribe(
            flostDesList: MutableList<FloatDescribeItem>,
            position: Int, level: Int, showView: View? = null
        ): Int {
            var mAdapter = adapter as CompatAdapter

            var lastTop = if (flostDesList.size == 0) {
                0
            } else {
                flostDesList.last().top + flostDesList.last().height
            }

            var consume = false
            var index = 0
            while (flostDesList.size > 0 && flostDesList.size > index) {
                var item = flostDesList[index]

                if (item.position == position) continue //

                if (level <= item.level) {      // 找到等级不相同的情况
                    if (showView == null) {     // 显示区以外
                        //删除所有当前位置的控件
                        while (flostDesList.size > index) {
                            flostDesList.removeLast()
                        }
                        lastTop = if (flostDesList.size == 0) {
                            0
                        } else {
                            flostDesList.last().top + flostDesList.last().height
                        }
                        //追加描述
                        var type = mAdapter.getItemViewType(position)
                        flostDesList.add(
                            FloatDescribeItem(
                                position,
                                level,
                                mAdapter.getItemHeight(type),
                                lastTop
                            )
                        )
                        consume = true
                        break
                    } else {       //显示区中

                        var overlapHeight = showView.top - lastTop // 折叠高度
//                        Log.e("result", "折叠高度计算：" + overlapHeight)
                        if (overlapHeight >= 0) {
                            throw Exception("追加不应该追加的数据  " + showView.top + "  ")
                        }

                        // 调整因为折叠造成的位移
                        var tempIndex = index
                        var lastBottom =
                            if (index == 0) 0 else flostDesList[index - 1].top + flostDesList[index - 1].height
                        while (flostDesList.size > tempIndex) {
                            flostDesList[tempIndex].top += overlapHeight
                            if (lastBottom != 0 && flostDesList[tempIndex].top + flostDesList[tempIndex].height < lastBottom) {
                                flostDesList[tempIndex].top = lastBottom - flostDesList[tempIndex].height
                                break
                            }
                            tempIndex++
                        }


                        lastTop = if (flostDesList.size == 0) {
                            0
                        } else {
                            flostDesList.last().top + flostDesList.last().height
                        }
                        //追加描述
                        var type = mAdapter.getItemViewType(position)
                        flostDesList.add(
                            FloatDescribeItem(
                                position,
                                level,
                                mAdapter.getItemHeight(type),
                                lastTop
                            )
                        )
                        consume = true
                        break
                    }
                }

                index++
            }

            // 表示当前列表中的等级都高于当前等级，直接追加浮动窗口描述
            if (!consume) {
                var type = mAdapter.getItemViewType(position)
                flostDesList.add(
                    FloatDescribeItem(
                        position,
                        level,
                        mAdapter.getItemHeight(type),
                        lastTop
                    )
                )
            }

            var covertHeight =
                if (flostDesList.size == 0) 0 else (flostDesList.last().top + flostDesList.last().height)

            return covertHeight
        }


        /**
         * 将原列表和当前列表不相同的部分删除
         */
        fun clearOldDefferentDescribe(
            oldList: MutableList<FloatDescribeItem>,
            newList: MutableList<FloatDescribeItem>
        ) {

            // 老列表数量多余新列表表时移除老列表多出部分
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

        }

        /**
         *  追加新浮动布局
         */
        fun addNewFloatLayout(newList: MutableList<FloatDescribeItem>) {
            var mAdapter = adapter as CompatAdapter

            //追加悬浮视图
            while (floatDescribeList.size != newList.size) {
                var currentDescribe = newList[floatDescribeList.size]
                var type = mAdapter.getItemViewType(currentDescribe.position)

                var vewHolder = mAdapter.createViewHolder(floatLayout!!, type)
                mAdapter.bindViewHolder(vewHolder, currentDescribe.position)
                vewHolder.itemView.z = -floatDescribeList.size.toFloat()

                currentDescribe.view = vewHolder.itemView
                floatLayout!!.addView(currentDescribe.view)
                floatDescribeList.add(currentDescribe)
            }

            //移动到指定位置
            for (index in 0 until floatDescribeList!!.size) {
                var itemView = floatDescribeList[index].view

                var layoutParame = itemView!!.layoutParams as FrameLayout.LayoutParams
                layoutParame.topMargin = newList[index].top
                itemView.layoutParams = layoutParame

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
         * 重写指定 Item高度,首次加载高度不影响尺寸变换动画
         */
        abstract fun getItemHeight(type: Int): Int

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
        var type: Int, var initHeight: Int
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
            itemView.layoutParams.height = initHeight

            var contentView = getItemLayout(type)
            if (contentView is View) {
                itemView.addView(contentView)
                initView = true
                viewCreated()
            } else if (contentView is Int) {
                CoroutineUtils.launchLayout(itemView.context, contentView, {
                    it.layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
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
         *
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