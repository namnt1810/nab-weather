package vn.namnt.nabweather.domain

/**
 * @author namnt
 * @since 12/12/2022
 */
object DomainErrorCodes {
    const val SUCCESS = 0
    const val UNKNOWN = -1

    const val NO_CONNECTIVITY = -3
    const val NO_INTERNET = -4

    const val CITY_NOT_FOUND = -100
    const val API_KEY_ERROR = -101
    const val API_LIMIT_EXCEEDED = -102
}