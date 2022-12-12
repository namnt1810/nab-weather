package vn.namnt.nabweather.presentation.utils

import android.content.Context
import vn.namnt.nabweather.R
import vn.namnt.nabweather.common.di.AppContext
import vn.namnt.nabweather.domain.DomainErrorCodes
import vn.namnt.nabweather.domain.DomainException
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
            is DomainException -> domainLocalizedErrorMessage(throwable)
            else -> unknownErrorMessage(throwable)
        }

        return message
    }

    private fun domainLocalizedErrorMessage(exception: DomainException): String {
        val message = when (exception.code) {
            DomainErrorCodes.CITY_NOT_FOUND -> context.getString(R.string.error_city_not_found_message)
            DomainErrorCodes.API_KEY_ERROR -> context.getString(R.string.error_invalid_api_key_message)
            DomainErrorCodes.API_LIMIT_EXCEEDED -> context.getString(R.string.error_api_limit_exceeded_message)
            DomainErrorCodes.NO_INTERNET -> context.getString(R.string.error_no_internet_message)
            DomainErrorCodes.NO_CONNECTIVITY -> context.getString(R.string.error_no_connectivity_message)
            else -> unknownErrorMessage(exception)
        }

        return message
    }
}
