package com.harrison.plugin.mvvm.core.provider

import com.harrison.plugin.mvvm.base.IModel

/**
 * Model 提供
 */
interface IModelProvider {

    /**
     * 根据指定ID获取对应的Model
     */
    fun factorModel(id:Int):IModel

    /**
     * 默认保存Model，如果不需要则将Model清除
     */
    fun cleanModel(id:Int)

}