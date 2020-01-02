package vn.com.jvs.updatecontacts.app

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import vn.com.jvs.updatecontacts.di.viewModelModule

class MainApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this@MainApp)
        setupTimber()
        setupKoin()
    }

    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@MainApp)
            modules(
                    listOf(
                            viewModelModule
                    )
            )
        }
    }
}