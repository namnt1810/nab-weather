package vn.namnt.nabweather.data.local

/**
 * @author namnt
 * @since 01/12/2022
 */
interface WeatherInfoLocalDatasource {
    suspend fun getWeatherInfo(city: String, fromDate: Long, daysCount: Int): List<WeatherInfoDBO>
}