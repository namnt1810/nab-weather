package vn.namnt.nabweather.presentation.utils

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

/**
 * @author namnt
 * @since 10/12/2022
 */
class AppWorkerFactory @Inject constructor(
    private val creators: @JvmSuppressWildcards Map<Class<out ListenableWorker>, Provider<AbstractWorkerFactory>>
): WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val found =
            creators.entries.find { Class.forName(workerClassName).isAssignableFrom(it.key) }
        val creator =
            found?.value ?: return null

        return creator.get().create(appContext, workerParameters)
    }
}
