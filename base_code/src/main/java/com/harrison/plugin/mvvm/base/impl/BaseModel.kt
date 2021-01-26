package com.harrison.plugin.mvvm.base.impl

import com.harrison.plugin.mvvm.base.IModel

open class BaseModel :IModel {
    override fun instanceModel(): IModel {
        return this
    }

    override fun removeInstance(model: IModel) {

    }
}