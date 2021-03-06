package com.example.baseplugin.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baseplugin.R
import com.example.baseplugin.widget.CustomLayoutManager
import com.harrison.plugin.mvvm.BaseActivityView
import kotlinx.android.synthetic.main.activity_second.*


class SecondActivity : BaseActivityView() {

    var data = arrayListOf<String>()
    var data02 = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewLayout(): Any {
        return R.layout.activity_second
    }

    var handler: Handler = Handler(Looper.getMainLooper())

    override fun viewCreated() {



//        var adapter = object: RecyclerView.Adapter<CusttomViewHoler>() {
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CusttomViewHoler {
//                Log.i("result","创建视  ========")
//                return CusttomViewHoler(LayoutInflater.from(parent.context).inflate(R.layout.item_test,parent,false))
//            }
//
//            override fun onBindViewHolder(holder: CusttomViewHoler, position: Int) {
//                holder.itemView.findViewById<TextView>(R.id.tv_test).setText("====")
//            }
//
//            override fun getItemCount(): Int {
//                return data.size
//            }
//
//            override fun getItemViewType(position: Int): Int {
//                return if(position%2 == 0) 1 else 2
//            }
//
//        }

        for (item in 0..100) {
            data.add("测试数据：" + item)
        }

        for (item in 0..10) {
            data02.add("标题类数据：" + item)
        }


//        recyclerview_list.layoutManager = LinearLayoutManager(this)
//        recyclerview_list.addItemDecoration(CustomItemDecoration())
//
//        recyclerview_list.floatLayout =   ll_float

    }

    fun createInfoView(info: String): View {
        var rootView = LinearLayout(this)
        var parameter = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rootView.layoutParams = parameter

        var textView = TextView(this)
        parameter = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.layoutParams = parameter
        textView.text = info
        rootView.addView(textView)

        return rootView
    }

    private fun createBitmap(view: View): Bitmap? {
        view.buildDrawingCache()
        return view.drawingCache
    }

    fun createBitmap2(v: View): Bitmap? {
        val bmp = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        v.draw(c)
        return bmp
    }

    inner class CustomItemDecoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.bottom = 10
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDrawOver(c, parent, state)
        }

    }


}