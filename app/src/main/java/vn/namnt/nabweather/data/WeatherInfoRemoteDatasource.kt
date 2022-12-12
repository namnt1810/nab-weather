package vn.namnt.nabweather.data

import vn.namnt.nabweather.common.TemperatureUnit

/**
 * @author namnt
 * @since 01/12/2022
 */
interface WeatherInfoRemoteDatasource {
    suspend fun getWeatherInfo(city: String, daysCount: Int, tempUnit: TemperatureUnit): RemoteWeatherData
}