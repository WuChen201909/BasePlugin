package com.harrison.plugin.mvvm.base

/**
 * 通用的View接口设置
 */
interface IView {

    /**
     * 初始化视图
     */
    fun initView()

    /**
     * 初始化观察者
     *  1、响应新数据和初始数据
     */
    fun initViewObservable()

    /**
     * 和ViewModel绑定
     */
    fun bindViewModel()

    /**
     * 取消数据绑定
     */
    fun unBindViewModel()

}