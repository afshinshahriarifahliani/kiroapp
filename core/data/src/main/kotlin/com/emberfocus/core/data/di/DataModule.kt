package com.emberfocus.core.data.di

import com.emberfocus.core.data.DefaultHabitRepository
import com.emberfocus.core.data.DefaultPreferencesRepository
import com.emberfocus.core.data.DefaultSessionRepository
import com.emberfocus.core.data.HabitRepository
import com.emberfocus.core.data.PreferencesRepository
import com.emberfocus.core.data.SessionRepository
import org.koin.dsl.module

val dataModule = module {
    single<HabitRepository> { DefaultHabitRepository(get()) }
    single<SessionRepository> { DefaultSessionRepository(get()) }
    single<PreferencesRepository> { DefaultPreferencesRepository(get()) }
}
