package vn.namnt.nabweather.data.remote

import vn.namnt.nabweather.common.TemperatureUnit
import vn.namnt.nabweather.data.remote.model.WeatherInfoResponse

/**
 * @author namnt
 * @since 01/12/2022
 */
interface WeatherInfoRemoteDatasource {
    suspend fun getWeatherInfo(city: String, daysCount: Int, tempUnit: TemperatureUnit): WeatherInfoResponse
}