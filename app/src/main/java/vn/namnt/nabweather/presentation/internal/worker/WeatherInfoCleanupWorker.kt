package vn.namnt.nabweather.presentation.internal.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import vn.namnt.nabweather.BuildConfig
import vn.namnt.nabweather.domain.WeatherInfoRepository
import vn.namnt.nabweather.presentation.utils.AbstractWorkerFactory
import javax.inject.Inject

/**
 * @author namnt
 * @since 10/12/2022
 */

internal class WeatherInfoCleanupWorker(
    private val repository: WeatherInfoRepository,
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            val affectedRows = repository.cleanupWeatherInfo()

            Result.success(Data.Builder().putInt("affectedRows", affectedRows).build())
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(this::class.java.simpleName, e.message, e)
            }
            Result.failure()
        }
    }
}

internal class WeatherInfoCleanupWorkerFactory @Inject constructor(
    private val repository: WeatherInfoRepository
) : AbstractWorkerFactory {
    override fun create(appContext: Context, workerParameters: WorkerParameters): ListenableWorker {
        return WeatherInfoCleanupWorker(repository, appContext, workerParameters)
    }
}
