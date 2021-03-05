package com.example.baseplugin.view

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.baseplugin.R
import com.example.baseplugin.http.TestServer
import com.harrison.plugin.mvvm.BaseActivityView
import com.harrison.plugin.util.io.CoroutineUtils
import com.harrison.plugin.util.io.CoroutineUtils.TaskQueueManager
import com.harrison.plugin.util.ui.ToastUtils
import com.harrison.plugin.widget.CustomAlertDialog
import com.harrison.plugin.widget.LoadingView
import kotlinx.android.synthetic.main.activity_one.*

class OneActivity : BaseActivityView() {
    override fun getViewLayout(): Any {
        return R.layout.activity_one
    }

    var gtt  = TaskQueueManager<View>()

    override fun viewCreated() {
        bt_032.setOnClickListener {
            Toast.makeText(this,"点击",Toast.LENGTH_SHORT).show()
        }
        bt_033.setOnClickListener {
            Toast.makeText(this,"点击2",Toast.LENGTH_SHORT).show()
        }

        bt_01.setOnClickListener {
//            var intent = Intent(this,OneActivity::class.java)
//            startActivity(intent)
//            pushNavigator(TowFragment())

//            CustomAlertDialog.Companion.showDialog(supportFragmentManager,"标题","提示","确认",{
//
//            },"取消",{
//
//            },"取消1",{
//
//            })

            LoadingView.fillInLayout(root_layout).setStatue(LoadingView.LOADING)

        }

        var layoutRoot = LinearLayout(this)

//        CoroutineUtils.launchIO {
//            TestServer().instance().getInformationType()
//        }


    }




}