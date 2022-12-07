package vn.namnt.nabweather.repository

import kotlinx.coroutines.flow.Flow
import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.entity.WeatherInfo

/**
 * @author namnt
 * @since 05/12/2022
 */
interface WeatherInfoRepository {
    suspend fun getWeatherInfo(
        city: String,
        fromDate: Long,
        temperatureUnit: TemperatureUnit,
        requestTime: Long
    ): Flow<Result<List<WeatherInfo>>>
}