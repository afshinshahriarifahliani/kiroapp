package com.emberfocus.feature.timer.di

import com.emberfocus.feature.timer.TimerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val timerModule = module {
    viewModel { TimerViewModel(get(), get()) }
}
