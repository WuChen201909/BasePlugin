package com.harrison.plugin.mvvm.base

/**
 * 通用的Model接口设计
 */
interface IModel {

    /**
     * 创建当前啊数据对象
     *
     */
    fun instanceModel():IModel

    /**
     * 清空数据
     */
    fun removeInstance(model:IModel)
}