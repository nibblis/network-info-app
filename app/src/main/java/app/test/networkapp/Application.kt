package app.test.networkapp

import android.app.Application
import app.test.networkapp.di.databaseModule
import app.test.networkapp.di.networkModule
import app.test.networkapp.di.repositoryModule
import app.test.networkapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Application)
            modules(networkModule, repositoryModule, viewModelModule, databaseModule)
        }
    }
}