package app.test.networkapp.di

import androidx.room.Room
import app.test.networkapp.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "ip_lookup_database"
        ).build()
    }

    single { get<AppDatabase>().organizationDao() }
    single { get<AppDatabase>().networkDao() }
}