package com.emberfocus.feature.stats.di

import com.emberfocus.feature.stats.StatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val statsModule = module {
    viewModel { StatsViewModel(get(), get()) }
}
