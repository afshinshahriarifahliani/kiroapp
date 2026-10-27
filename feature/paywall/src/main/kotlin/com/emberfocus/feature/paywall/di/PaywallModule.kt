package com.emberfocus.feature.paywall.di

import com.emberfocus.feature.paywall.PaywallViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val paywallModule = module {
    viewModel { PaywallViewModel(get()) }
}
