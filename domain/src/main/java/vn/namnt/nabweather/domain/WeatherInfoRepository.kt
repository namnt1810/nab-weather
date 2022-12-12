package vn.namnt.nabweather.domain

import kotlinx.coroutines.flow.Flow
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.entity.WeatherInfo

/**
 * @author namnt
 * @since 05/12/2022
 */
interface WeatherInfoRepository {
    fun getWeatherInfo(
        city: String,
        fromDate: Long,
        temperatureUnit: TemperatureUnit,
        requestTime: Long
    ): Flow<Result<List<WeatherInfo>>>

    suspend fun cleanupWeatherInfo(): Int
}
