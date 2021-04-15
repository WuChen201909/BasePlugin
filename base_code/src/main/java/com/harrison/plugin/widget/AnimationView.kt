package com.harrison.plugin.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout

/**
 * 带动画的扇形显示与隐藏父控件
 */
class AnimationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var valueAnimator: ValueAnimator = ValueAnimator()
    var animatorChangeValue:Int = 0
    private var paint = Paint()
    private var path = Path()
    private var size = -1

    private var bgColor = Color.parseColor("#994f4f4f")
    private val DURATION:Long= 800

    private var animatorVisibility:Int = View.GONE

    private var updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        animatorChangeValue = animation?.animatedValue as Int
        if(animatorChangeValue==0) visibility = animatorVisibility
        invalidate()
    }

    init {
        valueAnimator.duration = DURATION
        valueAnimator.addUpdateListener(updateListener)

        setBackgroundColor(Color.TRANSPARENT)

        paint.color = bgColor
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 1.0f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = MeasureSpec.getSize(heightMeasureSpec)+MeasureSpec.getSize(widthMeasureSpec)

    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawColor(Color.TRANSPARENT)

        path.reset()
        path.addCircle(
            (measuredWidth / 2).toFloat(),
            0.0f,
            animatorChangeValue.toFloat(),
            Path.Direction.CCW
        )

        canvas?.clipPath(path)
        canvas?.drawRect(Rect(0, 0, measuredWidth, measuredHeight), paint)
        super.onDraw(canvas)
    }


    fun setAnimatorVisibility(visibility: Int) {
        animatorVisibility = visibility
        if (visibility == View.VISIBLE) {
            setVisibility(View.VISIBLE)
            valueAnimator.setIntValues(0, size)
            valueAnimator.start()
        }else{
            valueAnimator.setIntValues(size, 0)
            valueAnimator.start()
        }
    }

}