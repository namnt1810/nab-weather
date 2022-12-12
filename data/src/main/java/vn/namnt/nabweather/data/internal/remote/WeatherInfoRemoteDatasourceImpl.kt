package vn.namnt.nabweather.data.internal.remote

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.data.RemoteWeatherDataResponse
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
    ): RemoteWeatherDataResponse {
        return withContext(dispatcher) {
            val callResponse =
                weatherService.getWeatherForecast(city, daysCount, tempUnit.queryString).execute()

            val errorCode = callResponse.code()

            val body = callResponse.body()
            if (callResponse.isSuccessful && body != null) {
                return@withContext RemoteWeatherDataResponse(
                    errorCode,
                    RemoteWeatherDataResponse.CityInfo(body.city.id, body.city.name),
                    body.list.map {
                        RemoteWeatherDataResponse.WeatherInfo(
                            it.date,
                            RemoteWeatherDataResponse.WeatherInfo.TemperatureInfo(it.temperature.day),
                            it.pressure,
                            it.humidity,
                            it.detail.map { RemoteWeatherDataResponse.WeatherInfo.WeatherDetail(it.description) }
                        )
                    }
                )
            } else {
                return@withContext RemoteWeatherDataResponse(errorCode, null, null)
            }
        }
    }
}

@VisibleForTesting
internal fun WeatherInfoResponse.toRemoteWeatherData(httpCode: Int): RemoteWeatherDataResponse {
    return RemoteWeatherDataResponse(
        httpCode,
        RemoteWeatherDataResponse.CityInfo(city.id, city.name),
        list.map { i ->
            RemoteWeatherDataResponse.WeatherInfo(
                i.date,
                RemoteWeatherDataResponse.WeatherInfo.TemperatureInfo(i.temperature.day),
                i.pressure,
                i.humidity,
                i.detail.map { RemoteWeatherDataResponse.WeatherInfo.WeatherDetail(it.description) }
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
