package com.harrison.plugin.mvvm.core.provider

import com.harrison.plugin.mvvm.base.IView

/**
 * 视图提供
 *  1、解决视图栈的管理
 */
interface IViewProvider {

    /**
     * 视图入栈
     */
    fun pushBack(view:IView)

    /**
     * 视图出栈
     */
    fun popView(view:IView)

}