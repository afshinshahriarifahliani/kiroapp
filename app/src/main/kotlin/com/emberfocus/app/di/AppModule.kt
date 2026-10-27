package com.emberfocus.app

import com.emberfocus.app.ui.AppViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AppViewModel(get(), get()) }
}
