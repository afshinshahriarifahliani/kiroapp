package com.emberfocus.core.billing.di

import com.emberfocus.core.billing.BillingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val billingModule = module {
    single(named("billingScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    single {
        BillingManager(
            context = androidContext(),
            preferences = get(),
            scope = get(named("billingScope")),
        )
    }
}
