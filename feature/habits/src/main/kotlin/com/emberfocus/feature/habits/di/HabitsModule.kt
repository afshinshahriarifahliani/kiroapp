package com.emberfocus.feature.habits.di

import com.emberfocus.feature.habits.HabitsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val habitsModule = module {
    viewModel { HabitsViewModel(get()) }
}
