package com.example.baseplugin

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.harrison.plugin.mvvm.core.AViewProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_jump_to_mvvm.setOnClickListener(onClickListener)
        button_jump_to_second.setOnClickListener(onClickListener)
    }

    private var onClickListener = View.OnClickListener {
        when (it.id) {
            R.id.button_jump_to_mvvm -> {
            }
            R.id.button_jump_to_second ->{
                AViewProvider.pushBack(SecondActivity::class.java)
            }
        }
    }

}