package com.emberfocus.feature.timer.di

import androidx.media3.common.util.UnstableApi
import com.emberfocus.feature.timer.TimerViewModel
import com.emberfocus.feature.timer.audio.DefaultSoundscapeController
import com.emberfocus.feature.timer.audio.SoundscapeController
import com.emberfocus.feature.timer.audio.SoundscapePlayer
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@OptIn(UnstableApi::class)
val timerModule = module {
    single { SoundscapePlayer(androidContext()) }
    single<SoundscapeController> { DefaultSoundscapeController(androidContext()) }
    viewModel { TimerViewModel(get(), get(), get()) }
}
