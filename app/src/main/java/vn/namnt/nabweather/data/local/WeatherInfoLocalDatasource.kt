package vn.namnt.nabweather.data.local

import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO

/**
 * @author namnt
 * @since 01/12/2022
 */
interface WeatherInfoLocalDatasource {
    suspend fun saveWeatherInfo(vararg items: WeatherInfoDBO)
    suspend fun getWeatherInfo(city: String, fromDate: Long, daysCount: Int): List<WeatherInfoDBO>
    suspend fun saveCityUpdateTime(city: String, lastUpdateTs: Long)
    suspend fun getCityLastUpdatedTime(city: String): Long
}