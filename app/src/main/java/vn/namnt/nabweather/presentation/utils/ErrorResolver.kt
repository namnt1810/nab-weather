package vn.namnt.nabweather.presentation.utils

import android.content.Context
import retrofit2.HttpException
import vn.namnt.nabweather.R
import vn.namnt.nabweather.data.ApiErrorCodes
import vn.namnt.nabweather.data.ApiException
import vn.namnt.nabweather.data.NoConnectivityException
import vn.namnt.nabweather.data.NoInternetException
import vn.namnt.nabweather.di.AppContext
import javax.inject.Inject

/**
 * @author namnt
 * @since 08/12/2022
 */
internal class ErrorResolver @Inject constructor(
    @AppContext
    private val context: Context
) {
    private fun unknownErrorMessage(throwable: Throwable) =
        context.getString(R.string.error_unknown_message, throwable.message)

    fun localizedErrorMessage(throwable: Throwable): String {
        val message = when (throwable) {
            is HttpException -> context.getString(
                R.string.error_http_exception_message,
                throwable.code(),
                throwable.message
            )
            is ApiException -> domainLocalizedErrorMessage(throwable)
            is NoConnectivityException -> context.getString(R.string.error_no_connectivity_message)
            is NoInternetException -> context.getString(R.string.error_no_internet_message)
            else -> unknownErrorMessage(throwable)
        }

        return message
    }

    private fun domainLocalizedErrorMessage(exception: ApiException): String {
        val message = when (exception.code) {
            ApiErrorCodes.NOT_FOUND -> context.getString(R.string.error_city_not_found_message)
            ApiErrorCodes.API_KEY_ERROR -> context.getString(R.string.error_invalid_api_key_message)
            ApiErrorCodes.API_LIMIT_EXCEEDED -> context.getString(R.string.error_api_limit_exceeded_message)
            else -> unknownErrorMessage(exception)
        }

        return message
    }
}
