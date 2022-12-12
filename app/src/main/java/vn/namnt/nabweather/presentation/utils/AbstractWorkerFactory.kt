package vn.namnt.nabweather.presentation.utils

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

/**
 * @author namnt
 * @since 10/12/2022
 */
interface AbstractWorkerFactory {
    fun create(appContext: Context, workerParameters: WorkerParameters): ListenableWorker
}
