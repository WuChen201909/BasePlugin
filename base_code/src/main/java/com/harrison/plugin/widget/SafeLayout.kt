package com.harrison.plugin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.harrison.plugin.util.developer.LogUtils

/**
 * 安全区布局
 */
class SafeLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {


    var titleView: View

    init {
        var titleHeight = 0;
        var resourceId =
            context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            titleHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        orientation = LinearLayout.VERTICAL
        titleView = View(context)
        var layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, titleHeight)
        titleView.layoutParams = layoutParams
        addView(titleView)

    }


}