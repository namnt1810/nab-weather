package vn.namnt.nabweather.presentation.internal.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import vn.namnt.nabweather.common.di.WorkerKey
import vn.namnt.nabweather.presentation.internal.worker.WeatherInfoCleanupWorker
import vn.namnt.nabweather.presentation.internal.worker.WeatherInfoCleanupWorkerFactory
import vn.namnt.nabweather.presentation.utils.AbstractWorkerFactory

@Module
internal abstract class WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(WeatherInfoCleanupWorker::class)
    internal abstract fun bindWeatherInfoCleanupWorker(f: WeatherInfoCleanupWorkerFactory): AbstractWorkerFactory
}
