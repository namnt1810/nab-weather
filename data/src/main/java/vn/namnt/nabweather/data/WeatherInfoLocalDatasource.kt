package vn.namnt.nabweather.data

/**
 * @author namnt
 * @since 01/12/2022
 */
interface WeatherInfoLocalDatasource {
    suspend fun saveWeatherInfo(vararg items: WeatherInfoData)
    suspend fun getWeatherInfo(cityId: Int, fromDate: Long, daysCount: Int): List<WeatherInfoData>
    suspend fun saveCityInfo(data: CityInfoData)
    suspend fun getCityInfo(city: String): CityInfoData?
    suspend fun deleteObsoleteData(): Int
}
