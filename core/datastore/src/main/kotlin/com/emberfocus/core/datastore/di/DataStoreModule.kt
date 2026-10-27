package com.emberfocus.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import com.emberfocus.core.datastore.PreferencesDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "emberfocus_prefs")

val dataStoreModule = module {
    single<DataStore<Preferences>> { androidContext().dataStore }
    single { PreferencesDataSource(get()) }
}
