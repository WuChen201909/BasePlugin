package com.example.baseplugin.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baseplugin.R
import com.harrison.plugin.mvvm.BaseActivityView
import com.harrison.plugin.util.developer.LogUtils
import com.harrison.plugin.widget.CompatRecyclerView
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity: BaseActivityView() {


    var data = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (i in 1..100){
            data.add("Test Data :"+i)
        }
    }

    override fun getViewLayout(): Any {
        return R.layout.activity_second
    }

    var handler :Handler = Handler(Looper.getMainLooper())

    override fun viewCreated() {

        var adapter : CompatRecyclerView.CompatRecyclerAdapter<MyViewHolder,String> = object:CompatRecyclerView.CompatRecyclerAdapter<MyViewHolder,String>(){
            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)
                var item = mData[position]
                holder.content.text = item
            }
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                var view = LayoutInflater.from(parent.context).inflate(R.layout.item_test,parent,false)
                return MyViewHolder(view)
            }
        }

        adapter.setOnItemClickListener(object :
            CompatRecyclerView.CompatRecyclerAdapter.OnItemClickListener<String>{
            override fun onItemClick(d: String) {
                Log.i("result","点击Item"+d)
            }
        })

        adapter.mData.addAll(data)
        crv_test.adapter = adapter
        crv_test.layoutManager = LinearLayoutManager(this)


        Thread{
            while (true){

                var position = (Math.random()*adapter.mData.size-1).toInt()
                var content = "便跟内容"+Math.random()*3000
                handler.post {
                    adapter.mData[position]  = content
                    adapter.notifyItemChanged(position)
                }

                Log.i("result","refresh item")
                Thread.sleep(500)
            }
        }.start()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var content = itemView.findViewById<TextView>(R.id.tv_test)
    }
}