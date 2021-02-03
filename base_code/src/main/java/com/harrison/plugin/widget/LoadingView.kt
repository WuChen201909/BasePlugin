package com.harrison.plugin.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.harrison.baseplugin.R

/**
 * 自定义loadingView,实现网络状态
 *
 * 使用方式
 *   调用后将会把状态视图填充到屏幕上
 *   LoadingView.fillInLayout(contentView)
 */
class LoadingView : LinearLayout, View.OnClickListener {
    private var imageView: ImageView? = null
    private var mRlError: RelativeLayout? = null
    private var mLlLoading: LinearLayout? = null
    private var mAni: AnimationDrawable? = null
    private var mView: View? = null
    private var ll_kong: View? = null
    private var mListener: OnRefreshListener? = null

    companion object {
        const val LOADING = 0 //加载中
        const val STOP_LOADING = 1 //停止加载
        const val NO_DATA = 2 //无数据
        const val NO_NETWORK = 3 //无网络
        const val GONE = 8 //隐藏
        const val LOADING_DIALOG = 5

        // 把加载视图注入到布局中
        fun fillInLayout(layout: ViewGroup): LoadingView {

            var parent :ViewGroup = layout.parent as ViewGroup
            parent.removeView(layout)

            var contentView = FrameLayout(layout.context)
            var contentLayoutParam = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            contentView.layoutParams = contentLayoutParam

            contentView.addView(layout)

            val loadingView = LoadingView(layout.context)
            val layoutParams: ViewGroup.LayoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            loadingView.layoutParams = layoutParams
            contentView.addView(loadingView)

            parent.addView(contentView)

            return loadingView
        }
    }

    fun setRefrechListener(mListener: OnRefreshListener?) {
        this.mListener = mListener
    }

    interface OnRefreshListener {
        fun refresh()
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var  mView = inflater.inflate(R.layout.loading_layout, this)
        mView.setBackgroundColor(Color.parseColor("#224f4f4f"))
        imageView = mView.findViewById<View>(R.id.iv_loading) as ImageView
        mLlLoading = mView.findViewById<View>(R.id.ll_loading) as LinearLayout
        ll_kong = mView.findViewById<View>(R.id.ll_kong) as LinearLayout
        mRlError = mView.findViewById<View>(R.id.rl_error) as RelativeLayout
        mAni = imageView!!.background as AnimationDrawable
        val tvError = mView.findViewById<View>(R.id.tv_error) as TextView
        //String exchange = getResources().getString(R.string.click_to_refresh);
        //tvError.setText(Html.fromHtml(exchange));
        mRlError!!.setOnClickListener(this)
        setStatue(GONE)
    }

    fun setStatue(status: Int) {
        visibility = VISIBLE
        try {
            if (status == LOADING) { //更新
                mRlError!!.visibility = View.GONE
                mLlLoading!!.visibility = VISIBLE
                ll_kong!!.visibility = View.GONE
                mAni!!.start()
            } else if (status == STOP_LOADING) {
                mAni!!.stop()
                visibility = View.GONE
                ll_kong!!.visibility = View.GONE
            } else if (status == NO_DATA) { //无数据情况
                mAni!!.stop()
                mRlError!!.visibility = View.GONE
                mLlLoading!!.visibility = View.GONE
                ll_kong!!.visibility = VISIBLE
            } else if (status == NO_NETWORK) { //无网络情况
                mAni!!.stop()
                mRlError!!.visibility = VISIBLE
                mLlLoading!!.visibility = View.GONE
                ll_kong!!.visibility = View.GONE
            } else {
                mAni!!.stop()
                visibility = View.GONE
                ll_kong!!.visibility = View.GONE
            }
        } catch (e: OutOfMemoryError) {
        }
    }

    override fun onClick(v: View) {
        if (mListener != null) {
            mListener!!.refresh()
            setStatue(LOADING)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        super.dispatchTouchEvent(ev)
        return true
    }


}