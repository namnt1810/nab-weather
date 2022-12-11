package vn.namnt.nabweather.presentation.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import vn.namnt.nabweather.di.WorkerKey
import vn.namnt.nabweather.presentation.utils.AbstractWorkerFactory
import vn.namnt.nabweather.presentation.worker.WeatherInfoCleanupWorker
import vn.namnt.nabweather.presentation.worker.WeatherInfoCleanupWorkerFactory

@Module
internal abstract class WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(WeatherInfoCleanupWorker::class)
    internal abstract fun bindWeatherInfoCleanupWorker(f: WeatherInfoCleanupWorkerFactory): AbstractWorkerFactory
}
