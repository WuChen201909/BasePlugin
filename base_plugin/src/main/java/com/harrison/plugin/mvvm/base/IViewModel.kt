package com.harrison.plugin.mvvm.base

import androidx.lifecycle.ViewModel

/**
 * 通用的ViewModel接口设计
 */
interface IViewModel {

    /**
     * 1、初始化Model
     * 2、向View发送数据
     */
    fun initModel()

}