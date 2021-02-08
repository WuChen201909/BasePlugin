package com.harrison.plugin.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager


/**
 * 自定义对话框 , 仿IOS风格
 *  1、标题
 *  2、内容
 *  3、按钮
 */
class CustomAlertDialog : DialogFragment() {

    var mDimAmount = 0.2f //背景昏暗度
    var radii = 30f //设置圆角的大小
    var mWidth = 0  //对话框宽度
    var mHeight = 0  //对话框高度

    var title: String? = null // 显示标题
    var content: String? = null // 对话框显示内容

    var canCancle = false //默认不可取消

    var actionList: MutableList<DialogAction> = arrayListOf()

    companion object {

        fun showDialog(
            fragmentManager: FragmentManager,
            content: String,
            canCancle:Boolean = false
        ) {
            this.showDialog(fragmentManager,null, content, "确认", null, null, null, null, null,canCancle)
        }

        fun showDialog(
            fragmentManager: FragmentManager,
            content: String,
            btnS01: String,
            btn01: () -> Unit,
            canCancle:Boolean = false
        ) {
            this.showDialog(fragmentManager,null, content, btnS01, btn01, null, null, null, null,canCancle)
        }

        fun showDialog(
            fragmentManager: FragmentManager,
            content: String,
            btnS01: String,
            btn01: () -> Unit,
            btnS02: String,
            btn02: () -> Unit,
            canCancle:Boolean = false
        ) {
            this.showDialog(fragmentManager,null, content, btnS01, btn01, btnS02, btn02, null, null,canCancle)
        }
        fun showDialog(
            fragmentManager: FragmentManager,
            content: String,
            btnS01: String?,
            btn01: (() -> Unit)?,
            btnS02: String?,
            btn02: (() -> Unit)?,
            btnS03: String?,
            btn03: (() -> Unit)?,
            canCancle:Boolean = false
        ) {
            this.showDialog(fragmentManager,null, content, btnS01, btn01, btnS02, btn02, btnS03, btn03,canCancle)
        }

        fun showDialog(
            fragmentManager: FragmentManager,
            title: String,
            content: String,
            btnS01: String,
            btn01: () -> Unit,
            canCancle:Boolean = false
        ) {

            this.showDialog(fragmentManager,title, content, btnS01, btn01, null, null, null, null,canCancle)
        }

        fun showDialog(
            fragmentManager: FragmentManager,
            title: String,
            content: String,
            btnS01: String,
            btn01: () -> Unit,
            btnS02: String,
            btn02: () -> Unit,
            canCancle:Boolean = false
        ) {
            this.showDialog(fragmentManager,title, content, btnS01, btn01, btnS02, btn02, null, null,canCancle)
        }

        fun showDialog(
            fragmentManager: FragmentManager,
            title: String?,
            content: String,
            btnS01: String?,
            btn01: (() -> Unit)?,
            btnS02: String?,
            btn02: (() -> Unit)?,
            btnS03: String?,
            btn03: (() -> Unit)?,
            canCancle:Boolean = false
        ) {
            var customAlertDialog: CustomAlertDialog = CustomAlertDialog()
            customAlertDialog.content = content
            customAlertDialog.canCancle = canCancle
            if(title != null){
                customAlertDialog.title = title
            }

            if (btnS01 != null) {
                var dialogAction = DialogAction()
                dialogAction.actionName = btnS01
                dialogAction.actionEvent = btn01
                customAlertDialog.actionList.add(dialogAction)
            }

            if (btnS02 != null) {
                var dialogAction = DialogAction()
                dialogAction.actionName = btnS02
                dialogAction.actionEvent = btn02
                customAlertDialog.actionList.add(dialogAction)
            }

            if (btnS03 != null) {
                var dialogAction = DialogAction()
                dialogAction.actionName = btnS03
                dialogAction.actionEvent = btn03
                customAlertDialog.actionList.add(dialogAction)
            }

            var transaction = fragmentManager.beginTransaction()
            transaction.add(customAlertDialog, "alert dialog")
            transaction.commit()
        }

    }


    /**
     * ===========================================================
     * 自定义对话框设置
     * ===========================================================
     */


    /**
     * ===========================================================
     * 自定义对话框内部处理
     * ===========================================================
     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        measureSize()


        var cancle = actionList.size == 0 || canCancle // 没有按钮时可以取消
        //配置当前对话框
        dialog?.let {
            it.setCanceledOnTouchOutside(cancle)
            it.window?.let {
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //设置背景为透明
                it.setDimAmount(mDimAmount); //设置背景透明度 范围是0.1f-1.0f
                isCancelable = cancle
            }
        }

        //设置背景
        var backgroundLayout: FrameLayout = FrameLayout(requireContext())
        var layoutParams = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        backgroundLayout.layoutParams = layoutParams
        backgroundLayout.background = fullRadDrawable()
    
        //设置内容布局
        var contentLayout: LinearLayout = LinearLayout(requireContext())
        var marginLayoutParams = ViewGroup.MarginLayoutParams(
            mWidth,
            mHeight
        )
        contentLayout.layoutParams = marginLayoutParams
        contentLayout.orientation = LinearLayout.VERTICAL
        backgroundLayout.addView(contentLayout)

        if (title != null) {
            //主要内容部分
            var titleView = TextView(requireContext())
            var titleLayoutParame = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            titleLayoutParame.setMargins(
                (radii / 2).toInt(),
                (radii / 2).toInt(),
                (radii / 2).toInt(),
                0
            )
            titleView.layoutParams = titleLayoutParame
            titleView.setText(title)
            titleView.setTextColor(Color.parseColor("#1C1C1C"))
            titleView.textSize = 16f
            contentLayout.addView(titleView)
        }

        var contentView = TextView(requireContext())
        var contentLayoutParame = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        contentLayoutParame.setMargins(
            (radii / 2).toInt(),
            (radii / 2).toInt(),
            (radii / 2).toInt(),
            0
        )
        contentLayoutParame.gravity = Gravity.CENTER
        contentView.layoutParams = contentLayoutParame
        contentView.setText(content)
        contentView.setTextColor(Color.parseColor("#1C1C1C"))
        contentView.textSize = 13f
        contentLayout.addView(contentView)

        if (actionList.size != 0) {
            var dividerView = View(requireContext())
            var dividerLayoutParame =
                ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
            dividerView.layoutParams = dividerLayoutParame
            dividerLayoutParame.setMargins(0, (radii / 2).toInt(), 0, 0)
            dividerView.setBackgroundColor(Color.parseColor("#E5E5E5"))
            contentLayout.addView(dividerView)

            var btnLayout = LinearLayout(requireContext())
            var btnLayoutParame = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                70
            )
            btnLayout.layoutParams = btnLayoutParame
            btnLayout.orientation = LinearLayout.HORIZONTAL
            btnLayout.gravity = Gravity.CENTER_HORIZONTAL
            btnLayout.setBackgroundColor(Color.TRANSPARENT)

            contentLayout.addView(btnLayout)

            var btnWidth: Int = ((mWidth - (actionList.size - 1) * 1) / actionList.size).toInt()

            // 添加按钮
            for ((index, action) in actionList.withIndex()) {
                var button = TextView(requireContext())
                var marginLayoutParams =
                    ViewGroup.MarginLayoutParams(btnWidth, ViewGroup.LayoutParams.MATCH_PARENT)
                button.layoutParams = marginLayoutParams
                button.setText(action.actionName)
                button.textSize = 13f
                button.setTextColor(Color.parseColor("#3691E6"))
                button.gravity = Gravity.CENTER

                // 选用不同的背景图
                if (actionList.size == 1) {
                    button.background = bottonRadDrawable()
                } else if (index == 0) {
                    button.background = lbRadSrawable()
                } else {
                    button.background = rbRadSrawable()
                }

                //添加分割线
                if (actionList.size > 1 && index != 0) {
                    var vDividerView = View(requireContext())
                    var vDividerLayoutParams =
                        ViewGroup.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT)
                    vDividerView.layoutParams = vDividerLayoutParams
                    vDividerView.setBackgroundColor(Color.parseColor("#E5E5E5"))
                    btnLayout.addView(vDividerView)
                }

                button.setOnClickListener {
                    dismiss()
                    action.actionEvent?.let {
                        it()
                    }
                }

                btnLayout.addView(button)
            }
        }
        return backgroundLayout
    }


    private fun measureSize() {
        if (mWidth == 0) {
            mWidth =
                (requireContext().resources.displayMetrics.widthPixels - (50 * requireContext().resources.displayMetrics.density) * 2).toInt()
        }
        if (mHeight == 0) {
            mHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    class DialogAction {
        var actionName: String? = null
        var actionEvent: (() -> Unit)? = null
    }


    /**
     * ===========================================================
     * 用到的背景图
     * ===========================================================
     */

    /**
     * 全圆角背景
     */
    private fun fullRadDrawable(): Drawable {
        val drawable = GradientDrawable()
        drawable.setShape(GradientDrawable.RECTANGLE)
        drawable.setCornerRadii(
            floatArrayOf(
                radii,
                radii,
                radii,
                radii,
                radii,
                radii,
                radii,
                radii
            )
        )
        drawable.setColor(Color.WHITE)
        return drawable
    }

    /**
     * 底部圆角
     */
    private fun bottonRadDrawable(): Drawable {
        val drawable = GradientDrawable()
        drawable.setShape(GradientDrawable.RECTANGLE)
        drawable.setCornerRadii(
            floatArrayOf(
                0f,
                0f,
                0f,
                0f,
                radii,
                radii,
                radii,
                radii
            )
        )
        drawable.setColor(Color.WHITE)
        return drawable
    }

    private fun rbRadSrawable(): Drawable {
        val drawable = GradientDrawable()
        drawable.setShape(GradientDrawable.RECTANGLE)
        drawable.setCornerRadii(
            floatArrayOf(
                0f,
                0f,
                0f,
                0f,
                radii,
                radii,
                0f,
                0f
            )
        )
        drawable.setColor(Color.WHITE)
        return drawable
    }

    private fun lbRadSrawable(): Drawable {
        val drawable = GradientDrawable()
        drawable.setShape(GradientDrawable.RECTANGLE)
        drawable.setCornerRadii(
            floatArrayOf(
                0f,
                0f,
                0f,
                0f,
                0f,
                0f,
                radii,
                radii
            )
        )
        drawable.setColor(Color.WHITE)
        return drawable
    }

}