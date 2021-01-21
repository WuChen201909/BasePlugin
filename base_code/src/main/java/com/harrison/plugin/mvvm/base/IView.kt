package com.harrison.plugin.mvvm.base

/**
 * 通用的View接口设置
 *      定义视图生命周期函数
 */
interface IView {

    /**
     *  界面初始化
     */
    fun initView()

    /**
     * 绑定ViewModel进行操作，一般是界面在前台的状态  // 去掉
     */
    fun bindViewModel()

    /**
     * 初始化观察者，页面的数据响应
     *  1、响应新数据和初始数据
     */
    fun initViewObservable()

    /**
     * 取消数据绑定ViewModel的操作，表示界面在后台或已经销毁
     */
    fun unBindViewModel()

}