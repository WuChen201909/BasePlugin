package com.harrison.plugin.mvvm.base.impl

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.harrison.plugin.mvvm.base.IViewModel


class ABaseViewModel(application: Application) : IViewModel, AndroidViewModel(application) {

    override fun initModel(){

    }

}

