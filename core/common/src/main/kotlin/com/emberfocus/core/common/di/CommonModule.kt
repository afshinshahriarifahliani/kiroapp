package com.emberfocus.core.common.di

import com.emberfocus.core.common.DefaultDispatcherProvider
import com.emberfocus.core.common.DispatcherProvider
import org.koin.dsl.module

val commonModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}
