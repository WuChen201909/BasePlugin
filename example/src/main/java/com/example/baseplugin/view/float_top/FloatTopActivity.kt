package com.ym.floattopwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baseplugin.R
import com.example.baseplugin.view.float_top.MyData
import com.harrison.plugin.widget.FloatTopWidget


class FloatTopActivity : AppCompatActivity() {

    var myData: ArrayList<MyData> = arrayListOf()

    lateinit var listView:RecyclerView
    lateinit var adapter:RecyclerViewAdapter
    lateinit var floatLayout: FloatTopWidget

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_float_top)

        //生成假数据
        for (index in 0..100) {
            for (level0 in 0..10) {
                myData.add(MyData("测试数据 $level0", 0, -1))
            }
            myData.add(MyData("悬浮等级 1  ================= $index", 0, 0))

            for (level0 in 0..5) {
                myData.add(MyData("测试数据 $level0", 0, -1))
            }
            myData.add(MyData("悬浮等级 2  =================  ", 0, 1))
            for (level0 in 0..5) {
                myData.add(MyData("测试数据 $level0", 0, -1))
            }
            myData.add(MyData("悬浮等级 3  =================  ", 0, 2))
            for (level0 in 0..5) {
                myData.add(MyData("测试数据 $level0", 0, -1))
            }
            myData.add(MyData("悬浮等级 3  =================  ", 0, 2))

            for (level0 in 0..5) {
                myData.add(MyData("测试数据 $level0", 0, -1))
            }
            myData.add(MyData("悬浮等级 2  =================  ", 0, 1))
            for (level0 in 0..5) {
                myData.add(MyData("测试数据 $level0", 0, -1))
            }
            myData.add(MyData("悬浮等级 3  =================  ", 0, 2))
        }

        //配置列表控件
        listView = findViewById(R.id.rl_info_list)
        listView.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerViewAdapter(myData)
        listView.adapter = adapter

        //浮动窗口设置
        floatLayout = findViewById(R.id.ftw_float_content)
        floatLayout.recyclerView = listView  //设置联动视图
        floatLayout.floatViewAdapter = object : FloatTopWidget.FloatViewAdapter() {  //设置相关数据
            override fun getLevelLayout(level: Int): Int {  //对应等级悬浮窗口加载的视图，功能呢类似于  onCreateViewHolder
                return R.layout.item_float_top
            }

            //设置RecyclerView中的对应Position是否是悬浮窗口，-1 代表非悬浮窗口大于等于0代表悬浮窗口
            //数字越小代表等级越高
            override fun getLevelByPosition(position: Int): Int {
                return myData[position].level
            }
            //悬浮视图的数据绑定
            override fun covertInfo(view: View, position: Int) {
                view.findViewById<TextView>(R.id.tv_item_content).text = myData[position].title
            }
        }

    }
}