package vn.namnt.nabweather.data.local

import vn.namnt.nabweather.data.local.database.entity.CityInfoDBO
import vn.namnt.nabweather.data.local.database.entity.WeatherInfoDBO

/**
 * @author namnt
 * @since 01/12/2022
 */
interface WeatherInfoLocalDatasource {
    suspend fun saveWeatherInfo(vararg items: WeatherInfoDBO)
    suspend fun getWeatherInfo(cityId: Int, fromDate: Long, daysCount: Int): List<WeatherInfoDBO>
    suspend fun saveCityInfo(dbo: CityInfoDBO)
    suspend fun getCityInfo(city: String): CityInfoDBO?
}