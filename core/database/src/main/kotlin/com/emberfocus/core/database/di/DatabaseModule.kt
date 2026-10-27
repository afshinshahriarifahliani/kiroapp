package com.emberfocus.core.database.di

import androidx.room.Room
import com.emberfocus.core.database.EmberFocusDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            EmberFocusDatabase::class.java,
            "emberfocus.db",
        ).fallbackToDestructiveMigration().build()
    }
    single { get<EmberFocusDatabase>().habitDao() }
    single { get<EmberFocusDatabase>().focusSessionDao() }
}
