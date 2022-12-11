package vn.namnt.nabweather

import android.content.Context
import androidx.multidex.MultiDex
import androidx.work.*
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import vn.namnt.nabweather.common.di.DaggerCommonComponent
import vn.namnt.nabweather.data.di.DaggerDataComponent
import vn.namnt.nabweather.presentation.di.DaggerAppComponent
import vn.namnt.nabweather.presentation.utils.AppWorkerFactory
import vn.namnt.nabweather.presentation.worker.WeatherInfoCleanupWorker
import vn.namnt.nabweather.repository.di.DaggerDomainComponent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author namnt
 * @since 08/12/2022
 */
class WeatherApplication : DaggerApplication(), Configuration.Provider {
    @Inject
    lateinit var delegatingWorkerFactory: DelegatingWorkerFactory

    @Inject
    lateinit var workerFactory: AppWorkerFactory

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        delegatingWorkerFactory.addFactory(workerFactory)

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "cleanup_weather_info_task",
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<WeatherInfoCleanupWorker>(15, TimeUnit.MINUTES).build()
            )
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val commonComponent = DaggerCommonComponent.factory()
            .create(applicationContext)
        val dataComponent = DaggerDataComponent.factory()
            .create(commonComponent)
        val domainComponent = DaggerDomainComponent.factory()
            .create(dataComponent)
        return DaggerAppComponent.factory()
            .create(this, DelegatingWorkerFactory(), commonComponent, domainComponent)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(delegatingWorkerFactory)
            .build()
    }
}
