package com.harrison.plugin.mvvm

interface BaseView {

    fun onTaskTop()
    /**
     * 当前驶入被押入栈底的事件回掉
     */
    fun onTaskCover()

}