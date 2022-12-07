package vn.namnt.nabweather.data.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.data.remote.model.WeatherInfoResponse
import vn.namnt.nabweather.data.remote.service.WeatherInfoService
import javax.inject.Inject

internal class WeatherInfoRemoteDatasourceImpl @Inject constructor(
    private val retrofit: Retrofit,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
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
                throw Exception()
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