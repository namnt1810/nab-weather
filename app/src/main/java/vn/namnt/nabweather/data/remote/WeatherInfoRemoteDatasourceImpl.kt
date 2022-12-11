package vn.namnt.nabweather.data.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.data.remote.error.ApiErrorCodes
import vn.namnt.nabweather.data.remote.model.WeatherInfoResponse
import vn.namnt.nabweather.data.remote.service.WeatherInfoService
import vn.namnt.nabweather.repository.exception.ApiException
import javax.inject.Inject

internal class WeatherInfoRemoteDatasourceImpl @Inject constructor(
    retrofit: Retrofit,
    private val dispatcher: CoroutineDispatcher
) : WeatherInfoRemoteDatasource {
    private val weatherService: WeatherInfoService = retrofit.create(WeatherInfoService::class.java)

    override suspend fun getWeatherInfo(
        city: String,
        daysCount: Int,
        tempUnit: TemperatureUnit
    ): WeatherInfoResponse {
        return withContext(dispatcher) {
            val callResponse =
                weatherService.getWeatherForecast(city, daysCount, tempUnit.queryString).execute()

            if (callResponse.isSuccessful) {
                return@withContext callResponse.body()!!
            } else {
                val code = callResponse.code()
                throw when (code) {
                    ApiErrorCodes.API_KEY_ERROR, ApiErrorCodes.NOT_FOUND, ApiErrorCodes.API_LIMIT_EXCEEDED -> ApiException(code)
                    else -> HttpException(callResponse)
                }
            }
        }
    }
}

internal val TemperatureUnit.queryString: String
    get() = when (this) {
        TemperatureUnit.DEFAULT -> "default"
        TemperatureUnit.METRIC -> "metric"
        TemperatureUnit.IMPERIAL -> "imperial"
    }
