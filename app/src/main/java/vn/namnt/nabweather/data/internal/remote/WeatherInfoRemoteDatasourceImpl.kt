package vn.namnt.nabweather.data.internal.remote

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.data.ApiErrorCodes
import vn.namnt.nabweather.data.ApiException
import vn.namnt.nabweather.data.RemoteWeatherData
import vn.namnt.nabweather.data.WeatherInfoRemoteDatasource
import vn.namnt.nabweather.data.internal.remote.model.WeatherInfoResponse
import vn.namnt.nabweather.data.internal.remote.service.WeatherInfoService
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
    ): RemoteWeatherData {
        return withContext(dispatcher) {
            val callResponse =
                weatherService.getWeatherForecast(city, daysCount, tempUnit.queryString).execute()

            if (callResponse.isSuccessful) {
                return@withContext callResponse.body()!!.toRemoteWeatherData()
            } else {
                val code = callResponse.code()
                throw when (code) {
                    ApiErrorCodes.API_KEY_ERROR, ApiErrorCodes.NOT_FOUND, ApiErrorCodes.API_LIMIT_EXCEEDED -> ApiException(
                        code
                    )
                    else -> HttpException(callResponse)
                }
            }
        }
    }
}

@VisibleForTesting
internal fun WeatherInfoResponse.toRemoteWeatherData(): RemoteWeatherData {
    return RemoteWeatherData(
        RemoteWeatherData.CityInfo(city.id, city.name),
        count,
        list.map { i ->
            RemoteWeatherData.WeatherInfo(
                i.date,
                RemoteWeatherData.WeatherInfo.TemperatureInfo(i.temperature.day),
                i.pressure,
                i.humidity,
                i.detail.map { RemoteWeatherData.WeatherInfo.WeatherDetail(it.description) }
            )
        }
    )
}

internal val TemperatureUnit.queryString: String
    get() = when (this) {
        TemperatureUnit.DEFAULT -> "default"
        TemperatureUnit.METRIC -> "metric"
        TemperatureUnit.IMPERIAL -> "imperial"
    }
