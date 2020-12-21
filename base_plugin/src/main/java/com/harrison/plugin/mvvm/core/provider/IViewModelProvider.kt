package com.harrison.plugin.mvvm.core.provider


import com.harrison.plugin.mvvm.base.IView
import com.harrison.plugin.mvvm.base.IViewModel

interface IViewModelProvider {
    /**
     * 创建对应的ViewModel
     */
    fun factorModel(view: IView): IViewModel

}