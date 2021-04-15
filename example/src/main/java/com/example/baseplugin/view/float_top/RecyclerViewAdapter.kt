package com.ym.floattopwidget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.baseplugin.R
import com.example.baseplugin.view.float_top.MyData


class RecyclerViewAdapter(var data:ArrayList<MyData>) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var content = itemView.findViewById<TextView>(R.id.tv_item_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var myView = LayoutInflater.from(parent.context).inflate(R.layout.item_float_top,parent,false)
        return MyViewHolder(myView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.content.text = data[position].title
    }

    override fun getItemCount(): Int {
        return data.size
    }
}