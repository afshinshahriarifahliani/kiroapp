package com.emberfocus.app

import android.app.Application
import com.emberfocus.core.billing.di.billingModule
import com.emberfocus.core.common.di.commonModule
import com.emberfocus.core.data.di.dataModule
import com.emberfocus.core.database.di.databaseModule
import com.emberfocus.core.datastore.di.dataStoreModule
import com.emberfocus.feature.habits.di.habitsModule
import com.emberfocus.feature.paywall.di.paywallModule
import com.emberfocus.feature.settings.di.settingsModule
import com.emberfocus.feature.stats.di.statsModule
import com.emberfocus.feature.timer.di.timerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class EmberFocusApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@EmberFocusApp)
            modules(
                commonModule,
                dataStoreModule,
                databaseModule,
                billingModule,
                dataModule,
                timerModule,
                habitsModule,
                statsModule,
                settingsModule,
                paywallModule,
                appModule,
            )
        }
    }
}
